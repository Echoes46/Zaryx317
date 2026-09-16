package io.zaryx.content.holiday;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class HolidayLayoutsTest {
 @Test void allEighteenLayoutsAreDistinctStableAndDoNotMutateOtherRounds() throws Exception {
  Gson gson=new Gson();var base=Arrays.stream(gson.fromJson(Files.readString(Path.of("etc/cfg/holiday-events.json")),HolidayEvents.Layout[].class)).filter(l->l.holiday==Holiday.HALLOWEEN).findFirst().orElseThrow();
  String original=gson.toJson(base);Set<String> layouts=new HashSet<>();
  for(int seed=0;seed<18;seed++){
   var layout=HolidayLayouts.round(base,seed);HolidayEvents.validateLayout(layout);
   String encoded=gson.toJson(layout);assertTrue(layouts.add(encoded));assertEquals(encoded,gson.toJson(HolidayLayouts.round(base,seed)));
   Set<String> rooms=new HashSet<>();for(var n:layout.npcs)if(n.role>=0)assertTrue(rooms.add(n.x+","+n.y));
   layout.objects[0].x=1;assertEquals(original,gson.toJson(base));assertEquals(encoded,gson.toJson(HolidayLayouts.round(base,seed)));
  }
 }
}
