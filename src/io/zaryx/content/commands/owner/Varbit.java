package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;


public class Varbit extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split("-");
        try {
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }
        int varbit = Integer.parseInt(args[0]);
        int state = Integer.parseInt(args[1]);
        player.getPA().sendConfig(varbit, state);
        } catch (Exception e) {
            player.sendMessage("Error. Correct syntax: ::varbit-id-state");
        }

    }
}
