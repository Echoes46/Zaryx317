package io.zaryx.content.minigames.barrows;

import io.zaryx.model.entity.player.Position;

import java.util.HashMap;
import java.util.Map;

public class TunnelDoors {

    public static final Map<Integer, TunnelDoor> DOOR_MAP = new HashMap<>();

    static {
        /**
         * Collection of tunnel doors. Hashed by their location.
         */
        registerDoor(20684, Position.create(3541, 9712), Rotation.EAST);
        registerDoor(20703, Position.create(3541, 9711), Rotation.EAST);
        registerDoor(20684, Position.create(3545, 9711), Rotation.WEST);
        registerDoor(20703, Position.create(3545, 9712), Rotation.WEST);
        registerDoor(20686, Position.create(3558, 9712), Rotation.EAST);
        registerDoor(20705, Position.create(3558, 9711), Rotation.EAST);
        registerDoor(20686, Position.create(3562, 9711), Rotation.WEST);
        registerDoor(20705, Position.create(3562, 9712), Rotation.WEST);
        registerDoor(20687, Position.create(3569, 9705), Rotation.NORTH);
        registerDoor(20706, Position.create(3568, 9705), Rotation.NORTH);
        registerDoor(20706, Position.create(3569, 9701), Rotation.SOUTH);
        registerDoor(20687, Position.create(3568, 9701), Rotation.SOUTH);
        registerDoor(20693, Position.create(3569, 9688), Rotation.NORTH);
        registerDoor(20712, Position.create(3568, 9688), Rotation.NORTH);
        registerDoor(20693, Position.create(3568, 9684), Rotation.SOUTH);
        registerDoor(20712, Position.create(3569, 9684), Rotation.SOUTH);
        registerDoor(20695, Position.create(3562, 9677), Rotation.WEST);
        registerDoor(20714, Position.create(3558, 9677), Rotation.EAST);
        registerDoor(20695, Position.create(3558, 9678), Rotation.EAST);
        registerDoor(20714, Position.create(3562, 9678), Rotation.WEST);
        registerDoor(20694, Position.create(3541, 9678), Rotation.EAST);
        registerDoor(20713, Position.create(3541, 9677), Rotation.EAST);
        registerDoor(20694, Position.create(3545, 9677), Rotation.WEST);
        registerDoor(20713, Position.create(3545, 9678), Rotation.WEST);
        registerDoor(20691, Position.create(3535, 9688), Rotation.NORTH);
        registerDoor(20710, Position.create(3534, 9688), Rotation.NORTH);
        registerDoor(20691, Position.create(3534, 9684), Rotation.SOUTH);
        registerDoor(20710, Position.create(3535, 9684), Rotation.SOUTH);
        registerDoor(20683, Position.create(3534, 9701), Rotation.SOUTH);
        registerDoor(20702, Position.create(3535, 9701), Rotation.SOUTH);
        registerDoor(20683, Position.create(3535, 9705), Rotation.NORTH);
        registerDoor(20702, Position.create(3534, 9705), Rotation.NORTH);
        registerDoor(20685, Position.create(3552, 9705), Rotation.NORTH);
        registerDoor(20704, Position.create(3551, 9705), Rotation.NORTH);
        registerDoor(20685, Position.create(3551, 9701), Rotation.SOUTH);
        registerDoor(20704, Position.create(3552, 9701), Rotation.SOUTH);
        registerDoor(20690, Position.create(3558, 9695), Rotation.EAST);
        registerDoor(20709, Position.create(3558, 9694), Rotation.EAST);
        registerDoor(20690, Position.create(3562, 9694), Rotation.WEST);
        registerDoor(20709, Position.create(3562, 9695), Rotation.WEST);
        registerDoor(20689, Position.create(3545, 9694), Rotation.WEST);
        registerDoor(20708, Position.create(3541, 9694), Rotation.EAST);
        registerDoor(20689, Position.create(3541, 9695), Rotation.EAST);
        registerDoor(20708, Position.create(3545, 9695), Rotation.WEST);
        registerDoor(20692, Position.create(3552, 9688), Rotation.NORTH);
        registerDoor(20711, Position.create(3551, 9688), Rotation.NORTH);
        registerDoor(20692, Position.create(3551, 9684), Rotation.SOUTH);
        registerDoor(20711, Position.create(3552, 9684), Rotation.SOUTH);
        registerDoor(20688, Position.create(3575, 9678), Rotation.EAST);
        registerDoor(20707, Position.create(3575, 9677), Rotation.EAST);
        registerDoor(20688, Position.create(3575, 9712), Rotation.EAST);
        registerDoor(20707, Position.create(3575, 9711), Rotation.EAST);
        registerDoor(20696, Position.create(3535, 9671), Rotation.NORTH);
        registerDoor(20715, Position.create(3534, 9671), Rotation.NORTH);
        registerDoor(20696, Position.create(3569, 9671), Rotation.NORTH);
        registerDoor(20715, Position.create(3568, 9671), Rotation.NORTH);
        registerDoor(20682, Position.create(3528, 9677), Rotation.WEST);
        registerDoor(20701, Position.create(3528, 9678), Rotation.WEST);
        registerDoor(20682, Position.create(3528, 9711), Rotation.WEST);
        registerDoor(20701, Position.create(3528, 9712), Rotation.WEST);
        registerDoor(20681, Position.create(3534, 9718), Rotation.SOUTH);
        registerDoor(20700, Position.create(3535, 9718), Rotation.SOUTH);
        registerDoor(20681, Position.create(3568, 9718), Rotation.SOUTH);
        registerDoor(20700, Position.create(3569, 9718), Rotation.SOUTH);
    }

    public enum Rotation {
        NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST
    }

    private static void registerDoor(int doorID, Position position, Rotation rotation) {
        DOOR_MAP.put(position.getX() << 16 | position.getY(), TunnelDoor.create(doorID, position, rotation));
    }

    public static TunnelDoor getDoor(int x, int y) {
       // System.out.println("size: "+DOOR_MAP.size());
        return DOOR_MAP.get(x << 16 | y);
    }
    public static TunnelDoor getDoor( Position position) {
      //  DOOR_MAP.get(objectId)
        //return DOOR_MAP.get(objectId).
          return getDoor(position.getX(), position.getY());
    }
//    public static TunnelDoor getDoor(Position position) {
//        return DOOR_MAP.get
//      //  return getDoor(position.getX(), position.getY());
//    }
}