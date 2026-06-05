package io.zaryx.content.commands.owner;

import io.zaryx.ServerStartup;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

/**
 * Updated by Khaos
 */
public class ReloadWalkable extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            ServerStartup.loadWalkableTiles();
            player.sendMessage("@gre@Reloaded walkable tile overrides from walkable_tiles.cfg.");
        } catch (Exception e) {
            player.sendMessage("@red@Failed to reload walkable tiles. Check server console.");
            e.printStackTrace();
        }
    }
}