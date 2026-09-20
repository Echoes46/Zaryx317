package io.zaryx.content.skills;

import io.zaryx.model.entity.Entity;
import io.zaryx.model.entity.player.Player;

/**
 * Shared progression benefits for Zaryx's custom skills.
 */
public final class CustomSkillBenefits {

    private static final double DEMON_HUNTER_LEVEL_99_MASTERY_BONUS = 0.021;
    private static final double FORTUNE_LEVEL_99_MASTERY_BONUS = 2.1;
    private static final int FORTUNE_MASTERY_SAVE_CHANCE = 10;

    private CustomSkillBenefits() {
    }

    /**
     * Percentage points added to an item's listed upgrade success rate.
     * Fortune grants one point per ten levels and reaches twelve points at mastery.
     */
    public static double fortuneUpgradeSuccessBonus(Player player) {
        int level = level(player, Skill.FORTUNE);
        return level / 10.0 + (level >= 99 ? FORTUNE_LEVEL_99_MASTERY_BONUS : 0.0);
    }

    public static int fortuneFailedUpgradeSaveChance(Player player) {
        return level(player, Skill.FORTUNE) >= 99 ? FORTUNE_MASTERY_SAVE_CHANCE : 0;
    }

    /**
     * Demon Hunter grants 0.1% damage and accuracy per level against demons,
     * with an additional mastery bump that brings level 99 to exactly 12%.
     */
    public static double demonHunterCombatMultiplier(Player player, Entity defender) {
        if (player == null || defender == null || !defender.isNPC() || !defender.asNPC().isDemon()) {
            return 1.0;
        }
        int level = level(player, Skill.DEMON_HUNTER);
        return 1.0 + level / 1000.0
                + (level >= 99 ? DEMON_HUNTER_LEVEL_99_MASTERY_BONUS : 0.0);
    }

    private static int level(Player player, Skill skill) {
        return Math.max(1, Math.min(99, player.getLevel(skill)));
    }
}
