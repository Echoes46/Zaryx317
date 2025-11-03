package io.zaryx.content.commands.punishment.impl;

import io.zaryx.Server;
import io.zaryx.content.commands.punishment.PunishmentCommand;
import io.zaryx.content.commands.punishment.PunishmentCommandArgs;
import io.zaryx.content.commands.punishment.PunishmentCommandParser;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Right;
import io.zaryx.model.entity.player.save.PlayerAddresses;
import io.zaryx.model.entity.player.save.PlayerSaveOffline;
import io.zaryx.util.dateandtime.TimeSpan;
import io.zaryx.util.discord.Discord;

import java.io.File;

import static io.zaryx.punishments.PunishmentType.MAC_BAN;
import static io.zaryx.punishments.PunishmentType.NET_BAN;

public class Netban implements PunishmentCommandParser {
    @Override
    public String name() {
        return "netban";
    }

    @Override
    public void add(Player staff, PunishmentCommandArgs args) {
        Player player = args.getPlayerForDisplayName();
        TimeSpan duration = args.getDuration();
        PlayerAddresses addresses = player.getValidAddresses();

        if (addresses.getIp() != null)
            Server.getPunishments().add(NET_BAN, duration, addresses.getIp());
        if (addresses.getMac() != null)
            Server.getPunishments().add(MAC_BAN, duration, addresses.getMac());
        if (addresses.getUUID() != null)
            Server.getPunishments().add(MAC_BAN, duration, addresses.getUUID());

        Discord.writeSuggestionMessage(staff.getDisplayName() + " Banned " + player.getDisplayName());
        PlayerHandler.nonNullStream().filter(it -> addresses.equals(it.getValidAddresses()) && it.getRights().isNot(Right.STAFF_MANAGER)).forEach(Player::forceLogout);
        staff.sendMessage("Banned all known addresses for {}.", player.getDisplayNameFormatted());
    }

    @Override
    public void remove(Player staff, PunishmentCommandArgs args) {
        String loginName = args.index(0).toLowerCase();

        Server.getIoExecutorService().submit(() -> {
            try {
                File file = PlayerSaveOffline.getCharacterFile(loginName);
                if (file == null) {
                    staff.addQueuedAction(plr -> plr.sendMessage("No character file with name {}.", loginName));
                    return;
                }

                PlayerAddresses addresses = PlayerSaveOffline.getAddresses(file);
                PlayerHandler.addQueuedAction(() -> {
                    Server.getPunishments().removeWithMessage(staff, NET_BAN, addresses.getIp());
                    Server.getPunishments().removeWithMessage(staff, MAC_BAN, addresses.getMac());
                    Server.getPunishments().removeWithMessage(staff, MAC_BAN, addresses.getUUID());
                });
            } catch (Exception e) {
                e.printStackTrace();
                staff.addQueuedAction(plr -> plr.sendMessage("Error occurred while removing netban, check console."));
            }
        });
    }

    @Override
    public String getFormat(String commandName) {
        return PunishmentCommand.getFormat(commandName, true);
    }
}
