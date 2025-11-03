package io.zaryx.content.skills.woodcutting;

import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.SkillcapePerks;
import io.zaryx.content.achievement.AchievementType;
import io.zaryx.content.achievement.Achievements;
import io.zaryx.content.achievement_diary.impl.*;
import io.zaryx.content.bonus.BoostScrolls;
import io.zaryx.content.bosses.hespori.Hespori;
import io.zaryx.content.bosses.wintertodt.Wintertodt;
import io.zaryx.content.event.eventcalendar.EventChallenge;
import io.zaryx.content.events.monsterhunt.CrystalTree;
import io.zaryx.content.prestige.PrestigePerks;
import io.zaryx.content.questing.Quest;
import io.zaryx.content.skills.Skill;
import io.zaryx.content.skills.firemake.Firemaking;
import io.zaryx.content.taskmaster.TaskMasterKills;
import io.zaryx.model.collisionmap.WorldObject;
import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.world.objects.GlobalObject;
import io.zaryx.util.Misc;

import java.util.Optional;

public class WoodcuttingEvent extends Event<Player> {
	private static boolean woodcuttingTree;
	private final Tree tree;
	private final Hatchet hatchet;
	private final int objectId;
	private final int x;
	private final int y;
	private int chops;

	private final int[] lumberjackOutfit = { 10933, 10939, 10940, 10941 };

	public WoodcuttingEvent(Player player, Tree tree, Hatchet hatchet, int objectId, int x, int y) {
		super("skilling", player, 1);
		this.tree = tree;
		this.hatchet = hatchet;
		this.objectId = objectId;
		this.x = x;
		this.y = y;
	}

	@Override
	public void execute() {
		double osrsExperience;
		int pieces = 0;
		pieces = handleOutfit(pieces);
		osrsExperience = tree.getExperience() + tree.getExperience() / 10 * pieces;
		if (BoostScrolls.checkHarvestBoost(attachment)) {
			osrsExperience *= 1.15;
		}
		if (canChop()) return;
		if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
			attachment.getInterfaceEvent().execute();
			super.stop();
			return;
		}
		chops++;

		double chopSpeed = calculateChopSpeed();

		int chopChance = 1 + (int) (tree.getChopsRequired() * chopSpeed);
		if (Boundary.isIn(attachment, Boundary.WOODCUTTING_GUILD_BOUNDARY)) {
			chopChance *= 1.5;
		}
		if (chopChance > 1 && hatchet.equals(Hatchet.INFERNAL_OR)) {
			chopChance = 1;
		}
		for (TaskMasterKills taskMasterKills : attachment.getTaskMaster().taskMasterKillsList) {
			if (taskMasterKills.getDesc().equalsIgnoreCase("Cut @whi@Magic Trees") && tree.equals(Tree.MAGIC)) {
				taskMasterKills.incrementAmountKilled(1);
				attachment.getTaskMaster().trackActivity(attachment, taskMasterKills);
				break;
			} else if (taskMasterKills.getDesc().equalsIgnoreCase("Cut @whi@Yew Trees") && tree.equals(Tree.YEW)) {
				taskMasterKills.incrementAmountKilled(1);
				attachment.getTaskMaster().trackActivity(attachment, taskMasterKills);
				break;
			} else if (taskMasterKills.getDesc().equalsIgnoreCase("Cut @whi@Oak Trees") && tree.equals(Tree.OAK)) {
				taskMasterKills.incrementAmountKilled(1);
				attachment.getTaskMaster().trackActivity(attachment, taskMasterKills);
				break;
			} else if (taskMasterKills.getDesc().equalsIgnoreCase("Cut @whi@Willow Trees") && tree.equals(Tree.WILLOW)) {
				taskMasterKills.incrementAmountKilled(1);
				attachment.getTaskMaster().trackActivity(attachment, taskMasterKills);
				break;
			} else if (taskMasterKills.getDesc().equalsIgnoreCase("Cut @whi@Maple Trees") && tree.equals(Tree.MAPLE)) {
				taskMasterKills.incrementAmountKilled(1);
				attachment.getTaskMaster().trackActivity(attachment, taskMasterKills);
				break;
			}
		}
		foeArtefact1(attachment);
		foeArtefact(attachment);
		if (Boundary.isIn(attachment, Boundary.WILDYMAN_CAVE )) {
			if (Misc.random(20) == 5) {
				int randomAmount = 3;
				attachment.sendMessage("You received " + randomAmount + " pkp while woodcutting!");
				attachment.getItems().addItem(2996, randomAmount);
			}
		}

		foeArtefact(attachment);
		if (Boundary.isIn(attachment, Boundary.WILDYMAN_CAVE )) {
			if (Misc.random(20) == 5) {
				int randomAmount = 10;
				attachment.sendMessage("You received " + randomAmount + " Blood money!");
				attachment.getItems().addItem(13307, randomAmount);
			}
		}
		if (hatchet.equals(Hatchet.DRAGON) || hatchet.equals(Hatchet.DRAGON_OR)) {
			double randomBurn = Misc.random(100);
			if (randomBurn <=  2.5) {
				Firemaking.lightFire(attachment, tree.getWood(), "dragon_axe");
				int firemakingLevel = attachment.playerLevel[Skill.FIREMAKING.getId()];
				int woodcuttingLevel = attachment.playerLevel[Skill.WOODCUTTING.getId()];
				double fireXp = tree.getExperience() * (1 + firemakingLevel / 200.0);
				double woodXp = tree.getExperience() * (1 + woodcuttingLevel / 200.0);
				attachment.getPA().addSkillXPMultiplied((int) fireXp, Skill.FIREMAKING.getId(), true);
				attachment.getPA().addSkillXPMultiplied((int) woodXp, Skill.WOODCUTTING.getId(), true);
				attachment.getItems().deleteItem(tree.getWood(), 1);
			}
			if (attachment.amDonated > 250) {
				if (randomBurn <= 5.5) {
					Firemaking.lightFire(attachment, tree.getWood(), "dragon_axe");
					int firemakingLevel = attachment.playerLevel[Skill.FIREMAKING.getId()];
					int woodcuttingLevel = attachment.playerLevel[Skill.WOODCUTTING.getId()];
					double fireXp = tree.getExperience() * (1 + firemakingLevel / 200.0);
					double woodXp = tree.getExperience() * (1 + woodcuttingLevel / 200.0);
					attachment.getPA().addSkillXPMultiplied((int) fireXp, Skill.FIREMAKING.getId(), true);
					attachment.getPA().addSkillXPMultiplied((int) woodXp, Skill.WOODCUTTING.getId(), true);
					attachment.getItems().deleteItem(tree.getWood(), 1);
				}
			}
		}
		if (tree.equals(Tree.MAGIC)) {
			for (Quest quest : attachment.getQuesting().getQuestList()) {
				if (quest.getName().equalsIgnoreCase("Santa's Troubles") && quest.getStage() == 3 && attachment.getPresentCounter() <= 45) {
					if (Misc.isLucky(75)) {
						attachment.sendMessage("@red@You find a christmas present while chopping the magic tree");
						attachment.setPresentCounter(attachment.getPresentCounter() + 1);
						if (attachment.getPresentCounter() == 45) {
							quest.incrementStage();
							attachment.sendMessage("@red@It looks like you've found all the present in the area, you need to return to santa.");
						}
					}
					break;
				}
			}
		}
		if (tree.equals(Tree.HESPORI)) {
			int randomTele = 1;
			if (attachment.getItems().playerHasItem(Hespori.KEY)) {
				attachment.moveTo(new Position(3099 + randomTele, 3476 + randomTele));
				Hespori.deleteEventItems(attachment);
				return;
			}
			int randomTele2 = Misc.random(2);
			attachment.canLeaveHespori = true;
			attachment.moveTo(new Position(3101, 3497, 0));
			attachment.getItems().addItem(tree.getWood(), 3);
			if ((Configuration.DOUBLE_DROPS_TIMER > 0 || Configuration.DOUBLE_DROPS) && Misc.random(2) == 1) {
				attachment.getItems().addItem(tree.getWood(), 3);
			}
			if (PrestigePerks.hasRelic(attachment, PrestigePerks.TRIPLE_HESPORI_KEYS) && Misc.isLucky(10)) {
				attachment.getItems().addItem(tree.getWood(), 9);
			}
			attachment.getPA().addSkillXPMultiplied((int)osrsExperience, Skill.WOODCUTTING.getId(), true);
			handleRewards();
			Hespori.deleteEventItems(attachment);
			attachment.getPA().addSkillXPMultiplied(330 , 19, true);
			super.stop();
			return;
		}
		if (tree.equals(Tree.CRYSTAL_TREE)) {
			if (!CrystalTree.progress) {
				stop();
				return;
			}
			CrystalTree.removeShards(1);
			CrystalTree.rockCheck();
			if (CrystalTree.CHOPS_REMAINING <= 0) {
				CrystalTree.removeWildCrystalTree(true);
				stop();
				return;
			}
		}
		if (tree.equals(Tree.BRUMA_ROOTS)) {
			if ((SkillcapePerks.WOODCUTTING.isWearing(attachment) || SkillcapePerks.isWearingMaxCape(attachment)) && attachment.getItems().freeSlots() < 2 || attachment.getItems().freeSlots() < 1) {
				attachment.sendMessage("You have run out of free inventory space.");
				super.stop();
				return;
			}
			if (!Wintertodt.started) {
				attachment.sendMessage("There's no need to do that at this time.");
				super.stop();
				return;
			}
			attachment.getItems().addItem(tree.getWood(), SkillcapePerks.WOODCUTTING.isWearing(attachment) || SkillcapePerks.isWearingMaxCape(attachment) ? 2 : 1);
			attachment.getPA().addSkillXPMultiplied(attachment.playerLevel[Skill.WOODCUTTING.getId()] * 4, 8, true);
			attachment.startAnimation(hatchet.getAnimation());
			return;
		}
		if (Misc.random(tree.getChopdownChance()) == 0) {
			int face = 0;
			Optional<WorldObject> worldObject = attachment.getRegionProvider().get(x, y).getWorldObject(objectId, x, y, 0);
			if (worldObject.isPresent()) {
				face = worldObject.get().getFace();
			}
			int stumpId = 0;
			if (tree.equals(Tree.REDWOOD)) {
				face = (attachment.absX < 1568) ? 1 : (attachment.absX > 1573) ? 3 : (attachment.absY < 3480) ? 0 : 2;
				if (objectId == 29668)
					stumpId = 29669;
				else if (objectId == 29670)
					stumpId = 29671;
			}

			Server.getGlobalObjects().add(new GlobalObject(tree.equals(Tree.REDWOOD) ? stumpId : tree.getStumpId(), x, y, attachment.heightLevel, face, 10, tree.getRespawnTime(), objectId));

			attachment.getItems().addItem(tree.getWood(), 1);
			attachment.sendSpamMessage("You get some logs.");
			attachment.getEventCalendar().progress(EventChallenge.CUT_DOWN_X_MAGIC_LOGS);
			attachment.getPA().addSkillXPMultiplied((int)osrsExperience, Skill.WOODCUTTING.getId(), true);
			Achievements.increase(attachment, AchievementType.WOODCUT, 1);
			attachment.getPA().sendSound(2734);
			handleRewards();

			super.stop();
			return;
		}
		if (!tree.equals(Tree.NORMAL)) {
			if (Misc.random(chopChance) == 0 || chops >= tree.getChopsRequired()) {
				chops = 0;
				int random = Misc.random(4);
				attachment.getPA().addSkillXPMultiplied((int) osrsExperience, Skill.WOODCUTTING.getId(), true);
				Achievements.increase(attachment, AchievementType.WOODCUT, 1);
				if ((attachment.getItems().isWearingItem(25066)) || (attachment.getItems().isWearingItem(13241) || attachment.getItems().playerHasItem(13241)) || attachment.getItems().playerHasItem(25066) && random == 2) {
					Firemaking.lightFire(attachment, tree.getWood(), "infernal_axe");
					return;
				}
				handleDiary(tree);
				foeArtefact(attachment);
				handleWildernessRewards();
				handleRewards();
				if ((SkillcapePerks.WOODCUTTING.isWearing(attachment) || SkillcapePerks.isWearingMaxCape(attachment)) && attachment.getItems().freeSlots() < 2) {
					attachment.sendMessage("You have run out of free inventory space.");
					super.stop();
					return;
				}
			}
			if (attachment.playerEquipment[Player.playerWeapon] == 25110 || attachment.playerEquipmentCosmetic[Player.playerWeapon] == 25110) {
				attachment.getItems().addItemToBankOrDrop(tree.getWood(), SkillcapePerks.WOODCUTTING.isWearing(attachment) ||
						SkillcapePerks.isWearingMaxCape(attachment) && attachment.getWoodcuttingEffect() ? 2 : 1);
						attachment.getPA().addSkillXPMultiplied((int)(osrsExperience), Player.playerWoodcutting, true);
			} else {
				if (osrsExperience > 0) {
					attachment.getPA().addSkillXPMultiplied((int)(osrsExperience), Player.playerWoodcutting, true);
				}
				attachment.getItems().addItem(tree.getWood(), SkillcapePerks.WOODCUTTING.isWearing(attachment) ||
						SkillcapePerks.isWearingMaxCape(attachment) && attachment.getWoodcuttingEffect() ? 2: 1);
			}
		}
		attachment.startAnimation(hatchet.getAnimation());
		attachment.getPA().sendSound(472,0,10,0);
	}

	private double calculateChopSpeed() {
		double chopSpeed = hatchet.getChopSpeed();
		int woodcuttingLevel = attachment.playerLevel[Skill.WOODCUTTING.getId()];
		int woodLevel = attachment.playerLevel[Skill.WOODCUTTING.getId()];
		int treeLevel = tree.getLevelRequired();

		// Scaling factors
		double woodBoost = 1 + (woodLevel / 100.0);
		double levelDifferenceFactor = Math.max(0.1, 1 - (treeLevel - woodcuttingLevel) / 100.0);
		double proximityTo99Factor = 1 + ((99 - woodcuttingLevel) / 99.0);

		// Apply scaling factors
		chopSpeed *= woodBoost;
		chopSpeed *= levelDifferenceFactor;
		chopSpeed *= proximityTo99Factor;

		// Slow down chop speed if using a lower level hatchet and player is within 10 levels of tree level requirement
		if (woodcuttingLevel <= treeLevel + 10 && hatchet.getLevelRequired() < treeLevel) {
			chopSpeed *= 0.75; // Reduce chop speed by 25%
		}

		return chopSpeed;
	}

	private int handleOutfit(int pieces) {
		for (int aLumberjackOutfit : lumberjackOutfit) {
			if (attachment.getItems().isWearingItem(aLumberjackOutfit)) {
				pieces += 2;
			}
		}
		return pieces;
	}

	private boolean canChop() {
		if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
			super.stop();
			return true;
		}
		if (!attachment.getItems().playerHasItem(hatchet.getItemId()) && !attachment.getItems().isWearingItem(hatchet.getItemId())) {
			attachment.sendMessage("Your axe has disappeared.");
			super.stop();
			return true;
		}
		if (attachment.playerLevel[Player.playerWoodcutting] < hatchet.getLevelRequired()) {
			attachment.sendMessage("You no longer have the level required to operate this hatchet.");
			super.stop();
			return true;
		}
		if (attachment.getItems().freeSlots() == 0) {
			attachment.sendMessage("You have run out of free inventory space.");
			super.stop();
			return true;
		}
		return false;
	}

	private void handleWildernessRewards() {
		if (Boundary.isIn(attachment, Boundary.RESOURCE_AREA)) {
			if (Misc.random(20) == 5) {
				int randomAmount = 1;
				attachment.sendMessage("You received " + randomAmount + " pkp while woodcutting!");
				attachment.getItems().addItem(2996, randomAmount);
			}
		}
	}

	private void handleDiary(Tree tree) {
		switch (tree) {
			case MAGIC:
				attachment.getEventCalendar().progress(EventChallenge.CUT_DOWN_X_MAGIC_LOGS, 2);
				if (Boundary.isIn(attachment, Boundary.AL_KHARID_BOUNDARY)) {
					attachment.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.CHOP_MAGIC_AL);
				}
				if (Boundary.isIn(attachment, Boundary.RESOURCE_AREA_BOUNDARY)) {
					attachment.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.MAGIC_LOG_WILD);
				}
				if (Boundary.isIn(attachment, Boundary.SEERS_BOUNDARY)) {
					attachment.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.CUT_MAGIC_SEERS);
				}
				break;
			case MAPLE:
				break;
			case NORMAL:
				break;
			case OAK:
				if (Boundary.isIn(attachment, Boundary.LUMRIDGE_BOUNDARY) || Boundary.isIn(attachment, Boundary.DRAYNOR_BOUNDARY)) {
					attachment.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.CHOP_OAK);
				}
				if (Boundary.isIn(attachment, Boundary.RELLEKKA_BOUNDARY)) {
					attachment.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.CHOP_OAK_FREM);
				}
				break;
			case WILLOW:
				if (Boundary.isIn(attachment, Boundary.FALADOR_BOUNDARY)) {
					attachment.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.CHOP_WILLOW);
				}
				if (Boundary.isIn(attachment, Boundary.DRAYNOR_BOUNDARY)) {
					attachment.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.CHOP_WILLOW_DRAY);
				}
				break;
			case YEW:
				if (Boundary.isIn(attachment, Boundary.FALADOR_BOUNDARY)) {
					attachment.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.CHOP_YEW);
				}
				if (Boundary.isIn(attachment, Boundary.VARROCK_BOUNDARY)) {
					attachment.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.YEWS_AND_BURN);
				}
				break;
			case TEAK:
				if (Boundary.isIn(attachment, Boundary.DESERT_BOUNDARY)) {
					attachment.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.CHOP_TEAK);
				}
				break;
			default:
				break;

		}
	}

	private static void foeArtefact(Player player) {
		int chance = 250;
		int artefactRoll = Misc.random(100);
		if (Misc.random(chance) == 1) {
			if (artefactRoll <65) {
				player.getItems().addItemUnderAnyCircumstance(11180, 1);
				player.sendMessage("You found a coin that can be dissolved, speak to Nomad!");
			} else if (artefactRoll >= 65 && artefactRoll <= 99) {
				player.getItems().addItemUnderAnyCircumstance(681, 1);
				player.sendMessage("You found a talisman that can be dissolved, speak to Nomad!");
			} else if (artefactRoll > 99){
				player.getItems().addItemUnderAnyCircumstance(9034, 1);
				PlayerHandler.executeGlobalMessage("@bla@[@red@Woodcutting@bla@]@blu@ " + player.getDisplayName() + " @red@just found a Golden statuette while wcing!");
			}
		}
	}

	private static void foeArtefact1(Player player) {
		int chance = 50;
		int artefactRoll = Misc.random(100);
		if (Misc.random(chance) == 1) {
			if (artefactRoll < 65) {
				player.getItems().addItemUnderAnyCircumstance(691, 1);
				player.sendMessage("You found a Upgrade point certificate (10k)");
			} else if (artefactRoll >= 65 && artefactRoll < 85) {
				player.getItems().addItemUnderAnyCircumstance(692, 1);
				player.sendMessage("You found a Upgrade point certificate (25k)");
			} else if (artefactRoll > 85 && artefactRoll < 95) {
				player.getItems().addItemUnderAnyCircumstance(693, 1);
				player.sendMessage("You found a Upgrade point certificate (50k)");
			} else if (artefactRoll > 95) {
				player.getItems().addItemUnderAnyCircumstance(696, 1);
				player.sendMessage("You found a Upgrade point certificate (250k)");
			}
		}
	}

	private void handleRewards() {
		int dropRate = 10;
		int clueAmount = 1;
		if (attachment.fasterCluesScroll) {
			dropRate = dropRate*2;
		}
		if (Hespori.activeGolparSeed) {
			clueAmount = 2;
		}
		if (Misc.random(tree.getPetChance() / dropRate) == 10) {
			switch (Misc.random(2)) {
				case 0:
					attachment.getItems().addItemUnderAnyCircumstance(19712, clueAmount);
					break;
				case 1:
					attachment.getItems().addItemUnderAnyCircumstance(19714, clueAmount);
					break;
				case 2:
					attachment.getItems().addItemUnderAnyCircumstance(19716, clueAmount);
					break;
			}
			attachment.sendMessage("@blu@You appear to see a clue nest fall from the tree, and pick it up.");
			attachment.sendSpamMessage("A bird's nest falls out of the tree.");
		}

		if (Misc.random(1000) == 1) {
			attachment.getItems().addItemUnderAnyCircumstance(lumberjackOutfit[Misc.random(lumberjackOutfit.length - 1)], 1);
			attachment.sendMessage("You notice a lumberjack piece falling from the tree and pick it up.");
		}

		int petRate = attachment.skillingPetRateScroll ? (int) (tree.getPetChance() * .75) : tree.getPetChance();
		if (Misc.random(100) == 2 && attachment.getItems().getItemCount(13322, false) == 0 && attachment.petSummonId != 13322) {
			PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + attachment.getDisplayName() + "</col> chopped down a tree for the <col=CC0000>Beaver</col> pet!");
			attachment.getItems().addItemUnderAnyCircumstance(13322, 1);
			attachment.getCollectionLog().handleDrop(attachment, 5, 13322, 1);
		}
	}

	@Override
	public void stop() {
		super.stop();
		if (attachment != null) {
			attachment.startAnimation(65535);
		}
	}

	@Override
	public Player getAttachment() {
		return super.getAttachment();
	}
}
