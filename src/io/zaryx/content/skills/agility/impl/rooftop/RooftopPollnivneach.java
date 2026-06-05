package io.zaryx.content.skills.agility.impl.rooftop;

import io.zaryx.content.achievement.AchievementType;
import io.zaryx.content.achievement.Achievements;
import io.zaryx.content.skills.agility.AgilityHandler;
import io.zaryx.content.skills.agility.MarkOfGrace;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.player.Player;

import java.util.Arrays;

/**
 * Rooftop Agility Pollnivneach
 *
 * Updated by Khaos
 */
public class RooftopPollnivneach {

    public static final int BASKET = 14935;
    public static final int STALL = 14936;
    public static final int BANNER = 14937;
    public static final int GAP1 = 14938;
    public static final int TREE = 14939;
    public static final int WALL = 12230;
    public static final int BARS = 14941;
    public static final int TREE2 = 14944;
    public static final int LINE = 14945;
    public static final int LADDER = 6260;

    public static int[] POLLNIVNEACH_OBJECTS = {
            BASKET, STALL, BANNER, GAP1, TREE, WALL, BARS, TREE2, LINE, LADDER
    };

    public boolean execute(final Player c, final int objectId) {
        if (!isPollnivneachObject(objectId)) {
            return false;
        }

        if (System.currentTimeMillis() - c.lastObstacleFail < 3000) {
            return false;
        }

        if (c.getAgilityHandler().checkLevel(c, objectId)) {
            return false;
        }

        switch (objectId) {
            case BASKET:
                MarkOfGrace.spawnMarks(c, "POLLNIVNEACH");
                AgilityHandler.delayEmote(c, "CLIMB_UP", 3351, 2964, 1, 2);
                c.getAgilityHandler().agilityProgress[0] = true;
                return true;

            case STALL:
                if (!hasProgress(c, 0)) {
                    resetCourse(c);
                    c.sendMessage("You need to start the course from the beginning.");
                    return false;
                }

                if (AgilityHandler.failObstacle(c, 3351, 2971, 0)) {
                    return false;
                }

                MarkOfGrace.spawnMarks(c, "POLLNIVNEACH");

                CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                    int ticks;

                    @Override
                    public void execute(CycleEventContainer container) {
                        if (c.isDisconnected()) {
                            container.stop();
                            return;
                        }

                        switch (ticks++) {
                            case 0:
                                c.startAnimation(3067);
                                AgilityHandler.delayEmote(c, "JUMP", 3349, 2972, 1, 1);
                                c.facePosition(3352, 2973);
                                break;

                            case 2:
                                c.startAnimation(3067);
                                AgilityHandler.delayEmote(c, "JUMP", 3352, 2974, 1, 1);
                                c.getAgilityHandler().agilityProgress[1] = true;
                                container.stop();
                                break;
                        }
                    }

                    @Override
                    public void onStopped() {
                    }
                }, 2);
                return true;

            case BANNER:
                if (!hasProgress(c, 1)) {
                    resetCourse(c);
                    c.sendMessage("You need to complete the previous obstacle first.");
                    return false;
                }

                MarkOfGrace.spawnMarks(c, "POLLNIVNEACH");

                c.startAnimation(3067);
                CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                    int ticks;

                    @Override
                    public void execute(CycleEventContainer container) {
                        if (c.isDisconnected()) {
                            container.stop();
                            return;
                        }

                        switch (ticks++) {
                            case 0:
                                AgilityHandler.delayEmote(c, "HANG_ON_POST", 3357, 2978, 2, 1);
                                c.facePosition(3358, 2978);
                                break;

                            case 1:
                                c.startAnimation(1118);
                                break;

                            case 2:
                                AgilityHandler.delayEmote(c, "HANG_ON_POST", 3360, 2978, 1, 1);
                                c.getAgilityHandler().agilityProgress[2] = true;
                                c.stopAnimation();
                                container.stop();
                                break;
                        }
                    }

                    @Override
                    public void onStopped() {
                    }
                }, 3);
                return true;

            case GAP1:
                if (!hasProgress(c, 2)) {
                    resetCourse(c);
                    c.sendMessage("You need to complete the previous obstacle first.");
                    return false;
                }

                if (AgilityHandler.failObstacle(c, 3366, 2976, 0)) {
                    return false;
                }

                MarkOfGrace.spawnMarks(c, "POLLNIVNEACH");

                AgilityHandler.delayEmote(c, "JUMP", 3366, 2976, 1, 2);
                c.getAgilityHandler().agilityProgress[3] = true;
                return true;

            case TREE:
                if (!hasProgress(c, 3)) {
                    resetCourse(c);
                    c.sendMessage("You need to complete the previous obstacle first.");
                    return false;
                }

                if (AgilityHandler.failObstacle(c, 3366, 2979, 0)) {
                    return false;
                }

                MarkOfGrace.spawnMarks(c, "POLLNIVNEACH");

                CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                    int ticks;

                    @Override
                    public void execute(CycleEventContainer container) {
                        if (c.isDisconnected()) {
                            container.stop();
                            return;
                        }

                        switch (ticks++) {
                            case 0:
                                c.startAnimation(3067);
                                AgilityHandler.delayEmote(c, "JUMP", 3368, 2979, 3, 1);
                                c.facePosition(3368, 2982);
                                break;

                            case 2:
                                c.startAnimation(3067);
                                AgilityHandler.delayEmote(c, "JUMP", 3367, 2982, 1, 1);
                                c.getAgilityHandler().agilityProgress[4] = true;
                                container.stop();
                                break;
                        }
                    }

                    @Override
                    public void onStopped() {
                    }
                }, 2);
                return true;

            case WALL:
                if (!hasProgress(c, 4)) {
                    resetCourse(c);
                    c.sendMessage("You need to complete the previous obstacle first.");
                    return false;
                }

                MarkOfGrace.spawnMarks(c, "POLLNIVNEACH");

                c.facePosition(3365, 2983);
                AgilityHandler.delayEmote(c, "CLIMB_UP", 3365, 2983, 2, 2);
                c.getAgilityHandler().agilityProgress[5] = true;
                return true;

            case BARS:
                if (!hasProgress(c, 5)) {
                    resetCourse(c);
                    c.sendMessage("You need to complete the previous obstacle first.");
                    return false;
                }

                MarkOfGrace.spawnMarks(c, "POLLNIVNEACH");

                c.getPA().movePlayer(3358, 2984, c.heightLevel);
                c.setForceMovement(3358, 2992, 0, 250, "WEST", 744);
                c.getAgilityHandler().agilityProgress[6] = true;
                return true;

            case TREE2:
                if (!hasProgress(c, 6)) {
                    resetCourse(c);
                    c.sendMessage("You need to complete the previous obstacle first.");
                    return false;
                }

                if (AgilityHandler.failObstacle(c, 3358, 2998, 0)) {
                    return false;
                }

                MarkOfGrace.spawnMarks(c, "POLLNIVNEACH");

                CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                    int ticks;

                    @Override
                    public void execute(CycleEventContainer container) {
                        if (c.isDisconnected()) {
                            container.stop();
                            return;
                        }

                        switch (ticks++) {
                            case 0:
                                c.startAnimation(3067);
                                AgilityHandler.delayEmote(c, "JUMP", 3360, 2997, 2, 1);
                                c.facePosition(3359, 3000);
                                break;

                            case 2:
                                c.startAnimation(3067);
                                AgilityHandler.delayEmote(c, "JUMP", 3359, 3000, 2, 1);
                                c.getAgilityHandler().agilityProgress[7] = true;
                                container.stop();
                                break;
                        }
                    }

                    @Override
                    public void onStopped() {
                    }
                }, 2);
                return true;

            case LINE:
                if (!hasProgress(c, 7)) {
                    resetCourse(c);
                    c.sendMessage("You need to complete the previous obstacle first.");
                    return false;
                }

                if (AgilityHandler.failObstacle(c, 3361, 3000, 0)) {
                    return false;
                }

                MarkOfGrace.spawnMarks(c, "POLLNIVNEACH");

                c.facePosition(3363, 3000);
                CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                    int ticks;

                    @Override
                    public void execute(CycleEventContainer container) {
                        if (c.isDisconnected()) {
                            container.stop();
                            return;
                        }

                        switch (ticks++) {
                            case 0:
                                c.startAnimation(3067);
                                AgilityHandler.delayEmote(c, "JUMP", 3364, 3000, 2, 1);
                                c.facePosition(3364, 3002);
                                break;

                            case 2:
                                c.startAnimation(3067);
                                AgilityHandler.delayEmote(c, "JUMP", 3364, 3001, 1, 1);
                                c.getAgilityHandler().agilityProgress[8] = true;
                                container.stop();
                                break;
                        }
                    }

                    @Override
                    public void onStopped() {
                    }
                }, 2);
                return true;

            case LADDER:
                if (!hasProgress(c, 8)) {
                    resetCourse(c);
                    c.sendMessage("You need to complete the full course before finishing the lap.");
                    return false;
                }

                MarkOfGrace.spawnMarks(c, "POLLNIVNEACH");

                AgilityHandler.delayEmote(c, "CLIMB_UP", 3351, 2961, 0, 1);
                c.getAgilityHandler().roofTopFinished(c, 8, 890, 22000);
                Achievements.increase(c, AchievementType.AGIL, 1);

                resetCourse(c);
                return true;
        }

        return false;
    }

    private boolean isPollnivneachObject(int objectId) {
        for (int id : POLLNIVNEACH_OBJECTS) {
            if (id == objectId) {
                return true;
            }
        }
        return false;
    }

    private boolean hasProgress(Player c, int index) {
        return c.getAgilityHandler().agilityProgress.length > index
                && c.getAgilityHandler().agilityProgress[index];
    }

    private void resetCourse(Player c) {
        Arrays.fill(c.getAgilityHandler().agilityProgress, false);
    }
}