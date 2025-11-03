package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.offlinestorage.ItemCollection;

public class storedrewards extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        ItemCollection.adminViewAll(player);
    }
}
