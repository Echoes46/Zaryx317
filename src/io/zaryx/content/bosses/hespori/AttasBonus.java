package io.zaryx.content.bosses.hespori;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import io.zaryx.content.QuestTab;
import io.zaryx.content.bonus.DoubleExperience;
import io.zaryx.content.wogw.Wogw;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;

public class AttasBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Wogw.EXPERIENCE_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        if (DiscordBot.INSTANCE != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("[ HESPORI SEED ]");
            embed.setThumbnail("https://oldschool.runescape.wiki/images/Hespori_seed_5.png?a6e6d");
            embed.setColor(Color.GREEN);
            embed.setTimestamp(java.time.Instant.now());
            embed.addField("The Attas Seed has sprouted and is granting 2x bonus experience for 1 hour!", "\u200B", false);
            DiscordBot.INSTANCE.sendWorldEvent(embed.build());
            //DiscordBot.INSTANCE.sendMessage(DiscordChannelType.WORLD_EVENTS, "@everyone");
        }
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] The Attas has sprouted and is granting 1 hours bonus xp!");
        QuestTab.updateAllQuestTabs();
    }

    @Override
    public void deactivate() {
        Hespori.activeAttasSeed = false;
        Hespori.ATTAS_TIMER = 0;
        updateObject(false);
    }

    @Override
    public boolean canPlant(Player player) {
        if (DoubleExperience.isDoubleExperience()) {
            player.sendMessage("This seed can't be planted during bonus experience.");
            return false;
        }
        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.ATTAS;
    }
}
