package io.zaryx.model.items;

import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;


public interface ItemAction {

    void handle(Player player, GameItem item);
    public default boolean canHandle(Player player, int itemId) {
        return player.getItems().getInventoryCount(itemId) > 0;
    }
    static void registerInventory(int itemId, int option, ItemAction action) {
        ItemDef def = ItemDef.forId(itemId);
        if(def.inventoryActions == null)
            def.inventoryActions = new ItemAction[5];
        def.inventoryActions[option - 1] = action;
    }

}
