package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

public class afkstore  extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.getShops().openShop(195);
    }

    public Optional<String> getDescription() {
        return Optional.of("Opens the afk store.");
    }
}
