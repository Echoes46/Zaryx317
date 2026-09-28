package io.zaryx.content.upgrade;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class UpgradeSettlementTest {
    private Field configField, eventsField, definitionsField;
    private Object oldConfig, oldEvents, oldDefinitions;
    private CycleEventHandler events;
    private Player player;
    private UpgradeInterface ui;

    @BeforeEach
    void setup() throws Exception {
        configField = field(Server.class, "configuration");
        eventsField = field(CycleEventHandler.class, "instance");
        definitionsField = field(ItemDef.class, "definitions");
        oldConfig = configField.get(null);
        oldEvents = eventsField.get(null);
        oldDefinitions = definitionsField.get(null);
        configField.set(null, ServerConfiguration.getDefault());
        events = new CycleEventHandler();
        eventsField.set(null, events);
        ItemDef.load();
        player = new Player(null);
        player.playerItems[0] = UpgradeMaterials.VOID_TOP.getRequired().getId() + 1;
        player.playerItemsN[0] = 1;
        player.foundryPoints = 1_000_000;
        ui = new UpgradeInterface(player) {
            @Override public double getBoost(double chance) { return 0; }
        };
        field(UpgradeInterface.class, "selectedUpgrade").set(ui, UpgradeMaterials.VOID_TOP);
    }

    @AfterEach
    void restore() throws Exception {
        configField.set(null, oldConfig);
        eventsField.set(null, oldEvents);
        definitionsField.set(null, oldDefinitions);
    }

    private static Field field(Class<?> owner, String name) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private void resolve() { for (int i = 0; i < 5; i++) events.process(); }

    @Test
    void failedUpgradeConsumesExactlyOneInputAndOneFeeOnTheGameThread() {
        ui.handleUpgrade(false);
        assertEquals(1_000_000, player.foundryPoints);
        assertEquals(1, player.getItems().getInventoryCount(8839));
        resolve();
        assertEquals(500_000, player.foundryPoints);
        assertEquals(0, player.getItems().getInventoryCount(8839));
        assertEquals(0, player.getItems().getInventoryCount(13072));
        resolve();
        assertEquals(500_000, player.foundryPoints);
    }

    @Test
    void disconnectBeforeSettlementDoesNotConsumeInputOrPoints() {
        ui.handleUpgrade(false);
        player.setDisconnected(true);
        resolve();
        assertEquals(1_000_000, player.foundryPoints);
        assertEquals(1, player.getItems().getInventoryCount(8839));
    }

    @Test
    void spendingPointsBeforeSettlementCannotCreateANegativeBalance() {
        ui.handleUpgrade(false);
        player.foundryPoints = 0;
        resolve();
        assertEquals(0, player.foundryPoints);
        assertEquals(1, player.getItems().getInventoryCount(8839));
    }
}
