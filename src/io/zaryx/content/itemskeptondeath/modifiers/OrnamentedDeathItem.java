package io.zaryx.content.itemskeptondeath.modifiers;

import io.zaryx.content.items.OrnamentedItem;
import io.zaryx.content.itemskeptondeath.DeathItemModifier;
import io.zaryx.model.Items;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrnamentedDeathItem implements DeathItemModifier {

    private static final Set<Integer> ALL;

    static {
        ALL = new HashSet<>();

        // Add all ornamented items except slayer helmets
        Arrays.stream(OrnamentedItem.values())
                .filter(it -> it.getStandardItem() != Items.SLAYER_HELMET && it.getStandardItem() != Items.SLAYER_HELMET_I)
                .forEach(it -> ALL.add(it.getOrnamentedItem()));
    }

    @Override
    public Set<Integer> getItemIds() {
        return ALL;
    }

    @Override
    public void modify(Player player, GameItem gameItem, boolean kept, List<GameItem> keptItems, List<GameItem> lostItems) {
        if (kept)
            return;
        OrnamentedItem ornamentedItem = OrnamentedItem.forOrnamentedItem(gameItem.getId());
        if (ornamentedItem == null)
            return;

        lostItems.remove(gameItem);
        lostItems.add(new GameItem(ornamentedItem.getOrnamentKitItem()));
        lostItems.add(new GameItem(ornamentedItem.getStandardItem()));
    }
}
