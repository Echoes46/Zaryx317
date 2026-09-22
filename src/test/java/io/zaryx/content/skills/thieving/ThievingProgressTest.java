package io.zaryx.content.skills.thieving;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.content.achievement.Achievements;
import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThievingProgressTest {

    @Test
    void customStallsAdvanceTheThievingAchievement() throws Exception {
        Field configuration = Server.class.getDeclaredField("configuration");
        configuration.setAccessible(true);
        Object previous = configuration.get(null);
        configuration.set(null, ServerConfiguration.getDefault());
        try {
            Player player = new Player(null);

            player.getThieving().recordSuccessfulStallSteal();

            assertEquals(1, player.getAchievements()
                    .getAmountRemaining(Achievements.Achievement.Theiving_Task_I));
        } finally {
            configuration.set(null, previous);
        }
    }
}
