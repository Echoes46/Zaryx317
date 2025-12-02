package io.zaryx.content;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.items.GameItem;
import io.zaryx.model.items.ItemAssistant;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.Discord;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/*
 * @author zds_Kai - Discord
 */

public class CorpseCart {

    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
     */
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static final int ANIMATION = 881;

    /**
     * Stores an array of items into each map with the corresponding rarity to the list
     */
    static {
        items.put(LootRarity.COMMON,
                Arrays.asList(


                        new GameItem(527, 100 + Misc.random(30)),
                        new GameItem(2860, 70 + Misc.random(30)),
                        new GameItem(3180, 70 + Misc.random(30)),
                        new GameItem(3126, 30 + Misc.random(30)),
                        new GameItem(533, 30 + Misc.random(30)),
                        new GameItem(4833, 20 + Misc.random(30))

                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(537, 5 + Misc.random(30)),
                        new GameItem(6730, 10 + Misc.random(30)),
                        new GameItem(535, 40 + Misc.random(30)),
                        new GameItem(22781, 10 + Misc.random(30))


                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(22125, 40 + Misc.random(30)),
                        new GameItem(22787, 20 + Misc.random(30)),
                        new GameItem(11944, 10 + Misc.random(30)),
                        new GameItem(22784, 10 + Misc.random(30)
                        )));
    }

    /**
     * The player object that will be triggering this event
     */
    private Player player;

    /**
     * @param player the player
     */
    public CorpseCart(Player player) {
        this.player = player;
    }


    public static void searchCart(Player c) {

        c.startAnimation(ANIMATION);
        int random = Misc.random(100);
        List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON) : random >= 55 && random <= 94 ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
        GameItem item = Misc.getRandomItem(itemList);
        c.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
        c.sendMessage("@blu@You search the cart and find some "+ ItemAssistant.getItemName(item.getId()) + " @red@bones!");
        PlayerHandler.executeGlobalMessage("@red@"+c.getLoginName() + " @blu@has received: @red@"+ ItemAssistant.getItemName(item.getId()) + " @blu@from the @red@Corpse Cart!");
        Discord.writeAchievements("```News: "+ c.getLoginName() + "  has received: "+ ItemAssistant.getItemName(item.getId()) + " from the @red@Corpse Cart!```");
        c.corpseCartClaim = false;
    }
}
