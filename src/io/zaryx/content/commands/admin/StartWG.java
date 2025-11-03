package io.zaryx.content.commands.admin;

import io.zaryx.content.commands.Command;
import io.zaryx.content.worldevent.WorldEventContainer;
import io.zaryx.content.worldevent.impl.WGWorldEvent;
import io.zaryx.model.entity.player.Player;

public class StartWG extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        WorldEventContainer.getInstance().startEvent(new WGWorldEvent());
        player.sendMessage("WeaponGames will start soon.");
    }
}
