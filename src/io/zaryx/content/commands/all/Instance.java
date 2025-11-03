package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class Instance extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        //player.getAoeInstanceHandler().open();
        player.sendMessage("New system coming soon! <3");
    }
}
