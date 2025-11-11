package io.zaryx.util.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.content.worldevent.WorldEventContainer;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.save.PlayerSave;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lord Hunterr
 * @Date 11/08/2025
 */


public class DiscordBot extends ListenerAdapter {
    /**
     * Helper Methods
     */

    public static DiscordBot INSTANCE;

    private final JDA jda;

    public static Map<String, Long> connectedAccounts = new HashMap<>();
    public static List<Long> disableMessage = new ArrayList<>();
    public static Map<String, Long> idForCode = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(DiscordBot.class);
    private static final Map<String, TextChannel> channels = new ConcurrentHashMap<>();
    private static final NumberFormat NF = NumberFormat.getInstance();

    public static JDA getJda() {
        if (INSTANCE == null) {
            throw new IllegalStateException("DiscordBot has not been initialized yet!");
        }
        return INSTANCE.jda;
    }

    private static Guild getGuild() {
        JDA j = getJda();
        return (j == null) ? null : j.getGuildById(DiscordChannelType.GUILD_ID.getChannelId());
    }

    private static boolean enabled() {
        return !Configuration.DISABLE_DISCORD_MESSAGING;
    }

    public static String generateCode(int length) {
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String special = "!@#$";
        String digits = "1234567890";
        String pool = lower + lower + special + digits;

        Random r = new Random();
        char[] out = new char[length];

        // Ensure at least one of each
        out[0] = lower.charAt(r.nextInt(lower.length()));
        out[1] = lower.charAt(r.nextInt(lower.length()));
        out[2] = special.charAt(r.nextInt(special.length()));
        out[3] = digits.charAt(r.nextInt(digits.length()));

        for (int i = 4; i < length; i++) {
            out[i] = pool.charAt(r.nextInt(pool.length()));
        }
        return new String(out);
    }

    /**
     * Initialization
     */

    public static void init() {
        if (INSTANCE != null) {
            logger.info("[Discord] Bot already initialized.");
            return;
        }
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            logger.info("[Discord] Messaging disabled via config.");
            return;
        }
        try {
            logger.info("[Discord] Connecting to Discord...");
            INSTANCE = new DiscordBot();
            INSTANCE.jda.awaitReady();
            INSTANCE.jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("Zaryx 317"));
            logger.info("[Discord] Connected as " + INSTANCE.jda.getSelfUser().getAsTag());
        } catch (LoginException | InterruptedException e) {
            throw new RuntimeException("Failed to initialize Discord bot", e);
        }
    }

    private DiscordBot() throws LoginException {
        jda = JDABuilder.createLight(Configuration.DISCORD_TOKEN,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES)
                .enableCache(CacheFlag.ACTIVITY)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setActivity(Activity.playing("Zaryx 317"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(this)
                .build();
    }

    /**
     * Messaging
     * @param type
     * @param message
     */

    public void sendMessage(final DiscordChannelType type, final String message) {
        if (jda.getStatus() == JDA.Status.CONNECTED) {
            final MessageBuilder builder = new MessageBuilder();
            builder.append(message);
            final MessageAction action = jda.getTextChannelById(type.getChannelId()).sendMessage(builder.build());
            action.queue();
        }
    }

    public void sendMessageEmbeds(final DiscordChannelType type, final MessageEmbed embed) {
        if (jda.getStatus() == JDA.Status.CONNECTED) {
            TextChannel channel = jda.getTextChannelById(type.getChannelId());
            if (channel != null) {
                channel.sendMessage(new MessageBuilder().setEmbed(embed).build()).queue();
            } else {
                System.err.println("ERROR: Discord channel not found for ID: " + type.getChannelId());
            }
        } else {
            System.err.println("ERROR: JDA is not connected. Current status: " + jda.getStatus());
        }
    }


    /* -------------------------------------------------------------------------------------------------------------------------- *\


    /**
     * All of the different messages sent for each channel.
     */

    /**
     * Grand Exchange: listing posted (sell/buy)
     */
    //public void writeGeListing(final MessageEmbed embed) {
    //    sendMessageEmbeds(DiscordChannelType.GE_LISTINGS, embed);
    //}

    /**
     * Drop log: rare boss drops
     */
    public void writeRareDrop(final MessageEmbed embed) {
        sendMessageEmbeds(DiscordChannelType.RARE_DROPS, embed);
    }

    /**
     * Trade Logs: Let's staff see all trades happening.
     */

    public void sendTradeLogs(final MessageEmbed embed) {
        sendMessageEmbeds(DiscordChannelType.TRADE_LOGS, embed);
    }

    /**
     * Server status: lets the players know
     */
    public void sendServerStatus(final MessageEmbed embed) {
        sendMessageEmbeds(DiscordChannelType.SERVER_STATUS, embed);
    }

    /**
     * Chat Logging: Lets staff see all messages in game.
     */

    public void sendChatLogs(final MessageEmbed embed) {
        sendMessageEmbeds(DiscordChannelType.CHAT_LOGS, embed);
    }

    /**
     * World events: broadcast any server/world event
     */
    public void sendWorldEvent(final MessageEmbed embed) {
        sendMessageEmbeds(DiscordChannelType.WORLD_EVENTS, embed);
    }

    /**
     * Character link: successful link notice
     */
    public void sendCharacterLinked(final MessageEmbed embed) {
        sendMessageEmbeds(DiscordChannelType.CHARACTER_LINKING, embed);
    }

    /**
     * Help Chat: Players needing staff help.
     */
    public void writeHelpChat(final MessageEmbed embed) {
        sendMessageEmbeds(DiscordChannelType.HELP_CHAT,embed);
    }

    /**
     * Achievements: 99s
     */
    public void writeAchievements99(final MessageEmbed embed) {
        sendMessageEmbeds(DiscordChannelType.ACHIEVEMENTS_LEVEL_UPS, embed);
    }

    /**
     * PvP: who killed who
     */
    public void writePvpKill(final MessageEmbed embed) {
        sendMessageEmbeds(DiscordChannelType.PVP_KILLS, embed);
    }

    /**
     * Bot-spam: players online convenience
     */
    public void writePlayersOnline(final MessageEmbed embed) {
        sendMessageEmbeds(DiscordChannelType.BOT_SPAM, embed);
    }

    /**
     * Staff Logging: Anything important that the staff need to know.
     */

    public void sendStaffLogs(final MessageEmbed embed) {
        sendMessageEmbeds(DiscordChannelType.STAFF_LOGS, embed);
    }

    /**
     * Sends info to the discord of all of the active stuff ingame:
     */

    public void sendSkillingEvents(final MessageEmbed embed) {
        sendMessageEmbeds(DiscordChannelType.SKILLING_EVENTS, embed);
    }


/* -------------------------------------------------------------------------------------------------------------------------- *\

    /**
     * Direct Message method
     */

    public static void sendPrivateMessage(User user, TextChannel fallbackChannel, String content) {
        if (!enabled() || user == null || content == null || content.isEmpty()) {
            return;
        }
        // If DMs are closed, nudge the user in the source channel
        ErrorHandler handler = new ErrorHandler()
                .handle(ErrorResponse.CANNOT_SEND_TO_USER, err -> {
                    if (fallbackChannel != null) {
                        fallbackChannel.sendMessage(user.getAsMention() + " You must enable your private messages first!").queue();
                    }
                });
        user.openPrivateChannel().queue(
                dm -> dm.sendMessage(content).queue(null, handler),
                failure -> {
                    if (fallbackChannel != null) {
                        fallbackChannel.sendMessage(user.getAsMention() + " I couldn't open a private channel with you.").queue();
                    }
                }
        );
    }

    /**
     * Broadcast a PM to all *linked & opted-in* users in the guild.
     */
    public static void sendPMS(String content) {
        if (!enabled() || content == null || content.isEmpty()) return;
        Guild guild = getGuild();
        if (guild == null) return;
        logger.info("[Discord] Sending PM broadcast to linked users…");
        for (Map.Entry<String, Long> entry : connectedAccounts.entrySet()) {
            Player player = PlayerHandler.getPlayerByLoginName(entry.getKey());
            if (player == null) continue;
            long discordId = entry.getValue();
            if (disableMessage.contains(discordId)) continue;
            Member member = guild.getMemberById(discordId);
            if (member == null) continue;
            User user = member.getUser();
            if (user == null) continue;
            ErrorHandler handler = new ErrorHandler()
                    .handle(ErrorResponse.CANNOT_SEND_TO_USER, err -> { /* silently ignore */ });
            user.openPrivateChannel().queue(
                    dm -> dm.sendMessage(content).queue(null, handler),
                    failure -> { /* ignore per-user failure */ }
            );
        }
    }

    /**
     * Account linking / syncing
     */

    public static void integrateAccount(Player player, String code) {
        if (!enabled() || player == null) return;

        if (connectedAccounts == null) {
            loadConnectedAccounts(); // safety
        }

        if (player.getDiscordUser() > 0) {
            player.sendMessage("You already have a connected discord account!");
            return;
        }

        if (!idForCode.containsKey(code)) {
            player.sendMessage("You have entered an invalid code! Try again.");
            return;
        }

        long userId = idForCode.remove(code);

        Long existing = connectedAccounts.get(player.getLoginName());
        if (existing != null && existing != userId) {
            player.sendMessage("This discord account is already linked to another player!");
            return;
        }

        JDA jda = getJda();
        if (jda == null) {
            player.sendMessage("Discord is not ready yet. Try again later.");
            return;
        }

        User user = jda.getUserById(userId);
        if (user == null) {
            player.sendMessage("Could not find your Discord user. Try again.");
            return;
        }

        String tag = user.getAsTag();
        player.sendMessage("You have connected the discord account '" + tag + "'.");
        connectedAccounts.put(player.getLoginName(), userId);
        player.setDiscordUser(userId);
        player.setDiscordTag(tag);
        updateDiscordInterface(player);
        if (DiscordBot.INSTANCE != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Player has used Discord Connect");
            embed.setColor(Color.BLUE);
            embed.setTimestamp(java.time.Instant.now());
            embed.addField("Player: ", player.getDisplayName(), false);
            DiscordBot.INSTANCE.sendCharacterLinked(embed.build());
        }
        if (DiscordBot.INSTANCE != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Player has used Discord Connect");
            embed.setColor(Color.BLUE);
            embed.setTimestamp(java.time.Instant.now());
            embed.addField("Player: ", player.getDisplayName(), false);
            embed.addField("IP Address: " , player.getIpAddress(), false);
            embed.addField("MAC Address: ", player.getMacAddress(), false);
            embed.addField("UUID Address: ", player.getUUID(), false);
            DiscordBot.INSTANCE.sendStaffLogs(embed.build());
        }

        if (!player.getDiscordlinked() && player.getDiscordPoints() <= 10) {
            player.amDonated += 10;
            player.updateRank();
            player.sendMessage("@mag@You received $10 to your total donated amount for linking your Discord account!");
            player.setDiscordlinked(true);
            player.setDiscordPoints(player.getDiscordPoints() + 10);
        }

        PlayerSave.saveGame(player);
        // TODO: Announce player syncing discord to notify others (if desired)
    }

    public static void setIntegration(Player player) {
        if (!enabled() || player == null) return;
        if (player.getDiscordUser() > 0 && player.getDiscordTag() != null) {
            connectedAccounts.put(player.getLoginName(), player.getDiscordUser());
        }
    }

    /**
     * Persistence (JSON)
    */
    public static void loadConnectedAccounts() {
        if (!enabled()) return;

        File file = new File("./save_files/discord/discordConnectedAccounts.json");
        if (!file.exists()) {
            logger.info("[Discord] No connected accounts file yet (that's fine).");
            return;
        }

        try (FileReader fileReader = new FileReader(file)) {
            JsonParser parser = new JsonParser();
            Gson gson = new GsonBuilder().create();
            JsonObject root = (JsonObject) parser.parse(fileReader);

            if (root.has("connectedAccounts")) {
                Map<String, Long> accounts = gson.fromJson(
                        root.get("connectedAccounts"),
                        new TypeToken<Map<String, Long>>() {}.getType()
                );
                connectedAccounts = (accounts != null) ? accounts : new HashMap<>();
            }

            if (root.has("disableMessage")) {
                Long[] data = gson.fromJson(root.get("disableMessage"), Long[].class);
                disableMessage = new ArrayList<>();
                if (data != null) {
                    Collections.addAll(disableMessage, data);
                }
            }

            logger.info("[Discord] Loaded connected accounts: " + connectedAccounts.size());
        } catch (Exception e) {
            logger.info("[Discord] Error loading connected accounts: " + e.getMessage());
        }
    }

    public static void saveConnectedAccounts() {
        if (!enabled()) return;
        File file = new File("./save_files/discord/discordConnectedAccounts.json");
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject object = new JsonObject();
            object.add("connectedAccounts", gson.toJsonTree(connectedAccounts));
            object.add("disableMessage", gson.toJsonTree(disableMessage));
            writer.write(gson.toJson(object));
            logger.info("[Discord] Saved connected accounts.");
        } catch (Exception e) {
            logger.info("[Discord] Error saving connected accounts: " + e.getMessage());
        }
    }

    /**
     * Discord Interface
     * @param player
     */

    public static void updateDiscordInterface(Player player) {
        if (player == null) return;
        if (player.getDiscordUser() <= 0) {
            player.getPA().sendString(37507, "@red@Inactive");
        } else {
            player.getPA().sendString(37507, "@whi@" + player.getDiscordTag());
        }
        if (disableMessage.contains(player.getDiscordUser())) {
            player.getPA().sendString(37508, "@whi@Active");
        } else {
            player.getPA().sendString(37508, "@red@Inactive");
        }
        Guild guild = getGuild();
        player.getPA().sendString(37509, "@red@Inactive");
        player.getPA().sendString(37510, "@red@Inactive");
        if (guild != null) {
            for (Member booster : guild.getBoosters()) {
                if (player.getDiscordUser() == booster.getIdLong()) {
                    player.getPA().sendString(37509, "@whi@Boosting!");
                    player.getPA().sendString(37510,
                            "@whi@Receiving 10% Damage Boost!" +
                                    "\\n@whi@Receiving 10% Rare rewards from raids!" +
                                    "\\n@whi@Receiving 10% Chance double achievement gain!");
                    break;
                }
            }
        }
        player.getPA().sendString(37511, "@whi@" + player.getDiscordPoints());
    }

    public static void buttonClick(Player player) {
        if (player == null) return;
        if (player.getDiscordTag() != null && player.getDiscordUser() > 0) {
            if (disableMessage.contains(player.getDiscordUser())) {
                disableMessage.remove(player.getDiscordUser());
            } else {
                disableMessage.add(player.getDiscordUser());
            }
        } else {
            player.sendMessage("You need to link your account first.");
        }
        updateDiscordInterface(player);
    }

    public static void syncUser(Player player) {
        if (player == null) return;
        player.getPA().sendEnterString("Enter the code from the Discord Bot.", DiscordBot::integrateAccount);
    }

    public static void disconnectUser(Player player) {
        if (player == null) return;
        if (connectedAccounts.remove(player.getLoginName()) != null) {
            player.setDiscordlinked(false);
            player.setDiscordTag("");
            player.setDiscordUser(0);
            player.sendMessage("Your discord account has been removed from your account.");
            if (DiscordBot.INSTANCE != null) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Player has disconnected their Discord Connect");
                embed.setColor(Color.RED);
                embed.setTimestamp(java.time.Instant.now());
                embed.addField("Player: ", player.getDisplayName(), false);
                DiscordBot.INSTANCE.sendCharacterLinked(embed.build());
            }
            updateDiscordInterface(player);
        }
    }

    /**
     * Points / Booster logic
    */

    public static long delay;
    public static void givePoints() {
        if (delay > System.currentTimeMillis()) return;
        Guild guild = getGuild();
        if (guild == null) return;
        for (Map.Entry<String, Long> entry : connectedAccounts.entrySet()) {
            Player player = PlayerHandler.getPlayerByLoginName(entry.getKey());
            if (player == null) continue;
            Member member = guild.getMemberById(entry.getValue());
            if (member == null) continue;
            CheckDonor(player, member.getRoles());
            boolean containsStatus = false;
            boolean boosting = false;
            for (Activity a : member.getActivities()) {
                String name = a.getName();
                if (name != null && name.toLowerCase().contains("zaryx")) {
                    containsStatus = true;
                    break;
                }
            }
            for (Member booster : guild.getBoosters()) {
                if (member.equals(booster) && player.getDiscordboostlastClaimed() < System.currentTimeMillis()) {
                    player.getItems().addItemUnderAnyCircumstance(13346, 2);
                    player.getItems().addItemUnderAnyCircumstance(696, 100);
                    player.getItems().addItemUnderAnyCircumstance(8167, 1);
                    player.sendMessage("Your discord boost has granted you with 2x UMB, 25m MadPoints & a Nomad Chest!");
                    player.setDiscordboostlastClaimed(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
                }
                if (member.equals(booster)) {
                    boosting = true;
                }
            }
            delay = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15);
            if (containsStatus) {
                player.increaseDiscordPoints(3000 + (boosting ? 3000 : 0));
            } else {
                player.increaseDiscordPoints(1000 + (boosting ? 3000 : 0));
            }
        }
    }

    public static void CheckDonor(Player player, List<Role> roles) {
        // TODO: map donation tiers to actual Discord roles if/when desired
        /* Example skeleton:
        long donated = player.amDonated;
        // inspect `roles`, add/remove as needed via guild.addRoleToMember(...)
        */
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            logger.info("Discord messaging disabled via config.");
            return;
        }
        if (e.getAuthor().isBot()) {
            return;
        }
        // Ignore messages from other servers
        if (e.getGuild().getIdLong() != DiscordChannelType.getGuildId()) {
            return;
        }

        if (e.getChannel().getIdLong() == DiscordChannelType.CHARACTER_LINKING.getChannelId() && e.getMessage().getContentDisplay().equalsIgnoreCase("!connect")) {
            User user = e.getAuthor();
            if (DiscordBot.connectedAccounts.containsValue(user.getIdLong())) {
                DiscordBot.sendPrivateMessage(user, e.getChannel(), "This discord account is already connected to another in-game account!");
            }
            if (DiscordBot.idForCode.containsValue(user.getIdLong())) {
                String code = null;
                for (Map.Entry<String, Long> entry : DiscordBot.idForCode.entrySet()) {
                    if (entry.getValue() == user.getIdLong()) {
                        code = entry.getKey();
                    }
                }
                if (code == null) {
                    code = DiscordBot.generateCode(4);
                }
                DiscordBot.sendPrivateMessage(user, e.getChannel(), "Hello! You have already generated a code! Enter the following in the discord integration prompt:\n"
                        + code);
                return;
            }
            String code = DiscordBot.generateCode(4);
            while (DiscordBot.idForCode.containsKey(code)) {
                code = DiscordBot.generateCode(4);
            }
            DiscordBot.idForCode.put(code, e.getAuthor().getIdLong());
            DiscordBot.sendPrivateMessage(user, e.getChannel(),
                    " **Hello! To Connect your Discord Follow the Steps Below !** " + "```To connect your discord account to your in-game account, enter the following in the discord integration prompt when you click \"sync\":\n"
                            + code + "```");
        } else if (e.getChannel().getIdLong() == 1183132345784606780L) {
            String text = e.getMessage().getContentRaw().toLowerCase();
            if (text.contains("::uneventban")) {
                String[] string = e.getMessage().getContentRaw().toLowerCase().split("-");
                String username = string[1].trim();
                JSONParser jsonParser = new JSONParser();
                String eventBansFileName = "eventbans.json";
                try (BufferedReader eventBansReader = new BufferedReader(new FileReader(Server.getSaveDirectory() + eventBansFileName))) {
                    Object obj = jsonParser.parse(eventBansReader);
                    JSONArray eventBansArray = (JSONArray) obj;
                    JSONArray updatedEventBansArray = new JSONArray();
                    for (Object entry : eventBansArray) {
                        JSONObject eventBan = (JSONObject) entry;
                        String entryUsername = (String) eventBan.get("username");
                        if (!username.equalsIgnoreCase(entryUsername)) {
                            updatedEventBansArray.add(eventBan);
                        }
                    }
                    try (FileWriter eventBansWriter = new FileWriter(Server.getSaveDirectory() + eventBansFileName)) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String prettyJson = gson.toJson(updatedEventBansArray);
                        eventBansWriter.write(prettyJson);
                    }
                } catch (IOException | ParseException ignoredEvent) {
                }
                return;
            }
            if (text.contains("::eventban")) {
                String[] string = e.getMessage().getContentRaw().toLowerCase().split("-");
                String username = string[1].trim();
                String userFileName = username + ".txt";
                String eventBansFileName = "eventbans.json";

                try {
                    JSONParser jsonParser = new JSONParser();
                    JSONArray eventBansArray;
                    try (BufferedReader eventBansReader = new BufferedReader(new FileReader(Server.getSaveDirectory() + eventBansFileName))) {
                        Object obj = jsonParser.parse(eventBansReader);
                        eventBansArray = (JSONArray) obj;
                    } catch (FileNotFoundException ignored) {
                        eventBansArray = new JSONArray();
                    }
                    boolean entryExists = false;
                    for (Object entry : eventBansArray) {
                        JSONObject eventBan = (JSONObject) entry;
                        String entryUsername = (String) eventBan.get("username");
                        if (username.equalsIgnoreCase(entryUsername)) {
                            entryExists = true;
                            break;
                        }
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private static void scanDirectoryForCharacterInfo(String directoryPath, String uuid, String macAddress, String ipAddress) {
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            logger.info("Specified path is not a directory.");
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


    /* -------------------------------------------------------------------------------------------------------------------------- *\


     */
    /**
     * Handlers for Discord Commands for Ingame
     */

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromGuild() && event.getChannel().getIdLong() != DiscordChannelType.STAFF_COMMAND_CHANNEL.getChannelId()) {
            return;
        }
        if (event.getAuthor().isBot()) return;
        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannel channel = event.getChannel();
        if (channel.getIdLong() != DiscordChannelType.STAFF_COMMAND_CHANNEL.getChannelId()) {
            return;
        }
        if (content.startsWith("::commands")) {
            channel.sendMessage(
                    "::afk [Username]\n" +
                            "::triggerwe\n" +
                            "::kick [Username]\n" +
                            "::teleport [Username] x y z"
            ).queue();
        }
        if (content.startsWith("::afk")) {
            String[] args = content.split(" ", 2);
            if (args.length < 2 ) {
                channel.sendMessage("You need to specify a player!").queue();
                return;
            }
            String targetName = args[1].trim();
            Player target = PlayerHandler.getPlayerByLoginName(targetName);
            if (target == null) {
                channel.sendMessage("Player '" + targetName + "' is not online.").queue();
                return;
            }
            target.getPA().startTeleport(2015, 5990, 0, "modern", false);
            channel.sendMessage("Teleported " + targetName + " to the AFK Zone!").queue();
        }
        if (content.startsWith("::triggerwe")) {
            WorldEventContainer.getInstance().setTriggerImmediateEvent(true);
            channel.sendMessage("Triggering next world event, please allow up to 30 seconds.").queue();
        }
        if (content.startsWith("::kick")) {
            String[] args = content.split(" ", 2);
            if (args.length < 2 ) {
                channel.sendMessage("You need to specify a player!").queue();
                return;
            }
            String targetName = args[1].trim();
            Player target = PlayerHandler.getPlayerByLoginName(targetName);
            if (target == null) {
                channel.sendMessage("Player '" + targetName + "' is not online.").queue();
                return;
            }
            target.forceLogout();
            channel.sendMessage("Successfully kicked " + targetName + " offline.").queue();
        }
        if (content.startsWith("::teleport")) {
            String[] parts = content.split(" ", 2);
            if (parts.length < 2) {
                channel.sendMessage("Usage: !!teleport <player name> <x> <y> [height]").queue();
                return;
            }
            String[] args = parts[1].split(" ");
            if (args.length < 3) {
                channel.sendMessage("Usage: !!teleport <player name> <x> <y> [height]").queue();
                return;
            }
            int coordIndex = -1;
            for (int i = 0; i < args.length; i++) {
                if (args[i].matches("\\d+")) {
                    coordIndex = i;
                    break;
                }
            }
            if (coordIndex == -1 || coordIndex + 1 >= args.length) {
                channel.sendMessage("Invalid coordinates! Try again!").queue();
                return;
            }
            String targetName = String.join(" ", java.util.Arrays.copyOfRange(args, 0, coordIndex));
            try {
                int x = Integer.parseInt(args[coordIndex]);
                int y = Integer.parseInt(args[coordIndex + 1]);
                int height = 0;
                if (coordIndex + 2 < args.length) {
                    height = Integer.parseInt(args[coordIndex + 2]);
                }
                Player target = PlayerHandler.getPlayerByLoginName(targetName);
                if (target == null) {
                    channel.sendMessage("'Player '" + targetName + "' is not online.").queue();return;
                }
                target.getPA().startTeleport(x, y, height, "modern", false);
                channel.sendMessage("Teleported " + targetName + " to (" + x + ", " + y + ", " + height + ").").queue();
            } catch (NumberFormatException e) {
                channel.sendMessage("Invalid coordinates! Must be numbers.").queue();
            }
        }
    }
}

