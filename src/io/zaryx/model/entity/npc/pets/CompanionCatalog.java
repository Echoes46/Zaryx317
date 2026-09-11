package io.zaryx.model.entity.npc.pets;

import java.io.*;
import java.util.*;

/** Reviewed item-ID profiles, loaded from a versioned resource rather than guessed at runtime. */
public final class CompanionCatalog {
    public enum Tier { COMMON, UNCOMMON, RARE, ELITE, DONOR_EXCLUSIVE }
    public static final class Profile {
        public final Tier tier;
        public final int skill;
        public final String source;
        private Profile(Tier tier, int skill, String source) { this.tier = tier; this.skill = skill; this.source = source; }
        public int strength() { return Math.min(4, tier.ordinal() + 1); }
    }
    private static final Map<Integer, Profile> PROFILES = load();
    private CompanionCatalog() { }
    public static Profile get(int id) { return PROFILES.get(id); }
    public static int family(int id) {
        if (id >= 1555 && id <= 1560) return 1555;
        if (id == 13180) return 13179;
        if (id == 12939 || id == 12940) return 12921;
        if (id == 12647) return 12654;
        if (id == 22318) return 12816;
        if (id == 21187 || id == 21188 || id == 21189 || id == 21192 || id == 21193 || id == 21194 || id == 21196 || id == 21197) return 13321;
        if (id >= 20665 && id <= 20691 && id % 2 == 1) return 20665;
        if (id == 22748 || id == 22750 || id == 22752) return 22746;
        return id;
    }
    private static Map<Integer, Profile> load() {
        Properties input = new Properties();
        try (InputStream stream = CompanionCatalog.class.getResourceAsStream("/pet-progression.properties")) {
            if (stream == null) throw new IOException("Missing pet-progression.properties");
            input.load(stream);
        } catch (IOException e) { throw new ExceptionInInitializerError(e); }
        Map<Integer, Profile> result = new HashMap<>();
        for (String key : input.stringPropertyNames()) {
            String[] parts = input.getProperty(key).split("\\|", 3);
            int id = Integer.parseInt(key), skill = Integer.parseInt(parts[1]);
            if (PetHandler.forItem(id) == null || skill < -1 || skill > 22) throw new IllegalStateException("Invalid companion profile " + key);
            result.put(id, new Profile(Tier.valueOf(parts[0]), skill, parts[2]));
        }
        for (PetHandler.Pets pet : PetHandler.Pets.values()) {
            if (!result.containsKey(pet.getItemId())) throw new IllegalStateException("Missing companion profile " + pet);
        }
        return Collections.unmodifiableMap(result);
    }
}
