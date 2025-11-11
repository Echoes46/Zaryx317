package io.zaryx.content;

import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.items.GameItem;
import io.zaryx.model.items.ItemAssistant;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/*
 * @author C.T
 */

public class SixHourScrolls {

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

                        new GameItem(691, 1),
                        new GameItem(691, 1),
                        new GameItem(691, 1),
                        new GameItem(691, 1),
                        new GameItem(693, 1),
                        new GameItem(693, 1),
                        new GameItem(693, 1),
                        new GameItem(693, 1),
                        new GameItem(2996, 10),
                        new GameItem(2996, 20),
                        new GameItem(20238, 1),
                        new GameItem(20238, 1),
                        new GameItem(21257, 1),
                        new GameItem(21257, 1),
                        new GameItem(12786, 1),
                        new GameItem(12786, 1),
                        new GameItem(11681, 50),
                        new GameItem(20238, 25),
                        new GameItem(2677, 1),
                        new GameItem(2677, 1),
                        new GameItem(24364, 1),
                        new GameItem(25478, 1),
                        new GameItem(2841, 1),
                        new GameItem(25478, 1)

                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(2996, 40),
                        new GameItem(2996, 40),
                        new GameItem(2996, 40),
                        new GameItem(24365, 1),
                        new GameItem(956, 1),
                        new GameItem(20238, 2),
                        new GameItem(20238, 2),
                        new GameItem(7968, 1),
                        new GameItem(7968, 1),
                        new GameItem(7968, 1),
                        new GameItem(24460, 1),
                        new GameItem(24460, 1),
                        new GameItem(12783, 1),
                        new GameItem(693, 1),
                        new GameItem(2841, 1),
                        new GameItem(2841, 1),
                        new GameItem(696, 1),
                        new GameItem(2801, 1),
                        new GameItem(2801, 1),
                        new GameItem(2801, 1),
                        new GameItem(2722, 1),
                        new GameItem(2722, 1)


                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(2722, 1),
                        new GameItem(19835, 1),
                        new GameItem(19835, 1),
                        new GameItem(19835, 1),
                        new GameItem(19835, 1),
                        new GameItem(24366, 1),
                        new GameItem(24366, 1),
                        new GameItem(24366, 1),
                        new GameItem(696, 5),
                        new GameItem(696, 5),
                        new GameItem(956, 2),
                        new GameItem(956, 2),
                        new GameItem(956, 1),
        new GameItem(956, 1),
        new GameItem(25478, 3),
                new GameItem(24365, 2),
                new GameItem(12783, 1),
                new GameItem(21079, 1),
                new GameItem(21034, 1),
                new GameItem(6769, 1),
                new GameItem(6805, 1),
                new GameItem(6805, 1)


                ));
    }

    /**
     * The player object that will be triggering this event
     */
    private Player player;

    /**
     * @param player the player
     */
    public SixHourScrolls(Player player) {
        this.player = player;
    }


    public static void searchTable(Player c) {

        c.startAnimation(ANIMATION);
        int random = Misc.random(100);
        List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON) : random >= 55 && random <= 94 ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
        GameItem item = Misc.getRandomItem(itemList);
        c.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
        c.sendMessage("@blu@You search the table and find a "+ ItemAssistant.getItemName(item.getId()) + " @red@scroll!");
        PlayerHandler.executeGlobalMessage("@red@"+c.getLoginName() + " @blu@has received: @red@"+ ItemAssistant.getItemName(item.getId()) + " @blu@from the @red@Scroll Table!");
        if (DiscordBot.INSTANCE != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("[ LOYALTY CHEST ]");
            embed.setColor(Color.GREEN);
            embed.setTimestamp(java.time.Instant.now());
            embed.addField(c.getDisplayName() + " has received: "+ ItemAssistant.getItemName(item.getId()) + " from the Loyalty Chest!", "\u200B", false);
            DiscordBot.INSTANCE.writeAchievements99(embed.build());
        }
        c.twoHourClaimable = false;
    }
}
