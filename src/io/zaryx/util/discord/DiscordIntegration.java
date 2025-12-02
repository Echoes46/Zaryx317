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
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DiscordIntegration {

    public static Map<String, Long> connectedAccounts = new HashMap<>();
    public static ArrayList<Long> disableMessage = new ArrayList<>();
    public static Map<String, Long> idForCode = new HashMap<>();

    /** Generate a safe, alphanumeric, uppercase code (no symbols). */
    public static String generateCode(int length) {
        final String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no O/0 or I/1
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /** Normalize user-entered code (strip non-alphanumeric, uppercase). */
    public static String normalizeCode(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }

    public static void sendPrivateMessage(User user, TextChannel c, String content) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) return;
        if (user == null) return; // safety

        ErrorHandler handler = new ErrorHandler().handle(
                ErrorResponse.CANNOT_SEND_TO_USER,
                (error) -> {
                    if (c != null) {
                        c.sendMessage(user.getAsMention() + " You must enable your private messages first!").queue();
                    }
                }
        );

        user.openPrivateChannel().queue(pc -> pc.sendMessage(content).queue(null, handler));
    }

    public static void sendPMS(String content) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) return;
        if (Discord.getJDA() == null) return;

        System.out.println("sending discord pms");
        Guild guild = Discord.getJDA().getGuildById(1182479719095078914L); // Server ID
        if (guild == null) return;

        for (Map.Entry<String, Long> entry : connectedAccounts.entrySet()) {
            Player player = PlayerHandler.getPlayerByLoginName(entry.getKey());
            if (player == null) continue;

            Member member = guild.getMemberById(entry.getValue());
            if (member == null) continue;

            if (disableMessage.contains(entry.getValue())) continue;

            User user = member.getUser();
            if (user == null) continue;

            ErrorHandler handler = new ErrorHandler().handle(
                    ErrorResponse.CANNOT_SEND_TO_USER,
                    (error) -> { /* ignore if DMs are closed */ }
            );

            user.openPrivateChannel().queue(pc -> {
                pc.sendMessage("A new update has just released on Zaryx!").queue(null, handler);
                pc.sendMessage("change this").queue(null, handler);
            });
        }
    }

    public static void integrateAccount(Player player, String code) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) return;
        if (player == null) return;

        if (connectedAccounts == null) {
            loadConnectedAccounts();
        }

        if (player.getDiscordUser() > 0) {
            player.sendMessage("You already have a connected discord account!");
            return;
        }

        // ✅ normalize user input before lookup
        String normalized = normalizeCode(code);
        if (!idForCode.containsKey(normalized)) {
            player.sendMessage("You have entered an invalid code! Try again.");
            return;
        }

        long userId = idForCode.get(normalized);
        idForCode.remove(normalized);

        if (connectedAccounts.containsValue(userId) && !Objects.equals(connectedAccounts.get(player.getLoginName()), userId)) {
            player.sendMessage("This discord account is already linked to another player!");
            return;
        }

        if (Discord.getJDA() == null) {
            player.sendMessage("Discord is not connected right now.");
            return;
        }

        User discordUser = Discord.getJDA().getUserById(userId);
        String tag = discordUser != null ? discordUser.getAsTag() : ("<@" + userId + ">");
        player.sendMessage("You have connected the discord account '" + tag + "'.");

        connectedAccounts.put(player.getLoginName(), userId);
        player.setDiscordUser(userId);
        player.setDiscordTag(tag);
        updateDiscordInterface(player);

        Discord.writeServerSyncMessage("```" + player.getDisplayName() + " : " + tag + " : " + player.getIpAddress() + " : " + player.getMacAddress() + " : " + player.getUUID() + "```");

        if (!player.getDiscordlinked() && player.getDiscordPoints() <= 10) {
            player.amDonated += 10;
            player.updateRank();
            player.sendMessage("@mag@You received $10 to your total donated amount for linking your Discord account!");
            player.setDiscordlinked(true);
            player.setDiscordPoints(player.getDiscordPoints() + 10);
        }

        PlayerSave.saveGame(player);
        // TODO: Announce the sync if desired
    }

    public static void setIntegration(Player player) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) return;
        if (player == null) return;

        if (player.getDiscordUser() > 0 && player.getDiscordTag() != null) {
            connectedAccounts.put(player.getLoginName(), player.getDiscordUser());
            player.setDiscordUser(player.getDiscordUser());
            player.setDiscordTag(player.getDiscordTag());
        }
    }

    public static void loadConnectedAccounts() {
        if (Configuration.DISABLE_DISCORD_MESSAGING) return;
        File file = new File("./save_files/discord/discordConnectedAccounts.json");

        try (FileReader fileReader = new FileReader(file)) {
            JsonParser fileParser = new JsonParser();
            Gson builder = new GsonBuilder().create();
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);

            if (reader.has("connectedAccounts")) {
                Map<String, Long> accounts = builder.fromJson(
                        reader.get("connectedAccounts"),
                        new TypeToken<Map<String, Long>>() {}.getType()
                );
                if (accounts != null) connectedAccounts = accounts;
            }

            if (reader.has("disableMessage")) {
                Long[] data = builder.fromJson(reader.get("disableMessage"), Long[].class);
                if (data != null) {
                    disableMessage.clear();
                    disableMessage.addAll(Arrays.asList(data));
                }
            }

            System.out.println("Loaded Discord Connected Accounts!");
        } catch (Exception e) {
            System.out.println("Error Loading Discord Connected Accounts!");
        }
    }

    public static void saveConnectedAccounts() {
        if (Configuration.DISABLE_DISCORD_MESSAGING) return;
        File file = new File("./save_files/discord/discordConnectedAccounts.json");
        try (FileWriter writer = new FileWriter(file)) {
            Gson builder = new GsonBuilder().setPrettyPrinting().create();
            JsonObject object = new JsonObject();

            object.add("connectedAccounts", builder.toJsonTree(connectedAccounts));
            object.add("disableMessage", builder.toJsonTree(disableMessage));

            writer.write(builder.toJson(object));
            writer.flush();
            System.out.println("Saved Discord Connected Accounts!");
        } catch (Exception e) {
            System.out.println("Error Saving Discord Connected Accounts!");
        }
    }

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

        if (Discord.getJDA() != null) {
            Guild guild = Discord.getJDA().getGuildById(1182479719095078914L); // Server Guild
            if (guild != null) {
                boolean isBooster = false;
                for (Member booster : guild.getBoosters()) {
                    if (booster != null && booster.getIdLong() == player.getDiscordUser()) {
                        isBooster = true;
                        break;
                    }
                }
                if (isBooster) {
                    player.getPA().sendString(37509, "@whi@Boosting!");
                    player.getPA().sendString(37510,
                            "@whi@Receiving 10% Damage Boost!"
                                    + "\\n@whi@Receiving 10% Rare rewards from raids!"
                                    + "\\n@whi@Receiving 10% Chance double achievement gain!");
                } else {
                    player.getPA().sendString(37509, "@red@Inactive");
                    player.getPA().sendString(37510, "@red@Inactive");
                }
            }
        }

        player.getPA().sendString(37511, "@whi@" + player.getDiscordPoints());
    }

    public static void buttonClick(Player player) {
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
        player.getPA().sendEnterString("Enter the code from the Discord Bot.", DiscordIntegration::integrateAccount);
    }

    public static void disconnectUser(Player player) {
        if (player == null) return;

        Iterator<Map.Entry<String, Long>> it = connectedAccounts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> entry = it.next();
            if (entry.getKey().equalsIgnoreCase(player.getLoginName())) {
                it.remove();
                player.setDiscordlinked(false);
                player.setDiscordTag("");
                player.setDiscordUser(0);
                player.sendMessage("Your discord account has been removed from your account.");
                break;
            }
        }
        Discord.writeServerSyncMessage("[DISCORD] " + player.getDisplayName() + " has disconnected their account.");
        updateDiscordInterface(player);
    }

    public static void sendMessage(String message, long channel) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) return;
        if (Discord.getJDA() == null) return;

        TextChannel ch = Discord.getJDA().getTextChannelById(channel);
        if (ch != null) {
            ch.sendMessage(message).queue();
        }
    }

    public static long delay;

    public static void givePoints() {
        if (delay > System.currentTimeMillis()) return;
        if (Discord.getJDA() == null) return;

        Guild guild = Discord.getJDA().getGuildById(1182479719095078914L); // Server ID
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
                String statusName = a.getName();
                if (statusName == null) continue;
                if (statusName.toLowerCase().contains("zaryx")) {
                    containsStatus = true;
                    break;
                }
            }

            for (Member booster : guild.getBoosters()) {
                if (booster != null && booster.getIdLong() == member.getIdLong()) {
                    boosting = true;

                    if (player.getDiscordboostlastClaimed() < System.currentTimeMillis()) {
                        player.getItems().addItemUnderAnyCircumstance(13346, 2);
                        player.getItems().addItemUnderAnyCircumstance(696, 100);
                        player.getItems().addItemUnderAnyCircumstance(8167, 1);
                        player.sendMessage("Your discord boost has granted you with 2x UMB, 25m MadPoints & a Nomad Chest!");
                        player.setDiscordboostlastClaimed(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
                    }
                    break;
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
        // TODO: map roles to donor tiers if desired
    }
}
