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

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static io.zaryx.model.entity.player.save.PlayerSave.getSaveDirectory;

/**
 * Slash command: /xmas name:<string>
 * - If player online: logs MAC/UUID/IP and any same-IP players who have completed the "Santa's Troubles" quest.
 * - If player offline: scans save files to print identifiers & quest stage; finds others with same UUID and prints theirs too.
 */
public class xmas extends ListenerAdapter implements SlashHandler{

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (!"xmas".equals(e.getName())) return;

        OptionMapping nameOpt = e.getOption("name");
        if (nameOpt == null || nameOpt.getAsString().isBlank()) {
            e.reply("Usage: `/xmas name:<player>`").setEphemeral(true).queue();
            return;
        }

        final String name = nameOpt.getAsString().trim();
        Player player = PlayerHandler.getPlayerByDisplayName(name);

        // We may do file I/O; defer so Discord doesn't time out the interaction.
        e.deferReply(true).queue(); // ephemeral

        if (player != null) {
            handleOnlinePlayer(player);
            e.getHook().editOriginal("✅ Logged XMAS info for **" + player.getDisplayName() + "** (and same-IP matches).").queue();
        } else {
            printData(name);
            e.getHook().editOriginal("ℹ️ `" + name + "` is offline. Scanned save files and logged XMAS info.").queue();
        }
    }

    /* ---------------- Online branch ---------------- */

    private void handleOnlinePlayer(Player player) {
        PlayerAddresses addresses = player.getValidAddresses();

        Discord.writeXmasMessage("[XMAS]: " + player.getDisplayName() +
                " MAC " + player.getMacAddress() +
                " UUID " + player.getUUID() +
                " IP " + player.getIpAddress() +
                " has also completed the quest!"
        );

        if (addresses == null || addresses.getIp() == null) return;

        List<Player> clientList = PlayerHandler.nonNullStream()
                .filter(p -> p.connectedFrom.equals(addresses.getIp()))
                .collect(Collectors.toList());

        for (Player pz : clientList) {
            for (Quest quest : pz.getQuesting().getQuestList()) {
                if (quest.getName().equalsIgnoreCase("santa's troubles") && quest.getStage() >= 17) {
                    Discord.writeXmasMessage("[XMAS]: " + pz.getDisplayName() +
                            " MAC " + pz.getMacAddress() +
                            " UUID " + pz.getUUID() +
                            " IP " + pz.getIpAddress() +
                            " has also completed the quest!"
                    );
                }
            }
        }
    }

    /* ---------------- Offline branch (file scan) ---------------- */

    private void printData(String name) {
        String filePath = getSaveDirectory() + name.toLowerCase() + ".txt";
        File charFile = new File(filePath);

        if (!charFile.isFile()) {
            Discord.writeXmasMessage("[XMAS]: " + name + ", this account doesn't exist");
            return;
        }

        String uuid = "";
        try (BufferedReader characterfile = new BufferedReader(new FileReader(charFile))) {
            String line;
            String token;
            String token2;
            StringBuilder message = new StringBuilder();

            while ((line = characterfile.readLine()) != null) {
                line = line.trim();
                int spot = line.indexOf('=');
                if (spot > -1) {
                    token  = line.substring(0, spot).trim();
                    token2 = line.substring(spot + 1).trim();

                    switch (token) {
                        case "character-username":
                            message.append("Player-Name = ").append(token2).append(", ");
                            break;
                        case "character-uuid":
                            message.append("Player-Unique-User-ID = ").append(token2).append(", ");
                            uuid = token2;
                            // note: deliberately no break to mimic legacy fallthrough
                        case "Santa's Troubles":
                            message.append("Player-Xmas-Quest-Stage = ").append(token2);
                            break;
                    }
                }
            }

            if (!uuid.isEmpty()) {
                searchFilesByUUID(uuid);
            }
        } catch (IOException ex) {
            Misc.println(name + ": error loading file.");
        }
    }

    private void printDataz(String name) {
        String filePath = getSaveDirectory() + name.toLowerCase() + ".txt";
        File charFile = new File(filePath);

        if (!charFile.isFile()) {
            Discord.writeXmasMessage("[XMAS]: " + name + ", this account doesn't exist");
            return;
        }

        StringBuilder message = new StringBuilder();

        try (BufferedReader characterfile = new BufferedReader(new FileReader(charFile))) {
            String line;
            String token;
            String token2;

            while ((line = characterfile.readLine()) != null) {
                line = line.trim();
                int spot = line.indexOf('=');
                if (spot > -1) {
                    token  = line.substring(0, spot).trim();
                    token2 = line.substring(spot + 1).trim();

                    switch (token) {
                        case "character-username":
                            message.append("Player-Name = ").append(token2).append(", ");
                            break;
                        case "character-mac-address":
                            message.append("Player-Mac-Address = ").append(token2).append(", ");
                            break;
                        case "character-uuid":
                            message.append("Player-Unique-User-ID = ").append(token2).append(", ");
                            break;
                        case "character-ip-address":
                            message.append("Player-IP-Address = ").append(token2).append(", ");
                            break;
                        case "Santa's Troubles":
                            message.append("Player-Xmas-Quest-Stage = ").append(token2);
                            break;
                    }
                }
            }

            Discord.writeXmasMessage("[XMAS]: " + message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void searchFilesByUUID(String uuid) {
        String srcDir = getSaveDirectory();
        File folder = new File(srcDir);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null || listOfFiles.length == 0) return;

        for (File f : listOfFiles) {
            if (!f.isFile()) continue;

            try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(f)))) {
                while (scanner.hasNext()) {
                    String word = scanner.next();
                    if (word.equalsIgnoreCase(uuid)) {
                        String base = f.getName();
                        String name = base.contains(".") ? base.substring(0, base.lastIndexOf('.')) : base;
                        printDataz(name.toLowerCase());
                        break;
                    }
                }
            } catch (FileNotFoundException ignored) {
            }
        }
    }
    @Override
    public void handle(SlashCommandInteractionEvent e) {
        onSlashCommandInteraction(e);
    }
}
