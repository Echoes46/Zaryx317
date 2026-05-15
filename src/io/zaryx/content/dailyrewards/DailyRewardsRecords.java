package io.zaryx.content.dailyrewards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import io.zaryx.Server;
import io.zaryx.model.entity.player.Player;
import org.apache.commons.io.FileUtils;

/**
 * Updated by Khaos
 */
public class DailyRewardsRecords {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(DailyRewardsRecords.class.getName());

    private static Map<String, Map<String, DailyRewardsRecord>> records = new HashMap<>();

    private static final ExecutorService SERVICE = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("daily-rewards-%d").build()
    );

    private static final String FILE_NAME = "daily_rewards_record.json";
    private static String file;

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    public static void load() throws IOException {
        file = Server.getSaveDirectory() + FILE_NAME;

        File rewardFile = new File(file);

        if (rewardFile.getParentFile() != null && !rewardFile.getParentFile().exists()) {
            rewardFile.getParentFile().mkdirs();
        }

        if (!rewardFile.exists()) {
            records = new HashMap<>();
            save();
            log.warning("No daily reward claims found. Created new daily rewards record file.");
            return;
        }

        if (rewardFile.length() == 0) {
            records = new HashMap<>();
            save();
            log.warning("Daily rewards record file was empty. Resetting file.");
            return;
        }

        Type type = new TypeToken<Map<String, Map<String, DailyRewardsRecord>>>() {
        }.getType();

        records = GSON.fromJson(FileUtils.readFileToString(rewardFile), type);

        if (records == null) {
            records = new HashMap<>();
        }

        int totalClaims = records.values()
                .stream()
                .mapToInt(map -> map == null ? 0 : map.values().size())
                .sum();

        log.info("Loaded data on " + totalClaims + " claimed daily rewards.");
    }

    private static void save() {
        SERVICE.submit(() -> {
            try {
                File rewardFile = new File(file);

                if (rewardFile.getParentFile() != null && !rewardFile.getParentFile().exists()) {
                    rewardFile.getParentFile().mkdirs();
                }

                FileUtils.writeStringToFile(rewardFile, GSON.toJson(records));

            } catch (Exception e) {
                log.warning("Failed to save daily rewards record file.");
                e.printStackTrace();
            }
        });
    }

    static boolean canClaim(Player player) {
        String id = DailyRewardContainer.get().getIdentifier();

        if (records == null) {
            records = new HashMap<>();
        }

        if (records.containsKey(id)) {
            List<DailyRewardsRecord> playerRecords = build(player);

            for (DailyRewardsRecord record : playerRecords) {
                DailyRewardsRecord playerRecord = records.get(id).get(record.getAddress());

                if (playerRecord != null) {
                    if (ChronoUnit.HOURS.between(playerRecord.getDate(), LocalDateTime.now()) < 24) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static List<DailyRewardsRecord> build(Player player) {
        List<DailyRewardsRecord> records = Lists.newArrayList();

        records.add(new DailyRewardsRecord(player.getIpAddress(), LocalDateTime.now()));

        if (player.getMacAddress() != null
                && player.getMacAddress().length() > 0
                && !player.getMacAddress().equals("0")) {
            records.add(new DailyRewardsRecord(player.getMacAddress(), LocalDateTime.now()));
        }

        return records;
    }

    static void add(Player player, int day) {
        String id = DailyRewardContainer.get().getIdentifier();

        if (records == null) {
            records = new HashMap<>();
        }

        List<DailyRewardsRecord> playerRecords = build(player);

        if (!records.containsKey(id)) {
            records.clear(); // Clear everything else because this is a new reward collection
            records.put(id, Maps.newHashMap());
        }

        for (DailyRewardsRecord record : playerRecords) {
            records.get(id).put(record.getAddress(), record);
        }

        save();
    }

    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, com.google.gson.JsonDeserializationContext context) {
            return LocalDateTime.parse(json.getAsString());
        }
    }
}