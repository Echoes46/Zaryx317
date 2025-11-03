package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.net.login.RS2LoginProtocol;

public class Addresswhitelist extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        RS2LoginProtocol.ADDRESS_WHITELIST.add(input);
        player.sendMessage("Add character to address whitelist: " + input);
    }
}
