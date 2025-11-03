package io.zaryx.content.bosses.bryophyta;

import io.zaryx.Server;
import io.zaryx.content.instances.InstanceConfiguration;
import io.zaryx.content.instances.InstanceConfigurationBuilder;
import io.zaryx.content.instances.InstancedArea;
import io.zaryx.model.Npcs;
import io.zaryx.model.collisionmap.WorldObject;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.world.objects.GlobalObject;

public class Bryophyta extends InstancedArea {

    public static final int KEY = 22_375;

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();

    public Bryophyta() {
        super(CONFIGURATION, Boundary.BRYOPHYTA_ROOM);
    }

    public void enter(Player player) {
        try {
            player.sendMessage("Your key fits the gate, causing it to swing open.");
            player.getItems().deleteItem(KEY, 1);
            player.moveTo(new Position(3214, 9938, getHeight()));
            add(player);
            NPC npc = new BryophytaNPC(Npcs.BRYOPHYTA, new Position(3220, 9934, getHeight()), player);
            add(npc);
            npc.attackEntity(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDispose() {

    }

    @Override
    public boolean handleClickObject(Player player, WorldObject object, int option) {
        switch (object.getId()) {
            case 32536:
                Server.getGlobalObjects().add(new GlobalObject(5582, object.getX(), object.getY(), getHeight(), object.getFace(), object.getType(), 200, object.getId()).setInstance(player.getInstance()));
                /**
                 * Bronze axe ting
                 */
                player.getItems().addItemUnderAnyCircumstance(1351, 1);
                return true;

            case 32535://TODO
                /**
                 * Leave
                 */

                this.dispose();
                player.getPA().movePlayer(3174, 9900, 0);
                player.sendMessage("Cautiously, you climb out of the damp cave.");
                return true;
        }
        return false;
    }
}