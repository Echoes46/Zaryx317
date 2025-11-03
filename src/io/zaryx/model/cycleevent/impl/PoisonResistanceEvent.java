package io.zaryx.model.cycleevent.impl;

import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.Health;
import io.zaryx.model.entity.HealthStatus;

public class PoisonResistanceEvent extends Event<Entity> {

	public PoisonResistanceEvent(Entity attachment, int ticks) {
		super("poison_resistance_event", attachment, ticks);
	}

	@Override
	public void execute() {
		if (attachment == null) {
			super.stop();
			return;
		}
		super.stop();

		Health health = attachment.getHealth();
		health.removeNonsusceptible(HealthStatus.POISON);
	}

}
