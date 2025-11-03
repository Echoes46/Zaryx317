package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.collisionmap.RegionProvider;
import io.zaryx.model.entity.player.Player;

/**
 * @author Arthur Behesnilian 4:07 PM
 */
public class ClipTile extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        int flag = input.length() == 0 ? RegionProvider.FULL_NPC_TILE_FLAG : Integer.parseInt(input);
        player.getRegionProvider().addClipping(flag, player.getX(), player.getY(), player.getHeight());
        player.sendMessage("You add flag=" + flag + " to " + player.getPosition());
    }

}
