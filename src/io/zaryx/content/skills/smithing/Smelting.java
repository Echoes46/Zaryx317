package io.zaryx.content.skills.smithing;

import io.zaryx.Server;
import io.zaryx.content.achievement.AchievementType;
import io.zaryx.content.achievement.Achievements;
import io.zaryx.content.taskmaster.TaskMasterKills;
import io.zaryx.model.Items;
import io.zaryx.model.SoundType;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.ItemAssistant;
import io.zaryx.util.Misc;

import java.util.Objects;

public class Smelting {

	public enum Bars {
		BRONZE(436, 438, 2349, 1, 6, 2405, true, "bronze"),
		IRON(440, -1, 2351, 15, 13, 2406, false, "iron"),
		SILVER(442, -1, 2355, 20, 14, 2407, false, "silver"),
		STEEL(440, 453, 2353, 30, 18, 2409, true, "steel"),
		GOLD(444, -1, 2357, 40, 23, 2410, false, "gold"),
		MITHRIL(447, 453, 2359, 50, 30, 2411, true, "mithril"),
		ADAMANT(449, 453, 2361, 70, 38, 2412, true, "adamant"),
		RUNE(451, 453, 2363, 85, 50, 2413, true, "rune"),
		LOVAKITE(13356, 453, 13354, 85, 50, 2414, true, "lovakite");
//		BRONZENOTED(437, 439, 2350, 1, 6, 2415, true, "bronzenoted"),
//		IRONNOTED(441, -1, 2352, 15, 13, 2416, false, "ironnoted"),
//		SILVERNOTED(442, -1, 2356, 20, 14, 2417, false, "silvernoted"),
//		STEELNOTED(441, 454, 2354, 30, 18, 2419, true, "steelnoted"),
//		GOLDNOTED(445, -1, 2358, 40, 23, 2420, false, "goldnoted"),
//		MITHRILNOTED(448, 454, 2360, 50, 30, 2421, true, "mithrilnoted"),
//		ADAMANTNOTED(450, 454, 2362, 70, 38, 2422, true, "adamantnoted"),
//		RUNENOTED(452, 454, 2364, 85, 50, 2423, true, "runenoted");

		private final int ore1;
		private final int ore2;
		private final int bar;
		private final int req;
		private final int exp;
		private final int frame;
		private final boolean twoOres;
		private final String type;

		public int getOre1() {
			return ore1;
		}

		public int getOre2() {
			return ore2;
		}

		public int getBar() {
			return bar;
		}

		public int getReq() {
			return req;
		}

		int getExp() {
			return exp;
		}

		int getFrame() {
			return frame;
		}

		public boolean twoOres() {
			return twoOres;
		}

		String getType() {
			return type;
		}

		Bars(int ore, int ore2, int bar, int req, int xp, int frame, boolean two, String type) {
			this.ore1 = ore;
			this.ore2 = ore2;
			this.bar = bar;
			this.req = req;
			this.exp = xp;
			this.frame = frame;
			this.twoOres = two;
			this.type = type;
		}

		public static Bars forType(String type) {
			for (Bars bar : values())
				if (bar.getType().equals(type))
					return bar;
			return null;
		}
	}

	/**
	 * Starts Smelting
	 */
	public static void startSmelting(Player c, String type, String amount, String usage) {
		c.barType = type;
		c.smeltAmount = getAmount(amount);
		c.bar = Bars.forType(c.barType);
		if(c.bar == null) //Making sure when the "bar" is null it returns
			return;
		boolean hasItems;
		if (c.bar.twoOres()) {
			hasItems = hasItems(c, c.bar.getOre1(), c.bar.getOre2(), c.bar.getBar());
		} else {
			hasItems = hasItems(c, c.bar.getOre1(), -1, c.bar.getBar());
		}
		if (hasItems) {
			if (hasReqLvl(c, c.bar.getReq(), c.bar.getBar())) {
				startCycle(c, usage);
			}
		}
	}

	public static void startCycle(final Player c, String usage) {
		c.getPA().stopSkilling();
		Server.getEventHandler().submit(new Event<Player>("skilling", c, 1) {

			public void execute() {
				if (attachment == null || attachment.isDisconnected()) {
					this.stop();
					return;
				}
				if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
					attachment.getInterfaceEvent().execute();
					super.stop();
					return;
				}
				if (c.lastSmelt <= 0 || System.currentTimeMillis() - c.lastSmelt >= 1000) {
					if (c.smeltAmount <= 0) {
						stop();
						return;
					}
					appendDelay(c, usage);
				}
			}

			@Override
			public void stop() {
				super.stop();
				if (attachment == null || attachment.isDisconnected()) {
					return;
				}
				resetSmelting(c);
			}
		});
	}

	/**
	 * Applies Smelting delay
	 */
	private static void appendDelay(Player c, String usage) {
		boolean goldSmithGaunts = c.getItems().isWearingItem(776) && c.bar.getBar() == 2357;
		double percentOfXp = (c.bar.getExp() / 2) + Smithing.getSmithsEquipmentCount(c);
		c.getPA().sendSound(2725, SoundType.AREA_SOUND);
		if (c.smeltAmount > 0) {
			boolean hasItems;
			if (c.bar.twoOres())
				hasItems = hasItems(c, c.bar.getOre1(), c.bar.getOre2(), c.bar.getBar());
			else
				hasItems = hasItems(c, c.bar.getOre1(), -1, c.bar.getBar());
			if (hasItems) {

				for (TaskMasterKills taskMasterKills : c.getTaskMaster().taskMasterKillsList) {
					if (taskMasterKills.getDesc().equalsIgnoreCase("Smelt @whi@Rune Bars") && c.bar.getBar() == Bars.RUNE.getBar()) {
						taskMasterKills.incrementAmountKilled(1);
						c.getTaskMaster().trackActivity(c, taskMasterKills);
					} else if (taskMasterKills.getDesc().equalsIgnoreCase("Smelt @whi@Bars")) {
						taskMasterKills.incrementAmountKilled(1);
						c.getTaskMaster().trackActivity(c, taskMasterKills);
					}
				}

				c.sendSpamMessage("You retrieve a bar of");

				Achievements.increase(c, AchievementType.SMITH, 1);
				c.getPA().sendSound(469);
				if (c.bar.twoOres()) {
					if (!Objects.equals(usage, "INFERNAL")) {
						c.startAnimation(899);
					}
					c.getItems().deleteItem(c.bar.getOre1(), 1);
					c.getItems().deleteItem(c.bar.getOre2(), 1);
					int amt = (c.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33077) && Misc.random(0,100) <= 10) ? 2 : 1;
					c.getItems().addItem(c.bar.getBar(), amt);
					c.getPA().addSkillXPMultiplied((int) (Objects.equals(usage, "INFERNAL") ? c.bar.getExp() / 2 : c.bar.getExp() + (goldSmithGaunts ? percentOfXp : 0)), Player.playerSmithing, true);
				} else {
					if (!Objects.equals(usage, "INFERNAL")) {
						c.startAnimation(899);
					}
					c.getItems().deleteItem(c.bar.getOre1(), 1);
					if ((c.bar.getBar() == 2351 && Misc.random(100) >= 50) && c.playerEquipment[Player.playerRing] != 2568)
						c.sendMessage("The ore is too impure and you fail to refine it.");
					else {
						int amt = (c.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33077) && Misc.random(0,100) <= 10) ? 2 : 1;
						c.getItems().addItem(c.bar.getBar(), amt);
						c.getPA().addSkillXPMultiplied((int) (usage == "INFERNAL" ? c.bar.getExp() / 2 : c.bar.getExp() + (goldSmithGaunts ? percentOfXp : 0)), Player.playerSmithing, true);
					}
				}
			}
		} else {
			resetSmelting(c);
			c.getPA().removeAllWindows();
			return;
		}
		c.lastSmelt = System.currentTimeMillis();
		c.smeltAmount--;
		c.getPA().removeAllWindows();
	}

	/**
	 * Resets Smelting variables
	 */
	public static void resetSmelting(Player c) {
		c.smeltAmount = 0;
		c.barType = "";
		c.bar = null;
		c.isSmelting = false;
		c.lastSmelt = 0;
		CycleEventHandler.getSingleton().stopEvents(c, c.smeltEventId);
	}

	/**
	 * Checks if the player has the required level
	 */
	public static boolean hasReqLvl(Player c, int req, int bar) {
		int level = c.getPA().getLevelForXP(c.playerXP[Player.playerSmithing]);
		if (level >= req)
			return true;
		else
			c.sendMessage("You need a Smithing level of " + req + " to smelt a " + ItemAssistant.getItemName(bar));
		resetSmelting(c);
		return false;
	}

	/**
	 * Checks if the player has the required items
	 */
	public static boolean hasItems(Player c, int firstOre, int secondOre, int createdBar) {
		String oreOne = String.valueOf(firstOre);
		String oreTwo = String.valueOf(secondOre);
		String barOutcome = String.valueOf(createdBar);
		if (secondOre > 0 && c.getItems().playerHasItem(Items.INFERNAL_PICKAXE) || secondOre > 0 && c.getItems().isWearingItem(Items.INFERNAL_PICKAXE)) {
			return true;
		}
		if (secondOre > 0) {
			if (c.getItems().playerHasItem(firstOre) && c.getItems().playerHasItem(secondOre))
				return true;
		} else {
			if (c.getItems().playerHasItem(firstOre))
				return true;
		}
		if (secondOre > 0)
			c.sendMessage("You need " + oreOne + " and " + oreTwo + " to smelt a " + barOutcome);
		else
			c.sendMessage("You need " + oreOne + " to smelt a " + barOutcome);
			resetSmelting(c);
		return false;
	}

	/**
	 * Gets the smelting amount
	 */
	private static int getAmount(String amount) {
		if (Objects.equals(amount, "ONE"))
			return 28;
		if (Objects.equals(amount, "FIVE"))
			return 5;
		if (Objects.equals(amount, "TEN"))
			return 10;
		if (Objects.equals(amount, "ALL"))
			return 28;
		
		return -1;
	}

}
