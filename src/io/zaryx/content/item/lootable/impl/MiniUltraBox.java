package io.zaryx.content.item.lootable.impl;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.MysteryBoxLootable;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.zaryx.content.item.lootable.LootRarity.VERY_RARE;

/**
 * Revamped a simple means of receiving a random item based on chance.
 *
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 */


public class MiniUltraBox extends MysteryBoxLootable {

    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
     */
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();


    /**
     * Stores an array of items into each map with the corresponding rarity to the list
     */
    static {
        items.put(LootRarity.COMMON,
                Arrays.asList(
                        new GameItem(6679, 10),
                        new GameItem(33220),
                        new GameItem(33220),
                        new GameItem(33221),
                        new GameItem(33221),
                        new GameItem(33222),
                        new GameItem(33222),
                        new GameItem(26219),
                        new GameItem(25739),
                        new GameItem(27275),
                        new GameItem(25731),
                        new GameItem(20997),
                        new GameItem(25398),
                        new GameItem(25389),
                        new GameItem(33226),
                        new GameItem(33226),
                        new GameItem(25401)



                ));
        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(6679, 10),
                        new GameItem(22999),
                        new GameItem(11481),
                        new GameItem(28173),
                        new GameItem(28169),
                        new GameItem(28171),
                        new GameItem(28869),
                        new GameItem(28338),
                        new GameItem(26992),
                        new GameItem(26990),
                        new GameItem(26903),
                        new GameItem(33233),
                        new GameItem(33231),
                        new GameItem(33231),
                        new GameItem(33220),
                        new GameItem(25692),
                        new GameItem(25818)

                ));

        items.put(LootRarity.RARE,
                Arrays.asList(

                        new GameItem(11433),
                        new GameItem(11429),
                        new GameItem(33216),
                        new GameItem(33216),
                        new GameItem(33217),
                        new GameItem(33217),
                        new GameItem(29999),
                        new GameItem(29999),
                        new GameItem(29999),
                        new GameItem(33812),
                        new GameItem(33812),
                        new GameItem(33806),
                        new GameItem(33806),
                        new GameItem(786)


                        ));

        items.put(VERY_RARE,
                Arrays.asList(
                        new GameItem(33800),
                        new GameItem(33802),
                        new GameItem(33804),
                        new GameItem(33810),
                        new GameItem(33800),
                        new GameItem(33802),
                        new GameItem(33804),
                        new GameItem(33810),
                        new GameItem(33186),
                        new GameItem(33187),
                        new GameItem(33188),
                        new GameItem(33183),
                        new GameItem(33009),
                        new GameItem(33001),
                        new GameItem(33002),
                        new GameItem(28919),
                        new GameItem(33806),
                        new GameItem(33808),
                        new GameItem(33814),
                        new GameItem(33005),
                        new GameItem(28583),
                        new GameItem(26939),

                        new GameItem(761)
                ));
    }

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public MiniUltraBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 6678;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }
}