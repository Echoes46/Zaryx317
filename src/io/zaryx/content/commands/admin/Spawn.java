package io.zaryx.content.commands.admin;

import java.util.Optional;

import io.zaryx.content.ItemSpawner;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class Spawn extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        ItemSpawner.open(player);
    }

    public Optional<String> getDescription() {
        return Optional.of("Opens an interface to spawn items.");
    }
}
