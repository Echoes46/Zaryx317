package io.zaryx.content.commands.admin;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

public class SetStoreD extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            String[] data = input.split("-");
            Player recipient = PlayerHandler.getPlayerByDisplayName(data[0]);
            int amount = Integer.parseInt(data[1]);


            if (recipient == null) {
                player.sendMessage("No player online with name: " + data[0]);
                return;
            }

            if (amount <= 0) {
                player.sendMessage("You cannot assign a value of 0!");
                return;
            }

            recipient.setStoreDonated(recipient.getStoreDonated() + amount);
            player.sendMessage("You have increased " + recipient.getDisplayName() + " Store donation amount by " + amount +", total: " + recipient.getStoreDonated());
            recipient.sendMessage("Your store donation has been increase by " + amount + ", Total: " +recipient.getStoreDonated());

        } catch (Exception e) {
            player.sendMessage("Error occurred, usage: ::setstored-name-amount");
        }
    }
}
