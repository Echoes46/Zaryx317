package io.zaryx.content.collection_log;

import io.zaryx.model.items.GameItem;
import io.zaryx.model.entity.npc.drops.DropManager;
import io.zaryx.util.ItemConstants;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CollectionLogCompletionTest {
    private List<GameItem> items(int... ids) {
        List<GameItem> items = new ArrayList<>();
        for (int id : ids) items.add(new GameItem(id, 1));
        return items;
    }

    @Test void retiredCertificatesDoNotBlockCompletionOrInflateProgress() {
        List<GameItem> required = items(100, 101);
        List<GameItem> saved = items(100, 101, 696, 33429);
        assertTrue(CollectionLog.isComplete(required, saved));
        assertEquals(2, CollectionLog.obtainedCount(required, saved));
        assertEquals(4, saved.size(), "Historical unlocks must be preserved");
    }

    @Test void equalCountsCannotSubstituteAnOldCertificateForAMissingItem() {
        assertFalse(CollectionLog.isComplete(items(100, 101), items(100, 696)));
        assertEquals(1, CollectionLog.obtainedCount(items(100, 101), items(100, 696)));
    }

    @Test void duplicateRequirementsAndUnlocksCountOnlyOnce() {
        assertTrue(CollectionLog.isComplete(items(100, 100, 101), items(100, 101, 101)));
        assertEquals(2, CollectionLog.obtainedCount(items(100, 100, 101), items(100, 101, 101)));
        assertFalse(CollectionLog.isComplete(items(100, 101), items(100, 100)));
    }

    @Test void zeroAmountsAndEmptyLogsAreNotComplete() {
        assertFalse(CollectionLog.isComplete(items(100), List.of(new GameItem(100, 0))));
        assertFalse(CollectionLog.isComplete(List.of(), items(696)));
        assertFalse(CollectionLog.isComplete(null, null));
        assertFalse(CollectionLog.isComplete(items(100), null));
    }

    @Test void currentRequiredCertificatesStillCount() {
        assertFalse(CollectionLog.isComplete(items(100, 696), items(100, 33429)));
        assertTrue(CollectionLog.isComplete(items(100, 696), items(100, 696, 33429)));
    }

    @Test void completedNamesAreGreen() {
        assertEquals("@gre@Nex", CollectionLog.completionName("Nex", true));
        assertEquals("Nex", CollectionLog.completionName("Nex", false));
    }

    @Test void removedCertificateBossesCompleteAgainstCurrentRuntimeDropTables() throws Exception {
        DropManager manager = new DropManager();
        Method read = DropManager.class.getDeclaredMethod("readFromDirectory", File.class, ItemConstants.class);
        read.setAccessible(true);
        read.invoke(manager, new File("etc/cfg/drops"), new ItemConstants().load());
        int[] bosses = {2265, 319, 5862, 9425, 11278, 8096, 8781, 10531, 10532};
        Set<Integer> retired = Set.of(691, 692, 693, 696, 33428, 33429);
        for (int boss : bosses) {
            List<GameItem> required = CollectionLog.uniqueRequirements(manager.getNPCdrops(boss));
            assertFalse(required.isEmpty(), "Missing runtime table: " + boss);
            assertTrue(required.stream().noneMatch(item -> retired.contains(item.getId())), "Retired certificate required: " + boss);
            List<GameItem> saved = new ArrayList<>(required);
            saved.add(new GameItem(696, 25));
            saved.add(new GameItem(33429, 1));
            assertTrue(CollectionLog.isComplete(required, saved), "Old saved unlocks block boss: " + boss);
            saved.remove(0);
            assertFalse(CollectionLog.isComplete(required, saved), "Missing current item accepted: " + boss);
        }
    }
}
