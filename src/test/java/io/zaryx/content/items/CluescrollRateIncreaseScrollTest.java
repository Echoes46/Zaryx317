package io.zaryx.content.items;

import io.zaryx.model.entity.player.ClientGameTimer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CluescrollRateIncreaseScrollTest {

    @Test
    void convertsServerTicksToAnAccurateScreenTimer() {
        assertEquals(1_800, CluescrollRateIncreaseScroll.remainingSeconds(3_000));
        assertEquals(1_800, CluescrollRateIncreaseScroll.remainingSeconds(2_999));
        assertEquals(1, CluescrollRateIncreaseScroll.remainingSeconds(1));
        assertEquals(0, CluescrollRateIncreaseScroll.remainingSeconds(0));
    }

    @Test
    void usesTheFasterCluesScrollAsItsTimerIcon() {
        assertEquals(24_460, ClientGameTimer.BONUS_CLUES.getTimerId());
    }
}
