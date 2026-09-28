package io.zaryx.content.upgrade;

import io.zaryx.content.fireofexchange.FireOfExchangeBurnPrice;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class UpgradeEconomyTest {
    @Test
    void successRollMatchesDisplayedPercentageIncludingFractionalFortuneBonus() {
        for (double chance : new double[]{0, 19.1, 39.1, 49, 59, 79, 91, 100}) {
            int successes = 0;
            for (int i = 0; i < 1000; i++) {
                if (UpgradeInterface.succeeds(chance, (i + 0.5) / 10.0)) successes++;
            }
            assertEquals((int) Math.round(chance * 10), successes);
            assertFalse(UpgradeInterface.succeeds(chance, chance));
        }
    }

    @Test
    void recipesAreUnambiguousAndCannotBeUpgradedThenDissolvedForGuaranteedProfit() {
        Set<Integer> inputs = new HashSet<>();
        for (UpgradeMaterials recipe : UpgradeMaterials.values()) {
            assertTrue(inputs.add(recipe.getRequired().getId()), recipe.name());
            assertTrue(recipe.getCost() > 0 && recipe.getCost() <= 175_000_000, recipe.name());
            assertTrue(recipe.getSuccessRate() > 0 && recipe.getSuccessRate() <= 100, recipe.name());
            int rewardValue = FireOfExchangeBurnPrice.getBurnPrice(null, recipe.getReward().getId(), false);
            // Even a free input and a guaranteed success cannot turn the fee into more points.
            assertTrue(rewardValue <= recipe.getCost() / 5, recipe.name() + " dissolves above 20% of its fee");
        }
    }

    @Test
    void repeatFailuresHaveBoundedExpectedPointCostsForRepresentativeFullChains() {
        assertEquals(500_000, UpgradeMaterials.VOID_TOP.getCost());
        assertEquals(500_000, UpgradeMaterials.AMULET_OF_FURY.getCost());
        assertTrue(chainCost(UpgradeMaterials.SANGUINGE_SCYTHE, new HashSet<>()) < 1_500_000_000);
        assertTrue(chainCost(UpgradeMaterials.BOXRING, new HashSet<>()) < 500_000_000);
        assertTrue(chainCost(UpgradeMaterials.FORCEHELM, new HashSet<>()) < 500_000_000);
        for (UpgradeMaterials recipe : UpgradeMaterials.values()) chainCost(recipe, new HashSet<>());
    }

    private double chainCost(UpgradeMaterials recipe, Set<UpgradeMaterials> visited) {
        assertTrue(visited.add(recipe), "Upgrade cycle at " + recipe.name());
        double priorCost = 0;
        for (UpgradeMaterials prior : UpgradeMaterials.values()) {
            if (prior.getReward().getId() == recipe.getRequired().getId()) priorCost = chainCost(prior, visited);
        }
        return (recipe.getCost() + priorCost) / ((recipe.getSuccessRate() + 0.1) / 100.0);
    }
}
