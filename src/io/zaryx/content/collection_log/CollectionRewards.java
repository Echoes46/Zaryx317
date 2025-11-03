package io.zaryx.content.collection_log;

import io.zaryx.Server;
import io.zaryx.content.battlepass.Pass;
import io.zaryx.content.item.lootable.impl.ArbograveChestItems;
import io.zaryx.content.item.lootable.impl.HesporiChestItems;
import io.zaryx.content.item.lootable.impl.RaidsChestRare;
import io.zaryx.content.item.lootable.impl.TheatreOfBloodChest;
import io.zaryx.content.items.aoeweapons.AoeWeapons;
import io.zaryx.content.trails.TreasureTrailsRewardItem;
import io.zaryx.content.trails.TreasureTrailsRewards;
import io.zaryx.content.upgrade.UpgradeMaterials;
import io.zaryx.model.Npcs;

import io.zaryx.model.entity.npc.pets.PetHandler;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.discord.Discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CollectionRewards {


    MOLE(5779, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    BARREL_CHEST(6342, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    DAGGANOTH_SUPREME(2265, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    DAGANNOTH_PRIME(2266, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    DAGANNOTH_REX(2267, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    KING_BLACK_DRAGON(239, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    KALPHITE_QUEEN(965, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    BANODS(2215, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    KRIL(3129, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    KREE(3162, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    COMMANDER(2205, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    CORP(319, new GameItem[]{new GameItem(696, 30),
            new GameItem(13346, 3), new GameItem(12588, 1), new GameItem(7774, 1)}),

    KRAKEN(494, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    CERB(5862, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    SIRE(5890, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    DEMONIC(7145, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    SHAMAN(6766, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    VORKATH(8028, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    ZULRAH(2042, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    HYDRA(8621, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    NIGHTMARE(9425, new GameItem[]{new GameItem(696, 30),
            new GameItem(13346, 3), new GameItem(12588, 1), new GameItem(7774, 1)}),

    SARACHNIS(8713, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    GUARDIANS(7888, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    BRYOPHYA(8195, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    OBOR(7416, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(26545, 3), new GameItem(956, 3)}),

    NEX(11278, new GameItem[]{new GameItem(696, 30),
            new GameItem(13346, 3), new GameItem(12588, 1), new GameItem(7774, 1)}),

    MALEDICTUS(5126, new GameItem[]{new GameItem(6829, 1),
            new GameItem(12588, 2), new GameItem(7774, 2),
            new GameItem(12579, 3)}),

    GALVEK(8096, new GameItem[]{new GameItem(6829, 1),
            new GameItem(12588, 2), new GameItem(7774, 2),
            new GameItem(12579, 3)}),

    VETRION(6611, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(4185, 20), new GameItem(956, 3)}),

    CALLISTO(6503, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(4185, 20), new GameItem(956, 3)}),


    SELDAEH(8888, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(4185, 20), new GameItem(956, 10)}),

    SCORPIA(6615, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(4185, 20), new GameItem(956, 3)}),


    VENENATIS(6610, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(4185, 20), new GameItem(956, 3)}),


    ELEMENTAL(2054, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(4185, 20), new GameItem(956, 3)}),


    FANATIC(6619, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(4185, 20), new GameItem(956, 3)}),


    ARCHAEOLOGIST(6618, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(4185, 20), new GameItem(956, 3)}),


    OLM(7554, new GameItem[]{new GameItem(12582, 3),
            new GameItem(19891, 2), new GameItem(12579, 2), new GameItem(7774, 1)}),


    TOB(8360, new GameItem[]{new GameItem(12582, 2),
            new GameItem(19891, 3), new GameItem(12579, 2), new GameItem(7774, 1)}),

    DHAROK(1673, new GameItem[]{new GameItem(696, 15),
            new GameItem(13346, 1), new GameItem(26545, 3), new GameItem(956, 1)}),

    AHRIM(1672, new GameItem[]{new GameItem(696, 15),
            new GameItem(13346, 1), new GameItem(26545, 3), new GameItem(956, 1)}),

    GUTHAN(1674, new GameItem[]{new GameItem(696, 15),
            new GameItem(13346, 1), new GameItem(26545, 3), new GameItem(956, 1)}),

    KARIL(1675, new GameItem[]{new GameItem(696, 15),
            new GameItem(13346, 1), new GameItem(26545, 3), new GameItem(956, 1)}),

    TORAG(1676, new GameItem[]{new GameItem(696, 15),
            new GameItem(13346, 1), new GameItem(26545, 3), new GameItem(956, 1)}),

    VERAC(1677, new GameItem[]{new GameItem(696, 15),
            new GameItem(13346, 1), new GameItem(26545, 3), new GameItem(956, 1)}),

    EASY(1, new GameItem[]{new GameItem(696, 30), new GameItem(13346, 2),
            new GameItem(8167, 3), new GameItem(6769, 1)}),

    MEDIUM(2, new GameItem[]{new GameItem(696, 30), new GameItem(13346, 3),
            new GameItem(8167, 4), new GameItem(6769, 1)}),

    HARD(3, new GameItem[]{new GameItem(696, 30), new GameItem(13346, 4),
            new GameItem(8167, 5), new GameItem(6769, 1)}),

    MASTER(4, new GameItem[]{new GameItem(696, 50), new GameItem(13346, 5),
            new GameItem(8167, 7), new GameItem(6769, 2)}),

    PETS(5, new GameItem[]{new GameItem(696, 250), new GameItem(12588, 3),
            new GameItem(6829, 3), new GameItem(2396, 3)}),

    WEAPONS(6, new GameItem[]{new GameItem(696, 1000),
            new GameItem(6829, 3), new GameItem(2396, 2)}),

    ARMOR(7, new GameItem[]{new GameItem(696, 1000),
            new GameItem(6829, 3), new GameItem(2396, 2)}),

    ACCESSORY(8, new GameItem[]{new GameItem(696, 250),
            new GameItem(6831, 1), new GameItem(7774, 1)}),

    MISC(9, new GameItem[]{new GameItem(696, 250),
            new GameItem(6831, 1), new GameItem(7774, 1)}),

    ARAPHEL_RED(8172, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(4185, 20), new GameItem(956, 3)}),

    ARAPHEL(8164, new GameItem[]{new GameItem(696, 20),
            new GameItem(13346, 3), new GameItem(4185, 20), new GameItem(956, 3)}),

    QUEEN(8781, new GameItem[]{new GameItem(6829, 1),
            new GameItem(12588, 2), new GameItem(7774, 2),
            new GameItem(12579, 3)}),

    CREATOR(10531, new GameItem[]{new GameItem(6829, 1),
            new GameItem(12588, 2), new GameItem(7774, 2),
            new GameItem(12579, 3)}),

    DESTRUCTOR(10532, new GameItem[]{new GameItem(6829, 1),
            new GameItem(12588, 2), new GameItem(7774, 2),
            new GameItem(12579, 3)}),

    ARBOGRAVE(1101, new GameItem[]{new GameItem(12582, 2),
            new GameItem(19891, 2), new GameItem(12579, 3), new GameItem(7774, 1)}),

    PERKFINDER(1230, new GameItem[]{new GameItem(696, 1000),
            new GameItem(6829, 3), new GameItem(2396, 2)}),

    HESPORI(8583, new GameItem[]{new GameItem(696, 30),
            new GameItem(13346, 3), new GameItem(12588, 1), new GameItem(7774, 2)});

    public int NpcID;
    public GameItem[] Rewards;

    CollectionRewards(int NpcID, GameItem[] Rewards) {
        this.NpcID = NpcID;
        this.Rewards = Rewards;
    }

    public static ArrayList<GameItem> getForNpcID(int npcID) {
        ArrayList<GameItem> collectionRewards = new ArrayList<>();
        for (CollectionRewards value : CollectionRewards.values()) {
            if (value.NpcID == npcID) {
                collectionRewards.addAll(Arrays.asList(value.Rewards));
            }
        }
        return collectionRewards;
    }

    public static boolean handleButton(Player player, int ID) {
        if (ID == 23236) {
            if (player.getCollectionLog().getCollections().containsKey(player.getCollectionLogNPC() + "")) {
                ArrayList<GameItem> itemsObtained = player.getCollectionLog().getCollections().get(player.getCollectionLogNPC() + "");
                if (itemsObtained != null) {
                    List<GameItem> drops = Server.getDropManager().getNPCdrops(player.getCollectionLogNPC());
                    if (player.getCollectionLogNPC() == 7554) {
                        drops = RaidsChestRare.getRareDrops();
                    } else if (player.getCollectionLogNPC() >= 1 && player.getCollectionLogNPC() <= 4) {
                        drops.clear();
                        drops = TreasureTrailsRewardItem.toGameItems(TreasureTrailsRewards.getRewardsForType(player.getCollectionLogNPC()));
                    } else if (player.getCollectionLogNPC() == 5) {
                        drops.clear();
                        drops = PetHandler.getPetIds(true);
                    } else if (player.getCollectionLogNPC() == 6) {
                        drops.clear();
                        for (UpgradeMaterials value : UpgradeMaterials.values()) {
                            if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.WEAPON)) {
                                drops.add(value.getReward());
                            }
                        }
                    } else if (player.getCollectionLogNPC() == 7) {
                        drops.clear();
                        for (UpgradeMaterials value : UpgradeMaterials.values()) {
                            if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ARMOUR)) {
                                drops.add(value.getReward());
                            }
                        }
                    } else if (player.getCollectionLogNPC() == 8) {
                        drops.clear();
                        for (UpgradeMaterials value : UpgradeMaterials.values()) {
                            if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ACCESSORY)) {
                                drops.add(value.getReward());
                            }
                        }
                    } else if (player.getCollectionLogNPC() == 9) {
                        drops.clear();
                        for (UpgradeMaterials value : UpgradeMaterials.values()) {
                            if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.MISC)) {
                                drops.add(value.getReward());
                            }
                        }
                    } else if (player.getCollectionLogNPC() == 10) {
                        drops.clear();
                        for (AoeWeapons value : AoeWeapons.values()) {
                            drops.add(new GameItem(value.ID));
                        }
                    } else if (player.getCollectionLogNPC() == Npcs.THE_MAIDEN_OF_SUGADINTI) {
                        drops = TheatreOfBloodChest.getRareDrops();
                    } else if (player.getCollectionLogNPC() == 1101) {
                        drops = ArbograveChestItems.getRareDrops();
                    } else if (player.getCollectionLogNPC() == 8583) {
                        drops = HesporiChestItems.getRareDrops();
                    }
                    if (drops != null &&
                            drops.size() == itemsObtained.size()
                            && !player.getClaimedLog().contains(player.getCollectionLogNPC())) {
                        player.getClaimedLog().add(player.getCollectionLogNPC());

                        for (GameItem gameItem : CollectionRewards.getForNpcID(player.getCollectionLogNPC())) {
                            player.getItems().addItemUnderAnyCircumstance(gameItem.getId(), gameItem.getAmount());
                        }
                        player.sendMessage("@gre@Your rewards have now been claimed!");

                        for (CollectionRewards value : CollectionRewards.values()) {
                            if (value.NpcID == player.getCollectionLogNPC()) {
                                Discord.jda.getTextChannelById(1269835340429004820L).sendMessage(player.getDisplayName() + " has just completed " + value.name().toLowerCase()).queue();
                                break;
                            }
                        }

                        int[] bossIds = {6342, 2265, 2266, 2267, 239, 965, 2215, 3129, 3162, 2205, 319, 494, 5862, 5890, 7145, 6766, 8028, 2042, 8621, 9425, 8713, 7888, 8195, 7416, 11278, 5126, 8096};
                        int[] wildyIds = {6611, 6503, 6615, 6610, 2054, 6619, 6618, 8172, 8164};
                        int[] raidIds = {7554, 8360, 1101};
                        int[] other = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
                        if (Arrays.stream(bossIds).anyMatch(i -> i == player.getCollectionLogNPC())) {
                            Pass.addExperience(player, 3);
                        } else if (Arrays.stream(wildyIds).anyMatch(i -> i == player.getCollectionLogNPC())) {
                            Pass.addExperience(player, 3);
                        } else if (Arrays.stream(raidIds).anyMatch(i -> i == player.getCollectionLogNPC())) {
                            Pass.addExperience(player, 4);
                        } else if (Arrays.stream(other).anyMatch(i -> i == player.getCollectionLogNPC())) {
                            Pass.addExperience(player, 4);
                        }

                    } else if (drops != null && drops.size() == itemsObtained.size()
                            && player.getClaimedLog().contains(player.getCollectionLogNPC())) {
                        player.sendMessage("@red@You've already claimed the reward from this log!");
                    } else if (drops != null &&
                            drops.size() != itemsObtained.size()) {
                        player.sendMessage("@red@You have not completed the log yet!");
                    }
                }
            }
            return true;
        }


        return false;
    }
}

