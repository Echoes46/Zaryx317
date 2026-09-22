package io.zaryx.content.skills.woodcutting;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.content.achievement.Achievements;
import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WoodcuttingProgressTest {

    @Test
    void everySuccessfulLogAdvancesTheWoodcuttingAchievement() throws Exception {
        Field configuration = Server.class.getDeclaredField("configuration");
        configuration.setAccessible(true);
        Object previous = configuration.get(null);
        configuration.set(null, ServerConfiguration.getDefault());
        try {
            Player player = new Player(null);
            WoodcuttingEvent event = new WoodcuttingEvent(player, Tree.NORMAL, Hatchet.BRONZE, 2092, 0, 0);

            event.recordSuccessfulChop();

            assertEquals(1, player.getAchievements()
                    .getAmountRemaining(Achievements.Achievement.Woodcutting_Task_I));
        } finally {
            configuration.set(null, previous);
        }
    }
}
