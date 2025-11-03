package io.zaryx.content.bosses.leviathan;

import io.zaryx.Server;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.model.*;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Position;
import io.zaryx.util.Misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TheLeviathan {

    private static final int MAGE_ORB_INTERVAL = 12; // Interval in seconds for mage orbs
    private static final int RANGE_ORB_INTERVAL = 12; // Interval in seconds for range orbs
    private static final int BOULDER1_INTERVAL = 15; // Interval in seconds for boulder1 attacks
    private static final int BOULDER2_INTERVAL = 30; // Interval in seconds for boulder2 attacks
    private static final int BOULDER2_DURATION = 5; // Duration in seconds for boulder2 phase
    private static final int BOULDER2_DAMAGE_DELAY = 3; // Delay in seconds before boulder2 applies damage

    private static long lastMageOrbAttackTime = 0;
    private static long lastRangeOrbAttackTime = 0;
    private static long lastBoulder1AttackTime = 0;
    private static long lastBoulder2AttackTime = 0;
    private static boolean isMageOrbActive = false;
    private static boolean isRangeOrbActive = false;

    public static void handleAttacks(NPC npc) {
        long currentTime = System.currentTimeMillis();

        // Mage Orb Attack
        if (shouldAttack(currentTime, lastMageOrbAttackTime, MAGE_ORB_INTERVAL)) {
            if (!isMageOrbActive) {
                handleMageOrbs(npc);
                lastMageOrbAttackTime = currentTime;
                isMageOrbActive = true;
                // Ensure Range Orb is not active
                if (currentTime - lastRangeOrbAttackTime < TimeUnit.SECONDS.toMillis(RANGE_ORB_INTERVAL)) {
                    isRangeOrbActive = false;
                }
            }
        }

        // Range Orb Attack
        if (shouldAttack(currentTime, lastRangeOrbAttackTime, RANGE_ORB_INTERVAL) && !isMageOrbActive) {
            handleRangeOrbs(npc);
            lastRangeOrbAttackTime = currentTime;
            isRangeOrbActive = true;
        }

        // Boulder 1 Attack
        if (shouldAttack(currentTime, lastBoulder1AttackTime, BOULDER1_INTERVAL)) {
            handleRangedBoulder1(npc);
            lastBoulder1AttackTime = currentTime;
        }

        // Boulder 2 Attack
        if (shouldAttack(currentTime, lastBoulder2AttackTime, BOULDER2_INTERVAL)) {
            handleRangedBoulder2(npc);
            lastBoulder2AttackTime = currentTime;
        }

        // Reset orbs activity after the interval
        if (currentTime - lastMageOrbAttackTime >= TimeUnit.SECONDS.toMillis(MAGE_ORB_INTERVAL) + TimeUnit.SECONDS.toMillis(2)) {
            isMageOrbActive = false;
        }
        if (currentTime - lastRangeOrbAttackTime >= TimeUnit.SECONDS.toMillis(RANGE_ORB_INTERVAL) + TimeUnit.SECONDS.toMillis(2)) {
            isRangeOrbActive = false;
        }
    }

    private static boolean shouldAttack(long currentTime, long lastAttackTime, int interval) {
        return currentTime - lastAttackTime >= TimeUnit.SECONDS.toMillis(interval);
    }

    private static void handleMageOrbs(NPC npc) {
        handleProjectileAttack(npc, CombatType.MAGE, 2483, 8500, 75, 44, 30, 50, 16);
    }

    private static void handleRangeOrbs(NPC npc) {
        handleProjectileAttack(npc, CombatType.RANGE, 2481, 8501, 80, 40, 30, 50, 16);
    }

    private static void handleProjectileAttack(NPC npc, CombatType attackType, int projectileId, int animationId,
                                               int speed, int startHeight, int endHeight, int time, int slope) {
        List<Player> targets = getTargets();
        if (targets.isEmpty()) {
            return;
        }

        Player targetPlayer = targets.get(Misc.random(targets.size() - 1));
        int angle = 0; // Angle is defaulted to 0; adjust if needed

        // Fire the projectile from the NPC to the player
        //fireProjectileNPCtoPLAYER(npc, targetPlayer, angle, speed, projectileId, startHeight, endHeight, time, slope);
        Projectile.createTargeted(npc, targetPlayer, new ProjectileBaseBuilder().setProjectileId(projectileId).setPitch(angle).setStartHeight(startHeight).setEndHeight(endHeight).setCurve(slope).setSpeed(speed).createProjectileBase()).send(null);

        // Set attack type and animation
        npc.setAttackType(attackType);
        npc.startAnimation(animationId);

        // Schedule damage application after the projectile reaches the player
        int travelTimeTicks = time / 30;
        CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (targetPlayer != null && Boundary.isIn(targetPlayer, Boundary.LEVIATHAN)) {
                    targetPlayer.appendDamage(npc, getProjectileDamage(attackType), Hitmark.HIT);
                }
                container.stop();
            }
        }, travelTimeTicks);
    }

    private static int getProjectileDamage(CombatType attackType) {
        switch (attackType) {
            case MAGE: return Misc.random(15, 30);
            case RANGE: return Misc.random(10, 25);
            default: return 0;
        }
    }

    private static void handleRangedBoulder1(NPC npc) {
        npc.setAttackType(CombatType.SPECIAL);
        npc.startAnimation(10282);

        List<Player> targets = getTargets();
        if (targets.isEmpty()) {
            return;
        }

        Player targetPlayer = targets.get(Misc.random(targets.size() - 1));
        Position playerPosition = targetPlayer.getPosition();
        List<Position> dropPositions = generateDropPositions(playerPosition, 4);

        // Drop boulders
        scheduleBoulderDrops(npc, dropPositions, 8, 2); // Longer delay to ensure no overlap
    }

    private static void handleRangedBoulder2(NPC npc) {
        npc.setAttackType(CombatType.SPECIAL);
        npc.startAnimation(10283);

        List<Player> targets = getTargets();
        if (targets.isEmpty()) {
            return;
        }

        Player targetPlayer = targets.get(Misc.random(targets.size() - 1));

        // Start time for the boulder dropping phase
        long phaseStartTime = System.currentTimeMillis();

        // Drop boulders continuously for 5 seconds
        CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                long elapsedTime = System.currentTimeMillis() - phaseStartTime;

                if (elapsedTime >= TimeUnit.SECONDS.toMillis(BOULDER2_DURATION)) {
                    container.stop();
                    return;
                }

                Position currentPosition = targetPlayer.getPosition();
                Server.playerHandler.sendStillGfx(new StillGraphic(2477, currentPosition), npc.getPosition());

                // Delay before applying damage to allow dodging
                CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer innerContainer) {
                        if (currentPosition.equals(targetPlayer.getPosition())) {
                            targetPlayer.appendDamage(npc, Misc.random(10, 30), Hitmark.HIT); // Apply damage
                        }
                        innerContainer.stop();
                    }
                }, BOULDER2_DAMAGE_DELAY * 1000 / 600); // Convert seconds to game ticks
            }
        }, 1); // Executes every game tick (600ms)
    }

    private static void scheduleBoulderDrops(NPC npc, List<Position> dropPositions, int numDrops, int delayBetweenDrops) {
        Set<Position> usedPositions = new HashSet<>();

        for (int i = 0; i < numDrops; i++) {
            final Position dropPosition = getUniqueDropPosition(dropPositions, usedPositions);
            CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    Server.playerHandler.sendStillGfx(new StillGraphic(2476, dropPosition), npc.getPosition());

                    // Apply damage if the boulder lands on a player
                    applyBoulderDamage(npc, dropPosition);

                    container.stop();
                }
            }, i * delayBetweenDrops * 600); // Delay between drops to avoid overlap
        }
    }

    private static void applyBoulderDamage(NPC npc, Position dropPosition) {
        for (Player target : getTargets()) {
            if (dropPosition.equals(target.getPosition())) {
                target.appendDamage(npc, Misc.random(10, 30), Hitmark.HIT); // Apply damage to the player
            }
        }
    }

    private static Position getUniqueDropPosition(List<Position> dropPositions, Set<Position> usedPositions) {
        Position dropPosition;
        do {
            dropPosition = dropPositions.get(Misc.random(dropPositions.size() - 1));
        } while (usedPositions.contains(dropPosition));
        usedPositions.add(dropPosition);
        return dropPosition;
    }

    private static List<Position> generateDropPositions(Position playerPosition, int radius) {
        List<Position> dropPositions = new ArrayList<>();
        int xStart = playerPosition.getX() - radius;
        int yStart = playerPosition.getY() - radius;
        int xEnd = playerPosition.getX() + radius;
        int yEnd = playerPosition.getY() + radius;

        for (int x = xStart; x <= xEnd; x++) {
            for (int y = yStart; y <= yEnd; y++) {
                Position pos = new Position(x, y);
                if (!Boundary.isIn(pos, Boundary.LEVIATHANCLOSE)) {
                    dropPositions.add(pos);
                }
            }
        }
        return dropPositions;
    }

    private static List<Player> getTargets() {
        List<Player> list = new ArrayList<>();
        for (Player player : PlayerHandler.getPlayers()) {
            if (player != null && Boundary.isIn(player, Boundary.LEVIATHAN)) {
                list.add(player);
            }
        }
        return list;
    }

    public static void setEnraged(boolean enraged) {
        // Handle enraged state if needed
    }
}
