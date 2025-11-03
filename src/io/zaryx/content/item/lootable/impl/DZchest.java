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

public class DZchest implements Lootable {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static Map<LootRarity, List<GameItem>> getItems() {
        return items;
    }

    static {
        items.put(LootRarity.COMMON, Arrays.asList(

                new GameItem(24365, 1),
                new GameItem(30002, 5),
                new GameItem(23686, 30),
                new GameItem(3025, 75),
                new GameItem(6686, 75),
                new GameItem(13442, 250),
                new GameItem(30002, 5),
                new GameItem(23686, 30),
                new GameItem(3025, 75),
                new GameItem(6686, 75),
                new GameItem(13442, 250),
                new GameItem(28418, 2),
                new GameItem(4151, 1),
                new GameItem(11907, 1),
                new GameItem(6828, 1),
                new GameItem(6828, 1),
                new GameItem(6828, 1),
                new GameItem(6828, 1),
                new GameItem(6828, 1),
                new GameItem(13346, 1),
                new GameItem(13346, 1)
        ));


        items.put(LootRarity.RARE, Arrays.asList(   // Decent Reward's only

                new GameItem(12926, 1),  //Blowpipe
                new GameItem(12902, 1),  //Toxic staff
                new GameItem(12929, 1),  //Serpentine helm
                new GameItem(13200, 1),  //Tanzanite mutagen
                new GameItem(13201, 1),  //Magma mutagen
                new GameItem(13239, 1),  //Primordial boots
                new GameItem(13237, 1),  //Pegasian boots
                new GameItem(13235, 1),  //Eternal boots
                new GameItem(12926, 1),  //Blowpipe
                new GameItem(12902, 1),  //Toxic staff
                new GameItem(12929, 1),  //Serpentine helm
                new GameItem(13239, 1),  //Primordial boots
                new GameItem(13237, 1),  //Pegasian boots
                new GameItem(13235, 1),  //Eternal boots


                new GameItem(13233, 1),  //Smouldering stone
                new GameItem(13233, 1),  //Smouldering stone
                new GameItem(13233, 1),  //Smouldering stone

                new GameItem(26500, 1),
                new GameItem(22002, 1),  //Dragonfire ward
                new GameItem(21633, 1),  //Ancient wyvern shield

                new GameItem(22611, 1),  //Vesta spear
                new GameItem(22614, 1),  //Vesta longsword
                new GameItem(22617, 1),  //Vesta chainbody
                new GameItem(22617, 1),  //Vesta chainbody
                new GameItem(22620, 1),  //Vesta chainskirt
                new GameItem(22623, 1),  //Statius warhammer
                new GameItem(22626, 1),  //Statius full helm
                new GameItem(22629, 1),  //Statius platebody
                new GameItem(22632, 1),  //Statius platelegs
                new GameItem(22648, 1),  //Zuriel's staff
                new GameItem(22651, 1),  //Zuriel's hood
                new GameItem(22654, 1),  //Zuriel's robe top
                new GameItem(22657, 1),  //Zuriel's robe bottom
                new GameItem(22639, 1),  //Morrigan's coif
                new GameItem(22642, 1),  //Morrigan's leatherbody
                new GameItem(22645, 1),  //Morrigan's leather chaps
                new GameItem(33111, 1),
                new GameItem(33112, 1),
                new GameItem(26090, 1),
                new GameItem(6805, 1),
                new GameItem(6805, 1),
                new GameItem(6721, 1),
                new GameItem(2401, 2)
        ));
    }
    private static GameItem randomChestRewards() {
        int rng = Misc.random(1000);
        List<GameItem> itemList = (rng > 950 ? getItems().get(LootRarity.RARE) : getItems().get(LootRarity.COMMON));
        return Misc.getRandomItem(itemList);
    }
    private static final int KEY = 2401;
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
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            GameItem reward = randomChestRewards();

            for (GameItem gameItem : getItems().get(LootRarity.RARE)) {
                if (gameItem.getId() == reward.getId()) {
                    PlayerHandler.executeGlobalMessage("@bla@[<col=7f0000>DZ KEY@bla@] <col=990000>" + c.getDisplayName() + "@bla@ has just received a <col=990000>" + reward.getDef().getName() + ".");
                    break;
                }
            }
            c.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());

        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }
}
