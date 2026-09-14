package io.zaryx.content.upgrade;
/** Mixed upgraded pieces retain the completed lower-tier set bonus. */
public final class UpgradeSetRules {
 private UpgradeSetRules() { }
 public static int tier(int[] equipped,int[][] progressions) {
  if(equipped.length!=progressions.length)return -1;
  int minimum=Integer.MAX_VALUE;
  for(int slot=0;slot<equipped.length;slot++) {
   int rank=-1;
   for(int i=0;i<progressions[slot].length;i++)if(equipped[slot]==progressions[slot][i])rank=i;
   if(rank<0)return -1;minimum=Math.min(minimum,rank);
  }
  return minimum==Integer.MAX_VALUE?-1:minimum;
 }
 public static int voidTier(int helm,int body,int legs,int gloves,int style) {
  int[][] heads={{11663,24183,26473},{11664,24184,26475},{11665,24185,26477}};
  return tier(new int[]{helm,body,legs,gloves},new int[][]{heads[style],{8839,13072,26469},{8840,13073,26471},{8842,24182,26467}});
 }
 public static int torvaTier(int helm,int body,int legs) {
  return tier(new int[]{helm,body,legs},new int[][]{{26382,28254,33153},{26384,28256,33154},{26386,28258,33155}});
 }
 public static int rangedTier(int helm,int body,int legs) {
  return tier(new int[]{helm,body,legs},new int[][]{{33144,33151,27235},{33145,33150,27238},{33146,33152,27241}});
 }
}
