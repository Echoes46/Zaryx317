package io.zaryx.content.collection_log;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.zaryx.Server;
import io.zaryx.content.achievement.AchievementType;
import io.zaryx.content.achievement.Achievements;
import io.zaryx.content.bosses.nightmare.NightmareConstants;
import io.zaryx.content.item.lootable.impl.*;
import io.zaryx.content.items.aoeweapons.AoeWeapons;
import io.zaryx.content.minigames.arbograve.ArbograveConstants;
import io.zaryx.content.trails.RewardLevel;
import io.zaryx.content.trails.TreasureTrailsRewardItem;
import io.zaryx.content.trails.TreasureTrailsRewards;
import io.zaryx.content.upgrade.UpgradeMaterials;
import io.zaryx.model.Npcs;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.definitions.NpcDef;
import io.zaryx.model.entity.npc.pets.PetHandler;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.group.GroupIronmanGroup;
import io.zaryx.model.entity.player.mode.wildygroup.GroupWildyManGroup;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 * @author Grant_ | www.rune-server.ee/members/grant_ | 10/7/19
 *
 */
public class CollectionLog {

	private static final Logger logger = LoggerFactory.getLogger(CollectionLog.class);

	public static final int PETS_ID = 5;

	/**
	 * Different tabs within interface
	 *
	 */
	public enum CollectionTabType {
		BOSSES, WILDERNESS, RAIDS, MINIGAMES, OTHER
	}

	/* Variables */
	public static HashMap<CollectionTabType, ArrayList<Integer>> collectionNPCS;
	private static final int INTERFACE_ID = 23110;

	private boolean groupIronman;

	private boolean groupWildyman;
	private String saveName;
	private CollectionLog linked;

	private HashMap<String, ArrayList<GameItem>> collections;

	public CollectionLog() {
		this.collections = new HashMap<>();
	}

	public String getSaveDirectory() {
		if (isGroupIronman()) {
			return Server.getSaveDirectory() + "/gim/collection_log/";
		}
		return Server.getSaveDirectory() + "/collection_log/";
	}

	public HashMap<String, ArrayList<GameItem>> getCollections() {
		return collections;
	}

	/** Current requirements, shared by the item grid, completed names and reward claims. */
	public List<GameItem> getRequiredItems(int npcId) {
		List<GameItem> drops;
		if (npcId == 7554) drops = RaidsChestRare.getRareDrops();
		else if (npcId >= 1 && npcId <= 4)
			drops = TreasureTrailsRewardItem.toGameItems(TreasureTrailsRewards.getRewardsForType(npcId));
		else if (npcId == PETS_ID) drops = PetHandler.getPetIds(true);
		else if (npcId >= 6 && npcId <= 9) {
			drops = new ArrayList<>();
			UpgradeMaterials.UpgradeType[] types = {UpgradeMaterials.UpgradeType.WEAPON,
					UpgradeMaterials.UpgradeType.ARMOUR, UpgradeMaterials.UpgradeType.ACCESSORY,
					UpgradeMaterials.UpgradeType.MISC};
			for (UpgradeMaterials value : UpgradeMaterials.values()) {
				if (value.isRare() && value.getType() == types[npcId - 6]) drops.add(value.getReward());
			}
		} else if (npcId == 10) {
			drops = new ArrayList<>();
			for (AoeWeapons value : AoeWeapons.values()) drops.add(new GameItem(value.ID));
		} else if (npcId == Npcs.THE_MAIDEN_OF_SUGADINTI) drops = TheatreOfBloodChest.getRareDrops();
		else if (npcId == 1101) drops = ArbograveChestItems.getRareDrops();
		else if (npcId == 8583) drops = HesporiChestItems.getRareDrops();
		else drops = Server.getDropManager().getNPCdrops(npcId);
		return uniqueRequirements(drops);
	}

	static List<GameItem> uniqueRequirements(Collection<GameItem> drops) {
		Map<Integer, GameItem> unique = new LinkedHashMap<>();
		if (drops != null) for (GameItem item : drops) {
			if (item != null && item.getId() >= 0) unique.putIfAbsent(item.getId(), item.copy());
		}
		return new ArrayList<>(unique.values());
	}

	static int obtainedCount(Collection<GameItem> required, Collection<GameItem> obtained) {
		Set<Integer> unlocked = new HashSet<>();
		if (obtained != null) for (GameItem item : obtained) {
			if (item != null && item.getAmount() > 0) unlocked.add(item.getId());
		}
		int count = 0;
		for (GameItem item : uniqueRequirements(required)) if (unlocked.contains(item.getId())) count++;
		return count;
	}

	static boolean isComplete(Collection<GameItem> required, Collection<GameItem> obtained) {
		List<GameItem> unique = uniqueRequirements(required);
		return !unique.isEmpty() && obtainedCount(unique, obtained) == unique.size();
	}

	public boolean isComplete(int npcId) {
		return isComplete(getRequiredItems(npcId), getUnlocked(npcId));
	}

	static String completionName(String name, boolean complete) {
		return (complete ? "@gre@" : "") + name;
	}

	private String getLogName(int npcId) {
		if (npcId >= 1 && npcId <= 4) return RewardLevel.VALUES.get(npcId).getFormattedName() + " clue scroll";
		switch (npcId) {
			case PETS_ID: return "Pets";
			case 6: return "Weapon Upgrades";
			case 7: return "Armor Upgrades";
			case 8: return "Accessory Upgrades";
			case 9: return "Misc Upgrades";
			case 10: return "Aoe Weapons";
			case Npcs.THE_MAIDEN_OF_SUGADINTI: return "Theatre of Blood";
			case 1101: return "Arbograve Swamp";
			case Npcs.DUSK_9: return "Grotesque Guardians";
			case 1230: return "Perkfinder Minigame";
			case 8583: return "Hespori";
			default: return Misc.optimizeText(NpcDef.forId(npcId).getName());
		}
	}

	private void refreshName(Player player, int npcId, boolean complete) {
		String name = completionName(getLogName(npcId), complete);
		if (collectionNPCS != null && player.collectionLogTab != null) {
			List<Integer> npcs = collectionNPCS.get(player.collectionLogTab);
			int index = npcs == null ? -1 : npcs.indexOf(npcId);
			if (index >= 0) player.getPA().sendFrame126(name, 23123 + index * 2);
		}
		if (player.getCollectionLogNPC() == npcId) player.getPA().sendFrame126(name, 23118);
	}

	/**
	 * Initializes the default npcs to be collecting for
	 */
	public static void init() {
		try {
			Path path = Paths.get(Server.getDataDirectory() + "/cfg/collection_npcs.json");
			File file = path.toFile();

			JsonParser parser = new JsonParser();
			if (!file.exists()) {
				return;
			}
			Object obj = parser.parse(new FileReader(file));
			JsonObject jsonUpdates = (JsonObject) obj;

			Type listType = new TypeToken<HashMap<CollectionTabType, ArrayList<Integer>>>() {
			}.getType();

			collectionNPCS = new Gson().fromJson(jsonUpdates, listType);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No default NPCs found!");
			collectionNPCS = new HashMap<>();
		}
	}

	/**
	 * Opens the interface for a player
	 */
	public void openInterface(Player c) {
		if (c.getPosition().inWild()
				|| Server.getMultiplayerSessionListener().inAnySession(c)
				|| Boundary.isIn(c, Boundary.DUEL_ARENA)
				|| Boundary.isIn(c, Boundary.FIGHT_CAVE)
				|| c.getPosition().inClanWarsSafe()
				|| Boundary.isIn(c, Boundary.INFERNO)
				|| c.getInstance() != null
				|| Boundary.isIn(c, NightmareConstants.BOUNDARY)
				|| Boundary.isIn(c, Boundary.OUTLAST_AREA)
				|| Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA)
				|| Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
				|| Boundary.isIn(c, Boundary.FOREST_OUTLAST)
				|| Boundary.isIn(c, Boundary.SNOW_OUTLAST)
				|| Boundary.isIn(c, Boundary.BOUNTY_HUNTER_OUTLAST)
				|| Boundary.isIn(c, Boundary.ROCK_OUTLAST)
				|| Boundary.isIn(c, Boundary.FALLY_OUTLAST)
				|| Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST)
				|| Boundary.isIn(c, new Boundary(3117, 3640, 3137, 3644))
				|| Boundary.isIn(c, new Boundary(3114, 3611, 3122, 3639))
				|| Boundary.isIn(c, new Boundary(3122, 3633, 3124, 3639))
				|| Boundary.isIn(c, new Boundary(3122, 3605, 3149, 3617))
				|| Boundary.isIn(c, new Boundary(3122, 3617, 3125, 3621))
				|| Boundary.isIn(c, new Boundary(3144, 3618, 3156, 3626))
				|| Boundary.isIn(c, new Boundary(3155, 3633, 3165, 3646))
				|| Boundary.isIn(c, new Boundary(3157, 3626, 3165, 3632))
				|| Boundary.isIn(c, Boundary.SWAMP_OUTLAST)
				|| Boundary.isIn(c, Boundary.WG_Boundary)
				|| Boundary.isIn(c, Boundary.PEST_CONTROL_AREA)
				|| Boundary.isIn(c, Boundary.RAIDS)
				|| Boundary.isIn(c, Boundary.OLM)
				|| Boundary.isIn(c, Boundary.RAID_MAIN)
				|| Boundary.isIn(c, Boundary.XERIC)
				|| Boundary.isIn(c, ArbograveConstants.ALL_BOUNDARIES)) {
			return;
		}

		c.setViewingCollectionLog(this);
		resetInterface(c);
		selectTab(c, CollectionTabType.BOSSES);
		//selectCell(0, CollectionTabType.BOSSES);
	}

	public void openInterfaceOther(Player player, Player c2) {
		player.setViewingCollectionLog(c2.getCollectionLog());
		resetInterface(player);
		selectTab(player, CollectionTabType.BOSSES);
		//selectCell(0, CollectionTabType.BOSSES);
	}

	/**
	 * Clears the interface
	 */
	public void resetInterface(Player player) {
		for(int i = 0; i < 50; i++) {
			player.getPA().sendFrame126("", 23123 + (i * 2));
			player.getPA().sendConfig(520 + i, 0);
		}
		player.getPA().sendConfig(519, 0);
		for(int i = 0; i < 3; i++) {
			player.getPA().sendConfig(571 + i, 0);
		}
	}

	/**
	 * Selects a tab within the interface
	 * @param type
	 */
	public void selectTab(Player player, CollectionTabType type) {
		if (collectionNPCS == null || collectionNPCS.isEmpty()) {
			return;
		}

		ArrayList<Integer> npcs = collectionNPCS.get(type);
		if (npcs != null) {
			resetInterface(player);
			player.collectionLogTab = type;
			player.previousSelectedCell = 0;
			player.getPA().sendConfig(player.previousSelectedTab == 0 ? 519 : 570 + player.previousSelectedTab, 0);
			player.previousSelectedTab = type.ordinal();
			player.getPA().sendConfig(type.ordinal() == 0 ? 519 : 570 + type.ordinal(), 1);
			for (int i = 0; i < npcs.size(); i++) {
				int npcId = npcs.get(i);
				player.getPA().sendFrame126(completionName(getLogName(npcId), isComplete(npcId)), 23123 + i * 2);
			}
			selectCell(player, 0, type);
		} else {
			player.sendMessage("There are no collection logs for this type yet.");
		}
	}

	/**
	 * Selects a cell from a tab type
	 * @param index
	 * @param type
	 */
	public void selectCell(Player player, int index, CollectionTabType type) {
		if (collectionNPCS == null || collectionNPCS.isEmpty()) {
			return;
		}

		ArrayList<Integer> npcs = collectionNPCS.get(type);
		if (npcs != null) {
			if (index < 0 || index >= npcs.size()) {
				return;
			}

			player.getPA().sendConfig(520 + player.previousSelectedCell, 0);
			player.previousSelectedCell = index;
			player.getPA().sendConfig(520 + index , 1);
			player.getPA().resetScrollBar(23121);

			if (npcs.get(index) == PETS_ID && this == player.getCollectionLog()) {
				List<GameItem> pets = PetHandler.getPetIds(false);
				for(GameItem petItem : pets) {
					if (player.getItems().getItemCount(petItem.getId(), false) > 0 || (player.hasFollower && player.petSummonId == petItem.getId())) {
						PetHandler.Pets petForItem = PetHandler.forItem(petItem.getId());
						if (petForItem != null) {
							PetHandler.Pets pet = PetHandler.getPetForParentId(petForItem);
							ArrayList<GameItem> petList = getCollections().get("" + 5);
							if (petList == null || petList.stream().noneMatch(item -> item.getId() == pet.getItemId())) {
								player.getCollectionLog().handleDrop(player, 5, pet.getItemId(), 1);
								player.sendMessage("@red@Added missing " + ItemDef.forId(pet.getItemId()).getName() + " to collection log.");
							}
						}
					}
				}
			}
			populateInterface(player, npcs.get(index));
		}
	}

	/**
	 * Populates the interface with data
	 * @param npcId
	 */
	public void populateInterface(Player player, int npcId) {
		if (!getCollections().containsKey("" + npcId)) { //If they've never looked at that NPC before, initialize a blank arraylist
			getCollections().put("" + npcId, new ArrayList<>());
			saveToJSON();
		}

		player.setCollectionLogNPC(npcId);

		String npcName = getLogName(npcId);

		player.getPA().sendFrame126(getSaveName() + "'s Collection Log", 23112);
		player.getPA().sendFrame126(Misc.optimizeText(npcName) /*+ "@gre@("+getPoints(npcId)+" Credits)"*/, 23118);
		player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + player.getNpcDeathTracker().getKc(npcId >= 1 && npcId <= 4 ? RewardLevel.VALUES.get(npcId).getFormattedName() : npcName), 23120);

		//Clear items
		for(int i = 0; i < 198; i++) {
			player.getPA().itemOnInterface(-1, 0, 23231, i);
		}

		for (int i = 0; i < 5; i++) {
			player.getPA().itemOnInterface(new GameItem(-1,0), 23235, i);
		}

		for (int i = 0; i < CollectionRewards.getForNpcID(npcId).size(); i++) {
			player.getPA().itemOnInterface(new GameItem(CollectionRewards.getForNpcID(npcId).get(i).getId(),CollectionRewards.getForNpcID(npcId).get(i).getAmount()), 23235, i);
		}

		ArrayList<GameItem> items = getCollections().get(npcId + "");

		player.dropItems = getRequiredItems(npcId);
		if (npcId == 7554) player.getPA().sendFrame126(npcName + ": @whi@" + player.raidCount, 23120);
		if (npcId >= PETS_ID && npcId <= 10)
			player.getPA().sendFrame126(npcName + ": @whi@" + obtainedCount(player.dropItems, items), 23120);
		if (npcId == Npcs.THE_MAIDEN_OF_SUGADINTI)
			player.getPA().sendFrame126(npcName + ": @whi@" + player.tobCompletions, 23120);
		if (npcId == 1101) player.getPA().sendFrame126(npcName + ": @whi@" + player.arboCompletions, 23120);

		int foundCount = 0;
		for(int i = 0; i < player.dropItems.size(); i++) {
			boolean found = false;
			for(int j = 0; j < items.size(); j++) {
				if (items.get(j).getId() == player.dropItems.get(i).getId() && items.get(j).getAmount() > 0) {
					player.getPA().itemOnInterface(items.get(j).getId(),items.get(j).getAmount(),23231,i);
					foundCount++;
					found = true;
					break;
				}
			}
			if (!found) {
				player.getPA().itemOnInterface(player.dropItems.get(i).getId(),0,23231,i);
			}
		}
		boolean complete = isComplete(player.dropItems, items);
		refreshName(player, npcId, complete);
		player.getPA().sendFrame126("Obtained: " + (complete ? "@gre@" : "@red@") + foundCount + "/" + player.dropItems.size(), 23119);
		player.getPA().showInterface(INTERFACE_ID);
	}

	public void handleDrop(Player player, int npcId, int dropId, int dropAmount) {
		handleDrop(player, npcId, dropId, dropAmount, true);
	}

	/**
	 * Handles and NPC dropping an item
	 * @param npcId
	 * @param dropId
	 * @param dropAmount
	 */
	public void handleDrop(Player player, int npcId, int dropId, int dropAmount, boolean message) {
		if (linked != null) {
			linked.handleDrop(player, npcId, dropId, dropAmount, false);
		}

		if (npcId == 2043 || npcId == 2044) { //All zulrahs
			npcId = 2042;
		}
		if (npcId == 965) {
			npcId = 963;
		}
		if (npcId == 963) {
			npcId = 965;
		}
		if (npcId == 7144  || npcId == 7146) {
			npcId = 7145;
		}

		if (npcId == 8615 || npcId == 8619 || npcId == 8620 || npcId == 8622) {
			npcId = 8621;
		}

		if (npcId == 7851) {
			npcId = 7888;
		}
		if (npcId == 1233 || npcId == 1234 || npcId == 1235 || npcId == 1230
				||npcId == 1231  || npcId == 1232 || npcId == 1227 || npcId == 1228
				||npcId == 1229 ) {
			npcId = 1230;
		}

		//Pets
		if (npcId == PETS_ID) {
			dropId = PetHandler.getPetForParentId(PetHandler.forItem(dropId)).getItemId();
		}

		String npcName = getLogName(npcId);

		if (!isCollectionNPC(npcId)) {
			return;
		}

		boolean wasComplete = isComplete(npcId);
		ArrayList<GameItem> currentItems = getCollections().get("" + npcId);
		if (currentItems == null) {
			currentItems = new ArrayList<>();
			currentItems.add(new GameItem(dropId, dropAmount));
			if (message) {
				player.sendMessage("You have unlocked another item in your collection log!");
				player.getPA().sendNotification("Collection Log", ItemDef.forId(dropId).getName() + " Unlocked", npcName, dropId);
			}
			Achievements.increase(player, AchievementType.COLLECTOR, 1);

		} else {
			boolean found = false;
			for(int i = 0; i < currentItems.size(); i++) {
				if (currentItems.get(i).getId() == dropId) {
					currentItems.get(i).setAmount(currentItems.get(i).getAmount() + dropAmount);
					found = true;
					break;
				}
			}

			if (!found) {
				currentItems.add(new GameItem(dropId, dropAmount));
				if (message) {
					player.sendMessage("You have unlocked another item in your collection log!");
					player.getPA().sendNotification("Collection Log", ItemDef.forId(dropId).getName() + " Unlocked", npcName, dropId);
					if (!player.getAchievements().isComplete(Achievements.Achievement.Collector)) {
						Achievements.increase(player, AchievementType.COLLECTOR, 1);
					}
				}

			}
		}
		getCollections().put("" + npcId, currentItems);
		boolean complete = isComplete(npcId);
		if (message && !wasComplete && complete) player.sendMessage("@gre@You have completed the " + npcName + " collection log!");
		if (player.getViewingCollectionLog() == this) refreshName(player, npcId, complete);
		//As soon as it gets a drop it saves Kraken has been getting the most complaints
		saveToJSON();
	}

	/**
	 * Checks if an NPC is in fact a collection NPC
	 * @param npcId
	 * @return
	 */
	public boolean isCollectionNPC(int npcId) {
		if (collectionNPCS == null) return false;
		for (Map.Entry<CollectionTabType, ArrayList<Integer>> entry : collectionNPCS.entrySet()) {
			for(int i = 0; i < entry.getValue().size(); i++) {
				if (entry.getValue().get(i) == npcId) {
					return true;
				}
			}
		}
		return false;
	}

	public ArrayList<GameItem> getUnlocked(int npcId) {
		return collections.getOrDefault(String.valueOf(npcId), Lists.newArrayList());
	}

	/**
	 * Gets the amount of unique items unlocked.
	 * Doesn't count item amounts or repeat items in different collection log tabs/categories.
	 */
	public int getUniquesUnlocked() {
		HashSet<Integer> uniques = new HashSet<>();

		for (List<GameItem> items : collections.values()) {
			for (GameItem item : items) {
				uniques.add(item.getId());
			}
		}

		return uniques.size();
	}

	/**
	 * Handles all buttons on the interface
	 * @param buttonId
	 * @return
	 */
	public boolean handleActionButtons(Player player, int buttonId) {
		if (buttonId >= 90082 && buttonId <= 90180) {
			int index = (buttonId - 90082) / 2;
			player.getViewingCollectionLog().selectCell(player, index, player.collectionLogTab);
			return true;
		}
		switch(buttonId) {
			case 90076:
				player.getViewingCollectionLog().selectTab(player, CollectionTabType.BOSSES);
				return true;
			case 90182:
				player.getViewingCollectionLog().selectTab(player, CollectionTabType.WILDERNESS);
				return true;
			case 90184:
				player.getViewingCollectionLog().selectTab(player, CollectionTabType.RAIDS);
				return true;
			case 90186:
				player.getViewingCollectionLog().selectTab(player, CollectionTabType.MINIGAMES);
				return true;
			case 90188:
				player.getViewingCollectionLog().selectTab(player, CollectionTabType.OTHER);
				return true;
			case 90073:
				player.getPA().closeAllWindows();
				return true;
		}
		return false;
	}

	/**
	 * Saves users collection to a JSON file
	 */
	public void saveToJSON() {
		if (getSaveName() == null) {
			logger.error("No name set for collection log to save.");
			return;
		}
		Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = prettyGson.toJson(getCollections());
		BufferedWriter bw;
		try {
			if (!new File(getSaveDirectory()).exists()) {
				Preconditions.checkState(new File(getSaveDirectory()).mkdirs());
			}
			bw = new BufferedWriter(new FileWriter(new File(getSaveDirectory() + getSaveName().toLowerCase() + ".json")));
			bw.write(prettyJson);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadForPlayer(Player player) {
		setGroupIronman(false);
		setSaveName(player.getLoginNameLower());
		loadCollections(); // Load collection for non-group ironman players
	}

	public void loadForGroupIronman(GroupIronmanGroup group) {
		setGroupIronman(true);
		setSaveName(group.getName().toLowerCase());
		loadCollections();
	}

	public void loadforGroupWildyman(GroupWildyManGroup group) {
		setGroupWildy(true);
		setSaveName(group.getName().toLowerCase());
		loadCollections();
	}

	public static void combineforGroupWildyMan(Player player, GroupWildyManGroup group) {
		if (group.getMergedCollectionLogs().contains(player.getLoginNameLower())) {
			return;
		}

		group.getMergedCollectionLogs().add(player.getLoginNameLower());

		HashMap<String, ArrayList<GameItem>> groupEntries = group.getCollectionLog().getCollections();
		HashMap<String, ArrayList<GameItem>> playerEntries = player.getCollectionLog().getCollections();

		if (!playerEntries.isEmpty()) {
			for (Map.Entry<String, ArrayList<GameItem>> entry : playerEntries.entrySet()) {
				ArrayList<GameItem> groupWildyItems = groupEntries.get(entry.getKey());

				if (groupWildyItems == null) {
					groupEntries.put(entry.getKey(), entry.getValue());
					logger.debug("Putting full entry onto group collection log because it doesn't exist in group collection log {}", entry);
					continue;
				}

				main: for (GameItem playerItem : entry.getValue()) {
					for (GameItem groupItem : groupWildyItems) {
						if (playerItem.getId() == groupItem.getId()) {
							groupItem.setAmount(playerItem.getAmount() + groupItem.getAmount());
							logger.debug("Combined player and group item to create new amount {}, originalGroupItem={}, originalPlayerItem={}", groupItem, groupItem, playerItem);
							continue main;
						}
					}

					groupWildyItems.add(playerItem);
					logger.debug("Added new group item from player collection log {}", playerItem);
				}
			}

			group.getCollectionLog().saveToJSON();
		}
	}

	/**
	 * Group ironman was released without a group collection log, and therefore people were filling
	 * up their collection logs individually. We needed to combine team members collection logs together
	 * for the release.
	 * TODO delete on re-release
	 */
	public static void combineForGroupIronman(Player player, GroupIronmanGroup group) {
		if (group.getMergedCollectionLogs().contains(player.getLoginNameLower())) {
			return;
		}

		group.getMergedCollectionLogs().add(player.getLoginNameLower());

		HashMap<String, ArrayList<GameItem>> groupEntries = group.getCollectionLog().getCollections();
		HashMap<String, ArrayList<GameItem>> playerEntries = player.getCollectionLog().getCollections();

		if (!playerEntries.isEmpty()) {
			for (Map.Entry<String, ArrayList<GameItem>> entry : playerEntries.entrySet()) {
				ArrayList<GameItem> groupItems = groupEntries.get(entry.getKey());

				if (groupItems == null) {
					groupEntries.put(entry.getKey(), entry.getValue());
					logger.debug("Putting full entry onto group collection log because it doesn't exist in group collection log {}", entry);
					continue;
				}

				main: for (GameItem playerItem : entry.getValue()) {
					for (GameItem groupItem : groupItems) {
						if (playerItem.getId() == groupItem.getId()) {
							groupItem.setAmount(playerItem.getAmount() + groupItem.getAmount());
							logger.debug("Combined player and group item to create new amount {}, originalGroupItem={}, originalPlayerItem={}", groupItem, groupItem, playerItem);
							continue main;
						}
					}

					groupItems.add(playerItem);
					logger.debug("Added new group item from player collection log {}", playerItem);
				}
			}

			group.getCollectionLog().saveToJSON();
		}
	}

	private Path getPlayerSaveFilePath() {
		return Paths.get(getSaveDirectory() + getSaveName().toLowerCase() + ".json");
	}

	/**
	 * Loads a users collection data
	 */
	public void loadCollections() {
		try {
			File file = getPlayerSaveFilePath().toFile();

			JsonParser parser = new JsonParser();
			if (!file.exists()) {
				return;
			}
			Object obj = parser.parse(new FileReader(file));
			JsonObject jsonUpdates = (JsonObject) obj;

			Type listType = new TypeToken<HashMap<String, ArrayList<GameItem>>>() {
			}.getType();

			collections = new Gson().fromJson(jsonUpdates, listType);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No collections found!");
			collections = new HashMap<>();
		}
	}

	public boolean isGroupWildy() {
		return groupWildyman;
	}

	public void setGroupWildy(boolean groupWildyman) {
		this.groupWildyman = groupWildyman;
	}

	public boolean isGroupIronman() {
		return groupIronman;
	}

	public void setGroupIronman(boolean groupIronman) {
		this.groupIronman = groupIronman;
	}

	public String getSaveName() {
		return saveName;
	}

	public void setSaveName(String saveName) {
		this.saveName = saveName;
	}

	public CollectionLog getLinked() {
		return linked;
	}

	public void setLinked(CollectionLog linked) {
		this.linked = linked;
	}
}
