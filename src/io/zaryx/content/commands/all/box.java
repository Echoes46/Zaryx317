package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

public class box  extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.getShops().openShop(600);
    }

    public Optional<String> getDescription() {
        return Optional.of("Welcome to the box shop.");
    }
}

