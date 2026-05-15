package io.zaryx.model.entity.player.packets;

/**
 * @author Ryan / Lmctruck30
 */

import io.zaryx.Server;
import io.zaryx.content.items.UseItem;
import io.zaryx.model.collisionmap.WorldObject;
import io.zaryx.model.entity.player.PacketType;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.zaryx.model.multiplayersession.MultiplayerSessionStage;
import io.zaryx.model.multiplayersession.MultiplayerSessionType;
import io.zaryx.model.multiplayersession.duel.DuelSession;
import io.zaryx.model.tickable.impl.WalkToTickable;

import java.util.Objects;

public class ItemOnObject implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().isLocked() || c.getLock().cannotInteract(c))
			return;
		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		c.interruptActions();

		int a = c.getInStream().readUnsignedWord();
		int objectId = c.getInStream().readInteger();
		int objectY = c.getInStream().readSignedWordBigEndianA();
		int b = c.getInStream().readUnsignedWord();
		int objectX = c.getInStream().readSignedWordBigEndianA();
		int itemId = c.getInStream().readUnsignedWord();

		c.objectX = objectX;
		c.objectY = objectY;
		c.xInterfaceId = -1;
		c.getPA().stopSkilling();

		WorldObject object = ClickObject.getObject(c, objectId, objectX, objectY);

		if (object == null) {
			return;
		}

		if (c.getItems().getInventoryCount(itemId) < 1)
			return;

		if (!c.getItems().playerHasItem(itemId, 1)) {
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}

		Position size = object.getObjectSize();
		c.setTickable(new WalkToTickable(c, object.getPosition(), size.getX(), size.getY(), player1 -> {

			c.getFarming().handleItemOnObject(itemId, objectId, objectX, objectY);
            // Rotten Potato on Object — permanently removes the object
            if (itemId == 5733 && c.getRights().getPrimary().isManagement()) {
                Server.getGlobalObjects().remove(objectId, objectX, objectY, c.heightLevel);
                java.util.Arrays.stream(io.zaryx.model.entity.player.PlayerHandler.players).forEach(p -> {
                    if (p != null) p.getPA().object(-1, objectX, objectY, 0, 10, true);
                });
                // Remove from cfg file
                try {
                    String path = Server.getDataDirectory() + "/cfg/obj/global_objects.cfg";
                    java.io.File file = new java.io.File(path);
                    if (file.exists()) {
                        java.util.List<String> lines = new java.util.ArrayList<>();
                        boolean removed = false;
                        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (!line.trim().isEmpty() && !line.trim().startsWith("//")) {
                                    String[] parts = line.trim().split("\t");
                                    if (parts.length >= 4) {
                                        try {
                                            if (Integer.parseInt(parts[0]) == objectId && Integer.parseInt(parts[1]) == objectX
                                                    && Integer.parseInt(parts[2]) == objectY && Integer.parseInt(parts[3]) == c.heightLevel) {
                                                removed = true;
                                                continue;
                                            }
                                        } catch (NumberFormatException ignored) {}
                                    }
                                }
                                lines.add(line);
                            }
                        }
                        if (removed) {
                            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(file))) {
                                for (int i = 0; i < lines.size(); i++) {
                                    writer.write(lines.get(i));
                                    if (i < lines.size() - 1) writer.newLine();
                                }
                            }
                        }
                        c.sendMessage(removed ? "@gre@Object " + objectId + " permanently removed!" : "@yel@Object " + objectId + " removed visually");
                    }
                } catch (Exception e) {
                    c.sendMessage("Error: " + e.getMessage());
                }
                return;
            }

			switch (c.objectId) {

				case 40949:
				case 2030: //Allows for items to be used from both sides of the furnace
					c.objectDistance = 4;
					c.objectXOffset = 3;
					c.objectYOffset = 3;
					break;
				case 26782:
					c.objectDistance = 7;
					break;
				case 28562:
				case 33320:
					c.objectDistance = 5;
					break;
				case 33311:
					c.objectDistance = 3;//hespori
					break;
				case 18818:
				case 40877:
				case 409:
				case 34855:
				case 4008:
					c.objectDistance = 3;
					break;
				case 884:
					c.objectDistance = 5;
					c.objectXOffset = 3;
					c.objectYOffset = 3;
					break;

				case 28900:
					c.objectDistance = 3;
					break;


				default:
					c.objectDistance = 1;
					c.objectXOffset = 0;
					c.objectYOffset = 0;
					break;

			}

			c.facePosition(objectX, objectY);
			UseItem.ItemonObject(c, objectId, objectX, objectY, itemId);
		}));
	}

}
