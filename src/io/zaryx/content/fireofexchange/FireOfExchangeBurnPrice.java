package io.zaryx.content.fireofexchange;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.zaryx.content.bosspoints.JarsToPoints;
import io.zaryx.content.upgrade.UpgradeMaterials;
import io.zaryx.model.Items;
import io.zaryx.model.definitions.ItemDef;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.shops.ShopAssistant;
import io.zaryx.model.shops.ShopItem;
import io.zaryx.model.world.ShopHandler;
import lombok.Getter;
import lombok.Setter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FireOfExchangeBurnPrice {

    public static int SHOP_ID;

    public static void init() {
        createBurnPriceShop();
    }

    public static int[] crystals = {33125,33126,33127,33128,33129,33130,33131,33132,33133,33134,33135,33136,33137,33138,33139,33140};

    public static void createBurnPriceShop() {
        Map<Integer, Integer> burnPrices = new HashMap<>();
        for (int i = 0; i < 60_000; i++) {
            int price = getBurnPrice(null, i, false);
            if (price > 0)
                burnPrices.put(i, price);
        }

        for (UpgradeMaterials value : UpgradeMaterials.values()) {
            int price = (int) (value.getCost() / 5);
            if (price > 0 && !burnPrices.containsKey(value.getReward().getId())) {
                burnPrices.put(value.getReward().getId(), price);
            }
        }

        for (int crystal : crystals) {
            burnPrices.remove(crystal);
        }

        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(burnPrices.entrySet());

        list.sort((a, b) -> {
            int comparison = b.getValue().compareTo(a.getValue());
            if (comparison == 0) {
                return a.getKey().compareTo(b.getKey());
            }

            return comparison;
        });

        List<ShopItem> shopItems = list.stream().map(it -> new ShopItem(it.getKey() + 1 /* shops need this +1 lol */,
                it.getValue(), it.getValue())).collect(Collectors.toList());
        SHOP_ID = ShopHandler.addShopAnywhere("Nomad's Dissolving Rates", shopItems);

        /*try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./temp/burn_prices.json"));
            if (!new File("./temp/burn_prices.json").exists()) {
                Preconditions.checkState(new File("./temp/burn_prices.json").mkdirs());
            }

            for (ShopItem shopItem : shopItems) {
                bufferedWriter.write("[TR][TD]" + ItemDef.forId(shopItem.getId()-1).getName() + "[/TD][TD]" + Misc.formatCoins(shopItem.getPrice()) + "[/TD][/TR]");
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /* UNCOMMENT IF YOU WANT TO DUMP NOMAD TO ITEM.JSON */
//        Item.DumpItemsIntoJson();
    }

    @Getter @Setter
    public static class Item {
        private int id;
        private String name;
        private int price;
        private int wikiPrice;

        // Constructors, getters, and setters
        public static void DumpItemsIntoJson() {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            List<Item> items = new ArrayList<>();
            try (FileWriter writer = new FileWriter("./temp/items.json")) {
                for (int i = 0; i < 45000; i++) {
                    ItemDef itemDefinition = ItemDef.forId(i);
                    if (itemDefinition != null && !itemDefinition.getName().contains("unknown")) {
                        int itemId = itemDefinition.getId();
                        int value = ShopAssistant.getItemShopValue(itemId);
                        int foeValue = FireOfExchangeBurnPrice.getBurnPrice(null, itemId, false);
                        if (foeValue < 0) {
                            foeValue = 0;
                        }

                        Item item = new Item();
                        item.setId(itemId);
                        item.setName(itemDefinition.getName());
                        item.setPrice(value);
                        item.setWikiPrice(foeValue);

                        items.add(item);
                    }
                }
                // Write the entire list of items to JSON
                String json = gson.toJson(items);
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void checkPrices() {
        for (int i = 0; i < 40_000; i++) {
            int shopBuyPrice = FireOfExchange.getExchangeShopPrice(i);
            int burn = getBurnPrice(null, i, false);
            if (shopBuyPrice != Integer.MAX_VALUE) {
                Preconditions.checkState(shopBuyPrice >= burn, "Item burns for more than shop price: " + i);
            }
        }
    }



    public static void openExchangeRateShop(Player player) {
        if (ShopHandler.getShopItems(SHOP_ID).isEmpty()) {
            createBurnPriceShop();
        }
        player.getShops().openShop(SHOP_ID);
        player.sendMessage("<icon=282> @red@You cannot buy anything here.@bla@ This interface only displays @pur@Nomad's Dissolving Rates!");
    }

    public static boolean hasValue(int itemId) {
        return getBurnPrice(null, itemId, false) != -1;
    }

    /**
     * Burning price.
     */
    public static int getBurnPrice(Player c, int itemId, boolean displayMessage) {
        if (Arrays.stream(JarsToPoints.JARS).anyMatch(it -> itemId == it)) {
            return JarsToPoints.FOE_POINTS;
        }

        switch (itemId) {
            case 33237:
                return 1;

            case 4087:
            case 4585:
            case 1149:
            case 1187:
                return 2500;

            case 7980:
                return 1_000_000;

            case 7981:
            case 7979:
            case 21275:
            case 23077:
            case 24466:
                return 10_000;

            case 11157:
            case 10933:  //Lumberjack Boots
            case 10939:  //Lumberjack Top
            case 10940:  //Lumberjack legs
            case 10941:  //Lumberjack Hat
            case 13258:  //Angler Hat
            case 13259:  //Angler Top
            case 13260:  //Angler Waders
            case 13261:  //Angler Boots
            case 5553:   //Rogue Top
            case 5554:   //Rogue Mask
            case 5555:   //Rogue Trousers
            case 5556:   //Rogue Gloves
            case 5557:   //Rogue Boots
            case 13642:  //Farmers Jacket
            case 13640:  //Farmers Boro Trousers
            case 13644:  //Farmers Boots
            case 13646:  //Farmers Strawhat
            case 12013:  //Prospector Helmet
            case 12014:  //Prospector Jacket
            case 12015:  //Prospector Legs
            case 12016:  //Prospector Boots
            case 20704:  //Pyromancer Garb
            case 20706:  //Pyromancer Robe
            case 20708:  //Pyromancer Hood
            case 20710:  //Pyromancer Boots
            case 20517:  //elder top
            case 20520:  //elder robe
            case 20595:  //elder hood
                return 20_000;

            case Items.TROUVER_PARCHMENT:
            case 20790:
                return 25_000;

            case 9032:
            case 3694:
            case 4722:  //barrows start
            case 4720:
            case 4716:
            case 4718:
            case 4714:
            case 4712:
            case 4708:
            case 4710:
            case 4736:
            case 4738:
            case 4732:
            case 4734:
            case 4753:
            case 4755:
            case 4757:
            case 4759:
            case 4745:
            case 4747:
            case 4749:
            case 4751:
            case 4724:
            case 4726:
            case 4728:
            case 4730:  // all barrows complete
            case Items.DRAGON_CHAINBODY:
            case Items.DRAGON_CHAINBODY_G:
            case Items.DRAGON_PLATEBODY_G:
            case Items.DRAGON_PLATELEGS_G:
            case Items.DRAGON_FULL_HELM_G:
            case Items.DRAGON_KITESHIELD_G:
            case Items.DRAGON_SQ_SHIELD_G:
            case Items.DRAGON_PLATESKIRT_G:
            case Items.DRAGON_BOOTS_G:
                return 50_000;

            case 9030:
            case 9042:
            case 2951:
            case 20366:
            case 22249:
            case 23444:
            case 23240:
            case 2577:  //ranger boots
            case Items.AMULET_OF_THE_DAMNED:
                return 100_000;

            case 6737:   //b ring
            case 6733:   //archer ring
            case 6731:   //seers ring
            case 11907:  //trident of the sea
            case 21892:  //dragon platebody
            case 21895:  //dragon kite
            case 12603:  //tyrannical ring
            case 12605:  //treasonaus ring
            case 11834:  //tassets
            case 13239:  //primordials
            case 13237:  //pegasion
            case 13235:  //eternal
            case 12924:  //blowpipe
            case 12926:  //blowpipe
            case 6:
            case 8:
            case 10:
            case 12:
                return 150_000;









            case 25322:
            case 25324:
            case 25326:
            case 25328:
            case 25330:
            case 25332:
            case 25334:
            case 27645:
            case 28128:
            case 7673:
            case 7671:

            case 24387:
            case 24389:
            case 24391:
            case 24393:

            case 9040:
            case 9028:
            case 2948:
            case 20789:
            case 22322:  //avernic
            case 21006:  //kodai wand
            case 22477:  //avernic hilt
                return 2_500_000;

            case 23848:  //crystal corrupt legs
            case 23842:  //crystal corrupt helm
            case 23845:  //crystal corrupt plate
                return 275_000;

            case 21018:  //ancestral
            case 21021:  //ancestral
            case 21024:  //ancestral
            case 22326:  //justiciar
            case 22327:  //justiciar
            case 22328:  //justiciar
            case 26520:
            case 26522:
            case 26524:
            case 26526:
                return 375_000;

            case 2950:
            case 10330:  //3rd age range begin
            case 10332:
            case 10334:
            case 10336:  //3rd age range finish
            case 10338:  //3rd age mage begin
            case 10340:
            case 10342:
            case 10344:  //3rd age mage finish
            case 10346:  //3rd age melee begin
            case 10348:
            case 10350:
            case 10352:  //3rd age melee finish
            case 12899:  //trident of swamp
            case Items.THIRD_AGE_PLATESKIRT:
            case Items.THIRD_AGE_BOW:
            case Items.THIRD_AGE_DRUIDIC_ROBE_TOP:
            case Items.THIRD_AGE_DRUIDIC_CLOAK:
            case Items.THIRD_AGE_DRUIDIC_ROBE_BOTTOMS:
            case Items.THIRD_AGE_DRUIDIC_STAFF:
            case Items.THIRD_AGE_LONGSWORD:
            case Items.THIRD_AGE_AXE:
            case Items.THIRD_AGE_PICKAXE:
            case 26488:
            case 26490:
            case 26492:
            case 26494:
            case 26496:
            case 26498:
            case 11838:
                return 500_000;




                //perks


            case 26710:
            case 26708:
                return 750_000;

            case 22547:  //craws bow u
            case 22550:  //craws bow
            case 22542:  //viggs mace u
            case 22545:  //viggs mace
            case 22552:  //thams sceptre u
            case 22555:  //thams sceptre
            case 10551:
            case 6570:
                return 800_000;

            case 10556:  //barb icons start
            case 10557:
            case 10558:
            case 10559:  //barb icons end
                return 1_000_000;

            case 24417:  //inquisitor mace
            case 23995:  //crystal blade
            case 24419:  //inquisitor helm
            case 24420:  //inquisitor plate
            case 24421:  //inquisitor skirt
            case Items.ZURIELS_HOOD:
            case Items.ZURIELS_ROBE_BOTTOM:
            case Items.ZURIELS_ROBE_TOP:
            case Items.STATIUSS_FULL_HELM:
            case Items.STATIUSS_PLATEBODY:
            case Items.STATIUSS_PLATELEGS:
            case 28067:
                return 1_250_000;

            case Items.VESTAS_CHAINBODY:
            case Items.VESTAS_PLATESKIRT:
            case Items.MORRIGANS_COIF:
            case Items.MORRIGANS_LEATHER_BODY:
            case Items.MORRIGANS_LEATHER_CHAPS:
            case Items.VESTAS_SPEAR:
            case Items.ZURIELS_STAFF:
                return 1_500_000;

            case Items.VESTAS_LONGSWORD:
            case Items.STATIUSS_WARHAMMER:
                return 2_000_000;

            case 26382:  //t helm
            case 26384:  //t chest
            case 26386:  //t legs
            case 26233:  //ancient gdsw
            case 26235:  //zart vambs
                return 25_000_000;

            case 18:
            case 20788:
            case 12773:
            case 12774:
            case 21295:
                return 2_500_000;



            case 25398:
            case 25389:
            case 25401:
            case 28173:
            case 28169:
            case 28171:
            case 28869:
            case 28338:
            case 25975:
            case 26903:
            case 26990:
            case 26992:
            return 15_000_000;



            case 11429:
            case 11481:
            case 22999:
            case 20786:
            case 11433:
                return 10_000_000;

            case 33144:
            case 33145:
            case 33146:
            case 33141:
            case 33142:
            case 33143:
            case 33184:
            case 21129:
            case 33148:
            case 33149:
                return 25_000_000;


            case 20486:
            case 20484:
            case 20483:
                return 31_000_000;

            case 20787:
                return 3_750_000;

            case 12006:
                return 400_000;




            case 25066:
            case 25063:
            case 25059:
                return 100_000;

            case 20997:
                return 20_000_000;

            case 26482:
            case 26486:
                return 35000;


            case 19553:
            case 19547:
            case 19544:
                return 20_000;

            case 22325:
            case 22323:
                return 20_000_000;

            case 11836://bandos boots
            case Items.BLACK_MASK_10:
                return 57500;
            case Items.MAGES_BOOK:
                return 5000;
            case Items.DAGONHAI_HAT:
            case Items.DAGONHAI_ROBE_BOTTOM:
            case Items.DAGONHAI_ROBE_TOP:
                return 100000;
            case Items.LONG_BONE:
                return 20000;
            case Items.BARRELCHEST_ANCHOR:
                return 100_000;
            case Items.DRAGON_2H_SWORD:
                return 125000;
            case 21902:  //dragon crossbow
                return 245000;
            case Items.RING_OF_THE_GODS:
                return 55000;

            case 11772://warrior i
                return 38000;
            case Items.SARACHNIS_CUDGEL:
                return 150_000;
            case Items.RING_OF_THIRD_AGE:
                return 99000;
            case Items.BONECRUSHER_NECKLACE:
                return 15000;
            case 11785://arma crossbow
                return 74000;
            case 11770://seers i
            case 11771://archer i
            case 11773://b ring i
                return 200000;
            case 21015://dihns bulwark
                return 453000;
            case 12929://serp helm
                return 153000;
            case 13265://abby dagger
            case 13271://abby dagger poison
                return 45300;
            case 21633://ancient wyvern shield
                return 153000;
            case 21000://twisted shield
                return 353000;
            case 23975://crystal body
            case 23971://crystal helm
            case 23979://crystal legs
                return 60000;
            case 22324://ghrazi rapier
                return 5_000_000;
            case 20716://tome of fire
                return 150000;
            case 22975://brimstone ring
                return 53000;
            case 12691://tyrannical ring i
            case 12692://tres ring (i)
            case Items.RING_OF_THE_GODS_I:
                return 200000;
            case 19478://light ballista
                return 16500;
            case 12902: //toxic staff of the dead
                return 200_000;
            case 11284: //dfs
            case 11283://dfs
                return 200_000;
            case 11826://army helm
            case 11828://army plate
            case 11830://arma leg
                return 250_000;
            case 13196://tanz helm
            case 13198://magma helm
                return 300_000;

            case Items.IMBUED_HEART:
                return 100_000;
            case 21003://elder maul
                return 414_000;
            case 21079://arcane scroll
                return 200_000;
            case 11832://bcp
                return 175_000;
            case 12922://tanz fang
                return 150_000;
            case 13263://bludgon
                return 275_000;
            case 21034://dex scroll
                return 214_000;
            case Items.NEITIZNOT_FACEGUARD:
            case Items.RING_OF_SUFFERING_I:
            case Items.SLED:
                return 200_000;
            case 22978:  //dragon hunter lance
                return 1_200_000;
            case 12821:  //spectral
            case 12825:  //arcane
                return 500_000;
            case 22981:  //ferocious gloves
                return 250_000;
            case 26374:  //zart cbow
                return 8_000_000;
            case 24422:  //nightmare staff
            case 24423:
            case 24424:
            case 24425:
                return 5_000_000;
            case 24517:  //eldritch orb
            case 24511:  //harmonised orb
            case 24514:  //volatile orb
                return 1_500_000;
            case 12817://ely
                return 2_250_000;
            case 2399: // FOE KEY
                return 5_000;
            //SKILLING ARTEFACTS
            case 11180://ancient coin
                return 10000;
            case 681://ancient talisman
                return 10000;
            case 9034://golden stat
                return 50000;
            //CHEST ARTIFACTS
            case 21547://small enriched bone
                return 25000;
            case 21549://medium enriched bone
                return 50000;
            case 21551://large enriched bone
                return 100000;
            case 21553://rare enriched bone
                return 200000;

            case 21012:
                return 500_000;
            case 25918:
                return 1_500_000;
            case 25916:
                return 750_000;
            //SLAYER HEADS
            case 2425:  //vorkath head
                return 50000;
            //special items
            case 21046:  //chest rate relic
            case 22316:  //sword of xeros
                return 500;
            //FOE PETS START
            case 30010://postie pete
                return 20000;

            case 10951:
                return 100000;




            case 33150:
            case 33151:
            case 33152:
            case 33153:
            case 33154:
            case 33155:
                return 100_000_000;


            case 30023:
                return 500000;


            case 33070:
                return 3000000;

            case 31014:
            case 25519:
            case 26348:
            case 10533:
            case 33208:
            case 25750:
            case 25751:
            case 25752:
            case 25749:
                return 22_500_000;

            case 30012://toucan
            case 30011://imp
            case 7582:
                return 19500;
            case 30013://penguin king
                return 27500;
            case 30014://klik
                return 10_000_000;
            case 30015://melee pet
            case 30016://range pet
            case 30017://magic pet
                return 487500;
            case 30018://healer
            case 30019://prayer
                return 250_000;
            case 30020://corrupt beast
                return 25_000_000;
            case 30021://roc pet
                return 25_000_000;
            case 30022://kratos pet
            case 27383:
            case 27354:
            case 27352:
                return 75_000_000;
            case 23939://seren
                return 5_000_000;
            case 3128:
                return 10_000_000;
            //FOE PETS END
/*            case 33128:
            case 33132:
            case 33136:
            case 33140:
                return 10000;*/

            case 11802:
            case 11804:
            case 11806:
            case 11808:
                return 300_000;
            // New List
            case 13576:
                return 550_000;
            case 19481:
                return 550_000;
            case 4151:
                return 100_000;
            case 20784:
                return 250_000;
            case 6585:
                return 25_000;
            case 12002:
                return 12_500;
            case 12004:
                return 50000;
            case 11924:
            case 11926:
                return 100_000;
            case 12806:
            case 12807:
                return 200_000;
            case 21028:
                return 250_000;
            case 11920:
            case 2579:
                return 250_000;
            case 6739:
                return 25_000;
            case 24664:
                return 1_250_000;
            case 24666:
                return 1_250_000;
            case 24668:
                return 1_250_000;

            case 691://foe cert
                return 10000;
            case 692://foe cert
                return 25000;
            case 693://foe cert
                return 50000;
            case 696://foe cert
                return 250000;
            case 8866://uim key
                return 100;
            case 8868://perm uim key
                return 4000;
            case 33073://DWARF_OVERLOAD
                return 3_000_000;
            case 33074://PK_MASTER
                return 3_000_000;
            case 33090://MAGIC_MASTER
                return 3_000_000;
            case 33091://YIN_YANG
                return 3_000_000;
            case 33102://NOVICE_ZERK
                return 3_000_000;
            case 33103://NOVICE_MAGICIAN
                return 3_000_000;
            case 33113:
            case 28951:
            case 33104://NOVICE_RANGER
                return 3_000_000;
            case 33105://PRO_ZERK
                return 3_000_000;
            case 33106://PRO_MAGICIAN
                return 3_000_000;
            case 33107://PRO_RANGER
                return 3_000_000;
            case 33108://SWEDISH_SWINDLE
                return 3_000_000;
            case 33117://MONK_HEALS
            case 33111:
            case 33109:
                return 3_000_000;
            case 33226:
                return 6_000_000;
            case 33118://DRAGON_FIRE
                return 3_000_000;
            case 33119://OVERLOAD_PROTECTION
                return 3_000_000;
            case 33121://CANNON_EXTENDER
                return 3_000_000;
            case 33079://RUNECRAFTER
                return 3_000_000;
            case 33080://PRO_FLETCHER
                return 3_000_000;
            case 33087://SKILLED_THIEF
                return 3_000_000;
            case 33088://CRAFTING_GURU
                return 3_000_000;
            case 33089://HOT_HANDS
                return 3_000_000;
            case 33093://DEMON_SLAYER
                return 3_000_000;
            case 33094://SLAYER_MASTER
                return 3_000_000;
            case 33095://PYROMANIAC
                return 3_000_000;
            case 33096://SKILLED_HUNTER
                return 3_000_000;
            case 33097://MOLTEN_MINER
                return 3_000_000;
            case 33098://WOODCHIPPER
                return 3_000_000;
            case 33099://BARE_HANDS
                return 3_000_000;
            case 33100://BARE_HANDS_X3
                return 3_000_000;
            case 33101://PRAYING_RESPECTS
                return 3_000_000;
            case 33122://PURE_SKILLS
                return 3_000_000;
            case 33077://IRON_GIANT
                return 3_000_000;
            case 33078://SLAYER_OVERRIDE
                return 3_000_000;
            case 33072://THE_FUSIONIST
                return 3_000_000;
            case 33075://WILDY_SLAYER
                return 3_000_000;
            case 33076://SNEAKY_SNEAKY
                return 3_000_000;
            case 33081://CHISEL_MASTER
                return 3_000_000;
            case 33082://AVAS_ACCOMPLICE
                return 3_000_000;
            case 33083://DEEPER_POCKETS
                return 3_000_000;
            case 33084://RECHARGER
                return 3_000_000;
            case 33085://MAGIC_PAPER_CHANCE
                return 3_000_000;
            case 33086://DRAGON_BAIT
                return 3_000_000;
            case 33092://FOUNDRY_MASTER
                return 3_000_000;
            case 33114://CASKET_MASTER
                return 3_000_000;
            case 33115://VOTING_KING
                return 3_000_000;
            case 33116://PET_LOCATOR
                return 3_000_000;
            case 33120://LUCKY_COIN
                return 3_000_000;
            case 33123://PC_PRO
                return 3_000_000;
            case 33124://SLAYER_GURU
                return 3_000_000;



            case 26463:
            case 26465:
            case 26467:
            case 26469:
            case 26471:
            case 26473:
            case 26475:
            case 26477:
            case 22111:
            case 11335:
               return 2_500_000;


                //newperks
            case 33222:
            case 33221:
            case 33220:
            case 33231:
            case 33216:
            case 33217:
                return 6_000_000;


            case 33162://WHIP
                return 10_000_000;
            case 33161://SCYTHE
            case 33160://TBOW
                return 22_500_000;


            case 33183:
            case 33186:
            case 33187:
            case 33188:
            case 33808:
            case 33814:
            case 33800:
            case 33802:
            case 33804:
            case 33810:
            case 33806:
            case 33009:
            case 33001:
            case 33002:
            case 33005:
                return 30_000_000;


            case 12422:
            case 12437:
            case 12600:
                return 500_000;
            case 12954:
                return 25_000;
            case 26719:
            case 26718:
                return 1_000_000;
            case 26720:
                return 500_000;
            case 26714:
            case 26715:
            case 26716:
            case 26221:
            case 26223:
            case 26225:
                return 1_500_000;
            case 26227:
            case 26229:
                return 250_000;
            case 33159:
                return 7_500_000;

            default:
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.getReward().getId() == itemId) {
                        return (int) (value.getCost() / 5);
                    }
                }
                return -1;
        }
    }


}
