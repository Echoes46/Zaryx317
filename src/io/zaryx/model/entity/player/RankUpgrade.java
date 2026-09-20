package io.zaryx.model.entity.player;

public enum RankUpgrade {
    AWAKENED(Right.Donator, 20),
    RUNIC(Right.Super_Donator, 50),
    MYSTIC(Right.Great_Donator,100),
    ARCANE(Right.Extreme_Donator, 250),
    ELDRITCH(Right.Major_Donator, 500),
    ASTRAL(Right.Supreme_Donator, 1250),
    ETHEREAL(Right.Gilded_Donator, 2500),
    CELESTIAL(Right.Platinum_Donator, 4000),
    DIVINE(Right.Apex_Donator, 6500),
    ETERNAL(Right.Almighty_Donator, 15000);

    /**
     * The rights that will be appended if upgraded
     */
    public final Right rights;

    /**
     * The amount required for the upgrade
     */
    public final int amount;

    RankUpgrade(Right rights, int amount) {
        this.rights = rights;
        this.amount = amount;
    }

    public static RankUpgrade forAmount(int totalDonated) {
        RankUpgrade earned = null;
        for (RankUpgrade rank : values()) {
            if (totalDonated >= rank.amount) {
                earned = rank;
            }
        }
        return earned;
    }
}
