package io.zaryx.content.commands.test;

import java.io.IOException;
import java.util.Optional;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class EquipmentSetup extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            io.zaryx.model.EquipmentSetup.equip(player, input);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage("Could not equip equipment setup.");
        }
    }

    public Optional<String> getDescription() {
        return Optional.of("Spawn a specific equipment setup.");
    }
}
