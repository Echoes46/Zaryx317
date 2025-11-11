package io.zaryx.content.achievement;

import io.zaryx.content.achievement.inter.TasksInterface;
import io.zaryx.content.bosses.hespori.Hespori;
import io.zaryx.content.seasons.Halloween;
import io.zaryx.model.Items;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.items.GameItem;
import io.zaryx.model.items.ImmutableItem;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.DiscordBot;
import io.zaryx.util.discord.DiscordChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.text.WordUtils;
import io.zaryx.content.battlepass.Pass;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Jason http://www.rune-server.org/members/jason
 * @date Mar 26, 2014 Mystery & Super done
 */
public class Achievements {

    public enum Achievement {

        /**
         * Tier 1 Achievement Start
         */

        Voter("I Voted", 1, AchievementTier.STARTER, AchievementType.VOTER, "Vote %d Time", 1, 0,
                new GameItem(693, 1), new GameItem(11739, 1), new GameItem(Items.VOTE_CRYSTAL, 2)),

        Questing("Complete Knowledge to Zaryx", 10, AchievementTier.STARTER, AchievementType.ARK_QUEST, "Complete Knowledge to Zaryx", 1,1,
                new GameItem(2528, 1), new GameItem(11681, 100), new GameItem (24364,1)),


        TRIVIA_TASK_I("Trivia %d", 9, AchievementTier.TIER_1, AchievementType.TRIVIA, "Answer 15 Trivia Questions", 15, 1,
                new GameItem(20000, 1), new GameItem(26486, 1), new GameItem(4675, 1)),


        /* Crabby("Kill Rock Crabs %d times", 9, AchievementTier.STARTER, AchievementType.SLAY_ROCKCRAB, "Kill Rock Crabs %d times", 75, 1,
                new GameItem(20000, 1), new GameItem(26486, 1), new GameItem(4675, 1)),
*/
        Daily("Dailyscape", 7, AchievementTier.STARTER, AchievementType.DAILY, "Collect %d Daily Reward", 1, 1,
                new GameItem(691, 1), new GameItem(11681, 100), new GameItem(995, 1000000)),

        Presets("Save a Preset", 5, AchievementTier.STARTER, AchievementType.PRESETS, "Save %d Preset", 1, 0,
                new GameItem(691, 1), new GameItem(11681, 100), new GameItem(995, 1000000)),

        Collector("Collector", 2, AchievementTier.STARTER, AchievementType.COLLECTOR, "Gain 5 Coll log slots", 5, 0,
                new GameItem(693, 1), new GameItem(11681, 100), new GameItem(995, 1000000)),

        Burner("Burn It", 8, AchievementTier.STARTER, AchievementType.FOE_POINTS, "Burn 25k upgrade points", 25000, 1,
                new GameItem(693, 1), new GameItem(11681, 100), new GameItem(995, 1000000)),

        WOGW_Donation("The Giver", 4, AchievementTier.STARTER, AchievementType.WOGW, "Donate 2.5m to well", 2_500_000, 0,
                new GameItem(696, 1), new GameItem(11681, 100), new GameItem(995, 5000000)),

        The_Slayer("The Slayer", 6, AchievementTier.STARTER, AchievementType.SLAY, "Complete %d Slayer Tasks", 5, 1,
                new GameItem(993, 1), new GameItem(13438, 1), new GameItem(7629, 5)),
//

        NEWB_VOTER("Democracy %d", 31, AchievementTier.TIER_1, AchievementType.VOTE_CHEST_UNLOCK, "Open %d Vote Chest", 1, 1,
                new GameItem(22093, 1), new GameItem(11739, 1), new GameItem(23933, 5)),

        CKey_Task("Crystal Clear %d", 26, AchievementTier.TIER_1, AchievementType.LOOT_CRYSTAL_CHEST, "Loot Crystal Chest %d Times", 10, 1,
                new GameItem(6199, 1), new GameItem(11681, 100), new GameItem (989, 10)),

        ClueScroll_Task("Treasure Trails %d", 30, AchievementTier.TIER_1, AchievementType.CLUES, "Loot %d Clue Caskets", 15, 1,
                new GameItem(10025, 5),   new GameItem(19841, 3),  new GameItem(6199, 1)),

        UNIQUE_DROP_NOVICE("Obtain %d unique drops", 34, AchievementTier.TIER_1, AchievementType.UNIQUE_DROPS, "Obtain %d unique drops", 5, 1,
                new GameItem(33136, 3), new GameItem(24365, 1),  new GameItem(6199, 1)),

        UPGRADE_ITEMS_NOVICE("Upgrade %d items", 35, AchievementTier.TIER_1, AchievementType.UPGRADE, "Upgrade %d items",  1, 1,
                new GameItem(696, 10), new GameItem(11681, 350), new GameItem(995, 5000000)),

        Pc_Task("Pest Control %d", 27, AchievementTier.TIER_1, AchievementType.PEST_CONTROL_ROUNDS, "Complete PC 30 times", 30, 1,
                new GameItem(11666, 1),   new GameItem(11681, 100), new GameItem(2528, 2)),


        DRAGON_SLAYER_I("Dragon Hunter %d", 12, AchievementTier.TIER_1, AchievementType.SLAY_DRAGONS, "Kill %d Dragons", 50, 1,
                new GameItem(22124, 50),  new GameItem(11681, 100), new GameItem(405, 2)),

        Barrows_Task_I("Barrows %d", 29, AchievementTier.TIER_1, AchievementType.BARROWS_KILLS, "Kill %d barrows npcs", 50, 1,
                new GameItem(7462, 1),  new GameItem(11681, 150), new GameItem(6199, 1)),

        Boss_Hunter_I(13, AchievementTier.TIER_1, AchievementType.SLAY_BOSSES, "Kill 10 Bosses", 10, 1,
                new GameItem(696, 10), new GameItem(11681, 250), new GameItem(6199, 2)),

        PvMer_I("Mob Killer %d", 11, AchievementTier.TIER_1, AchievementType.SLAY_ANY_NPCS, "Kill %d NPCs", 500, 1,
                new GameItem(993, 1), new GameItem(11681 , 250), new GameItem(405, 5)),

        Jad_Task("Fight Caves %d", 28, AchievementTier.TIER_1, AchievementType.FIGHT_CAVES_ROUNDS, "Complete the Fight Caves", 1, 1,
                new GameItem(6529, 100000),  new GameItem(11681, 250),  new GameItem(6199, 2)),

        Fishing_Task_I("Fishing %d", 14, AchievementTier.TIER_1, AchievementType.FISH, "Catch %d Fish", 500, 1,
                new GameItem(2528, 2),  new GameItem(30002, 5),  new GameItem(25527, 2500)),

        Cooking_Task_I("Cooking %d", 15, AchievementTier.TIER_1, AchievementType.COOK, "Cook %d Fish", 500, 1,
                new GameItem(2528, 2),   new GameItem(30002, 5), new GameItem(25527, 2500)),

        Mining_Task_I("Mining %d", 16, AchievementTier.TIER_1, AchievementType.MINE, "Mine %d Rocks", 500, 1,
                new GameItem(2528, 2),  new GameItem(30002, 5), new GameItem(25527, 2500)),

        Smithing_Task_I("Smithing %d", 17, AchievementTier.TIER_1, AchievementType.SMITH, "Smith %d Bars", 500, 1,
                new GameItem(2528, 2),  new GameItem(30002, 5), new GameItem(25527, 2500)),

        Farming_Task_I("Farming %d", 18, AchievementTier.TIER_1, AchievementType.FARM, "Harvest %d Crops", 250, 1,
                new GameItem(2528, 2),  new GameItem(30002, 5), new GameItem(25527, 2500)),

        Herblore_Task_I("Herblore %d", 19, AchievementTier.TIER_1, AchievementType.HERB, "Create %d Potions", 250, 1,
                new GameItem(2528, 2),  new GameItem(30002, 5), new GameItem(25527, 2500)),

        Woodcutting_Task_I("Woodcutting %d", 20, AchievementTier.TIER_1, AchievementType.WOODCUT, "Cut %d Trees", 500, 1,
                new GameItem(2528, 2),  new GameItem(30002, 5), new GameItem(25527, 2500)),

        Fletching_Task_I("Fletching %d", 21, AchievementTier.TIER_1, AchievementType.FLETCH, "Fletch %d Logs", 500, 1,
                new GameItem(2528, 2), new GameItem(30002, 5), new GameItem(25527, 2500)),

        Firemaking_Task_I("Firemaking %d", 22, AchievementTier.TIER_1, AchievementType.FIRE, "Light %d Logs", 500, 1,
                new GameItem(2528, 2),  new GameItem(30002, 5), new GameItem(25527, 2500)),

        Theiving_Task_I("Thieving %d", 23, AchievementTier.TIER_1, AchievementType.THIEV, "Steal %d Times", 500, 1,
                new GameItem(2528, 2),  new GameItem(30002, 5), new GameItem(25527, 2500)),

        Agility_Task_I("Agility %d", 24, AchievementTier.TIER_1, AchievementType.AGIL, "Complete 50 laps of agility", 50, 1,
                new GameItem(2528, 2), new GameItem(30002, 5), new GameItem(11849, 100)),

        /**
         * Tier 2 Achievement Start
         */


        ADVANCED_VOTER("Democracy %d", 19, AchievementTier.TIER_2, AchievementType.VOTE_CHEST_UNLOCK, "Open %d Vote Chests", 5, 2,
                new GameItem(22093, 3), new GameItem(11739, 1), new GameItem(23933, 10)),

        CHEST_LOOTER("Crystal Clear %d", 16, AchievementTier.TIER_2, AchievementType.LOOT_CRYSTAL_CHEST, "Loot Crystal Chest %d Times", 50, 2,
                new GameItem(696, 2),  new GameItem(11681, 400), new GameItem(989, 25)),


        CLUE_SCROLLER("Treasure Trails %d", 18, AchievementTier.TIER_2, AchievementType.CLUES, "Loot %d Clue Caskets", 50, 2,
                new GameItem(24460, 5),  new GameItem(11681, 400), new GameItem(10025, 10)),

        UNIQUE_DROP_INTERMEDIATE("Obtain %d unique drops", 43, AchievementTier.TIER_2, AchievementType.UNIQUE_DROPS, "Obtain %d unique drops", 15, 1,
                new GameItem(696, 5), new GameItem(24366, 1),new GameItem(19493, 1)),

        UPGRADE_ITEMS_INTERMEDIATE("Upgrade %d items", 44, AchievementTier.TIER_2, AchievementType.UPGRADE, "Upgrade %d items",  5, 1,
                new GameItem(696, 10),  new GameItem(6199, 5), new GameItem(30014, 1)),

        SLAYER_DESTROYER("Slayer %d", 15, AchievementTier.TIER_2, AchievementType.SLAY, "Complete %d Slayer Tasks", 25, 2,
                new GameItem(696, 5), new GameItem(13438, 5), new GameItem(7629, 5)),

        Dragon_Hunter_II("Dragon Hunter %d", 2, AchievementTier.TIER_2, AchievementType.SLAY_DRAGONS, "Kill %d Dragons", 150, 2,
                new GameItem(22124, 100), new GameItem(11681, 400), new GameItem(405, 5)),

        Barrows_Task_III("Barrows %d", 41, AchievementTier.TIER_2, AchievementType.BARROWS_KILLS, "Kill %d barrows npcs", 150, 1,
                new GameItem(696, 6), new GameItem(11681, 400), new GameItem(6199, 2)),

        Boss_Hunter_II(3, AchievementTier.TIER_2, AchievementType.SLAY_BOSSES, "Kill %d Bosses", 50, 2,
                new GameItem(993, 3), new GameItem(24366, 1), new GameItem(33136, 3)),

        PvMer_II("Mob Killer %d", 1, AchievementTier.TIER_2, AchievementType.SLAY_ANY_NPCS, "Kill %d NPCs", 1500, 2,
                new GameItem(995, 10000000), new GameItem(6828, 1), new GameItem(12783, 1)),

        RED_OF_FURY("Fight Caves %d", 17, AchievementTier.TIER_2, AchievementType.FIGHT_CAVES_ROUNDS, "Complete Fight Caves %d Times", 10, 2,
                new GameItem(6529, 300000), new GameItem(696, 5),  new GameItem(6570, 5)),


        NEX("Kill Nex %d Times", 20, AchievementTier.TIER_2, AchievementType.SLAY_NEX, "Kill Nex %d Times", 1, 2,
                new GameItem(993, 2), new GameItem(696, 5), new GameItem(33136,3)),

        NIGHTMARE("Kill Nightmare %d Times", 21, AchievementTier.TIER_2, AchievementType.NIGHTMARE, "Kill Nightmare %d Times", 1, 2,
                new GameItem(993, 2), new GameItem(696, 5), new GameItem(33136,3)),

        CORP("Kill Corporeal Beast %d Times", 26, AchievementTier.TIER_2, AchievementType.SLAY_CORP, "Kill Corporeal Beast %d Times", 1, 2,
                new GameItem(993, 2),  new GameItem(696, 5), new GameItem(33136, 3)),

        VORKATH("Kill Vorkath %d Times", 22, AchievementTier.TIER_2, AchievementType.SLAY_VORKATH, "Kill Vorkath %d Times", 5, 2,
                new GameItem(993, 2),  new GameItem(696, 5), new GameItem(33136, 3)),

        ZULRAH("Kill Zulrah %d Times", 23, AchievementTier.TIER_2, AchievementType.SLAY_ZULRAH, "Kill Zulrah %d Times", 5, 2,
                new GameItem(993, 2),  new GameItem(696, 5), new GameItem(33136, 3)),

        HYDRA("Kill Hydra %d Times", 24, AchievementTier.TIER_2, AchievementType.HYDRA, "Kill Hydra %d Times", 5, 2,
                new GameItem(993, 2),  new GameItem(696, 5), new GameItem(33136, 3)),

        CERB("Kill Cerberus %d Times", 25, AchievementTier.TIER_2, AchievementType.SLAY_CERB, "Kill Cerberus %d Times", 5, 2,
                new GameItem(993, 2),  new GameItem(696, 5), new GameItem(33136, 3)),


        KBD("Kill KBD %d Times", 27, AchievementTier.TIER_2, AchievementType.SLAY_KBD, "Kill KBD %d Times", 5, 2,
                new GameItem(993, 2),  new GameItem(696, 5), new GameItem(33136, 3)),

        SIRE("Kill SIRE %d Times", 28, AchievementTier.TIER_2, AchievementType.SLAY_SIRE, "Kill Abyssal Sire %d Times", 5, 2,
                new GameItem(993, 2),  new GameItem(696, 5), new GameItem(33136, 3)),

        KRAKEN("Kill Kraken %d Times", 29, AchievementTier.TIER_2, AchievementType.SLAY_KRAKEN, "Kill Kraken %d Times", 5, 2,
                new GameItem(993, 2),  new GameItem(696, 5), new GameItem(33136, 3)),

        VETION("Kill Vet'ion %d Times", 30, AchievementTier.TIER_2, AchievementType.SLAY_VETION, "Kill Vet'ion %d Times", 5, 2,
                  new GameItem(2996, 250), new GameItem(4185, 2),  new GameItem(33136, 3)),

        CALLISTO("Kill Callisto %d Times", 31, AchievementTier.TIER_2, AchievementType.SLAY_CALLISTO, "Kill Callisto %d Times", 5, 2,
                  new GameItem(2996, 250), new GameItem(6792, 2), new GameItem(33136, 3)),

        SCORPIA("Kill Scorpia %d Times", 32, AchievementTier.TIER_2, AchievementType.SLAY_SCORPIA, "Kill Scorpia %d Times", 5, 2,
                 new GameItem(2996, 250), new GameItem(4185, 2),  new GameItem(33136, 3)),

        VENENATIS("Kill Venenatis %d Times", 33, AchievementTier.TIER_2, AchievementType.SLAY_VENENATIS, "Kill Venenatis %d Times", 5, 2,
                 new GameItem(2996, 250), new GameItem(6792, 2), new GameItem(33136, 3)),

        CHAOS_ELE("Kill Chaos Elemental %d Times", 34, AchievementTier.TIER_2, AchievementType.SLAY_CHAOSELE, "Kill Chaos Elemental %d Times", 5, 2,
                 new GameItem(2996, 250), new GameItem(4185, 2),  new GameItem(33136, 3)),

        CHAOS_FANATIC("Kill Chaos Fanatic %d Times", 35, AchievementTier.TIER_2, AchievementType.SLAY_CHAOSFANATIC, "Kill Chaos Fanatic %d Times", 5, 2,
                 new GameItem(2996, 250),  new GameItem(6792, 2), new GameItem(33136, 3)),

        CRAZY_ARCH("Kill Crazy Archaeologist %d Times", 36, AchievementTier.TIER_2, AchievementType.SLAY_ARCHAEOLOGIST, "Kill Crazy Archaeologist %d Times", 5, 2,
                  new GameItem(2996, 250), new GameItem(4185, 2),  new GameItem(33136, 3)),

        SHADOW_ARA("Kill Shadow of Araphael %d Times", 37, AchievementTier.TIER_2, AchievementType.SLAY_SHADOWARAPHAEL, "Kill Shadow of Araphael %d Times", 5, 2,
                 new GameItem(2996, 250),  new GameItem(6792, 2), new GameItem(33136, 3)),

        ARA("Kill Araphael %d Times", 38, AchievementTier.TIER_2, AchievementType.SLAY_ARAPHAEL, "Kill Araphael %d Times", 5, 2,
                 new GameItem(2996, 250), new GameItem(4185, 2),  new GameItem(33136, 3)),

        INTERMEDIATE_FISHER("Fishing %d", 4, AchievementTier.TIER_2, AchievementType.FISH, "Catch %d Fish", 1500, 2,
                new GameItem(2528, 3),  new GameItem(22374, 5), new GameItem(25527, 5000)),

        INTERMEDIATE_CHEF("Cooking %d", 5, AchievementTier.TIER_2, AchievementType.COOK, "Cook %d Fish", 1500, 2,
                new GameItem(2528, 3),  new GameItem(22374, 5), new GameItem(25527, 5000)),

        INTERMEDIATE_MINER("Mining %d", 6, AchievementTier.TIER_2, AchievementType.MINE, "Mine %d Rocks", 1500, 2,
                new GameItem(2528, 3),  new GameItem(22374, 5), new GameItem(25527, 5000)),

        INTERMEDIATE_SMITH("Smithing %d", 7, AchievementTier.TIER_2, AchievementType.SMITH, "Smelt or Smith %d Bars", 1500, 2,
                new GameItem(2528, 3),  new GameItem(22374, 5), new GameItem(25527, 5000)),

        INTERMEDIATE_FARMER("Farming %d", 8, AchievementTier.TIER_2, AchievementType.FARM, "Harvest %d Crops", 800, 2,
                new GameItem(2528, 3),  new GameItem(22374, 5), new GameItem(25527, 5000)),

        INTERMEDIATE_MIXER("Herblore %d", 9, AchievementTier.TIER_2, AchievementType.HERB, "Create %d Potions", 800, 2,
                new GameItem(2528, 3),  new GameItem(22374, 5), new GameItem(25527, 5000)),

        INTERMEDIATE_CHOPPER("Woodcutting %d", 10, AchievementTier.TIER_2, AchievementType.WOODCUT, "Cut %d Trees", 1500, 2,
                new GameItem(2528, 3),  new GameItem(22374, 5), new GameItem(25527, 5000)),

        INTERMEDIATE_FLETCHER("Fletching %d", 11, AchievementTier.TIER_2, AchievementType.FLETCH, "Fletch %d Logs", 1500, 2,
                new GameItem(2528, 3),  new GameItem(22374, 5), new GameItem(25527, 5000)),

        INTERMEDIATE_PYRO("Firemaking %d", 12, AchievementTier.TIER_2, AchievementType.FIRE, "Light %d Logs", 1500, 2,
                new GameItem(2528, 3),  new GameItem(22374, 5), new GameItem(25527, 5000)),

        INTERMEDIATE_THIEF("Thieving %d", 13, AchievementTier.TIER_2, AchievementType.THIEV, "Steal %d Times", 1500, 2,
                new GameItem(2528, 3),  new GameItem(22374, 5), new GameItem(25527, 5000)),

        INTERMEDIATE_RUNNER("Agility %d", 14, AchievementTier.TIER_2, AchievementType.AGIL, "Complete 150 laps of agility", 150, 2,
                new GameItem(2528, 3),  new GameItem(22374, 5), new GameItem(12792, 4)),

        /**
         * Tier 3 Achievement Start
         */

        EXTREME_VOTER("Democracy %d", 20, AchievementTier.TIER_3, AchievementType.VOTE_CHEST_UNLOCK, "Open %d Vote Chests", 20, 3,
                new GameItem(22093, 10), new GameItem(11739, 5), new GameItem(23933, 25)),

        DIG_FOR_GOLD("Crystal Clear %d", 16, AchievementTier.TIER_3, AchievementType.LOOT_CRYSTAL_CHEST, "Loot Crystal Chest %d Times", 200, 3,
                new GameItem(696, 30),  new GameItem(11681, 2000), new GameItem(995, 25000000)),

        CLUE_CHAMP("Treasure Trails %d", 19, AchievementTier.TIER_3, AchievementType.CLUES, "Loot %d Clue Caskets", 200, 3,
                new GameItem(24460, 20), new GameItem(11681, 2000), new GameItem(6828, 1)),

        UNIQUE_DROP_EXPERT("Obtain %d unique drops", 44, AchievementTier.TIER_3, AchievementType.UNIQUE_DROPS, "Obtain %d unique drops", 35, 1,
                new GameItem(696, 50),  new GameItem(6828, 1), new GameItem(33108, 1)),

        UPGRADE_ITEMS_EXPERT("Upgrade %d items", 45, AchievementTier.TIER_3, AchievementType.UPGRADE, "Upgrade %d items",  10, 1,
                new GameItem(696, 50), new GameItem(11681, 2000), new GameItem(8167, 1)),

        SLAYER_EXPERT("Slayer %d", 15, AchievementTier.TIER_3, AchievementType.SLAY, "Complete %d Slayer Tasks", 100, 3,
                new GameItem(696, 30), new GameItem(13438, 10), new GameItem(24365, 3)),

        EXPERT_DRAGON_SLAYER("Dragon Hunter %d", 2, AchievementTier.TIER_3, AchievementType.SLAY_DRAGONS, "Kill %d Dragons", 500, 3,
                new GameItem(13116, 1), new GameItem(11681, 1500), new GameItem(33136, 5)),

        BARROWS_GOD("Barrows %d", 18, AchievementTier.TIER_3, AchievementType.BARROWS_KILLS, "Kill %d npcs at barrows", 500, 3,
                new GameItem(696, 30), new GameItem(11681, 1500), new GameItem(11730, 100)),

        BOSS_SLAUGHTERER("Boss Hunter %d", 3, AchievementTier.TIER_3, AchievementType.SLAY_BOSSES, "Kill %d Bosses", 350, 3,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(33136, 5)),

        SLAUGHTERER("Mob Killer %d", 1, AchievementTier.TIER_3, AchievementType.SLAY_ANY_NPCS, "Kill %d NPCs", 5000, 3,
                new GameItem(696, 40), new GameItem(11681, 2000), new GameItem(995, 25000000)),

        TZHAAR("Fight Caves %d", 17, AchievementTier.TIER_3, AchievementType.FIGHT_CAVES_ROUNDS, "Complete Fight Caves %d Times", 50, 3,
                new GameItem(6828, 1), new GameItem(11681, 2000), new GameItem(6570, 15)),

        INFERNO_NOVICE("Inferno %d", 43, AchievementTier.TIER_3, AchievementType.INFERNO, "Complete Inferno %d Times", 1, 1,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(21295, 3)),

        COX_GUARDIAN( 41, AchievementTier.TIER_3, AchievementType.COX, "Complete COX 5 times (raids 1)", 5, 4,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(6805, 1)),

        TOB_GUARDIAN( 42, AchievementTier.TIER_3, AchievementType.TOB, "Complete TOB 5 times (raids 2)", 5, 4,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(6805, 1)),

        ARBOGRAVE_GUARDIAN(46, AchievementTier.TIER_3, AchievementType.ARBO, "Complete Arbo 5 times (raids 3)",  5, 4,
                new GameItem(696, 50), new GameItem(26545, 1), new GameItem(6805, 1)),

        NEX_MASTER("Kill Nex %d Times", 21, AchievementTier.TIER_3, AchievementType.SLAY_NEX, "Kill Nex %d Times", 50, 2,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(6805, 1)),

        NIGHTMARE_MASTER("Kill Nightmare %d Times", 22, AchievementTier.TIER_3, AchievementType.NIGHTMARE, "Kill Nightmare %d Times", 100, 2,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(6805, 1)),

        CORP_MASTER("Kill Corporeal Beast %d Times", 27, AchievementTier.TIER_3, AchievementType.SLAY_CORP, "Kill Corporeal Beast %d Times", 100, 2,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(6805, 1)),

        VORKATH_MASTER("Kill Vorkath %d Times", 23, AchievementTier.TIER_3, AchievementType.SLAY_VORKATH, "Kill Vorkath %d Times", 150, 2,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(6828, 1)),

        ZULRAH_MASTER("Kill Zulrah %d Times", 24, AchievementTier.TIER_3, AchievementType.SLAY_ZULRAH, "Kill Zulrah %d Times", 150, 2,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(6828, 1)),

        HYDRA_MASTER("Kill Hydra %d Times", 25, AchievementTier.TIER_3, AchievementType.HYDRA, "Kill Hydra %d Times", 150, 2,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(6828, 1)),

        CERB_MASTER("Kill Cerberus %d Times", 26, AchievementTier.TIER_3, AchievementType.SLAY_CERB, "Kill Cerberus %d Times", 150, 2,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(6828, 1)),


        KBD_MASTER("Kill KBD %d Times", 28, AchievementTier.TIER_3, AchievementType.SLAY_KBD, "Kill KBD %d Times", 150, 2,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(6828, 1)),

        SIRE_MASTER("Kill SIRE %d Times", 29, AchievementTier.TIER_3, AchievementType.SLAY_SIRE, "Kill Abyssal Sire %d Times", 150, 2,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(6828, 1)),

        KRAKEN_MASTER("Kill Kraken %d Times", 30, AchievementTier.TIER_3, AchievementType.SLAY_KRAKEN, "Kill Kraken %d Times", 150, 2,
                new GameItem(696, 50), new GameItem(993, 5), new GameItem(6828, 1)),

        VETION_MASTER("Kill Vet'ion %d Times", 31, AchievementTier.TIER_3, AchievementType.SLAY_VETION, "Kill Vet'ion %d Times", 150, 2,
                new GameItem(696, 40), new GameItem(4185, 5), new GameItem(33136, 5)),

        CALLISTO_MASTER("Kill Callisto %d Times", 32, AchievementTier.TIER_3, AchievementType.SLAY_CALLISTO, "Kill Callisto %d Times", 150, 2,
                new GameItem(696, 40), new GameItem(6792, 5), new GameItem(33136, 5)),

        SCORPIA_MASTER("Kill Scorpia %d Times", 33, AchievementTier.TIER_3, AchievementType.SLAY_SCORPIA, "Kill Scorpia %d Times", 150, 2,
                new GameItem(696, 40), new GameItem(4185, 5), new GameItem(33136, 5)),

        VENENATIS_MASTER("Kill Venenatis %d Times", 34, AchievementTier.TIER_3, AchievementType.SLAY_VENENATIS, "Kill Venenatis %d Times", 150, 2,
                new GameItem(696, 40), new GameItem(6792, 5), new GameItem(33136, 5)),

        CHAOS_ELE_MASTER("Kill Chaos Elemental %d Times", 35, AchievementTier.TIER_3, AchievementType.SLAY_CHAOSELE, "Kill Chaos Elemental %d Times", 150, 2,
                new GameItem(696, 40), new GameItem(4185, 5), new GameItem(33136, 5)),

        CHAOS_FANATIC_MASTER("Kill Chaos Fanatic %d Times", 36, AchievementTier.TIER_3, AchievementType.SLAY_CHAOSFANATIC, "Kill Chaos Fanatic %d Times", 150, 2,
                new GameItem(696, 40), new GameItem(6792, 5), new GameItem(33136, 5)),

        CRAZY_ARCH_MASTER("Kill Crazy Archaeologist %d Times", 37, AchievementTier.TIER_3, AchievementType.SLAY_ARCHAEOLOGIST, "Kill Crazy Archaeologist %d Times", 150, 2,
                new GameItem(696, 40), new GameItem(4185, 5), new GameItem(33136, 5)),

        SHADOW_ARA_MASTER("Kill Shadow of Araphael %d Times", 38, AchievementTier.TIER_3, AchievementType.SLAY_SHADOWARAPHAEL, "Kill Shadow of Araphael %d Times", 150, 2,
                new GameItem(696, 40), new GameItem(6792, 5), new GameItem(33136, 5)),

        ARA_MASTER("Kill Araphael %d Times", 39, AchievementTier.TIER_3, AchievementType.SLAY_ARAPHAEL, "Kill Araphael %d Times", 150, 2,
                new GameItem(696, 40), new GameItem(4185, 5), new GameItem(33136, 5)),

        EXPERT_FISHER("Fishing %d", 4, AchievementTier.TIER_3, AchievementType.FISH, "Catch %d Fish", 4000, 3,
                new GameItem(6828, 1),  new GameItem(22374, 10), new GameItem(7478, 150000)),

        EXPERT_CHEF("Cooking %d", 5, AchievementTier.TIER_3, AchievementType.COOK, "Cook %d Fish", 4000, 3,
                new GameItem(6828, 1),  new GameItem(22374, 10), new GameItem(7478, 150000)),

        EXPERT_MINER("Mining %d", 6, AchievementTier.TIER_3, AchievementType.MINE, "Mine %d Rocks", 4000, 3,
                new GameItem(6828, 1),  new GameItem(22374, 10), new GameItem(7478, 150000)),

        EXPERT_SMITH("Smithing %d", 7, AchievementTier.TIER_3, AchievementType.SMITH, "Smelt or Smith %d Bars", 4000, 3,
                new GameItem(6828, 1),  new GameItem(22374, 10), new GameItem(7478, 150000)),

        EXPERT_FARMER("Farming %d", 8, AchievementTier.TIER_3, AchievementType.FARM, "Harvest %d Crops", 2250, 3,
                new GameItem(6828, 1),  new GameItem(22374, 10), new GameItem(7478, 150000)),

        EXPERT_MIXER("Herblore %d", 9, AchievementTier.TIER_3, AchievementType.HERB, "Create %d Potions", 2250, 3,
                new GameItem(6828, 1),  new GameItem(22374, 10), new GameItem(7478, 150000)),

        EXPERT_CHOPPER("Woodcutting %d", 10, AchievementTier.TIER_3, AchievementType.WOODCUT, "Cut %d Trees", 4000, 3,
                new GameItem(6828, 1),  new GameItem(22374, 10), new GameItem(7478, 150000)),

        EXPERT_FLETCHER("Fletching %d", 11, AchievementTier.TIER_3, AchievementType.FLETCH, "Fletch %d Logs", 4000, 3,
                new GameItem(6828, 1),  new GameItem(22374, 10), new GameItem(7478, 150000)),

        EXPERT_PYRO("Firemaking %d", 12, AchievementTier.TIER_3, AchievementType.FIRE, "Light %d Logs", 4000, 3,
                new GameItem(6828, 1),  new GameItem(22374, 10), new GameItem(7478, 150000)),

        EXPERT_THIEF("Thieving %d", 13, AchievementTier.TIER_3, AchievementType.THIEV, "Steal %d Times", 4000, 3,
                new GameItem(6828, 1),  new GameItem(22374, 10), new GameItem(7478, 150000)),

        EXPERT_RUNNER("Agility %d", 14, AchievementTier.TIER_3, AchievementType.ROOFTOP, "Complete 400 laps of agility", 400, 3,
                new GameItem(6828, 1),  new GameItem(22374, 10), new GameItem(7478, 150000)),
        /**
         * Tier 4 Achievement Start
         */

        MAX( 10, AchievementTier.TIER_4, AchievementType.MAX, "Achieve level 99 in all skills", 1, 4,
                new GameItem(2403, 1), new GameItem(13346, 2), new GameItem(6805, 2)),

        UNIQUE_DROP_MASTER("Obtain %d unique drops", 31, AchievementTier.TIER_4, AchievementType.UNIQUE_DROPS, "Obtain %d unique drops", 75, 1,
                new GameItem(6769, 1), new GameItem(13346, 2), new GameItem(6805, 2)),

        UPGRADE_ITEMS("Upgrade %d items", 32, AchievementTier.TIER_4, AchievementType.UPGRADE, "Upgrade %d items",  30, 1,
                new GameItem(6769, 1), new GameItem(13346, 2), new GameItem(6805, 2)),

        FIRE_OF_EXCHANGE(  7, AchievementTier.TIER_4, AchievementType.FOE_POINTS, "Burn 150m upgrade points", 150000000, 4,
                new GameItem(6769, 1), new GameItem(13346, 2), new GameItem(6805, 2)),

        MIMIC( 1, AchievementTier.TIER_4, AchievementType.MIMIC, "Kill %d Mimics", 20, 4,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        HUNLLEF( 2, AchievementTier.TIER_4, AchievementType.HUNLLEF, "Kill %d Hunllefs", 25, 4,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        WILDY_EVENT( 8, AchievementTier.TIER_4, AchievementType.WILDY_EVENT, "Finish %d\\nWildy Events", 100, 4,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        MAGE_ARENA_II( 9, AchievementTier.TIER_4, AchievementType.MAGE_ARENA_II, "Complete 1 Mage arena (II)", 1, 4,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        Jad_Task_EXPERT("Fight Caves %d", 3, AchievementTier.TIER_4, AchievementType.FIGHT_CAVES_ROUNDS, "Complete 250 Fight Caves", 250, 1,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        INFERNO_EXPERT("Inferno %d", 4, AchievementTier.TIER_4, AchievementType.INFERNO, "Complete Inferno %d Times", 50, 1,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        COX_CHAMPION( 5, AchievementTier.TIER_4, AchievementType.COX, "Complete 200 COX (Raids 1)", 200, 4,
                new GameItem(12582, 2), new GameItem(13346, 2), new GameItem(33109, 1)),

        TOB_CHAMPION( 6, AchievementTier.TIER_4, AchievementType.TOB, "Complete 200 TOB (Raids 2)", 200, 4,
                new GameItem(19891, 2), new GameItem(13346, 2), new GameItem(6805, 2)),

        ARBOGRAVE_KING(33, AchievementTier.TIER_4, AchievementType.ARBO, "Complete 200 Arbo (Raids 3)",  200, 4,
                new GameItem(12579, 2), new GameItem(13346, 2), new GameItem(6805, 2)),

        NEX_GOD1("Kill Nex %d Times", 21, AchievementTier.TIER_4, AchievementType.SLAY_NEX, "Kill Nex %d Times", 400, 2,
                new GameItem(8167, 1), new GameItem(13346, 2), new GameItem(6805, 2)),

        NIGHTMARE_GOD1("Kill Nightmare %d Times", 22, AchievementTier.TIER_4, AchievementType.NIGHTMARE, "Kill Nightmare %d Times", 400, 2,
                new GameItem(8167, 1), new GameItem(13346, 2), new GameItem(6805, 2)),

        CORP_GOD1("Kill Corporeal Beast %d Times", 27, AchievementTier.TIER_4, AchievementType.SLAY_CORP, "Kill Corporeal Beast %d Times", 400, 2,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        VORKATH_GOD1("Kill Vorkath %d Times", 23, AchievementTier.TIER_4, AchievementType.SLAY_VORKATH, "Kill Vorkath %d Times", 500, 2,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        ZULRAH_GOD1("Kill Zulrah %d Times", 24, AchievementTier.TIER_4, AchievementType.SLAY_ZULRAH, "Kill Zulrah %d Times", 500, 2,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        HYDRA_GOD1("Kill Hydra %d Times", 25, AchievementTier.TIER_4, AchievementType.HYDRA, "Kill Hydra %d Times", 500, 2,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        CERB_GOD1("Kill Cerberus %d Times", 26, AchievementTier.TIER_4, AchievementType.SLAY_CERB, "Kill Cerberus %d Times", 500, 2,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        KBD_GOD1("Kill KBD %d Times", 28, AchievementTier.TIER_4, AchievementType.SLAY_KBD, "Kill KBD %d Times", 500, 2,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        SIRE_GOD1("Kill SIRE %d Times", 29, AchievementTier.TIER_4, AchievementType.SLAY_SIRE, "Kill Abyssal Sire %d Times", 500, 2,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        KRAKEN_GOD1("Kill Kraken %d Times", 30, AchievementTier.TIER_4, AchievementType.SLAY_KRAKEN, "Kill Kraken %d Times", 500, 2,
                new GameItem(6828, 2), new GameItem(13346, 1), new GameItem(6805, 2)),

        MASTER_FISHER1("Fishing %d", 11, AchievementTier.TIER_4, AchievementType.FISH, "Catch %d Fish", 10000, 1,
                new GameItem(7478, 500000), new GameItem(13346, 1), new GameItem(6805, 2), new GameItem(25959, 1)),

        MASTER_CHEF1("Cooking %d", 12, AchievementTier.TIER_4, AchievementType.COOK, "Cook %d Fish", 10000, 1,
                new GameItem(7478, 500000), new GameItem(13346, 1), new GameItem(6805, 2)),

        MASTER_MINER1("Mining %d", 13, AchievementTier.TIER_4, AchievementType.MINE, "Mine %d Rocks", 10000, 1,
                new GameItem(7478, 500000), new GameItem(13346, 1), new GameItem(6805, 2), new GameItem(25959, 1)),

        MASTER_SMITH1("Smithing %d", 14, AchievementTier.TIER_4, AchievementType.SMITH, "Smelt or Smith %d Bars", 10000, 1,
                new GameItem(7478, 500000), new GameItem(13346, 1), new GameItem(6805, 2)),

        MASTER_FARMER1("Farming %d", 15, AchievementTier.TIER_4, AchievementType.FARM, "Harvest %d Crops", 5000, 1,
                new GameItem(7478, 500000), new GameItem(13346, 1), new GameItem(6805, 2)),

        MASTER_MIXER1("Herblore %d", 16, AchievementTier.TIER_4, AchievementType.HERB, "Create %d Potions", 5000, 1,
                new GameItem(7478, 500000), new GameItem(13346, 1), new GameItem(6805, 2)),

        MASTER_CHOPPER1("Woodcutting %d", 17, AchievementTier.TIER_4, AchievementType.WOODCUT, "Cut %d Trees", 10000, 1,
                new GameItem(7478, 500000), new GameItem(13346, 1), new GameItem(6805, 2), new GameItem(25959, 1)),

        MASTER_FLETCHER1("Fletching %d", 18, AchievementTier.TIER_4, AchievementType.FLETCH, "Fletch %d Logs", 10000, 1,
                new GameItem(7478, 500000), new GameItem(13346, 1), new GameItem(6805, 2)),

        MASTER_PYRO1("Firemaking %d", 19, AchievementTier.TIER_4, AchievementType.FIRE, "Light %d Logs", 10000, 1,
                new GameItem(7478, 500000), new GameItem(13346, 1), new GameItem(6805, 2)),

        MASTER_THIEF1("Thieving %d", 20, AchievementTier.TIER_4, AchievementType.THIEV, "Steal %d Times", 10000, 1,
                new GameItem(7478, 500000), new GameItem(13346, 1), new GameItem(6805, 2)),

        ;


        private String formattedName;
        private final AchievementTier tier;
        private final AchievementType type;
        private final String description;
        private final int amount;
        private final int identification;
        private final int points;
        private final GameItem[] rewards;

        Achievement(int identification, AchievementTier tier, AchievementType type,
                    String description, int amount, int points, GameItem... rewards) {
            this(null, identification, tier, type, description, amount, points, rewards);
        }

        Achievement(String formattedName, int identification, AchievementTier tier, AchievementType type,
                    String description, int amount, int points, GameItem... rewards) {
            this.formattedName = formattedName == null ? null : formattedName.replace("%d", tier.getTierText());
            this.identification = identification;
            this.tier = tier;
            this.type = type;
            this.description = description.replace("%d", Misc.insertCommas(amount));
            this.amount = amount;
            this.points = points;
            this.rewards = rewards;

            //format the items
            for (GameItem b : rewards) if (b.getAmount() == 0) b.setAmount(1);
        }

        @Override
        public String toString() {
            return "Achievement{" +
                    "formattedName='" + formattedName + '\'' +
                    ", tier=" + tier +
                    ", type=" + type +
                    ", description='" + description + '\'' +
                    ", amount=" + amount +
                    ", identification=" + identification +
                    ", points=" + points +
                    ", rewards=" + Arrays.toString(rewards) +
                    '}';
        }

        static {
            for (Achievement a : Achievement.values()) {
                for (Achievement b : Achievement.values()) {
                    if (a != b && a.getId() == b.getId() && a.getTier() == b.getTier()) {
                        throw new IllegalStateException(String.format("Achievements: %s and %s share the same id.", a.name(), b.name()));
                    }
                }
            }
        }

        public String getFormattedName() {
            if (formattedName == null) {
                formattedName = WordUtils.capitalize(name().toLowerCase().replace("_", " "))
                        .replace("Ii", "II")
                        .replace("Iii", "III")
                        .replace("Iv", "IV");
            }

            return formattedName;
        }

        public int getId() {
            return identification;
        }

        public AchievementTier getTier() {
            return tier;
        }

        public AchievementType getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public int getAmount() {
            return amount;
        }

        public int getPoints() {
            return points;
        }

        public GameItem[] getRewards() {
            return rewards;
        }

        public static final Set<Achievement> ACHIEVEMENTS = EnumSet.allOf(Achievement.class);
    }

    public static void increase(Player player, AchievementType type, int amount) {
        if (DiscordBot.getJda() != null) {
            Guild guild = DiscordBot.getJda().getGuildById(DiscordChannelType.GUILD_ID.getChannelId());

            if (guild != null) {
                for (Member booster : guild.getBoosters()) {
                    if (player.getDiscordUser() == booster.getUser().getIdLong()) {
                        if (Misc.isLucky(10)) {
                            amount += 1;
                        }
                        break;
                    }
                }
            }
        }

        if (Halloween.DoubleAchieve) {
            amount += 1;
        }

        if (Hespori.ACHIEVE_TIMER > 0) {
            amount += 1;
        }

        if (player.usingRage) {
            amount += 1;
        }

        if (player.eggNogTimer > System.currentTimeMillis()) {
            amount += 1;
        }

        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getType() == type) {


                int currentAmount = player.getAchievements().getAmountRemaining(achievement.getTier().getId(), achievement.getId());
                int tier = achievement.getTier().getId();

                if (currentAmount < achievement.getAmount() && !player.getAchievements().isComplete(achievement.getTier().getId(), achievement.getId())) {
                    player.getAchievements().setAmountRemaining(tier, achievement.getId(), currentAmount + amount);
                    if ((currentAmount + amount) >= achievement.getAmount()) {
                        player.getAchievements().setAmountRemaining(tier, achievement.getId(), achievement.getAmount()); // Set to max amount in case they went over
                        player.getAchievements().setComplete(tier, achievement.getId(), true);
                        player.getAchievements().setPoints(achievement.getPoints() + player.getAchievements().getPoints());
                        player.sendMessage(Misc.colorWrap(AchievementHandler.COLOR, "<clan=6>You've completed the " + achievement.getTier().getName().toLowerCase()
                                + " achievement '" + achievement.getFormattedName() + "'!"));
                            Pass.addExperience(player,3);

                        if (player.getAchievements().hasCompletedAll()) {
                            PlayerHandler.executeGlobalStaffMessage(Misc.colorWrap(AchievementHandler.COLOR,
                                    "<clan=6> " + player.getDisplayNameFormatted() + " has completed all achievements!"));
                        }
                    }

                    updateProgress(player, type);
                }
            }
        }
    }

    public static void updateProgress(Player player, AchievementType type) {
        for (Achievement achievement : Achievement.values()) {
            if (achievement.getType() == type) {
                TasksInterface.updateProgress(player, "achievements", achievement);
            }
        }
    }

    public static void addReward(Player player, Achievement achievement) {
        if (achievement.equals(Achievement.ARA_MASTER)) {
            GameItem[] rewards = {new GameItem(13302, 3),  new GameItem(4185, 15), new GameItem(6792, 15), new GameItem(13346, 1)};
            for (GameItem reward : rewards) {
                player.getItems().addItem(reward.getId(), reward.getAmount());
                Pass.addExperience(player,4);
            }
        } else {
            for (GameItem item : achievement.getRewards()) {
                player.getInventory().addAnywhere(new ImmutableItem(item.getId(), item.getAmount()));
            }
        }
    }

    public static void reset(Player player, AchievementType type) {
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getType() == type) {
                if (!player.getAchievements().isComplete(achievement.getTier().getId(), achievement.getId())) {
                    player.getAchievements().setAmountRemaining(achievement.getTier().getId(), achievement.getId(),
                            0);
                }
            }
        }
    }

    public static int getMaximumAchievements() {
        return Achievement.ACHIEVEMENTS.size();
    }
}
