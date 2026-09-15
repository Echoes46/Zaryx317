package io.zaryx.model.entity.player;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class OwnerEconomyLockTest {
    @TempDir Path directory;
    @Test void rankAndTransferChecksUseLoginNotDisplayName() throws Exception {
        java.lang.reflect.Field config = io.zaryx.Server.class.getDeclaredField("configuration");
        config.setAccessible(true);
        Object previous = config.get(null);
        config.set(null, io.zaryx.ServerConfiguration.getDefault());
        try {
            Player locked = new Player(null);
            locked.setLoginName("ChaseBanker");
            locked.setDisplayName("Someone else");
            Player other = new Player(null);
            other.setLoginName("anotherplayer");
            other.setDisplayName("chasebanker");
            OwnerEconomyLock.applyRank(locked);
            assertEquals(3, locked.getRights().getPrimary().getValue());
            assertTrue(OwnerEconomyLock.isLocked(locked));
            assertFalse(OwnerEconomyLock.isLocked(other));
            assertFalse(io.zaryx.model.multiplayersession.trade.Trade.requestable(locked, other));
            assertFalse(io.zaryx.model.multiplayersession.trade.Trade.requestable(other, locked));
            assertTrue(OwnerEconomyLock.denyCommand(locked, "as anotherplayer item 995"));
            io.zaryx.model.items.GroundItem drop = new io.zaryx.model.items.GroundItem(995, 3000, 3000, 0, 100, 0, "chasebanker");
            assertFalse(drop.isViewable(other));
            assertTrue(drop.isOwner(locked));
        } finally { config.set(null, previous); }
    }
    @Test void matchesOnlyTheExactLoginRegardlessOfCase() {
        assertTrue(OwnerEconomyLock.isTarget("ChaseBanker"));
        assertFalse(OwnerEconomyLock.isTarget("chasebanker2"));
        assertFalse(OwnerEconomyLock.isTarget("khaos"));
        assertFalse(OwnerEconomyLock.isTarget(null));
    }
    @Test void configurationFailsClosedAndRequiresExplicitFalse() throws Exception {
        Path config = directory.resolve("lock.properties");
        assertTrue(OwnerEconomyLock.load(config));
        for (String value : new String[]{"", "true", "off", "typo"}) {
            Files.writeString(config, "chasebanker.locked=" + value);
            assertTrue(OwnerEconomyLock.load(config));
        }
        Files.writeString(config, "chasebanker.locked=false");
        assertFalse(OwnerEconomyLock.load(config));
        Files.writeString(config, "chasebanker.locked=true");
        assertTrue(OwnerEconomyLock.load(config));
    }
    @Test void rejectsSpawnTransferAndPrivilegeEscalationCommands() {
        for (String command : new String[]{"item 995 100", "giveall 995", "spawnitem 995", "as khaos item 995", "script foo", "addrights alt 3", "shop 1", "rottenpotato", "unknowncommand", "homeevil", "/item 995"})
            assertFalse(OwnerEconomyLock.safeCommand(command), command);
        assertFalse(OwnerEconomyLock.safeCommand(null));
        assertTrue(OwnerEconomyLock.safeCommand("HOME"));
        assertTrue(OwnerEconomyLock.safeCommand("pet guardian angel"));
    }
}
