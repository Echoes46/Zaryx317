package io.zaryx.content.tradingpost;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class TradingPostCurrencyTest {
    @Test
    void repeatedSaveAndReloadPreservesEveryCurrencyAndOfferField() {
        for (int currency : new int[]{995, 13204, -1}) {
            TradePostOffer offer = new TradePostOffer("seller", new GameItem(4151, 12),
                    1234567, 1750000000000L, currency, 3);
            for (int login = 0; login < 5; login++) {
                offer = TradePostOffer.fromSaveFields("seller", offer.toSaveString().split("\t"));
                assertEquals(currency, offer.getCurrencyId());
                assertEquals(4151, offer.getItem().getId());
                assertEquals(12, offer.getItem().getAmount());
                assertEquals(1234567, offer.getPricePerItem());
                assertEquals(1750000000000L, offer.getTimestamp());
                assertEquals(3, offer.getTotalSold());
            }
        }
    }

    @Test
    void legacySavesKeepTheirHistoricalMeaningAndInvalidCurrenciesAreRejected() {
        assertEquals(13204, TradePostOffer.fromSaveFields("seller", "4151\t1\t100\tfalse\t0\t123".split("\t")).getCurrencyId());
        assertEquals(-1, TradePostOffer.fromSaveFields("seller", "4151\t1\t100\ttrue\t0\t123".split("\t")).getCurrencyId());
        assertThrows(IllegalArgumentException.class, () -> TradePostOffer.fromSaveFields("seller",
                "4151\t1\t100\t1234\t0\t123".split("\t")));
    }

    @Test
    void loginRetainsSeparateUncollectedBalancesIncludingLargeGpAmounts() throws Exception {
        ServerConfiguration previous = Server.getConfiguration();
        if (previous == null) Server.setConfiguration(ServerConfiguration.getDefault());
        try {
            Player player = new Player(null);
            player.tempCoinCoffer = 3_000_000_000L;
            player.tempPlatCoffer = 123;
            player.tempNomadCoffer = 456;
            POSManager post = new POSManager();
            Field tradePost = Player.class.getDeclaredField("tradePost");
            tradePost.setAccessible(true);
            tradePost.set(player, post);
            post.init(player);
            assertEquals(3_000_000_000L, post.getActualCoinCoffer());
            assertEquals(123, post.getCoinCoffer());
            assertEquals(456, post.getNomadCoffer());
            assertEquals(0, player.tempCoinCoffer);
            assertEquals(0, player.tempPlatCoffer);
            assertEquals(0, player.tempNomadCoffer);
        } finally {
            Field configuration = Server.class.getDeclaredField("configuration");
            configuration.setAccessible(true);
            configuration.set(null, previous);
        }
    }

    @Test
    void saleHistoryKeepsGpDistinctFromPlatinumAndPoints() {
        for (int currency : new int[]{995, 13204, -1}) {
            TradePostHistory history = new TradePostHistory("buyer", "seller", new GameItem(4151, 1), 123L, currency, 500);
            assertEquals(currency, history.getCurrencyId());
        }
    }
}
