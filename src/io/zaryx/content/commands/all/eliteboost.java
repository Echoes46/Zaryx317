package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

import java.util.concurrent.TimeUnit;

public class eliteboost extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.EliteCentCooldown < System.currentTimeMillis()) {
            player.EliteCentBoost = 6000;

            player.EliteCentCooldown = (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
        } else {
            player.sendMessage("This boost is on cooldown!");
        }
    }
}
