package io.zaryx.content.bosses.nightmare;

import com.google.common.base.Preconditions;
import io.zaryx.content.instances.InstancedArea;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Position;

public class NightmareStatusNPC extends NPC {

    public static NightmareStatusNPC npc;

    public static void init() {
        npc = new NightmareStatusNPC(NightmareStatus.IDLE.getNpcId(), NightmareConstants.STATUS_NPC_SPAWN_POSITION);
    }

    public static void update(InstancedArea instancedArea, Nightmare nightmare) {
        Preconditions.checkState(npc != null, "Status npc is null!");
        if (instancedArea == null) {
            npc.setStatus(NightmareStatus.IDLE);
        } else {
            Preconditions.checkState(nightmare != null, "Nightmare is null!");
            if (nightmare.isJoinable()) {
                npc.setStatus(NightmareStatus.STARTING);
            } else {
                npc.setStatus(nightmare.getPhase().getStatus());
            }
        }
    }

    private void setStatus(NightmareStatus status) {
        npc.requestTransform(status.getNpcId());
    }

    public NightmareStatusNPC(int npcID, Position position) {
        super(npcID, position);
    }

}
