package io.zaryx.content.commands.admin;

import io.zaryx.content.activityboss.Groot;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class gboss extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Groot.spawnGroot();
    }
}
