package io.zaryx.content.combat.weapon;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.content.combat.formula.rework.MagicCombatFormula;
import io.zaryx.model.Bonus;
import io.zaryx.model.definitions.*;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class PoweredStaffProgressionTest {
    Field configuration, definitions, ticks;
    Object oldConfig, oldDefinitions;
    long oldTicks;
    Map<Integer, ItemStats> oldStats;
    @BeforeEach void setup() throws Exception {
        configuration = Server.class.getDeclaredField("configuration"); configuration.setAccessible(true);
        oldConfig = configuration.get(null); configuration.set(null, ServerConfiguration.getDefault());
        definitions = ItemDef.class.getDeclaredField("definitions"); definitions.setAccessible(true);
        oldDefinitions = definitions.get(null); Map<Integer, ItemDef> items = new HashMap<>();
        for (int id : new int[]{-1,0,33149,27275,33205,28547})
            items.put(id, ItemDef.builder().id(id).name("Test weapon").build());
        definitions.set(null, items);
        ticks = Server.class.getDeclaredField("tickCount"); ticks.setAccessible(true);
        oldTicks = ticks.getLong(null); ticks.setLong(null, 100);
        oldStats = ItemStats.itemStatsMap; ItemStats.load();
    }
    @AfterEach void restore() throws Exception {
        configuration.set(null, oldConfig); definitions.set(null, oldDefinitions);
        ticks.setLong(null, oldTicks); ItemStats.itemStatsMap = oldStats;
    }
    Player player(int weapon, int mode) {
        Player p = new Player(null);
        p.getPerkSytem().gameItems = new ArrayList<>();
        p.playerEquipment[Player.playerWeapon] = weapon;
        p.playerEquipmentN[Player.playerWeapon] = 1;
        p.getCombatConfigs().setAttackStyle(mode);
        return p;
    }
    NPC npc() {
        return new NPC(1, 1, NpcDef.builder().name("Target").build(),
                NpcStats.builder().setName("Target").setHitpoints(100).createNpcStats());
    }

    int maxHit(int weapon, int spell, int extraBonus, int level) {
        Player p = player(weapon, 0);
        p.setSpellId(spell);
        p.playerLevel[6] = level;
        p.playerBonus[Bonus.MAGIC_DMG.ordinal()] =
                ItemStats.forId(weapon).getEquipment().getBonus(Bonus.MAGIC_DMG) + extraBonus;
        return MagicCombatFormula.STANDARD.getMaxHit(p, npc(), 1, 1);
    }

    @Test void upgradesRemainStrongerWithAdditionalGearAndBoostedMagic() {
        for (int level : new int[]{75, 99, 110, 120}) {
            for (int bonus : new int[]{0, 25, 50, 100, 200, 400, 1000}) {
                int nox = maxHit(33149, 107, bonus, level);
                int shadow = maxHit(27275, 100, bonus, level);
                int demon = maxHit(33205, 101, bonus, level);
                assertTrue(shadow > nox, "Shadow must exceed Noxious at bonus " + bonus);
                assertTrue(demon > shadow, "Demon X must exceed Shadow at bonus " + bonus);
            }
        }
    }

    @Test void otherSpellsKeepTheirExistingWeaponMultiplier() {
        int base = io.zaryx.content.combat.magic.CombatSpellData.getBaseDamage(
                io.zaryx.content.combat.magic.CombatSpellData.MAGIC_SPELLS[0]);
        assertEquals((int) Math.floor(base * 2.84), maxHit(27275, 0, 0, 99));
        assertEquals((int) Math.floor(base * 2.89), maxHit(33205, 0, 0, 99));
    }

    @Test void representativeMaximumHitsPreserveNoxiousAndImproveUpgrades() {
        assertEquals(82, maxHit(33149, 107, 0, 99));
        assertEquals(88, maxHit(27275, 100, 0, 99));
        assertEquals(96, maxHit(33205, 101, 0, 99));
        assertEquals(142, maxHit(33149, 107, 100, 99));
        assertEquals(151, maxHit(27275, 100, 100, 99));
        assertEquals(162, maxHit(33205, 101, 100, 99));
    }
}
