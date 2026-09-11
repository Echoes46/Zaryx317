package io.zaryx.model.entity.npc.pets;

import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PetPerksTest {
    @Test void kratosExplainsItsCombatRecoveryAndLoot() {
        String text = String.join("\n", PetPerks.describe(30022));
        assertTrue(text.contains("melee:"));
        assertTrue(text.contains("ranged:"));
        assertTrue(text.contains("magic:"));
        assertTrue(text.contains("Heal for 1/3"));
        assertTrue(text.contains("Restore prayer"));
        assertTrue(text.contains("+20% drop-rate"));
        assertTrue(text.contains("coin-bag"));
        assertTrue(text.contains("clue-scroll"));
    }
    @Test void dismissalDisablesCombatAndStorageEvenWithStaleItemId() throws Exception {
        io.zaryx.ServerConfiguration previous = io.zaryx.Server.getConfiguration();
        if (previous == null) io.zaryx.Server.setConfiguration(io.zaryx.ServerConfiguration.getDefault());
        try {
        Player player = new Player(null);
        player.petSummonId = 30122;
        player.hasFollower = true;
        assertTrue(PetHandler.hasDarkMagePet(player));
        assertTrue(PetHandler.hasDarkMeleePet(player));
        player.hasFollower = false;
        assertFalse(PetHandler.hasDarkMagePet(player));
        assertFalse(PetHandler.hasDarkMeleePet(player));
        player.petSummonId = 31014;
        assertFalse(PetHandler.hasstoragepetout(player));
        player.hasFollower = true;
        assertTrue(PetHandler.hasstoragepetout(player));
        } finally {
            java.lang.reflect.Field configuration = io.zaryx.Server.class.getDeclaredField("configuration");
            configuration.setAccessible(true);
            configuration.set(null, previous);
        }
    }
    @Test void cosmeticAndRetiredIdsDoNotAcquireBonuses() {
        assertEquals(0, PetPerks.dropBonus(12650));
        assertEquals(0, PetPerks.combatTier(12650, PetPerks.Style.MELEE));
        assertFalse(PetPerks.heals(30118));
        assertTrue(String.join(" ", PetPerks.describe(12650)).contains("progression bonuses"));
    }
    @Test void descriptionsFitPanelAndCustomNamesMatchClient() {
        for (PetHandler.Pets pet : PetHandler.Pets.values()) {
            int rows = 0;
            for (String text : PetPerks.describe(pet.getItemId())) rows += Math.max(1, (text.length() + 59) / 60);
            assertTrue(rows <= 64, pet.name());
        }
        assertEquals("Beaver", PetPerks.displayName(33065, "Rock"));
        assertEquals("Mystery Box", PetPerks.displayName(33067, "Fish"));
        assertEquals("Christmas Imp", PetPerks.displayName(33159, "Dark Seren"));
    }
}
