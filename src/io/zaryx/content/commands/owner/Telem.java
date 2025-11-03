package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.content.teleportv2.inter.TeleportInterface;
import io.zaryx.model.entity.player.Player;

public class Telem extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        TeleportInterface.open(player);
    }
}