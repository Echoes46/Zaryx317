package io.zaryx.content.item.lootable.impl;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.MysteryBoxLootable;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Revamped a simple means of receiving a random item based on chance.
 *
 * @author Junior
 * @date Feb 14, 2024 12:04 AM
 */


public class ArboBox extends MysteryBoxLootable {

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
                            new GameItem(2401, 2),
                            new GameItem(24365, 1),
                            new GameItem(2996, 50),
                            new GameItem(13346, 1)
                    ));
            items.put(LootRarity.UNCOMMON,
                    Arrays.asList(
                            new GameItem(26490),
                            new GameItem(26492),
                            new GameItem(26498),
                            new GameItem(8167),
                            new GameItem(22981),
                            new GameItem(13346, 1),  //Umb
                            new GameItem(12806, 1),
                            new GameItem(12807, 1),
                            new GameItem(22002, 1),
                         new GameItem(6805, 1)
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(26992),
                        new GameItem(26992),
                        new GameItem(26992),
                        new GameItem(26992),
                        new GameItem(696, 40),  //10m Nomad
                        new GameItem(696, 40),  //10m Nomad
                        new GameItem(2400, 2),   //Arbo key
                        new GameItem(2400, 2),   //Arbo key
                        new GameItem(2400, 2),   //Arbo key
                        new GameItem(2400, 2),   //Arbo key
                        new GameItem(2400, 2),   //Arbo key
                        new GameItem(22090, 1),
                        new GameItem(22090, 1),
                        new GameItem(22090, 1),
                        new GameItem(22090, 1),
                        new GameItem(25975, 1),  //Lightbearer ring
                        new GameItem(30014, 1) //K'klilk

                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(

                        new GameItem(696, 1000),  //50m Nomad
                        new GameItem(696, 1000),  //50m Nomad
                        new GameItem(696, 1000),  //50m Nomad
                        new GameItem(12582, 1),   //Coxbox
                        new GameItem(12582, 1),   //Coxbox
                        new GameItem(19891, 1),   //Tobbox
                        new GameItem(19891, 1),   //Tobbox
                        new GameItem(12582, 1),   //Coxbox
                        new GameItem(12582, 1),   //Coxbox
                        new GameItem(19891, 1),   //Tobbox
                        new GameItem(19891, 1),   //Tobbox
                        new GameItem(27285, 1),  //Eye of corrupter
                        new GameItem(27285, 1),  //Eye of corrupter
                        new GameItem(25975, 1),  //Lightbearer ring
                        new GameItem(25975, 1),  //Lightbearer ring
                        new GameItem(25975, 1),  //Lightbearer ring
                        new GameItem(12582, 1),   //Coxbox
                        new GameItem(12582, 1),   //Coxbox
                        new GameItem(19891, 1),   //Tobbox
                        new GameItem(19891, 1),   //Tobbox
                        new GameItem(27285, 1),  //Eye of corrupter
                        new GameItem(27285, 1),  //Eye of corrupter
                        new GameItem(25975, 1),  //Lightbearer ring
                        new GameItem(25975, 1),  //Lightbearer ring
                        new GameItem(25975, 1),  //Lightbearer ring
                        new GameItem(25398, 1),
                        new GameItem(25389, 1),
                        new GameItem(25401, 1),
                        new GameItem(28173, 1),
                        new GameItem(28169, 1),
                        new GameItem(28171, 1),
                        new GameItem(28869, 1),
                        new GameItem(28338, 1)

                        ));
    }

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public ArboBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 12579;
    }


    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items; }
}
