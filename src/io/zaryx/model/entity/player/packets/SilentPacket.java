package io.zaryx.model.entity.player.packets;

import io.zaryx.Server;
import io.zaryx.model.entity.player.PacketType;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.logging.player.ReceivedPacketLog;

/**
 * Slient Packet
 **/
public class SilentPacket implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		Server.getLogging().write(new ReceivedPacketLog(c, packetType, "data type: " + packetType + ", data length: " + packetSize));
	}
}
