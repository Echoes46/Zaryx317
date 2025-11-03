package io.zaryx.content.commands.owner;

import io.zaryx.Server;
import io.zaryx.content.collection_log.CollectionRewards;
import io.zaryx.content.commands.Command;
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

import java.util.List;

public class givelogs extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        for (CollectionRewards valuex : CollectionRewards.values()) {
            List<GameItem> drops = Server.getDropManager().getNPCdrops(valuex.NpcID);
            if (valuex.NpcID == 5126) {
                if (!player.getCollectionLog().getCollections().get(11246 + "").isEmpty()) {
                    drops.addAll(Server.getDropManager().getNPCdrops(11246));
                }
            }
            if (valuex.NpcID == 7554) {
                drops = RaidsChestRare.getRareDrops();
            } else if (valuex.NpcID >= 1 && valuex.NpcID <= 4) {
                drops.clear();
                drops = TreasureTrailsRewardItem.toGameItems(TreasureTrailsRewards.getRewardsForType(player.getCollectionLogNPC()));
            } else if (valuex.NpcID == 5) {
                drops.clear();
                drops = PetHandler.getPetIds(true);
            } else if (valuex.NpcID == 6) {
                drops.clear();
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.WEAPON)) {
                        drops.add(value.getReward());
                    }
                }
            } else if (valuex.NpcID == 7) {
                drops.clear();
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ARMOUR)) {
                        drops.add(value.getReward());
                    }
                }
            } else if (valuex.NpcID == 8) {
                drops.clear();
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ACCESSORY)) {
                        drops.add(value.getReward());
                    }
                }
            } else if (valuex.NpcID == 9) {
                drops.clear();
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.MISC)) {
                        drops.add(value.getReward());
                    }
                }
            } else if (valuex.NpcID == 10) {
                drops.clear();
                for (AoeWeapons value : AoeWeapons.values()) {
                    drops.add(new GameItem(value.ID));
                }
            } else if (valuex.NpcID == Npcs.THE_MAIDEN_OF_SUGADINTI) {
                drops = TheatreOfBloodChest.getRareDrops();
            }
            for (GameItem drop : drops) {
                player.getCollectionLog().handleDrop(player, valuex.NpcID, drop.getId(), drop.getAmount());
                player.sendMessage("Give collection Log - "+ valuex.NpcID + " / " + drop.getId() + " / " + drop.getAmount());
            }
        }
    }
}
