package io.zaryx.model.entity.npc.pets;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.content.commands.all.Pet;
import io.zaryx.model.entity.player.Player;
import java.lang.reflect.Field;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompanionJournalTest {
    private interface Check { void run(Player p) throws Exception; }
    private void player(Check check) throws Exception {
        Field config = Server.class.getDeclaredField("configuration"); config.setAccessible(true);
        Object old = config.get(null); config.set(null, ServerConfiguration.getDefault());
        Field definitions = io.zaryx.model.definitions.ItemDef.class.getDeclaredField("definitions");
        definitions.setAccessible(true);
        Object oldDefinitions = definitions.get(null);
        try {
            Map<Integer, io.zaryx.model.definitions.ItemDef> fixtures = new HashMap<>();
            for (PetHandler.Pets pet : PetHandler.Pets.values()) fixtures.put(pet.getItemId(),
                    io.zaryx.model.definitions.ItemDef.builder().id(pet.getItemId()).name(pet.name()).build());
            definitions.set(null, fixtures);
            check.run(new Player(null));
        } finally { definitions.set(null, oldDefinitions); config.set(null, old); }
    }
    private static void set(CompanionJournal journal, String key, Object value) throws Exception {
        Field field = CompanionJournal.class.getDeclaredField(key); field.setAccessible(true); field.set(journal, value);
    }
    @Test void ownershipAndSavedLevelsRemainIndependent() throws Exception {
        player(p -> {
            p.companionProgress.decode("30022:6500");
            assertFalse(CompanionJournal.owned(p, 30022));
            assertEquals(5, p.companionProgress.level(30022));
            p.petSummonId = 30022; // Stale summon ID does not imply ownership.
            assertFalse(CompanionJournal.owned(p, 30022));
            p.hasFollower = true;
            assertTrue(CompanionJournal.owned(p, 30022));
            assertFalse(CompanionJournal.owned(p, 30122));
            set(p.companionJournal, "ownership", 1);
            assertEquals(Collections.singletonList(30022), p.companionJournal.filtered(p));
            set(p.companionJournal, "ownership", 2);
            assertFalse(p.companionJournal.filtered(p).contains(30022));
            p.hasFollower = false;
            assertTrue(p.companionJournal.filtered(p).contains(30022));
            assertEquals(5, p.companionProgress.level(30022));
        });
    }
    @Test void searchRolesAndDuplicateIds() throws Exception {
        player(p -> {
            List<Integer> ids = CompanionJournal.ids();
            assertEquals(new HashSet<>(ids).size(), ids.size());
            set(p.companionJournal, "query", "30022");
            assertEquals(Collections.singletonList(30022), p.companionJournal.filtered(p));
            set(p.companionJournal, "query", "kratos");
            assertTrue(p.companionJournal.filtered(p).contains(30022));
            set(p.companionJournal, "query", "no such companion");
            assertTrue(p.companionJournal.filtered(p).isEmpty());
            assertTrue(CompanionJournal.matchesRole(13320, 2));
            assertFalse(CompanionJournal.matchesRole(30022, 2));
            assertTrue(CompanionJournal.matchesRole(30022, 1));
            assertTrue(CompanionJournal.matchesRole(30022, 3));
        });
    }
    @Test void descriptionsCoverEveryPetAndComparisonKeepsBothAccountsLevels() throws Exception {
        player(p -> {
            for (int id : CompanionJournal.ids()) {
                for (int tab = 1; tab <= 3; tab++) {
                    List<String> rows = Pet.wrapDetails(p.companionJournal.details(p, id, tab));
                    assertFalse(rows.isEmpty());
                    assertTrue(rows.size() <= Pet.ROW_COUNT, id + " tab " + tab);
                }
            }
            p.companionProgress.decode("30022:6500,30122:50000");
            set(p.companionJournal, "pinned", 30022);
            List<String> comparison = p.companionJournal.details(p, 30122, 4);
            assertTrue(comparison.contains("Saved level: 5 | 10"));
            assertTrue(comparison.contains("@or1@A: BASE PERKS"));
            assertTrue(comparison.contains("@or1@B: BASE PERKS"));
            assertTrue(p.companionJournal.click(p, 22864)); // Closed interface: consumes but never opens/sends packets.
            assertEquals(0, p.getOpenInterface());
            assertFalse(p.companionJournal.click(p, 999));
        });
    }
    @Test void acquisitionUsesActualParentMetadata() {
        assertTrue(PetHandler.journalSources(12650).get(0).contains("General Graardor"));
        assertTrue(PetHandler.journalSources(11995).stream().anyMatch(s -> s.contains("Chaos Fanatic")));
        assertTrue(PetHandler.journalSources(1555).get(0).contains("No direct NPC"));
    }
}
