package io.zaryx.content.items;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IslandScrollsTest {
    @Test
    void scrollDurationsAddWithoutAnHourLimit() {
        long ticks = IslandScrolls.addMinutes(0, IslandScrolls.minutesFor(24365));
        ticks = IslandScrolls.addMinutes(ticks, IslandScrolls.minutesFor(24365));
        assertEquals(60, IslandScrolls.remainingMinutes(ticks));

        ticks = IslandScrolls.addMinutes(ticks, IslandScrolls.minutesFor(24366));
        ticks = IslandScrolls.addMinutes(ticks, IslandScrolls.minutesFor(24364));
        assertEquals(135, IslandScrolls.remainingMinutes(ticks));
    }

    @Test
    void partialMinutesRoundUpForTheClientTimer() {
        assertEquals(30, IslandScrolls.remainingMinutes(2999));
        assertEquals(1, IslandScrolls.remainingMinutes(1));
        assertEquals(0, IslandScrolls.remainingMinutes(0));
        assertEquals(0, IslandScrolls.minutesFor(24363));
    }
}
