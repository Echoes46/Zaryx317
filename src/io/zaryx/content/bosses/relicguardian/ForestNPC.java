package io.zaryx.content.bosses.relicguardian;

import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;

import java.util.List;

/**
 * @author Ynneh
 */
public class ForestNPC extends NPC {


    public ForestNPC(int npcId, Position position, Player spawnedBy) {
        super(npcId, position);
        this.spawnedBy = spawnedBy.getIndex();
    }


    @Override
    public boolean canBeAttacked(Entity entity) {
        if (this.spawnedBy != entity.getIndex()) {
            if (entity instanceof Player) {
                Player p = (Player) entity;
                if (p != null)
                    p.sendMessage(this.getName()+" isn't after you.");
            }
            return false;
        }
        return true;
    }

    public boolean isImmune(Player player) {
        List<NPC> npcs_in_area = player.getInstance().getNpcs();
        if (npcs_in_area == null)
            return false;
        return true;

    }

    @Override
    public boolean canBeDamaged(Entity entity) {
        if (entity instanceof NPC)
            return false;
        return true;
    }

    @Override
    public boolean isFreezable() {
        return false;
    }

}

