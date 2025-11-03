package io.zaryx.content.teamBounties;

import java.time.LocalDateTime;

public class BountyEntry {

    private String loginName;
    private String displayName;
    private int amount;
    private LocalDateTime timestamp;

    // Default constructor required for Jackson deserialization
    public BountyEntry() {
    }

    public BountyEntry(String loginName, String displayName, int amount, LocalDateTime timestamp) {
        this.loginName = loginName;
        this.displayName = displayName;
        this.amount = amount;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
