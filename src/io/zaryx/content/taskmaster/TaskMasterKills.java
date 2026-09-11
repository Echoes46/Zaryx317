package io.zaryx.content.taskmaster;

import io.zaryx.model.items.GameItem;

import java.time.LocalDateTime;

public class TaskMasterKills {

    private GameItem[] items;
    private int amountToKill;
    private boolean claimedReward;
    private int amountKilled;
    private TaskDifficulty taskDifficulty;
    private TaskType taskType;
    private boolean weekly;
    private LocalDateTime localDateTime;
    private String desc;
    private boolean weeklyChallenge;
    private boolean rewardsAssigned;
    private boolean pinned;
    private transient boolean completionAnnounced;

    public TaskMasterKills(int amountToKill, int amountKilled, GameItem[] items, TaskDifficulty taskDifficulty, TaskType taskType, boolean weekly, LocalDateTime localDateTime, String desc) {
        this.setGameItems(items);
        this.amountToKill = amountToKill;
        this.amountKilled = amountKilled;
        this.taskDifficulty = taskDifficulty;
        this.taskType = taskType;
        this.weekly = weekly;
        this.localDateTime = localDateTime;
        this.desc = desc;
        setClaimedReward(claimedReward);
    }

    public String getDesc() {
        return desc;
    }

    public GameItem[] getItems() {
        return items;
    }

    public void setGameItems(GameItem[] items) {
        this.items = items;
    }

    public boolean getClaimedReward() {
        return claimedReward;
    }

    public void setClaimedReward(boolean claimedReward) {
        this.claimedReward = claimedReward;
    }

    public int getAmountToKill() {
        return amountToKill;
    }

    public void setAmountToKill(int npcToKill) {
        this.amountToKill = npcToKill;
    }

    public int getAmountKilled() {
        return amountKilled;
    }

    public void setAmountKilled(int amountKilled) {
        this.amountKilled = amountKilled;
    }

    public void incrementAmountKilled(int amountKilled) {
        if (amountKilled <= 0 || claimedReward || localDateTime == null || !LocalDateTime.now().isBefore(localDateTime)) return;
        this.amountKilled = (int) Math.min(amountToKill, (long) this.amountKilled + amountKilled);
    }

    public boolean complete() { return amountToKill > 0 && amountKilled >= amountToKill; }
    public boolean announceCompletion() {
        if (!complete() || completionAnnounced) return false;
        completionAnnounced = true; return true;
    }
    public boolean isWeeklyChallenge() { return weeklyChallenge; }
    public void setWeeklyChallenge(boolean value) { weeklyChallenge = value; }
    public boolean rewardsAssigned() { return rewardsAssigned; }
    public void setRewardsAssigned(boolean value) { rewardsAssigned = value; }
    public boolean isPinned() { return pinned; }
    public void setPinned(boolean value) { pinned = value; }
    public int slot() { return weeklyChallenge ? 3 : weekly ? 2 : taskType == TaskType.SKILLING ? 1 : 0; }
    public boolean expired(LocalDateTime now) { return localDateTime == null || !now.isBefore(localDateTime); }

    public TaskDifficulty getTaskDifficulty() {
        return taskDifficulty;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public boolean getWeekly() {
        return weekly;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
