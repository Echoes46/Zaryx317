package io.zaryx.content;


import io.zaryx.Server;
import io.zaryx.content.skills.hunter.impling.ItemRarity;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Right;
import io.zaryx.model.items.GameItem;
import io.zaryx.model.items.ItemAssistant;
import io.zaryx.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author C.T
 *
 */

/*
 * Handling for the Slayer Chest
 */

public class SlayerChest {

	/*
	 * Item Id's & Animations
	 */
	private static final int MYSTERY_BOX = 6199;
	private static final int KEY1 = 28416;// TIER 1 SLAYER KEY
	private static final int KEY2 = 28417;// TIER 2 SLAYER KEY
	private static final int KEY3 = 28418;// TIER 3 SLAYER KEY
	private static final int KEY4 = 28419;// TIER 4 SLAYER KEY

	public final static double MYSTERY_BOX_CHANCE = 65;//M BOX CHANCE

	public final static double SLAYER_SCROLL_CHANCE = 15;//SLAYER SCROLL CHANCE

	private static final int ANIMATION = 881;

	private static final Map<ItemRarity, List<GameItem>> items = new HashMap<>();

	static {

		/*
		 * Tier 1
		 */
		items.put(ItemRarity.COMMON, Arrays.asList(
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
		items.put(ItemRarity.UNCOMMON, Arrays.asList(
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
				new GameItem(28418, 1), // tier 3 key
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
		items.put(ItemRarity.RARE, Arrays.asList(
				new GameItem(26883),

				new GameItem(28416, 2), // tier 1 key
				new GameItem(28417, 2), // tier 2 key
				new GameItem(28418, 2), // tier 3 key
				new GameItem(28419, 1), // tier 4 key

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
		items.put(ItemRarity.VERY_RARE, Arrays.asList(
				new GameItem(2528, 3),
				new GameItem(2528, 3),
				new GameItem(33136),
				new GameItem(33136),
				new GameItem(33136),
				new GameItem(33136),
				new GameItem(6889),
				new GameItem(6889),
				new GameItem(6889),
				new GameItem(11235),
				new GameItem(11235),
				new GameItem(6570),
				new GameItem(6570),
				new GameItem(26884),
				new GameItem(26883),
				new GameItem(28419, 1),//key 4
				new GameItem(28419, 1),//key 4
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
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
				new GameItem(11739, 1),//vote chest
				new GameItem(19496, 1),//zenyte
				new GameItem(19496, 1),//zenyte
				new GameItem(19496, 1),//zenyte
				new GameItem(26545, 1),//elite void
				new GameItem(23943, 1),//signet
				new GameItem(27112, 1),//b gloves wrapped
				new GameItem(13227, 1),//boot crystals
				new GameItem(13229, 1),//peg crystal
				new GameItem(13231, 1),//prim crystal
				new GameItem(11286, 1),
				new GameItem(33078, 1),//perk
				new GameItem(33094, 1),//perk
				new GameItem(33124, 1),//perk
				new GameItem(33075, 1),//perk
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(11681, 250),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(13307, 3000),
				new GameItem(39006),


				new GameItem(12004, 1)));//tent
		/*
		 * Tier 5
		 */
		items.put(ItemRarity.ULTRA_RARE, Arrays.asList(
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess               // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                  // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess               // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                  // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess               // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                  // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess               // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                  // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//				new GameItem(5304, Misc.random(80, 120)),//torstol seed
//				new GameItem(7937, Misc.random(1000, 5000)),//pure ess                 // common
//				new GameItem(12934, Misc.random(600, 750)),//zulrah scale
//
//
//				new GameItem(19547, 1),//zenyte
//				new GameItem(19553, 1),//torture
//				new GameItem(19550, 1),//suffering
//				new GameItem(4151, 1),//whip
//				new GameItem(12927, 1),//serp vis
//				new GameItem(12932, 1),//magic fang
//				new GameItem(13227, 1),//boot crystals
//				new GameItem(13229, 1),//peg crystal
//				new GameItem(13231, 1),//prim crystal
				new GameItem(12004, 1)));//tent
	}

	/*
	 * Handles getting a random item from each list depending on the Tier.
	 */
	private static GameItem randomTier1ChestRewards(int chance) {
		int random = Misc.random(1, 100);
		List<GameItem> itemList = random <= 99 ? items.get(ItemRarity.COMMON) : items.get(ItemRarity.ULTRA_RARE);
		return Misc.getRandomItem(itemList);
	}

	private static GameItem randomTier2ChestRewards(int chance) {
		int random = Misc.random(1, 100);
		List<GameItem> itemList = random <= 98 ? items.get(ItemRarity.UNCOMMON) : items.get(ItemRarity.ULTRA_RARE);
		return Misc.getRandomItem(itemList);
	}

	private static GameItem randomTier3ChestRewards(int chance) {
		int random = Misc.random(1, 100);
		List<GameItem> itemList = random <= 97 ? items.get(ItemRarity.RARE) : items.get(ItemRarity.ULTRA_RARE);
		return Misc.getRandomItem(itemList);
	}

	private static GameItem randomTier4ChestRewards(int chance) {
		int random = Misc.random(1, 100);
		List<GameItem> itemList = random <= 96 ? items.get(ItemRarity.VERY_RARE) : items.get(ItemRarity.ULTRA_RARE);
		return Misc.getRandomItem(itemList);
	}

	public static void searchChest(Player c) {
		//Tier 1
		if (c.getItems().playerHasItem(KEY1)) {
			c.getItems().deleteItem(KEY1, 1);
			c.startAnimation(ANIMATION);
			c.getItems().addItemUnderAnyCircumstance(11681, Misc.random(10, 35));
			GameItem reward = Boundary.isIn(c, Boundary.DONATOR_ZONE) && c.getRights().isOrInherits(Right.Extreme_Donator) ? randomTier1ChestRewards(2) : randomTier1ChestRewards(9);
			if (!c.getItems().addItem(reward.getId(), reward.getAmount())) {
				Server.itemHandler.createGroundItem(c, reward.getId(), c.getX(), c.getY(), c.getHeight(), reward.getAmount());
			}
			c.sendMessage(" You receive a " + ItemAssistant.getItemName(reward.getId()) + " from the Slayer Chest!");
			c.getPA().addSkillXP((18), Player.playerSlayer, true);
		}
		//Tier 2
		else if (c.getItems().playerHasItem(KEY2)) {
			c.getItems().deleteItem(KEY2, 1);
			c.startAnimation(ANIMATION);
			c.getItems().addItemUnderAnyCircumstance(11681, Misc.random(35, 50));
			GameItem reward = Boundary.isIn(c, Boundary.DONATOR_ZONE) && c.getRights().isOrInherits(Right.Extreme_Donator) ? randomTier2ChestRewards(2) : randomTier2ChestRewards(9);
			if (!c.getItems().addItem(reward.getId(), reward.getAmount())) {
				Server.itemHandler.createGroundItem(c, reward.getId(), c.getX(), c.getY(), c.getHeight(), reward.getAmount());
			}
			c.sendMessage(" You receive a " + ItemAssistant.getItemName(reward.getId()) + " from the Slayer Chest!");
			c.getPA().addSkillXP((25), Player.playerSlayer, true);
		}
		//Tier 3
		else if (c.getItems().playerHasItem(KEY3)) {
			c.getItems().deleteItem(KEY3, 1);
			c.startAnimation(ANIMATION);
			c.getItems().addItemUnderAnyCircumstance(11681, Misc.random(50, 70));
			GameItem reward = Boundary.isIn(c, Boundary.DONATOR_ZONE) && c.getRights().isOrInherits(Right.Extreme_Donator) ? randomTier3ChestRewards(2) : randomTier3ChestRewards(9);
			if (!c.getItems().addItem(reward.getId(), reward.getAmount())) {
				Server.itemHandler.createGroundItem(c, reward.getId(), c.getX(), c.getY(), c.getHeight(), reward.getAmount());
			}
			c.sendMessage(" You receive a " + ItemAssistant.getItemName(reward.getId()) + " from the Slayer Chest!");
			PlayerHandler.executeGlobalMessage("@red@" + c.getLoginName() + " @pur@has received @red@" + ItemAssistant.getItemName(reward.getId()) + " @pur@from the @red@Slayer chest!");
			c.getPA().addSkillXP((33), Player.playerSlayer, true);
		}
		//Tier 4
		else if (c.getItems().playerHasItem(KEY4)) {
			c.getItems().deleteItem(KEY4, 1);
			c.startAnimation(ANIMATION);
			c.getItems().addItemUnderAnyCircumstance(995, Misc.random(1000000, 2500000));
			GameItem reward = Boundary.isIn(c, Boundary.DONATOR_ZONE) && c.getRights().isOrInherits(Right.Apex_Donator) ? randomTier4ChestRewards(2) : randomTier4ChestRewards(9);
			if (!c.getItems().addItem(reward.getId(), reward.getAmount())) {
				Server.itemHandler.createGroundItem(c, reward.getId(), c.getX(), c.getY(), c.getHeight(), reward.getAmount());

			}
			c.sendMessage(" You receive a " + ItemAssistant.getItemName(reward.getId()) + " from the Slayer Chest!");
			PlayerHandler.executeGlobalMessage("@red@" + c.getLoginName() + " @pur@has received @red@" + ItemAssistant.getItemName(reward.getId()) + " @pur@from the @red@Slayer chest!");
			c.getPA().addSkillXP((60), Player.playerSlayer, true);
			} else {
				c.getDH().sendStatement("You will need a Slayer key Tier 1-4 to open this.",
						"All Slayer monsters drop Keys, The Higher the tier the lower the chance.");
			}
		}
	}

