package io.zaryx.content.commands.owner;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;

public class NPCDumper extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        for (int i = 0; i < NPCHandler.npcs.length; i++) {
            NPC npc = NPCHandler.getNpc(i);
            if (npc != null) {
                if (Boundary.getWildernessLevel(npc.getX(), npc.getY()) > 0) {
                    for (GameItem allNPCdrop : Server.getDropManager().getAllNPCdrops(npc.getNpcId())) {
                        System.out.println("NPC ID : " + allNPCdrop.getId()+ ", " +allNPCdrop.getDef().getName() +", " + allNPCdrop.rarity);
                    }
                }
            }
        }
    }

}
