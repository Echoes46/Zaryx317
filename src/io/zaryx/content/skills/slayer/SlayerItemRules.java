package io.zaryx.content.skills.slayer;

import java.util.function.IntPredicate;

/** Item identities used by Slayer rewards and helmet assembly. */
public final class SlayerItemRules {
    private SlayerItemRules() { }

    private static final int[][] HELMET_IMBUES = {
            {11864, 11865}, {19639, 19641}, {19643, 19645}, {19647, 19649},
            {23073, 23075}, {21264, 21266}, {24370, 24444}, {21888, 21890},
            {25898, 25900}, {25904, 25906}, {25910, 25912}
    };

    public static int imbuedHelmetId(int helmetId) {
        for (int[] pair : HELMET_IMBUES) {
            if (pair[0] == helmetId) return pair[1];
        }
        return -1;
    }

    public static int firstHelmetToImbue(IntPredicate inInventory) {
        for (int[] pair : HELMET_IMBUES) {
            if (inInventory.test(pair[0])) return pair[0];
        }
        return -1;
    }

    public static boolean isUnnotedBlackMask(int itemId) {
        return itemId >= 8901 && itemId <= 8921 && (itemId & 1) == 1;
    }

    public static int firstBlackMask(IntPredicate inInventory) {
        for (int id = 8901; id <= 8921; id += 2) {
            if (inInventory.test(id)) return id;
        }
        return -1;
    }

    /** Point boosts are intentionally non-stacking; consumables are saved when a passive boost applies. */
    public static int pointMultiplier(boolean passiveDoublePoints, boolean scrollAvailable) {
        return passiveDoublePoints || scrollAvailable ? 2 : 1;
    }
}
