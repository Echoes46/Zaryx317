package io.zaryx.content.commands.owner;

import io.zaryx.content.battlepass.Rewards;
import io.zaryx.content.battlepass.Pass;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.concurrent.TimeUnit;

public class startBp extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        Pass.setSeasonEnded(false);
        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        int totalDaysInMonth = YearMonth.from(currentDate).lengthOfMonth();
        int remainingDays = totalDaysInMonth - currentDay;
        Pass.setDaysUntilEnd(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(remainingDays));

        // Reset all players' battle pass experience and tiers
        for (Player p : PlayerHandler.getPlayers()) {
            if (p != null) {
                p.setXp(0);
                p.setTier(0);
                p.sendMessage("Your battle pass experience and tiers have been reset.");
            }
        }

        Rewards.saveInformation();

        player.sendMessage("Battlepass has been started from today and the configurations have been saved.");
        Pass.openInterface(player);
    }
}