package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class sp extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getPA().showInterface(43214);
        player.sendMessage("You open the spawning menu...");
    }

}