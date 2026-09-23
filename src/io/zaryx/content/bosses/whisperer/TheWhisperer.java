package io.zaryx.content.bosses.whisperer;

import io.zaryx.Server;
import io.zaryx.content.bosses.hydra.CombatProjectile;
import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.model.CombatType;
import io.zaryx.model.StillGraphic;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.npc.NPCSpawning;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.util.Misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/** The post-quest Whisperer. All timers and summons belong to the individual NPC fight. */
public final class TheWhisperer {

    private static final int[] SPECIAL_HEALTH_PERCENT = {80, 55, 30};
    private static final int MAX_INCOMING_HIT = 75;
    private static final int PILLAR_ID = 12210;
    private static final int LOST_SOUL_ID = 12211; // Visible in the server's single-realm arena.
    private static final CombatProjectile RANGED_PROJECTILE = new CombatProjectile(2445, 50, 25, 0, 100, 0, 50);
    private static final CombatProjectile MAGIC_PROJECTILE = new CombatProjectile(2444, 50, 25, 0, 100, 0, 50);
    private static final Map<NPC, Fight> FIGHTS = Collections.synchronizedMap(new WeakHashMap<>());

    private TheWhisperer() {
    }

    private enum Special { SEEDS, SCREECH, SOUL_SIPHON }

    private static final class Fight {
        private int specialsCompleted;
        private boolean specialActive;
        private boolean enraged;
        private int enrageAttacks;
        private int tentacleAttacks;
        private final Special[] rotation = Misc.random(1) == 0
                ? new Special[]{Special.SEEDS, Special.SCREECH, Special.SOUL_SIPHON}
                : new Special[]{Special.SCREECH, Special.SOUL_SIPHON, Special.SEEDS};
        private final List<NPC> summons = new ArrayList<>();
    }

    /** Called instead of the generic NPC attack, once per attack cycle. */
    public static void handleAttack(NPC npc, Player target) {
        Fight fight = FIGHTS.computeIfAbsent(npc, unused -> new Fight());
        if (fight.specialActive || !canFight(npc, target)) {
            npc.attackTimer = 2;
            return;
        }

        npc.oldIndex = target.getIndex();
        target.underAttackByNpc = npc.getIndex();
        target.singleCombatDelay2 = System.currentTimeMillis();

        if (!fight.enraged && shouldStartSpecial(npc.getHealth().getCurrentHealth(),
                npc.getHealth().getMaximumHealth(), fight.specialsCompleted)) {
            npc.attackTimer = 10;
            startSpecial(npc, target, fight);
            return;
        }

        npc.attackTimer = fight.enraged ? 4 : 10;
        npc.startAnimation(NPCHandler.getAttackEmote(npc));
        if (fight.enraged) {
            // The final phase opens with ranged and changes style every two attacks.
            CombatType style = (fight.enrageAttacks / 2) % 2 == 0 ? CombatType.RANGE : CombatType.MAGE;
            fight.enrageAttacks++;
            fireShot(npc, target, style, 0, 3);
            summonEnrageTentacle(npc, target, fight.enrageAttacks);
        } else {
            boolean magicFirst = Misc.random(1) == 0;
            for (int shot = 0; shot < 3; shot++) {
                CombatType style = volleyStyle(fight.specialsCompleted, magicFirst, shot);
                fireShot(npc, target, style, shot, 4);
            }
            summonTentacles(npc, target, fight);
        }
    }

    static boolean shouldStartSpecial(int health, int maximum, int specialsCompleted) {
        return maximum > 0 && specialsCompleted < SPECIAL_HEALTH_PERCENT.length
                && health > 0 && health * 100L <= maximum * (long) SPECIAL_HEALTH_PERCENT[specialsCompleted];
    }

    static CombatType volleyStyle(int specialsCompleted, boolean magicFirst, int shot) {
        boolean magic = specialsCompleted == 0 || specialsCompleted == 3
                ? (specialsCompleted == 0 || shot != 1)
                : shot < 2;
        return magic == magicFirst ? CombatType.MAGE : CombatType.RANGE;
    }

    private static void fireShot(NPC npc, Player target, CombatType style, int shot, int hitDelay) {
        CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                container.stop();
                if (!canFight(npc, target)) return;
                sendProjectile(npc, target, style == CombatType.MAGE ? MAGIC_PROJECTILE : RANGED_PROJECTILE);
                CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer impact) {
                        impact.stop();
                        if (!canFight(npc, target)) return;
                        boolean protectedByPrayer = style == CombatType.MAGE
                                ? target.protectingMagic() : target.protectingRange();
                        int damage = protectedByPrayer ? 0 : Misc.random(1, style == CombatType.MAGE ? 12 : 14);
                        target.getDamageQueue().add(new Damage(target, damage, 0, target.playerEquipment,
                                damage == 0 ? Hitmark.MISS : Hitmark.HIT, style));
                    }
                }, hitDelay);
            }
        }, shot + 1);
    }

    private static void sendProjectile(NPC npc, Player target, CombatProjectile projectile) {
        int centerX = npc.getX() + npc.getSize() / 2;
        int centerY = npc.getY() + npc.getSize() / 2;
        target.getPA().createPlayersProjectile(centerX, centerY, target.getY() - centerY,
                target.getX() - centerX, projectile.getAngle(), projectile.getSpeed(), projectile.getGfx(),
                projectile.getStartHeight(), projectile.getEndHeight(), -target.getIndex() - 1, 65, projectile.getDelay());
    }

    private static void summonTentacles(NPC npc, Player target, Fight fight) {
        int x = target.getX();
        int y = target.getY();
        boolean plusFormation = fight.specialsCompleted >= 2 && fight.tentacleAttacks++ % 2 == 1;
        int[][] offsets = plusFormation
                ? new int[][]{{-4, 0}, {4, 0}, {0, -4}, {0, 4}}
                : new int[][]{{-4, -4}, {4, -4}, {-4, 4}, {4, 4}};
        for (int[] offset : offsets) {
            showGraphic(target, x + offset[0], y + offset[1], 2447);
        }
        // The converging tentacles punish remaining on the marked tile, not movement away.
        CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                container.stop();
                if (!canFight(npc, target)) return;
                showGraphic(target, x, y, 2450);
                if (target.getX() == x && target.getY() == y) target.appendDamage(20, Hitmark.HIT);
            }
        }, 4);
    }

    private static void summonEnrageTentacle(NPC npc, Player target, int attack) {
        int x = target.getX();
        int y = target.getY();
        int[][] offsets = {{-4, -4}, {4, -4}, {4, 4}, {-4, 4}, {-4, 0}, {0, 4}, {4, 0}, {0, -4}};
        int[] offset = offsets[Math.floorMod(attack - 1, offsets.length)];
        showGraphic(target, x + offset[0], y + offset[1], 2447);
        CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                container.stop();
                if (!canFight(npc, target)) return;
                showGraphic(target, x, y, 2450);
                if (target.getX() == x && target.getY() == y) target.appendDamage(20, Hitmark.HIT);
            }
        }, 4);
    }

    private static void startSpecial(NPC npc, Player target, Fight fight) {
        fight.specialActive = true;
        Special special = fight.rotation[fight.specialsCompleted];
        switch (special) {
            case SEEDS:
                startSeeds(npc, target, fight);
                break;
            case SCREECH:
                startScreech(npc, target, fight);
                break;
            case SOUL_SIPHON:
                startSoulSiphon(npc, target, fight);
                break;
        }
    }

    private static void startSeeds(NPC npc, Player target, Fight fight) {
        target.sendMessage("The Whisperer scatters corrupted seeds! Step on the three glowing tiles.");
        int x = npc.getX();
        int y = npc.getY();
        int direction = Misc.random(1) == 0 ? -1 : 1;
        int[][] seeds = {{x + direction * 5, y}, {x, y + 5}, {x - direction * 5, y + 5}};
        boolean[] collected = new boolean[seeds.length];
        CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
            int ticks;

            @Override
            public void execute(CycleEventContainer container) {
                if (!canFight(npc, target)) {
                    finishSpecial(npc, fight);
                    container.stop();
                    return;
                }
                boolean complete = true;
                for (int i = 0; i < seeds.length; i++) {
                    if (!collected[i] && target.getX() == seeds[i][0] && target.getY() == seeds[i][1]) {
                        collected[i] = true;
                    }
                    if (!collected[i]) {
                        complete = false;
                        if (ticks % 3 == 0) showGraphic(target, seeds[i][0], seeds[i][1], 2447);
                    }
                }
                if (complete || ++ticks >= 18) {
                    if (!complete) target.appendDamage(75, Hitmark.HIT);
                    finishSpecial(npc, fight);
                    container.stop();
                }
            }
        }, 1);
    }

    private static void startScreech(NPC npc, Player target, Fight fight) {
        Position returnPosition = new Position(npc.getX(), npc.getY(), npc.getHeight());
        npc.teleport(new Position(2656, 6354, npc.getHeight()));
        target.sendMessage("The Whisperer prepares three screeches! Shelter behind a floating column.");
        int[] xs = {2651, 2656, 2661};
        int[] hp = {20, 40, 60};
        for (int i = 0; i < xs.length; i++) {
            NPC column = NPCSpawning.spawnNpc(PILLAR_ID, xs[i], 6363, npc.getHeight(), 0, 0,
                    NPCSpawning.getStats(hp[i], 0, 0));
            if (column != null) {
                column.needRespawn = false;
                fight.summons.add(column);
            }
        }
        CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
            int pulses;

            @Override
            public void execute(CycleEventContainer container) {
                if (!canFight(npc, target)) {
                    npc.teleport(returnPosition);
                    finishSpecial(npc, fight);
                    container.stop();
                    return;
                }
                NPC shelteredColumn = null;
                for (NPC column : fight.summons) {
                    if (!column.isDead() && !column.isUnregister()
                            && Math.abs(target.getX() - column.getX()) <= 1 && target.getY() >= column.getY() + 1) {
                        shelteredColumn = column;
                        break;
                    }
                }
                // Each screech weakens every pillar and destroys the one used for cover.
                for (NPC column : fight.summons) {
                    if (column.isUnregister()) continue;
                    column.getHealth().setCurrentHealth(column.getHealth().getCurrentHealth() - 20);
                    if (column == shelteredColumn || column.getHealth().getCurrentHealth() <= 0) column.unregister();
                }
                showGraphic(target, target.getX(), target.getY(), 2446);
                if (shelteredColumn == null) target.appendDamage(45, Hitmark.HIT);
                if (++pulses == 3) {
                    npc.teleport(returnPosition);
                    finishSpecial(npc, fight);
                    container.stop();
                }
            }
        }, 5);
    }

    private static void startSoulSiphon(NPC npc, Player target, Fight fight) {
        target.sendMessage("The Whisperer siphons lost souls! Defeat every soul in any one group.");
        int x = npc.getX();
        int y = npc.getY();
        int[][] offsets = {{-5, -1}, {-5, 0}, {-5, 1}, {5, -1}, {5, 0}, {5, 1},
                {-1, -5}, {0, -5}, {1, -5}, {-1, 5}, {0, 5}, {1, 5}};
        List<List<NPC>> groups = new ArrayList<>();
        int[] groupSizes = {2, 3, 3, 4};
        int nextOffset = 0;
        for (int group = 0; group < groupSizes.length; group++) {
            List<NPC> souls = new ArrayList<>();
            for (int member = 0; member < groupSizes[group]; member++) {
                int[] offset = offsets[nextOffset++];
                NPC soul = NPCSpawning.spawnNpc(LOST_SOUL_ID, x + offset[0], y + offset[1],
                        npc.getHeight(), 0, 0, NPCSpawning.getStats(5, 0, 0));
                if (soul != null) {
                    soul.needRespawn = false;
                    soul.underAttack = false;
                    fight.summons.add(soul);
                    souls.add(soul);
                }
            }
            groups.add(souls);
        }
        CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                container.stop();
                if (canFight(npc, target)) {
                    boolean stopped = false;
                    for (int i = 0; i < groups.size(); i++) {
                        List<NPC> group = groups.get(i);
                        if (group.size() == groupSizes[i] && group.stream().allMatch(NPC::isDead)) {
                            stopped = true;
                            break;
                        }
                    }
                    if (!stopped) {
                        target.appendDamage(50, Hitmark.HIT);
                        npc.getHealth().increase(100);
                    }
                }
                finishSpecial(npc, fight);
            }
        }, 17);
    }

    private static void finishSpecial(NPC npc, Fight fight) {
        for (NPC summon : fight.summons) summon.unregister();
        fight.summons.clear();
        fight.specialsCompleted++;
        fight.specialActive = false;
        npc.attackTimer = 5;
    }

    /**
     * Keeps burst damage from skipping a scripted health phase. Each threshold becomes a
     * temporary floor until its special finishes; the fight can then continue toward the next
     * threshold. The per-hit cap also prevents custom weapons from deleting the enrage phase.
     */
    public static int modifyIncomingDamage(NPC npc, int damage) {
        if (npc == null || npc.getNpcId() != 12205 || damage <= 0) return damage;
        Fight fight = FIGHTS.computeIfAbsent(npc, unused -> new Fight());
        return capIncomingDamage(npc.getHealth().getCurrentHealth(), npc.getHealth().getMaximumHealth(),
                fight.specialsCompleted, fight.enraged, damage);
    }

    static int capIncomingDamage(int currentHealth, int maximumHealth, int specialsCompleted,
                                 boolean enraged, int damage) {
        if (damage <= 0) return damage;
        int cappedDamage = Math.min(damage, MAX_INCOMING_HIT);
        if (enraged || specialsCompleted >= SPECIAL_HEALTH_PERCENT.length) return cappedDamage;
        return Math.min(cappedDamage, Math.max(0,
                currentHealth - thresholdHealth(maximumHealth, specialsCompleted)));
    }

    static int thresholdHealth(int maximumHealth, int specialsCompleted) {
        if (maximumHealth <= 0 || specialsCompleted < 0 || specialsCompleted >= SPECIAL_HEALTH_PERCENT.length) {
            return 0;
        }
        return maximumHealth * SPECIAL_HEALTH_PERCENT[specialsCompleted] / 100;
    }

    private static boolean canFight(NPC npc, Player target) {
        return npc != null && !npc.isDead() && target != null && !target.isDead()
                && target.getHealth().getCurrentHealth() > 0 && npc.getInstance() == target.getInstance()
                && npc.getHeight() == target.getHeight() && Boundary.isIn(target, Boundary.WHISPERER_BOUNDARY);
    }

    private static void showGraphic(Player target, int x, int y, int graphic) {
        Server.playerHandler.sendStillGfx(new StillGraphic(graphic, new Position(x, y, target.getHeight())), target.getPosition());
    }

    /** Intercepts the first lethal hit for the documented 140 HP enrage phase. */
    public static boolean tryStartEnrage(NPC npc) {
        // A defeated Whisperer remains dead while the generic NPC respawn timer runs. Without
        // this guard, removing its completed fight state causes the corpse to start a second
        // enrage phase on the following tick and prevents the scheduled respawn.
        if (!canBeginEnrage(npc.applyDead, npc.needRespawn)) return false;
        Fight fight = FIGHTS.computeIfAbsent(npc, unused -> new Fight());
        if (fight.enraged) return false;
        for (NPC summon : fight.summons) summon.unregister();
        fight.summons.clear();
        fight.specialActive = false;
        fight.enraged = true;
        npc.getHealth().setCurrentHealth(140);
        npc.setDead(false);
        npc.applyDead = false;
        npc.actionTimer = 0;
        npc.attackTimer = 4;
        npc.forceChat("The shadows consume you!");
        return true;
    }

    static boolean canBeginEnrage(boolean deathProcessed, boolean respawnPending) {
        return !deathProcessed && !respawnPending;
    }

    public static void handleDeath(NPC npc) {
        Fight fight = FIGHTS.remove(npc);
        if (fight == null) return;
        for (NPC summon : fight.summons) summon.unregister();
        fight.summons.clear();
    }
}
