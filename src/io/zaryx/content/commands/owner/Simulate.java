package io.zaryx.content.commands.owner;

import io.zaryx.Server;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Location3D;
import io.zaryx.util.Misc;

public class Simulate extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        try {
            String[] args = input.split("-");
            if (args.length != 2) {
                throw new IllegalArgumentException();
            }
            int npcID = Integer.parseInt(args[0]);
            int amount = Misc.stringToInt(args[1]);

            NPC npc = new NPC(npcID, c.getPosition());
            npc.spawnedBy = c.getIndex();
            Location3D location = new Location3D(c.getX(), c.getY(), c.getHeight());

            for (int i = 0; i < amount; i++) {
                Server.getDropManager().create(c, npc, location, 1, npcID);
                c.getNpcDeathTracker().add(npc.getName(), npc.getDefinition().getCombatLevel(), 1);
            }
            npc.appendDamage(c,9999, Hitmark.HIT);
            npc.unregister();
        } catch (Exception e) {
            c.sendMessage("Error. Correct syntax: ::simulate-npcid-amount");
        }
    }
}