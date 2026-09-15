package io.zaryx.content.bosses;

/** Shared limits for Forest Guardian's legacy attack handlers. */
public final class ForestGuardianBalance {
    public static final int MAX_HIT = 40;
    private ForestGuardianBalance() { }

    /** Drain 10% of current level, but never below 75% of the base level. */
    public static int drainedLevel(int current, int base) {
        int floor = Math.max(1, base * 3 / 4);
        if (current <= floor) return current;
        return Math.max(floor, current - Math.max(1, current / 10));
    }
}
