package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.content.deals.AccountBoosts;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

public class deals  extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        AccountBoosts.openInterface(c);
    }

    public Optional<String> getDescription() { return Optional.of("Welcome to Zaryx Deals."); }
}
