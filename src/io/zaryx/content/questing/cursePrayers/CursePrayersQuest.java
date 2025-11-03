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

public class CursePrayersQuest extends Quest {

    private static final int ANCIENT_SYMBOL = 11181;
    private static final int ANCIENT_BOOK = 13513;
    private static boolean GOTSYMBOL = false;
    private static boolean GOTSKULL = false;
    private static boolean SLAINBEAST = false;


    @Override
    public String getName() {
        return "Ancient Curses";
    }

    @Override
    public List<SkillLevel> getStartRequirements() {
        return List.of(
                new SkillLevel(Skill.PRAYER, 70),
                new SkillLevel(Skill.STRENGTH, 70),
                new SkillLevel(Skill.SLAYER, 70));
    }



    @Override
    public List<String> getJournalText(int stage) {
        List<String> journalText = Lists.newArrayList();
        switch (stage) {
            case 0:
                journalText.add("There is a Wizard known as Surok Magis who may");
                journalText.add("have unlocked the secrets of the Ancient Curses.");
                journalText.add("he was last seen in the Kourend Catacombs.");
                journalText.add("");
                journalText.add("Rewards:");
                journalText.add("Access to Ancient Curses.");
                journalText.add("5x Exp Lamps");
                journalText.addAll(getStartRequirementLines());
                journalText.add("25m Gold Coins");
                break;
            case 1:
                journalText.add("You have found Surok Magis in Catacombs");
                journalText.add("He has agreed to teach you the Ancient Curses");
                journalText.add("but you must first prove yourself to him.");
                journalText.add("He's asked for you to come back when ready");
                break;
            case 2:
                journalText.add("He's given you a strange riddle");
                journalText.add("");
                journalText.add("In a town where wizards dwell,");
                journalText.add("Seek the place where books do tell,");
                journalText.add("Of secrets hidden in the dark,");
                journalText.add("Beneath the candles gentle spark.");
                break;
            case 3:
                journalText.add("A temple within the Wilderness");
                journalText.add("If youre lucky you keep what you give");
                journalText.add("Behind it lays the skull, dig..?");
                break;
            case 4:
                journalText.add("Surok has asked you to slay the beast");
                journalText.add("he told me he's somewhere in the wilderness");
                journalText.add("Defended by a sole black knight");
                journalText.add("a chest is the key to ending this madness.");
                journalText.add("The last clue was.. Headles close?");
                break;
            case 5:
                journalText.add("The Beast was defeated & balance has been restored");
                journalText.add("I have been rewarded with");
                journalText.add("Surok has given you an Ancient Prayer Scroll");
                journalText.add("5x Experience Lamps");
                break;
        }
        return journalText;
    }

    @Override
    public int getCompletionStage() {
        return 5;
    }

    @Override
    public int getRequiredQuestPoints() {
        return 3;
    }

    @Override
    public List<String> getCompletedRewardsList() {
        return Lists.newArrayList("Access to Ancient Curses.", "5x Experience Lamps");
    }

    @Override
    public boolean handleNpcClick(NPC npc, int option) {
        if (npc.getNpcId() == Npcs.SUROK_MAGIS) {
            if (getStage() == 0 && !canStartQuest()) {
                player.start(getSurokMagisDialogue()
                        .npc(DialogueExpression.ANGER_1, "You must have at lease 3 quest points to start this quest!"));
            }
            if (getStage() == 0) {
                if (!canStartQuest()) {
                    player.start(getSurokMagisDialogue()
                            .npc(DialogueExpression.ANGER_1, "You must have at lease 3 quest points to start this quest!"));
                    return false;
                }
                if (!player.getItems().playerHasItem(995, 25000000)) {
                    player.start(getSurokMagisDialogue()
                            .npc(DialogueExpression.ANGER_1, "Come talk to me when you have 25 million gold coins!"));
                    return false;
                } else {
                    player.getItems().deleteItem(995, 25000000);
                    player.start(getSurokMagisDialogue()
                            .player("Hello, are you Surok Magis?.")
                            .npc(DialogueExpression.DISTRESSED, "The great Surok Magis but who is asking?")
                            .player("I am " + player.getDisplayName() + " and I am wanting to learn the secrets,", "of the Ancient Curses and their powers.")
                            .npc(DialogueExpression.PLAIN_EVIL, "The Ancient Curses are not for the faint of heart.")
                            .npc(DialogueExpression.LAUGH_2, "It took me nearly two decadees to master them.", "What makes you think you're capable enough?")
                            .player(DialogueExpression.ANNOYED, "Well i'am strong & have the required levels and,", "I am willing to do what it takes!")
                            .npc(DialogueExpression.ANNOYED, "Hmm, I've had many adventurers come and try, ", "but none have been able to defeat the beast.")
                            .npc(DialogueExpression.HAPPY, "I wonder if it'll be different this time, ", "Very well i'll teach you how to get them!")
                            .player(DialogueExpression.HAPPY, "Thank You!")
                            .npc(DialogueExpression.CALM, "Come back and talk to me when you're ready.")
                            .exit(player -> {
                                incrementStage();
                            }));
                }
            } else if (getStage() == 1) {
                player.start(getSurokMagisDialogue()
                        .npc("Let's see if you're worthy!")
                        .npc("In a town where wizards dwell, ")
                        .npc("Seek the place where books do tell,")
                        .npc("Of secrets hidden in the dark, ")
                        .npc("Between the books of old.")
                        .exit(player -> {
                            incrementStage();

                        })
                );
            } else if (getStage() == 2) {
                if (GOTSYMBOL) {
                    player.getItems().deleteItem(ANCIENT_SYMBOL, 1);
                    player.getItems().deleteItem(ANCIENT_BOOK, 1);
                    player.start(getSurokMagisDialogue()
                            .npc(DialogueExpression.HAPPY, "You've found the Ancient Symbol and the Ancient Book.")
                            .npc("You're one step closer to unlocking the Ancient Curses.", "Here is the next riddle!")
                            .npc("The Temple in the Wilderness,")
                            .npc("Long ago it was a blessed refuge,")
                            .npc("Now only darkness and evil remains,")
                            .npc("Go there and dig! Find the skull of the blessed!")
                            .exit(player -> {
                                incrementStage();

                            }));
                } else {
                    player.start(getSurokMagisDialogue()
                            .npc("Have you Solved my riddle?")
                            .player("Not yet can you help me out?")
                            .npc("No!!")
                            .player("Chill dude, I'll be back when i've solved it"));
                }
            } else if (getStage() == 3) {
                if (GOTSKULL) {
                    player.getItems().deleteItem(13195, 1);
                    player.start(getSurokMagisDialogue()
                            .npc(DialogueExpression.HAPPY, "You've found the skull of the blessed!")
                            .npc(DialogueExpression.HAPPY, "This is the skull of ErDhartd the great one",
                                    "He was known for mastering the curse prayers, ", "We will use his skull to extract the knowledge",
                                    "of curses from his essence.")
                            .player(DialogueExpression.DISTRESSED, "Woah sounds kinda crazy")
                            .npc(DialogueExpression.PLAIN_EVIL, "This is Genius not Crazy!", "One last thing to do, you must defeat",
                                    "@red@the beast")
                            .npc(DialogueExpression.DISTRESSED, "He's somewhere in the wilderness, ", "Go and find him and slay him!")
                            .exit(player -> {
                                incrementStage();
                            }));
                } else {
                    player.start(getSurokMagisDialogue()
                            .npc(DialogueExpression.ANGER_1, "Can't you see im busy!"));
                }
            } else if (getStage() == 4) {
                if (SLAINBEAST) {
                    player.start(getSurokMagisDialogue()
                            .npc(DialogueExpression.HAPPY, "You've slain the beast!")
                            .npc("You've proven yourself worthy of the Ancient Curses.")
                            .npc("I will now teach you the secrets of the Ancient Curses.")
                            .exit(player -> {
                                incrementStage();
                                player.getPA().closeAllWindows();
                                giveQuestCompletionRewards();
                                player.addQuestPoints();
                            }));
                } else {
                    player.start(getSurokMagisDialogue()
                            .npc("Come Back to me when you've slain the Beast!"));
                }
            } else if (getStage() == 5) {
                player.start(getSurokMagisDialogue()
                        .npc(DialogueExpression.HAPPY, "Thank you for all your help " + player.getDisplayName() + "!",
                                "I was wrong about you!"));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemClick(int itemId) {
        switch (itemId) {
            case 719:
                if (isQuestCompleted()) {
                    player.getDH().sendDialogues(7789, 7307);
                } else {
                    player.start(new DialogueBuilder(player).player("I must finish the Ancient Curses Quest first."));
                }
            case 952:

                if (player.getItems().playerHasItem(13195)) {
                    player.sendMessage("@red@You already have the skull, take it to surok!");
                    return false;
                }
                if (getStage() == 3) {
                    int digX = 2946;
                    int digY = 3819;
                    if (player.getPosition().getX() == digX && player.getPosition().getY() == digY) {
                        player.startAnimation(830, 3);
                        player.getItems().addItem(13195, 1);
                        player.sendMessage("You dig and find a skull");
                        GOTSKULL = true;
                        player.start(new DialogueBuilder(player).player("I should take this to Surok"));
                        return true;
                    }
                } else {
                    player.sendMessage("@red@You can't dig here");
                }

        }
        return false;
    }

    @Override
    public void handleNpcKilled(NPC npc) {
        if (Boundary.CURSEBEAST.in(player) && npc.getNpcId() == Npcs.CURSEBEAST) {
            if (getStage() == 4) {
                SLAINBEAST = true;
                player.start(new DialogueBuilder(player).player("I should tell Surok i've slain the beast"));
            }
        }
    }

    @Override
    public boolean handleObjectClick(WorldObject object, int option) {
        switch (object.getId()) {
            case 27275:
                if (getStage() == 4 && !SLAINBEAST) {
                    NPCSpawning.spawnNpc(player, 9400, 3311, 3773, 0, 1, 55, true, true);
                    return true;
                } else {
                    player.sendMessage("@red@You find nothing of interest.");
                    return true;
                }
            case 32630:
                if (object.getX() == 3087) {
                    if (!isQuestCompleted()) {
                        player.start(new DialogueBuilder(player).player("I must finish the Ancient Curses quest first."));
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
            case 12539:
                if (GOTSYMBOL) {
                    player.sendMessage("You find nothing of interest.");
                    return true;
                }
                if (getStage() == 2) {
                    player.getItems().addItem(ANCIENT_SYMBOL, 1);
                    player.getItems().addItem(ANCIENT_BOOK, 1);
                    player.sendMessage("You find some sort of symbol and a book in the shelf");
                    GOTSYMBOL = true;
                    return true;
                }
                return false;
        }

        return false;
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.getItems().addItem(719, 1);
        player.getItems().addItem(2528, 5);
        player.getItems().addItem(6679, 2);
    }

    private DialogueBuilder getSurokMagisDialogue() {
        DialogueBuilder builder = new DialogueBuilder(player);
        builder.setNpcId(Npcs.SUROK_MAGIS);
        return builder;
    }

    public CursePrayersQuest(Player player  ) {
        super(player);
    }
}
