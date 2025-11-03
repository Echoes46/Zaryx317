package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.content.fusion.FusionTypes;
import io.zaryx.model.entity.player.Player;

public class Fuse extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {

        player.getFusionSystem().openInterface(FusionTypes.WEAPON);

    }
}
