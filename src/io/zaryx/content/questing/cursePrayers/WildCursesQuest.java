package io.zaryx.content.questing.cursePrayers;

import com.google.common.collect.Lists;
import io.zaryx.content.combat.melee.CombatPrayer;
import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueExpression;
import io.zaryx.content.questing.Quest;
import io.zaryx.content.skills.Skill;
import io.zaryx.model.Npcs;
import io.zaryx.model.SkillLevel;
import io.zaryx.model.collisionmap.WorldObject;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCSpawning;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;

import java.util.List;

/**
 * Author Kai - Discord = Zds_Kai
 */

public class WildCursesQuest extends Quest {

    private static final int RELIC_FRAGMENT_1 = 6820;
    private static final int RELIC_FRAGMENT_2 = 9042;
    private static final int RELIC_FRAGMENT_3 = 9044;
    private static boolean FOUND_RELIC_1 = false;
    private static boolean FOUND_RELIC_2 = false;
    private static boolean FOUND_RELIC_3 = false;
    private static boolean DEFEATED_GUARDIAN = false;
    private static final int CURSE_SCROLL = 719;

    @Override
    public String getName() {
        return "Forgotten Prayers";
    }

    @Override
    public List<SkillLevel> getStartRequirements() {
        return List.of(
                new SkillLevel(Skill.MAGIC, 60),
                new SkillLevel(Skill.ATTACK, 50),
                new SkillLevel(Skill.MINING, 50));
    }

    @Override
    public List<String> getJournalText(int stage) {
        List<String> journalText = Lists.newArrayList();
        switch (stage) {
            case 0:
                journalText.add("An ancient historian named Mairin seeks your help");
                journalText.add("in uncovering the secrets of the Forgotten Prayers.");
                journalText.add("She was last seen settling near the");
                journalText.add("Wilderness Home Area.");
                journalText.add("Rewards:");
                journalText.add("Access to Forgotten Prayers.");
                journalText.add("10x Exp Lamps");
                journalText.addAll(getStartRequirementLines());
                journalText.add("10m Gold Coins");
                break;
            case 1:
                journalText.add("You have met Mairin");
                journalText.add("She has agreed to share her knowledge with you");
                journalText.add("if you assist her in gathering the relics.");
                journalText.add("Find her when you are ready to start.");
                break;
            case 2:
                journalText.add("Mairin has a riddle for you:");
                journalText.add("");
                journalText.add("Along the coast of the lava");
                journalText.add("should i dig or should i rather?");
                journalText.add("Ask again and you might seek");
                journalText.add("more information you stupid geek :kiss:");
                break;
            case 3:
                journalText.add("You have found the Ancient Relic.");
                journalText.add("Now Mairin needs a fragment to restore it.");
                journalText.add("A castle to the north");
                journalText.add("Where thieving comes easy?");
                journalText.add("with birds?!");
                break;
            case 4:
                journalText.add("You have found the Ancient Relic.");
                journalText.add("Now Mairin needs a fragment to restore it.");
                journalText.add("A castle to the north");
                journalText.add("Where thieving comes easy?");
                journalText.add("with birds?!");
                break;
            case 5:
                journalText.add("Deafeat the guardian to unlock the prayers!");
                journalText.add("Mairan said somewhere west down the path?");
                journalText.add("with birds?!");
                break;
            case 6:
                journalText.add("I have Defeated the guardian!");
                journalText.add("Return to Mairan and let her know!");
                break;
            case 7:
                journalText.add("You have defeated the guardian and restored the relic.");
                journalText.add("Speak to Mairin to receive your rewards.");
                break;
        }
        return journalText;
    }

    @Override
    public int getCompletionStage() {
        return 6;
    }

    @Override
    public int getRequiredQuestPoints() {
        return 0;
    }

    @Override
    public List<String> getCompletedRewardsList() {
        return Lists.newArrayList("Access to Ancient Curses.", "10x Experience Lamps", "Ancient Relic");
    }

    @Override
    public boolean handleNpcClick(NPC npc, int option) {
        if (npc.getNpcId() == 7784) { // TODO EDIT THIS TO THE CORRECT NPC ID
            /*if ( !player.getMode().isWildyman()) {
                player.start(getMairinDialogue()
                        .npc(DialogueExpression.ANGER_1, "You must be either a Wildyman or a Group Wildyman, ", "to start this quest!"));
                return false;
            }*/
            if (getStage() == 0 && !canStartQuest()) {
                player.start(getMairinDialogue()
                        .npc(DialogueExpression.ANGER_1, "You must have at least 5 quest points to start this quest!"));
                return false;
            }
            if (getStage() == 0) {
                if (!canStartQuest()) {
                    player.start(getMairinDialogue()
                            .npc(DialogueExpression.ANGER_1, "You must have at least 5 quest points to start this quest!"));
                    return false;
                }
                if (!player.getItems().playerHasItem(995, 10000000)) {
                    player.start(getMairinDialogue()
                            .npc(DialogueExpression.ANGER_1, "Come talk to me when you have 10 million gold coins!"));
                    return false;
                } else {
                    player.getItems().deleteItem(995, 10000000);
                    player.start(getMairinDialogue()
                            .player("Hello, are you Mairin the Magnificent?")
                            .npc(DialogueExpression.DISTRESSED, "Yes, I am Mairin. What brings you here?")
                            .player("I am " + player.getDisplayName() + " and I seek to uncover the secrets", "of the Forgotten Prayers.")
                            .npc(DialogueExpression.PLAIN_EVIL, "The Forgotten Prayers are hidden and powerful.")
                            .npc(DialogueExpression.LAUGH_2, "It took me many years to find clues about them.", "What makes you think you can succeed?")
                            .player(DialogueExpression.ANNOYED, "I have the skills and determination needed,", "and I am ready to do whatever it takes!")
                            .npc(DialogueExpression.ANNOYED, "Many have tried and failed,", "but perhaps you will be different.")
                            .npc(DialogueExpression.HAPPY, "Very well, I will guide you to the relics!")
                            .player(DialogueExpression.HAPPY, "Thank you!")
                            .npc(DialogueExpression.CALM, "Come back and talk to me when you are ready.")
                            .exit(player -> incrementStage()));
                }
            } else if (getStage() == 1) {
                player.start(getMairinDialogue()
                        .npc("Let's begin the quest for the Forgotten Prayers!")
                        .npc("In the mines where shadows creep,")
                        .npc("Find the relic buried deep,")
                        .npc("Past the rocks that shimmer bright,")
                        .npc("Seek it out with all your might.")
                        .exit(player -> incrementStage()));
            } else if (getStage() == 2) {
                if (FOUND_RELIC_1) {
                    player.getItems().deleteItem(RELIC_FRAGMENT_1, 1);
                    player.start(getMairinDialogue()
                            .npc(DialogueExpression.HAPPY, "You've found a Relic.")
                            .npc("Now we need the rest of the relics to restore the prayers.")
                            .npc("Go to the castle to the north west.")
                            .npc("Collect the other Relic and bring it back to me.")
                            .exit(player -> incrementStage()));
                } else {
                    player.start(getMairinDialogue()
                            .npc("Have you found the other Relic?")
                            .player("Not yet, can you give me more clues?")
                            .npc("Useless being.. The coordinates are... 3078, 3806 ")
                            .npc("Use an online map to help or something, weakling..")
                            .player("Alright, thanks.."));
                }
            } else if (getStage() == 3) {
                if (FOUND_RELIC_2) {
                    player.getItems().deleteItem(RELIC_FRAGMENT_2, 1);
                    player.start(getMairinDialogue()
                            .npc(DialogueExpression.ANGER_1, "Adventurer!, I'am amazed", "you have found the Stone seal.")
                            .exit(player -> incrementStage()));
                } else {
                    player.start(getMairinDialogue()
                            .npc("Have you found the other Relic?")
                            .player("Not yet, can you give me more clues?")
                            .npc("Fine....to thieve is easy here.")
                            .player("Alright, I will keep searching."));
                }
            }
            else if (getStage() == 4) {
                player.start(getMairinDialogue()
                        .npc(DialogueExpression.HAPPY, "Now you must defeat the guardian.")
                                .npc("I have heard he takes true form when opening a chest...")
                        .npc("Rumour has it, hes located west down a path..")
                        .npc("Where birds are plenty and a cart of fallen victims lay")
                        .exit(player -> incrementStage()));

            } else if (getStage() == 5) {
                if (DEFEATED_GUARDIAN) {
                    player.start(getMairinDialogue()
                            .npc(DialogueExpression.HAPPY, "You have defeated the guardian and restored the relic.")
                            .npc("You are truly worthy of the Forgotten Prayers.")
                            .exit(player -> {
                                incrementStage();
                                player.getPA().closeAllWindows();
                                giveQuestCompletionRewards();
                                player.addQuestPoints();
                            }));
                } else {
                    player.start(getMairinDialogue()
                            .npc("Come back to me when you have defeated the guardian!"));
                }
            } else if (getStage() == 6) {
                player.start(getMairinDialogue()
                        .npc(DialogueExpression.HAPPY, "Thank you for your help, " + player.getDisplayName() + "!",
                                "You have proven yourself worthy of the Forgotten Prayers."));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemClick(int itemId) {
        switch (itemId) {


            case 25365:
                if (isQuestCompleted()) {
                    player.getDH().sendDialogues(7789, 7307);
                } else {
                    player.start(new DialogueBuilder(player).player("I must finish the Forgotten Curses Quest first."));
                }

            case 952:

                if (player.getItems().playerHasItem(RELIC_FRAGMENT_1)) {
                    player.sendMessage("@red@You already have the relic take it to Mairan!");
                    return false;
                }
                if (getStage() == 2) {
                    int digX = 3078;
                    int digY = 3806;


                    if (player.getPosition().getX() == digX && player.getPosition().getY() == digY || player.getPosition().getX() == digX + 1 && player.getPosition().getY() == digY || player.getPosition().getX() == digX && player.getPosition().getY() == digY + 1 || player.getPosition().getX() == digX + 1 && player.getPosition().getY() == digY + 1 || player.getPosition().getX() == digX - 1 && player.getPosition().getY() == digY || player.getPosition().getX() == digX && player.getPosition().getY() == digY - 1 || player.getPosition().getX() == digX - 1 && player.getPosition().getY() == digY - 1) {
                        player.startAnimation(830, 3);
                        player.getItems().addItem(RELIC_FRAGMENT_1, 1);
                        player.sendMessage("You dig and find an ancient relic");
                        player.start(new DialogueBuilder(player).player("I should take this to Mairin."));
                        FOUND_RELIC_1 = true;
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public void handleNpcKilled(NPC npc) {
        if (Boundary.WILDCURSEBEAST.in(player) && npc.getNpcId() == Npcs.CURSEBEAST) {
            if (getStage() == 5) {
                DEFEATED_GUARDIAN = true;
                player.start(new DialogueBuilder(player).player("I should tell Mairin i've slain the Guardian"));
            }
        }
    }

    @Override
    public boolean handleObjectClick(WorldObject object, int option) {
        switch (object.getId()) {
            case 31703:
                if (getStage() == 5 && !DEFEATED_GUARDIAN) {
                    NPCSpawning.spawnNpc(player, 9400, 3362, 3813, 0, 1, 55, true, true);
                } else {
                    player.sendMessage("@red@You find nothing of interest.");
                }
                return true;
            case 61:
                if (object.getX() == 3461) {
                    if (!isQuestCompleted()) {
                        player.start(new DialogueBuilder(player).player("I must finish the Forgotten Prayers Quest first."));
                    } else {
                        if (!player.usingcurseprayers) {
                            CombatPrayer.resetPrayers(player);
                            player.sendMessage("@pur@An ancient knowledge fills your mind...");
                            player.usingcurseprayers = true;
                            player.setSidebarInterface(5, 27674);
                            player.getPA().closeAllWindows();
                        } else {
                            player.sendMessage("You change to the regular prayer book.");
                            player.usingcurseprayers = false;
                            CombatPrayer.resetPrayers(player);
                            player.setSidebarInterface(5, 15608);
                            player.getPA().closeAllWindows();
                        }
                    }
                }
                break;
            case 14773:
                if (FOUND_RELIC_2) {
                    player.sendMessage("You find nothing of interest.");
                    return true;
                }
                if (getStage() == 3) {
                    player.getItems().addItem(RELIC_FRAGMENT_2, 1);
                    player.sendMessage("You find an ancient relic.");
                    FOUND_RELIC_2 = true;
                    return true;
                }
                return false;
        }

        return false;
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.getItems().addItem(2528, 10);
        player.getItems().addItem(25365, 1);
        player.getItems().addItem(6679, 2);
    }

    private DialogueBuilder getMairinDialogue() {
        DialogueBuilder builder = new DialogueBuilder(player);
        builder.setNpcId(8777); // TODO EDIT THIS TO THE CORRECT NPC ID
        return builder;
    }

    public WildCursesQuest(Player player) {
        super(player);
    }
}