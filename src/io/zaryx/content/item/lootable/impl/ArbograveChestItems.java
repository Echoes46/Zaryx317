package io.zaryx.content.item.lootable.impl;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.Lootable;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;

import java.util.*;

public class ArbograveChestItems implements Lootable {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static Map<LootRarity, List<GameItem>> getItems() {
        return items;
    }

    public static ArrayList<GameItem> getRareDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(LootRarity.RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops) {
                if (drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                drops.add(f);
            }
        }
        return drops;
    }

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(2400, 2), //Arbo key
                new GameItem(696, 10),
                new GameItem(696, 10),
                new GameItem(11681, 1000),
                new GameItem(11681, 1000),
                new GameItem(11681, 750),
                new GameItem(11681, 750),
                new GameItem(696, 10),
                new GameItem(696, 10),
                new GameItem(11681, 1000),
                new GameItem(11681, 1000),
                new GameItem(11681, 750),
                new GameItem(11681, 750),
                new GameItem(13307, 1500),
                new GameItem(13307, 1500),
                new GameItem(13307, 1500),
                new GameItem(13307, 1500),
                new GameItem(13307, 1500),
                new GameItem(13307, 1500),
                new GameItem(24366, 1),
                new GameItem(30002, 3),
                new GameItem(30002, 3),
                new GameItem(2528, 1),
                new GameItem(2528, 1),
                new GameItem(2528, 1),
                new GameItem(995, 500000),
                new GameItem(995, 500000),
                new GameItem(995, 500000),
                new GameItem(995, 500000),
                new GameItem(21905, 175),
                new GameItem(13440, 175),
                new GameItem(23686, 25),
                new GameItem(23686, 25)

        ));

        items.put(LootRarity.UNCOMMON,Arrays.asList(
                new GameItem(33136, 2),
                new GameItem(2400, 2), //Arbo key
                new GameItem(23240, 1),
                new GameItem(30002, 3),
                new GameItem(12806, 1),  //Maledtiction ward
                new GameItem(12807, 1),  //Odium ward
                new GameItem(26496, 1),  //Book (or) holy
                new GameItem(26494, 1),  //Book (or) war
                new GameItem(26492, 1),  //Book (or) war
                new GameItem(21905, 175),
                new GameItem(13440, 175),
                new GameItem(21905, 175),
                new GameItem(13440, 175),
                new GameItem(956, 1),
                new GameItem(33136, 2),
                new GameItem(2400, 2), //Arbo key
                new GameItem(23240, 1),
                new GameItem(30002, 3),
                new GameItem(12806, 1),  //Maledtiction ward
                new GameItem(12807, 1),  //Odium ward
                new GameItem(26496, 1),  //Book (or) holy
                new GameItem(26494, 1),  //Book (or) war
                new GameItem(26492, 1),  //Book (or) war
                new GameItem(21905, 175),
                new GameItem(13440, 175),
                new GameItem(21905, 175),
                new GameItem(13440, 175),
                new GameItem(956, 1),
                new GameItem(995, 500000),
                new GameItem(995, 500000),
                new GameItem(995, 500000),
                new GameItem(995, 500000),
                new GameItem(21905, 175),
                new GameItem(13440, 175),
                new GameItem(23686, 25),
                new GameItem(995, 500000),
                new GameItem(995, 500000),
                new GameItem(995, 500000),
                new GameItem(995, 500000),
                new GameItem(21905, 175),
                new GameItem(13440, 175),
                new GameItem(23686, 25),
                new GameItem(956, 1)
        ));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(12588, 1),
                new GameItem(12588, 1),
                new GameItem(12588, 1),
                new GameItem(12588, 1),
                new GameItem(12588, 1),
                new GameItem(12588, 1),
                new GameItem(26992),
                new GameItem(26992),
                new GameItem(26992),
                new GameItem(26992),
                new GameItem(27, 1),
                new GameItem(27, 1), //eyes
                new GameItem(27285, 1),  //eyes
                new GameItem(27285, 1),  //eyes
                new GameItem(27285, 1),  //eyes
                new GameItem(28274, 1),  //eyes
                new GameItem(28274, 1),  //eyes
                new GameItem(27285, 1),  //eyes
                new GameItem(27285, 1),  //eyes
                new GameItem(27285, 1),  //eyes
                new GameItem(27285, 1),  //eyes
                new GameItem(27285, 1),  //eyes
                new GameItem(28274, 1),  //eyes
                new GameItem(28274, 1),  //eyes
                new GameItem(27285, 1),  //eyes
                new GameItem(27285, 1),  //eyes
                new GameItem(25975, 1),  //lightbearer ring
                new GameItem(25975, 1),  //lightbearer ring
                new GameItem(25975, 1),  //lightbearer ring
                new GameItem(25975, 1),  //lightbearer ring
                new GameItem(25975, 1),  //lightbearer ring
                new GameItem(25975, 1),  //lightbearer
                new GameItem(25398, 1),
                new GameItem(25401, 1),
                new GameItem(25389, 1),
                new GameItem(28173, 1),
                new GameItem(28169, 1),
                new GameItem(28171, 1),
                new GameItem(28869, 1),
                new GameItem(28338, 1)






        ));
    }

    public static ArrayList<GameItem> getRare() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(LootRarity.RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops) {
                if (drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                drops.add(f);
            }
        }
        return drops;
    }

    public static ArrayList<GameItem> getAllDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        items.forEach((lootRarity, gameItems) -> {
            gameItems.forEach(g -> {
                if (!drops.contains(g)) {
                    drops.add(g);
                }
            });
        });
        return drops;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return getItems();
    }

    @Override
    public void roll(Player player) {

    }
}
