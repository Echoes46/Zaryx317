package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.discord.DiscordIntegration;

public class LinkDiscord extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (player == null) {
            return;
        }
        // Start the Discord linking flow
        DiscordIntegration.syncUser(player);
    }

    @Override
    public java.util.Optional<String> getDescription() {
        return java.util.Optional.of("Links your Discord account with your in-game account.");
    }

    @Override
    public String getFormat() {
        return "::linkdiscord";
    }

    @Override
    public java.util.Optional<String> getParameter() {
        return java.util.Optional.of("No parameters required");
    }
}
