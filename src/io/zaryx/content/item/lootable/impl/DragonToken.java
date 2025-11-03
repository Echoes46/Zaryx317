package io.zaryx.content.item.lootable.impl;

import java.util.*;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.MysteryBoxLootable;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;

/**
 * @author Sponge
 */

public class DragonToken extends MysteryBoxLootable {

    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
     */
    public static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    /**
     * Stores an array of items into each map with the corresponding rarity to the list
     */

    static {
        items.put(LootRarity.COMMON,
                Arrays.asList(
                        new GameItem(696, 100),
                        new GameItem(12588),
                        new GameItem(12588),
                        new GameItem(12588),
                        new GameItem(26382),
                        new GameItem(26384),//torva
                        new GameItem(26386),

                        new GameItem(33144),
                        new GameItem(33145), //pernix
                        new GameItem(33146),

                        new GameItem(33141),
                        new GameItem(33142), //virtus
                        new GameItem(33143),

                        new GameItem(33148), //weapons
                        new GameItem(33149),
                        new GameItem(26374),
                        new GameItem(33148),
                        new GameItem(33149),
                        new GameItem(26374),
                        new GameItem(26235) // vambs


                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(696, 100),
                        new GameItem(12588),
                        new GameItem(12588),
                        new GameItem(12588),
                        new GameItem(26382),
                        new GameItem(26384),//torva
                        new GameItem(26386),

                        new GameItem(33144),
                        new GameItem(33145), //pernix
                        new GameItem(33146),

                        new GameItem(33141),
                        new GameItem(33142), //virtus
                        new GameItem(33143),

                        new GameItem(33148), //weapons
                        new GameItem(33149),
                        new GameItem(26374),
                        new GameItem(33148),
                        new GameItem(33149),
                        new GameItem(26374),
                        new GameItem(26235) // vambs




                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(696, 200),
                        new GameItem(12588),
                        new GameItem(12588),
                        new GameItem(12588),
                        new GameItem(26382),
                        new GameItem(26384),//torva
                        new GameItem(26386),

                        new GameItem(33144),
                        new GameItem(33145), //pernix
                        new GameItem(33146),

                        new GameItem(33141),
                        new GameItem(33142), //virtus
                        new GameItem(33143),

                        new GameItem(33148), //weapons
                        new GameItem(33149),
                        new GameItem(26374),
                        new GameItem(33148),
                        new GameItem(33149),
                        new GameItem(26374),
                        new GameItem(26235) // vambs



                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(

                        new GameItem(26382),
                        new GameItem(26384),//torva
                        new GameItem(26386),

                        new GameItem(33144),
                        new GameItem(33145), //pernix
                        new GameItem(33146),

                        new GameItem(33141),
                        new GameItem(33142), //virtus
                        new GameItem(33143),

                        new GameItem(33148), //weapons
                        new GameItem(33149),
                        new GameItem(26374),
                        new GameItem(33148),
                        new GameItem(33149),
                        new GameItem(26374),
                        new GameItem(26235), // vambs
                        new GameItem(26269),
                        new GameItem(33207),
                        new GameItem(33204),
                        new GameItem(33204),
                        new GameItem(33204),
                        new GameItem(33202),
                        new GameItem(33203),
                        new GameItem(33205),
                        new GameItem(26269),
                        new GameItem(33207),

                        new GameItem(20483),
                        new GameItem(20484),
                        new GameItem(20486)


                ));
    }

    /**
     * Constructs a new myster box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public DragonToken(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 22087;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }
}
