package io.zaryx.model.collisionmap;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class WalkableTilesTest {
 @Test void dukeRangeEqualsAllSixIndividualTiles() {
  List<String> singles=new ArrayList<>();
  for(int x=3038;x<=3040;x++)for(int y=6415;y<=6416;y++)singles.add(x+" "+y+" 0 walkable");
  assertEquals(WalkableTiles.parse(singles),WalkableTiles.parse(List.of("3038-3040\t6415-6416\t0\twalkable")));
  assertEquals(6,WalkableTiles.parse(singles).size());
 }
 @Test void commentsWhitespaceAndReversedRangesWork() {
  assertEquals(WalkableTiles.parse(List.of("3038-3040 6415-6416 0 walkable")),
   WalkableTiles.parse(List.of("// heading","", " 3040-3038   6416-6415 0 WALKABLE // stairs")));
 }
 @Test void individualEditsPersistAndOverrideEarlierGroups() {
  var t=new WalkableTiles.Tile(3039,6415,0);
  List<String> lines=List.of("3038-3040 6415-6416 0 walkable");
  lines=WalkableTiles.withOverride(lines,t,false);assertFalse(WalkableTiles.parse(lines).get(t));
  lines=WalkableTiles.withOverride(lines,t,true);assertTrue(WalkableTiles.parse(lines).get(t));
  assertTrue(lines.get(lines.size()-1).endsWith("walkable"));
  assertEquals(6,WalkableTiles.parse(lines).size());
 }
 @Test void invalidLinesReportTheirLocation() {
  Exception e=assertThrows(IllegalArgumentException.class,()->WalkableTiles.parse(List.of("// note","1-2 3 0 walkabl")));
  assertTrue(e.getMessage().contains("line 2"));
  assertThrows(IllegalArgumentException.class,()->WalkableTiles.parse(List.of("1 2 4 walkable")));
 }
 @Test void clientUpdatesStayOnTheCorrectPlaneAndLoadedMap() {
  var tile=new WalkableTiles.Tile(3038,6415,0);
  assertTrue(WalkableTiles.visible(tile,0,3000,6400));
  assertFalse(WalkableTiles.visible(tile,1,3000,6400));
  assertFalse(WalkableTiles.visible(tile,0,2000,3000));
 }
 @Test void groupedServerCollisionMatchesIndividualOverridesAndPreservesBlockedException() throws Exception {
  ObjectDef.loadConfig();
  var grouped=WalkableTiles.parse(List.of("3038-3040 6415-6416 0 walkable","3039 6415 0 blocked"));
  List<String> singles=new ArrayList<>();
  for(int x=3038;x<=3040;x++)for(int y=6415;y<=6416;y++)singles.add(x+" "+y+" 0 walkable");
  singles.add("3039 6415 0 blocked");
  RegionProvider a=new RegionProvider(),b=new RegionProvider();
  Region ar=new Region(a,12132,false),br=new Region(b,12132,false);a.add(ar);b.add(br);
  for(int x=3038;x<=3040;x++)for(int y=6415;y<=6416;y++) {ar.addClip(x,y,0,0x200100);br.addClip(x,y,0,0x200100);}
  WalkableTiles.applyCollision(grouped,a);WalkableTiles.applyCollision(WalkableTiles.parse(singles),b);
  for(int x=3038;x<=3040;x++)for(int y=6415;y<=6416;y++) {
   assertEquals(ar.getClip(x,y,0),br.getClip(x,y,0));
   if(x==3039 && y==6415)assertNotEquals(0,ar.getClip(x,y,0));else assertEquals(0,ar.getClip(x,y,0));
  }
 }
}
