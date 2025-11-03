package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.content.skills.summoning.Summoning;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

public class UnSummon extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Summoning.dismissFamiliar(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("De-Summon your familiar.");
    }
}
