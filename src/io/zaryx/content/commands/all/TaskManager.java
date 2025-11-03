package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class TaskManager extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getTaskMaster().showInterface();
        player.getQuesting().handleHelpTabActionButton(667);
    }
}
