package io.zaryx.content.donor;

import io.zaryx.annotate.PostInit;
import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.impl.DonoVault;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.items.GameItem;
import io.zaryx.model.items.ItemAction;
import io.zaryx.util.Misc;

import java.util.List;

public class DonorVault {
    public static Boundary area = new Boundary(2944, 5824, 3007, 5887);
    @PostInit
    public static void handleStatics() {
        ItemAction.registerInventory(10943,1, (player, item) -> {
            if (player.getItems().getInventoryCount(item.getId()) <= 0) {
                player.sendMessage("@red@Not sure how you managed this but enjoy a 5 minute jail!");
                player.sendJail();
                return;
            }
            else if (Boundary.isIn(player, area)) {
                player.sendMessage("@red@You need to finish your current vault before starting another!");
                return;
            }


            player.DonorVaultObjects.clear();
            player.getItems().deleteItem(10943, 1);
            player.moveTo(new Position(2970, 5826, 0));
            player.sendMessage("@red@You tear a Donor Vault token, Good luck with your rewards!");
            player.sendMessage("@red@Click the final chest or teleport out to leave!");


        });
    }

    public static GameItem randomChestRewards() {
        List<GameItem> itemList = DonoVault.getItems().get(LootRarity.COMMON);
        return Misc.getRandomItem(itemList);
    }

    public static GameItem randomRareChestRewards() {
        List<GameItem> itemList = DonoVault.getItems().get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }

    public void cannotLogout (Player player) {

    }


}
