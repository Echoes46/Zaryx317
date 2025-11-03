package io.zaryx.content.upgrade;

import com.google.common.base.Preconditions;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.definitions.ItemStats;
import io.zaryx.model.items.GameItem;
import io.zaryx.util.Misc;
import lombok.Getter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static io.zaryx.content.upgrade.UpgradeMaterials.UpgradeType.*;//

@Getter
public enum UpgradeMaterials {


    /* Weapons */
    KONAR(WEAPON, 0, new GameItem(5061, 1), new GameItem(5060, 1), 10_000_000, 70, 25000,true),

    ABYSSAL_TENTACLE(WEAPON, 0, new GameItem(12006, 1), new GameItem(26484, 1), 10_000_000, 69, 25000,true),

    BULKY(WEAPON, 0, new GameItem(26484, 1), new GameItem(39006, 1), 35_000_000, 74, 25000,true),

    ARMADYL_GODSWORD(WEAPON, 0, new GameItem(11802, 1), new GameItem(20368, 1), 15_000_000, 59, 50000,true),

    SARADOMIN_GODSWORD(WEAPON, 0, new GameItem(11806, 1), new GameItem(20372, 1), 10_000_000, 69, 50000,true),

    ZAMORAK_GODSWORD(WEAPON, 0, new GameItem(11808, 1), new GameItem(20374, 1), 10_000_000, 69, 50000,true),

    BANDOS_GODSWORD(WEAPON, 0, new GameItem(11804, 1), new GameItem(20370, 1), 10_000_000, 69, 50000,true),

    Dragon_CLAWS(WEAPON, 0, new GameItem(20784, 1), new GameItem(26708, 1), 15_000_000, 59, 250000,true),

    DRAGON_WARHAMMER(WEAPON, 0, new GameItem(13576, 1), new GameItem(26710, 1), 15_000_000, 69, 250000,true),

    Ghrazi_Rapier(WEAPON, 0, new GameItem(22324, 1), new GameItem(25734, 1), 50_000_000, 59, 500000,true),

    OSMUMTEN_FANG(WEAPON, 0, new GameItem(26219, 1), new GameItem(27246, 1), 275_000_000, 49, 500000,true),

    OSMUMTEN_FANGOR(WEAPON, 0, new GameItem(27246, 1), new GameItem(33202, 1), 500_000_000, 39, 500000,true),

    SCYTHE_OF_VITUR(WEAPON, 0, new GameItem(22325, 1), new GameItem(25736, 1), 150_000_000, 59, 500000,true),

    HOLY_SCYTHE_OF_VITUR(WEAPON, 0, new GameItem(25736, 1), new GameItem(25739, 1), 275_000_000, 49, 500000,true),

    SANGUINGE_SCYTHE(WEAPON, 0, new GameItem(25739, 1), new GameItem(33203, 1), 500_000_000, 39, 500000,true),

    REALMSCYTHE(WEAPON, 0, new GameItem(33203, 1), new GameItem(39001, 1), 999_000_000, 20, 500000,true),


    HEAVY_BALLISTA(WEAPON, 0, new GameItem(19481, 1), new GameItem(26712, 1), 7_500_000, 79, 150000,true),

    DRAGON_HUNTER_CROSSBOW(WEAPON, 0, new GameItem(21012, 1), new GameItem(25916, 1), 15_000_000, 69, 250000,false),

    ZARYTE_CROSSBOW(WEAPON, 0, new GameItem(26374, 1), new GameItem(33206, 1), 175_000_000, 49, 500000, true),

    Ascension(WEAPON, 0, new GameItem(33206, 1), new GameItem(26269, 1), 500_000_000, 39, 500000,true),

    Twisted_bow(WEAPON, 0, new GameItem(20997, 1), new GameItem(33058, 1), 175_000_000, 49, 500000,true),

    SEREN(WEAPON, 0, new GameItem(33058, 1), new GameItem(33207, 1), 500_000_000, 39, 500000,true),

    REALMBOW(WEAPON, 0, new GameItem(33207, 1), new GameItem(33434, 1), 999_000_000, 20, 500000,true),


    ANCIENT_STAFF(WEAPON, 0, new GameItem(4675, 1), new GameItem(27624, 1), 50_000_000, 59, 100000, true),

    Sanguinesti_Staff(WEAPON, 0, new GameItem(22323, 1), new GameItem(25731, 1), 150_000_000, 49, 500000,true),

    NOXIOUS_STAFF(WEAPON, 0, new GameItem(33149, 1), new GameItem(27275, 1), 225_000_000, 49, 500000,true),

    Tumeken(WEAPON, 0, new GameItem(27275, 1), new GameItem(33205, 1), 500_000_000, 39, 500000,true),

    REALMSTAFF(WEAPON, 0, new GameItem(33205, 1), new GameItem(33433, 1), 999_000_000, 20, 500000,true),



    /* Armour */


    VOID_MAGE_HELM(ARMOUR, 0, new GameItem(11663, 1), new GameItem(24183, 1), 2_500_000, 79, 40000,false),

    VOID_RANGER_HELM(ARMOUR, 0, new GameItem(11664, 1), new GameItem(24184, 1), 2_500_000, 79, 40000,false),

    VOID_MELEE_HELM(ARMOUR, 0, new GameItem(11665, 1), new GameItem(24185, 1), 2_500_000, 79, 40000,false),

    VOID_TOP(ARMOUR, 0, new GameItem(8839, 1), new GameItem(13072, 1), 2_500_000, 79, 40000,false),

    VOID_BOTTOM(ARMOUR, 0, new GameItem(8840, 1), new GameItem(13073, 1), 2_500_000, 79, 40000,false),

    VOID_KNIGHT_GLOVES(ARMOUR, 0, new GameItem(8842, 1), new GameItem(24182, 1), 2_500_000, 79, 40000,false),

    VOID_MAGE_HELM_I(ARMOUR, 0, new GameItem(24183, 1), new GameItem(26473, 1), 10_000_000, 69, 125000,true),

    VOID_RANGER_HELM_I(ARMOUR, 0, new GameItem(24184, 1), new GameItem(26475, 1), 10_000_000, 69, 125000,true),

    VOID_MELEE_HELM_I(ARMOUR, 0, new GameItem(24185, 1), new GameItem(26477, 1), 10_000_000, 69, 125000,true),

    ELITE_VOID_TOP(ARMOUR, 0, new GameItem(13072, 1), new GameItem(26469, 1), 10_000_000, 69, 125000,true),

    ELITE_VOID_ROBE(ARMOUR, 0, new GameItem(13073, 1), new GameItem(26471, 1), 10_000_000, 69, 125000,true),

    VOID_KNIGHT_GLOVES_I(ARMOUR, 0, new GameItem(24182, 1), new GameItem(26467, 1), 10_000_000, 69, 125000,true),

    ARMADYL_HELM(ARMOUR, 0, new GameItem(11826, 1), new GameItem(26714, 1), 50_000_000, 59, 250000,true),

    ARMADYL_BODY(ARMOUR, 0, new GameItem(11828, 1), new GameItem(26715, 1), 50_000_000, 59, 250000,true),

    ARMADYL_LEGS(ARMOUR, 0, new GameItem(11830, 1), new GameItem(26716, 1), 50_000_000, 59, 250000,true),

    BANDOS_BODY(ARMOUR, 0, new GameItem(11832, 1), new GameItem(26718, 1), 50_000_000, 59, 250000,true),

    BANDOS_TASSETS(ARMOUR, 0, new GameItem(11834, 1), new GameItem(26719, 1), 50_000_000, 59, 250000,true),

    BANDOS_BOOTS(ARMOUR, 0, new GameItem(11836, 1), new GameItem(26720, 1), 50_000_000, 59, 250000,true),

    ANCESTRAL_HAT(ARMOUR, 0, new GameItem(21018, 1), new GameItem(24664, 1), 50_000_000, 59, 250000,true),

    ANCESTRAL_ROBE_TOP(ARMOUR, 0, new GameItem(21021, 1), new GameItem(24666, 1), 50_000_000, 59, 250000,true),

    ANCESTRAL_ROBE_BOTTOM(ARMOUR, 0, new GameItem(21024, 1), new GameItem(24668, 1), 50_000_000, 59, 250000,true),

    PERNIX_HOOD(ARMOUR, 0, new GameItem(33144, 1), new GameItem(27226, 1), 150_000_000, 49, 500000,true),

    PERNIX_BODY(ARMOUR, 0, new GameItem(33145, 1), new GameItem(27229, 1), 150_000_000, 49, 500000,true),

    PERNIX_BOTTOMS(ARMOUR, 0, new GameItem(33146, 1), new GameItem(27232, 1), 150_000_000, 49, 500000,true),

    TORVA_HELM(ARMOUR, 0, new GameItem(26382, 1), new GameItem(28254, 1), 150_000_000, 49, 500000,true),

    TORVA_PLATE(ARMOUR, 0, new GameItem(26384, 1), new GameItem(28256, 1), 150_000_000, 49, 500000,true),

    TORVA_LEGS(ARMOUR, 0, new GameItem(26386, 1), new GameItem(28258, 1), 150_000_000, 49, 500000,true),

    VIRTUS_HELM(ARMOUR, 0, new GameItem(33141, 1), new GameItem(27428, 1), 150_000_000, 49, 500000,true),

    VIRTUS_PLATE(ARMOUR, 0, new GameItem(33142, 1), new GameItem(27430, 1), 150_000_000, 49, 500000,true),

    VIRTUS_LEGS(ARMOUR, 0, new GameItem(33143, 1), new GameItem(27432, 1), 150_000_000, 49, 500000,true),

    AZIRHELM(ARMOUR, 0, new GameItem(27226, 1), new GameItem(33151, 1), 350_000_000, 49, 500000,true),

    AZIRBODY(ARMOUR, 0, new GameItem(27229, 1), new GameItem(33150, 1), 350_000_000, 49, 500000,true),

    AZIRLEGS(ARMOUR, 0, new GameItem(27232, 1), new GameItem(33152, 1), 350_000_000, 49, 500000,true),

    FORCEHELM(ARMOUR, 0, new GameItem(28254, 1), new GameItem(33153, 1), 350_000_000, 49, 500000,true),

    FORCEBODY(ARMOUR, 0, new GameItem(28256, 1), new GameItem(33154, 1), 350_000_000, 49, 500000,true),

    FORCELEGS(ARMOUR, 0, new GameItem(28258, 1), new GameItem(33155, 1), 350_000_000, 49, 500000,true),

    REAPERHELM(ARMOUR, 0, new GameItem(27428, 1), new GameItem(33199, 1), 350_000_000, 49, 500000,true),

    REAPERBODY(ARMOUR, 0, new GameItem(27430, 1), new GameItem(33200, 1), 350_000_000, 49, 500000,true),

    REAPERLEGS(ARMOUR, 0, new GameItem(27432, 1), new GameItem(33201, 1), 350_000_000, 49, 500000,true),


    TORSO(ARMOUR, 0, new GameItem(28067, 1), new GameItem(28069, 1), 50_000_000, 59, 500000,true),

    MALEDICTION(ARMOUR, 0, new GameItem(11924, 1), new GameItem(12806, 1), 25_000_000, 59, 500000,true),

    ODIUM(ARMOUR, 0, new GameItem(11926, 1), new GameItem(12807, 1), 25_000_000, 59, 250000,true),

    Dinhs_Balwark(ARMOUR, 0, new GameItem(21015, 1), new GameItem(28682, 1), 75_000_000, 49, 250000,true),

    ELIDINIS_WARD(ARMOUR, 0, new GameItem(25985, 1), new GameItem(27251, 1), 90_000_000, 49, 250000,true),

    ELIDINIS_WARD_F(ARMOUR, 0, new GameItem(27251, 1), new GameItem(27253, 1), 275_000_000, 39, 250000,true),

    DEVOUT(ARMOUR, 0, new GameItem(12598, 1), new GameItem(22954, 1), 50_000_000, 59, 500000,true),

    ECHO(ARMOUR, 0, new GameItem(22954, 1), new GameItem(28945, 1), 100_000_000, 49, 500000,true),

    REALMBOOTS(ARMOUR, 0, new GameItem(28945, 1), new GameItem(27410, 1), 150_000_000, 39, 500000,true),

    /* Accessories */

    AMULET_OF_FURY(ACCESSORY,0, new GameItem(6585, 1), new GameItem(12436, 1), 2_500_000, 69, 12500,false),

    Berserker_necklace(ACCESSORY,0, new GameItem(11128, 1), new GameItem(23240, 1), 2_500_000, 69, 12500,true),

    OCCULT_NECKLACE(ACCESSORY,0, new GameItem(12002, 1), new GameItem(19720, 1), 5_000_000, 59, 12500, true),

    AMULET_OF_TORTURE(ACCESSORY,0, new GameItem(19553, 1), new GameItem(20366, 1), 5_000_000, 59, 12500,true),

    NECKLACE_OF_ANGUISH(ACCESSORY,0, new GameItem(19547, 1), new GameItem(22249, 1), 5_000_000, 59, 12500,true),

    TORMENTED_BRACELET(ACCESSORY,0, new GameItem(19544, 1), new GameItem(23444, 1), 10_000_000, 49, 12500,true),

    SUFFERING(ACCESSORY, 0, new GameItem(19710, 1), new GameItem(20657, 1), 10_000_000, 59, 50000,true),

    RING_OF_WEALTH_i(ACCESSORY, 0, new GameItem(12785, 1), new GameItem(20790, 1), 1_000_000, 59, 50000,true),

    RING_OF_WEALTH_i_1(ACCESSORY, 0, new GameItem(20790, 1), new GameItem(20789, 1), 2_500_000, 49, 50000,true),

    RING_OF_WEALTH_i_2(ACCESSORY, 0, new GameItem(20789, 1), new GameItem(20788, 1), 5_000_000, 39, 50000,true),

    RING_OF_WEALTH_I_3(ACCESSORY, 0, new GameItem(20788, 1), new GameItem(20787, 1), 10_000_000, 29, 50000,true),

    RING_OF_WEALTH_I_4(ACCESSORY, 0, new GameItem(20787, 1), new GameItem(20786, 1), 25_000_000, 19, 50000,true),

    BOXRING(ACCESSORY, 0, new GameItem(20786, 1), new GameItem(26939, 1), 750_000_000, 60, 500000,true),


    WARRIOR(ACCESSORY, 0, new GameItem(11772, 1), new GameItem(28316, 1), 75_000_000, 39, 50000,true),

   ZERKER(ACCESSORY, 0, new GameItem(11773, 1), new GameItem(28307, 1), 75_000_000, 39, 50000,true),

    SEERS(ACCESSORY, 0, new GameItem(11770, 1), new GameItem(28313, 1), 75_000_000, 39, 50000,true),

   ARCHERS(ACCESSORY, 0, new GameItem(11771, 1), new GameItem(28310, 1), 75_000_000, 39, 50000,true),

    BARROWS(ACCESSORY, 0, new GameItem(7462, 1), new GameItem(27112, 1), 25_000_000, 49, 50000,true),

    WRAPPED(ACCESSORY, 0, new GameItem(27112, 1), new GameItem(13372, 1), 90_000_000, 39, 500000,true),

    DEFENDER(ACCESSORY, 0, new GameItem(22322, 1), new GameItem(27552, 1), 150_000_000, 39, 500000,true),


    /* Miscellaneous */


    ANGLERHAT(MISC, 0, new GameItem(13258, 1), new GameItem(25592, 1), 2_500_000, 59, 50000,true),

    ANGLERTOP(MISC, 0, new GameItem(13259, 1), new GameItem(25594, 1), 2_500_000, 59, 50000,true),

    ANGLERBOTTOM(MISC, 0, new GameItem(13260, 1), new GameItem(25596, 1), 2_500_000, 59, 50000,true),

    ANGLERBOOTS(MISC, 0, new GameItem(13261, 1), new GameItem(25598, 1), 2_500_000, 59, 50000,true),

    HAMMER(MISC, 0, new GameItem(2347, 1), new GameItem(2949, 1), 7_500_000, 29, 100,true),

    DRAGON_AXE(MISC, 0, new GameItem(6739, 1), new GameItem(13241, 1), 5_000_000, 49, 50000,true),

    MININGHAT(MISC, 0, new GameItem(12013, 1), new GameItem(25549, 1), 2_500_000, 59, 50000,true),

    MININGTOP(MISC, 0, new GameItem(12014, 1), new GameItem(25551, 1), 2_500_000, 59, 50000,true),

    MININGBOTTOM(MISC, 0, new GameItem(12015, 1), new GameItem(25553, 1), 2_500_000, 59, 50000,true),

    MININGBOOTS(MISC, 0, new GameItem(12016, 1), new GameItem(25555, 1), 2_500_000, 59, 50000,true),

    DRAGON_PICKAXE(MISC, 0, new GameItem(11920, 1), new GameItem(13243, 1), 5_000_000, 49, 50000,true),

    Dragon_harpoon(MISC, 0, new GameItem(21028, 1), new GameItem(21031, 1), 5_000_000, 49, 50000,true),

    Greater_Skeleton(MISC, 0, new GameItem(33070, 1), new GameItem(27889, 1), 99_500_000, 55, 50000,true)



    ;


    private UpgradeType type;
    private GameItem required, reward;
    private long cost;
    private int successRate, levelRequired, xp;
    private boolean rare;

    UpgradeMaterials(UpgradeType type, int levelRequired, GameItem required, GameItem reward, long cost, int successRate, int xp, boolean rare) {
        this.type = type;
        this.levelRequired = levelRequired;
        this.required = required;
        this.reward = reward;
        this.cost = cost;
        this.successRate = successRate;
        this.xp = xp;
        this.rare = rare;
    }

    UpgradeMaterials(UpgradeType type, int levelRequired, GameItem required, GameItem reward, long cost, int successRate, int xp) {
        this.type = type;
        this.required = required;
        this.levelRequired = levelRequired;
        this.reward = reward;
        this.cost = cost;
        this.successRate = successRate;
        this.xp = xp;
        this.rare = false;
    }

    public static ArrayList<UpgradeMaterials> getForType(UpgradeType type) {
        ArrayList<UpgradeMaterials> upgradeablesArrayList = new ArrayList<>();
        for (UpgradeMaterials upgradeables : values()) {
            if (upgradeables.getType() == type){
                upgradeablesArrayList.add(upgradeables);
            }
        }
        return upgradeablesArrayList;
    }


    public enum UpgradeType {

        WEAPON, ARMOUR, ACCESSORY, MISC

    }

    private static final boolean DumpUpgradeItems = false;
    private static final boolean DumpUpgradeStats = false;
    static {
        if (DumpUpgradeItems) {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./temp/upgrade_items.json"));
                if (!new File("./temp/upgrade_items.json").exists()) {
                    Preconditions.checkState(new File("./temp/upgrade_items.json").mkdirs());
                }

                ArrayList<UpgradeMaterials> um = new ArrayList<>(Arrays.asList(UpgradeMaterials.values()));

                um.sort(Comparator.comparing(UpgradeMaterials::getLevelRequired));

                for (UpgradeMaterials upgradeMaterials : um) {
                    bufferedWriter.write("[TR][TD]"+ ItemDef.forId(upgradeMaterials.getRequired().getId()).getName() + "[/TD][TD]" + Misc.formatCoins(upgradeMaterials.getCost()) + "[/TD][TD]" + upgradeMaterials.getLevelRequired() + "[/TD][TD]" + upgradeMaterials.getXp() + "[/TD][TD]" + upgradeMaterials.getSuccessRate() + "%[/TD][TD]" + ItemDef.forId(upgradeMaterials.getReward().getId()).getName() + "[/TD][/TR]");
                    bufferedWriter.newLine();
                }


                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (DumpUpgradeStats) {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./temp/upgrade_items_stats.json"));
                if (!new File("./temp/upgrade_items_stats.json").exists()) {
                    Preconditions.checkState(new File("./temp/upgrade_items_stats.json").mkdirs());
                }

                ArrayList<UpgradeMaterials> um = new ArrayList<>(Arrays.asList(UpgradeMaterials.values()));

                um.sort(Comparator.comparing(UpgradeMaterials::getLevelRequired));

                for (UpgradeMaterials upgradeMaterials : um) {

                    bufferedWriter.write("[TR][TD]"+ ItemStats.forId(upgradeMaterials.getReward().getId()).getName() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getAstab() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getAslash() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getAcrush() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getAmagic() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getArange() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getDstab() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getDslash()  + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getDcrush()  + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getDmagic()  + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getDrange() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getStr()  + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getRstr()  + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getMdmg() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getPrayer()  + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getAttackSpeed() + "[/TD][TD]" +
                            "NONE[/TD][/TR]");

                    bufferedWriter.newLine();
                }


                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
