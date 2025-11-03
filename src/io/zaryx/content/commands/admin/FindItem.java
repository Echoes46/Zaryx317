package io.zaryx.content.commands.admin;

import io.zaryx.content.commands.Command;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;

/**
 * @author Arthur Behesnilian 11:30 AM
 */
public class FindItem extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.sendMessage("Search results for <col=ff0000>" + input + ".");
        ItemDef.getDefinitions().values().stream().filter(item -> {
                    String itemName = item.getName().toLowerCase();
                    return itemName.contains(input.toLowerCase());
                }
        ).forEach(def -> player.sendMessage("(" + def.getId() + ") " + def.getName()));
    }

}
