package io.zaryx.content.teamBounties;

import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueExpression;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.model.entity.player.Player;

public class BountyDialogue extends DialogueBuilder {

    private static final int NPC_ID = 6599;

    public BountyDialogue(Player player) {
        super(player);
        setNpcId(NPC_ID);

        player("Hello, I'm interested in bounties.");
        npc("What would you like to do?");
        option(
                new DialogueOption("Place a bounty", BountyDialogue::placeBounty),
                new DialogueOption("Track a bounty", BountyDialogue::trackBounty),
                new DialogueOption("Cancel my bounty", BountyDialogue::cancelBounty),
                new DialogueOption("View Active Bounties", BountyDialogue::viewActiveBounties),
                new DialogueOption("Nevermind.", plr -> plr.getPA().closeAllWindows())
        );
    }

    private static void placeBounty(Player player) {
        player.getPA().sendEnterString("Enter the name of the player you want to place a bounty on:", (plr, targetName) -> {
            if (BountySystem.hasActiveBounty(targetName)) {
                player.start(new DialogueBuilder(player)
                        .setNpcId(NPC_ID)
                        .npc("This player already has an active bounty.", "You cannot place another one.")
                        .exit(plr2 -> plr2.getPA().closeAllWindows()));
                return;
            }

//            Player targetPlayer = PlayerHandler.getPlayerByLoginName(targetName);
//            if (targetPlayer != null && player.getIpAddress().equals(targetPlayer.getIpAddress())) {
//                player.start(new DialogueBuilder(player)
//                        .setNpcId(NPC_ID)
//                        .npc("You cannot place a bounty on a player who shares the same IP address as you.")
//                        .exit(plr2 -> plr2.getPA().closeAllWindows()));
//                return;
//            }

            player.getPA().sendEnterAmount("Enter the bounty reward amount (minimum 5000 blood money):", (plr2, reward) -> {
                if (reward < 5000) {
                    player.start(new DialogueBuilder(player)
                            .setNpcId(NPC_ID)
                            .npc("The minimum bounty amount is 5000 blood money.")
                            .exit(plr3 -> plr3.getPA().closeAllWindows()));
                    return;
                }

                if (!player.getItems().playerHasItem(13307, reward)) {
                    player.start(new DialogueBuilder(player)
                            .setNpcId(NPC_ID)
                            .npc("You do not have enough blood money to place this bounty.")
                            .exit(plr3 -> plr3.getPA().closeAllWindows()));
                    return;
                }

                BountySystem.placeBounty(player, targetName, reward);
                player.start(new DialogueBuilder(player)
                        .setNpcId(NPC_ID)
                        .npc(DialogueExpression.HAPPY, "The bounty has been successfully placed on " + targetName + "!")
                        .statement("Good luck, and may the hunt begin!")
                        .exit(plr3 -> plr3.getPA().closeAllWindows()));
            });
        });
    }

    private static void trackBounty(Player player) {
        player.getPA().sendEnterString("Enter the name of the player whose bounty you want to track:", (plr, targetName) -> {
            boolean hasBounty = BountySystem.hasActiveBounty(targetName);
            if (hasBounty) {
                int reward = BountySystem.getActiveBountyReward(targetName);
                player.start(new DialogueBuilder(player)
                        .setNpcId(NPC_ID)
                        .npc("There is an active bounty on " + targetName + ""," with a reward of " + reward + " blood money.")
                        .exit(plr2 -> plr2.getPA().closeAllWindows()));
            } else {
                player.start(new DialogueBuilder(player)
                        .setNpcId(NPC_ID)
                        .npc("There is no active bounty on " + targetName + ".")
                        .exit(plr2 -> plr2.getPA().closeAllWindows()));
            }
        });
    }

    private static void cancelBounty(Player player) {
        if (!BountySystem.hasActiveBounty(player.getLoginName())) {
            player.start(new DialogueBuilder(player)
                    .setNpcId(NPC_ID)
                    .npc("You do not have any active bounty on you to cancel.")
                    .exit(plr -> plr.getPA().closeAllWindows()));
            return;
        }

        player.start(new DialogueBuilder(player)
                .setNpcId(NPC_ID)
                .npc("Are you sure you want to cancel the bounty on your head?")
                .option(new DialogueOption("Yes", plr -> BountySystem.cancelBounty(plr, plr.getLoginName())),
                        new DialogueOption("No", plr -> plr.getPA().closeAllWindows()))
                .send());
    }

    private static void viewActiveBounties(Player player) {
        BountyLeaderboardUtils.displayActiveBounties(player);
    }

    private static void viewMostClaimedBounties(Player player) {
        BountyLeaderboardUtils.displayMostClaimedBounties(player);
    }
}
