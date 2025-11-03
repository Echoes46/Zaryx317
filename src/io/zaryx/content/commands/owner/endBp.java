package io.zaryx.content.commands.owner;

import io.zaryx.content.battlepass.Pass;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class endBp extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        Pass.setSeasonEnded(true);
        player.sendMessage("Battlepass has been ended.");
    }
}