package io.zaryx.content.minigames.barrows;

import ch.qos.logback.core.net.server.Client;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.world.objects.GlobalObject;

public class TunnelDoor extends GlobalObject {

    //	public GlobalObject(int id, int x, int y, int height, int face) {
    public TunnelDoor(int doorID, Position position, TunnelDoors.Rotation rotation) {
        super(doorID, position.getX(),position.getY(), 0, rotation.ordinal());
    }

    public static TunnelDoor create(int doorID, Position position, TunnelDoors.Rotation rotation) {
        return new TunnelDoor(doorID, position, rotation);
    }

    public void resetDoor(Client client) {
    }

//    public void resetDoor(Player client) {
//        GameObjectManager.placeLocalObject(client, this);
//    }
//
//    public void closeDoor(Player client, int closeID) {
//        GameObjectManager.placeLocalObject(client, new GameObject(closeID, getPosition(), 0, getRotation()));
//    }
}