package io.zaryx.util.discord.impl;

import io.zaryx.content.questing.Quest;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.save.PlayerAddresses;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.Discord;
import io.zaryx.util.discord.SlashHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static io.zaryx.model.entity.player.save.PlayerSave.getSaveDirectory;

/**
 * Updated by Khaos
 *
 * Slash command: /xmas name:<string>
 *
 * - If the player is online:
 *   Logs MAC, UUID, IP, and any same-IP players who completed Santa's Troubles.
 *
 * - If the player is offline:
 *   Scans save files, logs identifiers and quest stage, then finds other accounts with the same UUID.
 */
public class xmas extends ListenerAdapter implements SlashHandler {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!"xmas".equals(e.getName())) {
            return;
        }

        OptionMapping nameOpt = e.getOption("name");

        if (nameOpt == null || nameOpt.getAsString().isBlank()) {
            e.reply("Usage: /xmas name:<player>").setEphemeral(true).queue();
            return;
        }

        final String name = nameOpt.getAsString().trim();
        Player player = PlayerHandler.getPlayerByDisplayName(name);

        // File I/O can take time, so defer the reply to avoid Discord timing out the interaction.
        e.deferReply(true).queue();

        if (player != null) {
            handleOnlinePlayer(player);
            e.getHook()
                    .editOriginal("Logged XMAS info for " + player.getDisplayName() + " and same-IP matches.")
                    .queue();
        } else {
            printData(name);
            e.getHook()
                    .editOriginal(name + " is offline. Scanned save files and logged XMAS info.")
                    .queue();
        }
    }

    private void handleOnlinePlayer(Player player) {
        if (player == null) {
            return;
        }

        logCompletedQuestPlayer(player);

        PlayerAddresses addresses = player.getValidAddresses();

        if (addresses == null || addresses.getIp() == null) {
            return;
        }

        List<Player> sameIpPlayers = PlayerHandler.nonNullStream()
                .filter(p -> p != null && p.connectedFrom != null)
                .filter(p -> p.connectedFrom.equals(addresses.getIp()))
                .collect(Collectors.toList());

        for (Player otherPlayer : sameIpPlayers) {
            if (otherPlayer == null) {
                continue;
            }

            if (hasCompletedXmasQuest(otherPlayer)) {
                logCompletedQuestPlayer(otherPlayer);
            }
        }
    }

    private boolean hasCompletedXmasQuest(Player player) {
        if (player == null || player.getQuesting() == null || player.getQuesting().getQuestList() == null) {
            return false;
        }

        for (Quest quest : player.getQuesting().getQuestList()) {
            if (quest == null || quest.getName() == null) {
                continue;
            }

            if (quest.getName().equalsIgnoreCase("santa's troubles") && quest.getStage() >= 17) {
                return true;
            }
        }

        return false;
    }

    private void logCompletedQuestPlayer(Player player) {
        if (player == null) {
            return;
        }

        Discord.writeXmasMessage("[XMAS]: " + player.getDisplayName()
                + " MAC " + player.getMacAddress()
                + " UUID " + player.getUUID()
                + " IP " + player.getIpAddress()
                + " has completed Santa's Troubles.");
    }

    private void printData(String name) {
        File charFile = getPlayerSaveFile(name);

        if (!charFile.isFile()) {
            Discord.writeXmasMessage("[XMAS]: " + name + ", this account doesn't exist.");
            return;
        }

        String uuid = "";
        StringBuilder message = new StringBuilder();

        try (BufferedReader characterFile = new BufferedReader(new FileReader(charFile))) {
            String line;

            while ((line = characterFile.readLine()) != null) {
                line = line.trim();

                int spot = line.indexOf('=');

                if (spot <= -1) {
                    continue;
                }

                String token = line.substring(0, spot).trim();
                String token2 = line.substring(spot + 1).trim();

                switch (token) {
                    case "character-username":
                        appendPart(message, "Player-Name = " + token2);
                        break;

                    case "character-uuid":
                        appendPart(message, "Player-Unique-User-ID = " + token2);
                        uuid = token2;
                        break;

                    case "Santa's Troubles":
                        appendPart(message, "Player-Xmas-Quest-Stage = " + token2);
                        break;
                }
            }

            if (message.length() > 0) {
                Discord.writeXmasMessage("[XMAS]: " + message);
            }

            if (!uuid.isEmpty()) {
                searchFilesByUUID(uuid);
            }

        } catch (IOException ex) {
            Misc.println(name + ": error loading file.");
        }
    }

    private void printDataz(String name) {
        File charFile = getPlayerSaveFile(name);

        if (!charFile.isFile()) {
            Discord.writeXmasMessage("[XMAS]: " + name + ", this account doesn't exist.");
            return;
        }

        StringBuilder message = new StringBuilder();

        try (BufferedReader characterFile = new BufferedReader(new FileReader(charFile))) {
            String line;

            while ((line = characterFile.readLine()) != null) {
                line = line.trim();

                int spot = line.indexOf('=');

                if (spot <= -1) {
                    continue;
                }

                String token = line.substring(0, spot).trim();
                String token2 = line.substring(spot + 1).trim();

                switch (token) {
                    case "character-username":
                        appendPart(message, "Player-Name = " + token2);
                        break;

                    case "character-mac-address":
                        appendPart(message, "Player-Mac-Address = " + token2);
                        break;

                    case "character-uuid":
                        appendPart(message, "Player-Unique-User-ID = " + token2);
                        break;

                    case "character-ip-address":
                        appendPart(message, "Player-IP-Address = " + token2);
                        break;

                    case "Santa's Troubles":
                        appendPart(message, "Player-Xmas-Quest-Stage = " + token2);
                        break;
                }
            }

            if (message.length() > 0) {
                Discord.writeXmasMessage("[XMAS]: " + message);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void searchFilesByUUID(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return;
        }

        File folder = new File(getSaveDirectory());
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null || listOfFiles.length == 0) {
            return;
        }

        for (File file : listOfFiles) {
            if (file == null || !file.isFile()) {
                continue;
            }

            try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)))) {
                while (scanner.hasNext()) {
                    String word = scanner.next();

                    if (word.equalsIgnoreCase(uuid)) {
                        String fileName = file.getName();
                        String playerName = fileName.contains(".")
                                ? fileName.substring(0, fileName.lastIndexOf('.'))
                                : fileName;

                        printDataz(playerName.toLowerCase());
                        break;
                    }
                }

            } catch (FileNotFoundException ignored) {
            }
        }
    }

    private File getPlayerSaveFile(String name) {
        return new File(getSaveDirectory() + name.toLowerCase() + ".txt");
    }

    private void appendPart(StringBuilder builder, String value) {
        if (builder.length() > 0) {
            builder.append(", ");
        }

        builder.append(value);
    }

    @Override
    public void handle(SlashCommandInteractionEvent e) {
        onSlashCommandInteraction(e);
    }
}