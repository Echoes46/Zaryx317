package io.zaryx.model.collisionmap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FireGiantFloorTest {
    @Test
    void clearsTerrainBlockWithoutRemovingObjectCollision() {
        Region region = new Region(new RegionProvider(), 6557, false);
        region.addClip(1632, 10058, 0, 0x200000 | 0x100);

        region.clearBlockedFloor(1632, 10058, 0);

        assertEquals(0x100, region.getClip(1632, 10058, 0));
    }
}
