package io.zaryx.content.commands.all;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

public class vb extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (Server.getMultiplayerSessionListener().inAnySession(c)) {
            return;
        }
        if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
            c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
            return;
        }
        if (c.getPosition().inWild()) {
            return;
        }

        c.getPA().spellTeleport(2651,3926, 0, false);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Teles you to vote/dono boss area");
    }

}
