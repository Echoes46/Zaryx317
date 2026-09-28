package io.zaryx.content.combat;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DamageReductionTest {
    @Test
    void defenderIconReducesDamageOnceByTenPercent() {
        assertEquals(90, DamageReduction.apply(100, true, false));
        assertEquals(9, DamageReduction.apply(10, true, false));
        assertEquals(1, DamageReduction.apply(1, true, false));
    }

    @Test
    void juanGlovesTakePriorityInsteadOfStackingWithDefenderIcon() {
        assertEquals(85, DamageReduction.apply(100, false, true));
        assertEquals(85, DamageReduction.apply(100, true, true));
        assertEquals(100, DamageReduction.apply(100, false, false));
    }

    @Test
    void effectUsesCosmeticAuraSlotAndIsDisabledInWilderness() throws Exception {
        Field configuration = Server.class.getDeclaredField("configuration");
        configuration.setAccessible(true);
        Object previous = configuration.get(null);
        configuration.set(null, ServerConfiguration.getDefault());
        try {
            Player player = new Player(null);
            player.absX = 3000;
            player.absY = 3000;
            player.playerEquipmentCosmetic[Player.playerAura] = 10558;
            assertEquals(90, DamageReduction.apply(player, 100));

            player.playerEquipmentCosmetic[Player.playerAura] = -1;
            player.playerEquipment[Player.playerFeet] = 10558;
            assertEquals(100, DamageReduction.apply(player, 100));

            player.playerEquipmentCosmetic[Player.playerAura] = 10558;
            player.absX = 3200;
            player.absY = 3600;
            assertEquals(100, DamageReduction.apply(player, 100));
        } finally {
            configuration.set(null, previous);
        }
    }
}
