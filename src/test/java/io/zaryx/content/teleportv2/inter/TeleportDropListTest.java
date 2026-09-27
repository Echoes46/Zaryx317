package io.zaryx.content.teleportv2.inter;

import io.zaryx.model.items.GameItem;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TeleportDropListTest {
    @Test void completePreviewKeepsItemsBeyondTheOld203SlotLimit() {
        List<GameItem> drops = new ArrayList<>();
        for (int id = 1000; id < 1300; id++) drops.add(new GameItem(id, 1));
        drops.add(new GameItem(1000, 5));
        List<GameItem> preview = TeleportInterface.uniqueDrops(drops);
        assertEquals(300, preview.size());
        assertEquals(1000, preview.get(0).getId());
        assertEquals(1299, preview.get(299).getId());
        assertTrue(TeleportInterface.uniqueDrops(new ArrayList<>()).isEmpty());
    }
}
