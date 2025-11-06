package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.definitions.NpcDef;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCSpawning;
import io.zaryx.model.entity.player.Player;

public class spawnNpc extends Command {

    /* Rewrote by Khaos */
    @Override
    public void execute(Player c, String commandName, String input) {
        if (input == null || input.trim().isEmpty()) {
            c.sendMessage("Usage: ::spawnnpc <npc id>");
            return;
        }
        String[] parts = input.trim().split("\\s+");
        int newNPC;
        try {
            newNPC = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            c.sendMessage("Invalid NPC id: " + parts[0]);
            return;
        }
        if (newNPC <= 0) {
            c.sendMessage("No such NPC id.");
            return;
        }
        NpcDef def = NpcDef.forId(newNPC);
        if (def == null) {
            c.sendMessage("No NpcDef found for id " + newNPC + ".");
            return;
        }
        NPC npc = NPCSpawning.spawnNpc(newNPC, c.absX, c.absY, c.heightLevel, 1, 10);
        if (npc == null) {
            c.sendMessage("Failed to spawn NPC " + newNPC + ". Check NPCSpawning.");
            return;
        }
        npc.getBehaviour().setRespawn(true);
        c.sendMessage("You spawn npc " + def.getName() + " (" + newNPC + ").");
    }
}
