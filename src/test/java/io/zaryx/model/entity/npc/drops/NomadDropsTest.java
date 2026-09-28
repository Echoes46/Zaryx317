package io.zaryx.model.entity.npc.drops;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.ItemConstants;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class NomadDropsTest {
    @Test
    void barrelchestRuntimeTableContainsTheAdvertisedCertificateRoll() throws Exception {
        DropManager manager = new DropManager();
        Method read = DropManager.class.getDeclaredMethod("readFromDirectory", File.class, ItemConstants.class);
        read.setAccessible(true);
        read.invoke(manager, new File("etc/cfg/drops"), new ItemConstants().load());
        Method lookup = DropManager.class.getDeclaredMethod("groupFor", int.class);
        lookup.setAccessible(true);
        TableGroup group = (TableGroup) ((Optional<?>) lookup.invoke(manager, 6342)).orElseThrow();
        List<Table> currencyTables = group.stream()
                .filter(table -> table.getPolicy() == TablePolicy.NOMAD).collect(Collectors.toList());
        assertEquals(1, currencyTables.size());
        Table table = currencyTables.get(0);
        assertEquals(1, table.size());
        assertEquals(1, table.getSelectionSize(), "No empty selection slots may swallow the reward");
        Drop certificate = table.fetchRandom();
        assertNotNull(certificate);
        assertEquals(TablePolicy.UNCOMMON, certificate.getNomadRarity());
        assertEquals(20, TableGroup.nomadDropDenominator(table, certificate, 1.0));
        assertEquals(12, TableGroup.nomadDropDenominator(table, certificate, 2.0));
        assertEquals(691, certificate.getItemId());
        assertEquals(1, certificate.getMinimumAmount());
        assertEquals(25, certificate.getMaximumAmount());
    }

    @Test
    void mixedCertificateTableGivesEachDenominationItsOwnRateWithoutExtraSelectionPenalty() {
        int[] ids = {691, 692, 693, 696, 33428, 33429};
        int[] rates = {20, 40, 60, 100, 250, 500};
        Table table = new Table(TablePolicy.NOMAD, 100);
        for (int i = 0; i < ids.length; i++) {
            Drop drop = new Drop(Collections.singletonList(1), ids[i], 1, 25);
            drop.setNomadRate(rates[i], i < 3 ? TablePolicy.UNCOMMON : i == 3 ? TablePolicy.RARE : TablePolicy.VERY_RARE);
            table.add(drop);
            assertEquals(rates[i], TableGroup.nomadDropDenominator(table, drop, 1.0));
        }
        for (double modifier : new double[]{1.0, 2.0}) {
            int[] counts = new int[ids.length];
            for (int roll = 0; roll < 120000; roll++) {
                Drop selected = TableGroup.selectNomadDrop(table, modifier, (roll + 0.5) / 120000.0);
                if (selected != null) counts[table.indexOf(selected)]++;
            }
            for (int i = 0; i < ids.length; i++) {
                double multiplier = modifier == 1.0 ? 1.0 : i < 3 ? 1.75 : i == 3 ? 2.0 : 2.25;
                assertEquals((int) Math.round(120000.0 * multiplier / rates[i]), counts[i]);
            }
            assertNull(TableGroup.selectNomadDrop(table, modifier, 0.99999));
        }
    }

    @Test
    void doublingAndExtraLootRollsCannotExceedCurrencyCaps() throws Exception {
        ServerConfiguration previous = Server.getConfiguration();
        if (previous == null) Server.setConfiguration(ServerConfiguration.getDefault());
        try {
            Player player = new Player(null);
            player.doubleDropRate = 1;
            for (int id : new int[]{691, 692, 693, 696, 33428, 33429, 33237}) {
                int cap = id == 33237 ? 2 : 25;
                TableGroup group = new TableGroup(Collections.singletonList(11278));
                Table table = new Table(TablePolicy.NOMAD, 1);
                table.add(new Drop(Collections.singletonList(11278), id, cap, cap));
                group.add(table);
                List<GameItem> rewards = group.access(player, null, 2.0, 10, 11278);
                assertEquals(1, rewards.size());
                assertEquals(cap, rewards.get(0).getAmount());
                assertTrue(group.access(player, null, 2.0, 0, 11278).isEmpty());
            }
        } finally {
            Field configuration = Server.class.getDeclaredField("configuration");
            configuration.setAccessible(true);
            configuration.set(null, previous);
        }
    }

    @Test
    void viewerUsesRareBonusAndPreservesVacatedSlotOdds() {
        Table table = new Table(TablePolicy.COMMON, 4, 5);
        table.add(new Drop(Collections.singletonList(1), 100, 1, 1));
        assertEquals(20, TableGroup.individualDropDenominator(table, 1.0));
        Table nomad = new Table(TablePolicy.NOMAD, 100);
        nomad.add(new Drop(Collections.singletonList(1), 691, 1, 25));
        nomad.add(new Drop(Collections.singletonList(1), 696, 1, 25));
        assertEquals(200, TableGroup.individualDropDenominator(nomad, 1.0));
        assertEquals(100, TableGroup.individualDropDenominator(nomad, 2.0));
    }

    @Test
    void configuredBossCurrencyOnlyAppearsInTheDedicatedTableWithCorrectDenominationRates() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        List<Path> files;
        try (java.util.stream.Stream<Path> paths = Files.list(Path.of("etc/cfg/drops"))) {
            files = paths.filter(p -> p.toString().endsWith(".yml")).collect(Collectors.toList());
        }
        int affected = 0;
        Set<Integer> currencies = Set.of(691, 692, 693, 696, 33428, 33429, 33237);
        for (Path file : files) {
            JsonNode config = mapper.readTree(file.toFile());
            if (!config.has("nomad")) continue;
            affected++;
            Set<Integer> seen = new HashSet<>();
            assertTrue(config.get("nomad").get("accessibility").asInt() >= 100, file.toString());
            for (TablePolicy policy : TablePolicy.values()) {
                JsonNode table = config.get(policy.name().toLowerCase());
                if (table == null) continue;
                for (JsonNode item : table.get("items")) {
                    int id = item.path("item").asInt(-1);
                    if (policy == TablePolicy.NOMAD) {
                        assertTrue(currencies.contains(id), file.toString());
                        assertTrue(seen.add(id), file.toString());
                        assertEquals(1, item.get("minimum").asInt());
                        assertEquals(id == 33237 ? 2 : 25, item.get("maximum").asInt());
                        if (id != 33237) {
                            int expected = id == 691 ? 20 : id == 692 ? 40 : id == 693 ? 60
                                    : id == 696 ? 100 : id == 33428 ? 250 : 500;
                            assertEquals(expected, item.path("chance").asInt(), file.toString());
                            assertEquals(id == 691 || id == 692 || id == 693 ? "uncommon"
                                    : id == 696 ? "rare" : "very_rare", item.path("rarity").asText(), file.toString());
                        }
                    } else {
                        assertFalse(currencies.contains(id), file.toString());
                    }
                }
            }
        }
        assertEquals(63, affected);
    }
}
