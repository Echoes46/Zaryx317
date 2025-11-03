package io.zaryx.content.item.lootable.impl;

import io.zaryx.content.bosses.hespori.Hespori;
import io.zaryx.content.combat.death.NPCDeath;
import io.zaryx.content.item.lootable.LootRarity;
import io.zaryx.content.item.lootable.Lootable;
import io.zaryx.content.prestige.PrestigePerks;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.Discord;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;
import java.util.Map;

public class ArbograveChest implements Lootable {

    private static final int KEY = 2400;
    private static final int ANIMATION = 881;

    private static GameItem randomChestRewards() {
        List<GameItem> itemList = ArbograveChestItems.getItems().get(LootRarity.COMMON);
        return Misc.getRandomItem(itemList);
    }

    private static GameItem randomRareChestRewards() {
        List<GameItem> itemList = ArbograveChestItems.getItems().get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return ArbograveChestItems.getItems();
    }



    @Override
    public void roll(Player c) {
        int rareChance = 100;

        if (Hespori.activeKronosSeed) {
            rareChance = 60;
        } else if (c.amDonated >= 100 && c.amDonated < 250) {
            rareChance = 90;
        } else if (c.amDonated >= 250 && c.amDonated < 500) {
            rareChance = 85;
        } else if (c.amDonated >= 500 && c.amDonated < 750) {
            rareChance = 80;
        } else if (c.amDonated >= 750 && c.amDonated < 1000) {
            rareChance = 75;
        } else if (c.amDonated >= 1000 && c.amDonated < 1500) {
            rareChance = 70;
        } else if (c.amDonated >= 1500 && c.amDonated < 2000) {
            rareChance = 65;
        } else if (c.amDonated >= 2000 && c.amDonated < 3000) {
            rareChance = 60;
        } else if (c.amDonated >= 3000) {
            rareChance = 45;
        }

        if (Discord.jda != null) {
            Guild guild = Discord.jda.getGuildById(1248350477117165598L);

            if (guild != null) {
                for (Member booster : guild.getBoosters()) {
                    if (c.getDiscordUser() == booster.getUser().getIdLong()) {
                        rareChance -= 10;
                        break;
                    }
                }
            }
        }


        int chance = Misc.trueRand(rareChance);
        if (c.getItems().playerHasItem(KEY)) {
                c.getItems().deleteItem(KEY, 1);
                c.startAnimation(ANIMATION);
                GameItem reward = (chance < 5) ? randomRareChestRewards() : randomChestRewards();
                int finalAmount = (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount());
                if (c.daily2xRaidLoot > 0) {
                    finalAmount *= 2;
                }
            c.getItems().addItemUnderAnyCircumstance(reward.getId(), finalAmount);
            if (ArbograveChestItems.getItems().get(LootRarity.RARE).contains(reward)) {
                    c.sendMessage("@blu@You have received a rare item out of the storage unit.");
                    NPCDeath.announceKc(c, reward, c.arboCompletions);
                    c.getCollectionLog().handleDrop(c, 1101, reward.getId(), reward.getAmount());
                }
        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }
}
