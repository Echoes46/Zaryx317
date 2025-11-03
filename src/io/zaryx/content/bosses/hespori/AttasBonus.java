package io.zaryx.content.bosses.hespori;

import java.util.concurrent.TimeUnit;

import io.zaryx.content.QuestTab;
import io.zaryx.content.bonus.DoubleExperience;
import io.zaryx.content.wogw.Wogw;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.discord.Discord;

public class AttasBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Wogw.EXPERIENCE_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        Discord.writeIngameEvents("```The Attas has sprouted and is granting 1 hours bonus xp!``` <@&1248350477154783321>");
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
