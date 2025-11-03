package io.zaryx.content.questing.MonkeyMadness;

import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Position;

public class MMDemon extends NPC {

    public MMDemon(Position position) {
        super(1443, position);
        getBehaviour().setAggressive(true);
    }
}
