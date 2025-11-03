package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.content.games.blackjack.BJManager;
import io.zaryx.model.entity.player.Player;


public class Bjc extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.queue(() -> {
            player.setBjManager(new BJManager(player));
            player.getBjManager().open();
        });//just to test
    }
}
