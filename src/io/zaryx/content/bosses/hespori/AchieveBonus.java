package io.zaryx.content.bosses.hespori;

import io.zaryx.content.QuestTab;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class AchieveBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeAchieveSeed = true;
        Hespori.ACHIEVE_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        Discord.writeIngameEvents("```The Hespori Seed has sprouted and is granting 1 hours, 2x achievement points!``` <@&1248350477154783321>");
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
