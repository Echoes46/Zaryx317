package io.zaryx.content.commands.admin;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.content.polls.PollTab;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.broadcasts.Broadcast;

public class Reloaddrops extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            Server.getDropManager().read();
            player.sendMessage("@blu@Reloaded Drops.");
        } catch (Exception e) {
            player.sendMessage("@red@Error reloading drops!");
            e.printStackTrace();
        }
    }
}
