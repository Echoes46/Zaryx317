package io.zaryx.content.commands.owner;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCHandler;
import io.zaryx.model.entity.player.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Moves an NPC/fishing spot from one position to another and updates spawn files.
 * Usage: ::enpcmove npcId newX newY [newZ]
 * Finds the nearest NPC with that ID and moves it to the new position.
 */
public class Enpcmove extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        String[] args = input.split(" ");
        if (args.length < 1) {
            c.sendMessage("Usage: ::enpcmove npcId [newX newY newZ]");
            return;
        }
        try {
            int npcId = Integer.parseInt(args[0]);
            int newX = args.length >= 3 ? Integer.parseInt(args[1]) : c.absX;
            int newY = args.length >= 3 ? Integer.parseInt(args[2]) : c.absY;
            int newZ = args.length >= 4 ? Integer.parseInt(args[3]) : c.heightLevel;

            // Find the nearest NPC with this ID
            NPC nearest = null;
            int nearestDist = Integer.MAX_VALUE;
            for (int i = 0; i < NPCHandler.npcs.length; i++) {
                NPC npc = NPCHandler.npcs[i];
                if (npc != null && npc.getNpcId() == npcId) {
                    int dist = Math.abs(npc.absX - c.absX) + Math.abs(npc.absY - c.absY);
                    if (dist < nearestDist) {
                        nearestDist = dist;
                        nearest = npc;
                    }
                }
            }

            if (nearest == null) {
                c.sendMessage("No NPC with ID " + npcId + " found nearby.");
                return;
            }

            int oldX = nearest.absX;
            int oldY = nearest.absY;

            // Teleport the NPC
            nearest.absX = newX;
            nearest.absY = newY;
            nearest.heightLevel = newZ;
            nearest.makeX = newX;
            nearest.makeY = newY;
            nearest.teleport(new io.zaryx.model.entity.player.Position(newX, newY, newZ));

            c.sendMessage("@gre@Moved NPC " + npcId + " from (" + oldX + "," + oldY + ") to (" + newX + "," + newY + "," + newZ + ")");
            c.sendMessage("@yel@Note: This is a live move. Use ::buildnpc to save permanently at new position.");
            System.out.println("[ENPCMOVE] " + c.getLoginName() + " moved NPC " + npcId + " from " + oldX + "," + oldY + " to " + newX + "," + newY);

        } catch (NumberFormatException e) {
            c.sendMessage("Invalid number. Usage: ::enpcmove npcId newX newY [newZ]");
        }
    }
}
