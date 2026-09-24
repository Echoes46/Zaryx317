package io.zaryx.content.items;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Known unavailable client assets; deliberately affects recommendations only. */
final class TopGearModels {
    private static final Set<Integer> UNAVAILABLE = load();

    private TopGearModels() { }

    static boolean available(int id) { return !UNAVAILABLE.contains(id); }

    private static Set<Integer> load() {
        InputStream source = TopGearModels.class.getResourceAsStream("/topgear-missing-models.txt");
        if (source == null) throw new IllegalStateException("Missing topgear model audit resource");
        Set<Integer> ids = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(source, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String value = line.replace("\uFEFF", "").split("#", 2)[0].trim();
                if (!value.isEmpty()) ids.add(Integer.parseInt(value));
            }
        } catch (IOException | NumberFormatException e) {
            throw new IllegalStateException("Invalid topgear model audit resource", e);
        }
        return Collections.unmodifiableSet(ids);
    }
}
