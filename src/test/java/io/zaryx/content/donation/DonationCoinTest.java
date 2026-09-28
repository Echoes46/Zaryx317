package io.zaryx.content.donation;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DonationCoinTest {
    @Test
    void convertsOneCoinIntoOnePointAndCapsAtInventoryAmount() throws Exception {
        Field configuration = Server.class.getDeclaredField("configuration");
        configuration.setAccessible(true);
        Object previous = configuration.get(null);
        configuration.set(null, ServerConfiguration.getDefault());
        try {
            Player player = new Player(null);
            player.playerItems[0] = DonationCoin.ITEM_ID + 1;
            player.playerItemsN[0] = 5;

            assertEquals(3, DonationCoin.convert(player, 3));
            assertEquals(3, player.donatorPoints);
            assertEquals(2, player.getItems().getInventoryCount(DonationCoin.ITEM_ID));

            assertEquals(2, DonationCoin.convert(player, 100));
            assertEquals(5, player.donatorPoints);
            assertEquals(0, player.getItems().getInventoryCount(DonationCoin.ITEM_ID));
            assertEquals(0, DonationCoin.convert(player, 0));
        } finally {
            configuration.set(null, previous);
        }
    }

    @Test
    void doesNotConsumeCoinsWhenPointBalanceIsFull() throws Exception {
        Field configuration = Server.class.getDeclaredField("configuration");
        configuration.setAccessible(true);
        Object previous = configuration.get(null);
        configuration.set(null, ServerConfiguration.getDefault());
        try {
            Player player = new Player(null);
            player.playerItems[0] = DonationCoin.ITEM_ID + 1;
            player.playerItemsN[0] = 1;
            player.donatorPoints = Integer.MAX_VALUE;

            assertEquals(0, DonationCoin.convert(player, 1));
            assertEquals(1, player.getItems().getInventoryCount(DonationCoin.ITEM_ID));
        } finally {
            configuration.set(null, previous);
        }
    }
}
