package io.zaryx.content.games.blackjack;

/** Amounts remain long until a bounded inventory transfer is possible. */
public final class BlackjackAmounts {
    private BlackjackAmounts() { }

    public static int collectable(long pending, int coins, int freeSlots) {
        if (pending <= 0 || coins < 0 || (coins == 0 && freeSlots <= 0)) return 0;
        return (int) Math.min(pending, (long) Integer.MAX_VALUE - coins);
    }

    public static long adjust(long current, long available, boolean doubleBet) {
        long bounded = Math.max(0, Math.min(current, Integer.MAX_VALUE));
        return Math.min(Math.max(0, available), doubleBet ? Math.min(Integer.MAX_VALUE, bounded * 2) : bounded / 2);
    }
}
