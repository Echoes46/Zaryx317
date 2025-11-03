package io.zaryx.util.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Right;
import io.zaryx.model.entity.player.save.PlayerSave;
import io.zaryx.model.items.bank.BankItem;
import io.zaryx.model.items.bank.BankTab;
import io.zaryx.util.Misc;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import javax.security.auth.login.LoginException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Discord extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Discord.class);
    private static final Map<String, TextChannel> channels = new ConcurrentHashMap<>();
    public static JDA jda = null;

    public static String PREFIX = "::";
    public static String OWNER_ROLE = "1182565375045533797";//Currently set to God Tier on beta.
    public static String MANAGER_ROLE = "1373344420400988314";
    public static String DEVELOPER_ROLE = "1182565327540854866";
    public static String ADMIN_ROLE = "1182542511428866170";
    public static String GLOBAL_MOD_ROLE = "1264969673988898929";
    public static String SUPPORT_ROLE = "1364274082878853130";
    private static final long GUILD_ID = 1182479719095078914L;

    /** New channel IDs  Created by Khaos*/
    private static final long CH_IN_GAME_FEED_HELP   = 1427687377144516691L; // help chat feed
    private static final long CH_ACHIEVEMENTS        = 1427687494723567646L; // 99s etc.
    private static final long CH_DROP_LOG            = 1427687582774595695L; // rare drops
    private static final long CH_PVP                 = 1427704620016336916L; // pvp kills
    private static final long CH_GRAND_EXCHANGE      = 1427762559754178734L; // GE listings
    private static final long CH_BOT_SPAM            = 1429871344908111872L; // bot commands output
    private static final long CH_CHARACTER_LINK      = 1429871489372389478L; // discord char linking
    private static final long CH_EVENTS              = 1429883544695472128L; // world events

    private static final NumberFormat NF = NumberFormat.getInstance();

    public static Guild getGuild() {
        JDA j = getJDA();
        return (j == null) ? null : j.getGuildById(GUILD_ID);
    }

    /**
     * Write to a channel that contains misc. types of information about player activity.
     */
    public static void writeServerSyncMessage(String message, Object...args) {
        sendChannelMessage(1363971683047964672L, message, args);//Server-Logs
    }

    public static void sendCriticalWarning(Player player, String time, long oldTotalNomad, long newTotalNomad, boolean nomad) {
        String wealthType = nomad ? "Nomad" : "Coins";
        String message = String.format(
                "CRITICAL WARNING: Player %s has experienced a significant increase in %s wealth.\n" +
                        "Play time: %s\n%s wealth: %d => %d (Increase: %s)",
                player.getDisplayName(), wealthType, time, wealthType, oldTotalNomad, newTotalNomad,
                NumberFormat.getInstance().format(newTotalNomad - oldTotalNomad)
        );
        sendChannelMessage(1183132345784606780L, message);
    }

    public static void writeOnlineNotification(String message, Object...args) {
        sendChannelMessage(1429893722618728499L, message, args);//Bot-Information
    }

    public static void writeIngameEvents(String message, Object...args) {
        sendChannelMessage(1429883544695472128L, message, args);
    }

    public static void writeXmasMessage(String message, Object...args) {
        sendChannelMessage(1429883544695472128L, message, args);//xmas-logs
    }

    public static void writeSuggestionMessage(String message, Object...args) {
        sendChannelMessage(1363971683047964672L, message, args);//mod-comms
    }

    public static void writeCheatEngineMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
    }

    public static void writeAchievements(String message, Object...args) {
        sendChannelMessage(CH_ACHIEVEMENTS, message, args);
    }

    public static void writeGiveLog(String message, Object...args) {
        sendChannelMessage(1363971683047964672L, message, args);
    }

    /** Discord chat methods by Khaos*/

    /** In-game feed: mirror all Help chat messages */
    public static void writeHelpChat(String playerName, String message) {
        sendChannelMessage(CH_IN_GAME_FEED_HELP, "**Help** | **%s**: %s", playerName, message);
    }

    /** Achievements: 99s */
    public static void writeAchievement99(String playerName, String skillName) {
        sendChannelMessage(CH_ACHIEVEMENTS, "**%s** just achieved **99 %s**!", playerName, skillName);
    }

    /** Achievements: flexible milestone helper  */
    public static void writeAchievement(String playerName, String description) {
        sendChannelMessage(CH_ACHIEVEMENTS, "**%s** %s", playerName, description);
    }

    /** Drop log: rare boss drops */
    public static void writeRareDrop(String playerName, String npcName, String itemName, int qty, double dropRate, int killCount) {
        String rate = dropRate > 0 ? String.format(" (~1/%.0f)", dropRate) : "";
        String kc   = killCount > 0 ? " | KC: " + NF.format(killCount) : "";
        sendChannelMessage(CH_DROP_LOG, "**%s** received: **%s x%d** from **%s**%s%s",
                playerName, itemName, Math.max(qty, 1), npcName, rate, kc);
    }

    /** PvP: who killed who */
    public static void writePvpKill(String killer, String victim, String location, int riskedWealth) {
        String loc = (location != null && !location.isEmpty()) ? " at **" + location + "**" : "";
        String risk = riskedWealth > 0 ? " | Risked: " + NF.format(riskedWealth) : "";
        sendChannelMessage(CH_PVP, "**%s** defeated **%s**%s%s", killer, victim, loc, risk);
    }

    /** Grand Exchange: listing posted (sell/buy) */
    public static void writeGeListing(String seller, String itemName, int quantity, long priceEach) {
        long total = Math.max(0, quantity) * Math.max(0L, priceEach);
        sendChannelMessage(CH_GRAND_EXCHANGE, "**%s** listed **%s x%d** for **%s** each (Total: **%s**)",
                seller, itemName, Math.max(quantity,1), NF.format(priceEach), NF.format(total));
    }

    /** Bot-spam: */
    public static void writeBotSpam(String message, Object... args) {
        sendChannelMessage(CH_BOT_SPAM, message, args);
    }

    /** Bot-spam: players online convenience */
    public static void writePlayersOnline(int count) {
        sendChannelMessage(CH_BOT_SPAM, "Players online: **%s**", NF.format(count));
    }

    /** Character link: successful link notice */
    public static void writeCharacterLinked(String playerName, long discordUserId) {
        sendChannelMessage(CH_CHARACTER_LINK, "**%s** linked with <@%d> successfully.", playerName, discordUserId);
    }

    /** Character link: failed link or code mismatch */
    public static void writeCharacterLinkFailed(String playerName, long discordUserId, String reason) {
        sendChannelMessage(CH_CHARACTER_LINK, "Link failed for **%s** (<@%d>): %s", playerName, discordUserId, reason);
    }

    /** World events: broadcast any server/world event */
    public static void writeWorldEvent(String title, String details) {
        if (details == null || details.isEmpty()) {
            sendChannelMessage(CH_EVENTS, "**%s**", title);
        } else {
            sendChannelMessage(CH_EVENTS, "**%s** — %s", title, details);
        }
    }

    /**
     * Write to a channel that should not be ignored by staff.
     */
    public static void writeAddressSwapMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
    }

    /** Null-safe, guild-scoped sender */
    private static void sendChannelMessage(long channelId, String message, Object... args) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) return;

        Server.getIoExecutorService().submit(() -> {
            try {
                Guild g = getGuild();
                if (g == null) return; // Not in guild or JDA not ready

                TextChannel ch = g.getTextChannelById(channelId);
                if (ch == null) return; // channel not in this guild or no access

                ch.sendMessage(Misc.replaceBracketsWithArguments(message, args)).queue();
            } catch (Exception e) {
                System.out.println("[Discord] sendChannelMessage error: " + e.getMessage());
            }
        });
    }

    private static TextChannel getChannel(String name) {
        if (Configuration.DISABLE_DISCORD_MESSAGING)
            return null;
        if (channels.containsKey(name))
            return channels.get(name);

        List<TextChannel> foundChannels = getJDA().getTextChannelsByName(name, true);
        if (foundChannels.isEmpty()) {
            logger.error("No discord channel found with name: " + name);
            return null;
        }

        TextChannel channel = foundChannels.get(0);
        channels.put(name, channel);
        return channels.get(name);
    }

    public static JDA getJDA() {
        return jda;
    }

    private static boolean enabled() {
        return !Configuration.DISABLE_DISCORD_MESSAGING;
    }

    public void init() {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            System.out.println("Discord messaging disabled via config.");
            return;
        }
        if (jda != null) {
            System.out.println("Discord JDA already initialized; skipping.");
            return;
        }

        final String token = TokenProvider.getToken();
        if (token == null || token.isEmpty()) {
            System.err.println("[Discord] No token found. Set DISCORD_TOKEN env var, -Ddiscord.token, or ./config/discord.properties");
            return;
        }

        System.out.println("Loading Discord Bot...");

        try {
            JDABuilder builder = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_PRESENCES)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES)
                    .enableCache(CacheFlag.ACTIVITY)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL);

            jda = builder.build();
            jda.awaitReady();

            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("Zaryx 317"));
            jda.addEventListener(this);

            Guild g = getGuild();
            if (g == null) {
                System.err.println("[Discord] Bot is not in the expected guild: " + GUILD_ID);
            } else {
                g.loadMembers();
            }

            System.out.println("Discord bot ready as " + jda.getSelfUser().getAsTag());
        } catch (LoginException e) {
            System.err.println("[Discord] LoginException during JDA init: " + e.getMessage());
            e.printStackTrace();
            jda = null;
        } catch (Exception e) {
            System.err.println("[Discord] Failed to initialize JDA: " + e.getMessage());
            e.printStackTrace();
            jda = null;
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            System.out.println("Discord messaging disabled via config.");
            return;
        }

        if (e.getAuthor().isBot()) {
            return;
        }

        // Ignore messages from other servers
        if (e.getGuild().getIdLong() != GUILD_ID) {
            return;
        }

        if (e.getChannel().getIdLong() == 1429871489372389478L && e.getMessage().getContentDisplay().equalsIgnoreCase("!connect")) {
            User user = e.getAuthor();

            if (DiscordIntegration.connectedAccounts.containsValue(user.getIdLong())) {
                DiscordIntegration.sendPrivateMessage(user, e.getChannel(), "This discord account is already connected to another in-game account!");
            }

            if (DiscordIntegration.idForCode.containsValue(user.getIdLong())) {
                String code = null;
                for (Map.Entry<String, Long> entry : DiscordIntegration.idForCode.entrySet()) {
                    if (entry.getValue() == user.getIdLong()) {
                        code = entry.getKey();
                    }
                }

                if (code == null) {
                    code = DiscordIntegration.generateCode(4);
                }

                DiscordIntegration.sendPrivateMessage(user, e.getChannel(), "Hello! You have already generated a code! Enter the following in the discord integration prompt:\n"
                        + code);
                return;
            }

            String code = DiscordIntegration.generateCode(4);

            while (DiscordIntegration.idForCode.containsKey(code)) {
                code = DiscordIntegration.generateCode(4);
            }

            DiscordIntegration.idForCode.put(code, e.getAuthor().getIdLong());

            DiscordIntegration.sendPrivateMessage(user, e.getChannel(),
                    " **Hello! To Connect your Discord Follow the Steps Below !** " + "```To connect your discord account to your in-game account, enter the following in the discord integration prompt when you click \"sync\":\n"
                            + code + "```");
        } else if (e.getChannel().getIdLong() == 1183132345784606780L) {

            String text = e.getMessage().getContentRaw().toLowerCase();
            if (text.contains("::uneventban")) {
                String[] string = e.getMessage().getContentRaw().toLowerCase().split("-");
                String username = string[1].trim();

                // Read existing data from eventbans.json
                JSONParser jsonParser = new JSONParser();
                String eventBansFileName = "eventbans.json";

                try (BufferedReader eventBansReader = new BufferedReader(new FileReader(Server.getSaveDirectory() + eventBansFileName))) {
                    Object obj = jsonParser.parse(eventBansReader);
                    JSONArray eventBansArray = (JSONArray) obj;

                    // Find and remove the entry associated with the specified username
                    JSONArray updatedEventBansArray = new JSONArray();
                    for (Object entry : eventBansArray) {
                        JSONObject eventBan = (JSONObject) entry;
                        String entryUsername = (String) eventBan.get("username");

                        if (!username.equalsIgnoreCase(entryUsername)) {
                            updatedEventBansArray.add(eventBan);
                        }
                    }

                    // Write the updated data back to eventbans.json
                    try (FileWriter eventBansWriter = new FileWriter(Server.getSaveDirectory() + eventBansFileName)) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String prettyJson = gson.toJson(updatedEventBansArray);
                        eventBansWriter.write(prettyJson);
                    }

                    Discord.writeXmasMessage("Player " + username + " has been unbanned from the event.");
                } catch (IOException | ParseException ignoredEvent) {
                    Discord.writeXmasMessage("No ban for the username: " + username + " exists!");
                }
                return;
            }
            if (text.contains("::eventban")) {
                String[] string = e.getMessage().getContentRaw().toLowerCase().split("-");
                String username = string[1].trim();
                String userFileName = username + ".txt";
                String eventBansFileName = "eventbans.json";

                try {
                    // Read existing data from eventbans.json
                    JSONParser jsonParser = new JSONParser();
                    JSONArray eventBansArray;
                    try (BufferedReader eventBansReader = new BufferedReader(new FileReader(Server.getSaveDirectory() + eventBansFileName))) {
                        Object obj = jsonParser.parse(eventBansReader);
                        eventBansArray = (JSONArray) obj;
                    } catch (FileNotFoundException ignored) {
                        // If the file doesn't exist, create a new array
                        eventBansArray = new JSONArray();
                    }

                    // Check if an entry for the specified username already exists
                    boolean entryExists = false;
                    for (Object entry : eventBansArray) {
                        JSONObject eventBan = (JSONObject) entry;
                        String entryUsername = (String) eventBan.get("username");

                        if (username.equalsIgnoreCase(entryUsername)) {
                            entryExists = true;
                            break;
                        }
                    }

                    if (!entryExists) {
                        // Read user information from the username.txt file
                        try (BufferedReader reader = new BufferedReader(new FileReader(PlayerSave.getSaveDirectory() + userFileName))) {
                            String uuid = "";
                            String ipAddress = "";
                            String macAddress = "";

                            String line;
                            while ((line = reader.readLine()) != null) {
                                String[] parts = line.split("=");
                                if (parts.length == 2) {
                                    String key = parts[0].trim();
                                    String value = parts[1].trim();

                                    if ("character-uuid".equals(key)) {
                                        uuid = value;
                                    } else if ("character-ip-address".equals(key)) {
                                        ipAddress = value;
                                    } else if ("character-mac-address".equals(key)) {
                                        macAddress = value;
                                    }
                                }
                            }

                            // Create JSON object with user information
                            JSONObject userJson = new JSONObject();
                            userJson.put("username", username);

                            JSONObject addressJson = new JSONObject();
                            addressJson.put("uuid", uuid);
                            addressJson.put("ip_address", ipAddress);
                            addressJson.put("mac_address", macAddress);

                            userJson.put("address", addressJson);

                            // Add the new user information to the existing eventbans.json data
                            eventBansArray.add(userJson);

                            // Write the updated data back to eventbans.json
                            try (FileWriter eventBansWriter = new FileWriter(Server.getSaveDirectory() + eventBansFileName)) {
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                String prettyJson = gson.toJson(eventBansArray);
                                eventBansWriter.write(prettyJson);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                            Discord.writeXmasMessage("Event ban added successfully for " + username);
                        } catch (IOException ex) {
                            Discord.writeXmasMessage("Error processing event ban for " + username + " / " + ex.getMessage());
                        }
                    } else {
                        Discord.writeXmasMessage("Player " + username + " is already event-banned.");
                    }
                } catch (Exception ex) {
                    Discord.writeXmasMessage("Unexpected error: " + ex.getMessage());
                }
                return;
            }

            if (text.contains("::wealthcoins")) {
                List<String> lines = new ArrayList<>();

                for (Player player : PlayerHandler.getPlayers()) {
                    if (player != null && !player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
                        long coins = 0;

                        coins += player.getItems().getInventoryCount(995);

                        for (BankTab bankTab : player.getBank().getBankTab()) {
                            for (BankItem item : bankTab.getItems()) {
                                if (item.getId() - 1 == 995) {
                                    coins += item.getAmount();
                                }
                            }
                        }

                        lines.add("User: " + player.getDisplayName() + ", coins: " + Misc.formatCoins(coins));
                    }
                }

                List<String> messages = new ArrayList<>();
                StringBuilder currentMessage = new StringBuilder();
                for (String line : lines) {
                    if (currentMessage.length() + line.length() >= 1900) {
                        messages.add(currentMessage.toString());
                        currentMessage.setLength(0);
                    }
                    currentMessage.append(line).append("\n");
                }
                if (currentMessage.length() > 0) {
                    messages.add(currentMessage.toString());
                }

                for (String message : messages) {
                    Discord.writeXmasMessage(message);
                }
            }

            if (text.contains("::wealthplat")) {
                List<String> lines = new ArrayList<>();

                for (Player player : PlayerHandler.getPlayers()) {
                    if (player != null && !player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
                        long plat = 0;

                        plat += player.getItems().getInventoryCount(13204);

                        for (BankTab bankTab : player.getBank().getBankTab()) {
                            for (BankItem item : bankTab.getItems()) {
                                if (item.getId() - 1 == 13204) {
                                    plat += item.getAmount();
                                }
                            }
                        }

                        lines.add("User: " + player.getDisplayName() + ", plat: " + Misc.formatCoins(plat));
                    }
                }

                List<String> messages = new ArrayList<>();
                StringBuilder currentMessage = new StringBuilder();
                for (String line : lines) {
                    if (currentMessage.length() + line.length() >= 1900) {
                        messages.add(currentMessage.toString());
                        currentMessage.setLength(0);
                    }
                    currentMessage.append(line).append("\n");
                }
                if (currentMessage.length() > 0) {
                    messages.add(currentMessage.toString());
                }

                for (String message : messages) {
                    Discord.writeXmasMessage(message);
                }
            }

            if (text.contains("::wealthUpgrade")) {
                List<String> lines = new ArrayList<>();

                for (Player player : PlayerHandler.getPlayers()) {
                    if (player != null && !player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
                        long Upgrader = 0;

                        Upgrader += player.getItems().getInventoryCount(33237);
                        Upgrader += (player.getItems().getInventoryCount(691) * 10_000L);
                        Upgrader += (player.getItems().getInventoryCount(692) * 25_000L);
                        Upgrader += (player.getItems().getInventoryCount(693) * 50_000L);
                        Upgrader += (player.getItems().getInventoryCount(696) * 250_000L);

                        for (BankTab bankTab : player.getBank().getBankTab()) {
                            for (BankItem item : bankTab.getItems()) {
                                if (item.getId() - 1 == 33237) {
                                    Upgrader += item.getAmount();
                                }
                                if (item.getId() - 1 == 691) {
                                    Upgrader += (item.getAmount() * 10_000L);
                                }
                                if (item.getId() - 1 == 692) {
                                    Upgrader += (item.getAmount() * 25_000L);
                                }
                                if (item.getId() - 1 == 693) {
                                    Upgrader += (item.getAmount() * 50_000L);
                                }
                                if (item.getId() - 1 == 696) {
                                    Upgrader += (item.getAmount() * 250_000L);
                                }
                            }
                        }

                        Upgrader += player.foundryPoints;

                        lines.add("User: " + player.getDisplayName() + ", Upgrader: " + Misc.formatCoins(Upgrader));
                    }
                }

                List<String> messages = new ArrayList<>();
                StringBuilder currentMessage = new StringBuilder();
                for (String line : lines) {
                    if (currentMessage.length() + line.length() >= 1900) {
                        messages.add(currentMessage.toString());
                        currentMessage.setLength(0);
                    }
                    currentMessage.append(line).append("\n");
                }
                if (currentMessage.length() > 0) {
                    messages.add(currentMessage.toString());
                }

                for (String message : messages) {
                    Discord.writeXmasMessage(message);
                }
            }

            if (text.contains("::scan")) {
                String[] string = e.getMessage().getContentRaw().toLowerCase().split("-");
                String name = string[1];

                String uuidPattern = "character-uuid\\s*=\\s*([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})";
                String macAddressPattern = "character-mac-address\\s*=\\s*([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})";
                String ipAddressPattern = "character-ip-address\\s*=\\s*([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)";

                Pattern uuidPatternCompiled = Pattern.compile(uuidPattern);
                Pattern macAddressPatternCompiled = Pattern.compile(macAddressPattern);
                Pattern ipAddressPatternCompiled = Pattern.compile(ipAddressPattern);

                try {
                    String uuid = null;
                    String macAddress = null;
                    String ipAddress = null;

                    File targetFile = new File(PlayerSave.getSaveDirectory(), name+".txt");

                    if (!targetFile.isFile()) {
                        System.out.println("Target file not found: " + name+".txt");
                        return;
                    }

                    List<String> lines = Files.readAllLines(targetFile.toPath());

                    for (String line : lines) {
                        Matcher uuidMatcher = uuidPatternCompiled.matcher(line);
                        if (uuidMatcher.find()) {
                            uuid = uuidMatcher.group(1);
                        }

                        Matcher macAddressMatcher = macAddressPatternCompiled.matcher(line);
                        if (macAddressMatcher.find()) {
                            macAddress = line.replace("character-mac-address = ", "");
                        }

                        Matcher ipAddressMatcher = ipAddressPatternCompiled.matcher(line);
                        if (ipAddressMatcher.find()) {
                            ipAddress = ipAddressMatcher.group(1);
                        }
                    }

                    if (uuid != null || macAddress != null || ipAddress != null) {
                        System.out.println("Found character information:");
                        if (uuid != null) {
                            System.out.println("UUID: " + uuid);
                        }
                        if (macAddress != null) {
                            System.out.println("MAC Address: " + macAddress);
                        }
                        if (ipAddress != null) {
                            System.out.println("IP Address: " + ipAddress);
                        }

                        scanDirectoryForCharacterInfo(PlayerSave.getSaveDirectory(), uuid, macAddress, ipAddress);
                    } else {
                        System.out.println("Character information not found in the target file.");
                    }
                } catch (IOException ex) {

                }
                return;
            }

            DiscordCommands command = DiscordCommands.isCommand(e);
            if (Objects.isNull(command)) {
                return;
            }
            command.getAdapter().onGuildMessageReceived(e);
        }
    }

    private static void scanDirectoryForCharacterInfo(String directoryPath, String uuid, String macAddress, String ipAddress) {
        File directory = new File(directoryPath);

        if (!directory.isDirectory()) {
            System.out.println("Specified path is not a directory.");
            return;
        }

        ArrayList<String> character_names = new ArrayList<>();

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        if (containsCharacterInfo(file.toPath(), uuid, macAddress, ipAddress)) {
                            if (!character_names.contains(file.getName().replace(".txt", ""))) {
                                character_names.add(file.getName().replace(".txt", ""));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Discord.writeXmasMessage(Arrays.toString(character_names.toArray()));
    }

    private static boolean containsCharacterInfo(Path filePath, String uuid, String macAddress, String ipAddress) throws IOException {
        List<String> lines = Files.readAllLines(filePath);

        for (String line : lines) {
            if ((uuid != null && line.contains(uuid)) ||
                    (macAddress != null && line.contains(macAddress)) ||
                    (ipAddress != null && line.contains(ipAddress))) {
                return true;
            }
        }

        return false;
    }

}
