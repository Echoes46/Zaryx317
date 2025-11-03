package io.zaryx.content.bosses.relicguardian;

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

public class RelicGuardian extends InstancedArea {

    public static final int KEY = 33136;

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();

    public RelicGuardian() {
        super(CONFIGURATION, Boundary.RELIC_GUARDIAN);
    }

    public void enter(Player player) {
        try {
            player.sendMessage("You submit to the force of the portal, you are drawn in.");
            player.getItems().deleteItem(KEY, 1);
            player.moveTo(new Position(3021, 5788, getHeight()));
            add(player);
           NPC npc = new RelicNPC(Npcs.RELICGUARDIAN, new Position(3022, 5785, getHeight()), player);
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