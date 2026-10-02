package io.zaryx.model.items;

import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.definitions.ItemDefinitionLoader;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CoalStackabilityTest {
    @Test void onlyNotedCoalStacks() throws Exception {
        ItemDef.load();
        ItemDefinitionLoader.init();
        assertFalse(ItemDef.forId(453).isStackable());
        assertTrue(ItemDef.forId(454).isStackable());
    }
}
