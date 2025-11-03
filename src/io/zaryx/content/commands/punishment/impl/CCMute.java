package io.zaryx.content.commands.punishment.impl;

import io.zaryx.content.commands.punishment.OnlinePlayerPCP;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.dateandtime.TimeSpan;

public class CCMute extends OnlinePlayerPCP {

    @Override
    public String name() {
        return "ccmute";
    }

    @Override
    public boolean requiresDuration() {
        return false;
    }

    @Override
    public void add(Player staff, Player player, TimeSpan duration) {
        player.setHelpCcMuted(true);
        staff.sendMessage(player.getDisplayNameFormatted() + " muted from help cc.");
        player.sendMessage("You've been muted from the help cc.");
    }

    @Override
    public void remove(Player staff, Player player) {
        player.setHelpCcMuted(false);
        staff.sendMessage(player.getDisplayNameFormatted() + " unmuted from help cc.");
        player.sendMessage("You've been unmuted from the help cc.");
    }
}
