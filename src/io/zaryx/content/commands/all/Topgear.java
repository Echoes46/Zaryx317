package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.content.items.TopEquipment;
import io.zaryx.model.entity.player.Player;

public class Topgear extends Command {
    @Override public void execute(Player player, String commandName, String input) {
        TopEquipment.click(player, TopEquipment.ENTRY);
    }
}
