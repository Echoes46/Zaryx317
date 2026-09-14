package io.zaryx.content.upgrade;
import com.google.gson.*;
import io.zaryx.model.definitions.ItemStats;
import io.zaryx.model.definitions.ItemEquipmentStats;
import io.zaryx.model.Bonus;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class UpgradeBalanceTest {
 @Test void everyCombatUpgradePreservesBonusesAndImprovesSomething() throws Exception {
  JsonObject data=JsonParser.parseString(Files.readString(Path.of("etc/cfg/item/item_stats.json"))).getAsJsonObject();
  Set<Integer> utility=new HashSet<>(Arrays.asList(20790,20789,20788,20787,20786,26939));
  for(UpgradeMaterials recipe:UpgradeMaterials.values()) {
   int a=recipe.getRequired().getId(),b=recipe.getReward().getId();
   assertFalse(Arrays.asList(33433,33434,39001).contains(b),recipe.name());
   if(recipe.getType()==UpgradeMaterials.UpgradeType.MISC||utility.contains(b))continue;
   assertTrue(data.has(""+a),recipe.name());assertTrue(data.has(""+b),recipe.name());
   ItemEquipmentStats before=new Gson().fromJson(data.get(""+a),ItemStats.class).getEquipment();
   ItemEquipmentStats after=new Gson().fromJson(data.get(""+b),ItemStats.class).getEquipment();
   assertEquals(before.getSlot(),after.getSlot(),recipe.name());boolean improved=false;
   for(Bonus bonus:Bonus.values()) {
    int x=before.getBonus(bonus),y=after.getBonus(bonus);
    assertTrue(y>=x,recipe.name()+" loses "+bonus+": "+x+" -> "+y);improved|=y>x;
   }
   if(before.getAttackSpeed()>0) {assertTrue(after.getAttackSpeed()>0&&after.getAttackSpeed()<=before.getAttackSpeed(),recipe.name());improved|=after.getAttackSpeed()<before.getAttackSpeed();}
   assertTrue(improved,recipe.name()+" has no improvement");
  }
 }
 @Test void invalidAndStaleSlotsAreIgnored() {UpgradeInterface ui=new UpgradeInterface(null);ui.handleItemAction(-1);ui.upgradeMaterialsArrayList=UpgradeMaterials.getForType(UpgradeMaterials.UpgradeType.WEAPON);ui.handleItemAction(-1);ui.handleItemAction(ui.upgradeMaterialsArrayList.size());ui.handleItemAction(Integer.MAX_VALUE);}

 @Test void upgradesNeverRemoveAnExistingSpecial() {
  for(UpgradeMaterials r:UpgradeMaterials.values())if(io.zaryx.content.combat.specials.Specials.forWeaponId(r.getRequired().getId())!=null)
   assertNotNull(io.zaryx.content.combat.specials.Specials.forWeaponId(r.getReward().getId()),r.name());
 }
 @Test void mixedUpgradeSetsRetainTheirLowestCompletedTier() {
  int[][] gear={{26382,28254,33153},{26384,28256,33154},{26386,28258,33155}};
  for(int a=0;a<3;a++)for(int b=0;b<3;b++)for(int c=0;c<3;c++)assertEquals(Math.min(a,Math.min(b,c)),UpgradeSetRules.torvaTier(gear[0][a],gear[1][b],gear[2][c]));
  assertEquals(0,UpgradeSetRules.voidTier(24183,8839,8840,8842,0));
  assertEquals(1,UpgradeSetRules.voidTier(26473,13072,13073,24182,0));
  assertEquals(2,UpgradeSetRules.voidTier(26473,26469,26471,26467,0));
  assertEquals(-1,UpgradeSetRules.voidTier(26475,26469,26471,26467,0));
  assertEquals(1,UpgradeSetRules.rangedTier(27235,33150,33152));
 }
}
