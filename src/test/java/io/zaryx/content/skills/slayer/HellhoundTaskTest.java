package io.zaryx.content.skills.slayer;
import com.google.gson.*;
import java.nio.file.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class HellhoundTaskTest {
 private Task task(String names) {return new Gson().fromJson("{\"names\":"+names+"}",Task.class);}
 @Test void legacyAssignmentsMatchIndividualMonsters() {
  Task t=task("[\"hellhound, revenant hellhound, Revenant hellhound\"]");
  assertEquals("hellhound",t.getPrimaryName());assertTrue(t.matches("Hellhound"));assertTrue(t.matches("Revenant hellhound"));
  assertFalse(t.matches("Revenant imp"));assertFalse(t.matches("Skeleton Hellhound"));assertFalse(t.matches(null));
 }
 @Test void everyConfiguredHellhoundTaskMatchesHellhoundExactly() throws Exception {
  JsonArray masters=JsonParser.parseString(Files.readString(Path.of("etc/cfg/slayer_masters.json"))).getAsJsonArray();int found=0;
  for(JsonElement master:masters)for(JsonElement entry:master.getAsJsonObject().getAsJsonArray("available")) {
   Task t=new Gson().fromJson(entry,Task.class);
   if(!t.getPrimaryName().equals("hellhound"))continue;
   found++;assertTrue(t.matches("Hellhound"));assertFalse(t.matches("Revenant imp"));
   for(String name:t.getNames())assertFalse(name.contains(","));
  }
  assertEquals(3,found);
 }
 @Test void savedLegacyAssignmentKeepsItsRevenantAlias() {
  Task loaded=SlayerMaster.get("hellhound, revenant hellhound, Revenant hellhound").orElseThrow();
  assertTrue(loaded.matches("Hellhound"));assertTrue(loaded.matches("Revenant hellhound"));
 }
}
