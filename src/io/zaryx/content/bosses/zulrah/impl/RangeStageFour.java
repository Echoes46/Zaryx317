package io.zaryx.content.bosses.zulrah.impl;

import java.util.Arrays;

import io.zaryx.content.bosses.zulrah.DangerousEntity;
import io.zaryx.content.bosses.zulrah.DangerousLocation;
import io.zaryx.content.bosses.zulrah.SpawnDangerousEntity;
import io.zaryx.content.bosses.zulrah.Zulrah;
import io.zaryx.content.bosses.zulrah.ZulrahLocation;
import io.zaryx.content.bosses.zulrah.ZulrahStage;
import io.zaryx.model.CombatType;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.player.Player;

public class RangeStageFour extends ZulrahStage {

	public RangeStageFour(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().isDead() || player == null || player.isDead
				|| zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		int ticks = container.getTotalTicks();
		if (ticks == 4) {
			zulrah.getNpc().setFacePlayer(false);
			CycleEventHandler.getSingleton().addEvent(player, new SpawnDangerousEntity(zulrah, player,
					Arrays.asList(DangerousLocation.EAST, DangerousLocation.SOUTH_EAST, DangerousLocation.SOUTH_WEST), DangerousEntity.TOXIC_SMOKE, 40), 1);
		} else if (ticks == 16) {
			CycleEventHandler.getSingleton().addEvent(player,
					new SpawnDangerousEntity(zulrah, player, Arrays.asList(DangerousLocation.SOUTH_EAST, DangerousLocation.SOUTH_WEST), DangerousEntity.MINION_NPC), 1);
		} else if (ticks == 26) {
			zulrah.getNpc().setFacePlayer(true);
			zulrah.changeStage(5, CombatType.MAGE, ZulrahLocation.SOUTH);
			zulrah.getNpc().totalAttacks = 0;
			container.stop();
		}
	}

}
