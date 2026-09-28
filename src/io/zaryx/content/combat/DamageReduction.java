package io.zaryx.content.combat;

import io.zaryx.model.entity.player.Player;

/** Passive equipment damage reductions shared by every incoming-hit path. */
public final class DamageReduction {
    private static final int DEFENDER_ICON = 10558;
    private static final int JUAN_GLOVES = 13372;

    private DamageReduction() { }

    public static int apply(Player player, int damage) {
        if (damage <= 1 || player.getPosition().inWild()) {
            return damage;
        }

        boolean wearingJuanGloves = player.playerEquipment[Player.playerHands] == JUAN_GLOVES;
        boolean wearingDefenderIcon = player.playerEquipmentCosmetic[Player.playerAura] == DEFENDER_ICON;
        return apply(damage, wearingDefenderIcon, wearingJuanGloves);
    }

    static int apply(int damage, boolean wearingDefenderIcon, boolean wearingJuanGloves) {
        if (damage <= 1) {
            return damage;
        }
        // Only the strongest passive applies; these effects are not intended to stack.
        if (wearingJuanGloves) {
            return damage * 85 / 100;
        }
        if (wearingDefenderIcon) {
            return damage * 90 / 100;
        }
        return damage;
    }
}
