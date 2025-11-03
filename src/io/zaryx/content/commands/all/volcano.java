package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.content.wilderness.ActiveVolcano;
import io.zaryx.content.wildwarning.WildWarning;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

public class volcano extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (!ActiveVolcano.progress) {
            player.sendMessage("There's no issues with the volcano right now.");
            return;
        }

        WildWarning.sendWildWarning(player, p -> {
            p.getPA().movePlayer(3367,3929,0);
        });

    }
    @Override
    public Optional<String> getDescription() {
        return Optional.of("Teleport's to you the Volcano in the wilderness.");
    }
}
