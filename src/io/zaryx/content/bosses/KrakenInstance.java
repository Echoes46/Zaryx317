package io.zaryx.content.bosses;

import io.zaryx.content.instances.InstanceConfiguration;
import io.zaryx.content.instances.impl.LegacySoloPlayerInstance;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;

/**
 * 
 * @author Grant_ | www.rune-server.ee/members/grant_ | 12/5/19
 *
 */
public class KrakenInstance extends LegacySoloPlayerInstance {

	public KrakenInstance(Player player, Boundary boundary) {
		super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
	}

	@Override
	public void onDispose() { }
}
