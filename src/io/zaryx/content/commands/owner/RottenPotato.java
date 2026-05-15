package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

/**
 * Gives the player a Rotten Potato (item 5733).
 * Usage: ::rottenpotato
 */
public class RottenPotato extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (!c.getItems().playerHasItem(5733)) {
            c.getItems().addItem(5733, 1);
            c.sendMessage("You receive a Rotten Potato.");
        } else {
            c.sendMessage("You already have a Rotten Potato.");
        }
    }
}
