package io.zaryx.content.commands.helper;

import io.zaryx.content.commands.Command;
import io.zaryx.content.commands.punishment.PunishmentCommand;
import io.zaryx.model.entity.player.Player;

import java.util.Optional;

/**
 * Forces a given player to log out.
 * 
 * @author Emiel
 */
public class UnGambleBan extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		new PunishmentCommand(commandName, input).parse(c);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Unban a player from gambling.");
	}

	@Override
	public String getFormat() {
		return PunishmentCommand.getFormat(getCommand());
	}

}
