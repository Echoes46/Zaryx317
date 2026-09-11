package io.zaryx.content.taskmaster;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.ModeType;
import io.zaryx.model.items.GameItem;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.save.PlayerSave;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/** Task state and rewards are saved in the same atomic player snapshot as inventory. */
public class TaskMaster {
    private final Player player;
    public List<TaskMasterKills> taskMasterKillsList = new ArrayList<>();
    private TaskDifficulty preference = TaskDifficulty.MEDIUM;
    private boolean loadedFromPlayerSave;
    private boolean earn;
    private LocalDateTime moneyMakingTime = LocalDateTime.MIN;
    public LocalDateTime localDateTime;
    private long nextRefresh;
    private int selected;
    private final Random random = new Random();
    private static final Gson JSON = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
            new LocalDateTimeAdapter()).create();
    public TaskMaster(Player player) { this.player = player; }
    public static String clean(String s) { return s == null ? "" : s.replaceAll("@[a-zA-Z0-9]+@", "").replaceAll("<[^>]+>", ""); }
    public static String cycle(int slot) { return new String[]{"Hourly combat", "Hourly skilling", "Daily challenge", "Weekly challenge"}[slot]; }
    public TaskMasterKills task(int slot) { return taskMasterKillsList.stream().filter(t -> t.slot() == slot).findFirst().orElse(null); }

    public void generateTasks(Player p, boolean resetScroll) {
        long nowMillis = System.currentTimeMillis();
        if (!resetScroll && nowMillis < nextRefresh) return;
        nextRefresh = nowMillis + 1000;
        refresh(LocalDateTime.now());
        updateTracker();
        if (p.getOpenInterface() == 38000) render();
    }
    public void refresh(LocalDateTime now) {
        taskMasterKillsList.removeIf(t -> t == null || t.getTaskType() == null || t.getTaskDifficulty() == null
                || t.getDesc() == null || t.getAmountToKill() <= 0
                || (t.expired(now) && (!t.complete() || t.getClaimedReward())));
        for (TaskMasterKills t : taskMasterKillsList) {
            if (!t.rewardsAssigned()) {
                t.setGameItems(TaskRewards.roll(t.getTaskDifficulty(), t.getWeekly(), random));
                t.setRewardsAssigned(true);
            }
        }
        boolean wildy = player.getMode().getType() == ModeType.WILDYMAN || player.getMode().getType() == ModeType.GROUP_WILDYMAN;
        for (int slot=0; slot<4; slot++) {
            if (task(slot) != null) continue;
            List<Tasks> choices = candidates(slot, preference, wildy).stream().filter(t -> meetsSkillRequirement(player, t)).collect(Collectors.toList());
            if (choices.isEmpty()) continue;
            Tasks chosen = choices.get(random.nextInt(choices.size()));
            int amount = slot == 3 ? chosen.max * 3 : chosen.max;
            TaskMasterKills task = new TaskMasterKills(amount, 0, TaskRewards.roll(chosen.difficultyType, slot >= 2, random),
                    effectiveDifficulty(chosen), chosen.taskType, slot >= 2, slot < 2 ? now.plusHours(1) : slot == 2 ? now.plusDays(1) : now.plusWeeks(1), chosen.desc);
            task.setWeeklyChallenge(slot == 3);
            task.setRewardsAssigned(true);
            taskMasterKillsList.add(task);
        }
        earn = true;
    }
    public static List<Tasks> candidates(int slot, TaskDifficulty difficulty, boolean wildy) {
        return Arrays.stream(Tasks.values()).filter(t -> slot == 1 ? t.taskType == TaskType.SKILLING
                : t.taskType == TaskType.COMBAT && t.daily == (slot >= 2))
                .filter(t -> !wildy || t.wildy)
                .filter(t -> effectiveDifficulty(t).ordinal() <= difficulty.ordinal()).collect(Collectors.toList());
    }
    public static TaskDifficulty effectiveDifficulty(Tasks t) {
        if (!t.daily) return t.difficultyType;
        if (Arrays.asList("Inferno", "Nightmare", "Nex", "Alchemical Hydra").contains(t.desc)) return TaskDifficulty.ELITE;
        if (Arrays.asList("Kree'arra", "K'ril Tsutsaroth", "General Graardor", "Commander Zilyana", "Corporeal Beast", "TzTok-Jad", "Chambers", "Theatre Of Blood").contains(t.desc)) return TaskDifficulty.HARD;
        if (Arrays.asList("Zulrah", "Sarachnis", "Barrows").contains(t.desc)) return TaskDifficulty.MEDIUM;
        return t.difficultyType;
    }
    public static boolean meetsSkillRequirement(Player p, Tasks t) {
        String name=clean(t.desc).toLowerCase(Locale.ROOT);
        int skill=-1, level=1;
        if(name.contains("prayer pot")) { skill=15; level=38; }
        else if(name.contains("oak trees")) { skill=8; level=15; }
        else if(name.contains("willow trees")) { skill=8; level=30; }
        else if(name.contains("maple trees")) { skill=8; level=45; }
        else if(name.contains("magic trees")) { skill=8; level=75; }
        else if(name.contains("lobsters")) { skill=10; level=40; }
        else if(name.contains("sharks")) { skill=10; level=76; }
        else if(name.contains("swordfish")) { skill=7; level=45; }
        else if(name.contains("d'hide body")) { skill=12; level=63; }
        else if(name.contains("gems")) { skill=12; level=20; }
        else if(name.contains("rooftop")) { skill=16; level=10; }
        else if(name.contains("fletch")) { skill=9; level=5; }
        else if(name.equals("cerberus")) { skill=18; level=91; }
        else if(name.equals("alchemical hydra")) { skill=18; level=95; }
        return skill == -1 || p.getPA().getLevelForXP(p.playerXP[skill]) >= level;
    }
    public void showInterface() {
        if (!available()) { player.sendMessage("Please finish your current activity first."); return; }
        refresh(LocalDateTime.now());
        render();
        player.getPA().resetScrollBar(38030);
        player.getPA().showInterface(38000);
    }
    private boolean available() {
        return !player.isDead && !player.getInterfaceEvent().isActive()
                && !io.zaryx.Server.getMultiplayerSessionListener().inAnySession(player);
    }
    public boolean handleButton(int id) {
        if (!(id >= 38011 && id <= 38014 || id >= 38020 && id <= 38024)) return false;
        if (player.getOpenInterface() != 38000 || !available()) return true;
        if (id >= 38011 && id <= 38014) { selected = id - 38011; player.getPA().resetScrollBar(38030); }
        else if (id == 38020) {
            preference = TaskDifficulty.values()[(preference.ordinal()+1)%TaskDifficulty.values().length];
            player.sendMessage("Future assignments: up to " + preference.name().toLowerCase() + ". Current tasks are unchanged.");
        } else if (id == 38021) {
            TaskMasterKills t=task(selected);
            boolean pin=t != null && !t.isPinned();
            taskMasterKillsList.forEach(k -> k.setPinned(false));
            if (t != null) t.setPinned(pin);
            updateTracker();
        } else if (id == 38022) {
            TaskMasterKills t=task(selected);
            if (t != null) TaskTravel.open(player, t);
            return true;
        } else if (id == 38023) {
            TaskMasterKills t=task(selected);
            if (t != null) finishTask(player,t);
        } else { player.getPA().closeAllWindows(); return true; }
        render();
        return true;
    }
    private void render() {
        TaskMasterKills t=task(selected);
        List<String> lines=new ArrayList<>();
        if (t == null) {
            lines.add("No eligible task at your chosen difficulty for this mode.");
            lines.add("Raise the difficulty below to unlock more assignments.");
        } else {
            lines.add("@or1@" + clean(t.getDesc()));
            lines.add("Progress: " + t.getAmountKilled() + " / " + t.getAmountToKill() + " (" + calculatePercentage(t.getAmountKilled(),t.getAmountToKill()) + "%)");
            lines.add("Difficulty: " + t.getTaskDifficulty().name() + " | " + status(t));
            lines.add("Time remaining: " + getTaskTime(t));
            lines.add(""); lines.add("@or1@YOUR REWARDS");
            for(GameItem item:t.getItems()) lines.add(item.getAmount() + " x " + clean(ItemDef.forId(item.getId()).getName()));
            lines.add("Rewards are fixed for this assignment. Claim after completion.");
            lines.add(""); lines.add("@or1@WHERE TO GO");
            lines.addAll(TaskTravel.directions(t));
            lines.add("");
            lines.add("Completed rewards remain available after expiry.");
            lines.add("Claiming requires inventory space; no rewards are dropped.");
        }
        lines=io.zaryx.content.commands.all.Pet.wrapDetails(lines);
        player.getPA().sendString(38002,cycle(selected));
        for(int i=0;i<4;i++) player.getPA().sendString(38011+i,(i==selected?"@or1@":"@whi@")+new String[]{"Hourly combat","Hourly skilling","Daily","Weekly"}[i]);
        player.getPA().sendString(38020,"Up to " + preference.name().toLowerCase());
        player.getPA().sendString(38021,t!=null&&t.isPinned()?"Unpin":"Pin tracker");
        player.getPA().sendString(38023,t!=null&&t.getClaimedReward()?"Claimed":"Claim reward");
        for(int i=0;i<48;i++) player.getPA().sendString(38040+i,i<lines.size()?lines.get(i):"");
        player.getPA().setScrollableMaxHeight(38030,Math.max(190,lines.size()*17+8));
    }
    private static String status(TaskMasterKills t) { return t.getClaimedReward()?"Claimed":t.complete()?"Ready to claim":"In progress"; }
    public void updateTracker() {
        TaskMasterKills t=taskMasterKillsList.stream().filter(TaskMasterKills::isPinned).findFirst().orElse(null);
        player.getPA().sendString(38095,t==null?"":clean(t.getDesc()));
        player.getPA().sendString(38096,t==null?"":t.getAmountKilled()+" / "+t.getAmountToKill()+" - "+status(t));
        player.getPA().sendString(38097,t==null?"":getTaskTime(t)+" | ::taskmanager");
    }
    public void trackActivity(Player p, TaskMasterKills t) {
        if (!taskMasterKillsList.contains(t) || t.getClaimedReward()) return;
        if (t.announceCompletion()) p.sendMessage("@gre@Task complete: " + clean(t.getDesc()) + ". Claim it with ::taskmanager.");
        if(t.isPinned()) updateTracker();
    }
    public static boolean matchesNpc(String objective,String npcName) {
        String task=clean(objective).toLowerCase(Locale.ROOT), npc=clean(npcName).toLowerCase(Locale.ROOT);
        // These activities have explicit completion hooks; ordinary NPC deaths must not count twice.
        if(Arrays.asList("nightmare","chambers","theatre of blood","inferno").contains(task)) return false;
        if(task.equals("barrows")) return Arrays.asList("ahrim","dharok","guthan","karil","torag","verac").stream().anyMatch(npc::startsWith);
        if(task.equals("dagannoth")) return Arrays.asList("dagannoth rex","dagannoth prime","dagannoth supreme").contains(npc);
        return !npc.isEmpty() && task.equals(npc);
    }
    public void recordNpcKill(String npcName) {
        for(TaskMasterKills t:taskMasterKillsList) if(t.getTaskType()==TaskType.COMBAT && !t.complete()
                && !t.getClaimedReward() && !t.expired(LocalDateTime.now()) && matchesNpc(t.getDesc(),npcName)) {
            t.incrementAmountKilled(1); trackActivity(player,t);
        }
    }
    /** Claim all rewards together or leave both progress and inventory unchanged. */
    public void finishTask(Player p, TaskMasterKills t) {
        if(!available() || !taskMasterKillsList.contains(t) || !t.complete() || t.getClaimedReward()) return;
        int[] oldIds=p.playerItems.clone(), oldAmounts=p.playerItemsN.clone();
        TaskClaim.Result result=TaskClaim.claim(t, () -> {
            for(GameItem item:t.getItems()) if(!p.getItems().addItem(item.getId(),item.getAmount(),false)) return false;
            return true;
        }, () -> restoreInventory(oldIds,oldAmounts), () -> PlayerSave.saveGameInstant(p));
        if(result==TaskClaim.Result.NO_SPACE) p.sendMessage("Make inventory space for all task rewards, then claim again.");
        else if(result==TaskClaim.Result.SAVE_FAILED) p.sendMessage("Your reward could not be saved. Please try again.");
        else if(result==TaskClaim.Result.CLAIMED) p.sendMessage("@gre@Task rewards claimed for " + clean(t.getDesc()) + ".");
        updateTracker();
    }
    private void restoreInventory(int[] ids,int[] amounts) {
        System.arraycopy(ids,0,player.playerItems,0,ids.length);
        System.arraycopy(amounts,0,player.playerItemsN,0,amounts.length);
        player.getItems().resetItems(3214);
    }
    public boolean resetWithScroll() {
        if(!available() || taskMasterKillsList.stream().anyMatch(t->t.complete()&&!t.getClaimedReward())) {
            player.sendMessage("Claim completed rewards and finish your current activity before resetting."); return false;
        }
        taskMasterKillsList.removeIf(t->!t.isWeeklyChallenge());
        refresh(LocalDateTime.now());
        player.sendMessage("Hourly and daily tasks reset. The weekly challenge is unchanged."); return true;
    }
    public static String timeRemaining(LocalDateTime end, LocalDateTime now) {
        long seconds=end==null?0:Math.max(0,Duration.between(now,end).getSeconds());
        return seconds/86400+"d "+seconds/3600%24+"h "+seconds/60%60+"m";
    }
    public String getTaskTime(TaskMasterKills t) { return t.complete()&&!t.getClaimedReward()?"Reward held until claimed":timeRemaining(t.getLocalDateTime(),LocalDateTime.now()); }
    public String getTime(Player p) { return timeRemaining(moneyMakingTime,LocalDateTime.now()); }
    public int calculatePercentage(int amount,int total) { return total<=0?0:(int)Math.min(100,Math.max(0,(long)amount*100/total)); }
    public LocalDateTime getMoneyMakingTime() { return moneyMakingTime; }
    public void setMoneyMakingTime(LocalDateTime value) { moneyMakingTime=value; }
    public boolean getEarn() { return earn; }
    public void setEarn(boolean value) { earn=value; }
    public void handleDailySkips() {
        if (player.amDonated < 1500) {
            return;
        }

        int amt = 1;

        if (player.amDonated >= 2000 && player.amDonated < 3000) {
            amt = 2;
        } else if (player.amDonated >= 3000) {
            amt = 3;
        }

        if (player.getItems().hasAnywhere(20238)) {
            return;
        }

        for (io.zaryx.model.items.bank.BankTab bankTab : player.getBank().getBankTab()) {
            if (bankTab.getItemAmount(new io.zaryx.model.items.bank.BankItem(20237)) > 0
                    || bankTab.contains(new io.zaryx.model.items.bank.BankItem(20237))
                    || bankTab.containsAmount(new io.zaryx.model.items.bank.BankItem(20237))) {
                return;
            }
        }

        player.getItems().addItemUnderAnyCircumstance(20238, amt);
    }

    private static class State { List<TaskMasterKills> tasks; TaskDifficulty difficulty; }
    public String encode() {
        State s=new State(); s.tasks=taskMasterKillsList; s.difficulty=preference;
        return Base64.getEncoder().encodeToString(JSON.toJson(s).getBytes(StandardCharsets.UTF_8));
    }
    public void decode(String data) {
        State s=JSON.fromJson(new String(Base64.getDecoder().decode(data),StandardCharsets.UTF_8),State.class);
        if(s==null || s.tasks==null) throw new IllegalArgumentException("Invalid activity board state");
        taskMasterKillsList=new ArrayList<>(s.tasks);
        preference=s.difficulty==null?TaskDifficulty.MEDIUM:s.difficulty;
        loadedFromPlayerSave=true;
    }
    public void loadAllMoneyMaking(Player p) {
        if(loadedFromPlayerSave) return;
        Path path=Paths.get("./save_files/taskmaster/",p.getLoginName()+".json");
        if(!Files.exists(path)) return;
        try {
            importLegacy(Files.readString(path));
        } catch(Exception e) { throw new IllegalStateException("Cannot load legacy tasks for "+p.getLoginName(),e); }
    }
    void importLegacy(String data) {
        if(loadedFromPlayerSave) return;
        JsonElement root=JsonParser.parseString(data);
        JsonArray array;
        if(root.isJsonArray()) array=root.getAsJsonArray();
        else { array=new JsonArray(); if(root.isJsonObject()) array.add(root); }
        List<TaskMasterKills> loaded=JSON.fromJson(array,new TypeToken<List<TaskMasterKills>>(){}.getType());
        if(loaded!=null) taskMasterKillsList=new ArrayList<>(loaded);
    }
    /** Legacy hook retained. The main player save now owns this data. */
    public void saveAllMoneyMaking(Player p) { }
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>,JsonDeserializer<LocalDateTime> {
        public JsonElement serialize(LocalDateTime date,java.lang.reflect.Type t,JsonSerializationContext c) { return new JsonPrimitive(date.toString()); }
        public LocalDateTime deserialize(JsonElement json,java.lang.reflect.Type t,JsonDeserializationContext c) { return json.isJsonNull()?null:LocalDateTime.parse(json.getAsString()); }
    }
}
