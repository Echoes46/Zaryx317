package io.zaryx.content.item.lootable;

import java.util.List;
import java.util.Map;

import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;

public interface Lootable {

    Map<LootRarity, List<GameItem>> getLoot();

    void roll(Player player);

}
