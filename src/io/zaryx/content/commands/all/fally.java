package io.zaryx.content.commands.all;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.Mode;
import io.zaryx.model.entity.player.mode.ModeType;

public class fally extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        if (Server.getMultiplayerSessionListener().inAnySession(c)) {
            return;
        }
        if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
            c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
            return;
        }

        if (c.jailEnd > 1) {
            c.forcedChat("I'm trying to teleport away!");
            c.sendMessage("You are still jailed!");
            return;
        }
        if (c.getPosition().inWild() && c.wildLevel > 20) {
            if (c.petSummonId != 10533) {
                c.sendMessage("You can't use this command in the wilderness.");
                return;
            }
        }

        if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.GROUP_WILDYMAN))) {
            c.sendMessage("@red@You cannot access this boss because you're locked to the wilderness!");
        } else {
            c.getPA().spellTeleport(2961, 3382, 0, false);
        }
    }
}
