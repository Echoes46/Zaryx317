package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.content.events.monsterhunt.MonsterHunt;
import io.zaryx.model.entity.player.Player;

public class startmonster extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        MonsterHunt.spawnNPC();
        player.sendMessage("The Monster Hunt event has started!");
    }
}