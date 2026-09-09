package io.zaryx.model.entity.npc.pets;

import java.util.*;

/** Shared pet categories and player-facing descriptions. IDs are item IDs, never NPC IDs. */
public final class PetPerks {
    private PetPerks() { }
    public enum Style { MELEE, RANGED, MAGIC }
    public static final double COMBAT_BONUS = 0.10;
    public static final int COMBAT_ROLL = 50; // Existing roll is 0..100 inclusive.
    public static final int RECOVERY_ROLL = 10; // One successful value out of 11.
    public static final int RECOVERY_DIVISOR = 3;

    public static int combatTier(int id, Style style) {
        if (id == 30122 || id == 33071 || (id == 33068 && style == Style.MAGIC)) return 2;
        if (id == 30020 || id == 30022 || id == 30015 + style.ordinal()) return 1;
        return 0;
    }
    public static double dropBonus(int id) {
        switch (id) {
            case 25350: case 30022: case 27354: case 27383: case 27352: case 30122: return .20;
            case 30021: case 25348: return .10;
            case 30014: case 33159: case 23760: case 10533: case 30020: return .05;
            default: return 0;
        }
    }
    public static boolean heals(int id) { return id == 30018 || id == 30022 || id == 30122; }
    public static boolean restoresPrayer(int id) { return id == 30019 || id == 30022 || id == 30122; }
    public static String displayName(int id, String fallback) {
        switch (id) {
            case 33065: return "Beaver";
            case 33066: return "Rock";
            case 33067: return "Mystery Box";
            case 33068: return "Fish";
            case 33071: return "Head";
            case 33159: return "Christmas Imp";
            case 30022: return "Kratos";
            default: return fallback.replaceAll("@[a-zA-Z0-9]+@", "").replaceAll("<[^>]+>", "");
        }
    }
    public static List<String> describe(int id) {
        List<String> lines = new ArrayList<>();
        List<String> combat = new ArrayList<>();
        for (Style style : Style.values()) {
            int tier = combatTier(id, style);
            if (tier > 0) combat.add(style.name().toLowerCase(Locale.ROOT) + ": +10% damage modifier; "
                    + (tier == 2 ? "always." : "50/101 chance per roll."));
        }
        if (id == 25348) combat.add("+20% combat modifier; melee bonus excludes Wilderness.");
        if (id == 25350) combat.add("Extra raid/special-attack damage in legacy combat paths.");
        if (id == 10533) combat.add("+10% melee modifier when both targets are in Wilderness.");
        if (id == 27352) combat.add("+15% magic modifier with eligible powered staves.");
        if (id == 27383) combat.add("70% chance to trigger the Ely damage-reduction effect.");
        if (id == 23939) combat.add("50% chance to negate Seren / Unbearable attack damage.");
        section(lines, "COMBAT", combat);
        List<String> recovery = new ArrayList<>();
        if (heals(id)) recovery.add("Heal for 1/3 of damage dealt on a 1-in-11 roll.");
        if (restoresPrayer(id)) recovery.add("Restore prayer by 1/3 of damage on a 1-in-11 roll.");
        if (!recovery.isEmpty()) recovery.add("Requires positive damage; restoration caps at your level.");
        section(lines, "RECOVERY", recovery);
        List<String> loot = new ArrayList<>();
        double drop = dropBonus(id);
        if (drop > 0) loot.add("+" + (int)Math.round(drop * 100) + "% drop-rate modifier (not a flat drop chance).");
        if (id == 30010 || id == 30022 || id == 30122) loot.add("Collects eligible key drops when the collection roll succeeds.");
        if (id == 30010) loot.add("Upgrade-ticket drop quantity is set to two.");
        if (id == 30011 || id == 30022 || id == 30122) loot.add("Collects eligible clue-scroll drops.");
        if (id == 30011) loot.add("Eligible NPC kills: 1-in-226 chance of an XP lamp drop.");
        if (id == 30012 || id == 30022 || id == 30122) loot.add("Collects eligible resource-box drops.");
        if (id == 30013 || id == 30022 || id == 30122) loot.add("Collects eligible coin-bag drops.");
        if (id == 30022) loot.add("Also collects selected special boxes; rules vary by drop.");
        if (id >= 30010 && id <= 30013 || id == 30022) loot.add("Standard collection: 80/101 roll on eligible drops.");
        if (id == 30122) loot.add("Dark collection is guaranteed; 25/101 roll for an extra item.");
        if (id == 25350 || id == 30122) loot.add("Improves Theatre of Blood rare-reward roll by 20 points.");
        if (id == 23939) loot.add("Adds two separate 1-in-551 Wilderness key rolls.");
        if (id == 33067) loot.add("11/101 chance to double mystery-box reward quantity.");
        if (id == 33067) loot.add("Mystery-box perk also works while carried in inventory.");
        if (!loot.isEmpty()) loot.add("Collection needs an eligible drop; full-inventory rules vary.");
        section(lines, "LOOT", loot);
        List<String> utility = new ArrayList<>();
        if (id == 30013) utility.add("+25% XP through the standard XP award system.");
        if (id == 27383) utility.add("3x XP through the standard XP award system.");
        if (id == 27889) utility.add("Melee hits: 1-in-6 roll for bonus Slayer XP from damage.");
        if (id == 23760) utility.add("+10% mining XP; also works while carried in inventory.");
        if (id == 23760) utility.add("2x shooting-star / active-volcano material yield.");
        if (id == 33065) utility.add("2x AFK reward amount while summoned.");
        if (id == 31014) utility.add("Item storage: use the pet's storage interaction.");
        if (id == 10533) utility.add("Bypasses certain Wilderness teleport limits, not all checks.");
        if (id == 33208) utility.add("Speeds up Groot spawn progress; carried pet also qualifies.");
        if (id == 30122) utility.add("Carrying Dark Kratos also speeds up Groot spawn progress.");
        if (id >= 33210 && id <= 33212) utility.add("Christmas spawn progress: +" + (id - 33209) + " per eligible kill; carried also works.");
        if (id == 33159) utility.add("250 instead of 50 points for the qualifying seasonal boss.");
        section(lines, "UTILITY", utility);
        if (lines.isEmpty()) {
            lines.add("@or1@COMPANION");
            lines.add("This pet currently has no registered gameplay perk.");
        }
        return Collections.unmodifiableList(lines);
    }
    private static void section(List<String> target, String title, List<String> content) {
        if (content.isEmpty()) return;
        if (!target.isEmpty()) target.add("");
        target.add("@or1@" + title);
        target.addAll(content);
    }
}
