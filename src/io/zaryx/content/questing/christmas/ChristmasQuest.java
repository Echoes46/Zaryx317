package io.zaryx.content.questing.christmas;

import com.google.common.collect.Lists;
import io.zaryx.content.questing.Quest;
import io.zaryx.model.SkillLevel;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

import java.util.List;

public class ChristmasQuest extends Quest {

    public boolean Christmas = false;
    public ChristmasQuest(Player player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Santa's Troubles";
    }

    @Override
    public List<SkillLevel> getStartRequirements() {
        return Lists.newArrayList();
    }

    @Override
    public List<String> getJournalText(int stage) {
        List<String> lines = Lists.newArrayList();
        switch (stage) {

            case 0:
            if (Christmas) {
                    lines.add("To start this quest go to snowy located west of home,");
                    lines.add("Snowy will teleport you to the North Pole.");
                    lines.add("There I must speak to santa to begin the quest!");
                    lines.add("");
                    lines.add("Rewards:");
                    lines.add("2 Christmas Boxes");
                    lines.add("10m coins");
                    lines.add("2m foundry");
                    lines.add("200 seasonal points");
                    break;
            } else {
                    lines.add("Looks like you've missed the Christmas Quest this year!");
                    lines.add("Stick around it will return 1st December.");
                    break;
            }
            case 1:
                lines.add("I should head to the home mining area like santa mentioned,");
                lines.add("he said something about mining and finding the missing gifts.");
                break;
            case 2:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 3:
                lines.add("I should head to the home woodcutting area like santa mentioned");
                lines.add("he said something about magic tree's,");
                lines.add("maybe I can find the missing gifts there.");
                break;
            case 4:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 5:
                lines.add("I should head to the hunter area like santa mentioned,");
                lines.add("he said one of his elf's spotted some present's there.");
                break;
            case 6:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 7:
                lines.add("I should head to the farming patches like santa mentioned,");
                lines.add("he said one of his elf's spotted some present's there.");
                break;
            case 8:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 9:
                lines.add("I should go speak with Pong, Santa said he lived,");
                lines.add("North-east of Rellekka, hopefully, he has the present's.");
                break;
            case 10:
                lines.add("Pong told me he heard that the giant mole");
                lines.add(" has some of the christmas presents");
                lines.add("I can get there from the teleport menu.");
                break;
            case 11:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 12:
                lines.add("Santa mentioned speaking to Boar31337killer who's located,");
                lines.add("outside the front of Lumbridge castle.");
                break;
            case 13:
                lines.add("Boar31337killer told me he spotted presents in the cow field,");
                lines.add("I should head there an investigate.");
                break;
            case 14:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 15:
                lines.add("Santa mentioned I need to speak with jack,");
                lines.add("as the present's where stolen by anti-santa and his evil snowman.");
                break;
            case 16:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 17:
                lines.add("I have completed this quest!");
                lines.add("I obtained the following rewards:");

                lines.add("2 Christmas Boxes");
                lines.add("10m coins");
                lines.add("2m foundry");
                lines.add("200 seasonal points");
                break;
        }
        return lines;
    }

    @Override
    public int getCompletionStage() {
        return 17;
    }

    @Override
    public int getRequiredQuestPoints() {
        return 0;
    }

    @Override
    public List<String> getCompletedRewardsList() {
        return Lists.newArrayList("2 Christmas Boxes", "10m coins",
                "2m foundry", "200 seasonal points");
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.setSeasonalPoints(player.getSeasonalPoints() + 200);
        player.foundryPoints += 2_000_000;
        player.getItems().addItemUnderAnyCircumstance(995, 10_000_000);
        player.getItems().addItemUnderAnyCircumstance(12161, 2);
    }

    @Override
    public boolean handleNpcClick(NPC npc, int option) {
        return false;
    }

    @Override
    public void handleNpcKilled(NPC npc) {
        //kill giant mole
        if (getStage() == 10 && npc.getNpcId() == 5779) {
            if (Misc.isLucky(65)) {
                player.setPresentCounter(player.getPresentCounter() + 1);
                player.sendMessage("You've found a present you now have " + player.getPresentCounter() + " present's saved.");
            }

            if (player.getPresentCounter() == 125) {
                player.sendMessage("@red@You've now collected all the present's from the Giant Mole!");
                incrementStage();
            }
        }

        //kill cows
        if (getStage() == 13 && npc.getName().equalsIgnoreCase("cow") || getStage() == 13 && npc.getNpcId() == 1594) {
            if (Misc.isLucky(100)) {
                player.setPresentCounter(player.getPresentCounter() + 1);
                player.sendMessage("You've found a present you now have " + player.getPresentCounter() + " present's saved.");
            }

            if (player.getPresentCounter() == 150) {
                player.sendMessage("@red@You've now collected all the present's from the Cows!");
                incrementStage();
            }
        }

        //kill evil snowman
        if (getStage() == 15 && npc.getNpcId() == 2316) {
            player.setPresentCounter(player.getPresentCounter() + 100);
            player.sendMessage("You found the all the missing present's from last year!");
            player.sendMessage("You now have " + player.getPresentCounter() + " present's saved.");
            player.sendMessage("You need to return back to santa!");
            incrementStage();
        }
    }

}
