package io.zaryx.content.combat.formula;

import io.zaryx.Configuration;
import io.zaryx.content.combat.range.RangeData;
import io.zaryx.content.skills.slayer.NewInterface;
import io.zaryx.model.Bonus;
import io.zaryx.model.CombatType;
import io.zaryx.model.Items;
import io.zaryx.model.definitions.NpcStats;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.DiscordBot;
import io.zaryx.util.discord.DiscordChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.Arrays;
import java.util.Optional;

public class RangeMaxHit extends RangeData {

	public static int calculateRangeDefence(Player c) {
		int defenceLevel = CombatFormula.getPrayerBoostedDefenceLevel(c);
		return CombatFormula.getEffectLevel(defenceLevel, c.getItems().getBonus(Bonus.DEFENCE_RANGED));
		//return defenceLevel + c.getItems().getBonus(Bonus.DEFENCE_RANGED) + (c.getItems().getBonus(Bonus.DEFENCE_RANGED) / 2);
	}

	public static int getBestTwistedBowMagicLevel(NPC npc) {
		NpcStats stats = NpcStats.forId(npc.getNpcId());
		return Math.max(stats.getMagicLevel(), stats.getMagic());
	}

	public static double getTwistedBowAccuracyBoost(int magicLevel) {
		if (magicLevel > 500)
			magicLevel = 500;
		double boost = 140 + ((3d * magicLevel - 10d) / 100d) - (Math.pow(3d * magicLevel / 10d - 100d, 2) / 100d);
		return (Math.min(boost, 140) / 100);
	}

	public static double getTwistedBowDamageBoost(int magicLevel, boolean cox) {
		if (magicLevel > 500)
			magicLevel = 500;
		double boost = 250 + ((3d * magicLevel - 14d) / 100d) - (Math.pow((3d * magicLevel / 10d) - 140d, 2) / 100d);
		return (Math.min(boost, 250) / 100);
	}

	public static boolean wearingCrystalBow(Player c) {
		return c.playerEquipment[3] != -1 && Arrays.stream(RangeData.CRYSTAL_BOWS).anyMatch(it -> c.playerEquipment[3] == it);
	}

	public static int calculateRangeAttack(Player c) { // its not evne being used kkk
		int rangeLevel = c.playerLevel[4];
		int actualRangeLevel = c.getLevelForXP(c.playerXP[Player.playerRanged]);  /// im fucking retarded , legit retarded. how do you add something in a line ?
		if (c.npcAttackingIndex > 0 && c.getItems().isWearingItem(Items.TWISTED_BOW)) {
			Optional<NPC> npc = NPCHandler.getNpcAtIndex(c.npcAttackingIndex);
			if (npc.isPresent()) {
				double boost = getTwistedBowAccuracyBoost(getBestTwistedBowMagicLevel(npc.get()));
				rangeLevel += (rangeLevel * boost);
				if (c.debugMessage) {
					c.sendMessage("Twisted bow accuracy boost: " + boost);
				}
			}
		}

		rangeLevel = CombatFormula.getPrayerBoostedLevel(rangeLevel, CombatFormula.getPrayerRangedAccuracyBonus(c));

		if (c.npcAttackingIndex > 0 && c.getSlayer().getTask().isPresent()) {
			NPC npc = NPCHandler.npcs[c.npcAttackingIndex];
			if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.RANGE)) {
				rangeLevel += actualRangeLevel * .15;
			} else if (c.getItems().isWearingItem(12018, Player.playerAmulet)) {
				if (Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
					rangeLevel += actualRangeLevel * .20;
				}
			}
		}
		rangeLevel += CombatFormula.EFFECTIVE_LEVEL_BOOST;
		rangeLevel += c.attacking.getFightModeAttackBonus();
		return CombatFormula.getEffectLevel(rangeLevel, c.getItems().getBonus(Bonus.ATTACK_RANGED));
	}

	public static int maxHit(Player c) {
		int rangedStrength;
		if (wearingCrystalBow(c) || c.getItems().isWearingItem(Items.CRAWS_BOW, Player.playerWeapon)) {
			rangedStrength = RangeData.getRangeStr(c, Player.playerWeapon);
		} else if (c.playerEquipment[Player.playerWeapon] == Items.TOXIC_BLOWPIPE || c.playerEquipment[Player.playerWeapon] == 33177 || c.playerEquipment[Player.playerWeapon] == 28688) {//blowpipe
			rangedStrength = RangeData.getRangeStr(c, Player.playerArrows) + getRangeStr(c.getToxicBlowpipeAmmo());
		} else if (c.getCombatItems().wearingCrawsBow() || c.usingOtherRangeWeapons) {
			rangedStrength = RangeData.getRangeStr(c, Player.playerArrows);
		} else {
			rangedStrength = RangeData.getRangeStr(c);
		}

		double b = CombatFormula.getPrayerRangedStrengthBonus(c);

		if (c.fullEliteVoidRange() && !c.getPosition().inWild()) {
			b += .125;
		} else if (c.fullVoidRange() && !c.getPosition().inWild()) {
			b += .075;
		} else if (c.fullEliteORVoidRange() && !c.getPosition().inWild()) {
			b += .175;
		} else if (c.fullMasori() && !c.getPosition().inWild()) {
			b += .25;
		} else if (c.fullMasoriF() && !c.getPosition().inWild()) {
			b += .3;
		} else if (c.fullMalar()) {
			b += .50;
		} else if (c.fullSirenic() && !c.getPosition().inWild()) {
			b += .35;
		} else if (c.getItems().isWearingItem(26314)) {
			b += .50;
		} else if (c.getItems().isWearingItem(33005)) {
			b += .20;

		} else if (c.beckoning()) {
			b += .35;

		} else if (c.petSummonId == 27352) {
			b += .15;

		} else if (c.getItems().isWearingItem(33206)) {
		b += .5;
		}  else if (c.getItems().isWearingItem(22634) && !c.getPosition().inWild()) {
			b += .5;
		}  else if (c.getItems().isWearingItem(22636) && !c.getPosition().inWild()) {
			b += .5;
		}


				if (c.playerEquipment[Player.playerFeet] == 10556 && !c.getPosition().inWild()) { //attacker icon
					b += .10D;
				}
		if (DiscordBot.getJda() != null) {
			Guild guild = DiscordBot.getJda().getGuildById(DiscordChannelType.GUILD_ID.getGuildId());

					if (guild != null) {
						for (Member booster : guild.getBoosters()) {
							if (c.getDiscordUser() == booster.getUser().getIdLong()) {
								b += .25;
							}
						}
					}
				}

				if (c.npcAttackingIndex > 0) {
					NPC npc = NPCHandler.npcs[c.npcAttackingIndex];

					if (c.getItems().isWearingItem(21012) || c.getItems().isWearingItem(25916) || c.getItems().isWearingItem(25918)) {
						if (Misc.linearSearch(Configuration.DRAG_IDS, npc.getNpcId()) != -1) {
							b += 0.60;
						}
					} else if (c.getItems().isWearingItem(12018, Player.playerAmulet)) {
						if (Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1 || npc.getNpcId() == 8026 || npc.getNpcId() == 8027 || npc.getNpcId() == 8028) {
							b += 0.20;
						}
					} else if (c.getSlayer().getTask().isPresent()) {
						if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.RANGE) && c.getSlayer().getUnlocks().contains(NewInterface.Unlock.SUPER_SLAYER_HELM.getUnlock())) {
							b += 0.25;
						} else if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.RANGE)) {
							b += 0.15;
						}
					}

					if (c.hasFollower && (c.petSummonId == 25350 || c.petSummonId == 30122) && c.wildLevel > 0 && npc.getNpcId() == (6609 | 6615 | 6610 | 2054 | 6619 | 6618 | 8172 | 8164)) {
						b += 0.20;
					}
					if (c.hasFollower && c.petSummonId == 25348) {
						b += 0.20;
					}

					if (c.hasFollower && (c.petSummonId == 25350 || c.petSummonId == 30122) && Boundary.isIn(c, Boundary.RAIDS) || c.hasFollower && (c.petSummonId == 25350 || c.petSummonId == 30122) && c.getTobContainer().inTob()) {
						b += 0.20;
					}
				}
				boolean hasDarkVersion = (c.petSummonId == 30116 || c.petSummonId == 30120 || c.petSummonId == 30122);

				if (c.hasFollower
						&& ((c.petSummonId == 30016 || c.petSummonId == 30020 || c.petSummonId == 30022 || c.petSummonId == 25350)
						|| (hasDarkVersion))) {
					if (hasDarkVersion) {
						b *= 1.10;
					} else if (Misc.random(1) == 1) {
						b *= 1.10;
					}
				}

				if (c.usingRage && !c.getPosition().inWild()) {
					b *= 0.50;
				}


				double e = (c.playerLevel[4] * b) + 8 + c.attacking.getFightModeStrengthBonus();
				double max = (e * (1d + (double) rangedStrength / 64d) + 5d) / 10d;

				if (c.npcAttackingIndex > 0) {
					NPC npc = NPCHandler.npcs[c.npcAttackingIndex];

					if (c.getItems().isWearingItem(Items.TWISTED_BOW) || c.getItems().isWearingItem(33160) || c.getItems().isWearingItem(33058)) {
						double boost = getTwistedBowDamageBoost(getBestTwistedBowMagicLevel(npc), Boundary.FULL_RAIDS.in(c));
						max *= boost;
						if (max < 42)
							max = 42;
						if (c.debugMessage) {
							c.sendMessage("Twisted bow damage boost: " + boost);
						}
					}
				}

				return (int) max;
			}
		}
