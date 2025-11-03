package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.content.seasons.Christmas;
import io.zaryx.model.entity.player.Player;

public class xmas extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        if (!Christmas.isChristmas()) {
            c.sendMessage("It's not christmas time, you can't access the area");
            return;
        }
        if (c.inTrade || c.inDuel || c.getPosition().inWild()) {
            return;
        }
        if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
            c.sendMessage("@cr10@This player is currently at the pk district.");
            return;
        }
        c.getPA().startTeleport(2916, 3927,0,"foundry", false);
    }
}
