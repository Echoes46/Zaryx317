package io.zaryx.content.commands.moderator;

import io.zaryx.content.bosses.Durial321;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class Durial extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (Durial321.spawned || Durial321.alive) {
            c.sendMessage("You cannot execute this more than once!");
            return;
        }

        Durial321.init();
    }
}
