package io.zaryx.content.combat.weapon;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.content.combat.Hitmark;
import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.core.HitDispatcher;
import io.zaryx.model.CombatType;
import io.zaryx.model.definitions.*;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import io.zaryx.util.Misc;
import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class SpecialWeaponTest {
    Field configuration, definitions, ticks;
    Object oldConfig, oldDefinitions;
    long oldTicks;
    Map<Integer, ItemStats> oldStats;
    @BeforeEach void setup() throws Exception {
        configuration = Server.class.getDeclaredField("configuration"); configuration.setAccessible(true);
        oldConfig = configuration.get(null); configuration.set(null, ServerConfiguration.getDefault());
        definitions = ItemDef.class.getDeclaredField("definitions"); definitions.setAccessible(true);
        oldDefinitions = definitions.get(null); Map<Integer, ItemDef> items = new HashMap<>();
        for (int id : new int[]{-1,0,21015,28682,25604,28997,10146,10147,10148,10149})
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
    int button(int widget) { return Misc.hexToInt(new byte[]{(byte)(widget >> 8), (byte)widget}, 0, 2); }
    @Test void customButtonsSelectOnlyModesForEquippedWeapon() {
        Player p = player(21015, 0);
        assertTrue(p.attacking.clickWeaponTabButton(button(61406)));
        assertTrue(SpecialWeaponRules.blocking(p));
        assertFalse(p.attacking.clickWeaponTabButton(button(61437)));
        assertTrue(p.attacking.clickWeaponTabButton(button(61423)));
        assertEquals(1, p.autoRet);
        assertTrue(p.attacking.clickWeaponTabButton(button(61423)));
        assertEquals(0, p.autoRet);
        assertTrue(p.attacking.clickWeaponTabButton(button(61405)));
        assertEquals(CombatStyle.CRUSH, p.getCombatConfigs().getWeaponMode().getCombatStyle());
        for (int i = 0; i < 3; i++) {
            Player s = player(10148, 0);
            assertTrue(s.attacking.clickWeaponTabButton(button(61435 + i)));
            assertEquals(SpecialWeaponRules.salamanderType(i), s.attacking.getCombatType());
        }
    }
    @Test void blockStopsAttacksAndRequiresEightTicks() throws Exception {
        Player p = player(21015, 1);
        assertFalse(p.attacking.attackEntityCheck(npc(), false));
        assertFalse(SpecialWeaponRules.blockReady(p));
        ticks.setLong(null, 107); assertFalse(SpecialWeaponRules.blockReady(p));
        ticks.setLong(null, 108); assertTrue(SpecialWeaponRules.blockReady(p));
        p.getCombatConfigs().setAttackStyle(0);
        assertFalse(SpecialWeaponRules.blockReady(p));
        assertTrue(p.attackTimer >= 8);
    }
    @Test void swappingBulwarksRestartsProtectionAndUnequippingRemovesIt() throws Exception {
        Player p = player(21015, 1);
        ticks.setLong(null, 108);
        p.playerEquipment[Player.playerWeapon] = 28682;
        p.getCombatConfigs().updateWeapon();
        assertFalse(SpecialWeaponRules.blockReady(p));
        ticks.setLong(null, 116); assertTrue(SpecialWeaponRules.blockReady(p));
        p.playerEquipment[Player.playerWeapon] = 28997;
        p.getCombatConfigs().updateWeapon();
        assertFalse(SpecialWeaponRules.blockReady(p));
        assertEquals(WeaponData.MACE, p.getCombatConfigs().getWeaponData());
    }
    @Test void readyBlockReducesNpcHitsButNotPoisonOrPlayerHits() throws Exception {
        Player p = player(21015, 1); ticks.setLong(null, 108);
        p.getHealth().setMaximumHealth(100); p.getHealth().setCurrentHealth(100);
        p.appendDamage(npc(), 10, Hitmark.HIT);
        assertEquals(92, p.getHealth().getCurrentHealth());
        p.appendDamage(npc(), 10, Hitmark.POISON);
        assertEquals(82, p.getHealth().getCurrentHealth());
        p.appendDamage(player(-1, 0), 10, Hitmark.HIT);
        assertEquals(72, p.getHealth().getCurrentHealth());
    }
    @Test void eachSalamanderRequiresItsOwnTarInEveryStyle() {
        int[] weapons = {10149,10146,10147,10148};
        for (int i = 0; i < weapons.length; i++) for (int mode = 0; mode < 3; mode++) {
            Player p = player(weapons[i], mode);
            assertFalse(p.attacking.attackEntityCheck(npc(), false));
            p.playerEquipment[Player.playerArrows] = 10142 + i;
            p.playerEquipmentN[Player.playerArrows] = 1;
            assertTrue(SpecialWeaponRules.hasTar(p));
            assertEquals(mode == 1 ? 4 : 5, p.attacking.getAttackDelay());
            SpecialWeaponRules.consumeTar(p);
            assertEquals(-1, p.playerEquipment[Player.playerArrows]);
            assertEquals(0, p.playerEquipmentN[Player.playerArrows]);
            assertFalse(p.attacking.attackEntityCheck(npc(), false));
        }
    }
    @Test void magicDamageScalesWithLevelAndDoesNotUseStoredAutocast() {
        assertEquals(24, SpecialWeaponRules.magicMaxHit(10148,99));
        assertEquals(17, SpecialWeaponRules.magicMaxHit(10148,70));
        Player p = player(10148, 2);
        p.autocasting = true; p.autocastId = 0;
        p.playerEquipment[Player.playerArrows] = 10145; p.playerEquipmentN[Player.playerArrows] = 2;
        p.attacking.attackEntityCheck(npc(), false);
        assertEquals(SpecialWeaponRules.SALAMANDER_SPELL, p.getSpellId());
        assertFalse(p.autocasting);
        assertEquals(CombatType.MAGE, p.attacking.getCombatType());
        p.usingClickCast = true;
        assertFalse(SpecialWeaponRules.isSalamanderAttack(p));
    }
    @Test void dualWeaponUsesFourMaceStylesAndIsTwoHanded() {
        Player p = player(28997, 2);
        assertEquals(CombatStyle.STAB, p.getCombatConfigs().getWeaponMode().getCombatStyle());
        assertEquals(AttackStyle.CONTROLLED, p.getCombatConfigs().getWeaponMode().getAttackStyle());
        assertTrue(p.getItems().is2handed("Dual macuahuitl", 28997));
        assertEquals(4, p.attacking.getAttackDelay());
    }
    @Test void dualAttackQueuesTwoHalfHitsAndSecondNpcHitIsDelayed() {
        Player p = player(28997, 0);
        NPC target = npc();
        HitDispatcher dispatcher = new HitDispatcher(p, target) {
            public void addCombatXP(CombatType type, int amount) { }
            public void beforeDamageCalculated(CombatType type) { maximumDamage = 21; maximumAccuracy = 1; }
            public void afterDamageCalculated(CombatType type, boolean hit) { damage = hit ? maximumDamage : 0; }
        };
        dispatcher.playerHitEntity(CombatType.MELEE, null);
        List<Damage> hits = new ArrayList<>(p.getDamageQueue().getQueue());
        assertEquals(2, hits.size());
        assertEquals(10, hits.get(0).getAmount());
        assertEquals(11, hits.get(1).getAmount());
        assertEquals(hits.get(0).getTicks() + 1, hits.get(1).getTicks());
    }
    @Test void dualFirstMissPreventsSecondHitEvenWithSuccessfulSecondRoll() {
        Player p = player(28997, 0);
        Random previous = HitDispatcher.rand;
        HitDispatcher.rand = new Random() {
            int roll;
            @Override public double nextDouble() { return roll++ == 0 ? 0.9 : 0; }
        };
        try {
            new HitDispatcher(p, npc()) {
                public void addCombatXP(CombatType type, int amount) { }
            public void beforeDamageCalculated(CombatType type) { maximumDamage = 21; maximumAccuracy = 0.5; }
                public void afterDamageCalculated(CombatType type, boolean hit) { damage = hit ? maximumDamage : 0; }
            }.playerHitEntity(CombatType.MELEE, null);
            assertEquals(2, p.getDamageQueue().getQueue().size());
            for (Damage hit : p.getDamageQueue().getQueue()) assertEquals(0, hit.getAmount());
        } finally { HitDispatcher.rand = previous; }
    }

    @Test void dualSecondHitHasItsOwnAccuracyRoll() {
        Player p = player(28997, 0);
        Random previous = HitDispatcher.rand;
        HitDispatcher.rand = new Random() {
            int roll;
            @Override public double nextDouble() { return roll++ == 0 ? 0 : 0.9; }
        };
        try {
            new HitDispatcher(p, npc()) {
                public void addCombatXP(CombatType type, int amount) { }
                public void beforeDamageCalculated(CombatType type) { maximumDamage = 21; maximumAccuracy = 0.5; }
                public void afterDamageCalculated(CombatType type, boolean hit) { damage = hit ? maximumDamage : 0; }
            }.playerHitEntity(CombatType.MELEE, null);
            List<Damage> hits = new ArrayList<>(p.getDamageQueue().getQueue());
            assertEquals(10, hits.get(0).getAmount());
            assertEquals(0, hits.get(1).getAmount());
        } finally { HitDispatcher.rand = previous; }
    }
    @Test void salamanderMagicAndRangeUseTheActualTargetWithoutNightmareState() {
        for (int mode : new int[]{1,2}) {
            Player p = player(10148, mode);
            p.playerEquipment[Player.playerArrows] = 10145; p.playerEquipmentN[Player.playerArrows] = 1;
            p.attacking.attackEntityCheck(npc(), false);
            p.oldSpellId = p.getSpellId();
            new HitDispatcher(p, npc()) {
                public void addCombatXP(CombatType type, int amount) { }
                public void beforeDamageCalculated(CombatType type) { maximumAccuracy = 1; }
                public void afterDamageCalculated(CombatType type, boolean hit) { }
            }.playerHitEntity(p.attacking.getCombatType(), null);
            assertEquals(1, p.getDamageQueue().getQueue().size());
        }
    }

}
