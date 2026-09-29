package io.zaryx.content.minigames.raids;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.impl.RaidsChestItems;
import io.zaryx.model.items.GameItem;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ChambersUniqueRewardsTest {

    private static final int TWISTED_BOW = 20997;
    private static final int OSMUMTENS_FANG = 26219;

    @Test
    void fangHasTheSameUniqueWeightAsTwistedBow() {
        List<GameItem> uniques = RaidsChestItems.getItems().get(LootRarity.RARE);

        assertEquals(1, count(uniques, TWISTED_BOW));
        assertEquals(count(uniques, TWISTED_BOW), count(uniques, OSMUMTENS_FANG));
    }

    @Test
    void fangIsNoLongerInKalphiteQueenDrops() throws IOException {
        String drops = Files.readString(Path.of("etc/cfg/drops/kalphite_queen.yml"));

        assertFalse(drops.contains(String.valueOf(OSMUMTENS_FANG)));
        assertFalse(drops.contains("extremely_rare:"));
    }

    private static long count(List<GameItem> items, int itemId) {
        return items.stream().filter(item -> item.getId() == itemId).count();
    }
}
