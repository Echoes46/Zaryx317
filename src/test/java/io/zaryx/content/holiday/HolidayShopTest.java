package io.zaryx.content.holiday;

import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.npc.pets.PetHandler;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class HolidayShopTest {
    @Test void shopDoesNotResellCompletionCostumesAndEveryPageFits() {
        for (Holiday holiday : Holiday.values()) {
            Set<Integer> completion = new HashSet<>();
            for (int id : holiday.rewards) completion.add(id);
            Set<Integer> sold = new HashSet<>();
            List<HolidayShop.Reward> rewards = HolidayShop.rewards(holiday);
            for (HolidayShop.Reward reward : rewards) {
                assertTrue(reward.cost > 0);
                for (int id : reward.items) {
                    assertFalse(completion.contains(id), "Completion item sold: " + id);
                    assertTrue(sold.add(id), "Duplicate shop item: " + id);
                }
            }
            int pages = (rewards.size() + HolidayShop.PAGE_SIZE - 1) / HolidayShop.PAGE_SIZE;
            for (int page = 0; page < pages; page++) {
                int count = Math.min(HolidayShop.PAGE_SIZE, rewards.size() - page * HolidayShop.PAGE_SIZE);
                assertTrue(count + (page > 0 ? 1 : 0) + (page + 1 < pages ? 1 : 0) + 1 <= 5);
            }
        }
    }

    @Test void rewardsHaveServerDefinitionsAndPetsAreRegistered() throws Exception {
        ItemDef.load();
        for (Holiday holiday : Holiday.values()) {
            for (HolidayShop.Reward reward : HolidayShop.rewards(holiday)) {
                for (int id : reward.items) {
                    ItemDef def = ItemDef.forId(id);
                    assertNotNull(def, "Missing item: " + id);
                    assertNotNull(def.getName(), "Unnamed item: " + id);
                    assertNotEquals("Item", def.getName(), "Placeholder item: " + id);
                }
                if (reward.name.endsWith(" pet")) {
                    assertTrue(Arrays.stream(PetHandler.Pets.values())
                            .anyMatch(pet -> pet.getItemId() == reward.items[0]), reward.name);
                }
            }
        }
    }
}
