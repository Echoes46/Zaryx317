package io.zaryx.content.bosses;

import io.zaryx.content.bosses.relicguardian.ForestNPC;
import io.zaryx.content.instances.InstanceConfiguration;
import io.zaryx.content.instances.InstanceConfigurationBuilder;
import io.zaryx.content.instances.InstancedArea;
import io.zaryx.model.Npcs;
import io.zaryx.model.collisionmap.WorldObject;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;

public class ForestGuardian extends InstancedArea {


    public static final int KEY = 993;

    public ForestGuardian() {
        super(CONFIGURATION, Boundary.FORESTZONE);
    }



    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();


    public void enter(Player player) {
        try {
            if (player.getItems().playerHasItem(993) && player.getItems().playerHasItem(995, 5_000_000)) {
                player.sendMessage("@red@You crawl into the cave, Goodluck!");
                player.getItems().deleteItem(993, 1);
                player.getItems().deleteItem(995, 5000000);
                player.moveTo(new Position(2278, 5015, getHeight()));
                add(player);
                NPC npc = new ForestNPC(Npcs.FOREST_GUARDIAN, new Position(2263, 5017, getHeight()), player);
                add(npc);
                npc.attackEntity(player);
            } else {
                player.sendMessage("You need 5m and a key to the forest to enter");
            }
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

            case 12771://TODO
                /**
                 * Leave
                 */

                this.dispose();
                player.getPA().movePlayer(3114, 3499, 0);
                player.sendMessage("Cautiously, you return home");
                return true;
        }
        return false;
    }

    }

