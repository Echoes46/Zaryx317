package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.content.worldevent.WorldEventContainer;
import io.zaryx.model.entity.player.Player;

public class Triggerworldevent extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        WorldEventContainer.getInstance().setTriggerImmediateEvent(true);
        player.sendMessage("Triggering next world event, please allow up to 30 seconds.");
    }
}
