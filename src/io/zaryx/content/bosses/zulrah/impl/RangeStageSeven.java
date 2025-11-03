package io.zaryx.content.bosses.zulrah.impl;

import io.zaryx.content.bosses.zulrah.Zulrah;
import io.zaryx.content.bosses.zulrah.ZulrahLocation;
import io.zaryx.content.bosses.zulrah.ZulrahStage;
import io.zaryx.model.CombatType;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.entity.player.Player;

public class RangeStageSeven extends ZulrahStage {

	public RangeStageSeven(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().isDead() || player == null || player.isDead
				|| zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		if (zulrah.getNpc().totalAttacks > 5) {
			player.getZulrahEvent().changeStage(8, CombatType.MAGE, ZulrahLocation.SOUTH);
			zulrah.getNpc().totalAttacks = 0;
			container.stop();
			return;
		}
	}

}
