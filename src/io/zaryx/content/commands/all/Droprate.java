package io.zaryx.content.commands.all;

import java.util.Optional;

import io.zaryx.content.commands.Command;
import io.zaryx.content.playerinformation.Interface;
import io.zaryx.model.entity.npc.drops.DropManager;
import io.zaryx.model.entity.player.Player;

public class Droprate extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        double dr = DropManager.getModifier1(player);

        if (dr > 75) {
            player.forcedChat("My drop rate bonus is : 75%.");
        } else {
            player.forcedChat("My drop rate bonus is : " + DropManager.getModifier1(player) + "%.");
        }
        player.forcedChat("My drop rate bonus is : " + Interface.formatString(DropManager.getModifier1(player)));
    }
    @Override
    public Optional<String> getDescription() {
        return Optional.of("Shows drop rate bonus");
    }
}


