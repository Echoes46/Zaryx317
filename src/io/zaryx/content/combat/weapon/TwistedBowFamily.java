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

    /**
     * Keeps every upgrade tier measurably ahead of the bow used to create it.
     * Equipment bonuses and attack speed still contribute separately.
     */
    public static double localDamageMultiplier(int weapon) {
        switch (weapon) {
            case 33058: // Seren godbow
                return 2.50;
            case 33207: // Demon X bow
            case 20484: // Demon X bow (en)
                return 2.75;
            case 20997: // Twisted bow
            default:
                return 2.25;
        }
    }
}
