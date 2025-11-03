package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.offlinestorage.ItemCollection;

public class Givereward extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            String[] args = input.split("-");
            if (args.length != 3) {
                throw new IllegalArgumentException();
            }
            String playerName = args[0];
            int itemID = Integer.parseInt(args[1]);
            int amount = Misc.stringToInt(args[2]);

            ItemCollection.add(playerName, new GameItem(itemID, amount));
            player.sendMessage("You have given " + playerName + " " + ItemDef.forId(itemID).getName() + " x " + amount + "!");
//            Discord.writeOfflineRewardsMessage("[OFFLINE REWARDS] " + player.getDisplayName() + " has given " + playerName + " " + ItemDef.forId(itemID).getName() + " x " + amount + "!");

        } catch (Exception e) {
            player.sendMessage("Error. Correct syntax: ::givereward-player-itemid-amount");
        }
    }
}
