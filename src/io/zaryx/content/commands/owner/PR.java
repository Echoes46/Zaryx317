package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.content.games.PartyRoom;
import io.zaryx.model.entity.player.Player;

public class PR extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        new PartyRoom().run();
    }
}