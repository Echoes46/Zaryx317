package io.zaryx.sql.dailytracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.util.DataStorage;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DailyDataTracker {

    public static boolean ENABLED = true;
    private static final String DATA_FILE = "./etc/daily_tracker_data.json";
    private static Map<TrackerType, TrackerData> trackerDataMap = new HashMap<>();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    static {
        loadData();
        startScheduler();
    }

    private static void startScheduler() {
        scheduler.scheduleAtFixedRate(DailyDataTracker::saveData, 0, 1, TimeUnit.MINUTES);
    }

    public static void loadData() {
        try (FileReader reader = new FileReader(DATA_FILE)) {
            Type type = new TypeToken<Map<TrackerType, TrackerData>>() {}.getType();
            trackerDataMap = gson.fromJson(reader, type);
            if (trackerDataMap == null) {
                trackerDataMap = new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            trackerDataMap = new HashMap<>();
        }
    }

    public static void saveData() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            gson.toJson(trackerDataMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void insertData() {
        if (!ENABLED) {
            return;
        }
        TrackerData data = trackerDataMap.getOrDefault(TrackerType.VOTES, new TrackerData());

        data.setVotes(TrackerType.VOTES.getTrackerData());
        data.setDonations(TrackerType.DONATIONS.getTrackerData());
        data.setRealOnline(PlayerHandler.getUniquePlayerCount());
        data.setNewJoins(TrackerType.NEW_JOINS.getTrackerData());
        data.setDonorBoss(TrackerType.DONOR_BOSS.getTrackerData());
        data.setVoteBoss(TrackerType.VOTE_BOSS.getTrackerData());
        data.setAfkBoss(TrackerType.AFK_BOSS.getTrackerData());
        data.setDurial(TrackerType.DURIAL.getTrackerData());
        data.setGroot(TrackerType.GROOT.getTrackerData());

        trackerDataMap.put(TrackerType.VOTES, data);
        saveData();

        for (TrackerType trackerType : TrackerType.values()) {
            trackerType.setTrackerData(0);
            trackerType.getUniqueData().clear();
            trackerType.getUniqueData2().clear();
            DataStorage.saveData(trackerType.name(), trackerType.getTrackerData());
        }
    }

    public static void addUniqueData(TrackerType trackerType, String uniqueData, String uniqueData2) {
        TrackerData data = trackerDataMap.getOrDefault(trackerType, new TrackerData());
        if (data.getUniqueData().contains(uniqueData)) {
            return;
        }
        if (data.getUniqueData2().contains(uniqueData2)) {
            return;
        }

        data.getUniqueData().add(uniqueData);
        data.getUniqueData2().add(uniqueData2);

        trackerDataMap.put(trackerType, data);
        saveData();
    }

    public static void sendDailyTrackerEmail() throws IOException {
        LocalDate currentDate = LocalDate.now();

        StringBuilder emailMessage = new StringBuilder("Daily Votes: " + TrackerType.VOTES.getTrackerData() + "\n");
        emailMessage.append("Daily Donations: ").append(TrackerType.DONATIONS.getTrackerData()).append("\n");
        emailMessage.append("Real Online: ").append(trackerDataMap.get(TrackerType.REAL_ONLINE).getUniqueData().size()).append("\n");
        emailMessage.append("New Joins: ").append(TrackerType.NEW_JOINS.getTrackerData()).append("\n");
        emailMessage.append("Donor Bosses: ").append(TrackerType.DONOR_BOSS.getTrackerData()).append("\n");
        emailMessage.append("Vote Bosses: ").append(TrackerType.VOTE_BOSS.getTrackerData()).append("\n");
        emailMessage.append("AFK Bosses: ").append(TrackerType.AFK_BOSS.getTrackerData()).append("\n");
        emailMessage.append("Durial Bosses: ").append(TrackerType.DURIAL.getTrackerData()).append("\n");
        emailMessage.append("Groot Bosses: ").append(TrackerType.GROOT.getTrackerData()).append("\n");

       // EmailManager.sendEmail("RuneCrest Daily Tracker [" + currentDate.getMonth() + " " + currentDate.getDayOfMonth() + ", " + currentDate.getYear() + "]", emailMessage.toString());
    }

    public static LocalDate today = LocalDate.now();

    public static void newDay() {
        LocalDate now = LocalDate.now();
        if (today == null) {
            today = LocalDate.now();
            DataStorage.saveData("today", today.toString());
        }
        if (!today.equals(now) && ENABLED) {
            today = now;
            DataStorage.saveData("today", today);
            insertData();
        }
    }

    public static class TrackerData {
        private int votes;
        private int donations;
        private int realOnline;
        private int newJoins;
        private int donorBoss;
        private int voteBoss;
        private int afkBoss;
        private int durial;
        private int groot;
        private Set<String> uniqueData;
        private Set<String> uniqueData2;

        public TrackerData() {
            uniqueData = new HashSet<>();
            uniqueData2 = new HashSet<>();
        }

        // Getters and setters...

        public int getVotes() {
            return votes;
        }

        public void setVotes(int votes) {
            this.votes = votes;
        }

        public int getDonations() {
            return donations;
        }

        public void setDonations(int donations) {
            this.donations = donations;
        }

        public int getRealOnline() {
            return realOnline;
        }

        public void setRealOnline(int realOnline) {
            this.realOnline = realOnline;
        }

        public int getNewJoins() {
            return newJoins;
        }

        public void setNewJoins(int newJoins) {
            this.newJoins = newJoins;
        }

        public int getDonorBoss() {
            return donorBoss;
        }

        public void setDonorBoss(int donorBoss) {
            this.donorBoss = donorBoss;
        }

        public int getVoteBoss() {
            return voteBoss;
        }

        public void setVoteBoss(int voteBoss) {
            this.voteBoss = voteBoss;
        }

        public int getAfkBoss() {
            return afkBoss;
        }

        public void setAfkBoss(int afkBoss) {
            this.afkBoss = afkBoss;
        }

        public int getDurial() {
            return durial;
        }

        public void setDurial(int durial) {
            this.durial = durial;
        }

        public int getGroot() {
            return groot;
        }

        public void setGroot(int groot) {
            this.groot = groot;
        }

        public Set<String> getUniqueData() {
            return uniqueData;
        }

        public void setUniqueData(Set<String> uniqueData) {
            this.uniqueData = uniqueData;
        }

        public Set<String> getUniqueData2() {
            return uniqueData2;
        }

        public void setUniqueData2(Set<String> uniqueData2) {
            this.uniqueData2 = uniqueData2;
        }
    }

    public enum TrackerType {
        VOTES, DONATIONS, REAL_ONLINE, NEW_JOINS, DONOR_BOSS, VOTE_BOSS, AFK_BOSS, DURIAL, GROOT;

        private int trackerData;
        private Set<String> uniqueData = new HashSet<>();
        private Set<String> uniqueData2 = new HashSet<>();

        public int getTrackerData() {
            return trackerData;
        }

        public void setTrackerData(int trackerData) {
            this.trackerData = trackerData;
        }

        public Set<String> getUniqueData() {
            return uniqueData;
        }

        public Set<String> getUniqueData2() {
            return uniqueData2;
        }
    }
}
