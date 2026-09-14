package io.zaryx.content.combat.weapon;

/** Upgrade descendants retain the Twisted Bow's target scaling and local damage bonus. */
public final class TwistedBowFamily {
    private TwistedBowFamily() { }

    public static boolean contains(int weapon) {
        return weapon == 20997 || weapon == 33058 || weapon == 33207 || weapon == 20484;
    }

    public static double targetDamageMultiplier(int weapon, double twistedMultiplier) {
        // Upgrades previously had no low-magic penalty; preserve that benefit.
        return weapon == 20997 ? twistedMultiplier : Math.max(1.0, twistedMultiplier);
    }

    public static final double DAMAGE_MULTIPLIER = 2.25;
}
