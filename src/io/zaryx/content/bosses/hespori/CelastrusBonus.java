package io.zaryx.content.bosses.hespori;

import io.zaryx.content.QuestTab;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class CelastrusBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeCelastrusSeed = true;
        Hespori.CELASTRUS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        Discord.writeIngameEvents("```The Celastrus has sprouted and is granting 1 hour of 2x Brimstone keys!``` <@&1248350477154783321>");
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted()+ " @bla@planted a Celastrus seed which" +
                " granted @red@1 hour of 2x Brimstone keys!");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeCelastrusSeed = false;
        Hespori.CELASTRUS_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {

        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.CELASTRUS;
    }
}
