package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class hi extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split(" ");
        int id = Integer.parseInt(args[0]);
        player.headIcon = id;
        player.getPA().requestUpdates();
        player.sendMessage("HeadIcon: " + id);
    }
}
//						c.headIcon = PRAYER_HEAD_ICONS[i];
//						c.getPA().requestUpdates();