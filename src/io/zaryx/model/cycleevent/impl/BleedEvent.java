package io.zaryx.model.cycleevent.impl;

import io.zaryx.Server;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.model.cycleevent.Event;
import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.Health;
import io.zaryx.model.entity.HealthStatus;


import java.util.Optional;

public class BleedEvent extends Event<Entity> {


    private int damage;

    public int getItems() {
        return 33806;
    }

    private final Optional<Entity> inflictor;

    public BleedEvent(Entity attachment, int damage, Optional<Entity> inflictor) {
        super("health_status", attachment, 1);
        this.damage = damage;
        this.inflictor = inflictor;
    }

    @Override
    public void execute() {
        if (attachment == null) {
            super.stop();
            return;
        }

        Health health = attachment.getHealth();

        if (health.isNotSusceptibleTo(HealthStatus.BLEED)) {
            super.stop();
            return;
        }

        if (attachment.getHealth().getCurrentHealth() <= 0) {
            super.stop();
            return;
        }

        attachment.appendDamage(null, damage, Hitmark.DAWNBRINGER);
        inflictor.ifPresent(inf -> attachment.addDamageTaken(inf, damage));

        damage += 5;

        if (damage > 20) {
            Server.getEventHandler().stop(attachment, "health_status");
            super.stop();

        }
    }
}

