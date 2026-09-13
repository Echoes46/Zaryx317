package io.zaryx.content.achievement;
import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.definitions.NpcDef;
import io.zaryx.model.definitions.NpcStats;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NpcKillAchievementTest {
    @Test void reusedDragonStatsDoNotTurnImpsIntoDragons() throws Exception {
        java.lang.reflect.Field config = Server.class.getDeclaredField("configuration");
        config.setAccessible(true);
        Object previous = config.get(null);
        config.set(null, ServerConfiguration.getDefault());
        try {
            Player p = new Player(null);
            NpcStats template = NpcStats.builder().setName("Green dragon").setHitpoints(10).createNpcStats();
            NPC imp = new NPC(1, 7930, NpcDef.builder().name("Revenant imp").build(), template);
            p.getAchievements().kill(imp);
            assertEquals(0, p.getAchievements().getAmountRemaining(Achievements.Achievement.DRAGON_SLAYER_I));
            assertTrue(p.getAchievements().getAmountRemaining(Achievements.Achievement.PvMer_I) > 0);
            NPC dragon = new NPC(2, 264, NpcDef.builder().name("Green dragon").build(), template);
            p.getAchievements().kill(dragon);
            assertTrue(p.getAchievements().getAmountRemaining(Achievements.Achievement.DRAGON_SLAYER_I) > 0);
        } finally { config.set(null, previous); }
    }
}
