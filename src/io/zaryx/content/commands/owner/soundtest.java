package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.SoundType;
import io.zaryx.model.entity.player.Player;

public class soundtest extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split(" ");
        player.getPA().sendSound(Integer.parseInt(args[0]), SoundType.SOUND, null);

    }
}
