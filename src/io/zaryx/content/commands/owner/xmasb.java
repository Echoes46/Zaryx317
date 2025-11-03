package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.content.seasons.ChristmasBoss;
import io.zaryx.model.entity.player.Player;


public class xmasb extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        ChristmasBoss.initBoss();
    }
}
