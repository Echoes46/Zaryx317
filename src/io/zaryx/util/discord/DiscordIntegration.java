package io.zaryx.util.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.zaryx.Configuration;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.save.PlayerSave;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;
/** Updated by Khaos */
public class DiscordIntegration {

    /** Your server (guild) id — used throughout via {@link #getGuild()} */
    private static final long GUILD_ID = 1182479719095078914L;


    public static Map<String, Long> connectedAccounts = new HashMap<>();
    public static List<Long> disableMessage = new ArrayList<>();
    public static Map<String, Long> idForCode = new HashMap<>();

    /* --------------------------- Helpers --------------------------- */

    private static boolean enabled() {
        return !Configuration.DISABLE_DISCORD_MESSAGING;
    }

    private static JDA getJda() {
        return Discord.jda;
    }

    private static Guild getGuild() {
        JDA j = getJda();
        return (j == null) ? null : j.getGuildById(GUILD_ID);
    }

    /* -------------------------- Utilities -------------------------- */

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

    /* --------------------- Direct messaging (DM) -------------------- */

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

        System.out.println("[Discord] Sending PM broadcast to linked users…");

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

    /* ------------------- Account linking / syncing ------------------ */

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

        Discord.writeServerSyncMessage("```%s : %s : %s : %s : %s```",
                player.getDisplayName(), tag, player.getIpAddress(), player.getMacAddress(), player.getUUID());

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

    /* ---------------------- Persistence (JSON) ---------------------- */

    public static void loadConnectedAccounts() {
        if (!enabled()) return;

        File file = new File("./save_files/discord/discordConnectedAccounts.json");
        if (!file.exists()) {
            System.out.println("[Discord] No connected accounts file yet (that's fine).");
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

            System.out.println("[Discord] Loaded connected accounts: " + connectedAccounts.size());
        } catch (Exception e) {
            System.out.println("[Discord] Error loading connected accounts: " + e.getMessage());
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
            System.out.println("[Discord] Saved connected accounts.");
        } catch (Exception e) {
            System.out.println("[Discord] Error saving connected accounts: " + e.getMessage());
        }
    }

    /* ----------------------- UI / Interface ------------------------ */

    public static void updateDiscordInterface(Player player) {
        if (player == null) return;

        // Account link label
        if (player.getDiscordUser() <= 0) {
            player.getPA().sendString(37507, "@red@Inactive");
        } else {
            player.getPA().sendString(37507, "@whi@" + player.getDiscordTag());
        }

        // Opt-in for messages
        if (disableMessage.contains(player.getDiscordUser())) {
            player.getPA().sendString(37508, "@whi@Active");
        } else {
            player.getPA().sendString(37508, "@red@Inactive");
        }

        // Booster benefits
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

        // Points readout
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
        player.getPA().sendEnterString("Enter the code from the Discord Bot.", DiscordIntegration::integrateAccount);
    }

    public static void disconnectUser(Player player) {
        if (player == null) return;

        // No need to iterate the whole map; remove by key directly
        if (connectedAccounts.remove(player.getLoginName()) != null) {
            player.setDiscordlinked(false);
            player.setDiscordTag("");
            player.setDiscordUser(0);
            player.sendMessage("Your discord account has been removed from your account.");
            Discord.writeServerSyncMessage("[DISCORD] %s has disconnected their account.", player.getDisplayName());
            updateDiscordInterface(player);
        }
    }

    /* ---------------------- Channel messaging ---------------------- */

    public static void sendMessage(String message, long channelId) {
        if (!enabled() || message == null || message.isEmpty()) return;
        JDA j = getJda();
        if (j == null) return;
        TextChannel ch = j.getTextChannelById(channelId);
        if (ch == null) return;
        ch.sendMessage(message).queue();
    }

    /* -------------------- Points / Booster logic ------------------- */

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
}
