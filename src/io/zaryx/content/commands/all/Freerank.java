package io.zaryx.content.commands.all;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Freerank extends Command {

	private static final String FILE_PATH = "etc/freeranks.txt";
	private static Set<String> usedIps = new HashSet<>();
	private static Set<String> usedMacs = new HashSet<>();
	private static Set<String> usedPlayers = new HashSet<>();

	// Static block to load the IPs from the file on server startup
	static {
		loadUsedIps();
	}


	@Override
	public void execute(Player player, String commandName, String input)  {
		String playerName = player.getLoginName();
		String ipAddress = player.getIpAddress();
		String macAddress = player.getMacAddress();

		// Check if the player, IP, or MAC has already used the command
		if (usedPlayers.contains(playerName) || usedIps.contains(ipAddress) || usedMacs.contains(macAddress)) {
			player.sendMessage("You have already used this command.");
			return;
		}

		// Add the item to the player's inventory (assuming item 607 exists)
		boolean added = player.getItems().addItem(2396, 1);
		if (added) {
			player.sendMessage("You have received your free rank!");
			// Store the player, IP, and MAC to prevent future use
			usedPlayers.add(playerName);
			usedIps.add(ipAddress);
			usedMacs.add(macAddress);

			// Write the IP address to the file
			writeIpToFile(ipAddress);
		} else {
			player.sendMessage("Your inventory is full, please free up some space.");
		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Gives a free rank (item 607) to the player, but can only be used once per IP/MAC address.");
	}

	@Override
	public Optional<String> getParameter() {
		return Optional.empty(); // No parameters needed for this command
	}

	@Override
	public boolean isHidden() {
		return false; // This command should be visible
	}

	// Method to load used IPs from the file
	public static void loadUsedIps() {
		Path path = Paths.get(FILE_PATH);
		try {
			if (!Files.exists(path)) {
				// Create the file if it doesn't exist
				Files.createDirectories(path.getParent()); // Ensure the directory exists
				Files.createFile(path);
			}
			try (BufferedReader reader = Files.newBufferedReader(path)) {
				String line;
				while ((line = reader.readLine()) != null) {
					usedIps.add(line.trim());
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading or creating free rank IP file: " + e.getMessage());
		}
	}

	// Method to write a new IP address to the file
	private void writeIpToFile(String ipAddress) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
			writer.write(ipAddress);
			writer.newLine();
		} catch (IOException e) {
			System.err.println("Error writing to free rank IP file: " + e.getMessage());
		}
	}
}
