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
 * @author Sponge
 */

public class MiniSmb extends MysteryBoxLootable {

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
//                        new GameItem(11681),  //scrap paper
//                        new GameItem(11681),  //scrap paper
//                        new GameItem(11681),  //scrap paper
//
//                        new GameItem(6679),   //mini mystery
//                        new GameItem(6677),    //Mini smb
//
//
//                new GameItem(11730, 2),   //Overload (4)
//                new GameItem(11730, 2),   //Overload (4)
//
//                new GameItem(11212, 10), //Dragon arrows
//                new GameItem(11212, 10), //Dragon arrows
//
//                new GameItem(11230, 100), //Dragon darts
//                new GameItem(11230, 100), //Dragon darts
//
//                new GameItem(21948, 10), //Dragonston dragon bolts (e)
//                new GameItem(21948, 10), //Dragonston dragon bolts (e)
//
//                new GameItem(21348, 10), //amethyst
//                new GameItem(21348, 10), //amethyst
//
//                new GameItem(452, 10),   //Rune ore
//                new GameItem(452, 10),   //Rune ore
//
//                new GameItem(2364, 10),  //Rune bars
//                new GameItem(2364, 10),  //Rune bars
//
//
//                new GameItem(892, 25),   //Rune Arrow
//                new GameItem(892, 25),   //Rune Arrow
//
//                new GameItem(9144, 25),  //runite bolts
//                new GameItem(9144, 25)  //runite bolts
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
//                        new GameItem(11681),  //scrap paper
//                        new GameItem(11681),  //scrap paper
//                        new GameItem(11681),  //scrap paper
//
//                        new GameItem(6679),   //mini mystery
//                        new GameItem(6677),   //Mini smb
//
//                        new GameItem(384, 10),   //shark
//                        new GameItem(384, 10),   //shark
//                        new GameItem(384, 10),   //shark
//                        new GameItem(384, 10),   //shark
//                        new GameItem(384, 10),   //shark
//                        new GameItem(384, 10),   //shark
//                        new GameItem(384, 10),   //shark
//                        new GameItem(384, 10),   //shark
//                        new GameItem(384, 10),   //shark
//                        new GameItem(13442, 5),  //angler
//                        new GameItem(13442, 5),  //angler
//                        new GameItem(13442, 5),  //angler
//                        new GameItem(13442, 5),  //angler
//                        new GameItem(13442, 5),  //angler
//                        new GameItem(13442, 5),  //angler
//                        new GameItem(13442, 5),  //angler
//                        new GameItem(13442, 5),  //angler
//                        new GameItem(13442, 5),  //angler
//
//                        new GameItem(452, 2),    //runite ore
//                        new GameItem(452, 2),    //runite ore
//                        new GameItem(452, 2),    //runite ore
//
//                        new GameItem(2364, 2),   //runite bar
//                        new GameItem(2364, 2),   //runite bar
//                        new GameItem(2364, 2),   //runite bar
//
//                        new GameItem(23745),   //divine magic
//                        new GameItem(23745),   //divine magic
//                        new GameItem(23745),   //divine magic
//
//                        new GameItem(23733),   //divine range
//                        new GameItem(23733),   //divine range
//                        new GameItem(23733),   //divine range
//
//                        new GameItem(23721),   //divine defence
//                        new GameItem(23721),   //divine defence
//                        new GameItem(23721),   //divine defence
//
//                        new GameItem(23709),   //divine strength
//                        new GameItem(23709),   //divine strength
//                        new GameItem(23709),   //divine strength
//
//                        new GameItem(23697),    //divine attack
//                        new GameItem(23697),    //divine attack
//                        new GameItem(23697),    //divine attack
//
//                        new GameItem(4716),     //dharok helm
//                        new GameItem(4720),     //dharok body
//                        new GameItem(4722),     //dharok legs
//                        new GameItem(4718),     //dharok axe
//
//                        new GameItem(11838),    //Sara sword
//                        new GameItem(11838),    //Sara sword
//                        new GameItem(11838)     //Sara sword
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
//                        new GameItem(11681),  //scrap paper
//                        new GameItem(11681),  //scrap paper
//                        new GameItem(11681),  //scrap paper
//                        new GameItem(6679),   //mini mystery
//                        new GameItem(6677),   //Mini smb
//
//                        new GameItem(4151),   //Whip
//                        new GameItem(4151),   //Whip
//                        new GameItem(4151),    //Mini smb
//
//
//                        new GameItem(11730, 2),   //Overload (4)
//                        new GameItem(11730, 2),   //Overload (4)
//
//                        new GameItem(11212, 10), //Dragon arrows
//                        new GameItem(11212, 10), //Dragon arrows
//
//                        new GameItem(11230, 100), //Dragon darts
//                        new GameItem(11230, 100), //Dragon darts
//
//                        new GameItem(21948, 10), //Dragonston dragon bolts (e)
//                        new GameItem(21948, 10), //Dragonston dragon bolts (e)
//
//                        new GameItem(21348, 10), //amethyst
//                        new GameItem(21348, 10), //amethyst
//
//                        new GameItem(452, 10),   //Rune ore
//                        new GameItem(452, 10),   //Rune ore
//
//                        new GameItem(2364, 10),  //Rune bars
//                        new GameItem(2364, 10),  //Rune bars
//
//
//                        new GameItem(892, 25),   //Rune Arrow
//                        new GameItem(892, 25),   //Rune Arrow
//
//                        new GameItem(9144, 25),  //runite bolts
//                        new GameItem(9144, 25),  //runite bolts
//                        new GameItem(6585),   //Fury
//                        new GameItem(6585),   //Fury
//                        new GameItem(6585),   //Fury
//
//                        new GameItem(12002),  //Ocult
//                        new GameItem(12002),  //Ocult
//                        new GameItem(12002)   //Ocult
                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(
//                        new GameItem(691),    //10k Nomad
//                        new GameItem(692),    //25k Nomad
//
//                        new GameItem(11681),  //scrap paper
//                        new GameItem(11681),  //scrap paper
//                        new GameItem(11681),  //scrap paper
//                        new GameItem(6679),   //mini mystery
//                        new GameItem(6678),   //Mini Umb
//
//                        new GameItem(11826),  //armadyl helm
//                        new GameItem(11828),  //armadyl body
//                        new GameItem(11830),  //armadyls legs
//                        new GameItem(11785),  //armadyl crossbow
//                        new GameItem(11832),  //bandos chestplate
//                        new GameItem(11834),  //bandos tassets
//
//                        new GameItem(11804),  //Bgs
//                        new GameItem(11808),  //Zgs
//                        new GameItem(11806),  //Sgs
//                        new GameItem(11802),  //Ags
//                        new GameItem(23776),  //Hespori key
//
//                        new GameItem(13302),  //Bank key
//                        new GameItem(13239),  //primordial boots
//                        new GameItem(13235),  //eternal boots
//                        new GameItem(13237),  //pegasian boots
//                        new GameItem(6889),   //mages book
//
//                        new GameItem(4084),   //sled
//                        new GameItem(4084),   //sled
//
//                        new GameItem(12785),  //Row (i)
//                        new GameItem(12785),  //Row (i)
//                        new GameItem(12785),    //Mini smb
//
//
//                        new GameItem(11730, 2),   //Overload (4)
//                        new GameItem(11730, 2),   //Overload (4)
//
//                        new GameItem(11212, 10), //Dragon arrows
//                        new GameItem(11212, 10), //Dragon arrows
//
//                        new GameItem(11230, 100), //Dragon darts
//                        new GameItem(11230, 100), //Dragon darts
//
//                        new GameItem(21948, 10), //Dragonston dragon bolts (e)
//                        new GameItem(21948, 10), //Dragonston dragon bolts (e)
//
//                        new GameItem(21348, 10), //amethyst
//                        new GameItem(21348, 10), //amethyst
//
//                        new GameItem(452, 10),   //Rune ore
//                        new GameItem(452, 10),   //Rune ore
//
//                        new GameItem(2364, 10),  //Rune bars
//                        new GameItem(2364, 10),  //Rune bars
//
//
//                        new GameItem(892, 25),   //Rune Arrow
//                        new GameItem(892, 25),   //Rune Arrow
//
//                        new GameItem(9144, 25),  //runite bolts
//                        new GameItem(9144, 25)  //runite bolts
//                        ,    //Mini smb
//
//
//                        new GameItem(11730, 2),   //Overload (4)
//                        new GameItem(11730, 2),   //Overload (4)
//
//                        new GameItem(11212, 10), //Dragon arrows
//                        new GameItem(11212, 10), //Dragon arrows
//
//                        new GameItem(11230, 100), //Dragon darts
//                        new GameItem(11230, 100), //Dragon darts
//
//                        new GameItem(21948, 10), //Dragonston dragon bolts (e)
//                        new GameItem(21948, 10), //Dragonston dragon bolts (e)
//
//                        new GameItem(21348, 10), //amethyst
//                        new GameItem(21348, 10), //amethyst
//
//                        new GameItem(452, 10),   //Rune ore
//                        new GameItem(452, 10),   //Rune ore
//
//                        new GameItem(2364, 10),  //Rune bars
//                        new GameItem(2364, 10),  //Rune bars
//
//
//                        new GameItem(892, 25),   //Rune Arrow
//                        new GameItem(892, 25),   //Rune Arrow
//
//                        new GameItem(9144, 25),  //runite bolts
//                        new GameItem(9144, 25)  //runite bolts
                ));
    }

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public MiniSmb(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 6677;
    }


    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }
}
