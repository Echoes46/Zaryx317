package io.zaryx.content.perky;

import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Position;

public class AncientGuardian extends NPC {
    public AncientGuardian(int npcId, Position position) {
        super(npcId, position);
        this.getBehaviour().setRespawn(false);
        this.getBehaviour().setAggressive(true);
        this.getCombatDefinition().setAggressive(true);
    }


}
