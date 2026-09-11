package io.zaryx.model.entity.npc.pets;

import java.util.*;
import java.util.stream.Collectors;

/** Account-owned progress per companion family; swapping item instances cannot reset or transfer it. */
public final class CompanionProgress {
    private static final long[] THRESHOLDS = {0, 500, 1500, 3500, 6500, 11000, 17000, 25000, 35000, 50000};
    public static final long MAX_XP = 50000;
    private final Map<Integer, Long> experience = new TreeMap<>();
    private long nextAwardNanos;
    private boolean awarded;
    public long xp(int id) { return experience.getOrDefault(CompanionCatalog.family(id), 0L); }
    public int level(int id) { return levelForXp(xp(id)); }
    public static int levelForXp(long xp) {
        int level = 1;
        while (level < THRESHOLDS.length && xp >= THRESHOLDS[level]) level++;
        return level;
    }
    public static long threshold(int level) { return THRESHOLDS[Math.max(1, Math.min(10, level)) - 1]; }
    public static int milestones(int level) { return (level >= 3 ? 1 : 0) + (level >= 5 ? 1 : 0) + (level >= 7 ? 1 : 0) + (level >= 10 ? 1 : 0); }
    public boolean award(int id, int amount, long nowNanos) {
        if (CompanionCatalog.get(id) == null || amount <= 0 || (awarded && nowNanos - nextAwardNanos < 0)) return false;
        awarded = true;
        nextAwardNanos = nowNanos + 3_000_000_000L;
        experience.put(CompanionCatalog.family(id), Math.min(MAX_XP, xp(id) + Math.min(5, amount)));
        return true;
    }
    public String encode() {
        return experience.entrySet().stream().map(e -> e.getKey() + ":" + e.getValue()).collect(Collectors.joining(","));
    }
    public void decode(String saved) {
        experience.clear();
        for (String entry : saved.split(",")) {
            String[] parts = entry.split(":");
            if (parts.length != 2) continue;
            try {
                int id = Integer.parseInt(parts[0]);
                long xp = Math.max(0, Math.min(MAX_XP, Long.parseLong(parts[1])));
                if (CompanionCatalog.get(id) != null) experience.merge(CompanionCatalog.family(id), xp, Math::max);
            } catch (NumberFormatException ignored) { /* Invalid entry cannot discard other pets. */ }
        }
    }
}
