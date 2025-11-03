package io.zaryx.content.commands.test;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Right;

public class SetGameMode extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            player.getRights().reset();
            Right right = Right.valueOf(input.toUpperCase());
            player.setMode(right.getMode());
            player.getRights().setPrimary(right);
            player.sendMessage("Your mode is now " + right + ". Your other rights have been reset.");
        } catch (IllegalArgumentException e) {
            player.getPA().openQuestInterface("Mode not found: '" + input + "'", Arrays.stream(Right.values()).map(Right::toString).collect(Collectors.toList()));
        }
    }

    public Optional<String> getDescription() {
        return Optional.of("Set your game mode, use the command for a list.");
    }
}
