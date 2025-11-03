package io.zaryx.model.entity.player.mode.wildygroup.log;

import io.zaryx.model.items.GameItem;

public class GwmWithdrawItemLog {

    private final String displayName;
    private final GameItem gameItem;

    public GwmWithdrawItemLog(String displayName, GameItem gameItem) {
        this.displayName = displayName;
        this.gameItem = gameItem;
    }

    @Override
    public String toString() {
        return getDisplayName() + " withdraw " + getGameItem().getFormattedString();
    }

    public GwmWithdrawItemLog() {
        displayName = null;
        gameItem = null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public GameItem getGameItem() {
        return gameItem;
    }
}
