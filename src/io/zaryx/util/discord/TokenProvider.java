package io.zaryx.util.discord;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class TokenProvider {
    private TokenProvider() {}

    /**
     * Lookup order:
     *  1) ENV var: DISCORD_TOKEN
     *  2) JVM prop: -Ddiscord.token=xxx
     *  3) File: ./config/discord.properties (key: token=...)
     */
    public static String getToken() {
        // 1) environment variable
        String tok = System.getenv("DISCORD_TOKEN");
        if (tok != null && !tok.isBlank()) return tok.trim();

        // 2) system property
        tok = System.getProperty("discord.token");
        if (tok != null && !tok.isBlank()) return tok.trim();

        // 3) properties file
        try (FileInputStream in = new FileInputStream("./config/discord.properties")) {
            Properties p = new Properties();
            p.load(in);
            tok = p.getProperty("token");
            if (tok != null && !tok.isBlank()) return tok.trim();
        } catch (IOException ignored) {}

        return null;
    }
}
