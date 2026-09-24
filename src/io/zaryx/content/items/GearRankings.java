package io.zaryx.content.items;

import io.zaryx.content.combat.specials.Special;
import io.zaryx.content.combat.specials.Specials;
import io.zaryx.content.combat.weapon.WeaponData;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.definitions.ItemEquipmentStats;
import io.zaryx.model.definitions.ItemStats;
import java.util.*;
import java.util.stream.Collectors;

/** Transparent equipment comparison heuristic, never a combat/DPS prediction. */
public final class GearRankings {
    public enum Style { MELEE, RANGED, MAGIC }
    public enum Slot {
        HELMETS(0), BODY(4), LEGS(7), BOOTS(10), GLOVES(9), JEWELRY(2, 12), CAPE(1), WEAPON(3), SHIELD(5);
        private final int[] ids;
        Slot(int... ids) { this.ids = ids; }
        boolean includes(int id) { for (int slot : ids) if (slot == id) return true; return false; }
    }
    public static final class Entry {
        public final int id;
        public final String name;
        public final ItemEquipmentStats stats;
        public final Special special;
        public final double score, specialScore;
        Entry(int id, String name, ItemEquipmentStats stats, Special special, Style style) {
            this.id = id; this.name = name; this.stats = stats; this.special = special;
            specialScore = offence(stats, style) * specialBoost(special);
            score = score(stats, style, special);
        }
    }
    private GearRankings() { }

    public static List<Entry> top(Style style, Slot slot) {
        return rank(ItemStats.itemStatsMap, ItemDef.getDefinitions(), style, slot);
    }

    static List<Entry> rank(Map<Integer, ItemStats> stats, Map<Integer, ItemDef> definitions, Style style, Slot slot) {
        if (stats == null || definitions == null) return Collections.emptyList();
        Map<String, Entry> unique = new HashMap<>();
        for (Map.Entry<Integer, ItemStats> item : stats.entrySet()) {
            int id = item.getKey(); ItemStats data = item.getValue();
            // ItemAssistant restricts the perfect ring to server owners.
            if (id <= 0 || id == 773 || data == null || !Boolean.TRUE.equals(data.getEquipable())) continue;
            ItemEquipmentStats equipment = data.getEquipment();
            if (!slot.includes(equipment.getEquipmentSlot())) continue;
            ItemDef def = definitions.get(id);
            if (def == null || def.isNoted()) continue;
            String name = data.getName();
            if (name == null || name.trim().isEmpty()) continue;
            String normalized = name.toLowerCase(Locale.ROOT).trim();
            if (normalized.equals("null") || normalized.contains("broken") || normalized.contains("uncharged")
                    || normalized.contains("(inactive)") || normalized.endsWith(" 0") || normalized.contains("placeholder")) continue;
            if (!supports(id, equipment, style)) continue;
            Special special = equipment.getEquipmentSlot() == 3 ? Specials.forWeaponId(id) : null;
            Entry entry = new Entry(id, name, equipment, special, style);
            if (entry.score <= 0 || !Double.isFinite(entry.score)) continue;
            // Degraded copies of the same named gear do not consume the top ten.
            String key = normalized.replaceFirst(" (100|75|50|25)$", "");
            Entry previous = unique.get(key);
            if (previous == null || ORDER.compare(entry, previous) < 0) unique.put(key, entry);
        }
        return unique.values().stream().sorted(ORDER).limit(10).collect(Collectors.toList());
    }

    private static final Comparator<Entry> ORDER = Comparator.comparingDouble((Entry e) -> e.score).reversed()
            .thenComparing(e -> e.name, String.CASE_INSENSITIVE_ORDER).thenComparingInt(e -> e.id);

    private static boolean supports(int id, ItemEquipmentStats s, Style style) {
        // Armour with neutral offence is still useful for defence/prayer; do not
        // rank armour which penalizes the selected style and adds no damage.
        if (s.getEquipmentSlot() != 3) return accuracy(s, style) >= 0 || damage(s, style) > 0;
        WeaponData weapon = WeaponData.forItemId(id);
        if (weapon == WeaponData.SALAMANDER) return true;
        if (style == Style.RANGED) return weapon == WeaponData.BOW || weapon == WeaponData.THROWN
                || weapon == WeaponData.UNARMED && s.getArange() > 0;
        if (style == Style.MAGIC) return weapon == WeaponData.STAFF || weapon == WeaponData.SOTD
                || weapon == WeaponData.UNARMED && (s.getAmagic() > 0 || s.getMdmg() > 0);
        return weapon != WeaponData.BOW && weapon != WeaponData.THROWN
                && (accuracy(s, style) > 0 || s.getStr() > 0);
    }

    public static int accuracy(ItemEquipmentStats s, Style style) {
        return style == Style.MELEE ? Math.max(s.getAstab(), Math.max(s.getAslash(), s.getAcrush()))
                : style == Style.RANGED ? s.getArange() : s.getAmagic();
    }
    public static int damage(ItemEquipmentStats s, Style style) {
        return style == Style.MELEE ? s.getStr() : style == Style.RANGED ? s.getRstr() : s.getMdmg();
    }
    static double offence(ItemEquipmentStats s, Style style) {
        double value = Math.max(0, accuracy(s, style) + 4.0 * damage(s, style));
        return s.getEquipmentSlot() == 3 ? value * 4 / Math.max(1, s.getAttackSpeed() > 0 ? s.getAttackSpeed() : 4) : value;
    }
    static double specialBoost(Special special) {
        if (special == null || special.getRequiredCost() <= 0) return 0;
        double gains = Math.max(0, special.getAccuracy() - 1) + Math.max(0, special.getDamageModifier() - 1);
        return Math.min(0.30, (0.05 + gains * 0.10) * 5 / special.getRequiredCost());
    }
    static double score(ItemEquipmentStats s, Style style, Special special) {
        double defence = (s.getDstab() + (double)s.getDslash() + s.getDcrush() + s.getDmagic() + s.getDrange()) / 5;
        return offence(s, style) * (1 + specialBoost(special)) + defence * 0.15 + s.getPrayer() * 2;
    }
    public static String specialName(Special special) {
        return special.getClass().getSimpleName().replace("SpecialAttack", "").replaceAll("([a-z])([A-Z])", "$1 $2");
    }
}
