package io.zaryx.content.bosses.hespori;

import io.zaryx.content.QuestTab;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class NoxiferBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeNoxiferSeed = true;
        Hespori.NOXIFER_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        Discord.writeIngameEvents("```The Noxifer has sprouted and is granting 1 hour of 2x Slayer points!``` <@&1248350477154783321>");
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted() + " @bla@planted a Noxifer seed which" +
                " granted @red@1 hour of 2x Slayer points.");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeNoxiferSeed = false;
        Hespori.NOXIFER_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {

        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.NOXIFER;
    }
}
