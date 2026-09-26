package io.zaryx.model.entity.npc.drops;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class NomadDropsTest {
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
    void configuredBossCurrencyOnlyAppearsInTheDedicatedRareTable() throws Exception {
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
                    } else {
                        assertFalse(currencies.contains(id), file.toString());
                    }
                }
            }
        }
        assertEquals(63, affected);
    }
}
