package io.zaryx.content.bosses.whisperer;

import io.zaryx.Server;
import io.zaryx.content.bosses.hydra.CombatProjectile;
import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.model.*;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.definitions.NpcStats;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.npc.NPCSpawning;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Position;
import io.zaryx.util.Misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TheWhisperer {

    private static final int WHISPERER_NPC_ID = 12345; // replace with actual ID
    private static final int COLUMN_NPC_ID = 12210;
    private static final int COLUMN_HP = 250;
    private static final int HALF_HEALTH = 500; // replace with actual half health value
    private static final int TENTACLE_NPC_ID = 12208; // NPC ID for the tentacle
    private static final int WAVE_GFX_ID = 1904;
    private static final int SCREECH_GFX_ID = 2446;
    private static final CombatProjectile RANGED_PROJECTILE = new CombatProjectile(2445, 50, 25, 0, 100, 0, 50);
    private static final CombatProjectile MAGIC_PROJECTILE = new CombatProjectile(2444, 50, 25, 0, 100, 0, 50);

    private static List<NPC> columns = new ArrayList<>();
    private static List<NPC> tentacles = new ArrayList<>();
    private static boolean columnsSpawned = false;
    private static boolean waveAttackActive = false;
    private static boolean wavePhaseCompleted = false;
    private static boolean enragePhaseActive = false;

    private static int specialAttackCounter = 0;
    private static HashMap<Player, Integer> damageCount = new HashMap<>();

    public static void handleAttacks(NPC npc) {
        if (waveAttackActive || enragePhaseActive) {
            return; // Do not perform other attacks during the wave or enrage phase
        }

        if (npc.getHealth().getCurrentHealth() <= HALF_HEALTH && !columnsSpawned && !wavePhaseCompleted) {
            teleportAndSpawnColumns(npc);
        } else if (!waveAttackActive && wavePhaseCompleted) {
            handleBasicAttacks(npc);
        }
    }

    private static void handleBasicAttacks(NPC npc) {
        if (waveAttackActive) {
            return;
        }

        for (Player target : getTargets()) {
            // Determine attack pattern based on specialAttackCounter
            if (specialAttackCounter == 0) {
                // Initial phase: All projectiles are of the same type
                for (int i = 0; i < 3; i++) {
                    CombatType combatType = (Misc.random(1) == 0 ? CombatType.MAGE : CombatType.RANGE);
                    sendProjectile(combatType == CombatType.MAGE ? MAGIC_PROJECTILE : RANGED_PROJECTILE, target, npc);
                    applyDamage(target, combatType, npc);
                }
            } else if (specialAttackCounter == 1) {
                // After first special attack: Two of one type, one of the other
                for (int i = 0; i < 3; i++) {
                    CombatType combatType = (i < 2 ? CombatType.MAGE : CombatType.RANGE);
                    sendProjectile(combatType == CombatType.MAGE ? MAGIC_PROJECTILE : RANGED_PROJECTILE, target, npc);
                    applyDamage(target, combatType, npc);
                }
            } else {
                // Alternating attacks
                for (int i = 0; i < 3; i++) {
                    CombatType combatType = (i % 2 == 0 ? CombatType.RANGE : CombatType.MAGE);
                    sendProjectile(combatType == CombatType.MAGE ? MAGIC_PROJECTILE : RANGED_PROJECTILE, target, npc);
                    applyDamage(target, combatType, npc);
                }
            }

            // Tentacle Attack
            if (Misc.isLucky(85)) {
                handleTentacleAttack(target);
            }
        }

        if (npc.getHealth().getCurrentHealth() <= 0 && !enragePhaseActive) {
            startEnragePhase(npc);
        }
    }

    private static void applyDamage(Player target, CombatType combatType, NPC npc) {
        int delay = 5; // Typical OSRS delay for hitsplat appearance
        int damage = Misc.random(1, 5);

        if (combatType == CombatType.RANGE && !target.protectingRange()) {
            target.getDamageQueue().add(new Damage(target, damage, delay, target.playerEquipment, Hitmark.HIT, combatType));
        } else if (combatType == CombatType.MAGE) {
            if (target.protectingMagic()) {
                target.getDamageQueue().add(new Damage(target, 0, delay, target.playerEquipment, Hitmark.MISS, combatType));
            } else {
                target.getDamageQueue().add(new Damage(target, damage, delay, target.playerEquipment, Hitmark.HIT, combatType));
            }
        }
    }

    private static void handleTentacleAttack(Player player) {
        int playerX = player.getPosition().getX();
        int playerY = player.getPosition().getY();

        int[][] cornerOffsets = {
                {-4, -4}, {4, -4}, {-4, 4}, {4, 4}
        };

        for (int[] offset : cornerOffsets) {
            int offsetX = offset[0];
            int offsetY = offset[1];

            int tentacleX = playerX + offsetX;
            int tentacleY = playerY + offsetY;

            // Spawn tentacle NPC
            NPC tentacle = NPCSpawning.spawnNpc(TENTACLE_NPC_ID, tentacleX, tentacleY, 0, 0, 0);
            tentacle.startAnimation(new Animation(10266)); // Example animation ID, replace with actual stomping animation ID
            tentacles.add(tentacle);
            handleSplashAttack();
            CycleEventHandler.getSingleton().addEvent(tentacle, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (tentacle.isDead()) {
                        NPCHandler.despawn(tentacle.getNpcId(), 0);
                        tentacle.needRespawn = false;
                        container.stop();
                        return;
                    }

                    tentacle.setDead(true);
                    sendSplashGFX(player, tentacleX, tentacleY);
                    NPCHandler.despawn(tentacle.getNpcId(), 0);
                    tentacle.needRespawn = false;
                    container.stop();
                }
            }, 3); // Delay to allow the smash animation to play
        }
    }

    private static void despawnTentacles() {
        for (NPC tentacle : tentacles) {
            NPCHandler.despawn(tentacle.getNpcId(), 0);
        }
        tentacles.clear();
    }

    private static void sendSplashGFX(Player player, int gfxX, int gfxY) {
        int delay = 0; // Adjust delay as necessary
        int gfxId = 2450; // Example GFX ID for the splash, replace with actual ID
        sendGFXWithDelay(player, gfxX, gfxY, gfxId, delay);
    }

    private static void sendProjectile(CombatProjectile projectile, Player player, NPC npc) {
        int size = (int) Math.ceil((double) npc.getSize() / 2.0);

        int centerX = npc.getX() + size;
        int centerY = npc.getY() + size;
        int offsetX = (centerY - player.getY()) * -1;
        int offsetY = (centerX - player.getX()) * -1;
        player.getPA().createPlayersProjectile(centerX, centerY, offsetX, offsetY, projectile.getAngle(), projectile.getSpeed(), projectile.getGfx(), projectile.getStartHeight(), projectile.getEndHeight(), -player.getIndex() - 1, 65, projectile.getDelay());
    }

    public static void handleSplashAttack() {
        for (Player player : getTargets()) {
            int playerX = player.getPosition().getX();
            int playerY = player.getPosition().getY();

            int[][] diagonalOffsets = {
                    {-4, -4}, {4, -4}, {-4, 4}, {4, 4}
            };
            for (int[] offset : diagonalOffsets) {
                int offsetX = offset[0];
                int offsetY = offset[1];

                int tentacleX = playerX + offsetX;
                int tentacleY = playerY + offsetY;

                int diffX = playerX - tentacleX;
                int diffY = playerY - tentacleY;
                int steps = Math.max(Math.abs(diffX), Math.abs(diffY)) + 1;

                for (int step = 0; step <= steps; step++) {
                    int delay = step;
                    int currentGfxId = 2447 + step;

                    int gfxX = tentacleX + (diffX * step / steps);
                    int gfxY = tentacleY + (diffY * step / steps);

                    sendGFXWithDelay(player, gfxX, gfxY, currentGfxId, delay);
                }
            }
        }
    }

    private static void sendWaveGfxWithDelay(Player player, int spawnX, int spawnY, int gfxId, int delay) {
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                Server.playerHandler.sendStillGfx(new StillGraphic(gfxId, new Position(spawnX, spawnY, 0)), player.getPosition());
                container.stop();
            }
        }, delay);
    }

    private static void sendGFXWithDelay(Player player, int spawnX, int spawnY, int gfxId, int delay) {
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                Server.playerHandler.sendStillGfx(new StillGraphic(gfxId, new Position(spawnX, spawnY, 0)), player.getPosition());
                if (spawnX == player.getPosition().getX() && spawnY == player.getPosition().getY()) {
                    player.appendDamage(Misc.random(5, 15), Hitmark.HIT);
                    player.startGraphic(new Graphic(2450));
                }
                container.stop();
            }
        }, delay);
    }

    private static void teleportAndSpawnColumns(NPC npc) {
        npc.teleport(new Position(2656, 6354)); // Adjusted to the southern end of the cathedral
        startWaveAttack(npc);
    }

    private static void spawnColumns(NPC npc) {
        int[][] columnPositions = {
                {2667, 6364}, {2665, 6363}, {2662, 6364}, {2660, 6364},
                {2658, 6363}, {2657, 6364}, {2656, 6363}, {2654, 6364},
                {2653, 6363}, {2652, 6364}, {2650, 6363}, {2648, 6363}
        };

        for (int[] pos : columnPositions) {
            NpcStats columnStats = NPCSpawning.getStats(COLUMN_HP, 0, 0);
            NPC column = NPCSpawning.spawnNpc(COLUMN_NPC_ID, pos[0], pos[1], 0, 0, 0, columnStats);
            columns.add(column);
        }
        columnsSpawned = true;
    }

    private static void startWaveAttack(NPC npc) {
        waveAttackActive = true;
        npc.teleport(new Position(2656, 6354)); // Southern end of the cathedral
        spawnColumns(npc);

        CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
            int screechCount = 0;

            @Override
            public void execute(CycleEventContainer container) {
                if (screechCount >= 3) {
                    container.stop();
                    waveAttackActive = false;
                    columnsSpawned = false;
                    wavePhaseCompleted = true;
                    npc.teleport(new Position(2657, 6367)); // Return to the original position
                    handleBasicAttacks(npc);
                    despawnColumns();
                    return;
                }

                for (Player player : getTargets()) {
                    sendWaveGfxWithDelay(player, player.getPosition().getX(), player.getPosition().getY(), SCREECH_GFX_ID, 0);
                }

                checkAndDamageColumns();

                screechCount++;
                sendScreechGFX(npc);
            }
        }, 10); // Delay between each screech
    }

    private static void checkAndDamageColumns() {
        Iterator<NPC> iterator = columns.iterator();
        while (iterator.hasNext()) {
            NPC column = iterator.next();
            for (Player player : getTargets()) {
                if (player.distanceToPoint(column.getX(), column.getY()) <= 1) {
                    column.appendDamage(Misc.random(50, 100), Hitmark.HIT); // Damage to destroy column
                    displayColumnHealth(column);
                    if (column.getHealth().getCurrentHealth() <= 0) {
                        iterator.remove();
                        NPCHandler.despawn(column.getNpcId(), 0);
                    }
                    break;
                }
            }
        }
    }

    private static void sendScreechGFX(NPC npc) {
        for (Player player : getTargets()) {
            int startX = npc.getX();
            int startY = npc.getY();

            int endY = startY + 15; // Adjust as necessary to match the distance traveled by the screech

            // Triangular pattern
            for (int y = startY; y <= endY; y++) {
                int offsetX = (y - startY) / 3; // Adjust for the triangular pattern
                for (int x = startX - offsetX; x <= startX + offsetX; x++) {
                    sendWaveGfxWithDelay(player, x, y, SCREECH_GFX_ID, y - startY);
                }
            }
        }
    }

    private static void displayColumnHealth(NPC column) {
        // Display the column's health above it
        int currentHealth = column.getHealth().getCurrentHealth();
        int maxHealth = column.getHealth().getMaximumHealth();
        String healthStatus = currentHealth + "/" + maxHealth;
        column.forceChat(healthStatus);
    }

    private static void despawnColumns() {
        for (NPC column : columns) {
            NPCHandler.despawn(column.getNpcId(), 0);
        }
        columns.clear();
    }

    private static void startEnragePhase(NPC npc) {
        enragePhaseActive = true;
        npc.getHealth().increase(140);
        npc.getHealth().reset(); // Restores any drained stats

        CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
            int attackCounter = 0;
            boolean isMagicAttack = false;

            @Override
            public void execute(CycleEventContainer container) {
                if (npc.getHealth().getCurrentHealth() <= 0) {
                    container.stop();
                    enragePhaseActive = false;
                    return;
                }

                for (Player target : getTargets()) {
                    if (attackCounter % 2 == 0) {
                        isMagicAttack = !isMagicAttack;
                    }

                    CombatProjectile projectile = isMagicAttack ? MAGIC_PROJECTILE : RANGED_PROJECTILE;
                    sendProjectile(projectile, target, npc);

                    int delay = 3; // Faster attack speed during enrage phase
                    int damage = Misc.random(1, 8); // Increased damage range

                    if (isMagicAttack && target.protectingMagic()) {
                        target.getDamageQueue().add(new Damage(target, 0, delay, target.playerEquipment, Hitmark.MISS, CombatType.MAGE));
                    } else if (!isMagicAttack && target.protectingRange()) {
                        target.getDamageQueue().add(new Damage(target, 0, delay, target.playerEquipment, Hitmark.MISS, CombatType.RANGE));
                    } else {
                        target.getDamageQueue().add(new Damage(target, damage, delay, target.playerEquipment, Hitmark.HIT, isMagicAttack ? CombatType.MAGE : CombatType.RANGE));
                    }

                    // Tentacle Attack
                    handleTentacleAttack(target);
                }

                attackCounter++;
            }
        }, 4); // Increased attack frequency
    }

    private static List<Player> getTargets() {
        ArrayList<Player> list = new ArrayList<>();

        for (Player player : PlayerHandler.getPlayers()) {
            if (player != null && (Boundary.isIn(player, Boundary.WHISPERER_BOUNDARY))) {
                if (!player.isDead() && player.getHealth().getCurrentHealth() > 0) {
                    list.add(player);
                }
            }
        }

        return list;
    }

    public static void handleDeath(NPC npc) {
        HashMap<String, Integer> map = new HashMap<>();
        damageCount.forEach((p, i) -> {
            if (map.containsKey(p.getUUID())) {
                map.put(p.getUUID(), map.get(p.getUUID()) + 1);
            } else {
                map.put(p.getUUID(), 1);
            }
        });

        map.values().removeIf(integer -> integer > 1);
        reset();
    }

    private static void reset() {
        columns.clear();
        tentacles.clear();
        columnsSpawned = false;
        waveAttackActive = false;
        wavePhaseCompleted = false;
        enragePhaseActive = false;
        specialAttackCounter = 0;
        damageCount.clear();
    }
}

