package io.zaryx.content.item.lootable.impl;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.Lootable;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;

import java.util.*;

public class DonoVault implements Lootable {
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static Map<LootRarity, List<GameItem>> getItems() { return items; }

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

                new GameItem(995,5000000),
                new GameItem(13307,30000),
                new GameItem(995,5000000),
                new GameItem(13307,30000),
                new GameItem(13346,1),
                new GameItem(13346,1),
                new GameItem(13346,1),
                new GameItem(13346,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(13346,1),
                new GameItem(13346,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(26884,1),
                new GameItem(26884,1),
                new GameItem(26885,1),
                new GameItem(26885,1),
                new GameItem(24365,1),
                new GameItem(28419,2),
                new GameItem(28419,2),
                new GameItem(23083,2),
                new GameItem(23083,2),
                new GameItem(23776,2),
                new GameItem(23776,2),
                new GameItem(2401,2),
                new GameItem(2400,2),
                new GameItem(22093,2) //end common
        ));

        items.put(LootRarity.UNCOMMON,Arrays.asList(
                new GameItem(13346,1),
                new GameItem(13346,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(22088,1),
                new GameItem(22089,1),
                new GameItem(22090,1),
                new GameItem(22091,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(11739,1),
                new GameItem(11739,1),
                new GameItem(19897,1),
                new GameItem(19897,1),
                new GameItem(13346,1),
                new GameItem(13346,1),
                new GameItem(13346,1),
                new GameItem(13346,1),
                new GameItem(6769,1)


                //end uncommon
        ));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(27012,1),
                new GameItem(26886,1),
                new GameItem(26886,1),
                new GameItem(26886,1),
                new GameItem(11429,1),//pots
                new GameItem(11481,1),
                new GameItem(22999,1),
                new GameItem(11433,1),
                new GameItem(22088,1),
                new GameItem(22089,1),
                new GameItem(22090,1),
                new GameItem(22091,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(6679,1),
                new GameItem(13346,1),
                new GameItem(13346,1),
                new GameItem(13346,1),
                new GameItem(6831,1),
                new GameItem(6831,1),
                new GameItem(6831,1),
                new GameItem(19891,1),
                new GameItem(12582,1),
                new GameItem(19891,1),
                new GameItem(12582,1),
                new GameItem(12579,1),
                new GameItem(12579,1),
                new GameItem(12579,1),
                new GameItem(6829,1),
                new GameItem(6829,1),
                new GameItem(6829,1),
                new GameItem(28951,1),
                new GameItem(33009,1),
                new GameItem(33001,1),
                new GameItem(33002,1),
                new GameItem(33005,1),
                new GameItem(28919,1),

                new GameItem(33217,1),
                new GameItem(33216,1),
                new GameItem(33231,1),
                new GameItem(33221,1),

                new GameItem(2403,1),
                new GameItem(6678,1)

                

                //end rare
        ));
    }

    public static ArrayList<GameItem> getRare() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(LootRarity.RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops) {
                if(drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            }
            if(!foundItem) {
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
    public DonoVault(Player player) {
        super();
    }
    @Override
    public Map<LootRarity, List<GameItem>> getLoot() { return getItems(); }

    @Override
    public void roll(Player player) {

    }
}