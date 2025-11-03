package io.zaryx.net;

import io.zaryx.model.entity.player.PacketType;
import io.zaryx.model.entity.player.Player;

public class WheelOfFortuneEndListener  implements PacketType {
    @Override
    public void processPacket(Player c, int packetType, int packetSize) {
        byte index = c.inStream.readSignedByte();
        if (index == 96) {
            return;
        }
        c.getWheelOfFortune().onFinish(index);
    }
}
