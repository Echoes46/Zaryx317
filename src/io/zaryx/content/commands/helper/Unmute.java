package io.zaryx.content.commands.helper;

import io.zaryx.content.commands.Command;
import io.zaryx.content.commands.punishment.PunishmentCommand;
import io.zaryx.model.entity.player.Player;

public class Unmute extends Command {
	@Override
	public void execute(Player c, String commandName, String input) {
		new PunishmentCommand(commandName, input).parse(c);
	}

	@Override
	public String getFormat() {
		return PunishmentCommand.getFormat(getCommand());
	}
}
