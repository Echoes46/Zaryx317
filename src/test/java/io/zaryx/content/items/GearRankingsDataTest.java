package io.zaryx.content.items;

import com.google.gson.GsonBuilder;
import io.zaryx.model.definitions.*;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class GearRankingsDataTest {
    @Test void everyLiveDataCategoryHasTenRankedItems() throws Exception {
        Field field = ItemDef.class.getDeclaredField("definitions"); field.setAccessible(true);
        Object oldDefinitions = field.get(null);
        Map<Integer, ItemStats> oldStats = ItemStats.itemStatsMap;
        try {
            ItemDef.load(); ItemStats.load();
            Map<String,Object> report = new LinkedHashMap<>();
            for (GearRankings.Style style : GearRankings.Style.values()) {
                for (GearRankings.Slot slot : GearRankings.Slot.values()) {
                    List<GearRankings.Entry> entries = GearRankings.top(style, slot);
                    assertEquals(10, entries.size(), style + " / " + slot);
                    List<Object> rows = new ArrayList<>();
                    double last = Double.POSITIVE_INFINITY;
                    for (int i=0;i<entries.size();i++) {
                        GearRankings.Entry e=entries.get(i);
                        assertTrue(e.score<=last);last=e.score;
                        Map<String,Object> row=new LinkedHashMap<>();
                        row.put("id",e.id);row.put("name",e.name);row.put("score",e.score);
                        row.put("lines",TopEquipment.lines(e,style,i+1));rows.add(row);
                    }
                    report.put(style + "/" + slot, rows);
                }
            }
            Path output = Paths.get("build-test-world/reports/top-equipment.json");
            Files.createDirectories(output.getParent());
            Files.writeString(output,new GsonBuilder().setPrettyPrinting().create().toJson(report));
        } finally {field.set(null,oldDefinitions);ItemStats.itemStatsMap=oldStats;}
    }
}
