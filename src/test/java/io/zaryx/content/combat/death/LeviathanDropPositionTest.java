package io.zaryx.content.combat.death;

import io.zaryx.model.Npcs;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Position;
import io.zaryx.util.Location3D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeviathanDropPositionTest {

    @Test
    void lootDropsOneTileOutsideTheBlockedFootprint() {
        Location3D drop = NPCDeath.adjustBossDropPosition(Npcs.LEVIATHAN, 2077, 6368, 0);

        assertEquals(2076, drop.getX());
        assertEquals(6368, drop.getY());
        assertEquals(0, drop.getZ());
        assertTrue(Boundary.isIn(new Position(drop.getX(), drop.getY(), drop.getZ()), Boundary.LEVIATHAN));
        assertFalse(Boundary.isIn(new Position(drop.getX(), drop.getY(), drop.getZ()), Boundary.LEVIATHANCLOSE));
    }

    @Test
    void otherNpcDropsRemainOnTheirOriginalTile() {
        Location3D drop = NPCDeath.adjustBossDropPosition(1, 3200, 3201, 2);

        assertEquals(3200, drop.getX());
        assertEquals(3201, drop.getY());
        assertEquals(2, drop.getZ());
    }
}
