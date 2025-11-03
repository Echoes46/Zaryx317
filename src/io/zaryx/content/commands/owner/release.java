package io.zaryx.content.commands.owner;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.discord.Discord;

public class release extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Server.ServerLocked = !Server.ServerLocked;
        player.sendMessage("@red@The server is now " + (!Server.ServerLocked ? "unlocked" : "locked") + "!");

        if (!Server.ServerLocked) {
            Discord.writeIngameEvents("```Server is now online. @everyone```");
            Discord.writeIngameEvents("```Server is now online. @everyone```");
        }
    }
}
