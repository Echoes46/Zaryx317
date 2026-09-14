package io.zaryx.content.teleportv2.inter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class DungeonMonsterListTest {
 @Test void configuredWyvernTableSelectsOnly465() throws Exception {
  com.fasterxml.jackson.databind.JsonNode table=new com.fasterxml.jackson.databind.ObjectMapper(
   new com.fasterxml.jackson.dataformat.yaml.YAMLFactory()).readTree(new java.io.File("etc/cfg/drops/skeletal_wyvern.yml"));
  int npc=table.get("npc_id").asInt();assertEquals(465,npc);
  assertArrayEquals(new int[]{465},TeleportContent.uniqueLootMonsters(new int[]{465,466,467,468},
   id->"Skeletal Wyvern",id->id==npc));
 }
 @Test void choosesLootBearingVariantBeforeDeduplicating() {
  int[] actual=TeleportContent.uniqueLootMonsters(new int[]{465,466,467,468,100},
   id->id==100?"Ice giant":"Skeletal Wyvern",id->id==466 || id==467);
  assertArrayEquals(new int[]{466},actual);
 }
 @Test void keepsDifferentTypesInOrderAndNormalizesNames() {
  int[] actual=TeleportContent.uniqueLootMonsters(new int[]{1,2,3,4},
   id->id==1?"Skeletal_Wyvern":id==2?" skeletal wyvern ":id==3?"Ice giant":"Ice warrior",id->id!=4);
  assertArrayEquals(new int[]{1,3},actual);
 }
 @Test void entirelyEmptyRosterRemainsEmpty() {
  assertEquals(0,TeleportContent.uniqueLootMonsters(new int[]{465,466},id->"Skeletal Wyvern",id->false).length);
 }
}
