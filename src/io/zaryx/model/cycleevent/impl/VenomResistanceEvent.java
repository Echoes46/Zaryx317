package io.zaryx.model.cycleevent.impl;

import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.Health;
import io.zaryx.model.entity.HealthStatus;

public class VenomResistanceEvent extends Event<Entity> {

	public VenomResistanceEvent(Entity attachment, int ticks) {
		super("venom_resistance_event", attachment, ticks);
	}

	@Override
	public void execute() {
		super.stop();
		if (attachment == null) {
			return;
		}
		Health health = attachment.getHealth();
		health.removeNonsusceptible(HealthStatus.VENOM);
	}

}
