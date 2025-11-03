package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.content.deals.TimeOffers;
import io.zaryx.model.entity.player.Player;


public class ReloadOffers extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        TimeOffers.forceReloadOffers(player);
    }
}
