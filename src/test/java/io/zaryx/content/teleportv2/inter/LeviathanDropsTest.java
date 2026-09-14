package io.zaryx.content.teleportv2.inter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.zaryx.model.entity.npc.drops.DropManager;
import io.zaryx.util.ItemConstants;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class LeviathanDropsTest {
 @Test void liveDropLoaderExposesLeviathanLootToTeleportPreview() throws Exception {
  DropManager manager=new DropManager();
  java.lang.reflect.Method read=DropManager.class.getDeclaredMethod("readFromFile",File.class,ItemConstants.class);
  read.setAccessible(true);
  read.invoke(manager,new File("etc/cfg/drops/leviathan.yml"),new ItemConstants());
  Set<Integer> items=manager.getAllNPCdrops(TeleportInterface.BOSSES.THE_LEVIATHAN.getNpcId())
   .stream().map(i->i.getId()).collect(Collectors.toSet());
  assertTrue(items.containsAll(Arrays.asList(33237,892,9144,11212,33144,33145,33146)));
  assertEquals(23,items.size());
  assertTrue(manager.getAllNPCdrops(11214).isEmpty());
 }
 @Test void currencyAndTierRatesMatchDukeAndAmountsAreValid() throws Exception {
  ObjectMapper mapper=new ObjectMapper(new YAMLFactory());
  JsonNode levi=mapper.readTree(new File("etc/cfg/drops/leviathan.yml"));
  JsonNode duke=mapper.readTree(new File("etc/cfg/drops/duke.yml"));
  assertEquals(12214,levi.get("npc_id").asInt());
  assertEquals(duke.get("constant"),levi.get("constant"));
  Set<Integer> ids=new HashSet<>();
  for(String tier:Arrays.asList("constant","common","uncommon","rare","very_rare")) {
   assertEquals(duke.get(tier).get("accessibility"),levi.get(tier).get("accessibility"));
   for(JsonNode item:levi.get(tier).get("items")) {
    assertTrue(ids.add(item.get("item").asInt()),"Duplicate loot entry");
    assertTrue(item.get("minimum").asInt()>0);
    assertTrue(item.get("maximum").asInt()>=item.get("minimum").asInt());
   }
  }
 }
}
