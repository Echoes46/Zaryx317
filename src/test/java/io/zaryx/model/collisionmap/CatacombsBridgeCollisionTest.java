package io.zaryx.model.collisionmap;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CatacombsBridgeCollisionTest {

    @Test
    void stuckUpperFloorTileUsesTheBridgeInsteadOfBlockedGroundBeneath() throws Exception {
        int[][][] flags = readLandscape("etc/mapdata/index4/5472.gz"); // Region 6556
        int x = 1653 - 1600;
        int y = 10035 - 9984;
        assertEquals(1, flags[0][x][y]); // The ground underneath is blocked.
        assertEquals(2, flags[1][x][y]); // The walkable floor is a plane-one bridge.
        assertEquals(-1, Region.collisionPlane(0, flags[1][x][y]));
        assertEquals(0, Region.collisionPlane(1, flags[1][x][y]));

        // A non-bridge wall still blocks on plane zero.
        assertEquals(1, flags[0][0][0]);
        assertEquals(0, Region.collisionPlane(0, flags[1][0][0]));
    }

    @Test
    void fireGiantTilesAcrossTheNextRegionUseTheSameBridgeRule() throws Exception {
        int[][][] flags = readLandscape("etc/mapdata/index4/6264.gz"); // Region 6557
        int x = 1631 - 1600;
        int y = 10067 - 10048;
        assertEquals(1, flags[0][x][y]);
        assertEquals(2, flags[1][x][y]);
        assertEquals(-1, Region.collisionPlane(0, flags[1][x][y]));
        assertEquals(0, Region.collisionPlane(1, flags[1][x][y]));
    }

    private static int[][][] readLandscape(String path) throws Exception {
        ByteStream stream = new ByteStream(Region.getBuffer(new File(path)));
        int[][][] flags = new int[4][64][64];
        for (int plane = 0; plane < 4; plane++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    while (true) {
                        int value = stream.getUShort();
                        if (value == 0) break;
                        if (value == 1) {
                            stream.skip(1);
                            break;
                        }
                        if (value <= 49) stream.skip(2);
                        else if (value <= 81) flags[plane][x][y] = value - 49;
                    }
                }
            }
        }
        return flags;
    }
}
