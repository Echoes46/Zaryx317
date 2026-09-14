package io.zaryx.content.items;

import io.zaryx.content.combat.range.*;
import io.zaryx.content.combat.specials.*;
import io.zaryx.content.combat.specials.impl.*;
import io.zaryx.content.combat.weapon.*;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Right;
import io.zaryx.model.items.EquipmentSet;
import java.util.*;

/** Read-only descriptions derived from the equipped items and combat registries. */
public final class EquipmentGuide {
    public static final int ROOT=61700, ENTRY=61712, ROWS=64;
    private static final String[] TABS={"Combat","Special","Ammo","Sets"};
    private EquipmentGuide() { }
    public static boolean click(Player p,int id) {
        if (id != ENTRY && (id < 61705 || id > 61711)) return false;
        if (p.getInterfaceEvent().isActive() || io.zaryx.Server.getMultiplayerSessionListener().inAnySession(p)) return true;
        if(id==61709) {p.getPA().showInterface(65000);return true;}
        if(id==61711) {p.getPA().closeAllWindows();return true;}
        int page=id==ENTRY?0:id>=61705&&id<=61708?id-61705:p.equipmentGuidePage;
        open(p,page);return true;
    }
    public static void open(Player p,int page) {
        page=Math.max(0,Math.min(3,page));p.equipmentGuidePage=page;
        List<String> lines=wrap(details(p,page));
        for(int i=0;i<ROWS;i++)p.getPA().sendString(61720+i,i<lines.size()?lines.get(i):"");
        for(int i=0;i<TABS.length;i++)p.getPA().sendString(61705+i,(i==page?"@or1@":"@whi@")+TABS[i]);
        p.getPA().setScrollableMaxHeight(61715,Math.max(195,Math.min(ROWS,lines.size())*15+4));
        p.getPA().resetScrollBar(61715);p.getPA().showInterface(ROOT);
    }
    public static List<String> wrap(List<String> input) {
        List<String> out=new ArrayList<>();
        for(String raw:input) {
            String line=raw;
            while(line.length()>62) {int split=line.lastIndexOf(' ',62);if(split<1)split=62;out.add(line.substring(0,split));line=line.substring(split).trim();}
            out.add(line);
        }
        return out;
    }
    public static String name(Player p,int id) {
        if(id<=0)return "None";
        String label=ItemDef.forId(id).getName();
        return label+(p.getRights().contains(Right.STAFF_MANAGER)?" ["+id+"]":"");
    }
    private static String pretty(String s) {
        s=s.toLowerCase(Locale.ROOT).replace('_',' ');
        return s.isEmpty()?s:Character.toUpperCase(s.charAt(0))+s.substring(1);
    }
    public static List<String> details(Player p,int page) {
        List<String> out=new ArrayList<>();int id=p.getItems().getWeapon();
        out.add("Equipped weapon: "+name(p,id));out.add("");
        if(page==0)combat(p,id,out);
        else if(page==1)special(p,id,out);
        else if(page==2)ammo(p,id,out);
        else sets(p,out);
        return out;
    }
    private static void combat(Player p,int id,List<String> out) {
        WeaponData data=WeaponData.forItemId(id);
        out.add("ATTACK STYLES");
        for(WeaponMode mode:data.getWeaponModes()) {
            String label=pretty(mode.getAttackStyle().name());
            String xp=mode.getCombatStyle()==CombatStyle.RANGE?
                    (mode.getAttackStyle()==AttackStyle.DEFENSIVE?"Ranged + Defence":"Ranged"):
                    mode.getAttackStyle()==AttackStyle.ACCURATE?"Attack":mode.getAttackStyle()==AttackStyle.AGGRESSIVE?"Strength":
                    mode.getAttackStyle()==AttackStyle.CONTROLLED?"Attack + Strength + Defence":"Defence";
            if(data==WeaponData.SALAMANDER) {label=new String[]{"Scorch","Flare","Blaze"}[mode.getIndex()];xp=new String[]{"Strength","Ranged","Magic"}[mode.getIndex()];}
            if(data==WeaponData.BULWARK) {label=mode.getIndex()==0?"Pummel":"Block";xp=mode.getIndex()==0?"Attack":"No attacks / no combat XP";}
            if(id==28997)label=new String[]{"Pound","Pummel","Spike","Block"}[mode.getIndex()];
            out.add((p.getCombatConfigs().getAttackStyle()==mode.getIndex()?"> ":"  ")+label+" - "+mode.getCombatStyle()+" - "+xp);
        }
        out.add("Most damaging attacks also train Hitpoints.");
        if(data==WeaponData.STAFF||data==WeaponData.SOTD)out.add("These are staff melee styles. Casting spells uses Magic XP rules.");
        if(SpecialWeaponRules.isSalamander(id))out.add("All three styles consume one tar per attack.");
        if(SpecialWeaponRules.isBulwark(id)) {
            out.add("");out.add("BULWARK PROTECTION");
            out.add(SpecialWeaponRules.blocking(p)?SpecialWeaponRules.blockReady(p)?"Active: 20% less NPC combat damage.":"Block selected: protection is still activating.":"Inactive: select Block in the combat tab.");
            out.add("Protection starts after 8 ticks. No attacks in Block.");
            out.add("Does not reduce player, poison or environmental damage.");
            out.add("Leaving Block adds an 8-tick attack delay.");
        }
        if(id==28997) {out.add("");out.add("Two hits per attack. Normally, the second accuracy check needs the first to succeed.");}
    }
    private static void special(Player p,int id,List<String> out) {
        Special spec=Specials.forWeaponId(id);
        if(spec==null){out.add("This weapon has no special attack.");return;}
        String label=spec instanceof ShieldBash?"Shield Bash":spec instanceof BloodInfusion?"Blood Infusion":
                pretty(spec.getClass().getSimpleName().replaceAll("([a-z])([A-Z])","$1 $2").replace("Special Attack",""));
        out.add("SPECIAL ATTACK: "+label);
        out.add("Cost: "+format(spec.getRequiredCost()*10)+"% energy");
        out.add("Current energy: "+format(p.specAmount*10)+"% - "+(p.specAmount>=spec.getRequiredCost()?"Enough energy":"Not enough energy"));
        out.add("");
        if(spec instanceof BloodInfusion) {
            out.add("Requires full Blood Moon armour and dual macuahuitl.");
            out.add("Set requirement: "+(BloodInfusion.hasSet(p)?"Met":"Missing equipment - see Sets"));
            out.add("Sacrifices 25% of current HP, rounded down.");
            out.add("HP cost now: "+BloodInfusion.healthCost(p.getHealth().getCurrentHealth()));
            out.add("Raises maximum damage by 25% and adds a minimum damage roll. Misses still deal zero.");
            out.add("Both hits roll accuracy independently.");
            out.add("A successful accuracy roll guarantees Bloodrager.");
        } else if(spec instanceof ShieldBash) {
            out.add("20% increased accuracy. Hits up to 10 legal targets in an 11 x 11 area in multi-combat.");
            out.add("Single combat: primary target only. NPC attacks stay on NPCs; player attacks stay on players.");
            out.add("Lowers the strongest offensive style by 5%, even on zero damage. Defence and Prayer are unchanged.");
            out.add("Requires Pummel mode; Block prevents attacks.");
        } else {
            out.add("Select Special Attack in the combat tab to use it. Detailed secondary effects are not listed for this weapon yet.");
        }
    }
    private static String format(double n){return String.format(Locale.ROOT,n==Math.rint(n)?"%.0f":"%.1f",n);}
    private static void ammo(Player p,int id,List<String> out) {
        out.add("AMMUNITION & CHARGES");
        int equipped=p.playerEquipment[Player.playerArrows];
        if(SpecialWeaponRules.isSalamander(id)) {
            out.add("Required: "+name(p,SpecialWeaponRules.tar(id))+" in the ammunition slot.");
            out.add("One tar per attack, including melee. Tar is consumed.");
            out.add(SpecialWeaponRules.hasTar(p)?"Ready: correct tar equipped.":"Not ready: equip the matching tar.");
        } else if(id==12926||id==28688) {
            out.add("Requires loaded darts and Zulrah scales.");
            out.add("Loaded ammo: "+name(p,p.getToxicBlowpipeAmmo())+" x "+p.getToxicBlowpipeAmmoAmount());
            out.add("Scales: "+p.getToxicBlowpipeCharge());
        } else if(id==11907||id==12899) {
            out.add("Uses internal charges for magic attacks.");
            out.add("Charges remaining: "+(id==11907?p.getTridentCharge():p.getToxicTridentCharge()));
        } else if(id==22323||id==25731) {
            out.add("Uses Sanguinesti staff charges: "+p.getSangStaffCharge());
        } else if(id==22550||id==27655) {
            out.add("Requires revenant ether charges.");out.add("Charges: "+p.getPvpWeapons().getCharges(id));
        } else if(id==4734) {
            out.add("Requires bolt racks in the ammunition slot.");
            out.add(equipped==4740&&p.playerEquipmentN[Player.playerArrows]>0?"Correct ammo equipped.":"Equip bolt racks before attacking.");
        } else if(id==19478||id==19481||id==26712) {
            out.add("Requires javelins in the ammunition slot.");
            out.add(p.getCombatItems().usingJavelins(equipped)&&p.playerEquipmentN[Player.playerArrows]>0?"Current ammunition is compatible.":"Equip javelins before attacking.");
        } else if(Arrays.stream(new int[]{9185,26486,11785,21012,21902,26374,33206,28869,26269,25916,8880,25918}).anyMatch(x->x==id)) {
            out.add("Requires compatible bolts in the ammunition slot.");
            out.add(p.getCombatItems().properBolts()&&p.playerEquipmentN[Player.playerArrows]>0?"Current ammunition is compatible.":"Equip compatible bolts before attacking.");
        } else if(Arrays.stream(RangeData.CRYSTAL_BOWS).anyMatch(x->x==id)) {
            out.add("Does not consume arrows from the ammunition slot.");
        } else if(Bow.forWeapon(id).isPresent()) {
            Bow bow=Bow.forWeapon(id).get();
            out.add(id==29000?"Uses atlatl darts.":"Uses arrows up to "+pretty(bow.getMaxArrow().name())+" tier.");
            out.add(Bow.canUseArrow(p)&&p.playerEquipmentN[Player.playerArrows]>0?"Current ammunition is compatible.":"Equip compatible ammunition before attacking.");
        } else if(Arrays.stream(RangeData.OTHER_RANGE_WEAPONS).anyMatch(x->x==id)) {
            out.add("Uses the equipped thrown-weapon stack.");
        } else if(WeaponData.forItemId(id)==WeaponData.BOW) {
            out.add("Check this weapon's ammunition or charge requirements.");
        } else if(WeaponData.forItemId(id)==WeaponData.STAFF||WeaponData.forItemId(id)==WeaponData.SOTD) {
            out.add("Spellbook spells use the runes listed on the spell.");
            out.add("Powered weapons may also require internal charges.");
        } else out.add("No ammunition needed for this weapon's melee attacks.");
        out.add("");out.add("Ammo slot: "+name(p,equipped)+" x "+Math.max(0,p.playerEquipmentN[Player.playerArrows]));
    }
    private static void sets(Player p,List<String> out) {
        out.add("BLOOD MOON / BLOODRAGER");
        out.add(BloodInfusion.hasSet(p)?"ACTIVE - full set equipped":"INACTIVE - equip all four pieces below");
        int[] ids={29028,29022,29025,28997};int[] slots={Player.playerHat,Player.playerChest,Player.playerLegs,Player.playerWeapon};
        for(int i=0;i<ids.length;i++)out.add((p.playerEquipment[slots[i]]==ids[i]?"Equipped: ":"Missing: ")+name(p,ids[i]));
        out.add("Each successful accuracy roll has a 1-in-3 chance to attack one tick sooner; at most once per attack.");
        out.add("The full set also unlocks Blood Infusion.");
        out.add("");out.add("BARROWS SET COMPLETION");
        boolean found=false;
        for(EquipmentSet set:new EquipmentSet[]{EquipmentSet.DHAROK,EquipmentSet.GUTHAN,EquipmentSet.VERAC,EquipmentSet.TORAG,EquipmentSet.AHRIM,EquipmentSet.KARIL}) {
            int[] best=set.getEquipment()[0];int count=0;
            for(int[] variant:set.getEquipment()) {int n=0;for(int item:variant)if(p.getItems().isWearingItem(item))n++;if(n>count){count=n;best=variant;}}
            if(count==0)continue;found=true;
            out.add(pretty(set.name())+": "+count+" / "+best.length+(set.isWearing(p)?" - Complete":" - Incomplete"));
            for(int item:best)if(!p.getItems().isWearingItem(item))out.add("Missing: "+name(p,item));
        }
        if(!found)out.add("Equip a Barrows piece to see its matching set requirements.");
        out.add("This page covers Blood Moon and Barrows. Other custom set bonuses are not listed here yet.");
    }
}
