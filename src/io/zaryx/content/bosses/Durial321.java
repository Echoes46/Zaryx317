package io.zaryx.content.bosses;

import com.google.common.collect.Lists;
import io.zaryx.Server;
import io.zaryx.content.battlepass.Pass;
import io.zaryx.content.bosses.nex.attacks.IceBarrage;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.combat.death.NPCDeath;
import io.zaryx.content.combat.npc.NPCAutoAttackBuilder;
import io.zaryx.model.Animation;
import io.zaryx.model.CombatType;
import io.zaryx.model.ProjectileBaseBuilder;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCSpawning;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.sql.dailytracker.TrackerType;
import io.zaryx.util.Location3D;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.Discord;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Durial321 {

    public static boolean spawned;
    public static boolean alive;
    public static NPC durial;
    public static HashMap<Player, Integer> damageCount = new HashMap<>();
    public static List<Player> targets;
    private static void updateTargets() {
        targets = PlayerHandler.getPlayers().stream().filter(plr ->
                !plr.isDead && plr.getPosition().withinDistance(durial.getPosition(), 20) && plr.getHeight() == durial.getHeight()).collect(Collectors.toList());
    }
    private static void reset() {
        if (durial != null) {
            if (durial.getIndex() > 0) {
                durial.unregister();
            }
            durial = null;
        }
        if (!targets.isEmpty()) {
            targets.clear();
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
    }

    public static void handleDeath(NPC npc) {
        if (npc.getNpcId() == 5169) {
            spawned = false;
            alive = false;
            PlayerHandler.executeGlobalMessage("@cr22@[@red@Durial321@bla@] @red@Durial is dead, the event has ended!@cr22@");
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
                            Discord.writeServerSyncMessage("```[Durial321] " + player.getDisplayName() + " has tried to take more than 2 account's there!```");
                        }
                    }
                }
            }

            map.values().removeIf(integer -> integer > 1);

            damageCount.forEach((player, integer) -> {
                if (integer > 100 && map.containsKey(player.getUUID())) {
                    int amountOfDrops = 1;
                    if (NPCDeath.isDoubleDrops()) {
                        amountOfDrops++;
                    }
                    Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 5169);
                    Pass.addExperience(player, 5);
                }
            });
            reset();
        }
    }

    public static void init() {
        PlayerHandler.executeGlobalMessage("@cr22@[@red@Durial321@bla@] @red@The Falador Massacre event is active!@cr22@ ::fally");
        Discord.writeIngameEvents("[Durial321] Has just spawned inside Falador! ::fally");
        TrackerType.DURIAL.addTrackerData(1);
        spawned = true;
        alive = true;
        durial = NPCSpawning.spawnNpc(5169,2965,3385,0,1,5);
        durial.getBehaviour().setRespawn(false);
        durial.getBehaviour().setAggressive(true);
        durial.getHealth().setMaximumHealth(25000);
        durial.getHealth().reset();
        durial.setNpcAutoAttacks(Lists.newArrayList(new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(2)
                        .setHitDelay(1)
                        .setMultiAttack(false)
                        .setAnimation(new Animation(1979))
                        .setAttackDelay(3)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(-1).setCurve(16).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnHit(attack -> {
                            updateTargets();
                            new IceBarrage(attack.getVictim().asPlayer(), targets);
                        })
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(1) == 0)
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(1)
                        .setHitDelay(1)
                        .setAnimation(new Animation(1658))
                        .setAttackDelay(3)
                        .setOnHit(attack -> {
                            updateTargets();
                            for (Player target : targets) {
                                int dmg = Misc.random(0, 10);
                                if (dmg > 0) {
                                    if (target.protectingMelee()) {
                                        dmg = (dmg / 2);
                                        target.appendDamage(dmg, Hitmark.HIT);
                                    }
                                } else if (dmg == 0) {
                                    target.appendDamage(dmg, Hitmark.MISS);
                                }
                            }
                        })
                        .createNPCAutoAttack()));
    }
}
