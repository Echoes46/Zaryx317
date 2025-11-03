package io.zaryx.content.commands.owner;

import io.zaryx.content.battlepass.Pass;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

import java.util.Optional;

public class addbpxp extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split("-");
        String playerName = args[0];
        Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);
        if (optionalPlayer.isPresent()) {
            Player p = optionalPlayer.get();
            Pass.addExperience(p, Integer.parseInt(args[1]));
        } else {
            player.sendMessage(playerName + " is not online.");
        }
    }
}
