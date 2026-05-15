package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.player.Player;

/**
 * Removes the nearest NPC with the given ID.
 * Usage: ::enpcremove npcId
 * Without args: removes the nearest NPC to the player regardless of ID.
 */
public class Enpcremove extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        int targetId = -1;
        if (input != null && !input.trim().isEmpty()) {
            try {
                targetId = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                c.sendMessage("Usage: ::enpcremove [npcId]");
                return;
            }
        }

        NPC nearest = null;
        int nearestIdx = -1;
        int nearestDist = Integer.MAX_VALUE;

        for (int i = 0; i < NPCHandler.npcs.length; i++) {
            NPC npc = NPCHandler.npcs[i];
            if (npc == null) continue;
            if (targetId != -1 && npc.getNpcId() != targetId) continue;

            int dist = Math.abs(npc.absX - c.absX) + Math.abs(npc.absY - c.absY);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = npc;
                nearestIdx = i;
            }
        }

        if (nearest == null) {
            c.sendMessage(targetId == -1 ? "No NPCs found nearby." : "No NPC with ID " + targetId + " found.");
            return;
        }

        int removedId = nearest.getNpcId();
        int rx = nearest.absX, ry = nearest.absY;
        NPCHandler.npcs[nearestIdx] = null;

        c.sendMessage("@gre@Removed NPC " + removedId + " from (" + rx + ", " + ry + ")");
        System.out.println("[ENPCREMOVE] " + c.getLoginName() + " removed NPC " + removedId + " at " + rx + "," + ry);
    }
}
