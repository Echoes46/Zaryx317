package io.zaryx.content.items;
import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Right;
import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class EquipmentGuideTest {
    Field config, definitions; Object oldConfig, oldDefinitions;
    @BeforeEach void setup() throws Exception {
        config=Server.class.getDeclaredField("configuration");config.setAccessible(true);oldConfig=config.get(null);config.set(null,ServerConfiguration.getDefault());
        definitions=ItemDef.class.getDeclaredField("definitions");definitions.setAccessible(true);oldDefinitions=definitions.get(null);
        Map<Integer,ItemDef> items=new HashMap<>();
        for(int id:new int[]{28997,29028,29022,29025,10148,10145,9185,9144,19481,19484})items.put(id,ItemDef.builder().id(id).name("Item "+id).build());
        definitions.set(null,items);
    }
    @AfterEach void restore() throws Exception {config.set(null,oldConfig);definitions.set(null,oldDefinitions);}
    Player player(int weapon) {Player p=new Player(null);p.getPerkSytem().gameItems=new ArrayList<>();p.playerEquipment[Player.playerWeapon]=weapon;p.playerEquipmentN[Player.playerWeapon]=1;return p;}
    String page(Player p,int n) {return String.join("\n",EquipmentGuide.details(p,n));}
    @Test void infusionReportsRequirementsWithoutSpendingResources() {
        Player p=player(28997);p.specAmount=10;p.getHealth().setMaximumHealth(100);p.getHealth().setCurrentHealth(99);
        assertTrue(page(p,1).contains("Missing equipment"));
        p.playerEquipment[Player.playerHat]=29028;p.playerEquipment[Player.playerChest]=29022;p.playerEquipment[Player.playerLegs]=29025;
        String text=page(p,1);assertTrue(text.contains("Set requirement: Met"));assertTrue(text.contains("HP cost now: 24"));assertTrue(text.contains("Cost: 25%"));
        assertEquals(10,p.specAmount);assertEquals(99,p.getHealth().getCurrentHealth());assertTrue(page(p,3).contains("ACTIVE - full set"));
        assertTrue(EquipmentGuide.wrap(EquipmentGuide.details(p,3)).size()<=EquipmentGuide.ROWS);
    }
    @Test void tarReadinessRequiresPositiveStackAndDoesNotConsumeIt() {
        Player p=player(10148);assertTrue(page(p,2).contains("Not ready"));p.playerEquipment[Player.playerArrows]=10145;p.playerEquipmentN[Player.playerArrows]=2;
        assertTrue(page(p,2).contains("Ready: correct tar"));assertEquals(2,p.playerEquipmentN[Player.playerArrows]);
        String styles=page(p,0);assertTrue(styles.contains("Scorch"));assertTrue(styles.contains("Flare"));assertTrue(styles.contains("Blaze"));
    }
    @Test void crossbowsAndBallistasUseActualAmmoChecks() {
        Player p=player(9185);p.playerEquipment[Player.playerArrows]=9144;p.playerEquipmentN[Player.playerArrows]=1;
        assertTrue(page(p,2).contains("Current ammunition is compatible"));p.playerEquipmentN[Player.playerArrows]=0;assertTrue(page(p,2).contains("Equip compatible bolts"));
        p.playerEquipment[Player.playerWeapon]=19481;p.playerEquipment[Player.playerArrows]=19484;p.playerEquipmentN[Player.playerArrows]=1;assertTrue(page(p,2).contains("Current ammunition is compatible"));
    }
    @Test void numericSuffixIsOwnerOnly() {Player p=player(28997);assertFalse(EquipmentGuide.name(p,28997).contains("["));p.getRights().setPrimary(Right.STAFF_MANAGER);assertTrue(EquipmentGuide.name(p,28997).contains("[28997]"));}
}
