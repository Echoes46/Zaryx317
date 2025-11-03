package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.content.questing.QuestInterfaceV2;
import io.zaryx.model.entity.player.Player;

public class qzz extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        QuestInterfaceV2.openInterface(player);
    }
}
