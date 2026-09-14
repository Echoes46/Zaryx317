package io.zaryx.content.teleportv2.inter;
import io.zaryx.content.teleportv2.inter.TeleportInterface.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class TeleportGuideTest {
 @Test void allGuidesFitAvailableRows() throws Exception {
  java.lang.reflect.Field config=io.zaryx.Server.class.getDeclaredField("configuration");
  config.setAccessible(true);Object old=config.get(null);
  try {
   config.set(null,io.zaryx.ServerConfiguration.getDefault());
   io.zaryx.model.entity.player.Player p=new io.zaryx.model.entity.player.Player(null);
   for(int tab=0;tab<6;tab++)for(Teleport t:TeleportInterface.destinations(tab)) {
    java.util.List<String> lines=TeleportGuide.lines(p,t);
    assertTrue(lines.size()<=48,TeleportGuide.name(t));
    for(String line:lines)assertTrue(line.replaceAll("@...@", "").length()<=45,line);
   }
  } finally {config.set(null,old);}
 }
 @Test void everyDestinationHasReadableNameAndWrappedAccessAdvice() {
  for(int tab=0;tab<6;tab++) for(Teleport t:TeleportInterface.destinations(tab)) {
   assertFalse(TeleportGuide.name(t).contains("<"));
   assertNotNull(t.getPosition());
   for(String line:TeleportGuide.wrap(TeleportGuide.access(t)))assertTrue(line.length()<=45,line);
  }
 }
 @Test void collectionAliasesMatchBossAndRaidEntries() {
  assertEquals(7145,TeleportGuide.collectionNpc(BOSSES.DEMONIC_GORILLA));
  assertEquals(2042,TeleportGuide.collectionNpc(BOSSES.ZULRAH));
  assertEquals(7554,TeleportGuide.collectionNpc(MINIGAMES.COX));
  assertEquals(8360,TeleportGuide.collectionNpc(MINIGAMES.TOB));
 }
 @Test void travelOnlyDestinationsDoNotAdvertiseUnrelatedNpcLoot() {
  for(Teleport t:SKILLING.values())assertEquals(-1,TeleportGuide.dropNpc(t));
  assertEquals(-1,TeleportGuide.dropNpc(MINIGAMES.OUTLAST));
  assertTrue(TeleportGuide.wildernessRisk(PK.KBD));
  assertFalse(TeleportGuide.wildernessRisk(PK.MAGE_BANK));
 }
}
