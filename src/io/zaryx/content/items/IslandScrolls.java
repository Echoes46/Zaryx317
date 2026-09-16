package io.zaryx.content.items;

import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.ClientGameTimer;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;

import java.util.concurrent.TimeUnit;

public final class IslandScrolls {
    private static final int TICKS_PER_MINUTE = 100;

    private IslandScrolls() {
    }

    public static boolean use(Player player, int itemId) {
        int minutes = minutesFor(itemId);
        if (minutes == 0) {
            return false;
        }
        if (player.getItems().getInventoryCount(itemId) < 1) {
            return true;
        }

        boolean alreadyActive = player.IslandTimer > 0;
        player.getItems().deleteItem2(itemId, 1);
        player.IslandTimer = addMinutes(player.IslandTimer, minutes);
        syncTimer(player);
        player.sendMessage(alreadyActive
                ? "You have added " + minutes + " minutes to your Unicow timer! "
                    + remainingMinutes(player.IslandTimer) + " minutes remain."
                : "You now have " + minutes + " minutes at Unicows!");
        if (!Boundary.isIn(player, Boundary.UNICOW_AREA)) {
            player.moveTo(new Position(2847, 5086, 0));
        }
        return true;
    }

    static int minutesFor(int itemId) {
        switch (itemId) {
            case 24364: return 15;
            case 24365: return 30;
            case 24366: return 60;
            default: return 0;
        }
    }

    static long addMinutes(long currentTicks, int minutes) {
        long addedTicks = (long) minutes * TICKS_PER_MINUTE;
        return Math.max(0, currentTicks) + addedTicks;
    }

    static int remainingMinutes(long ticks) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0, ticks) / TICKS_PER_MINUTE
                + (ticks > 0 && ticks % TICKS_PER_MINUTE != 0 ? 1 : 0));
    }

    public static void syncTimer(Player player) {
        int remaining = remainingMinutes(player.IslandTimer);
        player.getPA().sendConfig(39, remaining);
        if (remaining > 0) {
            ClientGameTimer timer = remaining <= 15 ? ClientGameTimer.ISLAND_TIMER_15
                    : remaining <= 30 ? ClientGameTimer.ISLAND_TIMER_30
                    : ClientGameTimer.ISLAND_TIMER_60;
            player.getPA().sendGameTimer(timer, TimeUnit.MINUTES, remaining);
        }
    }
}
