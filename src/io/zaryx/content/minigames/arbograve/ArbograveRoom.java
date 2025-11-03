package io.zaryx.content.minigames.arbograve;

import io.zaryx.content.instances.InstancedArea;
import io.zaryx.model.collisionmap.WorldObject;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;

public abstract class ArbograveRoom {

    public abstract ArbograveBoss spawn(InstancedArea instancedArea);

    public abstract Position getPlayerSpawnPosition();

    public abstract Boundary getBoundary();

    public abstract boolean handleClickObject(Player player, WorldObject worldObject, int option);

    public abstract void handleClickBossGate(Player player, WorldObject worldObject);

    public abstract Position getDeathPosition();

    public abstract Position getFightStartPosition();

    public abstract boolean isRoomComplete(InstancedArea instancedArea);

}
