package io.zaryx.content.commands.admin;

import io.zaryx.Server;
import io.zaryx.content.battlepass.Pass;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.combat.death.NPCDeath;
import io.zaryx.content.commands.Command;
import io.zaryx.model.AnimationPriority;
import io.zaryx.model.CombatType;
import io.zaryx.model.entity.HealthStatus;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCSpawning;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.entity.player.broadcasts.Broadcast;
import io.zaryx.sql.dailytracker.TrackerType;
import io.zaryx.util.Location3D;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.stream.Collectors;

public class dboss extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        PlayerHandler.executeGlobalMessage("@red@[DONO BOSS]@blu@ " + c.getDisplayName()
                + " has just spawned a dono boss!! use ::vb or ::db");
        spawnBoss();
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Spawn's a donation boss manually.");
    }

    public static void spawnBoss() {
        spawnNPC();
        if (DiscordBot.INSTANCE != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("[ DONOR BOSS ]");
            embed.setImage("https://oldschool.runescape.wiki/images/thumb/Boss.png/150px-Boss.png?fb192");
            embed.setColor(Color.GREEN);
            embed.setTimestamp(java.time.Instant.now());
            embed.addField("Donor Boss has spawned, use ::db to fight!", "\u200B", false);
            DiscordBot.INSTANCE.sendWorldEvent(embed.build());
            //DiscordBot.INSTANCE.sendMessage(DiscordChannelType.WORLD_EVENTS, "@everyone");
        }
        TrackerType.DONOR_BOSS.addTrackerData(1);
    }

    public static void Attack(NPC npc) {
        if (npc == null || npc.isDead) {
            return;
        }

        donaboss = npc;

        updateTargets();
        if (targets.isEmpty()) {
            npc.attackTimer = 4;
            return;
        }

        int rng = Misc.random(1, 100);

        if (rng <= 30) {
            // Basic fire
            npc.startAnimation(7904, AnimationPriority.HIGH);
            npc.setAttackType(CombatType.MAGE);
            npc.projectileId = 1469;
            npc.endGfx = -1;
            npc.hitDelayTimer = 4;
            npc.attackTimer = 10;
            npc.maxHit = 5;

        } else if (rng <= 50) { // 31–50: Arc AoE
            npc.startAnimation(7901, AnimationPriority.HIGH);
            npc.setAttackType(CombatType.MAGE);
            npc.projectileId = 1471;
            npc.endGfx = -1;
            npc.hitDelayTimer = 4;
            npc.attackTimer = 10;
            npc.maxHit = 5;
            arcAttack();

        } else if (rng <= 70) { // 51–70: Boulder AoE
            npc.startAnimation(7908, AnimationPriority.HIGH);
            npc.setAttackType(CombatType.MAGE);
            npc.projectileId = 1478;
            npc.endGfx = -1;
            npc.hitDelayTimer = 4;
            npc.attackTimer = 10;
            npc.maxHit = 5;
            boulderAttack();

        } else if (rng <= 85) { // 71–85: Poison AoE
            npc.startAnimation(7905, AnimationPriority.HIGH);
            npc.setAttackType(CombatType.MAGE);
            npc.projectileId = 1470;
            npc.endGfx = -1;
            npc.hitDelayTimer = 4;
            npc.attackTimer = 10;
            npc.maxHit = 5;
            poiAttack();

        } else if (rng <= 95) { // 86–95: Purple fire AoE
            npc.startAnimation(7907, AnimationPriority.HIGH);
            npc.setAttackType(CombatType.MAGE);
            npc.projectileId = 1479;
            npc.endGfx = -1;
            npc.hitDelayTimer = 4;
            npc.attackTimer = 10;
            npc.maxHit = 5;
            fireAttack();

        } else { // 96–100: Bone AoE
            npc.startAnimation(7907, AnimationPriority.HIGH); // adjust anim if you want unique bones anim
            npc.setAttackType(CombatType.MAGE);
            npc.projectileId = 1489;
            npc.endGfx = -1;
            npc.hitDelayTimer = 4;
            npc.attackTimer = 10;
            npc.maxHit = 5;
            boneAttack();
        }
    }

    public static List<Player> targets = new ArrayList<>();
    public static HashMap<Player, Integer> damageCount = new HashMap<>();
    public static final Boundary BOUNDARY = Boundary.VOTE_BOSS;
    private static NPC donaboss;

    /* Updated by Khaos */
    public static void updateTargets() {
        if (donaboss == null || donaboss.isDead) {
            targets.clear();
            return;
        }

        targets = PlayerHandler.getPlayers().stream()
                .filter(Objects::nonNull)
                .filter(p -> !p.isDead)
                .filter(p -> Boundary.isIn(p, BOUNDARY))
                .filter(p -> p.getHeight() == donaboss.getHeight())
                .collect(Collectors.toList());
    }

    public static void spawnNPC() {
        targets.clear();
        damageCount.clear();

        Npcs npcType = Npcs.DONA_BOSS;
        donaboss = NPCSpawning.spawnNpcOld(
                8096,
                3736, 3975, 0, 0,
                npcType.getHp(),
                npcType.getMaxHit(),
                npcType.getAttack(),
                npcType.getDefence()
        );
        donaboss.getBehaviour().setRespawn(false);
        donaboss.getBehaviour().setAggressive(true);
        donaboss.getBehaviour().setRunnable(true);
        donaboss.getHealth().setMaximumHealth(3000);
        donaboss.getHealth().reset();
        announce();
        if (DiscordBot.INSTANCE != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("[ DONOR BOSS ]");
            embed.setImage("https://oldschool.runescape.wiki/images/thumb/Boss.png/150px-Boss.png?fb192");
            embed.setColor(Color.GREEN);
            embed.setTimestamp(java.time.Instant.now());
            embed.addField("Donor Boss has spawned, use ::db to fight!", "\u200B", false);
            DiscordBot.INSTANCE.sendWorldEvent(embed.build());
            //DiscordBot.INSTANCE.sendMessage(DiscordChannelType.WORLD_EVENTS, "@everyone");
        }
    }

    public static void announce() {
        new Broadcast("<img=11> [DONO BOSS] the dono boss [Galvek] has spawned!, use ::vb or ::db")
                .addTeleport(new Position(3738, 3967, 0))
                .copyMessageToChatbox()
                .submit();
    }

    public static void handleRewards() {
        HashMap<String, Integer> map = new HashMap<>();
        damageCount.forEach((p, i) -> {
            if (map.containsKey(p.getUUID())) {
                map.put(p.getUUID(), map.get(p.getUUID()) + 1);
            } else {
                map.put(p.getUUID(), 1);
            }
        });

        for (String s : map.keySet()) {
            if (map.containsKey(s) && map.get(s) > 1) {
                for (Player player : PlayerHandler.getPlayers()) {
                    if (player.getUUID().equalsIgnoreCase(s)) {
                        if (DiscordBot.INSTANCE != null) {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle("DONOR BOSS MULTI-LOGGING ");
                            embed.setThumbnail("https://oldschool.runescape.wiki/images/thumb/Dungeon_entrance_logo.png/150px-Dungeon_entrance_logo.png?1b922");
                            embed.setColor(Color.BLUE);
                            embed.setTimestamp(java.time.Instant.now());
                            embed.addField("Player: ", player.getDisplayName() + " has tried to take more than 2 account's to the Donor Boss!", false);
                            DiscordBot.INSTANCE.sendStaffLogs(embed.build());
                        }
                    }
                }
            }
        }

        map.values().removeIf(integer -> integer > 1);

        damageCount.forEach((player, integer) -> {
            if (integer > 1 && map.containsKey(player.getUUID())) {
                int amountOfDrops = 2;
                if (NPCDeath.isDoubleDrops()) {
                    amountOfDrops++;
                }
                Pass.addExperience(player, 10);
                Server.getDropManager().create(
                        player,
                        donaboss,
                        new Location3D(player.getX(), player.getY(), player.getHeight()),
                        amountOfDrops,
                        8096
                );
            }
        });
        PlayerHandler.executeGlobalMessage("@red@[DONO BOSS]@blu@ the dono boss [@red@Galvek@blu@] has been defeated!");
        despawn();
    }

    public static void despawn() {
        targets.clear();
        damageCount.clear();
        /*
        if (donaboss != null) {
            if (donaboss.getIndex() > 0) {
                donaboss.unregister();
            }
            donaboss = null;
        }
        */
    }

    public static boolean isSpawned() {
        return getDonaboss() != null;
    }

    public static NPC getDonaboss() {
        return donaboss;
    }

    public enum Npcs {

        DONA_BOSS(8096, "Galvek", 3000, 33, 250, 1);

        private final int npcId;
        private final String monsterName;
        private final int hp;
        private final int maxHit;
        private final int attack;
        private final int defence;

        Npcs(final int npcId, final String monsterName, final int hp,
             final int maxHit, final int attack, final int defence) {
            this.npcId = npcId;
            this.monsterName = monsterName;
            this.hp = hp;
            this.maxHit = maxHit;
            this.attack = attack;
            this.defence = defence;
        }

        public int getNpcId() {
            return npcId;
        }

        public String getMonsterName() {
            return monsterName;
        }

        public int getHp() {
            return hp;
        }

        public int getMaxHit() {
            return maxHit;
        }

        public int getAttack() {
            return attack;
        }

        public int getDefence() {
            return defence;
        }
    }

    public static void arcAttack() {
        updateTargets();
        if (donaboss == null || donaboss.isDead || targets.isEmpty()) {
            return;
        }

        for (Player possibleTargets : targets) {
            possibleTargets.gfx0(1466);
            int dam;
            if (possibleTargets.protectingMagic()) {
                dam = 0;
            } else {
                dam = Misc.random(0, 10);
            }
            possibleTargets.appendDamage(dam, (dam > 0 ? Hitmark.HIT : Hitmark.MISS));
        }
    }

    public static void poiAttack() {
        updateTargets();
        if (donaboss == null || donaboss.isDead || targets.isEmpty()) {
            return;
        }

        for (Player possibleTargets : targets) {
            possibleTargets.gfx0(1471);
            int dam;
            if (possibleTargets.protectingMagic()) {
                dam = 0;
            } else {
                dam = Misc.random(0, 10);
            }
            possibleTargets.appendDamage(dam, (dam > 0 ? Hitmark.HIT : Hitmark.MISS));
            if (!possibleTargets.getHealth().getStatus().isPoisoned()) {
                possibleTargets.getHealth().proposeStatus(HealthStatus.POISON, 3, Optional.of(donaboss));
            }
        }
    }

    public static void boulderAttack() {
        updateTargets();
        if (donaboss == null || donaboss.isDead || targets.isEmpty()) {
            return;
        }

        for (Player possibleTargets : targets) {
            possibleTargets.gfx0(1473);
            int dam;
            if (possibleTargets.protectingMagic()) {
                dam = 0;
            } else {
                dam = Misc.random(0, 10);
            }
            possibleTargets.appendDamage(dam, (dam > 0 ? Hitmark.HIT : Hitmark.MISS));
        }
    }

    public static void fireAttack() {
        updateTargets();
        if (donaboss == null || donaboss.isDead || targets.isEmpty()) {
            return;
        }

        for (Player possibleTargets : targets) {
            possibleTargets.gfx0(1478);
            int dam;
            if (possibleTargets.protectingMagic()) {
                dam = 0;
            } else {
                dam = Misc.random(0, 10);
            }
            possibleTargets.appendDamage(dam, (dam > 0 ? Hitmark.HIT : Hitmark.MISS));
        }
    }

    public static void boneAttack() {
        updateTargets();
        if (donaboss == null || donaboss.isDead || targets.isEmpty()) {
            return;
        }

        for (Player possibleTargets : targets) {
            possibleTargets.gfx0(1489);
            int dam;
            if (possibleTargets.protectingMagic()) {
                dam = 0;
            } else {
                dam = Misc.random(0, 10);
            }
            possibleTargets.appendDamage(dam, (dam > 0 ? Hitmark.HIT : Hitmark.MISS));
        }
    }
}
