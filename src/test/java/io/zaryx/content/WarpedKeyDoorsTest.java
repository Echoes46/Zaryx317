package io.zaryx.content;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WarpedKeyDoorsTest {
    @Test void bothDoorsConsumeOneKeyEnteringAndAllowKeylessExit() throws Exception {
        java.lang.reflect.Field config = Server.class.getDeclaredField("configuration");
        config.setAccessible(true);
        Object previous = config.get(null);
        config.set(null, ServerConfiguration.getDefault());
        try {
            for (int[] route : new int[][]{{4102,4101,4104}, {4126,4129,4125},
                    {4102,4104,4101}, {4126,4125,4129}}) {
                Player p = new Player(null);
                p.absX = 3169; p.absY = route[1];
                boolean entering = route[2] == 4104 || route[2] == 4125;
                if (entering) { p.playerItems[0] = 3469; p.playerItemsN[0] = 2; }
                WarpedKeyDoors.handle(p, 3168, route[0], 0);
                assertEquals(3169, p.getTeleportToX());
                assertEquals(route[2], p.getTeleportToY());
                assertEquals(entering ? 1 : 0, p.getItems().getItemAmount(3468));
                WarpedKeyDoors.handle(p, 3168, route[0], 0);
                assertEquals(entering ? 1 : 0, p.getItems().getItemAmount(3468));
            }
            Player p = new Player(null);
            p.absX = 3169; p.absY = 4101;
            WarpedKeyDoors.handle(p, 3168, 4102, 0);
            assertNotEquals(4104, p.getTeleportToY());
            p.playerItems[0] = 3469; p.playerItemsN[0] = 1;
            p.morphed = true;
            WarpedKeyDoors.handle(p, 3168, 4102, 0);
            assertTrue(p.getItems().playerHasItem(3468, 1));
            assertNotEquals(4104, p.getTeleportToY());
            assertEquals(-1, WarpedKeyDoors.destinationY(1, 4102, 0, 4101));
            assertEquals(-1, WarpedKeyDoors.destinationY(3168, 4102, 1, 4101));
        } finally { config.set(null, previous); }
    }
}
