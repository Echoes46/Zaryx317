package io.zaryx.content.teamBounties;

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

public class BountyChest {

    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
     */
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static final int ANIMATION = 881;
    public static final int CHEST_ID = 33119;
    public static final int KEY = 13306;

    /**
     * Stores an array of items into each map with the corresponding rarity to the list
     */
    static {
        items.put(LootRarity.COMMON,
                Arrays.asList(
                        new GameItem(995, 500_000 + Misc.random(500_000)),   // Coins
                        new GameItem(1127, 1 + Misc.random(2)),             // Rune Platebody
                        new GameItem(1079, 1 + Misc.random(2)),             // Rune Platelegs
                        new GameItem(1215, 1 + Misc.random(2)),             // Dragon Dagger
        new GameItem(6679, 10),
                new GameItem(33220),
                new GameItem(33220),
                new GameItem(33221),
                new GameItem(33221),
                new GameItem(33222),
                new GameItem(33222),
                new GameItem(26219),
                new GameItem(25739),
                new GameItem(27275),
                new GameItem(25731),
                new GameItem(20997),
                new GameItem(25398),
                new GameItem(25389),
                new GameItem(33226),
                new GameItem(33226),
                new GameItem(25401),
                        new GameItem(696, 20),
                        new GameItem(2401, 1),
                        new GameItem(24365, 1),
                        new GameItem(2996, 50),
                        new GameItem(11924, 1),
                        new GameItem(11926, 1),
                        new GameItem(6828, 2),
                        new GameItem(200, 3),   //Guam
                        new GameItem(202, 3),   //Marrentil
                        new GameItem(204, 3),   //Tarromin
                        new GameItem(206, 3),   //Harralander
                        new GameItem(208, 3),   //Ranarr
                        new GameItem(3050, 3),  //Toadflax
                        new GameItem(210, 3),   //Irit
                        new GameItem(212, 3),   //Avantoe
                        new GameItem(214, 3),   //Kwuarm
                        new GameItem(3052, 3),  //Snapdragon
                        new GameItem(216, 3),   //Cadanite
                        new GameItem(2486, 3),  //Lantadyme
                        new GameItem(218, 3),   //Dwarf weed
                        new GameItem(220, 3)    //Torstol
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(4151, 1 + Misc.random(1)),             // Abyssal Whip
                        new GameItem(11840, 1 + Misc.random(1)),            // Dragon Boots
                        new GameItem(11732, 1 + Misc.random(1)),             // Dragon Platelegs

        new GameItem(200, 3),   //Guam
                new GameItem(202, 3),   //Marrentil
                new GameItem(204, 3),   //Tarromin
                new GameItem(206, 3),   //Harralander
                new GameItem(208, 3),   //Ranarr
                new GameItem(3050, 3),  //Toadflax
                new GameItem(210, 3),   //Irit
                new GameItem(212, 3),   //Avantoe
                new GameItem(214, 3),   //Kwuarm
                new GameItem(3052, 3),  //Snapdragon
                new GameItem(216, 3),   //Cadanite
                new GameItem(2486, 3),  //Lantadyme
                new GameItem(218, 3),   //Dwarf weed
                new GameItem(220, 3)    //Torstol
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(12437, 1),                             // Ancient Relic
                        new GameItem(12831, 1),                             // Blessed Spirit Shield
                        new GameItem(6739, 1),                               // Dragon Axe
        new GameItem(33800),
                new GameItem(33802),
                new GameItem(33804),
                new GameItem(33810),
                new GameItem(33800),
                new GameItem(33802),
                new GameItem(33804),
                new GameItem(33810),
                new GameItem(33186),
                new GameItem(33187),
                new GameItem(33188),
                new GameItem(33183),
                new GameItem(33009),
                new GameItem(33001),
                new GameItem(33002),
                new GameItem(28919),
                new GameItem(33806),
                new GameItem(33808),
                new GameItem(33814),
                new GameItem(33005),
                new GameItem(28583),
                new GameItem(26939),

                new GameItem(761),
                        new GameItem(696, 40),  //10m Nomad

                        new GameItem(8125, 1),   //5m Nomad
                        new GameItem(25918),   //Dragon hunter c'bow (b)

                        new GameItem(25904),   //Vamp slayer helm
                        new GameItem(25904),   //Vamp slayer helm
                        new GameItem(25904),   //Vamp slayer helm


                        new GameItem(26990),
                        new GameItem(26990),
                        new GameItem(26990),
                        new GameItem(26990),

                        new GameItem(22477),   //Avernic defender
                        new GameItem(22477),   //Avernic defender
                        new GameItem(22477),   //Avernic defender
                        new GameItem(22477),   //Avernic defender
                        new GameItem(22477),   //Avernic defender

                        new GameItem(22326),   //Justicar helm
                        new GameItem(22327),   //Justicar body
                        new GameItem(22328),   //Justicar legs

                        new GameItem(24664),   //Twisted ancestral hat
                        new GameItem(24664),   //Twisted ancestral hat

                        new GameItem(24666),   //Twisted ancestral top
                        new GameItem(24666),   //Twisted ancestral top

                        new GameItem(22089, 1),
                        new GameItem(22089, 1),
                        new GameItem(22089, 1),


                        new GameItem(24668),   //Twisted ancestral bottom
                        new GameItem(24668)    //Twisted ancestral bottom
                ));
    }

    /**
     * The player object that will be triggering this event
     */
    private Player player;

    /**
     * @param player the player
     */
    public BountyChest(Player player) {
        this.player = player;
    }

    public static void unlockChest(Player c) {
        if (!c.getItems().playerHasItem(KEY)) {
            c.sendMessage("@red@You need a bank key to unlock this chest.");
            return;
        }

        c.getItems().deleteItem(KEY, 1);
        c.startAnimation(ANIMATION);
        int random = Misc.random(100);
        List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON) : random >= 55 && random <= 94 ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
        GameItem item = Misc.getRandomItem(itemList);
        c.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
        c.sendMessage("@blu@You unlock the chest and find some " + ItemAssistant.getItemName(item.getId()) + "!");
        PlayerHandler.executeGlobalMessage("@red@" + c.getLoginName() + " @blu@has received: @red@" + ItemAssistant.getItemName(item.getId()) + " @blu@from the @red@Bounty Chest!");
        Discord.writeAchievements("```News: " + c.getLoginName() + "  has received: " + ItemAssistant.getItemName(item.getId()) + " from the Bounty Chest!```");
    }
}
