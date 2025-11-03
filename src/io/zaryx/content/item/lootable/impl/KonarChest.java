package io.zaryx.content.item.lootable.impl;

import io.zaryx.content.event.eventcalendar.EventChallenge;
import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.Lootable;
import io.zaryx.content.prestige.PrestigePerks;
import io.zaryx.model.Items;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KonarChest implements Lootable {

    private static final int KEY = 23083;
    private static final int ANIMATION = 881;

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {
        items.put(LootRarity.COMMON, Arrays.asList(//common loot


                new GameItem(454, 100),  //coal
                new GameItem(454, 150),  //coal
                new GameItem(454, 200),  //coal
                new GameItem(1618, 25),  //uncut diamonds
                new GameItem(1620, 25),  //uncut ruby
                new GameItem(Items.DRAGON_ARROWTIPS, 80),
                new GameItem(Items.DRAGON_DART_TIP, 200),
                new GameItem(1080, 4),   //rune platelegs
                new GameItem(1080, 8),   //rune platelegs
                new GameItem(1128, 4),   //rune platebody
                new GameItem(1128, 8),   //rune platebody
                new GameItem(1164, 4),   //rune full helm
                new GameItem(1164, 8),   //rune full helm
                new GameItem(452, 18),   //runite ore
                new GameItem(Items.IRON_ORE_NOTED, 250), //iron ore
                new GameItem(Items.IRON_ORE_NOTED, 300), //iron ore
                new GameItem(Items.IRON_ORE_NOTED, 325), //iron ore
                new GameItem(Items.BURNT_PAGE, 15),
                new GameItem(Items.RAW_TUNA_NOTED, 250),
                new GameItem(Items.RAW_LOBSTER_NOTED, 300),//
                new GameItem(Items.RAW_SWORDFISH_NOTED, 300),
                new GameItem(Items.RAW_MONKFISH_NOTED, 250),
                new GameItem(Items.RAW_SHARK_NOTED, 250),
                new GameItem(Items.RAW_MANTA_RAY_NOTED, 250),
                new GameItem(Items.STEEL_BAR_NOTED, 500),
                new GameItem(Items.MAGIC_LOGS_NOTED, 160),
                new GameItem(Items.PURE_ESSENCE_NOTED, 3000),
                new GameItem(Items.TORSTOL_SEED, 10),
                new GameItem(Items.RANARR_SEED, 10),
                new GameItem(Items.SNAPDRAGON_SEED, 10),
                new GameItem(Items.OVERLOAD_4, 5),
                new GameItem(Items.SNAPDRAGON_SEED, 10),
                new GameItem(Items.YEW_ROOTS_NOTED, 30),
                new GameItem(Items.MAGIC_ROOTS_NOTED, 30),
                new GameItem(Items.CRUSHED_NEST_NOTED, 30),
                new GameItem(Items.WINE_OF_ZAMORAK_NOTED, 30),
                new GameItem(Items.SNAPE_GRASS_NOTED, 40)
                ));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(5061, 1),
                new GameItem(22943, 1),
                new GameItem(4837, 1),
                new GameItem(26882, 1), //reference
                new GameItem(33124, 1),
                new GameItem(33094, 1),
                new GameItem(33075, 1),
                new GameItem(8901, 1),
                new GameItem(8901, 1),
                new GameItem(11128, 1),
                new GameItem(2841, 1),
                new GameItem(12783, 1),
                new GameItem(4224, 1),
                new GameItem(28416, 1),
                new GameItem(28417, 1),
                new GameItem(28418, 1),
                new GameItem(26884, 1),
                new GameItem(12785, 1),
                new GameItem(13307, 3000),




                new GameItem(26883),
                new GameItem(Items.BURNT_PAGE, 50),
                new GameItem(Items.DRAGON_HARPOON, 1),
                new GameItem(Items.DRAGON_SWORD, 1),
                new GameItem(Items.MYSTIC_BOOTS_DUSK, 1),
                new GameItem(Items.MYSTIC_GLOVES_DUSK, 1),
                new GameItem(Items.MYSTIC_HAT_DUSK, 1),
                new GameItem(Items.MYSTIC_ROBE_BOTTOM_DUSK, 1),
                new GameItem(Items.MYSTIC_ROBE_TOP_DUSK, 1),
                new GameItem(Items.MYSTIC_BOOTS_DUSK, 1),
                new GameItem(Items.MYSTIC_GLOVES_DUSK, 1),
                new GameItem(Items.MYSTIC_HAT_DUSK, 1),
                new GameItem(Items.MYSTIC_ROBE_BOTTOM_DUSK, 1),
                new GameItem(Items.MYSTIC_ROBE_TOP_DUSK, 1),
                new GameItem(Items.MYSTIC_BOOTS_DUSK, 1),
                new GameItem(Items.MYSTIC_GLOVES_DUSK, 1),
                new GameItem(Items.MYSTIC_HAT_DUSK, 1),
                new GameItem(Items.MYSTIC_ROBE_BOTTOM_DUSK, 1),
                new GameItem(Items.MYSTIC_ROBE_TOP_DUSK, 1),
                new GameItem(Items.MYSTIC_BOOTS_DUSK, 1),
                new GameItem(Items.MYSTIC_GLOVES_DUSK, 1),
                new GameItem(Items.MYSTIC_HAT_DUSK, 1),
                new GameItem(Items.MYSTIC_ROBE_BOTTOM_DUSK, 1),
                new GameItem(Items.MYSTIC_ROBE_TOP_DUSK, 1),
                new GameItem(Items.BOOTS_OF_BRIMSTONE, 1),
                new GameItem(Items.GUARDIAN_BOOTS, 1) //the end one doesnt have a comma so edit from the top of the code
                ));
    }


    private static GameItem randomChestRewards(Player c, int chance) {
        int random = Misc.random(chance);//misc random is what you use if you want to randomise an amount rather than a set amount of an item
        int rareChance = 990;
        if (c.getItems().playerHasItem(21046)) {
            rareChance = 988;
            c.getItems().deleteItem(21046, 1);
            c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
            c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
        }
        List<GameItem> itemList = random <= rareChance ? items.get(LootRarity.COMMON) : items.get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }
    
    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public void roll(Player c) {
        if (c.getItems().playerHasItem(KEY)) {//this basically says if a player has a konar key and uses it on a chest it performs an animation, deletes the key and gives a reward
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
    GameItem reward = randomChestRewards(c, 1000);

            c.getItems().addItem(reward.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount()));
        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
            }
    }
}
