package io.zaryx.model.entity.player.mode;

import io.zaryx.model.entity.player.mode.group.ExpModeType;

public class ExpMode {

    protected final ExpModeType type;

    public ExpMode(ExpModeType type) {
        this.type = type;
    }

    public ExpModeType getType() {
        return type;
    }

}
