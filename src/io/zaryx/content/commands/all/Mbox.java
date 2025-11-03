package io.zaryx.content.commands.all;

import java.util.Optional;

import io.zaryx.content.commands.Command;
import io.zaryx.content.item.lootable.LootableInterface;
import io.zaryx.model.entity.player.Player;

/**
 * Open the mbox in the default web browser.
 * 
 * @author Noah
 */
public class Mbox extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		LootableInterface.openInterface(c);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens the loot table interface.");
	}

}
