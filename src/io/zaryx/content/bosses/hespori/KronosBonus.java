package io.zaryx.content.bosses.hespori;

import io.zaryx.content.QuestTab;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class KronosBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeKronosSeed = true;
        Hespori.KRONOS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        if (DiscordBot.INSTANCE != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("[ HESPORI SEED ]");
            embed.setThumbnail("https://oldschool.runescape.wiki/images/Hespori_seed_5.png?a6e6d");
            embed.setColor(Color.GREEN);
            embed.setTimestamp(java.time.Instant.now());
            embed.addField("The Consecration Seed has sprouted and is granting Doubled Chances at ToB & Raid Of The Damned for 1 hour!", "\u200B", false);
            DiscordBot.INSTANCE.sendWorldEvent(embed.build());
            //DiscordBot.INSTANCE.sendMessage(DiscordChannelType.WORLD_EVENTS, "@everyone");
        }
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted() + " @bla@planted a Kronos seed which" +
                " granted @red@1 hour of double Raids 1 keys");
        PlayerHandler.executeGlobalMessage("@red@                   , doubled chances at ToB & Raid Of The Damned!");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeKronosSeed = false;
        Hespori.KRONOS_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {
        return Hespori.KRONOS_TIMER <= 0;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.KRONOS;
    }
}
