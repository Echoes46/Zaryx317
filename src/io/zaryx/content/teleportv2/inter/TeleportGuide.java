package io.zaryx.content.teleportv2.inter;

import io.zaryx.content.teleportv2.inter.TeleportInterface.*;
import io.zaryx.content.skills.slayer.SlayerMaster;
import io.zaryx.model.definitions.NpcDef;
import io.zaryx.model.entity.npc.stats.*;
import io.zaryx.model.entity.player.Player;
import java.util.*;

/** Advice is separate from the existing teleport and encounter access checks. */
public final class TeleportGuide {
    private TeleportGuide() { }
    public static String name(Teleport t) { return t.getName().replaceAll("<[^>]*>", "").replace("<", ""); }
    public static boolean wildernessRisk(Teleport t) {
        return t instanceof PK && t != PK.FEROX_ENCLAVE && t != PK.MAGE_BANK || t.getPosition().inWild();
    }
    public static int collectionNpc(Teleport t) {
        if (t == BOSSES.DEMONIC_GORILLA) return 7145;
        if (t == BOSSES.ZULRAH) return 2042;
        if (t == BOSSES.ALCHEMICAL_HYDRA) return 8621;
        if (t == BOSSES.GROTESQUE_GUARDIANS) return 7888;
        if (t == MINIGAMES.COX) return 7554;
        if (t == MINIGAMES.TOB) return 8360;
        if (t == MINIGAMES.ARBOGRAVE_SWAMP) return 1101;
        return dropNpc(t);
    }
    public static int dropNpc(Teleport t) {
        if (t instanceof SKILLING) return -1;
        if (t instanceof PK && (t.getNpcId()==306 || t.getNpcId()==107)) return -1;
        if (t instanceof MINIGAMES && t!=MINIGAMES.COX && t!=MINIGAMES.TOB
                && t!=MINIGAMES.ARBOGRAVE_SWAMP && t!=MINIGAMES.BARROWS) return -1;
        return t.getNpcId();
    }
    public static List<String> lines(Player player, Teleport t) {
        List<String> out=new ArrayList<>();
        add(out,"RISK", wildernessRisk(t) ? "Wilderness / PvP: items can be lost." :
                t instanceof PK ? "Safe arrival; Wilderness is nearby." : "Outside Wilderness. Combat can still be dangerous.");
        boolean combat=t instanceof BOSSES || t instanceof MONSTERS || t instanceof DUNGEONS || t instanceof PK && dropNpc(t)>0;
        NpcCombatDefinition def=NpcCombatDefinition.definitions.get(t.getNpcId());
        if (combat) {
            int hp=def==null ? 500 : def.getLevel(NpcCombatSkill.HITPOINTS);
            String gear=hp<=100 ? "Starter / basic gear" : hp<=300 ? "Mid-tier gear" : hp<=1000 ? "Upgraded gear" : "Endgame gear; consider a team";
            int level=hp<=100 ? 60 : hp<=300 ? 80 : hp<=1000 ? 100 : 110;
            if (t==MONSTERS.COWS || t==MONSTERS.ROCKCRAB || t==MONSTERS.SANCRAB) { level=3;gear="Starter gear"; }
            add(out,"SUGGESTED SETUP",level+"+ combat (advice, not an entry rule). " +gear+".");
            add(out,"ATTACK STYLE",style(t,def));
            add(out,"PRAYERS",t instanceof BOSSES || t instanceof PK ?
                    "Match protection prayers to incoming attacks; change for phases." :
                    def!=null && "Melee".equalsIgnoreCase(def.getAttackStyle()) ? "Protect from Melee against close-range attacks." :
                    "Use protection for the monster's current attack style.");
        } else if (t instanceof MINIGAMES) {
            add(out,"PREPARATION",t==MINIGAMES.COX || t==MINIGAMES.TOB || t==MINIGAMES.ARBOGRAVE_SWAMP || t==MINIGAMES.INFERNO ?
                    "Suggested: 110+ combat, upgraded gear and combat-style switches. Learn each encounter's prayers." :
                    "Bring supplies for this activity. Skill and equipment needs depend on the activity.");
        } else add(out,"PREPARATION","Travel destination. Choose equipment for your planned activity.");
        add(out,"REQUIREMENTS & COST",access(t));
        NpcDef npc=NpcDef.forId(t.getNpcId());
        if (combat && npc!=null) SlayerMaster.get(npc.getName().replace('_',' ')).ifPresent(task -> {
            int required=task.getLevel();
            if(required>1) out.add("Slayer: "+required+" (you: "+player.playerLevel[18]+").");
        });
        if (t==BOSSES.BRYOPHYTA) out.add("Mossy key in inventory: "+(player.getItems().playerHasItem(22375,1)?"yes":"no")+".");
        if (t==BOSSES.OBOR) out.add("Giant key in inventory: "+(player.getItems().playerHasItem(20754,1)?"yes":"no")+".");
        add(out,"REWARDS",dropNpc(t)>0 ? "Open Drops for loot, Full drop table for rates, or Collection log for progress." :
                "Rewards depend on the activity; this location has no direct monster drop preview.");
        if(t instanceof DUNGEONS) out.addAll(wrap("Drop preview represents one monster; others in the dungeon have different loot."));
        return out;
    }
    private static String style(Teleport t,NpcCombatDefinition def) {
        if(t==BOSSES.DEMONIC_GORILLA || t==BOSSES.DAGANNOTH_KINGS || t==BOSSES.KALPHITE_QUEEN || t==BOSSES.ZULRAH || t==BOSSES.GROTESQUE_GUARDIANS)
            return "Bring combat-style switches for different enemies or phases.";
        if(def==null)return "Bring your strongest setup and adjust to the encounter.";
        NpcBonus[] stats={NpcBonus.STAB_BONUS,NpcBonus.SLASH_BONUS,NpcBonus.CRUSH_BONUS,NpcBonus.MAGIC_BONUS,NpcBonus.RANGE_BONUS};
        String[] names={"Stab","Slash","Crush","Magic","Ranged"};int min=Integer.MAX_VALUE;
        for(NpcBonus stat:stats)min=Math.min(min,def.getDefenceBonus(stat));
        List<String> best=new ArrayList<>();for(int i=0;i<stats.length;i++)if(def.getDefenceBonus(stats[i])==min)best.add(names[i]);
        return best.size()>2 ? "No clear armour weakness; use your strongest setup." :
                String.join(" / ",best)+" has lower listed defence. Boss mechanics may require another style.";
    }
    static String access(Teleport t) {
        String prefix="Teleport: free. ";
        if(t==BOSSES.OBOR)return prefix+"Entry consumes one giant key.";
        if(t==BOSSES.BRYOPHYTA)return prefix+"Entry consumes one mossy key.";
        if(t==BOSSES.CERBERUS)return prefix+"91 Slayer and a Hellhound or Cerberus task.";
        if(t==BOSSES.THERMO)return prefix+"A Smoke Devil or Thermonuclear Smoke Devil task is required here.";
        if(t==BOSSES.ABYSSAL_SIRE)return prefix+"An Abyssal Demon or Abyssal Sire task is required.";
        if(t==DUNGEONS.CRYSTALCAVERN)return prefix+"Crystal creatures require their matching Slayer task.";
        if(t==PK.KBD)return prefix+"Arrival is in the Wilderness, outside the lair. Bring dragonfire protection.";
        if(t==MONSTERS.MITHRILDRAGONS || t==PK.LAVA_DRAGS || t==PK.WEST_DRAGONS)return prefix+"Bring dragonfire protection.";
        if(t==MINIGAMES.OUTLAST)return prefix+"Join while tournament registration is open.";
        if(t==MINIGAMES.BLASTFURNACE)return prefix+"Bring ores; disconnecting loses stored ores/bars.";
        return prefix+"Check entrance requirements and any entry charge before starting the encounter or activity.";
    }
    private static void add(List<String> lines,String title,String text) {
        if(!lines.isEmpty())lines.add("");lines.add("@or1@"+title);lines.addAll(wrap(text));
    }
    static List<String> wrap(String text) {
        List<String> lines=new ArrayList<>();String current="";
        for(String word:text.split(" ")) {
            if(!current.isEmpty() && current.length()+word.length()+1>45) {lines.add(current);current="";}
            current+=(current.isEmpty()?"":" ")+word;
        }
        if(!current.isEmpty())lines.add(current);return lines;
    }
}
