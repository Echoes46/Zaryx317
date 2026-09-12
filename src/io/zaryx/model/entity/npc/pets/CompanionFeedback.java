package io.zaryx.model.entity.npc.pets;

import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.ItemAssistant;
import java.util.*;

/** Account preference and change-only updates for the summoned companion HUD. */
public final class CompanionFeedback {
    public boolean enabled = true;
    private String[] sent;

    public static String[] progress(long xp, boolean paused) {
        int level = CompanionProgress.levelForXp(xp);
        if (level >= 10) return new String[]{"Maximum level - 50,000 XP", "All milestones unlocked", "100"};
        long earned = xp - CompanionProgress.threshold(level);
        long required = CompanionProgress.threshold(level + 1) - CompanionProgress.threshold(level);
        int next = level < 3 ? 3 : level < 5 ? 5 : level < 7 ? 7 : 10;
        return new String[]{earned + " / " + required + " XP to level " + (level + 1),
                paused ? "XP paused - Wilderness / PvP" : "Next perk upgrade: level " + next,
                Long.toString(earned * 100 / required)};
    }

    public void update(Player p) {
        int id = CompanionBenefits.activeId(p);
        String[] lines = {"", "", "", "0"};
        if (enabled && id != -1) {
            String[] progress = progress(p.companionProgress.xp(id), p.getPosition().inWild() || p.playerAttackingIndex > 0);
            lines = new String[]{ItemAssistant.getItemName(id) + " - Lv " + p.companionProgress.level(id), progress[0], progress[1], progress[2]};
        }
        for (int i = 0; i < lines.length; i++)
            if (sent == null || !lines[i].equals(sent[i])) p.getPA().sendString(22876 + i, lines[i]);
        sent = lines;
    }

    public static List<String> upgrades(int id, int before, int after) {
        List<String> result = new ArrayList<>();
        CompanionCatalog.Profile profile = CompanionCatalog.get(id);
        if (profile == null || CompanionProgress.milestones(before) == CompanionProgress.milestones(after)) return result;
        String skill = profile.skill == -1 ? "All skill" : io.zaryx.content.skills.Skill.forId(profile.skill).toString();
        result.add(skill + " XP: " + pct(CompanionBenefits.xpBonus(id, before, profile.skill)) + " -> " + pct(CompanionBenefits.xpBonus(id, after, profile.skill)));
        if (CompanionBenefits.dropBonus(id, after) > 0) result.add("Drop modifier: " + pct(CompanionBenefits.dropBonus(id, before)) + " -> " + pct(CompanionBenefits.dropBonus(id, after)));
        if (CompanionBenefits.damageBonus(id, after) > 0) result.add("PvM damage: " + pct(CompanionBenefits.damageBonus(id, before)) + " -> " + pct(CompanionBenefits.damageBonus(id, after)));
        if (CompanionBenefits.recoveryChance(id, after) > 0) result.add("Second wind: " + CompanionBenefits.recoveryChance(id, before) + "% -> " + CompanionBenefits.recoveryChance(id, after) + "%");
        return result;
    }
    private static String pct(double n) { return String.format(Locale.ROOT, "%.1f%%", n * 100); }
    public void levelUp(Player p, int id, int before, int after) {
        List<String> changes = upgrades(id, before, after);
        p.getPA().sendNotification("Companion level up!", ItemAssistant.getItemName(id) + " reached level " + after,
                changes.isEmpty() ? "Keep training for the next milestone." : "Perks improved! See chat for details.", id);
        p.sendMessage("@or1@" + ItemAssistant.getItemName(id) + " reached companion level " + after + "!");
        for (String change : changes) p.sendMessage("@or1@" + change);
    }
}
