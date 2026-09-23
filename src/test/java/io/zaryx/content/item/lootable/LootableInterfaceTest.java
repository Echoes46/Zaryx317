package io.zaryx.content.item.lootable;

import io.zaryx.content.item.lootable.impl.p2pDivisionBox;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LootableInterfaceTest {

    @Test
    void p2pDivisionBoxDisplaysItsScrapPaperReward() {
        List<GameItem> displayed = LootableInterface.collectItemsForDisplay(new p2pDivisionBox(null), false);

        assertEquals(5_000, displayed.stream()
                .filter(item -> item.getId() == 11681)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Scrap paper is missing from the P2P box table"))
                .getAmount());
    }

    @Test
    void displayIncludesEveryRewardRarityAndScrapPaper() {
        Map<LootRarity, List<GameItem>> rewards = new EnumMap<>(LootRarity.class);
        rewards.put(LootRarity.COMMON, Arrays.asList(new GameItem(11681, 5_000), new GameItem(100)));
        rewards.put(LootRarity.UNCOMMON, Arrays.asList(new GameItem(101), new GameItem(100)));
        rewards.put(LootRarity.RARE, Arrays.asList(new GameItem(102)));
        rewards.put(LootRarity.VERY_RARE, Arrays.asList(new GameItem(103)));
        rewards.put(LootRarity.ULTRA_RARE, Arrays.asList(new GameItem(104)));

        Lootable lootable = new Lootable() {
            @Override
            public Map<LootRarity, List<GameItem>> getLoot() {
                return rewards;
            }

            @Override
            public void roll(Player player) {
            }
        };

        assertEquals(Arrays.asList(11681, 100, 101), ids(LootableInterface.collectItemsForDisplay(lootable, false)));
        assertEquals(Arrays.asList(102, 103, 104), ids(LootableInterface.collectItemsForDisplay(lootable, true)));
    }

    private static List<Integer> ids(List<GameItem> items) {
        return items.stream().map(GameItem::getId).collect(java.util.stream.Collectors.toList());
    }
}
