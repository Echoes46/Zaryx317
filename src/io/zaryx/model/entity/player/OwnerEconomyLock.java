package io.zaryx.model.entity.player;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

/** Login-bound economy restrictions; only the server operator can change the switch. */
public final class OwnerEconomyLock {
    private static final boolean ENABLED = load(Path.of("etc/cfg/owner-economy-lock.properties"));
    private static final Set<String> SAFE_COMMANDS = Set.of("home", "pet", "pets", "activity", "tasks", "commands", "help", "rules", "players", "staff", "time", "discord", "website");
    private OwnerEconomyLock() { }

    static boolean load(Path path) {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(path)) {
            properties.load(reader);
            // Only an explicit false unlocks. Missing or malformed configuration stays locked.
            return !"false".equalsIgnoreCase(properties.getProperty("chasebanker.locked", "true").trim());
        } catch (IOException | IllegalArgumentException exception) {
            return true;
        }
    }
    public static boolean isTarget(String login) {
        return login != null && "chasebanker".equals(login.trim().toLowerCase(Locale.ROOT));
    }
    public static boolean isLocked(String login) { return ENABLED && isTarget(login); }
    public static boolean isLocked(Player player) { return player != null && isLocked(player.getLoginName()); }
    public static boolean deny(Player player) {
        if (!isLocked(player)) return false;
        player.sendMessage("Your account's economy lock prevents this action.");
        return true;
    }
    public static boolean denyTransfer(Player player, Player other) {
        if (!isLocked(player) && !isLocked(other)) return false;
        if (player != null) player.sendMessage("An account economy lock prevents this interaction.");
        return true;
    }
    static boolean safeCommand(String command) {
        if (command == null) return false;
        String text = command.trim().toLowerCase(Locale.ROOT);
        return SAFE_COMMANDS.contains(text.split("\\s+", 2)[0]);
    }
    public static boolean denyCommand(Player player, String command) {
        return isLocked(player) && !safeCommand(command) && deny(player);
    }
    public static void applyRank(Player player) {
        if (isTarget(player.getLoginName())) player.getRights().setPrimary(Right.STAFF_MANAGER);
    }
}
