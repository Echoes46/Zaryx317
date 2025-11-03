package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class curses extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.setSidebarInterface(5, 27674);
    } //24633 old value
}
