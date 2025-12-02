package io.zaryx.content.bosses.hespori;

import io.zaryx.content.QuestTab;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class GolparBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeGolparSeed = true;
        Hespori.GOLPAR_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        Discord.writeIngameEvents("```The Consecration has sprouted and is granting 1 hour of 2x bonus loot including: Crystal keys, Coin bags, Resource boxes and Clues!``` <@&1248350477154783321>");
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted() + " @bla@planted a Golpar seed which" +
                " granted @red@1 hour of 2x bonus loot including:");
        PlayerHandler.executeGlobalMessage("@red@                   Crystal keys, Coin bags, Resource boxes and Clues!");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeGolparSeed = false;
        Hespori.GOLPAR_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {

        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.GOLPAR;
    }
}
