package io.zaryx.util.discord;

//import io.zaryx.BotTokenHandler;
import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.Misc;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Updated by Khaos
 *
 * Central Discord integration:
 * - Registers slash command metadata in onReady
 * - Routes slash interactions to handlers in impl/*
 * - Sends log messages to channels
 * - Sends announcement/event messages as clean red embeds
 * - Uses event-specific images when available
 * - Uses a default announcement image when no specific image is found
 * - Keeps presence updated with online player count
 * - Adds OSRS Wiki item images to Trading Post embeds when possible
 */
public class Discord extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Discord.class);

    // ===== Channel IDs =====
    public static final long CHANNEL_SERVER_LOGS     = 1414017894513250325L; // Server-Logs
    public static final long CHANNEL_OFFLINE_REWARDS = 1217970340517515404L; // Offline-rewards
    public static final long CHANNEL_BOT_INFO        = 1429883544695472128L; // Bot-Information
    public static final long CHANNEL_PICKUP_LOGS     = 1416922612491092088L; // pickup-logs
    public static final long CHANNEL_XMAS_LOGS       = 1224876362683383838L; // xmas-logs
    public static final long CHANNEL_MOD_COMMS       = 1414043828943454309L; // mod-comms
    public static final long CHANNEL_DEATH           = 1427704620016336916L; // death-logs
    public static final long CHANNEL_DROPS           = 1414017693639512084L; // drop-logs
    public static final long CHANNEL_ACHIEVEMENTS    = 1416922210421182595L; // achievements
    public static final long CHANNEL_GIVELOG         = 1487289370582061056L; // give-log
    public static final long CHANNEL_CONNECT_ONLY    = 1429871489372389478L; // kept for /connect
    public static final long CHANNEL_PUNISHMENT      = 1417610944049053778L; // punishment logs mute/ban etc..
    private static final long CHANNEL_RARE_DROPS     = 1427687582774595695L; // rare drop logs
    public static final long CHANNEL_TRADING_POST    = 1427762559754178734L; // trading-post
    public static final long CHANNEL_TRIAL_OF_ARMS = 0L; // Trial of Arms

    // ===== Announcement Embed Settings =====
    private static final Color ANNOUNCEMENT_RED = new Color(190, 20, 20);

    private static final String DEFAULT_ANNOUNCEMENT_IMAGE_URL = "https://i.ibb.co/dSyKMcg/Chat-GPT-Image-May-16-2026-05-16-55-PM.png";

    private static final String CRYSTAL_TREE_IMAGE_URL = "https://i.ibb.co/0p0kHj35/Crystal-tree-grown.png";
    private static final String SHOOTING_STAR_IMAGE_URL = "https://i.ibb.co/qLdPD5Xf/1024px-Crashed-Star-size-9.png";
    private static final String HESPORI_IMAGE_URL = "https://i.ibb.co/k60zWBrX/Hespori.png";
    private static final String VOLCANO_BOULDER_URL = "https://i.ibb.co/3mBVqyQj/472px-Giant-Boulder-attached.png";

    // ===== Trading Post Image Settings =====
    private static final String OSRS_WIKI_IMAGE_BASE = "https://oldschool.runescape.wiki/images/";
    private static final String TRADING_POST_FALLBACK_IMAGE_URL = OSRS_WIKI_IMAGE_BASE + "Coins_detail.png";

    // ===== Roles =====
    public static String OWNER_ROLE      = "1182565375045533797";
    public static String MANAGER_ROLE    = "1436983435569790988";
    public static String DEVELOPER_ROLE  = "1182565327540854866";
    public static String ADMIN_ROLE      = "1416923243675386048";
    public static String GLOBAL_MOD_ROLE = "1416923185626087424";
    public static String SUPPORT_ROLE    = "1416923102012506202";
    public static String MEMBER_ROLE     = "1183257193265508384";

    // ===== Internals =====
    private static JDA jda;

    private static final ScheduledExecutorService presenceScheduler = Executors.newSingleThreadScheduledExecutor();

    // ===== Central command router =====
    private static final Map<String, SlashHandler> COMMANDS = new HashMap<>();

    static {
        COMMANDS.put("commands",          new io.zaryx.util.discord.impl.Commands());
        COMMANDS.put("giveitem",          new io.zaryx.util.discord.impl.GiveItem());
        COMMANDS.put("ban",               new io.zaryx.util.discord.impl.Ban());
        COMMANDS.put("unban",             new io.zaryx.util.discord.impl.UnBan());
        COMMANDS.put("mute",              new io.zaryx.util.discord.impl.Mute());
        COMMANDS.put("unmute",            new io.zaryx.util.discord.impl.UnMute());
        COMMANDS.put("jail",              new io.zaryx.util.discord.impl.Jail());
        COMMANDS.put("unjail",            new io.zaryx.util.discord.impl.UnJail());
        COMMANDS.put("offlinereward",     new io.zaryx.util.discord.impl.OfflineReward());
        COMMANDS.put("voteboss",          new io.zaryx.util.discord.impl.VoteBoss());
        COMMANDS.put("donorboss",         new io.zaryx.util.discord.impl.DonorBoss());
        COMMANDS.put("groot",             new io.zaryx.util.discord.impl.Groot());
        COMMANDS.put("xmas",              new io.zaryx.util.discord.impl.xmas());
        COMMANDS.put("listonlineplayers", new io.zaryx.util.discord.impl.ListOnlinePlayers());
        COMMANDS.put("currentevents",     new io.zaryx.util.discord.impl.CurrentEvents());
        COMMANDS.put("connectaccount",    new io.zaryx.util.discord.impl.ConnectAccount());
        COMMANDS.put("netmute",           new io.zaryx.util.discord.impl.NetMute());
        COMMANDS.put("netunmute",         new io.zaryx.util.discord.impl.NetUnMute());
        COMMANDS.put("sendhome",          new io.zaryx.util.discord.impl.SendHome());
        COMMANDS.put("message",           new io.zaryx.util.discord.impl.MessageBroadcast());
    }

    public static JDA getJDA() {
        return jda;
    }

    // ===== Init / Shutdown =====
    public static synchronized void init() {
//        if (Configuration.isDev()) {
//            logger.info("Discord skipped because server is in dev mode.");
//            return;
//        }

        if (jda != null) {
            logger.warn("Discord.init() was called more than once. Ignoring duplicate startup.");
            return;
        }

        final String token = BotTokenHandler.token;

        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Discord token is empty. Set BotTokenHandler.token.");
        }

        try {
            Discord listener = new Discord();

            JDABuilder builder = JDABuilder.createDefault(token)
                    .enableIntents(
                            GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MESSAGES
                    )
                    .enableCache(CacheFlag.ACTIVITY)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .addEventListeners(listener);

            jda = builder.build();
            jda.awaitReady();

            updatePresence();

            jda.getGuilds().forEach(Guild::loadMembers);
            logger.info("Discord: connected and ready.");

            presenceScheduler.scheduleAtFixedRate(Discord::updatePresence, 30, 30, TimeUnit.SECONDS);

        } catch (Exception e) {
            throw new RuntimeException("Failed to start Discord bot", e);
        }
    }

    public static void shutdown() {
        try {
            presenceScheduler.shutdownNow();
        } catch (Exception ignore) {
        }

        if (jda != null) {
            try {
                jda.getPresence().setPresence(OnlineStatus.OFFLINE, null);
            } catch (Exception ignore) {
            }
        }
    }

    // ===== Presence handling =====
    public static void updatePresence() {
        final JDA bot = jda;
        if (bot == null) return;

        int online = 0;

        try {
            online = PlayerHandler.getUniquePlayerCount();
        } catch (Exception ignored) {
        }

        final Activity activity = Activity.playing("Zaryx with " + Math.max(online, 0) + " players!");

        Server.getIoExecutorService().submit(() -> {
            try {
                bot.getPresence().setPresence(OnlineStatus.ONLINE, activity);
            } catch (Exception e) {
                logger.warn("Failed to update Discord presence", e);
            }
        });
    }

    /**
     * Call this on player login/logout to nudge presence immediately.
     */
    public static void onPlayerOnlineChange() {
        updatePresence();
    }

    // ===== Slash command routing =====
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        final SlashHandler handler = COMMANDS.get(e.getName());

        if (handler == null) {
            e.reply("Unknown command.").setEphemeral(true).queue();
            return;
        }

        try {
            handler.handle(e);
        } catch (Throwable t) {
            logger.error("Error handling /" + e.getName(), t);
            e.reply("Something went wrong executing this command.").setEphemeral(true).queue();
        }
    }

    // ===== Slash command registration =====
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        jda.updateCommands().addCommands(
                Commands.slash("message", "Broadcast a message")
                        .addOption(OptionType.STRING, "message", "Your message", true),

                Commands.slash("giveitem", "Give an item to a player")
                        .addOption(OptionType.STRING, "name", "The name of the player", true)
                        .addOption(OptionType.INTEGER, "id", "The item ID", true)
                        .addOption(OptionType.INTEGER, "amount", "The amount of the item", true),

                Commands.slash("ban", "Ban a player")
                        .addOption(OptionType.STRING, "name", "The name of the player", true),

                Commands.slash("jail", "Jail a player")
                        .addOption(OptionType.STRING, "name", "The name of the player", true),

                Commands.slash("listonlineplayers", "List all online players"),

                Commands.slash("currentevents", "Displays current in-game events"),

                Commands.slash("connectaccount", "Link your Discord and in-game account"),

                Commands.slash("mute", "Mute a player")
                        .addOption(OptionType.STRING, "name", "The name of the player", true),

                Commands.slash("netmute", "Network mute a player")
                        .addOption(OptionType.STRING, "name", "The name of the player", true),

                Commands.slash("sendhome", "Send a player home")
                        .addOption(OptionType.STRING, "name", "The name of the player", true),

                Commands.slash("unban", "Unban a player")
                        .addOption(OptionType.STRING, "name", "The name of the player", true),

                Commands.slash("unmute", "Unmute a player")
                        .addOption(OptionType.STRING, "name", "The name of the player", true),

                Commands.slash("netunmute", "Network unmute a player")
                        .addOption(OptionType.STRING, "name", "The name of the player", true),

                Commands.slash("unjail", "Unjail a player")
                        .addOption(OptionType.STRING, "name", "The name of the player", true),

                Commands.slash("offlinereward", "Gives reward to offline box")
                        .addOption(OptionType.STRING, "name", "The name of the player", true)
                        .addOption(OptionType.INTEGER, "id", "The item ID", true)
                        .addOption(OptionType.INTEGER, "amount", "The amount of the item", true),

                Commands.slash("voteboss", "Spawns Vote Boss"),
                Commands.slash("donorboss", "Spawns Donor Boss"),
                Commands.slash("groot", "Spawns Groot"),

                Commands.slash("tpdelete", "Removes a player's Trading Post listing")
                        .addOption(OptionType.STRING, "name", "Player name", true),

                Commands.slash("xmas", "Xmas helper")
        ).queue();

        updatePresence();

        logger.info("Discord: slash commands registered.");

        jda.getGuilds().forEach(Guild::loadMembers);
        jda.retrieveCommands().queue();
    }

    // ===== Channel helpers =====
    public static void writeServerSyncMessage(String message, Object... args) {
        sendChannelMessage(CHANNEL_SERVER_LOGS, message, args);
    }

    public static void writeOfflineRewardsMessage(String message, Object... args) {
        sendChannelMessage(CHANNEL_OFFLINE_REWARDS, message, args);
    }

    public static void writeOnlineNotification(String message, Object... args) {
        sendChannelMessage(CHANNEL_BOT_INFO, message, args);
    }

    public static void writeIngameEvents(String message, Object... args) {
        sendAnnouncementEmbed(CHANNEL_BOT_INFO, "[ WORLD EVENT ]", message, args);
    }

    public static void writeTournaments(String message, Object... args) {
        sendAnnouncementEmbed(CHANNEL_BOT_INFO, "[ TOURNAMENT ]", message, args);
    }

    public static void writeTrialOfArms(String message, Object... args) {
        sendAnnouncementEmbed(CHANNEL_TRIAL_OF_ARMS, "[ TRIAL OF ARMS ]", message, args);
    }

    public static void writePickupMessage(String message, Object... args) {
        sendChannelMessage(CHANNEL_PICKUP_LOGS, message, args);
    }

    public static void writeXmasMessage(String message, Object... args) {
        sendChannelMessage(CHANNEL_XMAS_LOGS, message, args);
    }

    public static void writeSuggestionMessage(String message, Object... args) {
        sendChannelMessage(CHANNEL_MOD_COMMS, message, args);
    }

    public static void writeFoeMessage(String message, Object... args) {
        writeServerSyncMessage(message, args);
        sendChannelMessage(CHANNEL_BOT_INFO, message, args);
    }

    public static void writeReferralMessage(String message, Object... args) {
        writeServerSyncMessage(message, args);
    }

    public static void writeCheatEngineMessage(String message, Object... args) {
        writeServerSyncMessage(message, args);
    }

    public static void writeDeathHandler(String message, Object... args) {
        sendChannelMessage(CHANNEL_DEATH, message, args);
    }

    public static void writeDropHandler(String message, Object... args) {
        sendChannelMessage(CHANNEL_DROPS, message, args);
    }

    public static void writeRareDropHandler(String message, Object... args) {
        sendChannelMessage(CHANNEL_RARE_DROPS, message, args);
    }

    public static void writeAchievements(String message, Object... args) {
        sendChannelMessage(CHANNEL_ACHIEVEMENTS, message, args);
    }

    public static void writeGiveLog(String message, Object... args) {
        sendChannelMessage(CHANNEL_GIVELOG, message, args);
    }

    public static void writeAddressSwapMessage(String message, Object... args) {
        writeServerSyncMessage(message, args);
    }

    public static void writePunishmentLog(String message, Object... args) {
        sendChannelMessage(CHANNEL_PUNISHMENT, message, args);
    }

    // ===== Basic plain-text Discord messages =====
    private static void sendChannelMessage(long channelId, String message, Object... args) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) return;
        if (jda == null) return;

        Server.getIoExecutorService().submit(() -> {
            try {
                TextChannel ch = jda.getTextChannelById(channelId);

                if (ch != null) {
                    ch.sendMessage(Misc.replaceBracketsWithArguments(message, args)).queue();
                } else {
                    logger.warn("Discord: channel not found for id {}", channelId);
                }

            } catch (Exception e) {
                logger.error("Discord sendChannelMessage error", e);
            }
        });
    }

    // ===== Announcement embed messages =====
    private static void sendAnnouncementEmbed(long channelId, String defaultTitle, String message, Object... args) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) return;
        if (jda == null) return;

        String formattedMessage = Misc.replaceBracketsWithArguments(message, args);

        Server.getIoExecutorService().submit(() -> {
            try {
                TextChannel ch = jda.getTextChannelById(channelId);

                if (ch == null) {
                    logger.warn("Discord: channel not found for id {}", channelId);
                    return;
                }

                MessageEmbed embed = buildAnnouncementEmbed(defaultTitle, formattedMessage);
                ch.sendMessageEmbeds(embed).queue();

            } catch (Exception e) {
                logger.error("Discord sendAnnouncementEmbed error", e);
            }
        });
    }

    private static MessageEmbed buildAnnouncementEmbed(String defaultTitle, String message) {
        String safeMessage = message == null ? "" : message;
        String lower = safeMessage.toLowerCase();

        String title = defaultTitle;
        String description = cleanAnnouncementMessage(safeMessage);
        String imageUrl = DEFAULT_ANNOUNCEMENT_IMAGE_URL;

        if (lower.contains("crystal tree")) {
            title = "[ WORLD EVENT: CRYSTAL TREE ]";
            description = cleanWorldEventMessage(safeMessage, "Crystal Tree", "tree");
            imageUrl = CRYSTAL_TREE_IMAGE_URL;

        } else if (lower.contains("shooting star") || lower.contains("crashed star") || lower.contains("star")) {
            title = "[ WORLD EVENT: SHOOTING STAR ]";
            description = cleanWorldEventMessage(safeMessage, "Shooting Star", "star");
            imageUrl = SHOOTING_STAR_IMAGE_URL;

        } else if (lower.contains("hespori")) {
            title = "[ WORLD EVENT: HESPORI ]";
            description = cleanWorldEventMessage(safeMessage, "Hespori", "worldevent");
            imageUrl = HESPORI_IMAGE_URL;

        } else if (lower.contains("volcano")) {
            title = "[ WORLD EVENT: VOLCANO ]";
            description = cleanWorldEventMessage(safeMessage, "Volcano", "worldevent");
            imageUrl = VOLCANO_BOULDER_URL;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setDescription(description);
        embed.setColor(ANNOUNCEMENT_RED);
        embed.setTimestamp(Instant.now());

        if (isValidImageUrl(imageUrl)) {
            embed.setImage(imageUrl);
        }

        return embed.build();
    }

    private static String cleanWorldEventMessage(String message, String eventName, String emojiName) {
        String cleaned = cleanAnnouncementMessage(message);

        cleaned = cleaned.replace("[" + eventName + "]", "");
        cleaned = cleaned.replace("[ " + eventName + " ]", "");
        cleaned = cleaned.replace("::" + emojiName, "");

        return cleaned.trim();
    }

    private static String cleanAnnouncementMessage(String message) {
        if (message == null) return "";

        String cleaned = message;

        // Removes Discord code blocks so text does not appear inside a boxed code area.
        cleaned = cleaned.replace("```", "");

        // Removes single backtick inline-code formatting.
        cleaned = cleaned.replace("`", "");

        // Converts escaped new lines into real new lines if a caller sends "\\n".
        cleaned = cleaned.replace("\\n", "\n");

        return cleaned.trim();
    }

    private static boolean isValidImageUrl(String imageUrl) {
        if (imageUrl == null) return false;

        String url = imageUrl.trim();

        if (url.isEmpty()) return false;
        if (url.contains("PUT_DEFAULT")) return false;
        if (url.contains("YOUR_")) return false;

        String lower = url.toLowerCase();

        return lower.startsWith("http")
                && (lower.endsWith(".png")
                || lower.endsWith(".jpg")
                || lower.endsWith(".jpeg")
                || lower.endsWith(".gif")
                || lower.endsWith(".webp"));
    }

    // ===== Trading Post item image helpers =====
    private static String getTradingPostItemImageUrl(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return TRADING_POST_FALLBACK_IMAGE_URL;
        }

        String cleanName = cleanItemNameForWiki(itemName);

        if (cleanName.isEmpty()) {
            return TRADING_POST_FALLBACK_IMAGE_URL;
        }

        String fileName = cleanName.replace(" ", "_") + "_detail.png";
        fileName = encodeWikiImageFileName(fileName);

        return OSRS_WIKI_IMAGE_BASE + fileName;
    }

    private static String cleanItemNameForWiki(String itemName) {
        String cleanName = itemName == null ? "" : itemName.trim();

        // Removes common RSPS color/image tags.
        cleanName = cleanName.replaceAll("<[^>]*>", "");

        // Removes item amount suffix if it ever gets passed in with the name.
        cleanName = cleanName.replaceAll("\\s+x\\d+$", "");

        // Normalizes repeated spaces.
        cleanName = cleanName.replaceAll("\\s+", " ").trim();

        if (cleanName.isEmpty()) {
            return "";
        }

        // OSRS Wiki file names are usually capitalized like:
        // Dragon_longsword_detail.png
        // Water_rune_detail.png
        // Fire_rune_detail.png
        cleanName = Character.toUpperCase(cleanName.charAt(0)) + cleanName.substring(1);

        return cleanName;
    }

    private static String encodeWikiImageFileName(String fileName) {
        if (fileName == null) {
            return "";
        }

        return fileName
                .replace("'", "%27")
                .replace("#", "%23")
                .replace("+", "%2B")
                .replace(",", "%2C")
                .replace("&", "%26");
    }

    public static void sendCriticalWarning(Player player, String time, long oldTotalNomad, long newTotalNomad, boolean nomad) {
        String wealthType = nomad ? "Nomad" : "Coins";

        String message = String.format(
                "CRITICAL WARNING: Player %s has experienced a significant increase in %s wealth.\n" +
                        "Play time: %s\n" +
                        "%s wealth: %d => %d (Increase: %s)",
                player.getDisplayName(),
                wealthType,
                time,
                wealthType,
                oldTotalNomad,
                newTotalNomad,
                NumberFormat.getInstance().format(newTotalNomad - oldTotalNomad)
        );

        Server.getIoExecutorService().submit(() -> {
            try {
                TextChannel channel = getJDA().getTextChannelById(CHANNEL_SERVER_LOGS);

                if (channel != null) {
                    channel.sendMessage(message).queue();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void writeTradingPostMessage(
            String title,
            String playerName,
            String type,
            String itemName,
            int itemAmount,
            long priceEach,
            String currencyName
    ) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) return;
        if (jda == null) return;

        long total = priceEach * itemAmount;
        String itemImageUrl = getTradingPostItemImageUrl(itemName);

        String description = "**Player:** " + playerName + "\n"
                + "**Type:** " + type + "\n"
                + "**Item:** " + itemName + " x" + Misc.formatCoins(itemAmount) + "\n"
                + "**Price:** " + Misc.formatCoins(priceEach) + " " + currencyName + " each\n"
                + "**Total:** " + Misc.formatCoins(total) + " " + currencyName;

        Server.getIoExecutorService().submit(() -> {
            try {
                TextChannel ch = jda.getTextChannelById(CHANNEL_TRADING_POST);

                if (ch == null) {
                    logger.warn("Discord: channel not found for id {}", CHANNEL_TRADING_POST);
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(title);
                embed.setDescription(description);
                embed.setColor(ANNOUNCEMENT_RED);
                embed.setTimestamp(Instant.now());

                if (isValidImageUrl(itemImageUrl)) {
                    embed.setThumbnail(itemImageUrl);
                } else {
                    embed.setThumbnail(TRADING_POST_FALLBACK_IMAGE_URL);
                }

                ch.sendMessageEmbeds(embed.build()).queue(
                        success -> {},
                        error -> {
                            logger.warn("Discord Trading Post embed failed. Retrying with fallback coin image.", error);

                            EmbedBuilder fallbackEmbed = new EmbedBuilder();
                            fallbackEmbed.setTitle(title);
                            fallbackEmbed.setDescription(description);
                            fallbackEmbed.setColor(ANNOUNCEMENT_RED);
                            fallbackEmbed.setTimestamp(Instant.now());
                            fallbackEmbed.setThumbnail(TRADING_POST_FALLBACK_IMAGE_URL);

                            ch.sendMessageEmbeds(fallbackEmbed.build()).queue();
                        }
                );

            } catch (Exception e) {
                logger.error("Discord writeTradingPostMessage error", e);
            }
        });
    }

}