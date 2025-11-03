package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.impl.RaidsChestItems;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.zaryx.content.minigames.raids.Raids.rollUnique;


public class CoxReward extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        int uniqueBudget = Misc.random(25000,150000);
        int uniques = 0;
        for (int i = 0; i < 3; i++) { // up to 3 uniques
            if (uniqueBudget <= 0)
                break;
            int pointsToUse = Math.min(350000, uniqueBudget); // max of 350k points per unique attempt
            uniqueBudget -= pointsToUse;
            double chance = (double) pointsToUse / 4200 / 100.0; // 1% chance per 4200 points - OSRS = 8675
            if (ThreadLocalRandom.current().nextDouble() < chance) {
                uniques++;
                player.getRaidRewards().add(rollUnique());

/*                if (uniques == 1) {
                    getPlayers().forEach(p -> p.sendMessage("@pur@Special loot:"));
                }
                PlayerHandler.executeGlobalMessage("@pur@[RARE DROP] " + lucker.getDisplayName()+ " Has just received @red@" + item.getDef().getName() + " from Chambers of Xeric!");*/
            }
        }
        if (player.getRaidRewards().isEmpty()) {
            for (int i = 0; i < 3; i++) {
                List<GameItem> itemList = RaidsChestItems.getItems().get(LootRarity.COMMON);
                GameItem gameItem = itemList.get(Misc.random(itemList.size() - 1));

                gameItem.setAmount(Misc.random(gameItem.getAmount()));

                player.getRaidRewards().add(gameItem);
            }
        }


        for (int z = 0; z < 6; z++) {
            player.getPA().itemOnInterface(-1, 1, 22729, z);
        }

        player.setLootCox(false);
        int i = 0;
        for (GameItem raidReward : player.getRaidRewards()) {
            player.getItems().addItemUnderAnyCircumstance(raidReward.getId(), raidReward.getAmount());
            player.getPA().itemOnInterface(raidReward.getId(), raidReward.getAmount(), 22729, i++);
        }

        if (!player.getRaidRewards().isEmpty()) {
            if (player.getItems().getInventoryCount(21046) > 0) {
                player.getItems().deleteItem2(21046, 1);
                player.sendErrorMessage("You roll a chance at an extra reward");
                if (Misc.isLucky(5)) {
                    player.getItems().addItemUnderAnyCircumstance(11681,10000);
                    player.sendErrorMessage("WINNER! uhm....Kinda?");
                }
            }
        }

        player.getRaidRewards().clear();
        player.getPA().showInterface(22725);
    }
}
