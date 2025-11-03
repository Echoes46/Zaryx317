package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.offlinestorage.ItemCollection;

public class Viewrewards extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
            try {
                String[] args = input.split("-");
                String playerName = args[0];
                ItemCollection.adminView(player, playerName);
            } catch (Exception e) {
                player.sendMessage("Error. Correct syntax: ::viewrewards-playername");
            }
    }
}
