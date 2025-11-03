package io.zaryx.content.commands.admin;

import io.zaryx.content.commands.Command;
import io.zaryx.content.commands.punishment.PunishmentCommand;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

public class UnNetMute extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		new PunishmentCommand(commandName, input).parse(c);
	}

	@Override
	public String getFormat() {
		return PunishmentCommand.getFormat(getCommand());
	}

	public Optional<String> getDescription() {
		return Optional.of("Remove net mute for online player");
	}

}
