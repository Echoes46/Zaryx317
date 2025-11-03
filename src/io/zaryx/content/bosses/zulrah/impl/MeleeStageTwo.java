package io.zaryx.content.bosses.zulrah.impl;

import io.zaryx.content.bosses.zulrah.Zulrah;
import io.zaryx.content.bosses.zulrah.ZulrahLocation;
import io.zaryx.content.bosses.zulrah.ZulrahStage;
import io.zaryx.model.CombatType;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.entity.player.Player;

public class MeleeStageTwo extends ZulrahStage {

	public MeleeStageTwo(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().isDead() || player == null || player.isDead
				|| zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		if (zulrah.getNpc().totalAttacks > 1 && zulrah.getNpc().attackTimer == 9 && zulrah.getStage() == 2) {
			player.getZulrahEvent().changeStage(3, CombatType.MAGE, ZulrahLocation.NORTH);
			zulrah.getNpc().totalAttacks = 0;
			zulrah.getNpc().setFacePlayer(true);
			container.stop();
			return;
		}
	}

}
