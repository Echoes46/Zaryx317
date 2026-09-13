package io.zaryx.model.entity.npc.pets;

import io.zaryx.content.commands.all.Pet;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.ItemAssistant;
import java.util.*;
import java.util.stream.Collectors;

/** Read-only, account-local browsing state. Never changes pets or progression. */
public final class CompanionJournal {
    public static final int PAGE_SIZE = 9;
    private static final String[] TABS = {"Browse", "Progress", "Perks", "", "Compare"};
    private static final String[] OWNERSHIP = {"All", "Owned", "Missing"};
    private static final String[] ROLES = {"All roles", "Combat", "Skilling", "Utility"};
    private int selected = -1, pinned = -1, tab, page, ownership, role, detailPage;
    private String query = "";
    private List<Integer> visible = Collections.emptyList();

    public static List<Integer> ids() {
        return Arrays.stream(PetHandler.Pets.values()).map(PetHandler.Pets::getItemId)
                .distinct().filter(id -> !name(id).trim().equalsIgnoreCase("Dwarf remains")).sorted(Comparator.comparing(CompanionJournal::name).thenComparingInt(Integer::intValue))
                .collect(Collectors.toList());
    }
    public static String name(int id) {
        String fallback = ItemAssistant.getItemName(id);
        if (fallback == null || fallback.equalsIgnoreCase("null")) fallback = PetHandler.forItem(id).name().replace('_', ' ');
        return PetPerks.displayName(id, fallback);
    }
    public static boolean owned(Player p, int id) {
        return (p.hasFollower && p.petSummonId == id) || p.getItems().getItemCount(id, false) > 0;
    }
    public static boolean showItemIds(Player p) {
        return p.getRights().contains(io.zaryx.model.entity.player.Right.STAFF_MANAGER);
    }
    public static String label(Player p, int id, boolean compact) {
        String text = name(id);
        if (compact && text.length() > 32) text = text.substring(0, 29) + "...";
        return text + (showItemIds(p) ? " [" + id + "]" : "");
    }
    public static boolean matchesRole(int id, int role) {
        if (role == 0) return true;
        if (role == 1) return CompanionBenefits.damageBonus(id, 1) > 0
                || PetPerks.describe(id).contains("@or1@COMBAT");
        if (role == 2) return CompanionCatalog.get(id).skill >= 0;
        return PetPerks.describe(id).stream().anyMatch(s -> s.contains("UTILITY") || s.contains("LOOT") || s.contains("RECOVERY"));
    }
    public List<Integer> filtered(Player p) {
        return ids().stream().filter(id -> (query.isEmpty() || name(id).toLowerCase(Locale.ROOT).contains(query)
                || showItemIds(p) && String.valueOf(id).equals(query)))
                .filter(id -> ownership == 0 || owned(p, id) == (ownership == 1))
                .filter(id -> matchesRole(id, role)).collect(Collectors.toList());
    }
    public void open(Player p, String input) {
        query = input == null ? "" : input.trim().toLowerCase(Locale.ROOT);
        if (query.length() > 60) query = query.substring(0, 60);
        ownership = role = page = detailPage = 0;
        selected = CompanionBenefits.activeId(p);
        tab = query.isEmpty() && selected != -1 ? 1 : 0;
        render(p);
        p.getPA().showInterface(61200);
    }
    public boolean click(Player p, int button) {
        boolean row = button >= 61269 && button < 61333;
        boolean control = button >= 61333 && button <= 61337 || button >= 61339 && button <= 61344;
        if (!row && !control) return false;
        if (button == 61336) return true; // Retired Sources button, including older clients.
        if (p.getOpenInterface() != 61200 || p.getInterfaceEvent().isActive()
                || io.zaryx.Server.getMultiplayerSessionListener().inAnySession(p)) return true;
        if (button != 61339 && button != 61340) detailPage = 0;
        if (row) {
            int index = button - 61269;
            if (tab != 0 || index >= visible.size()) return true;
            selected = visible.get(index);
            tab = 1;
        } else if (button <= 61337) tab = button - 61333;
        else if (button == 61341) { ownership = (ownership + 1) % 3; page = 0; tab = 0; }
        else if (button == 61342) { role = (role + 1) % 4; page = 0; tab = 0; }
        else if (button == 61344) { p.companionFeedback.enabled = !p.companionFeedback.enabled; p.companionFeedback.update(p); }
        else if (button == 61343) { if (selected != -1) pinned = selected; }
        else {
            List<Integer> entries = filtered(p);
            int direction = button == 61339 ? -1 : 1;
            if (tab == 4) detailPage = Math.max(0, detailPage + direction);
            else if (tab == 0) page = Math.max(0, Math.min(page + direction, Math.max(0, (entries.size() - 1) / PAGE_SIZE)));
            else if (!entries.isEmpty()) selected = entries.get(Math.floorMod(entries.indexOf(selected) + direction, entries.size()));
        }
        render(p);
        return true;
    }
    public List<String> details(Player p, int id, int section) {
        List<String> lines = new ArrayList<>();
        if (id == -1) return Arrays.asList("No companions match these filters.", "Use the ownership/role buttons or ::pet to reset.");
        if (section == 1) {
            lines.addAll(CompanionBenefits.describe(p, id));
            lines.removeIf(s -> s.startsWith("Acquisition:") || s.startsWith("Existing pet perks"));
            lines.add("@or1@MILESTONES");
            for (int level : new int[]{3, 5, 7, 10}) lines.add("Level " + level + " - " + CompanionProgress.threshold(level)
                    + " total XP - " + (p.companionProgress.level(id) >= level ? "Unlocked" : "Locked"));
            lines.add("Levels belong to your account. Trading never transfers XP.");
            lines.add("Cosmetic forms share their companion family's progression.");
        } else if (section == 2) {
            lines.add("@or1@BASE PERKS - AVAILABLE FROM LEVEL 1");
            lines.addAll(PetPerks.describe(id));
            lines.add("");
            lines.add("Progress tab shows the additional bonuses from your level.");
            lines.add("Summon this companion to activate bonuses unless stated.");
        } else if (section == 4) {
            if (pinned == -1) return Arrays.asList("@or1@COMPARE TWO COMPANIONS", "Choose a companion and press Pin.",
                    "Browse to another companion, then open Compare.", "Both use your account's saved levels, even when missing.");
            lines.add("@or1@A: " + name(pinned) + " | B: " + name(id));
            lines.add("Saved level: " + p.companionProgress.level(pinned) + " | " + p.companionProgress.level(id));
            lines.add("Saved XP: " + p.companionProgress.xp(pinned) + " | " + p.companionProgress.xp(id));
            lines.add("@or1@PROGRESSION BONUSES: A | B");
            lines.add("Skill XP: " + xpLabel(p, pinned) + " | " + xpLabel(p, id));
            lines.add("Drop modifier: " + pct(CompanionBenefits.dropBonus(pinned, p.companionProgress.level(pinned))) + " | " + pct(CompanionBenefits.dropBonus(id, p.companionProgress.level(id))));
            lines.add("PvM damage: " + pct(CompanionBenefits.damageBonus(pinned, p.companionProgress.level(pinned))) + " | " + pct(CompanionBenefits.damageBonus(id, p.companionProgress.level(id))));
            lines.add("Second wind: " + CompanionBenefits.recoveryChance(pinned, p.companionProgress.level(pinned)) + "% | " + CompanionBenefits.recoveryChance(id, p.companionProgress.level(id)) + "%");
            lines.add("@or1@A: BASE PERKS"); lines.addAll(PetPerks.describe(pinned));
            lines.add("@or1@B: BASE PERKS"); lines.addAll(PetPerks.describe(id));
        }
        lines.replaceAll(line -> line.replace("progression bonuses shown above", "progression bonuses on the Progress tab"));
        return lines;
    }
    private static String pct(double n) { return String.format(Locale.ROOT, "%.1f%%", n * 100); }
    private static String xpLabel(Player p, int id) {
        int skill = CompanionCatalog.get(id).skill;
        return pct(CompanionBenefits.xpBonus(id, p.companionProgress.level(id), skill)) + " "
                + (skill == -1 ? "all skills" : io.zaryx.content.skills.Skill.forId(skill).toString());
    }
    private void render(Player p) {
        p.getPA().sendString(61344, p.companionFeedback.enabled ? "XP tracker: On" : "XP tracker: Off");
        List<Integer> entries = filtered(p);
        if (pinned != -1 && !ids().contains(pinned)) pinned = -1;
        if (!entries.contains(selected)) selected = entries.isEmpty() ? -1 : entries.get(0);
        page = Math.min(page, Math.max(0, (entries.size() - 1) / PAGE_SIZE));
        visible = new ArrayList<>(entries.subList(Math.min(page * PAGE_SIZE, entries.size()), Math.min((page + 1) * PAGE_SIZE, entries.size())));
        List<String> lines = new ArrayList<>();
        if (tab == 0) {
            for (int id : visible) lines.add(label(p, id, true) + " - Lv " + p.companionProgress.level(id)
                    + " - " + (owned(p, id) ? "Owned" : "Missing"));
            if (lines.isEmpty()) lines.add("No matches. Change filters or use ::pet to reset.");
        } else lines = Pet.wrapDetails(details(p, selected, tab));
        p.getPA().sendString(61216, tab == 0 ? "Browse companions - " + entries.size() + " matches" : selected == -1 ? "No selection" : label(p, selected, false));
        p.getPA().sendString(61223, tab == 0 ? "Page " + (page + 1) + " / " + Math.max(1, (entries.size() + 8) / 9) + " - Click a companion to inspect"
                : selected == -1 ? "Change filters to find companions" : "Level " + p.companionProgress.level(selected) + " | XP " + p.companionProgress.xp(selected)
                + " | " + (owned(p, selected) ? "Owned" : "Missing") + (CompanionBenefits.activeId(p) == selected ? " | Summoned" : " | Not summoned"));
        for (int i = 0; i < 5; i++) p.getPA().sendString(61333 + i, (i == tab ? "@or1@" : "@whi@") + TABS[i]);
        p.getPA().sendString(61341, OWNERSHIP[ownership]);
        p.getPA().sendString(61342, ROLES[role]);
        p.getPA().sendString(61343, pinned == selected && selected != -1 ? "Pinned" : "Pin");
        int pages = Math.max(1, (lines.size() + Pet.ROW_COUNT - 1) / Pet.ROW_COUNT);
        detailPage = Math.min(detailPage, pages - 1);
        List<String> displayed = lines.subList(detailPage * Pet.ROW_COUNT, Math.min(lines.size(), (detailPage + 1) * Pet.ROW_COUNT));
        p.getPA().sendString(61226, tab == 4 ? "Comparison page " + (detailPage + 1) + " / " + pages + " | Previous / Next turns pages."
                : "Search: ::pet name" + (showItemIds(p) ? " or ID" : "") + " | Pin a pet, then compare another.");
        for (int i = 0; i < Pet.ROW_COUNT; i++) p.getPA().sendString(61269 + i, i < displayed.size() ? displayed.get(i) : "");
        p.getPA().setScrollableMaxHeight(61224, Math.max(176, displayed.size() * 18 + 8));
        p.getPA().resetScrollBar(61224);
    }
}
