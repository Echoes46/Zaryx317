package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.content.playerinformation.Interface;
import io.zaryx.model.entity.player.Player;

public class PlayerInfo extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Interface.Open(player);
    }
}
