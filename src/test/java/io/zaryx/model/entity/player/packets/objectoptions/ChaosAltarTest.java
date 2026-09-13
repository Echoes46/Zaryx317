package io.zaryx.model.entity.player.packets.objectoptions;
import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChaosAltarTest {
    @Test void wildernessAltarRestoresDuringCombatWithoutReducingBoostedPrayer() throws Exception {
        java.lang.reflect.Field config = Server.class.getDeclaredField("configuration");
        config.setAccessible(true);
        Object previous = config.get(null);
        config.set(null, ServerConfiguration.getDefault());
        Class<io.zaryx.model.collisionmap.ObjectDef> objectClass = io.zaryx.model.collisionmap.ObjectDef.class;
        java.lang.reflect.Field indices = objectClass.getDeclaredField("streamIndices"); indices.setAccessible(true);
        java.lang.reflect.Field cache = objectClass.getDeclaredField("cache"); cache.setAccessible(true);
        java.lang.reflect.Field type = objectClass.getDeclaredField("type"); type.setAccessible(true);
        Object oldIndices = indices.get(null), oldCache = cache.get(null);
        try {
            java.lang.reflect.Constructor<io.zaryx.model.collisionmap.ObjectDef> constructor = objectClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            io.zaryx.model.collisionmap.ObjectDef[] fixtures = new io.zaryx.model.collisionmap.ObjectDef[20];
            for (int i = 0; i < fixtures.length; i++) { fixtures[i] = constructor.newInstance(); type.setInt(fixtures[i], i == 0 ? 411 : 26258); }
            indices.set(null, new int[30000]); cache.set(null, fixtures);
            Player p = new Player(null);
            p.absX = 2950; p.absY = 3821; p.heightLevel = 0; p.wildLevel = 38;
            p.playerXP[5] = 13034431;
            p.underAttackByPlayer = 1;
            p.underAttackByNpc = 1;
            for (int id : new int[]{411, 26258}) {
                p.playerLevel[5] = 10;
                ObjectOptionOne.handleOption(p, id, 2948, 3820);
                assertEquals(99, p.playerLevel[5]);
                p.playerLevel[5] = 110;
                ObjectOptionOne.handleOption(p, id, 2948, 3820);
                assertEquals(110, p.playerLevel[5]);
            }
        } finally { indices.set(null, oldIndices); cache.set(null, oldCache); config.set(null, previous); }
    }
}
