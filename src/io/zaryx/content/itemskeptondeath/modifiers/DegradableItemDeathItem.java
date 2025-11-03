package io.zaryx.content.itemskeptondeath.modifiers;

import io.zaryx.content.items.Degrade;
import io.zaryx.content.itemskeptondeath.DeathItemModifier;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DegradableItemDeathItem implements DeathItemModifier {

    private static final Set<Integer> ALL;

    static {
        ALL = new HashSet<>();
        Arrays.stream(Degrade.DegradableItem.values()).forEach(it -> ALL.add(it.getItemId()));
    }

    @Override
    public Set<Integer> getItemIds() {
        return ALL;
    }

    @Override
    public void modify(Player player, GameItem gameItem, boolean kept, List<GameItem> keptItems, List<GameItem> lostItems) {
        if (kept)
            return;

        Degrade.DegradableItem degrade = Degrade.DegradableItem.forId(gameItem.getId()).orElse(null);
        if (degrade == null)
            return;

        lostItems.remove(gameItem);
        Degrade.reset(player, degrade);

        if (degrade.getBrokenId() > 0 && ItemDef.forId(degrade.getBrokenId()).isTradable()) {
            lostItems.add(new GameItem(degrade.getBrokenId()));
        } else {
            lostItems.add(gameItem);
        }
    }
}
