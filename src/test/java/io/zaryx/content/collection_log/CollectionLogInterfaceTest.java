package io.zaryx.content.collection_log;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerAssistant;
import io.zaryx.model.items.GameItem;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CollectionLogInterfaceTest {
    static class TestLog extends CollectionLog {
        @Override public List<GameItem> getRequiredItems(int id) {
            return List.of(new GameItem(100, 1), new GameItem(101, 1));
        }
        @Override public void saveToJSON() { }
    }
    static class TestPlayer extends Player {
        final TestLog log = new TestLog();
        final Map<Integer, String> text = new HashMap<>();
        final PlayerAssistant assistant = new PlayerAssistant(this) {
            @Override public void sendFrame126(String message, int id) { text.put(id, message); }
            @Override public void itemOnInterface(int item, int amount, int frame, int slot) { }
            @Override public void itemOnInterface(GameItem item, int frame, int slot) { }
            @Override public void showInterface(int id) { }
        };
        TestPlayer() { super(null); saveCharacter = false; }
        @Override public PlayerAssistant getPA() { return assistant == null ? super.getPA() : assistant; }
        @Override public CollectionLog getCollectionLog() { return log == null ? super.getCollectionLog() : log; }
    }

    @Test void listTitleAndProgressAgreeForExistingSavesAndOtherPlayers() throws Exception {
        Field configuration = Server.class.getDeclaredField("configuration"); configuration.setAccessible(true);
        Object oldConfiguration = configuration.get(null);
        var oldNpcs = CollectionLog.collectionNPCS;
        try {
            configuration.set(null, ServerConfiguration.getDefault());
            CollectionLog.collectionNPCS = new HashMap<>();
            CollectionLog.collectionNPCS.put(CollectionLog.CollectionTabType.OTHER, new ArrayList<>(List.of(6)));
            TestPlayer player = new TestPlayer();
            player.log.getCollections().put("6", new ArrayList<>(List.of(new GameItem(100, 1), new GameItem(101, 1), new GameItem(696, 5))));
            player.setViewingCollectionLog(player.log);
            player.log.selectTab(player, CollectionLog.CollectionTabType.OTHER);
            assertEquals("@gre@Weapon Upgrades", player.text.get(23123));
            assertEquals("@gre@Weapon Upgrades", player.text.get(23118));
            assertEquals("Obtained: @gre@2/2", player.text.get(23119));
            assertEquals(3, player.log.getUnlocked(6).size());

            // A previously claimed reward does not grant it twice.
            player.getClaimedLog().add(6);
            assertTrue(CollectionRewards.handleButton(player, 23236));
            assertEquals(1, player.getClaimedLog().size());

            player.log.getUnlocked(6).remove(1);
            player.log.selectTab(player, CollectionLog.CollectionTabType.OTHER);
            assertEquals("Weapon Upgrades", player.text.get(23123));
            assertEquals("Weapon Upgrades", player.text.get(23118));
            assertEquals("Obtained: @red@1/2", player.text.get(23119));
            player.getClaimedLog().clear();
            assertTrue(CollectionRewards.handleButton(player, 23236));
            assertTrue(player.getClaimedLog().isEmpty());

            TestLog otherLog = new TestLog();
            otherLog.getCollections().put("6", new ArrayList<>(List.of(new GameItem(100, 1), new GameItem(101, 1))));
            player.setViewingCollectionLog(otherLog);
            otherLog.selectTab(player, CollectionLog.CollectionTabType.OTHER);
            assertEquals("@gre@Weapon Upgrades", player.text.get(23123));
            assertEquals("Obtained: @gre@2/2", player.text.get(23119));
            assertTrue(CollectionRewards.handleButton(player, 23236));
            assertTrue(player.getClaimedLog().isEmpty());
        } finally {
            CollectionLog.collectionNPCS = oldNpcs;
            configuration.set(null, oldConfiguration);
        }
    }
}
