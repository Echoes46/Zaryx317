package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.content.events.monsterhunt.CrystalTree;
import io.zaryx.content.wildwarning.WildWarning;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

public class tree extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (!CrystalTree.progress) {
            player.sendMessage("@blu@There is no crystal tree event currently active.");
            return;
        }

        if (player.jailEnd > 1) {
            player.forcedChat("I'm trying to teleport away!");
            player.sendMessage("You are still jailed!");
            return;
        }

        if (CrystalTree.ACTIVE.treeSpawn.inWild()) {
            WildWarning.sendWildWarning(player, p -> {
                p.getPA().movePlayer(CrystalTree.ACTIVE.treeSpawn.getX()-2,CrystalTree.ACTIVE.treeSpawn.getY(),0);
            });
        } else {
            player.getPA().movePlayer(CrystalTree.ACTIVE.treeSpawn.getX()-2,CrystalTree.ACTIVE.treeSpawn.getY(),0);
        }

    }
    @Override
    public Optional<String> getDescription() {
        return Optional.of("Teleport's to you the Crystal Tree.");
    }
}
