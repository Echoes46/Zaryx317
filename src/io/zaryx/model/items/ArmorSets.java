package io.zaryx.model.items;

import io.zaryx.model.entity.player.Player;

public class ArmorSets {
    private static int[][] ArmorSets = {
            /*bronze_(l)*/		 { 12960, 1155, 1117, 1075, 1189},//complete
            /*bronze_(sk)*/		 { 12962, 1155, 1117, 1087, 1189},//complete
            /*iron_(l)*/		 { 12972, 1153, 1115, 1067, 1191},//complete
            /*iron_(sk)*/		 { 12974, 1153, 1115, 1081, 1191},//complete
            /*steel_(l)*/		 { 12984, 1157, 1119, 1069, 1193},//complete
            /*steel_(sk)*/		 { 12986, 1157, 1119, 1083, 1193},//complete
            /*black_(l)*/		 { 12988, 1165, 1125, 1077, 1195},//complete
            /*black_(sk)*/		 { 12990, 1165, 1125, 1089, 1195},//complete
            /*mithril_(l)*/		 { 13000, 1159, 1121, 1071, 1197},
            /*mithril_(sk)*/	 { 13002, 1159, 1121, 1085, 1197},
            /*adamant_(l)*/		 { 13012, 1161, 1123, 1073, 1199},
            /*adamant_(sk)*/	 { 13014, 1161, 1123, 1091, 1199},
            /*rune_(l)*/		 { 13024, 1163, 1127, 1079, 1201},
            /*rune_(sk)*/		 { 13026, 1163, 1127, 1093, 1201},
            /*ahrim's*/			 { 12881, 4708, 4712, 4714, 4710},//complete
            /*dharok's*/		 { 12877, 4716, 4720, 4722, 4718},//complete
            /*guthan's*/		 { 12873, 4724, 4728, 4730, 4726},//complete
            /*karil's*/			 { 12883, 4732, 4736, 4738, 4734},//complete
            /*torag's*/			 { 12879, 4745, 4749, 4751, 4747},//complete
            /*verac's*/			 { 12875, 4753, 4757, 4759, 4755},//complete
            /*green_dragonhide*/ { 12865, 12518, 1135, 1099, 1065},
            /*blue_dragonhide*/	 { 12867, 12520, 2499, 2493, 2487},//complete
            /*red_dragonhide*/	 { 12869, 12522, 2501, 2495, 2489},//complete
            /*black_dragonhide*/ { 12871, 12524, 2503, 2497, 2491},//complete
            //infinity_robes*/	 { 11874, 6918, 6916, 6924},
            //*splitbark*/		 { 11876, 3385, 3387, 3389},
            /*steel_(t)*/		 { 20376, 20184, 20187, 20193, 20196},//complete
            /*steel_(g)*/		 { 20382, 20169, 20172, 20178, 20181},//complete
            /*black_(t)*/		 { 12992, 2587, 2583, 2585, 2589},//complete
            /*black_(g)*/		 { 12996, 2595, 2591, 2593, 2597},//complete
            /*adamant_(t)*/		 { 13016, 2605, 2599, 2601, 2603},//complete
            /*adamant_(g)*/		 { 13020, 2613, 2607, 2609, 2611},//complete
            /*rune_(t)*/		 { 13028, 2627, 2623, 2625, 2629},//complete
            /*rune_(g)*/		 { 13032, 2619, 2615, 2617, 2621},//complete
            //*enchanted_robe*/	 { 11902, 7400, 7399, 7398},
            //*wizard_robe_(t)*/	 { 11904, 7396, 10687, 7388},
            //*wizard_robe_(g)*/	 { 11906, 7394, 10686, 7386},
            /*guthix*/			 { 13048, 2673, 2669, 2671, 2675},//complete
            /*saradomin*/		 { 13040, 2665, 2661, 2663, 2667},//complete
            /*zamorak*/			 { 13044, 2657, 2653, 2655, 2659},//complete
            /*gilded*/			 { 13036, 3486, 3481, 3483, 3488},//complete
            /*armadyl*/			 { 13052, 12470, 12472, 12476, 12478},
            /*bandos*/			 { 13056, 12480, 12482, 12486, 12488},
            /*ancient*/			 { 13062, 12460, 12462, 12466, 12468},
            /*sara d'hide*/		{  13163, 19933, 10384, 10386, 10388},
            /*guthix d'hide*/	{  13165, 19927, 10378, 10380,10376} ,
            /*zammy d'hide*/	{  13161, 19936, 10368, 10370, 10372},
            /*bandos d'hide*/	{  13167, 19924, 12498, 12500, 12502},
            /*arma d'hide*/		{  13169, 19930, 12506, 12508, 12510},
            /*ancient d'hide*/	{  13171, 19921, 12490, 12492, 12494},
            /*sara page set*/			{13149, 3827, 3828, 3829,3830},
            /*zammy	page set*/			{13151, 3831, 3832, 3833, 3834},
            /*guthix page set*/			{13153, 3835, 3836, 3837, 3838},
            /*Ancient page set*/			{13159, 12621, 12622, 12623, 12624},
            /*armadyl page set*/			{13157, 12617, 12618, 12619, 12620},
            /*bandos page set*/			{13155, 12613, 12614, 12615, 12616},
            /*Obsidian Set*/					{21279, 21298, 21301, 21304, -1},
            /*Dragon Platebody Set*/			{21882, 11335, 21892, 4087, 21895},
            /*Justiciar set*/			{22438, 22326, 22327, 22328, -1},
            /*Mystic set (Light)*/			{23110, 4109, 4111, 4113, 4115, 4117},
            /*Mystic set (Blue)*/			{23113, 4089, 4091, 4093, 4095, 4097},
            /*Mystic set (Dark)*/			{23116, 4099, 4101, 4103, 4105, 4107},
            /*Mystic set (Dusk)*/			{23119, 23047, 23050, 23053, 23053, 23059},
            /*Cannon*/				/*	{12863, 6, 8, 10, 12},*/
    };
    /*	public class robeSet {
              public static int [][] robeSets = {
             /*sara/	{, 10440, 10446, 10452, 10458, 10464, 10470},
             /*guthix/	{, 10442, 10448, 10454, 10462, 10466, 10472},
        /*zammy/	{, 10444, 10450, 10456, 10460, 10468, 10474},
          /*ancient/	{, 12193, 12195, 12197, 12199, 12201, 12203},
          /*armadyl/	{, 12253, 12255, 12257, 12259, 12261, 12263},
          /*bandos/	{, 12265, 12267, 12269, 12271, 12273, 12275},
          };
        public class pageSet {
            public static int [][] pageSet = {
                sara	{13149, 3827, 3828, 3829,3930},
                zammy	{13151, 3831, 3832, 3833, 3834},
                guthix	{13153, 3835, 3836, 3837, 3838},
                Ancient	{13159, 12621, 12622, 12623, 12624},
                armadyl	{13157, 12617, 12618, 12619 12620},
                bandos	{13155, 12613, 12614, 12615, 12616},
            }
        };
         */
    public static boolean isSet(int id) {
        return getSet(id) != null;
    }

    private static int[] getSet(int id) {
        for (int i = 0; i < ArmorSets.length; i++)
            if(ArmorSets[i][0] == id)
                return ArmorSets[i];
        return null;
    }

    private static int getSetNum(int id) {
        for (int i = 0; i < ArmorSets.length; i++)
            if(ArmorSets[i][0] == id)
                return i;
        return 100;
    }

    public static void handleSet(Player c, int id) {
        if (!c.getItems().playerHasItem(id)) {
            return;
        }
        int properDataName = getSetNum(id);
        int [] data = getSet(id);
        if(data == null)
            return;
        if (c.getItems().freeSlots() < (data.length - 2)) {
            c.sendMessage("You do not have enough free inventory slots to do this.");
            return;
        }
        c.getItems().deleteItem(id, 1);
        c.sendMessage("You unpack the @blu@"+ c.getItems().getItemName(ArmorSets[properDataName][0]) + "!");
        for(int i = 1; i < data.length; i++) {
            c.getItems().addItem(data[i], 1);
        }
    }
    public static boolean isPackSet(int id) {
        return getRepackSet(id) != 1;
    }
    public static int getRepackSet(int id) {
        for (int i = 0; i < ArmorSets.length; i++) {
            if(ArmorSets[i][1] == id) {
                return i;
            }
            if(ArmorSets[i][2] == id) {
                return i;
            }
            if(ArmorSets[i][3] == id) {
                return i;
            }
            if(ArmorSets[i][4] == id) {
                return i;
            }

        }
        return -1;
    }

    public static boolean hasFullSet(Player c, int itemId) {
        for (int i = 0; i < ArmorSets.length; i++)
            if(ArmorSets[i][1] == itemId)
                if(c.getItems().playerHasItem(ArmorSets[i][2]))
                    if(c.getItems().playerHasItem(ArmorSets[i][3]))
                        if(c.getItems().playerHasItem(ArmorSets[i][4]))
                            return true;
        for(int i = 0; i < ArmorSets.length; i++)
            if(ArmorSets[i][2] == itemId)
                if(c.getItems().playerHasItem(ArmorSets[i][1]))
                    if(c.getItems().playerHasItem(ArmorSets[i][3]))
                        if(c.getItems().playerHasItem(ArmorSets[i][4]))
                            return true;
        for(int i = 0; i < ArmorSets.length; i++)
            if(ArmorSets[i][3] == itemId)
                if(c.getItems().playerHasItem(ArmorSets[i][1]))
                    if(c.getItems().playerHasItem(ArmorSets[i][2]))
                        if(c.getItems().playerHasItem(ArmorSets[i][4]))
                            return true;
        for(int i = 0; i < ArmorSets.length; i++)
            if(ArmorSets[i][4] == itemId)
                if(c.getItems().playerHasItem(ArmorSets[i][1]))
                    if(c.getItems().playerHasItem(ArmorSets[i][2]))
                        if(c.getItems().playerHasItem(ArmorSets[i][3]))
                            return true;
        return false;
    }




    public static void packSet(Player c, int id){
        c.sendMessage("You pack the items and create a @blu@"+ c.getItems().getItemName(ArmorSets[id][0]) + "!");
        c.getItems().deleteItem(ArmorSets[id][1], 1);
        c.getItems().deleteItem(ArmorSets[id][2], 1);
        c.getItems().deleteItem(ArmorSets[id][3], 1);
        c.getItems().deleteItem(ArmorSets[id][4], 1);
        c.getItems().addItem(ArmorSets[id][0], 1);
    }
}
