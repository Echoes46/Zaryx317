package io.zaryx.content.minigames.arbograve;

import io.zaryx.Server;
import io.zaryx.content.achievement.AchievementType;
import io.zaryx.content.achievement.Achievements;
import io.zaryx.content.battlepass.Pass;
import io.zaryx.content.bosses.hespori.Hespori;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.instances.InstancedArea;
import io.zaryx.content.minigames.arbograve.bosses.Leech;
import io.zaryx.content.prestige.PrestigePerks;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.entity.player.mode.Mode;
import io.zaryx.model.entity.player.mode.ModeType;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;

public class ArbograveBoss extends NPC {

    public ArbograveBoss(int npcId, Position position, InstancedArea instancedArea) {
        super(npcId, position);
        instancedArea.add(this);
        getBehaviour().setRespawn(false);
        getBehaviour().setAggressive(true);
        getCombatDefinition().setAggressive(true);
    }

    public void onDeath() {
        Entity killer = calculateKiller();
        if (getInstance() != null) {
            if (this.asNPC() != null && this.asNPC().getNpcId() == 6477) {
                for (NPC npc : getInstance().getNpcs()) {
                    npc.appendDamage(npc.getHealth().getMaximumHealth(), Hitmark.HIT);
                }
                for (Player player : getInstance().getPlayers()) {
                    player.sendMessage("You have completed Raid Of The Damned!");
                    player.getBossTimers().death("Raid Of The Damned");
                    Pass.addExperience(player, 2);
                    player.arboCompletions++;
                    int points = Misc.random(500, 2000);
                    int rng = Misc.trueRand(1);
                    if (rng == 1) {
                        points /= 2;
                    }

                    player.arboPoints += points;
                    player.sendMessage("You now have a total of " + player.arboPoints + " Raid Of The Damned Points!");
                    int keys = player.getArboContainer().lives;

                    if (keys > 0) {
                        if (PrestigePerks.hasRelic(player, PrestigePerks.TRIPLE_HESPORI_KEYS) && Misc.isLucky(10)) {
                            keys *= 3;
                        }

                        if (Hespori.KRONOS_TIMER > 0 && Misc.random(100) >= 95) {
                            keys *= 2;
                        }

                        player.getItems().addItemUnderAnyCircumstance(2400, keys);
                    } else {
                        player.sendMessage("You had no key's remaining so you get fuck all.");
                    }
                    if (player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.GROUP_WILDYMAN))) {
                        player.moveTo(new Position(3182, 3472, 0));
                        Achievements.increase(player, AchievementType.ARBO, 1);
                    } else {

                        player.moveTo(new Position(2845, 3273, 0));
                        Achievements.increase(player, AchievementType.ARBO, 1);
                    }
                }
            }

            if (asNPC().getNpcId() != 3233 && asNPC().getNpcId() != 1782) {
                new Leech(new Position(asNPC().getX(), asNPC().getY()), asNPC().getInstance());

                for (Player player : asNPC().getInstance().getPlayers()) {
                    Server.itemHandler.createGroundItem(player, new GameItem(6691, Misc.random(2, 4)), new Position(asNPC().getX(), asNPC().getY(), player.getHeight()), 0);
                }
            }
        }
    }
}
