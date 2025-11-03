package io.zaryx.content.commands.owner;

import io.zaryx.content.commands.Command;
import io.zaryx.model.definitions.NpcDef;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.npc.NPCSpawning;
import io.zaryx.model.entity.player.Player;

/**
 * Spawn a specific Npc.
 * 
 * @author Emiel
 *
 */
public class spawnNpc extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		int newNPC = Integer.parseInt(input);
		if (newNPC > 0) {
//			NPCHandler.despawn(newNPC, c.heightLevel);
			NPC npc = NPCSpawning.spawnNpc(newNPC, c.absX, c.absY, c.heightLevel, 1, 10);
			npc.getBehaviour().setRespawn(true);
/*			NPCHandler.newNPC(spawn.id, spawn.position.getX(), spawn.position.getY(), spawn.position.getHeight(), walkingTypeOrdinal, NpcMaxHit.getMaxHit(spawn.id));
			NPCSpawning.spawnNpc(c, newNPC, c.absX, c.absY, c.heightLevel, 0, 7, false, false);*/
			c.sendMessage("You spawn npc " + NpcDef.forId(newNPC).getName() + ", "+ newNPC);
		} else {
			c.sendMessage("No such NPC.");
		}
	}
}
