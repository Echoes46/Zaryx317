package io.zaryx.content.commands.admin;

import io.zaryx.content.commands.Command;
import io.zaryx.content.worldevent.WorldEventContainer;
import io.zaryx.content.worldevent.impl.HesporiWorldEvent;
import io.zaryx.model.entity.player.Player;

public class Starthespori extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        WorldEventContainer.getInstance().startEvent(new HesporiWorldEvent());
        player.sendMessage("Hespori will start soon.");
    }
}
