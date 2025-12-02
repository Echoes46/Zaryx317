package io.zaryx.content.bosses.hespori;

import io.zaryx.content.QuestTab;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class KronosBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeKronosSeed = true;
        Hespori.KRONOS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        Discord.writeIngameEvents("```The Kronos has sprouted and is granting 1 hour of double Raids 1 keys, doubled chances at ToB & Raid Of The Damned!``` <@&1248350477154783321>");
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
