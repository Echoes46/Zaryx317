package io.zaryx.content.teamBounties;

import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;

import java.util.List;

public class BountyLeaderInterface {
    private static final int INTERFACE_ID = 18820;

    private static final int LEADERBOARD_TYPE_SELECT_BUTTON_START = 18828;
    private static final int LEADERBOARD_TYPE_SELECT_BUTTON_END = 18866;
    private static final int LEADERBOARD_TYPE_SELECT_TEXT_START = 18829;

    private static final int NAME_START_ID = 18870;
    private static final int COUNT_START_ID = 18871;
    private static final int UNUSED_TEXT_START_ID = 18872; // Placeholder for unused texts

    public static void display(Player player, List<BountyEntry> entries, String title) {
        // Set the title of the leaderboard
        player.getPA().sendString(INTERFACE_ID, title);

        // Display the leaderboard entries
        int index = 0;
        for (BountyEntry entry : entries) {
            player.getPA().sendString(NAME_START_ID + (index * 4), entry.getLoginName());
            player.getPA().sendString(COUNT_START_ID + (index * 4), Misc.insertCommas("" + entry.getAmount()));
            index++;
        }

        // Clear any remaining unused text lines on the interface
        for (; index < 10; index++) {
            player.getPA().sendString(NAME_START_ID + (index * 4), "");
            player.getPA().sendString(COUNT_START_ID + (index * 4), "");
        }

        // Hide unused text sections
        for (int i = UNUSED_TEXT_START_ID; i < UNUSED_TEXT_START_ID + 20; i++) {
            player.getPA().sendString(i, "");
        }

        // Show the interface
        player.getPA().showInterface(INTERFACE_ID);
    }
}
