package io.zaryx.content.commands.owner;

import io.zaryx.Configuration;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

public class Bogof extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (Configuration.BOGOF) {
            Configuration.BOGOF = false;
            PlayerHandler.executeGlobalMessage("@cr1@ @blu@The BOGOF Offer has now expired <3");
        } else {
            Configuration.BOGOF = true;
            PlayerHandler.executeGlobalMessage("@cr1@ @blu@The BOGOF Offer is now active! all donations will be doubled!!");
        }
    }
}
