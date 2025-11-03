package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class comp extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.getCompletionistCapeRe().sendColours();
        player.getPA().showInterface(59960);
    }
}
