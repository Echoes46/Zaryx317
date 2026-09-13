package io.zaryx.content.combat.weapon;
import io.zaryx.model.Items;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StarterWeaponTest {
    @Test void starterWeaponsHaveCorrectStyleTables() {
        assertEquals(WeaponData.BOW, WeaponData.forItemId(Items.STARTER_BOW));
        assertEquals(WeaponInterface.BOW, WeaponData.forItemId(Items.STARTER_BOW).getWeaponInterface());
        assertEquals(3, WeaponData.forItemId(Items.STARTER_BOW).getWeaponModes().length);
        assertEquals(WeaponData.SLASH_SWORD, WeaponData.forItemId(Items.STARTER_SWORD));
    }
}
