package io.zaryx.model.entity.player.save;

import io.zaryx.util.PasswordHashing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class PlayerSnapshotTest {
    @TempDir Path directory;
    private PlayerSave.Snapshot snapshot(Path path, long pending) throws Exception {
        var constructor = PlayerSave.Snapshot.class.getDeclaredConstructor(Path.class, String.class, String.class);
        constructor.setAccessible(true);
        return constructor.newInstance(path, "character-password = [SNAPSHOT_PASSWORD]\nbj-pending-payout = "
                + pending + "\n[EOF]\n", "test-password");
    }
    @Test void lateOldSaveCannotOverwriteNewPayoutBalance() throws Exception {
        Path target = directory.resolve("player.txt");
        PlayerSave.Snapshot old = snapshot(target, 100);
        PlayerSave.Snapshot current = snapshot(target, 2_500_000_000L);
        assertTrue(PlayerSave.writeSnapshot(current));
        assertTrue(PlayerSave.writeSnapshot(old));
        String data = Files.readString(target);
        assertTrue(data.contains("bj-pending-payout = 2500000000"));
        assertFalse(data.contains("test-password"));
        assertFalse(data.contains("[SNAPSHOT_PASSWORD]"));
        String hash = data.substring("character-password = ".length(), data.indexOf('\n'));
        assertTrue(PasswordHashing.check("test-password", hash));
    }
}
