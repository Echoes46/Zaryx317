package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.content.questing.QuestInterfaceV2;
import io.zaryx.model.entity.player.Player;

public class quest extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        QuestInterfaceV2.openInterface(player);
        player.getQuesting().handleHelpTabActionButton(666);
    }
}
