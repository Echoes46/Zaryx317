package io.zaryx.content;

import io.zaryx.model.entity.player.Player;

/** The two gates into the Revenant Maledictus enclosure. */
public final class WarpedKeyDoors {
    private WarpedKeyDoors() { }

    static int destinationY(int doorX, int doorY, int height, int playerY) {
        if (doorX != 3168 || height != 0) return -1;
        if (doorY == 4102) return playerY <= 4102 ? 4104 : 4101;
        if (doorY == 4126) return playerY >= 4126 ? 4125 : 4129;
        return -1;
    }

    public static void handle(Player player, int doorX, int doorY, int height) {
        int destination = destinationY(doorX, doorY, height, player.getY());
        if (destination == -1 || player.getHeight() != height || player.teleTimer > 0
                || Math.abs(player.getX() - 3169) > 2 || Math.abs(player.getY() - doorY) > 3) return;
        boolean entering = destination == 4104 || destination == 4125;
        if (entering) {
            if (!player.getItems().playerHasItem(3468, 1)) {
                player.getDH().sendStatement("You need a Warped key to unlock this door.");
                return;
            }
        }
        player.getPA().movePlayer(3169, destination, 0);
        // movePlayer can refuse movement (for example, while a bank PIN is locked).
        if (entering && player.teleTimer > 0 && player.getTeleportToX() == 3169
                && player.getTeleportToY() == destination) {
            player.getItems().deleteItem(3468, 1);
        }
    }
}
