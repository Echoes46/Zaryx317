package io.zaryx.model.world.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalObjectsTest {

    @Test
    void removalPlaceholderDoesNotOccupyAnEmptyTile() {
        GlobalObjects objects = new GlobalObjects();
        objects.objects.add(new GlobalObject(-1, 3108, 3494, 0, 0, 10));

        assertFalse(objects.anyExists(3108, 3494, 0));

        objects.objects.add(new GlobalObject(5249, 3108, 3494, 0, 0, 10));
        assertTrue(objects.anyExists(3108, 3494, 0));
    }
}
