package io.zaryx.content.minigames.barrows;


import io.zaryx.content.combat.Hitmark;
import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;

public class TunnelEvent extends Event<Player> {

	public TunnelEvent(String signature, Player attachment, int ticks) {
		super(signature, attachment, ticks);
	}

	@Override
	public void execute() {
		if (attachment == null) {
			super.stop();
			return;
		}
		if (!Boundary.isIn(attachment, Barrows.TUNNEL)) {
			if (attachment.getBarrows().isCompleted()) {
				attachment.getBarrows().initialize();//what resets barrows lol
			}
			stop();
			return;
		}
//		attachment.getBarrows().getActive().ifPresent(brother -> {
//			if (brother.getNPC() == null) {
//				stop();
//			} else {
//				NPC npc = brother.getNPC();
//				if (attachment.distanceToPoint(npc.absX, npc.absY) > 20) {
//					stop();
//				}
//			}
//		});
		if (attachment.getBarrows().isCompleted()) {
			attachment.getPA().shakeScreen(3, 3, 3, 3);
			if ((getElapsedTicks() + 1) % 10 == 0) {
				attachment.appendDamage(5, Hitmark.HIT);
			}
		} else if ((getElapsedTicks() + 1) % 30 == 0) {
			attachment.getBarrows().drainPrayer();
		}
	}

	@Override
	public void stop() {
		super.stop();
		if (attachment == null) {
			return;
		}
		attachment.getPA().sendFrame107();
	//	attachment.getBarrows().resetafterleaving();
//		attachment.getBarrows().getActive().ifPresent(brother -> {
//			brother.setActive(false);
//			NPC npc = brother.getNPC();
//			if (npc != null) {
//				NPCHandler.npcs[npc.getIndex()] = null;
//			}
//		});
	}

}
