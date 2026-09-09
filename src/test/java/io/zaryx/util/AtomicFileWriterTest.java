package io.zaryx.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class AtomicFileWriterTest {
    @TempDir Path directory;
    @Test void replacesCompleteSaveAndLeavesNoTemporaryFiles() throws Exception {
        Path file = directory.resolve("player.txt");
        Files.writeString(file, "old save");
        AtomicFileWriter.write(file, "complete new save".getBytes());
        assertEquals("complete new save", Files.readString(file));
        try (var files = Files.list(directory)) { assertEquals(1, files.count()); }
    }
    @Test void failedCommitPreservesExistingData() throws Exception {
        Path target = Files.createDirectory(directory.resolve("player.txt"));
        Path existing = target.resolve("preserved.txt");
        Files.writeString(existing, "original");
        assertThrows(java.io.IOException.class, () -> AtomicFileWriter.write(target, new byte[]{1}));
        assertEquals("original", Files.readString(existing));
        try (var files = Files.list(directory)) { assertEquals(1, files.count()); }
    }
}
