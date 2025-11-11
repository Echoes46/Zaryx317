package io.zaryx.content.bosses.hespori;

import io.zaryx.content.QuestTab;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class AchieveBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeAchieveSeed = true;
        Hespori.ACHIEVE_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        if (DiscordBot.INSTANCE != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("[ HESPORI SEED ]");
            embed.setThumbnail("https://oldschool.runescape.wiki/images/Hespori_seed_5.png?a6e6d");
            embed.setColor(Color.GREEN);
            embed.setTimestamp(java.time.Instant.now());
            embed.addField("The Hespori Seed has sprouted and is granting 2x achievement points for 1 hour!", "\u200B", false);
            DiscordBot.INSTANCE.sendWorldEvent(embed.build());
            //DiscordBot.INSTANCE.sendMessage(DiscordChannelType.WORLD_EVENTS, "@everyone");
        }
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] The Hespori Seed has sprouted and is granting 1 hours, 2x achievement points!");
        QuestTab.updateAllQuestTabs();
    }

    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeAchieveSeed = false;
        Hespori.ACHIEVE_TIMER = 0;
    }

    @Override
    public boolean canPlant(Player player) {
        if (Hespori.activeAchieveSeed) {
            player.sendMessage("This seed can't be planted during 2x Achievement points.");
            return false;
        }
        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.HESPORI;
    }


}
