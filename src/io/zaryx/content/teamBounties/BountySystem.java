package io.zaryx.content.teamBounties;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.broadcasts.Broadcast;
import io.zaryx.util.Misc;
import io.zaryx.model.entity.player.PlayerHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BountySystem {

    private static final String BOUNTY_DIR = "./bounties/active/";
    private static final String CLAIMED_DIR = "./bounties/claimed/";
    private static final String CANCELLED_DIR = "./bounties/cancelled/";
    private static final Map<String, Bounty> activeBounties = new HashMap<>();
    static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        loadActiveBounties();
    }

    public static synchronized void placeBounty(Player player, String targetName, int reward) {
        if (player == null || targetName == null || targetName.isEmpty()) {
            player.sendMessage("Invalid target name. Please enter a valid player name.");
            return;
        }
        if (player.getLoginName().equalsIgnoreCase(targetName)) {
            player.sendMessage("You cannot place a bounty on yourself.");
            return;
        }

        if (hasActiveBounty(targetName)) {
            player.sendMessage("This player already has an active bounty. You cannot place another one.");
            return;
        }

        if (!player.getItems().playerHasItem(13307, reward)) {
            player.sendMessage("You do not have enough blood money to place this bounty.");
            return;
        }

        player.getItems().deleteItem(13307, reward);
        Bounty bounty = new Bounty(player.getLoginName(), targetName, reward);
        activeBounties.put(targetName.toLowerCase(), bounty);

        saveBountyToFile(bounty);

        player.sendMessage("Bounty placed on " + targetName + " with a reward of " + reward + " blood money.");

        notifyTargetOfBounty(player, targetName, reward);
        broadcastBountyPlacement(player, targetName, reward);
    }

    private static void broadcastBountyPlacement(Player placer, String targetName, int reward) {
        String broadcastMessage = "<img=95> " + placer.getLoginName() + " has placed a bounty of " + reward + " blood money on " + targetName + "!";
        Broadcast broadcast = new Broadcast(broadcastMessage);
        broadcast.submit();
    }

    public static synchronized boolean claimBounty(Player player, String targetName) {
        Bounty bounty = activeBounties.get(targetName.toLowerCase());

        if (bounty != null) {
            int reward = bounty.getReward();
            int rewardAfterTax = reward - (reward * 15 / 100);

            player.getItems().addItem(13307, rewardAfterTax);
            player.getItems().addItem(13306, 1);
            player.getItems().addItem(995, Misc.random(1_000_000, 10_000_000));

            player.sendMessage("You have claimed the bounty on " + targetName + " and received " + rewardAfterTax + " blood money after tax.");

            moveBountyToDirectory(targetName.toLowerCase(), CLAIMED_DIR);
            BountyLeaderboardUtils.updateClaimedBountyLeaderboard(player, rewardAfterTax);
            broadcastBountyClaim(player, targetName, rewardAfterTax);
            return true;
        } else {
            player.sendMessage("No active bounty found on " + targetName + ".");
            return false;
        }
    }

    public static synchronized void cancelBounty(Player player, String targetName) {
        Bounty bounty = activeBounties.get(targetName.toLowerCase());

        if (bounty != null) {
            int rewardAmount = bounty.getReward();
            int cancellationCost = rewardAmount + (rewardAmount * 25 / 100);

            if (!player.getItems().playerHasItem(13307, cancellationCost)) {
                player.start(new DialogueBuilder(player)
                        .setNpcId(4671) // Adjust NPC ID as needed
                        .npc("You do not have enough blood money to cancel the bounty.")
                        .statement("You need " + cancellationCost + " blood money to cancel the bounty.")
                        .exit(plr -> plr.getPA().closeAllWindows())
                        .send());
                return;
            }

            player.getItems().deleteItem(13307, cancellationCost);
            moveBountyToDirectory(targetName.toLowerCase(), CANCELLED_DIR);

            player.start(new DialogueBuilder(player)
                    .setNpcId(6599) // Adjust NPC ID as needed
                    .npc("The bounty on your head has been successfully canceled.")
                    .statement("You paid " + cancellationCost + " blood money to cancel the bounty.")
                    .exit(plr -> plr.getPA().closeAllWindows())
                    .send());
        } else {
            player.start(new DialogueBuilder(player)
                    .setNpcId(4671) // Adjust NPC ID as needed
                    .npc("You do not have any active bounty on you to cancel.")
                    .exit(plr -> plr.getPA().closeAllWindows())
                    .send());
        }
    }

    private static synchronized void moveBountyToDirectory(String targetName, String directory) {
        Bounty bounty = activeBounties.remove(targetName);
        if (bounty != null) {
            File bountyFile = new File(BOUNTY_DIR + targetName + ".json");
            File targetFile = new File(directory + targetName + ".json");

            try {
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }
                Files.move(bountyFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.deleteIfExists(bountyFile.toPath()); // Delete the original file from the active folder
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized Collection<Bounty> getActiveBounties() {
        return activeBounties.values();
    }

    public static synchronized void broadcastBountyClaim(Player claimer, String targetName, int reward) {
        String broadcastMessage = "<img=95> " + claimer.getLoginName() + " has claimed the bounty on " + targetName + " for " + reward + " blood money!";
        Broadcast broadcast = new Broadcast(broadcastMessage);
        broadcast.submit();
    }

    private static synchronized void saveBountyToFile(Bounty bounty) {
        try {
            File dir = new File(BOUNTY_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            objectMapper.writeValue(new File(BOUNTY_DIR + bounty.getTarget().toLowerCase() + ".json"), bounty);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void notifyTargetOfBounty(Player placer, String targetName, int reward) {
        Player targetPlayer = PlayerHandler.getPlayerByLoginName(targetName);
        if (targetPlayer != null) {
            targetPlayer.start(new DialogueBuilder(targetPlayer)
                    .setNpcId(6599)
                    .npc("A bounty has been placed on you by " + placer.getLoginName() + ".", "The reward is " + reward + " blood money.")
                    .exit(plr -> plr.getPA().closeAllWindows()));
        }
    }

    public static synchronized boolean hasActiveBounty(String targetName) {
        return activeBounties.containsKey(targetName.toLowerCase());
    }

    public static synchronized int getActiveBountyReward(String targetName) {
        Bounty bounty = activeBounties.get(targetName.toLowerCase());
        return bounty != null ? bounty.getReward() : 0;
    }

    public static void loadActiveBounties() {
        File dir = new File(BOUNTY_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("No active bounties directory found.");
            return;
        }

        File[] bountyFiles = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (bountyFiles == null || bountyFiles.length == 0) {
            System.out.println("No bounty files found.");
            return;
        }

        System.out.println("Files found:");
        for (File file : bountyFiles) {
            System.out.println(file.getName());
        }

        for (File bountyFile : bountyFiles) {
            try {
                Bounty bounty = objectMapper.readValue(bountyFile, Bounty.class);
                if (bounty != null) {
                    activeBounties.put(bounty.getTarget().toLowerCase(), bounty);
                    System.out.println("Loaded bounty for target: " + bounty.getTarget());
                } else {
                    System.out.println("Failed to load bounty from file: " + bountyFile.getName());
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + bountyFile.getName());
                e.printStackTrace();
            }
        }

        System.out.println("Loaded " + activeBounties.size() + " active bounties.");
    }

    public static class Bounty {
        private String placer;
        private String target;
        private int reward;

        // Default constructor required for Jackson deserialization
        public Bounty() {
        }

        public Bounty(String placer, String target, int reward) {
            this.placer = placer;
            this.target = target;
            this.reward = reward;
        }

        public String getPlacer() {
            return placer;
        }

        public String getTarget() {
            return target;
        }

        public int getReward() {
            return reward;
        }
    }
}
