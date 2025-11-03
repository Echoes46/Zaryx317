package io.zaryx.content.item.lootable.impl;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.MysteryBoxLootable;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Revamped a simple means of receiving a random item based on chance.
 *
 * @author Sponge
 */
public class MiniNormalMysteryBox extends MysteryBoxLootable {

    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their LootRarity.
     */
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    /**
     * Stores an array of items into each map with the corresponding rarity to the list
     */
    static {

        items.put(LootRarity.COMMON,
                Arrays.asList(

                        new GameItem(6199 ,1),
                        new GameItem(6199 ,1),
                        new GameItem(6199 ,1),
                        new GameItem(11739 ,1),
                        new GameItem(11739 ,1),
                        new GameItem(11739 ,1),
                        new GameItem(6828 ,1),
                        new GameItem(6828 ,1),
                        new GameItem(6828 ,1)

                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(

                        new GameItem(11739 ,1),
                        new GameItem(11739 ,1),
                        new GameItem(13346 ,1),
                        new GameItem(13346 ,1),
                        new GameItem(6828 ,1),
                        new GameItem(6828 ,1),
                        new GameItem(19897 ,1)
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(13346 ,1),
                        new GameItem(13346 ,1),
                        new GameItem(12582 ,1),
                        new GameItem(12582 ,1),
                        new GameItem(19891 ,1),
                        new GameItem(19891 ,1),
                        new GameItem(8167,1),
                        new GameItem(8167 ,1),
                        new GameItem(6831 ,1),
                        new GameItem(12588 ,1),
                        new GameItem(12588 ,1),
                        new GameItem(6831 ,1)


                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(

                        new GameItem(12579 ,1),
                        new GameItem(6829 ,1),
                        new GameItem(12579 ,1),
                        new GameItem(12579 ,1),
                        new GameItem(6829 ,1),
                        new GameItem(12579 ,1),
                        new GameItem(6829 ,1),
                        new GameItem(12588 ,1),
                        new GameItem(22087,1),
                        new GameItem(6678,1)

                ));

    }

    /**
     * Constructs a new myster box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public MiniNormalMysteryBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 6679;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }
}
