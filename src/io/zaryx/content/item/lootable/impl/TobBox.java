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


public class TobBox extends MysteryBoxLootable {

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
                        new GameItem(8167),
                        new GameItem(22981),
                        new GameItem(13346, 1),  //Umb
                        new GameItem(12806, 1),
                        new GameItem(12807, 1),
                        new GameItem(22002, 1),
                        new GameItem(27624) //Ancient sceptre

                ));

        items.put(LootRarity.RARE,
                Arrays.asList(

                        new GameItem(696, 40),  //10m Nomad

                        new GameItem(8125, 1),   //5m Nomad
                        new GameItem(25918),   //Dragon hunter c'bow (b)

                        new GameItem(25904),   //Vamp slayer helm
                        new GameItem(25904),   //Vamp slayer helm
                        new GameItem(25904),   //Vamp slayer helm


                        new GameItem(26990),
                        new GameItem(26990),
                        new GameItem(26990),
                        new GameItem(26990),

                        new GameItem(22477),   //Avernic defender
                        new GameItem(22477),   //Avernic defender
                        new GameItem(22477),   //Avernic defender
                        new GameItem(22477),   //Avernic defender
                        new GameItem(22477),   //Avernic defender

                        new GameItem(22326),   //Justicar helm
                        new GameItem(22327),   //Justicar body
                        new GameItem(22328),   //Justicar legs

                        new GameItem(24664),   //Twisted ancestral hat
                        new GameItem(24664),   //Twisted ancestral hat

                        new GameItem(24666),   //Twisted ancestral top
                        new GameItem(24666),   //Twisted ancestral top

                        new GameItem(22089, 1),
                        new GameItem(22089, 1),
                        new GameItem(22089, 1),


                        new GameItem(24668),   //Twisted ancestral bottom
                        new GameItem(24668)    //Twisted ancestral bottom
                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(

                        new GameItem(696, 1000),  //50m Nomad
                        new GameItem(696, 1000),  //50m Nomad
                        new GameItem(696, 1000),  //50m Nomad

                        new GameItem(12582),              //Cox box
                        new GameItem(12582),              //Cox box
                        new GameItem(12582),              //Cox box
                        new GameItem(12582),              //Cox box
                        new GameItem(19891),              //Tob box
                        new GameItem(19891),              //Tob box
                        new GameItem(19891),              //Tob box
                        new GameItem(19891),              //Tob box
                        new GameItem(12579),              //Arbo box
                        new GameItem(22325),              //Scythe
                        new GameItem(22323),              //Sanguinesti staff

                        new GameItem(22324),              //Ghrazi rapier
                        new GameItem(22324),              //Ghrazi rapier
                        new GameItem(22324),              //Ghrazi rapier
                        new GameItem(22324),              //Ghrazi rapier
                        new GameItem(22324)               //Ghrazi rapier
                ));
    }

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public TobBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 19891;
    }


    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items; }
}
