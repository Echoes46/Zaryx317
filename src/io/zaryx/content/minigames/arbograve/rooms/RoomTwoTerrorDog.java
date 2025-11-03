package io.zaryx.content.minigames.arbograve.rooms;

import io.zaryx.Server;
import io.zaryx.content.instances.InstancedArea;
import io.zaryx.content.minigames.arbograve.ArbograveBoss;
import io.zaryx.content.minigames.arbograve.ArbograveConstants;
import io.zaryx.content.minigames.arbograve.ArbograveRoom;
import io.zaryx.content.minigames.arbograve.bosses.Leech;
import io.zaryx.content.minigames.arbograve.bosses.MutantTarn;
import io.zaryx.model.collisionmap.WorldObject;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.world.objects.GlobalObject;
import io.zaryx.util.Misc;

public class RoomTwoTerrorDog extends ArbograveRoom {
    @Override
    public ArbograveBoss spawn(InstancedArea instancedArea) {
        Server.getGlobalObjects().add(new GlobalObject(21299, 1718, 4272, instancedArea.getHeight(), 1, 10, -1, -1).setInstance(instancedArea));
        for (int i = 0; i < 8 + (instancedArea.getPlayers().size() * 2); i++) {
            new Leech(new Position(Misc.random(1709, 1717), Misc.random(4264, 4272)),instancedArea);
        }
        return new MutantTarn(instancedArea);
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(1681, 4243);
    }

    @Override
    public Boundary getBoundary() {
        return ArbograveConstants.ARBO_5th_ROOM;
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public void handleClickBossGate(Player player, WorldObject worldObject) {

    }

    @Override
    public Position getDeathPosition() {
        return new Position(1681, 4243);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(1681, 4243);
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }
}
