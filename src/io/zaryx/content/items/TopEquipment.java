package io.zaryx.content.items;

import io.zaryx.Server;
import io.zaryx.content.combat.specials.impl.BloodInfusion;
import io.zaryx.model.definitions.ItemEquipmentStats;
import io.zaryx.model.entity.player.Player;
import java.util.*;

/** Read-only interface; no inventory, equipment, special energy or currency mutations. */
public final class TopEquipment {
    public static final int ROOT = 61900, ENTRY = 61904, HELP = 62100;
    private static final String[] STYLES = {"Melee", "Ranged", "Magic"};
    private static final String[] SLOTS = {"Helmets", "Body", "Legs", "Boots", "Gloves", "Jewelry", "Cape", "Weapon", "Shield"};
    private TopEquipment() { }

    public static boolean click(Player p, int id) {
        boolean control = id == 61903 || id == ENTRY || id == 61905 || id == 61908 || id == 62102
                || id >= 61910 && id <= 61912 || id >= 61920 && id <= 61928;
        if (!control) return false;
        if (p.getInterfaceEvent().isActive() || Server.getMultiplayerSessionListener().inAnySession(p)) return true;
        if (id != ENTRY && !p.isInterfaceOpen(ROOT) && !p.isInterfaceOpen(HELP)) return true;
        if (id == 61903) { p.getPA().closeAllWindows(); return true; }
        if (id == 61908) { EquipmentGuide.open(p, p.equipmentGuidePage); return true; }
        if (id == 61905) { p.getPA().showInterface(HELP); return true; }
        if (id >= 61910 && id <= 61912) p.topEquipmentStyle = id - 61910;
        if (id >= 61920 && id <= 61928) p.topEquipmentSlot = id - 61920;
        open(p);
        return true;
    }

    public static void open(Player p) {
        int style = Math.max(0, Math.min(2, p.topEquipmentStyle));
        int slot = Math.max(0, Math.min(8, p.topEquipmentSlot));
        GearRankings.Style combatStyle = GearRankings.Style.values()[style];
        List<GearRankings.Entry> entries = GearRankings.top(combatStyle, GearRankings.Slot.values()[slot]);
        for (int i = 0; i < 3; i++) p.getPA().sendString(61910 + i, (i == style ? "@or1@" : "@whi@") + STYLES[i]);
        for (int i = 0; i < 9; i++) p.getPA().sendString(61920 + i, (i == slot ? "@or1@" : "@whi@") + SLOTS[i]);
        p.getPA().sendString(61906, STYLES[style] + " / " + SLOTS[slot]);
        p.getPA().sendString(61907, entries.isEmpty() ? "No eligible equipment in the server data."
                : "Top " + entries.size() + " by gear score - scroll for more");
        for (int row = 0; row < 10; row++) {
            int base = 61950 + row * 10;
            GearRankings.Entry e = row < entries.size() ? entries.get(row) : null;
            p.getPA().sendFrame34(e == null ? -1 : e.id, 0, base + 1, e == null ? 0 : 1);
            String[] lines = e == null ? new String[6] : lines(e, combatStyle, row + 1);
            for (int line = 0; line < 6; line++) p.getPA().sendString(base + 2 + line, lines[line] == null ? "" : lines[line]);
        }
        p.getPA().setScrollableMaxHeight(61940, Math.max(218, entries.size() * 86));
        p.getPA().resetScrollBar(61940);
        p.getPA().showInterface(ROOT);
    }

    static String[] lines(GearRankings.Entry e, GearRankings.Style style, int rank) {
        ItemEquipmentStats s = e.stats;
        String slot = s.getEquipmentSlot() == 2 ? "Necklace" : s.getEquipmentSlot() == 12 ? "Ring" : "";
        String speed = s.getEquipmentSlot() == 3 ? " | Speed " + (s.getAttackSpeed() > 0 ? s.getAttackSpeed() : 4) + " ticks" : "";
        String[] lines = new String[6];
        lines[0] = rank + ". " + shorten(e.name, 37);
        lines[1] = "Score " + number(e.score) + (slot.isEmpty() ? "" : " | " + slot) + speed;
        lines[2] = "Attack " + GearRankings.accuracy(s, style) + " | "
                + (style == GearRankings.Style.MAGIC ? "Magic dmg " : "Strength ") + GearRankings.damage(s, style)
                + (style == GearRankings.Style.MAGIC ? "%" : "") + " | Prayer " + s.getPrayer();
        lines[3] = "Def S/Sl/C/M/R: " + s.getDstab() + "/" + s.getDslash() + "/" + s.getDcrush() + "/" + s.getDmagic() + "/" + s.getDrange();
        lines[4] = e.special == null ? "Special: none" : "Spec: " + shorten(GearRankings.specialName(e.special), 25) + " | " + number(e.special.getRequiredCost() * 10) + "% energy";
        lines[5] = e.special == null ? "" : e.special instanceof BloodInfusion ? "Requires full Blood Moon set; costs 25% current HP."
                : "Spec acc x" + number(e.special.getAccuracy()) + " / dmg x" + number(e.special.getDamageModifier()) + " | +" + number(e.specialScore) + " score";
        return lines;
    }
    private static String number(double n) { return String.format(Locale.ROOT, "%.2f", n).replaceAll("0+$", "").replaceAll("\\.$", ""); }
    private static String shorten(String text, int limit) { return text.length() <= limit ? text : text.substring(0, limit - 3) + "..."; }
}
