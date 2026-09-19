package io.zaryx.model.entity.npc.drops;

import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.ItemConstants;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DropRateTest {

    @Test
    void oneHundredPercentBonusIsNotReducedByTheLootRoller() {
        assertEquals(2.0, TableGroup.effectiveMultiplier(TablePolicy.RARE, 2.0));
        assertEquals(2.0, TableGroup.effectiveMultiplier(TablePolicy.RARE, 5.0));
        assertEquals(50, DropManager.applyModifierToDenominator(100, 1.0));
    }

    @Test
    void viewerRateIncludesTableSelectionAndMatchesRarityScaling() {
        Table rare = table(TablePolicy.RARE, 4, 5);
        assertEquals(20, TableGroup.individualDropDenominator(rare, 1.0));
        assertEquals(10, TableGroup.individualDropDenominator(rare, 2.0));

        Table common = table(TablePolicy.COMMON, 4, 5);
        assertEquals(14, TableGroup.individualDropDenominator(common, 2.0));
        assertEquals(5, TableGroup.individualDropDenominator(table(TablePolicy.COMMON, 1, 5), 2.0));

        Table veryRare = table(TablePolicy.VERY_RARE, 4, 5);
        assertEquals(9, TableGroup.individualDropDenominator(veryRare, 2.0));
    }

    @Test
    void everyConstantEntryIsAwarded() {
        TableGroup group = new TableGroup(Collections.singletonList(1));
        Table constants = new Table(TablePolicy.CONSTANT, 0);
        constants.add(new Drop(Collections.singletonList(1), 100, 1, 1));
        constants.add(new Drop(Collections.singletonList(1), 200, 2, 2));
        group.add(constants);

        List<GameItem> drops = group.access(null, null, 1.0, 1, 1);
        assertEquals(2, drops.size());
        assertEquals(100, drops.get(0).getId());
        assertEquals(200, drops.get(1).getId());
    }

    @Test
    void spongesHatReachesTheDisplayedOneHundredPercentCap() throws Exception {
        ServerConfiguration previous = Server.getConfiguration();
        if (previous == null) Server.setConfiguration(ServerConfiguration.getDefault());
        long previousWogw = io.zaryx.content.wogw.Wogw._20_PERCENT_DROP_RATE_TIMER;
        long previousIasor = io.zaryx.content.bosses.hespori.Hespori.IASOR_TIMER;
        try {
            io.zaryx.content.wogw.Wogw._20_PERCENT_DROP_RATE_TIMER = 0;
            io.zaryx.content.bosses.hespori.Hespori.IASOR_TIMER = 0;
            Player player = new Player(null);
            player.getPerkSytem().gameItems.clear();
            player.playerEquipment[Player.playerHat] = 33238;
            player.playerEquipmentN[Player.playerHat] = 1;
            assertEquals(1.0, DropManager.getModifier(player));
        } finally {
            io.zaryx.content.wogw.Wogw._20_PERCENT_DROP_RATE_TIMER = previousWogw;
            io.zaryx.content.bosses.hespori.Hespori.IASOR_TIMER = previousIasor;
            Field configuration = Server.class.getDeclaredField("configuration");
            configuration.setAccessible(true);
            configuration.set(null, previous);
        }
    }

    @Test
    void everyConfiguredDropTablePassesTheRuntimeLoaderValidation() throws Exception {
        DropManager manager = new DropManager();
        ItemConstants itemConstants = new ItemConstants().load();
        Method read = DropManager.class.getDeclaredMethod("readFromFile", File.class, ItemConstants.class);
        read.setAccessible(true);
        List<Path> files;
        try (java.util.stream.Stream<Path> paths = Files.walk(Path.of("etc", "cfg", "drops"))) {
            files = paths.filter(path -> path.toString().endsWith(".yml")).collect(Collectors.toList());
        }
        assertTrue(files.size() >= 280);
        for (Path file : files) {
            read.invoke(manager, file.toFile(), itemConstants);
        }
    }

    private static Table table(TablePolicy policy, int accessibility, int entries) {
        Table table = new Table(policy, accessibility);
        for (int i = 0; i < entries; i++) {
            table.add(new Drop(Collections.singletonList(1), 100 + i, 1, 1));
        }
        return table;
    }
}
