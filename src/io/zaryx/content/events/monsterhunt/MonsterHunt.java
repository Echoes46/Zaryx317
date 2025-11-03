package io.zaryx.content.events.monsterhunt;

import java.util.Arrays;
import java.util.List;

import io.zaryx.content.bosses.wildypursuit.FragmentOfSeren;
import io.zaryx.content.bosses.wildypursuit.TheUnbearable;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.npc.NPCSpawning;
import io.zaryx.util.Misc;
import lombok.Getter;

/**
 * MonsterHunt.java
 * 
 * @author Jashy
 *
 * Re-written to make it cleaner - @Emre
 * 
 * A class to spawn NPC's at different locations in the wilderness.
 *
 */

public class MonsterHunt {

	public enum Npcs {
		FRAGMENT_OF_SEREN(FragmentOfSeren.FRAGMENT_ID, "Seren", 3000, 30, 250, 250),
		UNBEARABLE(TheUnbearable.NPC_ID, "Unbearable", 3000, 55, 400, 350),
		MALEDICTUS(11246, "Rev Maledictus", 5000, 55, 400, 350);
		
		private final int npcId;

		private final String monsterName;

		private final int hp;

		private final int maxHit;

		private final int attack;

		private final int defence;

		Npcs(final int npcId, final String monsterName, final int hp, final int maxHit, final int attack, final int defence) {
			this.npcId = npcId;
			this.monsterName = monsterName;
			this.hp = hp;
			this.maxHit = maxHit;
			this.attack = attack;
			this.defence = defence;
		}

		public int getNpcId() {
			return npcId;
		}

		public String getMonsterName() {
			return monsterName;
		}

		public int getHp() {
			return hp;
		}

		public int getMaxHit() {
			return maxHit;
		}

		public int getAttack() {
			return attack;
		}

		public int getDefence() {
			return defence;
		}
	}

	/**
	 * The spawnNPC method which handles the spawning of the NPC and the global
	 * message sent.
	 * 
	 * @param c
	 */

	public static boolean spawned;
	
	private static int npcType;
	
	public static long monsterKilled = System.currentTimeMillis();
	
	private static final MonsterHuntLocation[] locations = {
			new MonsterHuntLocation(3258, 3878, "Demonic Ruins <col=ff0000>(45)</col>"),
			new MonsterHuntLocation(3233, 3638, "Chaos Altar <col=ff0000>(14)</col>"),
			new MonsterHuntLocation(3199, 3887, "Lava Dragons <col=ff0000>(46)</col>"),
			new MonsterHuntLocation(3307, 3933, "Rogues' Castle <col=ff0000>(52)</col>"),
			new MonsterHuntLocation(3306, 3668, "Hill Giants <col=ff0000>(19)</col>"),
			//Deep Wildy Locs
			new MonsterHuntLocation(3166, 4025, "Wyrm Ruins <col=ff0000>(64)</col>"),
			new MonsterHuntLocation(3236, 4083, "Wildy Obelisk <col=ff0000>(71)</col>"),
			new MonsterHuntLocation(3097, 4067, "Revenant Ruins <col=ff0000>(69)</col>")};

	@Getter
	private static MonsterHuntLocation currentLocation;
	@Getter
	public static String name;
	private static boolean isSeren = true;

	public static void despawn() {
		switch (npcType) {
			case FragmentOfSeren.FRAGMENT_ID:
				// Handle the despawning of Fragment Of Seren and its related entities
				if (!FragmentOfSeren.activePillars.isEmpty()) {
					for (NPC pillar : FragmentOfSeren.activePillars) {
						if (pillar != null) {
							NPCHandler.despawn(pillar.getNpcId(), 0);
						}
					}
					FragmentOfSeren.activePillars.clear();
				}
				if (FragmentOfSeren.currentSeren != null) {
					FragmentOfSeren.currentSeren.setDead(true);
				}
				break;

			default:
				NPCHandler.despawn(npcType, 0);
				break;
		}
		NPCHandler.despawn(npcType, 0);
		if (npcType == FragmentOfSeren.FRAGMENT_ID) {
			NPCHandler.despawn(FragmentOfSeren.FRAGMENT_ID, 0);
			NPCHandler.despawn(FragmentOfSeren.NPC_ID, 0);
			NPCHandler.despawn(FragmentOfSeren.CRYSTAL_WHIRLWIND, 0);
		}

		spawned = false;
		currentLocation = null;
		monsterKilled = System.currentTimeMillis();
	}

	public static void spawnNPC() {
		List<MonsterHuntLocation> locationsList = Arrays.asList(locations);
		MonsterHuntLocation randomLocation = Misc.randomTypeOfList(locationsList);
		currentLocation = randomLocation;

		// Randomly select one of the three bosses
		List<Npcs> npcsList = Arrays.asList(Npcs.FRAGMENT_OF_SEREN, Npcs.UNBEARABLE, Npcs.MALEDICTUS);
		Npcs randomNpc = Misc.randomTypeOfList(npcsList);

		name = randomNpc.getMonsterName();
		npcType = randomNpc.getNpcId();

		if (npcType == FragmentOfSeren.FRAGMENT_ID) {
			FragmentOfSeren.currentSeren = NPCSpawning.spawnNpcOld(
					randomNpc.getNpcId(),
					randomLocation.getX(),
					randomLocation.getY(),
					0,
					1,
					randomNpc.getHp(),
					randomNpc.getMaxHit(),
					randomNpc.getAttack(),
					randomNpc.getDefence()
			);
		} else {
			NPCSpawning.spawnNpcOld(
					randomNpc.getNpcId(),
					randomLocation.getX(),
					randomLocation.getY(),
					0,
					1,
					randomNpc.getHp(),
					randomNpc.getMaxHit(),
					randomNpc.getAttack(),
					randomNpc.getDefence()
			);
		}

		spawned = true;
	}

	public static void setCurrentLocation(MonsterHuntLocation currentLocation) {
		MonsterHunt.currentLocation = currentLocation;
	}

	public static void setName(String name) {
		MonsterHunt.name = name;
	}

	public static String getTimeLeft() {
		if (spawned) {
			String npcName;
			switch (npcType) {
				case FragmentOfSeren.FRAGMENT_ID:
					npcName = "Seren";
					break;
				case TheUnbearable.NPC_ID:
					npcName = "The Unbearable";
					break;
				case 11246: // Assuming MALEDICTUS_ID is unique, if it's the same as UNBEARABLE, use appropriate logic
					npcName = "Rev Maledictus";
					break;
				default:
					npcName = "Unknown";
					break;
			}
			return "Wildy Event: @gre@" + npcName;
		}

		long timeLeft = System.currentTimeMillis() - monsterKilled;
		int minutesPassed = (int) (timeLeft / (1000 * 60));
		return "Wildy Event: @red@" + (40 - minutesPassed) + " minutes";
	}
}