package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.content.worldevent.WorldEventInformation;
import io.zaryx.model.entity.player.Player;

public class events extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        WorldEventInformation.openInformationInterface(player);
    }
}
