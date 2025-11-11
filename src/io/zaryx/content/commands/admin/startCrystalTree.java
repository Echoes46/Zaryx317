package io.zaryx.content.commands.admin;

import io.zaryx.content.commands.Command;
import io.zaryx.content.events.monsterhunt.CrystalTree;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

public class startCrystalTree extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {

        PlayerHandler.executeGlobalMessage("The Glistening tree has sprouted!");
        CrystalTree.Tick();

    }
}