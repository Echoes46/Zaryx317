package io.zaryx.content.bosses.hespori;

import io.zaryx.content.QuestTab;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.Discord;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class KeldaBonus implements HesporiBonus {

    @Override
    public void activate(Player player) {
        Hespori.activeKeldaSeed = true;
        Hespori.KELDA_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        Discord.writeIngameEvents("```The Kelda has sprouted and is granting 1 hour of 2x Larren's keys!``` <@&1248350477154783321>");
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted() + " @bla@planted a Kelda seed which" +
                " granted @red@1 hour of 2x Larren's keys.");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeKeldaSeed = false;
        Hespori.KELDA_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            return false;
        }
        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.KELDA;
    }
}
