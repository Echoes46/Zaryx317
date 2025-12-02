package io.zaryx.util.discord;

import io.zaryx.BotTokenHandler;
import io.zaryx.Configuration;
import io.zaryx.Server;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.Misc;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
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

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Central Discord integration:
 * - Registers slash command metadata in onReady
 * - Routes slash interactions to handlers in impl/*
 * - Sends log messages to channels
 * - Keeps presence updated with online player count
 */
public class Discord extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Discord.class);

    // ===== Channel IDs =====
    public static final long CHANNEL_SERVER_LOGS     = 1414017894513250325L; // Server-Logs
    public static final long CHANNEL_OFFLINE_REWARDS = 1217970340517515404L; // Offline-rewards
    public static final long CHANNEL_BOT_INFO        = 1414018863934341160L; // Bot-Information
    public static final long CHANNEL_PICKUP_LOGS     = 1416922612491092088L; // pickup-logs
    public static final long CHANNEL_XMAS_LOGS       = 1224876362683383838L; // xmas-logs
    public static final long CHANNEL_MOD_COMMS       = 1414043828943454309L; // mod-comms
    public static final long CHANNEL_DEATH           = 1416922358643687424L; // death-logs
    public static final long CHANNEL_DROPS           = 1414017693639512084L; // drop-logs
    public static final long CHANNEL_ACHIEVEMENTS    = 1416922210421182595L; // achievements
    public static final long CHANNEL_GIVELOG         = 1416922290691506246L; // give-log
    public static final long CHANNEL_CONNECT_ONLY    = 1429871489372389478L; // kept for /connect
    public static final long CHANNEL_PUNISHMENT      = 1417610944049053778L; // punishment logs mute/ban etc..
    private static final long CHANNEL_RARE_DROPS     = 1427687582774595695L; // rare drop logs

    // ===== Roles (if you use them elsewhere) =====
    public static String OWNER_ROLE      = "1182565375045533797";
    public static String MANAGER_ROLE    = "1436983435569790988";
    public static String DEVELOPER_ROLE  = "1182565327540854866";
    public static String ADMIN_ROLE      = "1416923243675386048";
    public static String GLOBAL_MOD_ROLE = "1416923185626087424";
    public static String SUPPORT_ROLE    = "1416923102012506202";
    public static String MEMBER_ROLE     = "1183257193265508384";
    
    // ===== Internals =====
    private static final Map<String, TextChannel> channelCache = new ConcurrentHashMap<>();
    private static JDA jda;

    // Presence scheduler
    private static final ScheduledExecutorService presenceScheduler = Executors.newSingleThreadScheduledExecutor();

    // ===== Central command router =====
    private static final Map<String, SlashHandler> COMMANDS = new HashMap<>();
    static {
        // Use fully-qualified names here to avoid a name clash with JDA's Commands builder.
        COMMANDS.put("commands",       new io.zaryx.util.discord.impl.Commands());
        COMMANDS.put("giveitem",       new io.zaryx.util.discord.impl.GiveItem());
        COMMANDS.put("ban",            new io.zaryx.util.discord.impl.Ban());
        COMMANDS.put("unban",          new io.zaryx.util.discord.impl.UnBan());
        COMMANDS.put("mute",           new io.zaryx.util.discord.impl.Mute());
        COMMANDS.put("unmute",         new io.zaryx.util.discord.impl.UnMute());
        COMMANDS.put("jail",           new io.zaryx.util.discord.impl.Jail());
        COMMANDS.put("unjail",         new io.zaryx.util.discord.impl.UnJail());
        COMMANDS.put("offlinereward",  new io.zaryx.util.discord.impl.OfflineReward());
        COMMANDS.put("voteboss",       new io.zaryx.util.discord.impl.VoteBoss());
        COMMANDS.put("donorboss",      new io.zaryx.util.discord.impl.DonorBoss());
        COMMANDS.put("groot",          new io.zaryx.util.discord.impl.Groot());
        COMMANDS.put("xmas",           new io.zaryx.util.discord.impl.xmas());

        // Add more here when you create them:
         COMMANDS.put("listonlineplayers", new io.zaryx.util.discord.impl.ListOnlinePlayers());
         COMMANDS.put("currentevents",     new io.zaryx.util.discord.impl.CurrentEvents());
         COMMANDS.put("connectaccount",    new io.zaryx.util.discord.impl.ConnectAccount());
         COMMANDS.put("netmute",           new io.zaryx.util.discord.impl.NetMute());
         COMMANDS.put("netunmute",         new io.zaryx.util.discord.impl.NetUnMute());
         COMMANDS.put("sendhome",          new io.zaryx.util.discord.impl.SendHome());
         COMMANDS.put("message",           new io.zaryx.util.discord.impl.MessageBroadcast());
    }


    // ===== JDA accessor =====
    public static JDA getJDA() { return jda; }

    // ===== Init / Shutdown =====
    public static void init() {
        if (Configuration.isDev()) return;
        final String token = BotTokenHandler.token;
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Discord token is empty. Set BotTokenHandler.token.");
        }

        try {
            JDABuilder builder = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
                    .enableCache(CacheFlag.ACTIVITY)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL);

            jda = builder.build();
            jda.addEventListener(new Discord()); // central listener only
            jda.awaitReady();

            // Initial presence (may be 0; scheduler + hooks will correct)
            updatePresence();

            jda.getGuilds().forEach(Guild::loadMembers);
            logger.info("Discord: connected and ready.");

            presenceScheduler.scheduleAtFixedRate(Discord::updatePresence, 30, 30, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to start Discord bot", e);
        }
    }

    public static void shutdown() {
        try { presenceScheduler.shutdownNow(); } catch (Exception ignore) {}
        if (jda != null) {
            try { jda.getPresence().setPresence(OnlineStatus.OFFLINE, null); } catch (Exception ignore) {}
        }
    }

    // ===== Presence handling =====
    public static void updatePresence() {
        if (Configuration.isDev()) return;
        final JDA bot = jda;
        if (bot == null) return;

        int online = 0;
        try { online = PlayerHandler.getUniquePlayerCount(); } catch (Exception ignored) {}
        final Activity activity = Activity.playing("Zaryx with " + Math.max(online, 0) + " players!");

        Server.getIoExecutorService().submit(() -> {
            try { bot.getPresence().setPresence(OnlineStatus.ONLINE, activity); }
            catch (Exception e) { logger.warn("Failed to update Discord presence", e); }
        });
    }

    /** Call this on player login/logout to nudge presence immediately. */
    public static void onPlayerOnlineChange() { updatePresence(); }

    // ===== Slash command routing =====
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        // Place for a central permission gate if you want:
        // if (!DiscordPermissions.canUse(e)) { e.reply("❌ No permission").setEphemeral(true).queue(); return; }

        final SlashHandler handler = COMMANDS.get(e.getName());
        if (handler == null) {
            e.reply("Unknown command.").setEphemeral(true).queue();
            return;
        }
        try {
            handler.handle(e);
        } catch (Throwable t) {
            logger.error("Error handling /" + e.getName(), t);
            e.reply("❌ Something went wrong executing this command.").setEphemeral(true).queue();
        }
    }

    // ===== Slash command registration (metadata) =====
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        jda.updateCommands().addCommands(
                Commands.slash("message", "Broadcast a message")
                        .addOption(OptionType.STRING, "message", "Your message", true),

                Commands.slash("giveitem", "Give an item to a player")
                        .addOption(OptionType.STRING,  "name",   "The name of the player", true)
                        .addOption(OptionType.INTEGER, "id",     "The item ID",            true)
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

                // Your custom extras already implemented:
                Commands.slash("offlinereward", "Gives reward to offline box")
                        .addOption(OptionType.STRING,  "name",   "The name of the player", true)
                        .addOption(OptionType.INTEGER, "id",     "The item ID",            true)
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
        jda.retrieveCommands().queue(); // optional sanity check
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
        sendChannelMessage(CHANNEL_BOT_INFO, message, args);
    }
    public static void writeTournaments(String message, Object... args) {
        sendChannelMessage(CHANNEL_BOT_INFO, message, args);
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
    public static void writePunishmentLog(String message, Object... args){
        sendChannelMessage(CHANNEL_PUNISHMENT, message, args);
    }

    private static void sendChannelMessage(long channelId, String message, Object... args) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) return;
        if (jda == null) return;

        Server.getIoExecutorService().submit(() -> {
            try {
                TextChannel ch = jda.getTextChannelById(channelId);
                if (ch != null) ch.sendMessage(Misc.replaceBracketsWithArguments(message, args)).queue();
                else logger.warn("Discord: channel not found for id {}", channelId);
            } catch (Exception e) {
                logger.error("Discord sendChannelMessage error", e);
            }
        });
    }
    public static void sendCriticalWarning(Player player, String time, long oldTotalNomad, long newTotalNomad, boolean nomad) {
        // Construct the message
        String wealthType = nomad ? "Nomad" : "Coins";
        String message = String.format("CRITICAL WARNING: Player %s has experienced a significant increase in %s wealth.\n" +
                        "Play time: %s\n" +
                        "%s wealth: %d => %d (Increase: %s)",
                player.getDisplayName(), wealthType, time, wealthType, oldTotalNomad, newTotalNomad, NumberFormat.getInstance().format(newTotalNomad - oldTotalNomad));

        Server.getIoExecutorService().submit(() -> {
            try {
                if (getJDA().getTextChannelById(1414017894513250325L) != null) {
                    Objects.requireNonNull(getJDA().getTextChannelById(1414017894513250325L)).sendMessage(message).queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
