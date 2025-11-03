package io.zaryx.content.skills.prayer;

import com.google.common.base.Stopwatch;
import io.zaryx.Server;
import io.zaryx.content.event.eventcalendar.EventChallenge;
import io.zaryx.content.skills.Skill;
import io.zaryx.content.taskmaster.TaskMasterKills;
import io.zaryx.model.Items;
import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.EquipmentSet;
import io.zaryx.util.Misc;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * A class that manages a single player training the {@code Skill.PRAYER} skill.
 * 
 * @author Jason MacKeigan
 * @date Mar 10, 2015, 2015, 2:48:38 AM
 */
public class Prayer {

	public static final int RESTORE_PRAYER_BONES = 1;
	public static final int RESTORE_PRAYER_BIG_BONES = 2;
	public static final int RESTORE_PRAYER_BABY_DRAG_WYRM = 3;
	public static final int RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH = 4;
	public static final int RESTORE_PRAYER_SUPER_DRAGON = 5;

	/**
	 * The current bone being used on the altar
     * -- GETTER --
     *  The bone last used on the altar
     *
     *
     * -- SETTER --
     *  Modifies the last bone used on the altar to the parameter
     *
     @return the bone on the altar
      * @param altarBone the bone on the altar

     */
	@Setter
    @Getter
    private Optional<Bone> altarBone = Optional.empty();

	/**
	 * The current bone being used on the altar
	 */
	@Getter
	@Setter
	private Optional<EctoBones> ectoBone = Optional.empty();

	@Getter
	@Setter
	private Optional<BoneMeal> boneMeal = Optional.empty();

	/**
	 * The time that must pass before two bones can be buried consecutively.
	 */
	private static final int BURY_DELAY = 1_200;

	/**
	 * A set of all bones that cannot be modified at any time to ensure consistency
	 */
	private static final Set<Bone> BONES = Collections.unmodifiableSet(EnumSet.allOf(Bone.class));

	private static final Set<EctoBones> ECTO_BONES = Collections.unmodifiableSet(EnumSet.allOf(EctoBones.class));

	private static final Set<BoneMeal> BONE_MEALS = Collections.unmodifiableSet(EnumSet.allOf(BoneMeal.class));


	/**
	 * The player that will be training the {@code Skill.PRAYER} skill.
	 */
	private final Player player;

	/**
	 * Tracks the time in milliseconds of the last bury or use of bone on an altar
	 */
	private final Stopwatch lastAction = Stopwatch.createStarted();

	/**
	 * Creates a new class that will manage training the prayer skill for an individual player.
	 * 
	 * @param player the player training the skill
	 */
	public Prayer(Player player) {
		this.player = player;
	}

	/**
	 * Attempts to bury a single bone into the dirt
	 * 
	 * @param bone the bone being burried
	 */
	public void bury(Bone bone) {
		player.getPA().stopSkilling();
		if (!player.getItems().playerHasItem(bone.getItemId())) {
			return;
		}
		if (lastAction.elapsed(TimeUnit.MILLISECONDS) < BURY_DELAY) {
			return;
		}
		if (bone.getItemId() == 536) {
			player.getEventCalendar().progress(EventChallenge.BURY_X_DRAGON_BONES);
		}
		for (TaskMasterKills taskMasterKills : player.getTaskMaster().taskMasterKillsList) {
			if (taskMasterKills.getDesc().equalsIgnoreCase("Bury @whi@bones")) {
				taskMasterKills.incrementAmountKilled(1);
				player.getTaskMaster().trackActivity(player, taskMasterKills);
			}
		}
		ItemDef definition = ItemDef.forId(bone.getItemId());
		player.sendMessage("You bury the " + (definition == null ? "bone" : definition.getName()) + ".");
		player.getPA().addSkillXPPrayer(bone.getExperience() * (Boundary.isIn(player, Boundary.LAVA_DRAGON_ISLE) && bone.getItemId() == 11943 ? 4 : 1), Skill.PRAYER.getId(), true);
		player.getItems().deleteItem2(bone.getItemId(), 1);
		player.startAnimation(827);
		player.getPA().sendSound(380);
		lastAction.reset();
		lastAction.start();
		onBonesBuriedOrCrushed(bone, false);
	}

	public boolean handleObjects(Player player, int objectId) {
		if (objectId == 16655) {
			if (player.boneOnAltar) {
				return false;
			}
			if (player.getItems().playerHasItem(526)) {
				ectoBone = isOperableEctoBone(526);
			} else if (player.getItems().playerHasItem(530)) {
				ectoBone = isOperableEctoBone(530);
			} else if (player.getItems().playerHasItem(532)) {
				ectoBone = isOperableEctoBone(532);
			} else if (player.getItems().playerHasItem(534)) {
				ectoBone = isOperableEctoBone(534);
			} else if (player.getItems().playerHasItem(536)) {
				ectoBone = isOperableEctoBone(536);
			} else if (player.getItems().playerHasItem(11943)) {
				ectoBone = isOperableEctoBone(11943);
			}
			if (ectoBone.isPresent()) {
				player.boneOnAltar = true;
				player.objectId = objectId;
				player.objectX = player.getX();
				player.objectY = player.getY();
				ectoGrinder(28, player.objectX, player.objectY);
				return true;
			}
		}
		if (objectId == 33524) {
			if (player.boneOnAltar) {
				return false;
			}
			if (player.getItems().playerHasItem(527)) {
				altarBone = isOperableBone(527);
			} else if (player.getItems().playerHasItem(531)) {
				altarBone = isOperableBone(531);
			} else if (player.getItems().playerHasItem(2860)) {
				altarBone = isOperableBone(2860);
			} else if (player.getItems().playerHasItem(3180)) {
				altarBone = isOperableBone(3180);
			} else if (player.getItems().playerHasItem(533)) {
				altarBone = isOperableBone(533);
			} else if (player.getItems().playerHasItem(3126)) {
				altarBone = isOperableBone(3126);
			} else if (player.getItems().playerHasItem(4833)) {
				altarBone = isOperableBone(4833);
			} else if (player.getItems().playerHasItem(535)) {
				altarBone = isOperableBone(535);
			} else if (player.getItems().playerHasItem(22781)) {
				altarBone = isOperableBone(22781);
			} else if (player.getItems().playerHasItem(537)) {
				altarBone = isOperableBone(537);
			} else if (player.getItems().playerHasItem(22784)) {
				altarBone = isOperableBone(22784);
			} else if (player.getItems().playerHasItem(6730)) {
				altarBone = isOperableBone(6730);
			} else if (player.getItems().playerHasItem(6813)) {
				altarBone = isOperableBone(6813);
			} else if (player.getItems().playerHasItem(11944)) {
				altarBone = isOperableBone(11944);
			} else if (player.getItems().playerHasItem(22787)) {
				altarBone = isOperableBone(22787);
			} else if (player.getItems().playerHasItem(22125)) {
				altarBone = isOperableBone(22125);
			}
			if (altarBone.isPresent()) {
				player.boneOnAltar = true;
				player.objectId = objectId;
				player.objectX = player.getX();
				player.objectY = player.getY();
				alter(50000, player.objectX, player.objectY);
				return true;
			}
		}
		if (objectId == 16648) {
			if (player.boneOnAltar) {
				return false;
			}
			if (player.getItems().playerHasItem(11922)) {
				boneMeal = isOperableBoneMeal(11922);
			} else if (player.getItems().playerHasItem(4261)) {
				boneMeal = isOperableBoneMeal(4261);
			} else if (player.getItems().playerHasItem(6810)) {
				boneMeal = isOperableBoneMeal(6810);
			} else if (player.getItems().playerHasItem(4256)) {
				boneMeal = isOperableBoneMeal(4256);
			}
			if (boneMeal.isPresent()) {
				player.boneOnAltar = true;
				player.objectId = objectId;
				player.objectX = player.getX();
				player.objectY = player.getY();
				ectoFuntus(28, player.objectX, player.objectY);
				return true;
			}
		}
		if (objectId == 10530 && Boundary.isIn(player, Boundary.ECTOFUNTUS)) {
		if (player.amDonated >= 249) {
			player.getItems().openUpBank();
			return true;
		} else {
			player.sendMessage("@red@You must be a Emerald Donator to use this chest.");
			return false;
		}
		}
		return false;
}

	public void ectoFuntus(final int amount, int objectX, int objectY) {
		if (!boneMeal.isPresent()) {
			return;
		}
		BoneMeal bone2 = boneMeal.get();
		player.boneOnAltar = false;
		player.getPA().stopSkilling();
		if (!player.getItems().playerHasItem(bone2.getItemId())) {
			return;
		}
		if (lastAction.elapsed(TimeUnit.MILLISECONDS) < BURY_DELAY) {
			return;
		}
		ItemDef definition = ItemDef.forId(bone2.getItemId());
		//player.getPA().stillGfx(624, 3659, 3525, player.heightLevel, 1);
//		player.getPA().addSkillXP(player.objectId == 31984 ? bone.getExperience() * 6 : bone.getExperience() * 3, Skill.PRAYER.getId(), true);
		player.getPA().addSkillXPMultiplied(bone2.getExperience() * 2.5, Skill.PRAYER.getId(), true);
		player.getItems().deleteItem2(bone2.getItemId(), 1);
		player.getItems().addItem(4278, bone2.getEctoTokens());
		player.startAnimation(645);
		lastAction.reset();
		lastAction.start();

		Server.getEventHandler().submit(new Event<Player>("skilling", player, 3) {
			int remaining = amount - 1;

			@Override
			public void execute() {
				int chance = Misc.random(2) + 1;
				if (player == null || player.isDisconnected() || player.getSession() == null) {
					super.stop();
					return;
				}
				if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
					attachment.getInterfaceEvent().execute();
					super.stop();
					return;
				}
				if (!player.getItems().playerHasItem(bone2.getItemId())) {
					super.stop();
					player.sendMessage("You have run out of " + (definition == null ? "bones" : definition.getName()) + ".");
					return;
				}
				if (remaining <= 0) {
					super.stop();
					player.objectId = 0;
					return;
				}
				if (player.saradominFaction) {
					remaining--;
					player.facePosition(player.objectX, player.objectY);
					//player.getPA().stillGfx(624, 3659, 3525, player.heightLevel, 1);
					player.getPA().addSkillXPMultiplied(bone2.getExperience() * 4.2, Skill.PRAYER.getId(), true);
					player.getItems().deleteItem2(bone2.getItemId(), 1);
					player.getItems().addItem(4278, bone2.getEctoTokens());
					player.startAnimation(645);
					lastAction.reset();
					lastAction.start();
				}
				remaining--;
				player.facePosition(player.objectX, player.objectY);
				//player.getPA().stillGfx(624, 3659, 3525, player.heightLevel, 1);
				player.getPA().addSkillXPMultiplied(bone2.getExperience() * 2.5, Skill.PRAYER.getId(), true);
				player.getItems().deleteItem2(bone2.getItemId(), 1);
				player.getItems().addItem(4278, bone2.getEctoTokens());
				player.startAnimation(645);
				lastAction.reset();
				lastAction.start();
			}

		});
	}

	public void ectoGrinder(final int amount, int objectX, int objectY) {
		if (!ectoBone.isPresent()) {
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			player.getDH().sendStatement("You have no more free slots.");
			player.nextChat = -1;
			return;
		}
		EctoBones bone1 = ectoBone.get();
		player.boneOnAltar = false;
		player.getPA().stopSkilling();
		if (!player.getItems().playerHasItem(bone1.getItemId())) {
			return;
		}
		if (lastAction.elapsed(TimeUnit.MILLISECONDS) < BURY_DELAY) {
			return;
		}
		ItemDef definition = ItemDef.forId(bone1.getItemId());
		player.getPA().stillGfx(624, 3659, 3525, player.heightLevel, 1);
//		player.getPA().addSkillXP(player.objectId == 31984 ? bone.getExperience() * 6 : bone.getExperience() * 3, Skill.PRAYER.getId(), true);
		player.getPA().addSkillXPMultiplied(bone1.getExperience() / 30.5, Skill.PRAYER.getId(), true);
		player.getItems().deleteItem2(bone1.getItemId(), 1);
		player.getItems().addItem(bone1.getBoneMealId(), 1);
		player.startAnimation(3705);
		lastAction.reset();
		lastAction.start();
		Server.getEventHandler().submit(new Event<Player>("skilling", player, 3) {
			int remaining = amount - 1;

			@Override
			public void execute() {
				int chance = Misc.random(2) + 1;
				if (player == null || player.isDisconnected() || player.getSession() == null) {
					super.stop();
					return;
				}
				if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
					attachment.getInterfaceEvent().execute();
					super.stop();
					return;
				}
				if (!player.getItems().playerHasItem(bone1.getItemId())) {
					super.stop();
					player.sendMessage("You have run out of " + (definition == null ? "bones" : definition.getName()) + ".");
					return;
				}
				if (remaining <= 0) {
					super.stop();
					player.objectId = 0;
					return;
				}
				remaining--;
				player.facePosition(player.objectX, player.objectY);
				player.getPA().stillGfx(624, 3659, 3525, player.heightLevel, 1);
				player.getPA().addSkillXPMultiplied(bone1.getExperience() / 30.5, Skill.PRAYER.getId(), true);
				player.getItems().deleteItem2(bone1.getItemId(), 1);
				player.getItems().addItem(bone1.getBoneMealId(), 1);
				player.startAnimation(3705);
				lastAction.reset();
				lastAction.start();
			}

		});
	}

	public void alter(final int amount, int objectX, int objectY) {
		if (!altarBone.isPresent()) {
			return;
		}
		Bone bone = altarBone.get();
		player.boneOnAltar = false;
		player.getPA().stopSkilling();
		if (!player.getItems().playerHasItem(bone.getItemId())) {
			return;
		}
		if (lastAction.elapsed(TimeUnit.MILLISECONDS) < BURY_DELAY) {
			return;
		}
		ItemDef definition = ItemDef.forId(bone.getItemId());
		player.getPA().stillGfx(624, objectX, objectY, player.heightLevel, 1);
//		player.getPA().addSkillXP(player.objectId == 31984 ? bone.getExperience() * 6 : bone.getExperience() * 3, Skill.PRAYER.getId(), true);
		player.getPA().addSkillXPMultiplied(player.objectId == 31984 ? bone.getExperience() * 2 : bone.getExperience() * 1.25, Skill.PRAYER.getId(), true);
		player.getItems().deleteItem2(bone.getItemId(), 1);
		player.startAnimation(3705);
		lastAction.reset();
		lastAction.start();
		Server.getEventHandler().submit(new Event<Player>("skilling", player, 3) {
			int remaining = amount - 1;

			@Override
			public void execute() {
				int chance = Misc.random(2) + 1;
				if (player == null || player.isDisconnected() || player.getSession() == null) {
					super.stop();
					return;
				}
				if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
					attachment.getInterfaceEvent().execute();
					super.stop();
					return;
				}
				if (!player.getItems().playerHasItem(bone.getItemId())) {
					super.stop();
					player.sendMessage("You have run out of " + (definition == null ? "bones" : definition.getName()) + ".");
					return;
				}
				if (remaining <= 0) {
					super.stop();
					player.objectId = 0;
					return;
				}
				remaining--;
				player.facePosition(player.objectX, player.objectY);
				player.getPA().stillGfx(624, objectX, objectY, player.heightLevel, 1);
				player.getPA().addSkillXPMultiplied(player.objectId == 31984 ? bone.getExperience() * 2 : bone.getExperience() * 1.25, Skill.PRAYER.getId(), true);
				if (player.getPosition().inWild() && chance == 1) {
					player.getItems().addItem(bone.getItemId(), 1);
					player.sendMessage("@red@The god of chaos smiles on you and returns your sacrifice.");
				}
				for (TaskMasterKills taskMasterKills : player.getTaskMaster().taskMasterKillsList) {
					if (taskMasterKills.getDesc().equalsIgnoreCase("Use @whi@Dragon bones(altar)") &&
							bone.getItemId() == Items.DRAGON_BONES) {
						taskMasterKills.incrementAmountKilled(1);
						player.getTaskMaster().trackActivity(player, taskMasterKills);
					}
				}
				player.getItems().deleteItem2(bone.getItemId(), 1);
				player.startAnimation(3705);
				lastAction.reset();
				lastAction.start();
			}

		});
	}

	public void onBonesBuriedOrCrushed(Bone bone, boolean crushed) {
		if (crushed && Boundary.CATACOMBS.in(player) || EquipmentSet.isWearing(player, EquipmentSet.DRAGONBONE_NECKLACE)) {
			player.restore(Skill.PRAYER, bone.getPrayerRestore());
		}
	}

    /**
	 * Determines if the {@code itemId} matches any of the {@link Bone} itemId values.
	 * 
	 * @param itemId the item id we're comparing
	 * @return {@code true} if a bone exists with a matching itemId.
	 */
	public static Optional<Bone> isOperableBone(int itemId) {
		return BONES.stream().filter(bone -> bone.getItemId() == itemId).findFirst();
	}

	public static Optional<EctoBones> isOperableEctoBone(int itemId) {
		return ECTO_BONES.stream().filter(bone -> bone.getItemId() == itemId).findFirst();
	}

	public static Optional<BoneMeal> isOperableBoneMeal(int itemId) {
		return BONE_MEALS.stream().filter(bone -> bone.getItemId() == itemId).findFirst();
	}
}
