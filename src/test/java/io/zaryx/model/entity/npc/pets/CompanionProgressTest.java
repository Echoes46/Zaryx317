package io.zaryx.model.entity.npc.pets;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompanionProgressTest {
    @Test void individualTypesAndCosmeticFamiliesPersistIndependently() {
        CompanionProgress progress = new CompanionProgress();
        assertTrue(progress.award(30022, 5, 0));
        assertFalse(progress.award(23760, 5, 1)); // Swapping pets cannot bypass cooldown.
        assertTrue(progress.award(23760, 3, 3_000_000_000L));
        assertEquals(5, progress.xp(30022));
        assertEquals(3, progress.xp(23760));
        CompanionProgress loaded = new CompanionProgress();
        loaded.decode(progress.encode());
        assertEquals(progress.encode(), loaded.encode());
        loaded.decode("13321:100,21187:200,30022:700");
        assertEquals(200, loaded.xp(13321));
        assertEquals(200, loaded.xp(21197));
        assertEquals(700, loaded.xp(30022));
    }
    @Test void corruptEntriesAndOverflowCannotBreakProgress() {
        CompanionProgress progress = new CompanionProgress();
        progress.decode("30022:9223372036854775807,23760:-8,999999:10,bad:word,13320:100,30022:oops");
        assertEquals(CompanionProgress.MAX_XP, progress.xp(30022));
        assertEquals(0, progress.xp(23760));
        assertEquals(100, progress.xp(13320));
        assertEquals(0, progress.xp(999999));
        assertTrue(progress.award(30022, Integer.MAX_VALUE, 0)); // Max-level recovery still gets an action roll.
        assertEquals(CompanionProgress.MAX_XP, progress.xp(30022));
        assertFalse(progress.award(999999, 5, 4_000_000_000L));
    }
    @Test void everyLevelBoundaryAndMilestoneIsCorrect() {
        for (int level = 2; level <= 10; level++) {
            assertEquals(level - 1, CompanionProgress.levelForXp(CompanionProgress.threshold(level) - 1));
            assertEquals(level, CompanionProgress.levelForXp(CompanionProgress.threshold(level)));
        }
        assertEquals(0, CompanionProgress.milestones(1));
        assertEquals(1, CompanionProgress.milestones(3));
        assertEquals(2, CompanionProgress.milestones(5));
        assertEquals(3, CompanionProgress.milestones(7));
        assertEquals(4, CompanionProgress.milestones(10));
        assertEquals(99, CompanionBenefits.restoreOne(98, 99));
        assertEquals(99, CompanionBenefits.restoreOne(99, 99));
        assertEquals(110, CompanionBenefits.restoreOne(110, 99));
    }
    @Test void everyPetHasAnEffectivePerkAndHigherTiersHaveMore() {
        for (PetHandler.Pets pet : PetHandler.Pets.values()) {
            int id = pet.getItemId();
            CompanionCatalog.Profile profile = CompanionCatalog.get(id);
            assertNotNull(profile, pet.name());
            assertTrue(CompanionBenefits.xpBonus(id, 1, profile.skill) > 0, pet.name());
            assertTrue(CompanionBenefits.xpBonus(id, 10, profile.skill) > CompanionBenefits.xpBonus(id, 1, profile.skill));
        }
        assertEquals(0, CompanionBenefits.damageBonus(1555, 10));
        assertTrue(CompanionBenefits.damageBonus(30122, 1) > 0);
        assertEquals(9, CompanionBenefits.recoveryChance(30122, 10));
        assertEquals(0, CompanionBenefits.xpBonus(13320, 10, 14));
        assertTrue(CompanionBenefits.xpBonus(13320, 10, 10) > 0);
    }
    @Test void activeFollowerGateAndAllPanelDescriptionsWork() throws Exception {
        java.lang.reflect.Field config = io.zaryx.Server.class.getDeclaredField("configuration");
        config.setAccessible(true);
        Object previous = config.get(null);
        config.set(null, io.zaryx.ServerConfiguration.getDefault());
        try {
            io.zaryx.model.entity.player.Player player = new io.zaryx.model.entity.player.Player(null);
            player.petSummonId = 30022;
            assertEquals(-1, CompanionBenefits.activeId(player));
            CompanionBenefits.earn(player, 5, false);
            assertEquals(0, player.companionProgress.xp(30022));
            player.hasFollower = true;
            assertEquals(30022, CompanionBenefits.activeId(player));
            for (PetHandler.Pets pet : PetHandler.Pets.values()) {
                for (int level : new int[]{1, 3, 5, 7, 10}) {
                    player.companionProgress.decode(pet.getItemId() + ":" + CompanionProgress.threshold(level));
                    java.util.List<String> rows = new java.util.ArrayList<>(CompanionBenefits.describe(player, pet.getItemId()));
                    rows.addAll(PetPerks.describe(pet.getItemId()));
                    rows = io.zaryx.content.commands.all.Pet.wrapDetails(rows);
                    assertTrue(rows.size() <= io.zaryx.content.commands.all.Pet.ROW_COUNT, pet.name() + " level " + level + " rows=" + rows.size());
                }
            }
        } finally { config.set(null, previous); }
    }
}
