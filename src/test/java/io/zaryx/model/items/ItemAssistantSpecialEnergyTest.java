package io.zaryx.model.items;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemAssistantSpecialEnergyTest {

    @Test
    void convertsInternalEnergyToAValidWholePercentage() {
        assertEquals(100, ItemAssistant.specialEnergyPercent(10.0));
        assertEquals(75, ItemAssistant.specialEnergyPercent(7.5));
        assertEquals(68, ItemAssistant.specialEnergyPercent(6.75));
        assertEquals(33, ItemAssistant.specialEnergyPercent(3.25));
        assertEquals(0, ItemAssistant.specialEnergyPercent(-1.0));
        assertEquals(100, ItemAssistant.specialEnergyPercent(11.0));
    }
}
