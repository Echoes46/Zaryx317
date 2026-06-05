package io.zaryx.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Updated by Khaos
 */
public class HighscoresExporter {

    private static final File CHARACTER_SAVE_DIRECTORY =
            new File("save_files/public/character_saves");

    private static final String HIGHSCORES_OUTPUT_FILE =
            System.getProperty("zaryx.highscoresFile", "highscores.json");

    public static void export() {
        File[] saves = CHARACTER_SAVE_DIRECTORY.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".txt")
        );

        if (saves == null) {
            System.out.println("Highscores export failed: character save directory not found.");
            return;
        }

        Arrays.sort(saves);

        List<HighscorePlayer> players = new ArrayList<>();

        for (File save : saves) {
            HighscorePlayer player = parseSave(save);

            if (player != null) {
                players.add(player);
            }
        }

        players.sort(
                Comparator.comparingInt((HighscorePlayer p) -> p.totalLevel).reversed()
                        .thenComparing(Comparator.comparingLong((HighscorePlayer p) -> p.totalXp).reversed())
        );

        for (int i = 0; i < players.size(); i++) {
            players.get(i).rank = i + 1;
        }

        writeJson(players);
    }

    private static HighscorePlayer parseSave(File save) {
        try {
            List<String> lines = Files.readAllLines(save.toPath());

            HighscorePlayer player = new HighscorePlayer();
            player.username = "";
            player.displayName = "";
            player.rights = 0;
            player.gameMode = "Normal";
            player.gameModeKey = "normal";
            player.xpRate = "1x";
            player.xpRateKey = "1";
            player.levels = new int[25];
            player.experience = new int[25];

            for (String line : lines) {
                line = line.trim();

                if (line.startsWith("character-username =")) {
                    player.username = value(line);
                } else if (line.startsWith("display-name =")) {
                    player.displayName = value(line);
                } else if (line.startsWith("character-rights =")) {
                    player.rights = parseInt(value(line));
                } else if (line.startsWith("mode =")) {
                    applyGameMode(player, value(line));
                } else if (line.startsWith("expmode =")) {
                    applyXpRate(player, value(line));
                } else if (line.startsWith("character-skill =")) {
                    String[] parts = value(line).split("\\s+");

                    if (parts.length >= 3) {
                        int skillId = parseInt(parts[0]);
                        int level = parseInt(parts[1]);
                        int xp = parseInt(parts[2]);

                        if (skillId >= 0 && skillId < player.levels.length) {
                            player.levels[skillId] = level;
                            player.experience[skillId] = xp;
                        }
                    }
                }
            }

            if (player.displayName == null || player.displayName.isEmpty()) {
                player.displayName = player.username;
            }

// Exclude staff/admin accounts from highscores for now.
            if (player.rights > 0) {
                return null;
            }

            for (int i = 0; i < player.levels.length; i++) {
                player.totalLevel += player.levels[i];
                player.totalXp += player.experience[i];
            }

            return player;
        } catch (Exception e) {
            System.out.println("Failed to parse highscores save: " + save.getName());
            e.printStackTrace();
            return null;
        }
    }

    private static void applyGameMode(HighscorePlayer player, String rawMode) {
        String normalized = normalize(rawMode);

        switch (normalized) {
            case "standard":
            case "normal":
                player.gameMode = "Normal";
                player.gameModeKey = "normal";
                break;

            case "ironman":
            case "iron":
                player.gameMode = "Ironman";
                player.gameModeKey = "ironman";
                break;

            case "ultimateironman":
            case "ultimate":
            case "uim":
                player.gameMode = "Ultimate Ironman";
                player.gameModeKey = "ultimate_ironman";
                break;

            case "hardcoreironman":
            case "hardcore":
            case "hcim":
                player.gameMode = "Hardcore Ironman";
                player.gameModeKey = "hardcore_ironman";
                break;

            case "groupironman":
            case "groupiron":
            case "gim":
                player.gameMode = "Group Ironman";
                player.gameModeKey = "group_ironman";
                break;

            case "wildyman":
            case "wildy":
                player.gameMode = "Wildyman";
                player.gameModeKey = "wildyman";
                break;

            case "groupwildyman":
            case "groupwildy":
            case "gwildy":
            case "gwm":
                player.gameMode = "Group Wildyman";
                player.gameModeKey = "group_wildyman";
                break;

            default:
                player.gameMode = toDisplayName(rawMode);
                player.gameModeKey = toKey(rawMode);
                break;
        }
    }

    private static void applyXpRate(HighscorePlayer player, String rawXpMode) {
        String normalized = normalize(rawXpMode);

        switch (normalized) {
            case "onetimes":
            case "onetime":
            case "one":
            case "1":
            case "1x":
                player.xpRate = "1x";
                player.xpRateKey = "1";
                break;

            case "fivetimes":
            case "fivetime":
            case "five":
            case "5":
            case "5x":
                player.xpRate = "5x";
                player.xpRateKey = "5";
                break;

            case "tentimes":
            case "tentime":
            case "ten":
            case "10":
            case "10x":
                player.xpRate = "10x";
                player.xpRateKey = "10";
                break;

            case "twentyfivetimes":
            case "twentyfivetime":
            case "twentyfive":
            case "25":
            case "25x":
                player.xpRate = "25x";
                player.xpRateKey = "25";
                break;

            default:
                String digits = rawXpMode.replaceAll("[^0-9]", "");

                if (!digits.isEmpty()) {
                    player.xpRate = digits + "x";
                    player.xpRateKey = digits;
                } else {
                    player.xpRate = rawXpMode;
                    player.xpRateKey = normalize(rawXpMode);
                }
                break;
        }
    }

    private static void writeJson(List<HighscorePlayer> players) {
        try {
            java.nio.file.Path outputPath = Paths.get(HIGHSCORES_OUTPUT_FILE);

            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }

            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"lastUpdated\": ").append(System.currentTimeMillis()).append(",\n");
            json.append("  \"players\": [\n");

            for (int i = 0; i < players.size(); i++) {
                HighscorePlayer player = players.get(i);

                json.append("    {\n");
                json.append("      \"rank\": ").append(player.rank).append(",\n");
                json.append("      \"username\": \"").append(escape(player.username)).append("\",\n");
                json.append("      \"displayName\": \"").append(escape(player.displayName)).append("\",\n");
                json.append("      \"rights\": ").append(player.rights).append(",\n");
                json.append("      \"gameMode\": \"").append(escape(player.gameMode)).append("\",\n");
                json.append("      \"gameModeKey\": \"").append(escape(player.gameModeKey)).append("\",\n");
                json.append("      \"xpRate\": \"").append(escape(player.xpRate)).append("\",\n");
                json.append("      \"xpRateKey\": \"").append(escape(player.xpRateKey)).append("\",\n");
                json.append("      \"totalLevel\": ").append(player.totalLevel).append(",\n");
                json.append("      \"totalXp\": ").append(player.totalXp).append(",\n");

                json.append("      \"levels\": [");
                for (int s = 0; s < player.levels.length; s++) {
                    json.append(player.levels[s]);
                    if (s < player.levels.length - 1) {
                        json.append(", ");
                    }
                }
                json.append("],\n");

                json.append("      \"experience\": [");
                for (int s = 0; s < player.experience.length; s++) {
                    json.append(player.experience[s]);
                    if (s < player.experience.length - 1) {
                        json.append(", ");
                    }
                }
                json.append("]\n");

                json.append("    }");

                if (i < players.size() - 1) {
                    json.append(",");
                }

                json.append("\n");
            }

            json.append("  ]\n");
            json.append("}\n");

            Files.write(outputPath, json.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("Failed to write highscores JSON.");
            e.printStackTrace();
        }
    }

    private static String value(String line) {
        int index = line.indexOf("=");

        if (index == -1) {
            return "";
        }

        return line.substring(index + 1).trim();
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase()
                .replace("_", "")
                .replace("-", "")
                .replace(" ", "")
                .replace("x", "x");
    }

    private static String toKey(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        return value
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+", "")
                .replaceAll("_+$", "");
    }

    private static String toDisplayName(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Unknown";
        }

        String cleaned = value.trim().replace("_", " ").replace("-", " ").toLowerCase();
        String[] words = cleaned.split("\\s+");
        StringBuilder display = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            if (display.length() > 0) {
                display.append(" ");
            }

            display.append(Character.toUpperCase(word.charAt(0)));

            if (word.length() > 1) {
                display.append(word.substring(1));
            }
        }

        return display.toString();
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private static class HighscorePlayer {
        int rank;
        String username;
        String displayName;
        int rights;
        String gameMode;
        String gameModeKey;
        String xpRate;
        String xpRateKey;
        int totalLevel;
        long totalXp;
        int[] levels;
        int[] experience;
    }
}
