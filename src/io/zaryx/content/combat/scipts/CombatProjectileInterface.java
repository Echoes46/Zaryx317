package io.zaryx.content.combat.scipts;

import io.zaryx.content.bosses.hydra.CombatProjectile;
import io.zaryx.model.ProjectileBase;

/**
 * @author Glaba
 * @project Exiled-Server
 * @social Discord: Glabay
 * @since 2024-06-04
 */
public interface CombatProjectileInterface {

    default ProjectileBase transformProjectile(CombatProjectile combatProjectile) {
        return new ProjectileBase(
            combatProjectile.getGfx(),
            combatProjectile.getDelay(),
            combatProjectile.getSpeed(),
            combatProjectile.getStartHeight(),
            combatProjectile.getEndHeight(),
            combatProjectile.getAngle(),
            1
        );
    }
}
