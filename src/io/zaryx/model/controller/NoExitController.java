package io.zaryx.model.controller;

import io.zaryx.model.entity.player.Player;

/**
 * Does not allow any exits by the player, only through manually movement of the
 * player through the server.
 * Restrictions: no magic teleporting, ...
 */
public class NoExitController extends DefaultController {

    @Override
    public String getKey() {
        return "no_exit";
    }

    @Override
    public boolean canMagicTeleport(Player player) {
        return false;
    }

}
