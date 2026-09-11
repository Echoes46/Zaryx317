package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import java.util.*;

/** Opens the Companion Journal, optionally searching by name or item ID. */
public class Pet extends Command {
    public static final int ROW_COUNT = 64;
    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.getInterfaceEvent().isActive() || io.zaryx.Server.getMultiplayerSessionListener().inAnySession(player)) {
            player.sendMessage("Please finish your current activity first.");
            return;
        }
        player.companionJournal.open(player, input);
    }

    public static List<String> wrapDetails(List<String> details) {
        List<String> lines = new ArrayList<>();
        for (String line : details) {
            while (line.length() > 67) {
                int split = line.lastIndexOf(' ', 67);
                if (split <= 0) split = 67;
                lines.add(line.substring(0, split));
                line = line.substring(split).trim();
            }
            lines.add(line);
        }
        return lines;
    }
}
