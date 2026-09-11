package io.zaryx.model.entity.npc.pets;

import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.Entity;
import io.zaryx.content.skills.Skill;
import io.zaryx.util.Misc;
import java.util.*;

/** Level benefits are additive to existing pet perks, and never replace them. */
public final class CompanionBenefits {
    private CompanionBenefits() { }
    public static int activeId(Player p) {
        return p.hasFollower && CompanionCatalog.get(p.petSummonId) != null ? p.petSummonId : -1;
    }
    public static double xpBonus(int id, int level, int skill) {
        CompanionCatalog.Profile profile = CompanionCatalog.get(id);
        if (profile == null || (profile.skill != -1 && profile.skill != skill)) return 0;
        return (profile.strength() + CompanionProgress.milestones(level)) / 100.0;
    }
    public static double dropBonus(int id, int level) {
        CompanionCatalog.Profile p = CompanionCatalog.get(id);
        return p == null || p.strength() < 2 ? 0 : (p.strength() - 1 + CompanionProgress.milestones(level) * .5) / 100.0;
    }
    public static double damageBonus(int id, int level) {
        CompanionCatalog.Profile p = CompanionCatalog.get(id);
        return p == null || p.strength() < 3 ? 0 : (p.strength() - 2 + CompanionProgress.milestones(level) * .5) / 100.0;
    }
    public static int recoveryChance(int id, int level) {
        CompanionCatalog.Profile p = CompanionCatalog.get(id);
        return p == null || p.strength() < 4 ? 0 : 5 + CompanionProgress.milestones(level);
    }
    public static double damageBonus(Player p, Entity target) {
        int id = activeId(p);
        return target.isNPC() && !p.getPosition().inWild() ? damageBonus(id, p.companionProgress.level(id)) : 0;
    }
    public static void earn(Player p, int points, boolean combat) {
        int id = activeId(p);
        if (id == -1 || p.getPosition().inWild() || p.playerAttackingIndex > 0) return;
        int old = p.companionProgress.level(id);
        if (!p.companionProgress.award(id, points, System.nanoTime())) return;
        int level = p.companionProgress.level(id);
        if (level > old) p.sendMessage("@or1@Your companion reached level " + level + "! Use ::pet to see its bonuses.");
        // The same account-wide award cooldown prevents multi-hit/AOE recovery spam.
        if (combat && Misc.random(99) < recoveryChance(id, level)) {
            p.getHealth().setCurrentHealth(restoreOne(p.getHealth().getCurrentHealth(), p.getHealth().getMaximumHealth()));
            p.playerLevel[5] = restoreOne(p.playerLevel[5], p.getPA().getLevelForXP(p.playerXP[5]));
            p.getPA().refreshSkill(3);
            p.getPA().refreshSkill(5);
        }
    }
    public static int restoreOne(int current, int cap) { return current < cap ? current + 1 : current; }
    public static List<String> describe(Player player, int id) {
        int level = player.companionProgress.level(id);
        List<String> result = new ArrayList<>();
        CompanionCatalog.Profile profile = CompanionCatalog.get(id);
        result.add("@or1@PROGRESSION - " + profile.tier.name().replace('_', ' '));
        result.add("Acquisition: " + profile.source.split(";", 2)[0] + ".");
        result.add("Level " + level + " / 10 | Companion XP: " + player.companionProgress.xp(id));
        if (level < 10) result.add("Next level: " + (CompanionProgress.threshold(level + 1) - player.companionProgress.xp(id)) + " XP remaining.");
        else result.add("Maximum level reached. All milestone bonuses unlocked.");
        result.add("Earn XP from PvM damage and eligible skilling while summoned.");
        result.add("Max 5 XP per 3 seconds; no PvP, Wilderness or training dummies.");
        String skill = profile.skill == -1 ? "All skill" : Skill.forId(profile.skill).toString();
        result.add(skill + " XP: +" + percent(xpBonus(id, level, profile.skill)) + "%.");
        if (dropBonus(id, level) > 0) result.add("Additional drop-rate modifier: +" + percent(dropBonus(id, level)) + "%.");
        if (damageBonus(id, level) > 0) result.add("Additional PvM damage: +" + percent(damageBonus(id, level)) + "% outside Wilderness.");
        if (recoveryChance(id, level) > 0) result.add("Second wind: " + recoveryChance(id, level) + "% roll per eligible PvM action; +1 HP/prayer.");
        if (level < 10) {
            int next = level < 3 ? 3 : level < 5 ? 5 : level < 7 ? 7 : 10;
            result.add("Next perk upgrade at level " + next + ": +1% XP"
                    + (profile.strength() >= 2 ? ", +0.5% drop" : "") + ".");
            if (profile.strength() >= 3) result.add("Also +0.5% PvM damage" + (profile.strength() >= 4 ? " and +1% second-wind chance." : "."));
        }
        result.add("Existing pet perks below remain available at level 1.");
        result.add("");
        return result;
    }
    private static String percent(double value) { return String.format(Locale.ROOT, "%.1f", value * 100); }
}
