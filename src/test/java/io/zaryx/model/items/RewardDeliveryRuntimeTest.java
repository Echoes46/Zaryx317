package io.zaryx.model.items;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.content.CollectionBox;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.mode.Mode;
import io.zaryx.model.entity.player.mode.ModeType;
import io.zaryx.model.items.bank.BankItem;
import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class RewardDeliveryRuntimeTest {
    private Field definitions, configuration;
    private Object oldDefinitions, oldConfiguration;
    private Player player;
    private io.zaryx.model.definitions.ItemDefinitionLoader[] oldCache;

    @BeforeEach void setup() throws Exception {
        definitions = ItemDef.class.getDeclaredField("definitions"); definitions.setAccessible(true);
        oldDefinitions = definitions.get(null);
        Map<Integer, ItemDef> items = new HashMap<>();
        items.put(995, ItemDef.builder().id(995).name("Coins").stackable(true).build());
        items.put(100, ItemDef.builder().id(100).name("Reward").build());
        items.put(101, ItemDef.builder().id(101).name("Noted reward").stackable(true).noted(true).noteId(100).build());
        definitions.set(null, items);
        oldCache = io.zaryx.model.definitions.ItemDefinitionLoader.cache;
        io.zaryx.model.definitions.ItemDefinitionLoader.cache = new io.zaryx.model.definitions.ItemDefinitionLoader[10];
        for (int i = 0; i < 10; i++) {
            io.zaryx.model.definitions.ItemDefinitionLoader.cache[i] = new io.zaryx.model.definitions.ItemDefinitionLoader();
            io.zaryx.model.definitions.ItemDefinitionLoader.cache[i].id = 100;
        }
        configuration = Server.class.getDeclaredField("configuration"); configuration.setAccessible(true);
        oldConfiguration = configuration.get(null); configuration.set(null, ServerConfiguration.getDefault());
        player = new Player(null);
        player.saveCharacter = false;
    }

    @AfterEach void teardown() throws Exception {
        definitions.set(null, oldDefinitions);
        io.zaryx.model.definitions.ItemDefinitionLoader.cache = oldCache;
        configuration.set(null, oldConfiguration);
    }

    private void fullInventory() {
        Arrays.fill(player.playerItems, 101);
        Arrays.fill(player.playerItemsN, 1);
    }

    private void fullBank() {
        for (int i = 0; i < player.getBank().getBankCapacity(); i++)
            player.getBank().getBankTab(0).add(new BankItem(10000 + i, 1));
    }

    @Test void inventoryIsPreferredAndWholeBundleFallsBackToBank() {
        assertEquals(RewardDelivery.Destination.INVENTORY, RewardDelivery.deliver(player, List.of(new GameItem(995, 20))));
        assertEquals(20, player.getItems().getItemAmount(995));
        fullInventory();
        assertEquals(RewardDelivery.Destination.BANK, RewardDelivery.deliver(player, List.of(new GameItem(995, 30), new GameItem(100, 3))));
        assertEquals(30, player.getBank().getBankTab(0).getItemAmount(new BankItem(996)));
        assertEquals(3, player.getBank().getBankTab(0).getItemAmount(new BankItem(101)));
    }

    @Test void bothFullLeavesEverythingUnchanged() {
        fullInventory(); fullBank();
        assertEquals(RewardDelivery.Destination.NO_SPACE, RewardDelivery.deliver(player, List.of(new GameItem(995, 30))));
        assertEquals(player.getBank().getBankCapacity(), player.getBank().getItemCount());
        assertEquals(0, player.getItems().getItemAmount(995));
    }

    @Test void notedRewardUsesExistingPlaceholderInAnotherTabEvenWhenBankFull() {
        fullInventory(); fullBank();
        player.getBank().getBankTab(0).getItems().remove(0);
        player.getBank().getBankTab(3).add(new BankItem(101, 0));
        assertEquals(RewardDelivery.Destination.BANK, RewardDelivery.deliver(player, List.of(new GameItem(101, 5))));
        assertEquals(5, player.getBank().getBankTab(3).getItemAmount(new BankItem(101)));
        assertEquals(player.getBank().getBankCapacity(), player.getBank().getItemCount());
    }

    @Test void rollbackRestoresInventoryAndBankPlaceholders() {
        fullInventory();
        player.getBank().getBankTab(2).add(new BankItem(101, 0));
        Runnable rollback = RewardDelivery.rollback(player);
        RewardDelivery.deliver(player, List.of(new GameItem(100, 5), new GameItem(995, 8)));
        rollback.run();
        assertEquals(1, player.getBank().getItemCount());
        assertEquals(0, player.getBank().getBankTab(2).getItemAmount(new BankItem(101)));
        assertEquals(28, player.getItems().getItemAmount(100));
    }

    @Test void ultimateIronmanCannotUseBankFallback() {
        player.setMode(Mode.forType(ModeType.ULTIMATE_IRON_MAN));
        fullInventory();
        assertEquals(RewardDelivery.Destination.NO_SPACE, RewardDelivery.deliver(player, List.of(new GameItem(995, 1))));
        assertEquals(0, player.getBank().getItemCount());
    }

    @Test void automaticRewardsAreRetainedAndRoundTripThroughPlayerSave() {
        fullInventory(); fullBank();
        player.getItems().addItemUnderAnyCircumstance(995, 10);
        CollectionBox.CollectionBoxSave save = new CollectionBox.CollectionBoxSave();
        assertEquals("995:10", save.encode(player, "collection_box"));
        // More than the old 128-slot limit must survive without silently losing items.
        for (int i = 0; i < 130; i++) player.getCollectionBox().add(player, new GameItem(100, 50));
        String encoded = save.encode(player, "collection_box");
        save.decode(player, "collection_box", encoded);
        assertEquals(encoded, save.encode(player, "collection_box"));
    }
}
