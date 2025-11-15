package io.zaryx.content.commands.all;

import java.util.Optional;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;

public class Dailyreward extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (!Boundary.HOME.in(player)) { //Switched from EDGEVILLE_PARAMATER
            player.sendMessage("You must be in the Home area to use this command.");
        } else {
            player.getDailyRewards().openInterface();
        }
    }

    public Optional<String> getDescription() {
        return Optional.of("Opens the daily reward interface (only in Home area).");
    }
}
