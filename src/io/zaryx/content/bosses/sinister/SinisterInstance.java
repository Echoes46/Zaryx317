package io.zaryx.content.bosses.sinister;

import io.zaryx.content.bosses.obor.OborNPC;
import io.zaryx.content.instances.InstanceConfiguration;
import io.zaryx.content.instances.InstancedArea;
import io.zaryx.model.Npcs;
import io.zaryx.model.collisionmap.WorldObject;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;

/**
 * @author Ynneh
 */

public class SinisterInstance extends InstancedArea {

    public static final int KEY = 20754;

    public SinisterInstance() {
        super(InstanceConfiguration.CLOSE_ON_EMPTY, Boundary.OBOR_AREA);
    }

    public void begin(Player player) {
        player.sendMessage("You use your key to unlock the gate");
        player.getItems().deleteItem(KEY, 1);
        player.getPA().sendScreenFlash(1, 500);

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

            int tick;

            @Override
            public void execute(CycleEventContainer container) {

                if (player == null || player.isDead() || !player.isOnline()) {
                    container.stop();
                    return;
                }
                if (tick == 4) {
                    enter(player);
                    container.stop();
                }
                tick++;
            }

        }, 1);
    }

    public void enter(Player player) {
        try {
            Position pos = new Position(3092, 9815, getHeight());
            player.moveTo(pos);
            player.facePosition(pos.getX(), pos.getY() - 1);
            add(player);
            NPC npc = new OborNPC(Npcs.OBOR, new Position(3095, 9799, getHeight()), player);
            add(npc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject object, int option) {
        switch (object.getId()) {
            case 29489:
            case 29488:
                /**
                 * Leave
                 */
                player.moveTo(new Position(3095, 9832, 0));
                return true;

            case 29491://rocks down
                if (player.getInstance().getNpcs().size() > 0) {
                    NPC npc = player.getInstance().getNpcs().get(0);
                    npc.attackEntity(player);
                }

                climbRocks(player, object.getY() >= 9806);
                return true;
        }
        return false;
    }

    private void climbRocks(Player player, boolean enter) {

        int x = player.getX();

        int y = enter ? 9804 : 9807;

        player.setForceMovement(x, y, 0, 75, "NORTH", 1148);
        /**
         * Faces Object
         */
        player.facePosition(x, y);

        player.sendMessage("You climb "+(enter ? "into" : "out")+" of the pit.");

    }

    @Override
    public void onDispose() {
        getPlayers().stream().forEach(plr -> {
            remove(plr);
        });
    }
}
