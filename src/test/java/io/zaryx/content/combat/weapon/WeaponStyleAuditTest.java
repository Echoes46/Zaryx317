package io.zaryx.content.combat.weapon;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.entity.player.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WeaponStyleAuditTest {
    @Test void completeWeaponSlotAuditMatchesRuntimeResolution() throws Exception {
        com.google.gson.JsonArray audit;
        try (java.io.Reader reader = java.nio.file.Files.newBufferedReader(java.nio.file.Paths.get("docs/weapon-style-audit.json"))) {
            audit = com.google.gson.JsonParser.parseReader(reader).getAsJsonArray();
        }
        com.google.gson.JsonObject stats;
        try (java.io.Reader reader = java.nio.file.Files.newBufferedReader(java.nio.file.Paths.get("etc/cfg/item/item_stats.json"))) {
            stats = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
        }
        java.util.Set<Integer> covered = new java.util.HashSet<>();
        for (com.google.gson.JsonElement entry : audit) {
            com.google.gson.JsonObject row = entry.getAsJsonObject();
            int id = row.get("id").getAsInt();
            assertTrue(covered.add(id), "Duplicate audit ID " + id);
            assertEquals(row.get("style").getAsString(), WeaponData.forItemId(id).name(), "item " + id);
        }
        for (java.util.Map.Entry<String, com.google.gson.JsonElement> entry : stats.entrySet()) {
            com.google.gson.JsonObject item = entry.getValue().getAsJsonObject();
            if (item.has("equipment") && item.getAsJsonObject("equipment").has("slot")
                    && item.getAsJsonObject("equipment").get("slot").getAsInt() == 3)
                assertTrue(covered.contains(Integer.parseInt(entry.getKey())), "New weapon needs style review: " + entry.getKey());
        }
    }
    @Test void ordinaryAndCustomVariantsUseTheirWeaponFamily() {
        int[][] cases = {{1375, 6589, 4886, 4889}, {1311, 6609, 20555},
                {1379, 1389, 1391, 4862, 4865, 27275, 33169, 33433},
                {4212, 4934, 4937, 23856, 33163, 33434, 33435},
                {3098, 13652, 33808}, {12797, 25376}, {3170, 4910, 22734},
                {8872, 8874}, {13093, 13101, 29796, 33442}, {25849, 22810, 33177}};
        WeaponData[] types = {WeaponData.BATTLEAXE, WeaponData.CRUSH_SWORD, WeaponData.STAFF,
                WeaponData.BOW, WeaponData.CLAWS, WeaponData.PICKAXE, WeaponData.SPEAR,
                WeaponData.STAB_SWORD, WeaponData.HALBERD, WeaponData.THROWN};
        for (int i = 0; i < cases.length; i++) for (int id : cases[i])
            assertEquals(types[i], WeaponData.forItemId(id), "item " + id);
        assertEquals(WeaponData.UNARMED, WeaponData.forItemId(-1));
        assertEquals(WeaponData.UNARMED, WeaponData.forItemId(2460)); // Flowers are not a combat weapon.
    }
    @Test void styleSelectionStaysValidWhenSwitchingWeaponFamilies() throws Exception {
        java.lang.reflect.Field config = Server.class.getDeclaredField("configuration");
        config.setAccessible(true);
        Object previous = config.get(null); config.set(null, ServerConfiguration.getDefault());
        try {
            Player p = new Player(null);
            p.playerEquipment[Player.playerWeapon] = 1311;
            p.playerEquipmentN[Player.playerWeapon] = 1;
            p.getCombatConfigs().setAttackStyle(3);
            assertEquals(AttackStyle.DEFENSIVE, p.getCombatConfigs().getWeaponMode().getAttackStyle());
            p.playerEquipment[Player.playerWeapon] = 33163;
            p.getCombatConfigs().updateWeapon();
            assertEquals(2, p.getCombatConfigs().getAttackStyle());
            assertEquals(CombatStyle.RANGE, p.getCombatConfigs().getWeaponMode().getCombatStyle());
            p.getCombatConfigs().setAttackStyle(-1);
            assertEquals(0, p.getCombatConfigs().getAttackStyle());
        } finally { config.set(null, previous); }
    }
}
