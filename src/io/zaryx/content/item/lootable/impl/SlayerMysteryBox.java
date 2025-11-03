package io.zaryx.content.item.lootable.impl;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.MysteryBoxLootable;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import QuickUltra.Rarity;

/**
 * Revamped a simple means of receiving a random item based on chance.
 *
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 */
public class SlayerMysteryBox extends MysteryBoxLootable {

	/**
	 * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
	 */
	private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

	/**
	 * Stores an array of items into each map with the corresponding rarity to the list
	 */
	static {
		items.put(LootRarity.COMMON, Arrays.asList(
				new GameItem(26882),
				new GameItem(1618, Misc.random(20, 50)),//uncut diamond
				new GameItem(1620, Misc.random(20, 50)),//uncut ruby
				new GameItem(693, 3),//nomad
				new GameItem(454, Misc.random(25, 50)),//coal
				new GameItem(445, Misc.random(25, 50)),//gold
				new GameItem(441, Misc.random(25, 50)),//iron ore
				new GameItem(2354, Misc.random(25, 50)),//steel bar
				new GameItem(378, Misc.random(25, 50)),//lobster
				new GameItem(372, Misc.random(25, 50)),//swordfish
				new GameItem(384, Misc.random(15, 25)),//shark
				new GameItem(5295, Misc.random(2, 5)),//rannar seed
				new GameItem(378, Misc.random(25, 50)),//lobster
				new GameItem(372, Misc.random(25, 50)),//swordfish
				new GameItem(384, Misc.random(15, 25)),//shark
				new GameItem(5295, Misc.random(2, 5)),//rannar seed

				new GameItem(13307, 250), // bm
				new GameItem(11681, 50), // scrap
				new GameItem(28416, 1), // tier 1 key
				new GameItem(28417, 1), // tier 2 key
				new GameItem(7629, 1), // slayer scroll
				new GameItem(21046, 2), // chest scroll
				new GameItem(990, 2),//ckey
				new GameItem(13438, 1),//slayer chest
				new GameItem(536, 15),
				new GameItem(2841, 1),//bxp scroll
				new GameItem(12934, Misc.random(10, 50))));//zulrah scale

		/*
		 * Tier 2
		 */
		items.put(LootRarity.UNCOMMON, Arrays.asList(
				new GameItem(26882),
				new GameItem(26882),
				new GameItem(26882),

				new GameItem(1618, Misc.random(50, 100)),//uncut diamond
				new GameItem(1620, Misc.random(50, 100)),//uncut ruby
				new GameItem(454, Misc.random(50, 100)),//coal
				new GameItem(441, Misc.random(50, 100)),//iron ore
				new GameItem(7945, Misc.random(50, 100)),//monkfish
				new GameItem(384, Misc.random(50, 100)),//shark
				new GameItem(390, Misc.random(50, 100)),//manta
				new GameItem(693, 8),//nomad
				new GameItem(693, 8),//nomad
				new GameItem(696, 2),//nomad

				new GameItem(449, Misc.random(40, 60)),// addy ore
				new GameItem(447, Misc.random(50, 100)),//steel bar
				new GameItem(1514, Misc.random(50, 100)),// magic logs
				new GameItem(1618, Misc.random(50, 100)),//uncut diamond
				new GameItem(1620, Misc.random(50, 100)),//uncut ruby
				new GameItem(454, Misc.random(50, 100)),//coal
				new GameItem(441, Misc.random(50, 100)),//iron ore
				new GameItem(7945, Misc.random(50, 100)),//monkfish
				new GameItem(384, Misc.random(50, 100)),//shark
				new GameItem(390, Misc.random(50, 100)),//manta
				new GameItem(449, Misc.random(40, 60)),// addy ore
				new GameItem(447, Misc.random(50, 100)),//steel bar
				new GameItem(1514, Misc.random(50, 100)),// magic logs
				new GameItem(11232, Misc.random(50, 100)),//dragon dart tip
				new GameItem(5295, Misc.random(5, 12)),//rannar seed
				new GameItem(5300, Misc.random(5, 12)),//snap seed
				new GameItem(7937, Misc.random(250, 300)),//pure ess

				new GameItem(13307, 500),
				new GameItem(11681, 100),
				new GameItem(28416, 1), // tier 1 key
				new GameItem(28417, 1), // tier 2 key
				new GameItem(7629, 2), // slayer scroll
				new GameItem(21046, 3), // chest scroll
				new GameItem(13438, 1),//slayer chest
				new GameItem(24364, 1),//is scoll
				new GameItem(12696, 1),//super comb
				new GameItem(536, 20),//dbones
				new GameItem(30002, 1),//resource
				new GameItem(2528, 1),//lamp

				new GameItem(12934, Misc.random(150, 250))));//zulrah scale

		/*
		 * Tier 3
		 */
		items.put(LootRarity.RARE, Arrays.asList(
				new GameItem(26883),

				new GameItem(28416, 2), // tier 1 key
				new GameItem(28417, 2), // tier 2 key

				new GameItem(13307, 1000),//bm
				new GameItem(13307, 1000),
				new GameItem(13307, 1000),
				new GameItem(13307, 1000),
				new GameItem(13307, 1000),

				new GameItem(11681, 200),//scrap
				new GameItem(11681, 200),//scrap
				new GameItem(11681, 200),//scrap
				new GameItem(11681, 200),//scrap
				new GameItem(11681, 200),//scrap

				new GameItem(993, 1), // sinister
				new GameItem(6199, 1),//mbox
				new GameItem(11840, 1),//d boots
				new GameItem(6585, 1),//fury
				new GameItem(7462, 1),//b gloves
				new GameItem(33075, 1),//wildy perk
				new GameItem(33136, 1),//relic key
				new GameItem(23933, 2),//crystals
				new GameItem(12785, 1),//row i
				new GameItem(12954, 1),//defender
				new GameItem(10551, 1)));//elite void





		/*
		 * Tier 4
		 */
		items.put(LootRarity.VERY_RARE, Arrays.asList(
				new GameItem(26884),
				new GameItem(26883),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(4151, 1),//whip
				new GameItem(26545, 1),//elite void
				new GameItem(12004, 1)));//tent

	}

	/**
	 * Constructs a new myster box to handle item receiving for this player and this player alone
	 *
	 * @param player the player
	 */
	public SlayerMysteryBox(Player player) {
		super(player);
	}

	@Override
	public int getItemId() {
		return 13438;
	}

	@Override
	public Map<LootRarity, List<GameItem>> getLoot() {
		return items;
	}
}