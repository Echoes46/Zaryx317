package io.zaryx.content.skills.firemake;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class BonfireTest {
    private interface Check { void run(Player player); }
    private void player(Check check) throws Exception {
        Field config = Server.class.getDeclaredField("configuration");
        config.setAccessible(true);
        Object previous = config.get(null);
        config.set(null, ServerConfiguration.getDefault());
        Player player = new Player(null);
        player.playerItems[0] = 1512;
        player.playerItemsN[0] = 10;
        player.playerLevel[11] = 99;
        try { check.run(player); }
        finally {
            Server.getEventHandler().stop(player);
            Server.getEventHandler().process();
            config.set(null, previous);
        }
    }
    private void assertLogsRemain(Player player) {
        for (int i = 0; i < 6; i++) Server.getEventHandler().process();
        assertEquals(1512, player.playerItems[0]);
        assertEquals(10, player.playerItemsN[0]);
        assertFalse(Server.getEventHandler().isRunning(player, "skilling"));
    }
    @Test void stopSkillingCancelsBeforeFirstBurn() throws Exception {
        player(player -> {
            assertTrue(Burner.handleBonfireClick(player, 30019));
            assertTrue(Server.getEventHandler().isRunning(player, "skilling"));
            player.getPA().stopSkilling();
            assertLogsRemain(player);
        });
    }
    @Test void stopSkillingCancelsAnActiveBonfire() throws Exception {
        player(player -> {
            Burner.handleBonfireClick(player, 30019);
            Server.getEventHandler().process();
            player.getPA().stopSkilling();
            assertLogsRemain(player);
        });
    }
    @Test void repeatedClicksDoNotLeaveUncancelledBurnLoops() throws Exception {
        player(player -> {
            for (int i = 0; i < 4; i++) Burner.handleBonfireClick(player, 30019);
            Server.getEventHandler().process();
            player.getPA().stopSkilling();
            assertLogsRemain(player);
        });
    }
    @Test void movementAlsoStopsBurning() throws Exception {
        player(player -> {
            Burner.handleBonfireClick(player, 30019);
            Server.getEventHandler().process();
            player.absX++;
            assertLogsRemain(player);
        });
    }
}
