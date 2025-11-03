package io.zaryx.content.commands.owner;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.punishments.PunishmentType;

public class ManualUnNetBan extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Server.getPunishments().removeWithMessage(player, PunishmentType.NET_BAN, input);
        Server.getPunishments().removeWithMessage(player, PunishmentType.MAC_BAN, input);
    }
}
