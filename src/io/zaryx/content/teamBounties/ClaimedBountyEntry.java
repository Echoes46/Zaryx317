package io.zaryx.content.teamBounties;

import java.time.LocalDateTime;

public class ClaimedBountyEntry {

    private String loginName;
    private String displayName;
    private int totalAmount;
    private int totalPlayersHunted;
    private LocalDateTime timestamp;

    // Default constructor required for Jackson deserialization
    public ClaimedBountyEntry() {
    }

    public ClaimedBountyEntry(String loginName, String displayName, int totalAmount, int totalPlayersHunted, LocalDateTime timestamp) {
        this.loginName = loginName;
        this.displayName = displayName;
        this.totalAmount = totalAmount;
        this.totalPlayersHunted = totalPlayersHunted;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalPlayersHunted() {
        return totalPlayersHunted;
    }

    public void setTotalPlayersHunted(int totalPlayersHunted) {
        this.totalPlayersHunted = totalPlayersHunted;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
