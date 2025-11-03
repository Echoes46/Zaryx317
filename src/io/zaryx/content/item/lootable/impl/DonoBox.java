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
 */


public class DonoBox extends MysteryBoxLootable {

    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
     */
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {
        items.put(LootRarity.COMMON,
                Arrays.asList(



                        new GameItem(20786),  //Row (i5)
                        new GameItem(20786),  //Row (i5)
                        new GameItem(13302),
                        new GameItem(11832),
                        new GameItem(11834),
                        new GameItem(11836),

                        new GameItem(11826),
                        new GameItem(11828),
                        new GameItem(11830),

                        new GameItem(11785),

                        new GameItem(10557),  //Collector
                        new GameItem(10557),  //Collector

                        new GameItem(10556),  //Attacker
                        new GameItem(10556),  //Attacker

                        new GameItem(10558),  //Defender

                        new GameItem(6769)    //$5
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(2403),   //$10

                        new GameItem(10559),  //Healer

                        new GameItem(12817),  //Elysian
                        new GameItem(12817),  //Elysian

                        new GameItem(24423),  //Harmonised nightmare staff
                        new GameItem(24423),  //Harmonised nightmare staff

                        new GameItem(24425),  //Eldritch nightmare staff
                        new GameItem(24425),  //Eldritch nightmare staff

                        new GameItem(24424),  //Volatile nightmare staff
                        new GameItem(24424),  //Volatile nightmare staff

                        new GameItem(26235)   //Zaryte vembraces



                ));

        items.put(LootRarity.RARE,
                Arrays.asList(

                        new GameItem(22091, 1),
                        new GameItem(22091, 1),
                        new GameItem(22091, 1),
                        new GameItem(22091, 1),
                        new GameItem(2396),   //$25
                        new GameItem(12582),  //Cox box
                        new GameItem(19891),  //Tob box
                        new GameItem(12579),  //Arbo box

                        new GameItem(22325),  //scythe
                        new GameItem(20997),  //Tbow
                        new GameItem(26374),  //Zaryte c'bow

                        new GameItem(33144),
                        new GameItem(33145),
                        new GameItem(33146),

                        new GameItem(33141),
                        new GameItem(33142),
                        new GameItem(33143),

                        new GameItem(28338),

                        new GameItem(30021)  //Roc






                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(
                        new GameItem(786),    //$50

                        new GameItem(26382),  //Torva helm
                        new GameItem(26382),  //Torva helm

                        new GameItem(26384),  //Torva body
                        new GameItem(26384),  //Torva body

                        new GameItem(26386),  //Torva legs
                        new GameItem(26386),  //Torva legs
                        new GameItem(25736),  //Holy scythe

                        new GameItem(30022),  //Kratos
                        new GameItem(30022),  //Kratos

                        new GameItem(33112),  //Pot of gold

                        new GameItem(33122),  //Pure skills
                        new GameItem(33122),  //Pure skills

                        new GameItem(28869),

                        new GameItem(25731),  //Holy sanguinesti staff
                        new GameItem(25731),  //Holy sanguinesti staff

                        new GameItem(25734),  //Holy ghrazi rapier
                        new GameItem(25734),  //Holy ghrazi rapier

                        new GameItem(27251),   //Elidinis' ward (f)
                        new GameItem(22087)
                        ));
    }

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public DonoBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 12588;
    }


    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }
}