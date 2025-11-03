package io.zaryx.content.afkzone;

import io.zaryx.content.skills.Skill;
import io.zaryx.model.Animation;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.world.objects.GlobalObject;
import io.zaryx.util.Location3D;

public class Afk {
    /**
     * The stealing animation
     */
    private static final int ANIMATION = 881;

    public static void Start(Player c, Location3D location, int id) {
        c.facePosition(location.getX(), location.getY());
        c.afk_obj_position = new Position(location.getX(), location.getY(), 0);
        c.stopPlayerSkill = true;
        c.setAfkTier(id);
        CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.AFKZone);
        handleAnimation(c, id);




        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.AFKZone, c, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {

                if (c.isDisconnected()) {
                    container.stop();
                    return;
                }
                if (!c.stopPlayerSkill) {
                    c.stopAnimation();
                    container.stop();
                    return;
                }

                int afkPoints = c.getAfkPoints();

                if (afkPoints < 0) {
                    c.setAfkPoints(Math.abs(afkPoints)); // Convert negative points to positive
                }

                c.setAfkPoints(c.getAfkPoints() + getPoints(c));
                c.setAfkAttempts(c.getAfkAttempts() + getPoints(c));

//                AfkBoss.handleGoblinTick();

                c.afk_position = c.getPosition();
//                c.sendMessage("@red@You now gain " + getPoints(c) + " afk points, Total: " + c.getAfkPoints() + "!", TimeUnit.MINUTES.toMillis(3));

                if (container.getTotalExecutions() % 3 == 0) {
                    c.startAnimation(c.AfkAnimation);
                    if (c.AfkAnimation == 4975) {
                        c.gfx0(831);
                    }
                    if (c.AfkAnimation == 4951) {
                        c.gfx0(819);
                    }
                }

            }
        },3,false);
    }


    public static int getPoints(Player c) {
        int count = 1;

//        33065
        if (c.amDonated >= 20 && c.amDonated < 50) {
            count += 1;
        } else if (c.amDonated >= 50 && c.amDonated < 100) {
            count += 2;
        } else if (c.amDonated >= 100 && c.amDonated < 250) {
            count += 3;
        } else if (c.amDonated >= 250 && c.amDonated < 500) {
            count += 4;
        } else if (c.amDonated >= 500 && c.amDonated < 3000) {
            count += 5;
        } else if (c.amDonated >= 3000) {
            count += 15;
        }

        count += afkSum(c);

        if (c.hasFollower && c.petSummonId == 33065) {
            count *= 2;
        }



        return count;
    }
    public static int[] afk_ids = {26858, 26860, 26862};
    public static int afkSum(Player c) {
        int total = 0;
        for (int grace : afk_ids) {
            if (c.getItems().isWearingItem(grace)) {
                total++;
            }
        }
        return total;
    }


    private static void handleAnimation(Player player, int objectID) {
        switch (objectID) {
            case 35834:

                     if (player.playerLevel[Skill.HERBLORE.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                         if (player.playerLevel[Skill.HERBLORE.getId()] < 50) {
                            player.sendMessage("You need 50 HERBLORE to afk here");
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.startAnimation(new Animation(2282));
                            player.getPA().addSkillXP(15, Skill.HERBLORE.getId(), true);
                        }

                    }
                }, 3 );
                return;

            case 33710:
                if (player.playerLevel[Skill.WOODCUTTING.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                        if (player.playerLevel[Skill.WOODCUTTING.getId()] < 50) {
                            player.sendMessage("You need 50 WOODCUTTING to afk here");
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.getPA().addSkillXP(15, Skill.WOODCUTTING.getId(), true);
                            player.startAnimation(new Animation(8778));
                        }

                    }
                }, 3 );
                return;

            case 28791:
                if (player.playerLevel[Skill.FIREMAKING.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                        if (player.playerLevel[Skill.FIREMAKING.getId()] < 50) {
                            player.sendMessage("@red@You need 50 FIREMAKING to afk here");
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.startAnimation(new Animation(4975));
                            player.getPA().addSkillXP(15, Skill.FIREMAKING.getId(), true);

                        }

                    }
                }, 3 );
                return;

            case 10091:
                if (player.playerLevel[Skill.FISHING.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                        if (player.playerLevel[Skill.FISHING.getId()] < 50) {
                            player.sendMessage("@red@You need 50 FISHING to afk here");
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.startAnimation(new Animation(619));
                            player.getPA().addSkillXP(15, Skill.FISHING.getId(), true);
                        }

                    }
                }, 3 );
                return;


            case 20211:
                if (player.playerLevel[Skill.AGILITY.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                        if (player.playerLevel[Skill.AGILITY.getId()] < 50) {
                            player.sendMessage("@red@You need 50 AGILITY to afk here");
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.startAnimation(new Animation(839));
                            player.getPA().addSkillXP(15, Skill.AGILITY.getId(), true);
                        }

                    }
                }, 3 );
                return;

            case 26324:
                if (player.playerLevel[Skill.FLETCHING.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                        if (player.playerLevel[Skill.FLETCHING.getId()] < 50) {
                            player.sendMessage("@red@You need 50 FLETCHING to afk here");
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.startAnimation(new Animation(6702));
                            player.getPA().addSkillXP(15, Skill.FLETCHING.getId(), true);
                        }

                    }
                }, 3 );
                return;

            case 7746:
                if (player.playerLevel[Skill.FARMING.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                        if (player.playerLevel[Skill.FARMING.getId()] < 50) {
                            player.sendMessage("@red@You need 50 FARMING to afk here");
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.startAnimation(new Animation(2273));
                            player.getPA().addSkillXP(15, Skill.FARMING.getId(), true);
                        }

                    }
                }, 3 );
                return;
            case 36572:
                if (player.playerLevel[Skill.THIEVING.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.playerLevel[Skill.THIEVING.getId()] < 50) {
                            player.sendMessage("@red@You need 50 THIEVING to afk here");
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.startAnimation(new Animation(881));
                            player.getPA().addSkillXP(15, Skill.THIEVING.getId(), true);
                        }

                    }
                }, 3 );
                return;
            case 28562:
                if (player.playerLevel[Skill.SMITHING.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                        if (player.playerLevel[Skill.SMITHING.getId()] < 50) {
                            player.sendMessage("@red@You need 50 SMITHING to afk here");
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.startAnimation(new Animation(1649));
                            player.getPA().addSkillXP(15, Skill.SMITHING.getId(), true);
                        }

                    }
                }, 3 );
                return;
            case 8986:
                if (player.playerLevel[Skill.HUNTER.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                        if (player.playerLevel[Skill.HUNTER.getId()] < 50) {
                            player.sendMessage("@red@You need 50 HUNTER to afk here");
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.startAnimation(new Animation(2243));
                            player.getPA().addSkillXP(15, Skill.HUNTER.getId(), true);
                        }

                    }
                }, 3 );
                return;
            case 33257:
                if (player.playerLevel[Skill.MINING.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                        if (player.playerLevel[Skill.MINING.getId()] < 50) {
                            player.sendMessage("@red@You need 50 MINING to afk here");
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.startAnimation(new Animation(6746));
                            player.getPA().addSkillXP(15, Skill.MINING.getId(), true);
                        }

                    }
                }, 3 );
                return;
            case 39095:
                if (player.playerLevel[Skill.RUNECRAFTING.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                        if (player.playerLevel[Skill.RUNECRAFTING.getId()] < 50) {
                            player.sendMessage("@red@You need 50 RUNECRAFTING to afk here");
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.startAnimation(new Animation(645));
                            player.getPA().addSkillXP(15, Skill.RUNECRAFTING.getId(), true);
                        }

                    }
                }, 3 );
                return;
            case 30932:
                if (player.playerLevel[Skill.COOKING.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.playerLevel[Skill.COOKING.getId()] < 50) {
                            player.sendMessage("@red@You need 50 COOKING to afk here");
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.startAnimation(new Animation(897));
                            player.getPA().addSkillXP(15, Skill.COOKING.getId(), true);
                        }

                    }
                }, 3 );
                return;
            case 39395:
                if (player.playerLevel[Skill.CRAFTING.getId()] > 49)
                    if (!CycleEventHandler.getSingleton().getEvents().isEmpty()) {
                        CycleEventHandler.getSingleton().stopEvents(player);
                    }
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.isDisconnected()) {
                            container.stop();
                            return;
                        }
                        if (player.isRunning() || player.isMoving) {
                            container.stop();
                            return;
                        }
                        if (player.playerLevel[Skill.CRAFTING.getId()] < 50) {
                            player.sendMessage("@red@You need 50 CRAFTING to afk here");
                            container.stop();
                            return;
                        }
                        int amount = container.getCyclesBetweenExecution();
                        if (amount > 1) {
                            player.startAnimation(new Animation(2269));
                            player.getPA().addSkillXP(15, Skill.CRAFTING.getId(), true);
                        }

                    }
                }, 3 );
        }}


    public static boolean handleAFKObjectCheck(Player player, GlobalObject go) {
        switch (go.getObjectId()) {
            case 35834:
            case 33710:
            case 8988:
            case 8986:
            case 28791:
            case 10091:
            case 35971:
            case 36570:
            case 36571:
            case 36572:
            case 20211:
            case 39095:
            case 33257:
            case 28562:
            case 15251:
            case 35969:
            case 30932:
            case 39395:
            case 7746:
            case 26324:
                player.afk_object = go.getObjectId();
                return true;
        }

        return false;
    }
}
