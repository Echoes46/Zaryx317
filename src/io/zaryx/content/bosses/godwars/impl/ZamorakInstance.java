package io.zaryx.content.bosses.godwars.impl;

import io.zaryx.content.instances.InstanceConfiguration;
import io.zaryx.content.instances.impl.LegacySoloPlayerInstance;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.npc.NPCSpawning;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;

public class ZamorakInstance extends LegacySoloPlayerInstance {

    public ZamorakInstance(Player player, Boundary boundary) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
    }

    public static void enter(Player player, ZamorakInstance instance) {
        try {
            instance.add(player);

            for (NPC npc : NPCHandler.npcs) {
                if (npc != null && Boundary.isIn(npc, Boundary.ZAMORAK_GODWARS) && npc.getHeight() == 2 && !instance.getNpcs().contains(npc) && npc.getHeight() == 2 && npc.getInstance() == null) {
                    int maxhit = new NPCHandler().getMaxHit(player, npc);
                    NPC zammy = NPCSpawning.spawnNpc(instance, npc.getNpcId(), npc.getX(), npc.getY(), instance.getHeight()+2, 1, maxhit);
                    instance.add(zammy);
                }
            }

            player.moveTo(new Position(2925, 5331, instance.getHeight()+2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
