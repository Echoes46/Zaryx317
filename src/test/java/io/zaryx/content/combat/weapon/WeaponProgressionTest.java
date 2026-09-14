package io.zaryx.content.combat.weapon;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.content.combat.formula.rework.*;
import io.zaryx.content.upgrade.UpgradeMaterials;
import io.zaryx.model.entity.npc.stats.NpcCombatSkill;
import io.zaryx.model.Bonus;
import io.zaryx.model.CombatType;
import io.zaryx.content.combat.Damage;
import io.zaryx.content.combat.core.HitDispatcher;
import io.zaryx.content.combat.specials.Special;
import io.zaryx.content.combat.specials.Specials;
import io.zaryx.model.definitions.*;
import io.zaryx.model.entity.npc.NPC;
import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class WeaponProgressionTest {
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
        oldStats = ItemStats.itemStatsMap; ItemStats.load();
        for (int id : ItemStats.itemStatsMap.keySet())
            items.put(id, ItemDef.builder().id(id).name("Weapon " + id).build());
        definitions.set(null, items);
        ticks = Server.class.getDeclaredField("tickCount"); ticks.setAccessible(true);
        oldTicks = ticks.getLong(null); ticks.setLong(null, 100);
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

    Player equipped(int id, int bonus, int level) {
        Player p = player(id, 0);
        Arrays.fill(p.playerLevel, level);
        Arrays.fill(p.playerXP, p.getPA().getXPForLevel(level));
        for (Bonus b : Bonus.values())
            p.playerBonus[b.ordinal()] = ItemStats.forId(id).getEquipment().getBonus(b) + bonus;
        if (WeaponData.forItemId(id) == WeaponData.STAFF) {
            p.usingMagic = true;
            p.setSpellId(id == 33149 ? 107 : id == 27275 ? 100 : id == 33205 ? 101
                    : id == 22323 || id == 25731 ? 98 : 0);
            p.setSangStaffCharge(100);
        }
        return p;
    }
    CombatFormula formula(Player p) {
        CombatStyle style = p.getCombatConfigs().getWeaponMode().getCombatStyle();
        if (p.usingMagic) return MagicCombatFormula.STANDARD;
        if (style == CombatStyle.RANGE) return RangeCombatFormula.STANDARD;
        return new MeleeCombatFormula();
    }
    @Test void allConfiguredCombatWeaponsHaveFiniteDamageAccuracyAndSpeed() {
        int checked = 0;
        for (Map.Entry<Integer, ItemStats> entry : ItemStats.itemStatsMap.entrySet()) {
            int id = entry.getKey();
            if (entry.getValue().getEquipment() == null || entry.getValue().getEquipment().getSlot() != 3
                    || WeaponData.forItemId(id) == WeaponData.UNARMED) continue;
            Player p = equipped(id, 100, 99);
            // Exercise the ordinary formula for each weapon; powered and multi-hit
            // mechanics have separate integration tests below and in SpecialWeaponTest.
            NPC target = npc();
            CombatFormula f = formula(p);
            int max = f.getMaxHit(p, target);
            double accuracy = f.getAccuracy(p, target);
            assertTrue(max >= 0, "Negative max hit: " + id);
            assertTrue(Double.isFinite(accuracy) && accuracy >= 0 && accuracy <= 1, "Invalid accuracy: " + id);
            assertTrue(p.attacking.getAttackDelay() > 0, "Invalid attack delay: " + id);
            checked++;
        }
        assertTrue(checked >= 900, "Inventory coverage unexpectedly dropped: " + checked);
    }

    @Test void scalingSpecialsPreserveCalculatedHitsAndMisses() {
        for (int id : new int[]{33058, 33207, 20484, 33005, 25979, 27287, 33204}) {
            Player p = equipped(id, 100, 99);
            Special special = Specials.forWeaponId(id);
            for (int amount : new int[]{0, 10, 75, 300, 600}) {
                Damage hit = new Damage(amount);
                special.activate(p, npc(), hit);
                assertEquals(amount, hit.getAmount(), "Special replaced the combat roll for " + id);
            }
        }
    }

    @Test void rangedSpecialsStillRespectImmuneNpcTargets() {
        NPC immune = new NPC(2, 8355, NpcDef.builder().name("Immune").build(),
                NpcStats.builder().setName("Immune").setHitpoints(100).createNpcStats());
        for (int id : new int[]{33058, 33207, 20484, 33005}) {
            Damage hit = new Damage(200);
            Specials.forWeaponId(id).activate(equipped(id, 100, 99), immune, hit);
            assertEquals(0, hit.getAmount());
        }
    }

    @Test void bowDispatcherDoesNotApplyHiddenMultipliersAgain() {
        for (int id : new int[]{20997, 33058, 33207, 20484}) {
            Player p = equipped(id, 100, 99);
            p.weaponUsedOnAttack = id;
            NPC target = npc();
            target.getCombatDefinition().setLevel(NpcCombatSkill.MAGIC, 250);
            int expected = RangeCombatFormula.STANDARD.getMaxHit(p, target);
            final int[] dispatchedMax = {-1};
            new HitDispatcher(p, target) {
                public void addCombatXP(CombatType type, int amount) { }
                public void beforeDamageCalculated(CombatType type) { dispatchedMax[0] = maximumDamage; }
                public void afterDamageCalculated(CombatType type, boolean hit) { }
            }.playerHitEntity(CombatType.RANGE, null);
            assertEquals(expected, dispatchedMax[0], "Hidden multiplier for bow " + id);
        }
    }

    @Test void upgradedBowSpecialsScaleAboveNormalHitsAtHighTargetMagic() {
        for (int id : new int[]{33058, 33207, 20484}) for (int magic : new int[]{1, 100, 250, 350}) {
            Player p = equipped(id, 100, 99);
            NPC target = npc();
            target.getCombatDefinition().setLevel(NpcCombatSkill.MAGIC, magic);
            Special special = Specials.forWeaponId(id);
            int normal = formula(p).getMaxHit(p, target);
            int spec = formula(p).getMaxHit(p, target, special.getDamageModifier(), 1);
            assertTrue(spec >= normal * 2, "Special loses target scaling for " + id);
        }
    }

    @Test void demonBowSpecialRetainsSerenModifiersAndCostsLess() {
        Special seren = Specials.forWeaponId(33058), demon = Specials.forWeaponId(33207);
        assertTrue(demon.getAccuracy() >= seren.getAccuracy());
        assertTrue(demon.getDamageModifier() >= seren.getDamageModifier());
        assertTrue(demon.getRequiredCost() < seren.getRequiredCost());
    }

    @Test void vestaAndStatiusSpecialsBoostRatherThanReduceDamage() {
        for (int id : new int[]{22613, 22622}) for (int bonus : new int[]{0, 100, 400}) {
            Player p = equipped(id, bonus, 99);
            Special special = Specials.forWeaponId(id);
            NPC target = npc();
            int normal = formula(p).getMaxHit(p, target);
            int boosted = formula(p).getMaxHit(p, target, special.getDamageModifier(), 1);
            assertTrue(boosted > normal, "Special reduces damage for " + id);
        }
    }

    @Test void ascensionExtraHitsRemainRanged() {
        for (int id : new int[]{33206, 26269}) {
            Player p = equipped(id, 100, 99);
            Specials.forWeaponId(id).activate(p, npc(), new Damage(80));
            assertEquals(3, p.getDamageQueue().getQueue().size());
            for (Damage hit : p.getDamageQueue().getQueue()) assertEquals(CombatType.RANGE, hit.getCombatType());
        }
    }

    @Test void standardMaterialTiersAndSpearTiersDoNotReverse() throws Exception {
        com.google.gson.JsonObject stats = com.google.gson.JsonParser.parseString(
                java.nio.file.Files.readString(java.nio.file.Path.of("etc/cfg/item/item_stats.json"))).getAsJsonObject();
        Map<String, Integer> ids = new HashMap<>();
        stats.entrySet().forEach(entry -> {
            com.google.gson.JsonObject row = entry.getValue().getAsJsonObject();
            if (row.has("equipment") && row.getAsJsonObject("equipment").has("slot")
                    && row.getAsJsonObject("equipment").get("slot").getAsInt() == 3)
                ids.merge(row.get("name").getAsString().toLowerCase(Locale.ROOT), Integer.parseInt(entry.getKey()), Math::min);
        });
        List<int[]> pairs = new ArrayList<>();
        for (String family : new String[]{"dagger", "sword", "longsword", "scimitar", "2h sword", "mace",
                "warhammer", "battleaxe", "spear", "halberd", "claws", "dart", "knife", "thrownaxe", "crossbow"}) {
            Integer previous = null;
            for (String metal : new String[]{"bronze", "iron", "steel", "black", "mithril", "adamant", "rune", "dragon"}) {
                Integer id = ids.get(metal + " " + family);
                if (id == null) continue;
                if (previous != null) pairs.add(new int[]{previous, id});
                previous = id;
            }
        }
        pairs.add(new int[]{25979, 27287});
        pairs.add(new int[]{27287, 33204});
        List<String> problems = new ArrayList<>();
        for (int[] pair : pairs) for (int bonus : new int[]{0, 100, 400}) {
            Player a = equipped(pair[0], bonus, 99), b = equipped(pair[1], bonus, 99);
            NPC target = npc();
            int x = formula(a).getMaxHit(a, target), y = formula(b).getMaxHit(b, target);
            if (y < x || formula(b).getAccuracy(b, target) < formula(a).getAccuracy(a, target)
                    || b.attacking.getAttackDelay() > a.attacking.getAttackDelay())
                problems.add(pair[0] + " -> " + pair[1] + " bonus=" + bonus + " max=" + x + "->" + y);
        }
        assertTrue(pairs.size() >= 100);
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test void everyWeaponRecipeRetainsDamageAccuracyAndSpeed() {
        List<String> problems = new ArrayList<>();
        for (UpgradeMaterials recipe : UpgradeMaterials.values()) {
            int a = recipe.getRequired().getId(), b = recipe.getReward().getId();
            if (ItemStats.forId(a).getEquipment() == null || ItemStats.forId(a).getEquipment().getSlot() != 3) continue;
            for (int bonus : new int[]{0, 100, 400}) for (int level : new int[]{75, 99, 120})
                for (int magic : new int[]{1, 100, 250, 350}) {
                    Player before = equipped(a, bonus, level), after = equipped(b, bonus, level);
                    NPC target = npc();
                    target.getCombatDefinition().setLevel(NpcCombatSkill.MAGIC, magic);
                    target.getCombatDefinition().setLevel(NpcCombatSkill.DEFENCE, 200);
                    CombatFormula f = formula(before);
                    int x = f.getMaxHit(before, target), y = formula(after).getMaxHit(after, target);
                    double ax = f.getAccuracy(before, target), ay = formula(after).getAccuracy(after, target);
                    if (y < x || ay + 0.000001 < ax || after.attacking.getAttackDelay() > before.attacking.getAttackDelay())
                        problems.add(recipe.name() + " bonus=" + bonus + " level=" + level + " targetMagic=" + magic
                                + " max=" + x + "->" + y + " accuracy=" + ax + "->" + ay);
                }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }
}
