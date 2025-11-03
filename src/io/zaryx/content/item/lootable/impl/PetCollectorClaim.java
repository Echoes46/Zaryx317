package io.zaryx.content.item.lootable.impl;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.Lootable;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class    PetCollectorClaim implements Lootable {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static Map<LootRarity, List<GameItem>> getItems() {
        return items;
    }

    static {
        items.put(LootRarity.COMMON,
                Arrays.asList(
                        new GameItem(11681, 2500), //scrap paper
                        new GameItem(11681, 2500), //scrap paper
                        new GameItem(11681, 2500), //scrap paper
                        new GameItem(2528, 20),
                        new GameItem(2528, 20),
                        new GameItem(2528, 20),
                        new GameItem(33136, 5),
                        new GameItem(33136, 5),
                        new GameItem(33136, 5),
                        new GameItem(993, 5),
                        new GameItem(993, 5),
                        new GameItem(993, 5),
                        new GameItem(24365, 2),
                        new GameItem(24365, 2),
                        new GameItem(22093, 2),
                        new GameItem(4185, 2),
                        new GameItem(4185, 2),
                        new GameItem(6792, 2),
                        new GameItem(24365, 2),
                        new GameItem(6828, 1)





                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(

                        new GameItem(11681, 4000), //scrap paper
                        new GameItem(11681, 4000), //scrap paper
                        new GameItem(11681, 4000), //scrap paper
                        new GameItem(2528, 20),
                        new GameItem(2528, 20),
                        new GameItem(2528, 20),
                        new GameItem(33136, 5),
                        new GameItem(33136, 5),
                        new GameItem(33136, 5),
                        new GameItem(4185, 2),
                        new GameItem(4185, 2),
                        new GameItem(6792, 2),
                        new GameItem(993, 5),
                        new GameItem(993, 5),
                        new GameItem(993, 5),
                        new GameItem(24365, 2),
                        new GameItem(24365, 2),
                        new GameItem(24365, 2),
                        new GameItem(13438, 2),
                        new GameItem(13438, 2),
                        new GameItem(13438, 2),
                        new GameItem(6828, 2),
                        new GameItem(6828, 2),
                        new GameItem(6828, 2),
                        new GameItem(22093, 2),
                        new GameItem(6805, 1)

                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(11681, 7500), //scrap paper
                        new GameItem(11681, 7500), //scrap paper
                        new GameItem(11681, 7500), //scrap paper
                        new GameItem(6828, 3), //scrap paper
                        new GameItem(6828, 3), //scrap paper
                        new GameItem(6828, 3), //scrap paper
                        new GameItem(13346, 1), //scrap paper
                        new GameItem(11739, 1),
                        new GameItem(12582, 1),
                        new GameItem(19891, 1),
                        new GameItem(12579, 1),
                        new GameItem(8167, 1),
                        new GameItem(4185, 2),
                        new GameItem(4185, 2),
                        new GameItem(6792, 2),
                        new GameItem(4185, 2),
                        new GameItem(4185, 2),
                        new GameItem(6792, 2),
                        new GameItem(6831, 1)
                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(
                        new GameItem(31014, 1),
                        new GameItem(6829, 1),
                        new GameItem(2400, 10),
                        new GameItem(19897, 1),
                        new GameItem(13346, 1),
                        new GameItem(13346, 1),
                        new GameItem(13346, 1),
                        new GameItem(13346, 1),
                        new GameItem(13346, 1),
                        new GameItem(13346, 1),
                        new GameItem(13346, 1),
                        new GameItem(13346, 1)
                ));
    }

    private static GameItem randomChestRewards() {
        int rng = Misc.random(1000);
        List<GameItem> itemList = (rng > 950 ? getItems().get(LootRarity.RARE) : getItems().get(LootRarity.COMMON));
        return Misc.getRandomItem(itemList);
    }
    private static final int KEY = 13302;
    private static final int ANIMATION = 881;
    //Object ID = 43486;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 18; i++) {
            int pointz = 0;
            for (int ii = 0; ii < 32; ii++) {
                int rng = Misc.trueRand(1);
                int points = Misc.random(500, 2000);
                if (rng == 1) {
                    points /= 2;
                }
                pointz += points;
            }
            System.out.println(pointz);
        }
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return getItems();
    }

    @Override
    public void roll(Player c) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            GameItem reward = randomChestRewards();

            for (GameItem gameItem : getItems().get(LootRarity.RARE)) {
                if (gameItem.getId() == reward.getId()) {
                    PlayerHandler.executeGlobalMessage("@bla@[<col=7f0000>Pet Collector@bla@] <col=990000>" + c.getDisplayName() + "@bla@ has just received a <col=990000>" + reward.getDef().getName() + ".");
                    break;
                }
            }
            c.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());
    }
}
