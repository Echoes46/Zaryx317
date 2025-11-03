package io.zaryx.model.entity.player.packets;

import io.zaryx.Server;
import io.zaryx.content.ItemSpawner;
import io.zaryx.content.skills.crafting.JewelryMaking;
import io.zaryx.content.skills.smithing.Smithing;
import io.zaryx.model.ContainerAction;
import io.zaryx.model.ContainerActionType;
import io.zaryx.model.entity.player.PacketType;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.group.GroupIronmanBank;
import io.zaryx.model.entity.player.mode.wildygroup.GroupWildyManBank;
import io.zaryx.model.items.GameItem;
import io.zaryx.model.multiplayersession.MultiplayerSession;
import io.zaryx.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.zaryx.model.multiplayersession.MultiplayerSessionStage;
import io.zaryx.model.multiplayersession.MultiplayerSessionType;
import io.zaryx.model.multiplayersession.duel.DuelSession;
import io.zaryx.model.multiplayersession.flowerpoker.FlowerPokerSession;
import io.zaryx.model.multiplayersession.trade.TradeSession;
import io.zaryx.util.logging.player.ReceivedPacketLog;

import java.util.Objects;

/**
 * Bank 10 Items
 **/
public class ContainerAction3 implements PacketType {

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
		int interfaceId = c.getInStream().readUnsignedWordBigEndian();
		int removeId = c.getInStream().readUnsignedWordA();
		int removeSlot = c.getInStream().readUnsignedWordA();

		Server.getLogging().write(new ReceivedPacketLog(c, packetType, "i=" + interfaceId + "/" + removeSlot + "/" + removeId));
		ContainerAction action = new ContainerAction(ContainerActionType.ACTION_3, interfaceId, removeId, removeSlot);

		if (c.debugMessage)
			c.sendMessage("ContainerAction3: interfaceid: "+interfaceId+", removeSlot: "+removeSlot+", removeID: " + removeId);

		
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (interfaceId == 48500) {
			c.getTradePost().handleInput(interfaceId, 3, removeId);
			return;
		}
		if (interfaceId == 26022) {
			c.getTradePost().handleInput(interfaceId, 3, removeSlot);
			return;
		}

		if (c.getLootingBag().isWithdrawInterfaceOpen() || c.getLootingBag().isDepositInterfaceOpen()) {
			if (c.getLootingBag().handleClickItem(removeId, 10)) {
				return;
			}
		}
		if (c.viewingRunePouch) {
			if (c.getRunePouch().handleClickItem(removeId, 10, interfaceId)) {
				return;
			}
		}
		if (interfaceId == 35150) {
			c.getUpgradeInterface().handleItemAction(removeSlot);
			return;
		}

		if (c.getBank().withdraw(interfaceId, removeId, 10)) {
			return;
		}

		switch (interfaceId) {
		case GroupIronmanBank.INTERFACE_ITEM_CONTAINER_ID:
			GroupIronmanBank.processContainerAction(c, action);
			GroupWildyManBank.processContainerAction(c, action);
			break;
			case ItemSpawner.CONTAINER_ID:
				ItemSpawner.spawn(c, removeId, -1);
				break;
/*		case 26022:
			c.start(new DialogueBuilder(c).option("Are you sure you want to buy this item?",
					new DialogueOption("Yes.", p -> {
						Listing.buyListing(p, removeSlot, 10);
						p.getPA().closeAllWindows();
					}),
					new DialogueOption("No.", p -> p.getPA().closeAllWindows())));
			break;*/
/*		case 48500: //Listing interface
			if(c.isListing) {
				int amount = 10;
				if(c.getItems().getItemAmount(removeId) < 10)
					amount = c.getItems().getItemAmount(removeId);
				Listing.openSelectedItem(c, removeId, amount, 0);
			}
		break;*/
		
		case 4233:
		case 4239:
		case 4245:
			JewelryMaking.mouldItem(c, removeId, 10);
			break;
		case 1119:
		case 1120:
		case 1121:
		case 1122:
		case 1123:
			case 2031:
			Smithing.readInput(c.playerLevel[Player.playerSmithing], Integer.toString(removeId), c, 27);
			break;
		case 1688:
			c.getPA().useOperate(removeId);
			break;
		case 3900:
			c.getShops().buyItem(removeId, removeSlot, 5);
			break;
		case 64016:
			c.getShops().buyItem(removeId, removeSlot, 5);
			break;

		case 3823:
			c.getShops().sellItem(removeId, removeSlot, 5);
			break;

		case 5064:
			if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot do this whilst trading.");
				return;
			}
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("You have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (c.inpakyak) {

				c.getPakYak().deposit(removeId, 10, true);
				return;
			}
			if (c.isBanking) {
				c.getItems().addToBank(removeId, 10, true);
			} else {
				GroupIronmanBank.processContainerAction(c, action);
				GroupWildyManBank.processContainerAction(c, action);
			}
			break;
			case 45349:
				c.getPakYak().withdraw(removeId,10);
				break;

		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof DuelSession || session instanceof FlowerPokerSession) {
				session.addItem(c, new GameItem(removeId, 10));
			}
			break;

		case 3415:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof FlowerPokerSession) {
				session.removeItem(c, removeSlot, new GameItem(removeId, 10));
			}
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(c, removeSlot, new GameItem(removeId, 10));
			}
			break;

		}
	}

}
