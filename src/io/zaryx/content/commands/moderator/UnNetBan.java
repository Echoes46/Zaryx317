package io.zaryx.content.commands.moderator;

import io.zaryx.content.commands.Command;
import io.zaryx.content.commands.punishment.PunishmentCommand;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

public class UnNetBan extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        new PunishmentCommand(commandName, input).parse(player);
    }

    public Optional<String> getDescription() {
        return Optional.of("Unban all addresses of an offline player by display name.");
    }

    @Override
    public String getFormat() {
        return PunishmentCommand.getFormat(getCommand());
    }
}
