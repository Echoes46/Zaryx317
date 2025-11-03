package io.zaryx.content.minigames.tob.rooms;

import io.zaryx.content.instances.InstancedArea;
import io.zaryx.content.minigames.tob.TobConstants;
import io.zaryx.content.minigames.tob.TobRoom;
import io.zaryx.content.minigames.tob.bosses.Nylocas;
import io.zaryx.model.collisionmap.WorldObject;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.world.objects.GlobalObject;

public class RoomThreeNylocas extends TobRoom {

    @Override
    public Nylocas spawn(InstancedArea instancedArea) {
        return new Nylocas(instancedArea);
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(3296, 4283);
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public void handleClickBossGate(Player player, WorldObject worldObject) {
        if (player.getY() >= 4256) {
            player.getPA().movePlayer(player.getX(), 4254, player.getHeight());
        } else {
            player.getPA().movePlayer(player.getX(), 4256, player.getHeight());
        }
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }

    @Override
    public Boundary getBoundary() {
        return TobConstants.NYLOCAS_BOSS_ROOM_BOUNDARY;
    }

    @Override
    public Position getDeathPosition() {
        return new Position(3295, 4256, 0);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(3295, 4254, 0);
    }

    @Override
    public GlobalObject getFoodChestPosition() {
        return getFoodChest(new Position(3303, 4274, 0), 3);
    }
}
