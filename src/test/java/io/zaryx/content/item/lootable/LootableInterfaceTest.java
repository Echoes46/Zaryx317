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
    void p2pDivisionBoxDisplaysItsConfiguredRewards() {
        Lootable box = new p2pDivisionBox(null);
        // Reward quantities and tiers can be customized without changing display behavior.
        assertSectionMatches(box, false, LootRarity.COMMON, LootRarity.UNCOMMON);
        assertSectionMatches(box, true, LootRarity.RARE, LootRarity.VERY_RARE, LootRarity.ULTRA_RARE);
    }

    private static void assertSectionMatches(Lootable box, boolean rare, LootRarity... tiers) {
        Map<Integer, Integer> expected = new java.util.HashMap<>();
        for (LootRarity tier : tiers) {
            for (GameItem item : box.getLoot().getOrDefault(tier, java.util.Collections.emptyList())) {
                expected.putIfAbsent(item.getId(), item.getAmount());
            }
        }
        List<GameItem> displayed = LootableInterface.collectItemsForDisplay(box, rare);
        Map<Integer, Integer> actual = new java.util.HashMap<>();
        for (GameItem item : displayed) actual.put(item.getId(), item.getAmount());
        assertEquals(expected, actual, rare ? "Rare rewards" : "Common rewards");
        assertEquals(expected.size(), displayed.size(), "Display must deduplicate item IDs");
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

        // A customized table can move scrap paper into the rare section.
        rewards.put(LootRarity.COMMON, Arrays.asList(new GameItem(995, 150_000_000), new GameItem(100)));
        rewards.put(LootRarity.RARE, Arrays.asList(new GameItem(102), new GameItem(11681, 50_000)));
        List<GameItem> common = LootableInterface.collectItemsForDisplay(lootable, false);
        List<GameItem> rare = LootableInterface.collectItemsForDisplay(lootable, true);
        assertEquals(Arrays.asList(995, 100, 101), ids(common));
        assertEquals(150_000_000, common.get(0).getAmount());
        assertEquals(Arrays.asList(102, 11681, 103, 104), ids(rare));
        assertEquals(50_000, rare.get(1).getAmount());
    }

    private static List<Integer> ids(List<GameItem> items) {
        return items.stream().map(GameItem::getId).collect(java.util.stream.Collectors.toList());
    }
}
