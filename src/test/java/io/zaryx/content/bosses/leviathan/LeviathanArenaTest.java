package io.zaryx.content.bosses.leviathan;

import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeviathanArenaTest {

    @Test
    void entranceStairsSeparateSafeApproachFromCombatArena() {
        assertFalse(Boundary.isIn(new Position(2069, 6368, 0), Boundary.LEVIATHAN));
        assertFalse(Boundary.isIn(new Position(2070, 6368, 0), Boundary.LEVIATHAN));
        assertTrue(Boundary.isIn(new Position(2071, 6368, 0), Boundary.LEVIATHAN));
    }
}
