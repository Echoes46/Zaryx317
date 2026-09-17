package io.zaryx.content.holiday;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class HolidayLayoutsTest {
 @Test void oldAndExpandedLayoutsAreStableReachableAndVaried() throws Exception {
  Gson gson=new Gson();var base=Arrays.stream(gson.fromJson(Files.readString(Path.of("etc/cfg/holiday-events.json")),HolidayEvents.Layout[].class)).filter(l->l.holiday==Holiday.HALLOWEEN).findFirst().orElseThrow();
  String original=gson.toJson(base);Set<String> layouts=new HashSet<>();
  Map<Integer,Set<String>> ghostSpots=new HashMap<>(),supplySpots=new HashMap<>();
  for(int seed=0;seed<HolidayLayouts.LEGACY_LAYOUTS+HolidayLayouts.NEW_LAYOUTS;seed++){
   var layout=HolidayLayouts.round(base,seed);HolidayEvents.validateLayout(layout);
   String encoded=gson.toJson(layout);
   if(seed==18)assertEquals(gson.toJson(HolidayLayouts.round(base,0)),encoded);
   else assertTrue(layouts.add(encoded),"Repeated layout seed "+seed);
   if(seed>=HolidayLayouts.LEGACY_LAYOUTS){
    for(var n:layout.npcs)if(n.role>=0)ghostSpots.computeIfAbsent(n.id,k->new HashSet<>()).add(n.x+","+n.y);
    for(var station:layout.objects)if(station.role>=0&&station.role<3)supplySpots.computeIfAbsent(station.id,k->new HashSet<>()).add(station.x+","+station.y);
   }assertEquals(encoded,gson.toJson(HolidayLayouts.round(base,seed)));
   Set<String> rooms=new HashSet<>();for(var n:layout.npcs)if(n.role>=0)assertTrue(rooms.add(n.x+","+n.y));
   layout.objects[0].x=1;assertEquals(original,gson.toJson(base));assertEquals(encoded,gson.toJson(HolidayLayouts.round(base,seed)));
  }
  assertEquals(3,ghostSpots.size());assertEquals(3,supplySpots.size());
  ghostSpots.values().forEach(spots->assertTrue(spots.size()>=9,"Ghost has too few possible spots"));
  supplySpots.values().forEach(spots->assertTrue(spots.size()>=9,"Supply has too few possible spots"));
 }
 @Test void christmasHelpersAndSuppliesMoveWithinGarden() throws Exception {
  Gson gson=new Gson();
  var base=Arrays.stream(gson.fromJson(Files.readString(Path.of("etc/cfg/holiday-events.json")),HolidayEvents.Layout[].class))
      .filter(l->l.holiday==Holiday.CHRISTMAS).findFirst().orElseThrow();
  String original=gson.toJson(base);
  Map<Integer,Set<String>> guests=new HashMap<>(),supplies=new HashMap<>();
  Set<String> penguin=new HashSet<>();
  for(int seed=HolidayLayouts.LEGACY_LAYOUTS;seed<HolidayLayouts.LEGACY_LAYOUTS+HolidayLayouts.NEW_LAYOUTS;seed++){
   var layout=HolidayLayouts.round(base,seed);
   HolidayEvents.validateLayout(layout);
   assertEquals(gson.toJson(layout),gson.toJson(HolidayLayouts.round(base,seed)));
   for(var npc:layout.npcs){
    if(npc.role>=0)guests.computeIfAbsent(npc.id,k->new HashSet<>()).add(npc.x+","+npc.y);
    if(npc.role==-2)penguin.add(npc.x+","+npc.y);
    if(npc.role==-1&&!npc.home){assertEquals(2987,npc.x);assertEquals(3382,npc.y);}
   }
   for(var station:layout.objects)if(station.role>=0&&station.role<3)
    supplies.computeIfAbsent(station.id,k->new HashSet<>()).add(station.x+","+station.y);
  }
  assertEquals(3,guests.size());assertEquals(3,supplies.size());
  guests.values().forEach(spots->assertTrue(spots.size()>=9));
  supplies.values().forEach(spots->assertTrue(spots.size()>=9));
  assertEquals(3,penguin.size());
  assertEquals(original,gson.toJson(base));
 }
}
