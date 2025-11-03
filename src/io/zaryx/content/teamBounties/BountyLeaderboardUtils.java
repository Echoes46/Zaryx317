package io.zaryx.content.teamBounties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.zaryx.model.entity.player.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BountyLeaderboardUtils {

    private static final String LEADERBOARD_DIR = "./bounties/leaderboards/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static List<BountyEntry> getActiveBounties() {
        List<BountyEntry> entries = new ArrayList<>();
        for (BountySystem.Bounty bounty : BountySystem.getActiveBounties()) {
            entries.add(new BountyEntry(
                    bounty.getPlacer(),
                    "Bounty on " + bounty.getTarget(),
                    bounty.getReward(),
                    LocalDateTime.now()
            ));
        }
        return entries;
    }

    public static List<ClaimedBountyEntry> getMostClaimedBounties() {
        List<ClaimedBountyEntry> entries = new ArrayList<>();
        File leaderboardFile = new File(LEADERBOARD_DIR + "most_claimed_bounties.json");

        if (leaderboardFile.exists()) {
            try {
                ClaimedBountyEntry[] savedEntries = objectMapper.readValue(leaderboardFile, ClaimedBountyEntry[].class);
                for (ClaimedBountyEntry entry : savedEntries) {
                    entries.add(entry);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entries;
    }

    public static void updateClaimedBountyLeaderboard(Player player, int rewardAmount) {
        List<ClaimedBountyEntry> entries = getMostClaimedBounties();
        ClaimedBountyEntry entry = entries.stream()
                .filter(e -> e.getLoginName().equalsIgnoreCase(player.getLoginName()))
                .findFirst()
                .orElse(null);

        if (entry != null) {
            entry.setTotalAmount(entry.getTotalAmount() + rewardAmount);
            entry.setTotalPlayersHunted(entry.getTotalPlayersHunted() + 1);
            entry.setTimestamp(LocalDateTime.now());
        } else {
            entries.add(new ClaimedBountyEntry(
                    player.getLoginName(),
                    player.getDisplayName(),
                    rewardAmount,
                    1,
                    LocalDateTime.now()
            ));
        }

        saveLeaderboard(entries);
    }

    private static void saveLeaderboard(List<ClaimedBountyEntry> entries) {
        File leaderboardFile = new File(LEADERBOARD_DIR + "most_claimed_bounties.json");
        try {
            objectMapper.writeValue(leaderboardFile, entries.toArray(new ClaimedBountyEntry[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void displayActiveBounties(Player player) {
        List<BountyEntry> activeBounties = getActiveBounties();
        player.getPA().showInterface(18820); // Assuming this is the interface ID for displaying leaderboards
        player.getPA().sendString(18821, "Active Bounties"); // Set the title

        int index = 0;
        for (BountyEntry entry : activeBounties) {
            player.getPA().sendString(18822 + index * 3, entry.getDisplayName());
            player.getPA().sendString(18823 + index * 3, String.valueOf(entry.getAmount()));
            index++;
        }
    }

    public static void displayMostClaimedBounties(Player player) {
        List<ClaimedBountyEntry> mostClaimedBounties = getMostClaimedBounties();
        player.getPA().showInterface(18820); // Assuming this is the interface ID for displaying leaderboards
        player.getPA().sendString(18821, "Most Claimed Bounties"); // Set the title

        int index = 0;
        for (ClaimedBountyEntry entry : mostClaimedBounties) {
            player.getPA().sendString(18822 + index * 3, entry.getDisplayName());
            player.getPA().sendString(18823 + index * 3, "Total Amount: " + entry.getTotalAmount());
            player.getPA().sendString(18824 + index * 3, "Players Hunted: " + entry.getTotalPlayersHunted());
            index++;
        }
    }
}
