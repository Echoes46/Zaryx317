package io.zaryx.content.skills.firemake;

import io.zaryx.content.achievement.AchievementType;
import io.zaryx.content.achievement.Achievements;
import io.zaryx.content.skills.DoubleExpScroll;
import io.zaryx.content.skills.Skill;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.broadcasts.Broadcast;
import io.zaryx.model.world.objects.GlobalObject;
import io.zaryx.util.Misc;

import java.util.Arrays;
import java.util.Comparator;

public class Burner extends Event<Player> {

    private LogData log = null;
    private GlobalObject object;

    int objectId = attachment.getInStream().readInteger();


    public Burner(Player player, LogData log) {
        super("skilling", player, (Boundary.isIn(player, Boundary.DONATOR_ZONE) || Boundary.isIn(player, Boundary.DONATOR_ZONE_NEW) ? 1 : 3));
        this.log = log;
    }

    public static int totalLogsAdded = 0;

    public static boolean handleBonfireClick(Player player, int objectId) {
        if (objectId == 30019) {
            LogData[] allLogData = LogData.values();
            Arrays.sort(allLogData, Comparator.comparing(LogData::getlevelRequirement).reversed());

            CycleEvent burnLogEvent = new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    for (LogData logData : allLogData) {
                        if (player.getItems().playerHasItem(logData.getlogId()) && player.playerLevel[11] >= logData.getlevelRequirement()) {
                            player.startAnimation(897);
                            player.getItems().deleteItem(logData.getlogId(), 1);
                            Achievements.increase(player, AchievementType.FIRE, 1);
                            int experience = (int) logData.getExperience();
                            if (Boundary.isIn(player, Boundary.PREMIUMBONFIRE)) {
                                experience *= 1.5;
                                player.getItems().addItem(995, 75 + Misc.random(750));
                                player.getPA().addSkillXP(experience, 11, true);
                            }
                            player.getPA().addSkillXP((int) logData.getExperience(), 11, true);
                            player.sendMessage("You add some " + logData.name().toLowerCase().replaceAll("_", " ") + " to the bonfire.");
                            totalLogsAdded++;
                            if (totalLogsAdded == 1000) {
                                new Broadcast("<img=10><col=6666FF> [Bonfire] : 1000 Logs have been burnt 4000 more for Double Exp! ").copyMessageToChatbox().submit();
                            }
                            if (totalLogsAdded == 2000) {
                                new Broadcast("<img=10><col=6666FF> [Bonfire] : 2000 Logs have been burnt 3000 more for Double Exp! ").copyMessageToChatbox().submit();
                            }
                            if (totalLogsAdded == 5000) {
                                // Broadcast a message and activate double experience event
                                new Broadcast("<img=10><col=6666FF> [Bonfire] : 10,000 Logs have been Burnt, 1.5x is exp now active for 1 hr.").copyMessageToChatbox().submit();
                                // Iterate over all players and open the double experience scroll for each one
                                for (Player p : PlayerHandler.players) {
                                    if (p != null) {
                                        DoubleExpScroll.openScroll(p);
                                    }
                                }
                                totalLogsAdded = 0; // Reset the counter
                                stop();
                            }


                            return;
                        }
                    }

                    player.sendMessage("You need logs to add to the bonfire, and you must meet the level requirements to burn them.");
                    container.stop();
                }
            };

            CycleEventContainer container = new CycleEventContainer(-1, player, burnLogEvent, 2);
            CycleEventHandler.getSingleton().addEvent(container);

            return true;
        }
        return false;
    }



    @Override
    public void execute() {
        if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
            stop();
            return;
        }

        if (log == null) {
            stop();
            return;
        }

        if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
            attachment.getInterfaceEvent().execute();
            stop();
            return;
        }

        double osrsExperience = 0;

        if (!attachment.getItems().playerHasItem(log.getlogId())) {
            attachment.sendMessage("You do not have anymore of this log.");
            stop();
            return;
        }

            attachment.getItems().deleteItem(log.getlogId(), 1);
            Achievements.increase(attachment, AchievementType.FIRE, 1);
            osrsExperience = log.getExperience() + log.getExperience() / 10;

            attachment.getPA().addSkillXPMultiplied((int) osrsExperience * 2, Skill.FIREMAKING.getId(), true);
        }
}
