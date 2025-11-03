package io.zaryx.content.item.lootable.impl;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.Lootable;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import io.zaryx.model.items.ItemAssistant;
import io.zaryx.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Revamped a simple means of receiving a random item based on chance.
 *
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 */
public class VoteMysteryBox implements Lootable {

	/**
	 * The item id of the mystery box required to trigger the event
	 */
	public static final int VOTE_MYSTERY_BOX = 11739;

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
						new GameItem(28416, 1),
						new GameItem(696, 1),     //10k nomad
						new GameItem(989, 5),     //crystal key
						new GameItem(23083, 5)    //Brimstone key
				));

		items.put(LootRarity.UNCOMMON,
				Arrays.asList(
						new GameItem(22093, 1),
						new GameItem(28417, 1),
						new GameItem(24364),  //hespori key
						new GameItem(22374),  //hespori key
						new GameItem(22374),  //hespori key
						new GameItem(2528, 2),
						new GameItem(13438, 1),
						new GameItem(6199, 1),
						new GameItem(23933, 6)


				));
		items.put(LootRarity.RARE,
				Arrays.asList(
						new GameItem(20238, 2),
						new GameItem(28418, 1),
						new GameItem(4185, 2),   //porazdir key
						new GameItem(6792, 2),   //serens key
						new GameItem(4185, 2),   //porazdir key
						new GameItem(6792, 2),   //serens key
						new GameItem(7, 1),
						new GameItem(8, 1),
						new GameItem(10, 1),
						new GameItem(12, 1),
						new GameItem(10551, 1),
						new GameItem(12598, 1),
						new GameItem(26498, 1),
						new GameItem(22943, 1),
						new GameItem(12004)  //kraken tenticle
				));

		items.put(LootRarity.VERY_RARE,
				Arrays.asList(
						new GameItem(28419, 1),
						new GameItem(4185, 2),   //porazdir key
						new GameItem(6792, 2),   //serens key
						new GameItem(4185, 2),   //porazdir key
						new GameItem(6792, 2),   //serens key
						new GameItem(4185, 2),   //porazdir key
						new GameItem(6792, 2),   //serens key
						new GameItem(4185, 2),   //porazdir key
						new GameItem(6792, 2),   //serens key
						new GameItem(4185, 2),   //porazdir key
						new GameItem(6792, 2),   //serens key
						new GameItem(8167),   //nomad chest
						new GameItem(6889),   //mages book
						new GameItem(11907),  //trident
						new GameItem(26482),  //whip (or)
						new GameItem(696, 1)   //250k nomad
				));
	}


	@Override
	public Map<LootRarity, List<GameItem>> getLoot() {
		return items;
	}

	/**
	 * Opens a mystery box if possible, and ultimately triggers and event, if possible.
	 */
	public void roll(Player player) {
		if (System.currentTimeMillis() - player.lastMysteryBox < 600) {
			return;
		}
		if (player.getItems().freeSlots() < 2) {
			player.sendMessage("You need at least two free slots to open a hourly box.");
			return;
		}
		if (!player.getItems().playerHasItem(VOTE_MYSTERY_BOX)) {
			player.sendMessage("You need a hourly box to do this.");
			return;
		}
		player.getItems().deleteItem(VOTE_MYSTERY_BOX, 1);
		player.lastMysteryBox = System.currentTimeMillis();
		int random = Misc.random(100);
		List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON) : random >= 55 && random <= 80 ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
		GameItem item = Misc.getRandomItem(itemList);
		GameItem itemDouble = Misc.getRandomItem(itemList);

		if (Misc.random(10) == 0) {
			player.getItems().addItem(item.getId(), item.getAmount());
			player.getItems().addItem(itemDouble.getId(), itemDouble.getAmount());
			player.sendMessage("You receive <col=255>" + item.getAmount() + " x " + ItemAssistant.getItemName(item.getId()) + "</col>.");
			player.sendMessage("You receive <col=255>" + itemDouble.getAmount() + " x " + ItemAssistant.getItemName(itemDouble.getId()) + "</col>.");
		} else {
			player.getItems().addItem(item.getId(), item.getAmount());
			player.sendMessage("You receive <col=255>" + item.getAmount() + " x " + ItemAssistant.getItemName(item.getId()) + "</col>.");
		}
	}

}