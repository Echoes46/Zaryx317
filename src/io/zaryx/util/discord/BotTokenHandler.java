package io.zaryx.util.discord;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;

public class BotTokenHandler {

    private static final String CONFIG_FILE = "discord-config.json";

    public static final String token = loadToken();

    private static String loadToken() {
        try {
            File file = new File(CONFIG_FILE);

            if (!file.exists()) {
                throw new RuntimeException("Missing " + CONFIG_FILE + ". Create it in the server root folder.");
            }

            try (FileReader reader = new FileReader(file)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                if (!json.has("token") || json.get("token").getAsString().trim().isEmpty()) {
                    throw new RuntimeException("Missing or empty token in " + CONFIG_FILE);
                }

                return json.get("token").getAsString().trim();
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load Discord bot token from " + CONFIG_FILE, e);
        }
    }

    private BotTokenHandler() {
        // Utility class
    }
}