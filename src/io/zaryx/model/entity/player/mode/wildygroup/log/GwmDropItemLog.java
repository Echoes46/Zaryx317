package io.zaryx.model.entity.player.mode.wildygroup.log;

import io.zaryx.model.entity.player.Position;
import io.zaryx.model.items.GameItem;

public class GwmDropItemLog {

    private final String displayName;
    private final GameItem gameItem;
    private final Position position;

    public GwmDropItemLog(String displayName, GameItem gameItem, Position position) {
        this.displayName = displayName;
        this.gameItem = gameItem;
        this.position = position;
    }

    private GwmDropItemLog() {
        displayName = null;
        gameItem = null;
        position = null;
    }

    @Override
    public String toString() {
        return getDisplayName() + " dropped " + getGameItem().getFormattedString() + " at " + getPosition().getFormattedString();
    }

    public String getDisplayName() {
        return displayName;
    }

    public GameItem getGameItem() {
        return gameItem;
    }

    public Position getPosition() {
        return position;
    }
}
