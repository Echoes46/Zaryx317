package io.zaryx.content.commands.all;

import java.util.Optional;

import io.zaryx.content.commands.Command;
import io.zaryx.content.vote_panel.VotePanelInterface;
import io.zaryx.model.entity.player.Player;


public class Vpanel extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        VotePanelInterface.openInterface(c, true);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Opens your vote panel.");
    }

}
