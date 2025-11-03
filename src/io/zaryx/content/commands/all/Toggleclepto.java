package io.zaryx.content.commands.all;

import java.util.Optional;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

/**
 * Toggles whether a warning will be shown when attempting to drop an item on the ground.
 *
 * @author Emiel
 *
 */
public class Toggleclepto extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        c.setCleptoWarning(!c.isCleptoWarning());
        c.sendMessage("You will {} @bla@be notified of clepto drops in chat", c.isCleptoWarning() ? "@gre@now" : "@red@not@bla@");
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Toggles the item drop warning on or off");
    }

}
