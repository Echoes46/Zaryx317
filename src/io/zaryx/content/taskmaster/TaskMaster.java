package io.zaryx.content.taskmaster;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.Mode;
import io.zaryx.model.entity.player.mode.ModeType;
import io.zaryx.model.items.GameItem;
import io.zaryx.model.items.bank.BankItem;
import io.zaryx.model.items.bank.BankTab;
import io.zaryx.util.Misc;

import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TaskMaster {

        private Player player;

        public TaskMaster(Player player) {
                this.player = player;
        }

        public List<TaskMasterKills> taskMasterKillsList = new ArrayList<>();
        private LocalDateTime moneyMakingTime = LocalDateTime.MIN;
        private static final String DIR = "./save_files/taskmaster/";
        private boolean earn = false;

        public void handleDailySkips() {
                if (player.amDonated < 1500) {
                        return;
                }
                int amt = 1;
                if (player.amDonated  >= 2000 && player.amDonated < 3000) {
                        amt = 2;
                } else if (player.amDonated  >= 3000) {
                        amt = 3;
                }
                if (player.getItems().hasAnywhere(20238)) {
                        return;
                }
                for (BankTab bankTab : player.getBank().getBankTab()) {
                        if (bankTab.getItemAmount(new BankItem(20237)) > 0 ||
                                bankTab.contains(new BankItem(20237)) ||
                                bankTab.containsAmount(new BankItem(20237))) {
                                return;
                        }
                }

                player.getItems().addItemUnderAnyCircumstance(20238, amt);
        }


        public void showInterface() {
                for (TaskMasterKills taskMasterKills : player.getTaskMaster().taskMasterKillsList) {
                        if (taskMasterKills.getWeekly()) {
                                player.getPA().sendString(38008, "Complete @whi@" + taskMasterKills.getDesc() + " @or1@" + (taskMasterKills.getAmountToKill() - taskMasterKills.getAmountKilled()) + " times.");
                                player.getPA().sendString(38009, getTaskTime(taskMasterKills));
                                player.getPA().sendString(38010, taskMasterKills.getAmountToKill() > taskMasterKills.getAmountKilled() ? "@red@Incomplete" : "@gre@Compelete");
                                player.getPA().sendConfig(5002, taskMasterKills.getAmountToKill() > taskMasterKills.getAmountKilled() ? 0 : 1);
                        }
                        if (taskMasterKills.getTaskType() == TaskType.COMBAT && !taskMasterKills.getWeekly()) {
                                player.getPA().sendString(38002, "Kill @whi@" + taskMasterKills.getDesc() + " @or1@" + (taskMasterKills.getAmountToKill() - taskMasterKills.getAmountKilled()) + " times.");
                                player.getPA().sendString(38003, getTaskTime(taskMasterKills));
                                player.getPA().sendString(38004, taskMasterKills.getAmountToKill() > taskMasterKills.getAmountKilled() ? "@red@Incomplete" : "@gre@Compelete");
                                player.getPA().sendConfig(5000, taskMasterKills.getAmountToKill() > taskMasterKills.getAmountKilled() ? 0 : 1);
                        }
                        if (taskMasterKills.getTaskType() == TaskType.SKILLING) {
                                player.getPA().sendString(38005, taskMasterKills.getDesc() + " @or1@" + (taskMasterKills.getAmountToKill() - taskMasterKills.getAmountKilled()) + " times.");
                                player.getPA().sendString(38006, getTaskTime(taskMasterKills));
                                player.getPA().sendString(38007, taskMasterKills.getAmountToKill() > taskMasterKills.getAmountKilled() ? "@red@Incomplete" : "@gre@Compelete");
                                player.getPA().sendConfig(5001, taskMasterKills.getAmountToKill() > taskMasterKills.getAmountKilled() ? 0 : 1);
                        }
                }
                player.getPA().showInterface(38000);
        }

        public LocalDateTime localDateTime;

        public void generateTasks(Player player, boolean resetScroll) {
                if (player.getTaskMaster().taskMasterKillsList.isEmpty() || player.getTaskMaster().taskMasterKillsList.stream().anyMatch(t -> LocalDateTime.now().isAfter(t.getLocalDateTime()))) {
                        player.getTaskMaster().setMoneyMakingTime(LocalDateTime.now().plus(1, ChronoUnit.HOURS));
                        if (player.getTaskMaster().taskMasterKillsList != null) {
                                player.getTaskMaster().taskMasterKillsList.removeIf(killz -> LocalDateTime.now().isAfter(killz.getLocalDateTime()));
                        } else {
                                player.getTaskMaster().taskMasterKillsList.clear();
                        }

                        while (player.getTaskMaster().taskMasterKillsList.size() < 3) {
                                Tasks tasks = Tasks.values()[Misc.random(Tasks.values().length-1)];

                                if (player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.GROUP_WILDYMAN))) {
                                        if (tasks.taskType == TaskType.COMBAT && !tasks.daily && tasks.wildy && player.getTaskMaster().taskMasterKillsList.stream().noneMatch(t -> t.getTaskType() == TaskType.COMBAT && !t.getWeekly())) {
                                                TaskMasterKills killz = new TaskMasterKills(tasks.max, 0, new GameItem[]{new GameItem(13307, 750)}, tasks.difficultyType, tasks.taskType, false, LocalDateTime.now().plus(1, ChronoUnit.HOURS), tasks.desc);
                                                player.getTaskMaster().taskMasterKillsList.add(killz);
                                                player.sendMessage("[@pur@TaskMaster@bla@] You've been assigned " + killz.getDesc() +" x "+ killz.getAmountToKill() + ", " + killz.getTaskDifficulty().name().toLowerCase() + " difficulty");
                                        }

                                        if (tasks.taskType == TaskType.SKILLING && player.getTaskMaster().taskMasterKillsList.stream().noneMatch(t -> t.getTaskType() == TaskType.SKILLING)) {
                                                TaskMasterKills killz = new TaskMasterKills(tasks.max, 0, new GameItem[]{new GameItem(13307, 750)}, tasks.difficultyType, tasks.taskType, false, LocalDateTime.now().plus(1, ChronoUnit.HOURS), tasks.desc);
                                                player.getTaskMaster().taskMasterKillsList.add(killz);
                                                player.sendMessage("[@pur@TaskMaster@bla@] You've been assigned " + killz.getDesc().replace("@whi@","") +" x "+ killz.getAmountToKill() + ", " + killz.getTaskDifficulty().name().toLowerCase() + " difficulty");
                                        }

                                        if (player.getTaskMaster().taskMasterKillsList.stream().noneMatch(TaskMasterKills::getWeekly)) {
                                                if (tasks.daily && tasks.wildy) {
                                                        TaskMasterKills killz = new TaskMasterKills(tasks.max, 0, new GameItem[]{new GameItem(13307, 1500)}, tasks.difficultyType, tasks.taskType, true, LocalDateTime.now().plus(1, ChronoUnit.DAYS), tasks.desc);
                                                        player.getTaskMaster().taskMasterKillsList.add(killz);
                                                        player.sendMessage("[@pur@TaskMaster@bla@] You've been assigned " + killz.getDesc() +" x "+ killz.getAmountToKill() + ", " + killz.getTaskDifficulty().name().toLowerCase() + " difficulty");
                                                }
                                        }
                                } else {
                                        if (tasks.taskType == TaskType.COMBAT && !tasks.daily && player.getTaskMaster().taskMasterKillsList.stream().noneMatch(t -> t.getTaskType() == TaskType.COMBAT && !t.getWeekly())) {
                                                TaskMasterKills killz = new TaskMasterKills(tasks.max, 0, new GameItem[]{new GameItem(13307, 1500)}, tasks.difficultyType, tasks.taskType, false, LocalDateTime.now().plus(1, ChronoUnit.HOURS), tasks.desc);
                                                player.getTaskMaster().taskMasterKillsList.add(killz);
                                                player.sendMessage("[@pur@TaskMaster@bla@] You've been assigned " + killz.getDesc() +" x "+ killz.getAmountToKill() + ", " + killz.getTaskDifficulty().name().toLowerCase() + " difficulty");
                                        }

                                        if (tasks.taskType == TaskType.SKILLING && player.getTaskMaster().taskMasterKillsList.stream().noneMatch(t -> t.getTaskType() == TaskType.SKILLING)) {
                                                TaskMasterKills killz = new TaskMasterKills(tasks.max, 0, new GameItem[]{new GameItem(13307, 2500)}, tasks.difficultyType, tasks.taskType, false, LocalDateTime.now().plus(1, ChronoUnit.HOURS), tasks.desc);
                                                player.getTaskMaster().taskMasterKillsList.add(killz);
                                                player.sendMessage("[@pur@TaskMaster@bla@] You've been assigned " + killz.getDesc().replace("@whi@","") +" x "+ killz.getAmountToKill() + ", " + killz.getTaskDifficulty().name().toLowerCase() + " difficulty");
                                        }

                                        if (player.getTaskMaster().taskMasterKillsList.stream().noneMatch(TaskMasterKills::getWeekly)) {
                                                if (tasks.daily) {
                                                        TaskMasterKills killz = new TaskMasterKills(tasks.max, 0, new GameItem[]{new GameItem(13307, 2500)}, tasks.difficultyType, tasks.taskType, true, LocalDateTime.now().plus(1, ChronoUnit.DAYS), tasks.desc);
                                                        player.getTaskMaster().taskMasterKillsList.add(killz);
                                                        player.sendMessage("[@pur@TaskMaster@bla@] You've been assigned " + killz.getDesc() +" x "+ killz.getAmountToKill() + ", " + killz.getTaskDifficulty().name().toLowerCase() + " difficulty");
                                                }
                                        }
                                }
                        }

                }
                player.getTaskMaster().setEarn(true);
                if (resetScroll) {
                        player.sendMessage("You have reset your Hourly/Daily tasks!");
                }
        }

        public String getTime(Player player) {
                LocalDateTime localtime = player.getTaskMaster().getMoneyMakingTime();
                LocalDateTime localtime1 = LocalDateTime.now();

                long hours = localtime1.until(localtime, ChronoUnit.HOURS);
                long minutes = localtime1.until(localtime, ChronoUnit.MINUTES);
                long seconds = localtime1.until(localtime, ChronoUnit.SECONDS);

                return hours + "h " + minutes % 60 + "m " + seconds % 60 + "s";
        }

        public String getTaskTime(TaskMasterKills taskMasterKills) {
                LocalDateTime localtime = taskMasterKills.getLocalDateTime();
                LocalDateTime localtime1 = LocalDateTime.now();

                long hours = localtime1.until(localtime, ChronoUnit.HOURS);
                long minutes = localtime1.until(localtime, ChronoUnit.MINUTES);
                long seconds = localtime1.until(localtime, ChronoUnit.SECONDS);

                return hours + "h " + minutes % 60 + "m " + seconds % 60 + "s";

        }

        public LocalDateTime getMoneyMakingTime() {
                return moneyMakingTime;
        }

        public void setMoneyMakingTime(LocalDateTime moneyMakingTime) {
                this.moneyMakingTime = moneyMakingTime;
        }

        public int calculatePercentage(int obtained, int total) {
                return obtained * 100 / total;
        }

        public void trackActivity(Player player, TaskMasterKills kills) {
                if (kills.getClaimedReward()) {
                        return;
                }

                player.sendMessage("You have now completed <col=FF0000>"
                        + calculatePercentage(kills.getAmountKilled(), kills.getAmountToKill())
                        + "%<col=0> of your objective.");

                if (kills.getAmountKilled() >= kills.getAmountToKill()) {
                        finishTask(player, kills);
                }
        }


                // Method to pick a random subset of items
                private List<int[]> pickRandomRewards(List<int[]> rewardList, int count) {
                        List<int[]> result = new ArrayList<>(rewardList);
                        Random random = new Random();
                        List<int[]> selectedRewards = new ArrayList<>();
                        while (selectedRewards.size() < count && !result.isEmpty()) {
                                int index = random.nextInt(result.size());
                                selectedRewards.add(result.remove(index));
                        }
                        return selectedRewards;
                }

                public void finishTask(Player player, TaskMasterKills kills) {
                        if (kills.getClaimedReward())
                                return;

                        List<int[]> rewards = new ArrayList<>();

                        // Define lists of possible rewards as arrays of {itemId, amount}
                        List<int[]> easyRewards = Arrays.asList(
                                new int[]{11681, 1000},
                                new int[]{995, 1000000},
                                new int[]{6679, 1},
                                new int[]{2528, 1},
                                new int[]{2528, 1},
                                new int[]{4447, 1},
                                new int[]{6828, 1},
                                new int[]{30002, 1},
                                new int[]{28417, 1},
                                new int[]{11681, 1000},
                                new int[]{995, 1000000},
                                new int[]{696, 1},
                                new int[]{2528, 1},
                                new int[]{2528, 1},
                                new int[]{4447, 1},
                                new int[]{6828, 1},
                                new int[]{30002, 1},
                                new int[]{28417, 1},
                                new int[]{956, 1},
                                new int[]{696, 1}
                        );
                        List<int[]> mediumRewards = Arrays.asList(
                                new int[]{11681, 2000},
                                new int[]{6679, 1},
                                new int[]{30002, 3},
                                new int[]{6828, 1},
                                new int[]{995, 2500000},
                                new int[]{24364, 1},
                                new int[]{28418, 1},
                                new int[]{11681, 2000},
                                new int[]{6679, 1},
                                new int[]{30002, 3},
                                new int[]{6828, 1},
                                new int[]{995, 2500000},
                                new int[]{24364, 1},
                                new int[]{28418, 1},
                                new int[]{28418, 1},
                                new int[]{11681, 2000},
                                new int[]{6679, 1},
                                new int[]{30002, 3},
                                new int[]{6828, 1},
                                new int[]{995, 2500000},
                                new int[]{24364, 1},
                                new int[]{28418, 1},
                                new int[]{13346, 1},
                                new int[]{6769, 1},
                                new int[]{696, 5}
                        );
                        List<int[]> hardRewards = Arrays.asList(
                                new int[]{11681, 3000},
                                new int[]{6679, 2},
                                new int[]{995, 3000000},
                                new int[]{30002, 5},
                                new int[]{11681, 3000},
                                new int[]{6679, 2},
                                new int[]{11739, 1},
                                new int[]{995, 3000000},
                                new int[]{30002, 5},
                                new int[]{6828, 1},
                                new int[]{13346, 1},
                                new int[]{6828, 1},
                                new int[]{13346, 1},
                                new int[]{696, 8},
                                new int[]{696, 8},
                                new int[]{13346, 1},
                                new int[]{6828, 1},
                                new int[]{13346, 1},
                                new int[]{696, 8},
                                new int[]{696, 8},
                                new int[]{28418, 2},
                                new int[]{12588, 1},
                                new int[]{2403, 1},
                                new int[]{24364, 1}
                        );
                        List<int[]> eliteRewards = Arrays.asList(
                                new int[]{11681, 5000},
                                new int[]{26545, 2},
                                new int[]{6679, 3},
                                new int[]{995, 5000000},
                                new int[]{13346, 1},
                                new int[]{6805, 1},
                                new int[]{11681, 5000},
                                new int[]{26545, 2},
                                new int[]{11681, 5000},
                                new int[]{26545, 2},
                                new int[]{6679, 3},
                                new int[]{995, 5000000},
                                new int[]{13346, 1},
                                new int[]{6805, 1},
                                new int[]{11681, 5000},
                                new int[]{2401, 5},
                                new int[]{6679, 3},
                                new int[]{995, 5000000},
                                new int[]{13346, 1},
                                new int[]{6805, 1},
                                new int[]{28419, 3},
                                new int[]{19891, 1},
                                new int[]{12582, 1},
                                new int[]{12579, 1},
                                new int[]{696, 12},
                                new int[]{6678, 1},
                                new int[]{2396, 1},
                                new int[]{24365, 1}
                        );

                        // Determine reward list based on task type and difficulty
                        if (!kills.getWeekly()) {
                                if (kills.getTaskType() == TaskType.COMBAT) {
                                        if (kills.getTaskDifficulty() == TaskDifficulty.EASY) {
                                                rewards = easyRewards;
                                                player.sendMessage("@cya@You completed your Hourly Combat Task");
                                        } else if (kills.getTaskDifficulty() == TaskDifficulty.MEDIUM) {
                                                rewards = mediumRewards;
                                                player.sendMessage("@cya@You completed your Hourly Combat Task");
                                        } else if (kills.getTaskDifficulty() == TaskDifficulty.HARD) {
                                                rewards = hardRewards;
                                                player.sendMessage("@cya@You completed your Hourly Combat Task");
                                        } else if (kills.getTaskDifficulty() == TaskDifficulty.ELITE) {
                                                rewards = eliteRewards;
                                                player.sendMessage("@cya@You completed your Hourly Combat Task");
                                        }

                                } else if (kills.getTaskType() == TaskType.SKILLING) {
                                        if (kills.getTaskDifficulty() == TaskDifficulty.EASY) {
                                                rewards = easyRewards;
                                                player.sendMessage("@cya@You completed your Hourly Skilling Task");
                                        } else if (kills.getTaskDifficulty() == TaskDifficulty.MEDIUM) {
                                                rewards = mediumRewards;
                                                player.sendMessage("@cya@You completed your Hourly Skilling Task");
                                        } else if (kills.getTaskDifficulty() == TaskDifficulty.HARD) {
                                                rewards = hardRewards;
                                                player.sendMessage("@cya@You completed your Hourly Skilling Task");
                                        } else if (kills.getTaskDifficulty() == TaskDifficulty.ELITE) {
                                                rewards = eliteRewards;
                                                player.sendMessage("@cya@You completed your Hourly Skilling Task");
                                        }
                                }
                        } else if (kills.getTaskType() == TaskType.COMBAT) {
                                // Weekly Combat Task rewards
                                rewards = Arrays.asList(
                                        new int[]{11681, 2500},
                                        new int[]{696, 25},
                                        new int[]{6828, 1}
                                );
                                player.sendMessage("@cya@You completed your Daily Combat Task");
                        }

                        // Pick 2 random rewards if rewards list is not empty
                        if (!rewards.isEmpty()) {
                                List<int[]> selectedRewards = pickRandomRewards(rewards, 2);
                                for (int[] reward : selectedRewards) {
                                        player.getItems().addItemUnderAnyCircumstance(reward[0], reward[1]);
                                }
                        }

                        kills.setClaimedReward(true);
                        player.sendMessage("<col=4B0082>Congratulations you finished " + kills.getDesc() + " x " + kills.getAmountToKill() + ".");
                }



        public boolean getEarn() {
                return earn;
        }

        public void setEarn(boolean earn) {
                this.earn = earn;
        }

        public void loadAllMoneyMaking(Player player) {
            // Create the path and file objects.
            Path path = Paths.get(DIR + player.getLoginName() + ".json");
            File file = path.toFile();

            if (!file.exists())
                return;

            // Now read the properties from the json parser.
            try (FileReader fileReader = new FileReader(file)) {
                Gson gson = new GsonBuilder().create();

                // Parse root safely
                JsonElement rootEl = JsonParser.parseReader(fileReader);
                if (rootEl == null || rootEl.isJsonNull()) {
                    // Nothing to load; keep list empty
                    System.out.println("[TaskMaster] " + player.getLoginName() + ": moneyMaking is null/empty");
                    return;
                }

                // We want to deserialize a List<TaskMasterKills>
                Type listType = new TypeToken<List<TaskMasterKills>>() {
                }.getType();

                // Normalize to an array: accept array OR single object; ignore anything else
                JsonArray asArray;
                if (rootEl.isJsonArray()) {
                    asArray = rootEl.getAsJsonArray();
                } else if (rootEl.isJsonObject()) {
                    asArray = new JsonArray();
                    asArray.add(rootEl.getAsJsonObject());
                } else {
                    System.out.println("[TaskMaster] " + player.getLoginName() + ": unexpected JSON type " + rootEl.getClass().getSimpleName());
                    return;
                }

                TaskMasterKills[] arr = gson.fromJson(asArray, TaskMasterKills[].class);
                List<TaskMasterKills> kills = (arr == null) ? Collections.emptyList() : Arrays.asList(arr);


                player.getTaskMaster().taskMasterKillsList.addAll(kills);

                System.out.println("Loaded " + player.getTaskMaster().taskMasterKillsList.size()
                        + " tasks to " + player.getLoginName() + ".json");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

            public void saveAllMoneyMaking(Player player) {
                // Create the path and file objects.
                Path path = Paths.get(DIR + player.getLoginName() + ".json");
                File file = path.toFile();
                file.getParentFile().setWritable(true);

                // Attempt to make the sale save directory if it doesn't exist.
                if (!file.getParentFile().exists()) {
                        try {
                                file.getParentFile().mkdirs();
                        } catch (SecurityException e) {
                                System.out.println("Unable to create directory for money making data!");
                        }
                }

                try (FileWriter writer = new FileWriter(file)) {
                        Gson builder = new GsonBuilder().setPrettyPrinting().create();
                        String json = builder.toJson(player.getTaskMaster().taskMasterKillsList);
                        writer.write(json);

                        System.out.println("Saved " + player.getTaskMaster().taskMasterKillsList + " tasks to "
                                + player.getLoginName() + ".json");

                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

    private static JsonArray getArrayOrEmpty(JsonObject obj, String key) {
        if (obj == null) return new JsonArray();
        JsonElement el = obj.get(key);
        return (el != null && el.isJsonArray()) ? el.getAsJsonArray() : new JsonArray();
    }

    private static String getString(JsonObject obj, String key, String def) {
        JsonElement el = obj.get(key);
        return (el != null && el.isJsonPrimitive()) ? el.getAsString() : def;
    }

    private static int getInt(JsonObject obj, String key, int def) {
        JsonElement el = obj.get(key);
        return (el != null && el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber())
                ? el.getAsInt() : def;
    }

    private static boolean getBool(JsonObject obj, String key, boolean def) {
        JsonElement el = obj.get(key);
        return (el != null && el.isJsonPrimitive()) ? el.getAsBoolean() : def;
    }

}
