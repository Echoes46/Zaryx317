package io.zaryx.content.skills.slayer;
import com.google.gson.*;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class SlayerTaskAuditTest {
 @Test void everyTaskHasRealMatchingMonstersAndValidDestination() throws Exception {
  JsonObject definitions=JsonParser.parseString(Files.readString(Path.of("etc/cfg/npc/npc_definitions.json"))).getAsJsonObject();
  List<String> npcNames=new ArrayList<>();
  for(Map.Entry<String,JsonElement> entry:definitions.entrySet())if(entry.getValue().getAsJsonObject().has("name")) npcNames.add(entry.getValue().getAsJsonObject().get("name").getAsString());
  int checked=0;
  for(SlayerMaster master:SlayerMaster.MASTERS)for(Task task:master.getAvailable()) {
   checked++;assertTrue(task.hasTeleport(),master.getId()+" "+task);
   assertTrue(npcNames.stream().anyMatch(task::matches),master.getId()+" "+task+" has no matching NPC");
   assertFalse(task.matches("unrelated npc"));
   for(int i=0;i<task.getNames().length;i++) {
    String alias=task.getNames()[i];assertFalse(alias.contains(","),task.toString());assertFalse(alias.matches("[0-9]+"),task.toString());
    if(i>0)assertTrue(npcNames.stream().anyMatch(n->n.equalsIgnoreCase(alias)),"Unknown alias "+alias);
   }
   Task restored=SlayerMaster.get(master.getId(),task.getPrimaryName()).orElseThrow();
   assertArrayEquals(task.getNames(),restored.getNames(),master.getId()+" "+task);
   assertArrayEquals(task.getTeleportLocation(),restored.getTeleportLocation(),master.getId()+" "+task);
  }
  assertEquals(145,checked);
 }
 @Test void revenantFamilyIncludesImpAndHellhoundButNotOtherImps() {
  Task t=SlayerMaster.get(7663,"revenant").orElseThrow();
  assertTrue(t.matches("Revenant imp"));assertTrue(t.matches("Revenant hellhound"));assertFalse(t.matches("Imp"));
  assertFalse(SlayerMaster.get(6797,"dagannoth kings").orElseThrow().matches("Dagannoth mother"));
 }
 @Test void invalidTeleportRecordsAreRejectedBeforeIndexing() {
  for(String value:new String[]{"null","[]","[1]","[-1,-1,-1]","[0,3000,0]","[3000,3000,4]"})
   assertFalse(new Gson().fromJson("{\"teleport\":"+value+"}",Task.class).hasTeleport());
 }
}
