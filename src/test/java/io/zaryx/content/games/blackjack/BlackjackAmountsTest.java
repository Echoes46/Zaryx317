package io.zaryx.content.games.blackjack;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BlackjackAmountsTest {
    @Test void largeNaturalPayoutIsCollectedWithoutLosingItsRemainder() {
        long pending = 2_500_000_000L;
        int first = BlackjackAmounts.collectable(pending, 0, 1);
        assertEquals(Integer.MAX_VALUE, first);
        pending -= first;
        assertEquals(352_516_353L, pending);
        assertEquals(pending, BlackjackAmounts.collectable(pending, 0, 1));
    }
    @Test void fullInventoryDoesNotConsumePendingWinnings() {
        assertEquals(0, BlackjackAmounts.collectable(1000, 0, 0));
        assertEquals(0, BlackjackAmounts.collectable(1000, Integer.MAX_VALUE, 0));
        assertEquals(5, BlackjackAmounts.collectable(1000, Integer.MAX_VALUE - 5, 0));
    }
    @Test void splitAndDoublePayoutsCanBeCollectedInChunks() {
        long total = 8L * Integer.MAX_VALUE;
        long remaining = total;
        for (int i = 0; i < 8; i++) remaining -= BlackjackAmounts.collectable(remaining, 0, 1);
        assertEquals(0, remaining);
    }
    @Test void adjustBetClampsAndNeverOverflows() {
        assertEquals(Integer.MAX_VALUE, BlackjackAmounts.adjust(Long.MAX_VALUE, Integer.MAX_VALUE, true));
        assertEquals(50, BlackjackAmounts.adjust(101, 1000, false));
        assertEquals(30, BlackjackAmounts.adjust(100, 30, true));
        assertEquals(0, BlackjackAmounts.adjust(-1, 30, true));
    }
}
