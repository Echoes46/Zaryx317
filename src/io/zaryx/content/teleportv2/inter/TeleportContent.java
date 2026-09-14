package io.zaryx.content.teleportv2.inter;

import io.zaryx.content.teleportv2.inter.TeleportInterface.*;
import java.util.*;

/** Area rosters audited against configured spawns; IDs retain different loot variants. */
public final class TeleportContent {
    private TeleportContent() { }
    private static final Map<Teleport,int[]> AREAS=new HashMap<>();
    static {
        AREAS.put(DUNGEONS.ASGARNIANICE_DUNGEON,new int[]{2085,2841,465,466,467,468});
        AREAS.put(DUNGEONS.BRIMHAVENDUNGEON,new int[]{2026,270,273,274,1432,2084,891,247});
        AREAS.put(DUNGEONS.TAVERLYDUNGEON,new int[]{70,85,520,2834,1545,2006,291,268,241,1432,259,135,3023,3484});
        AREAS.put(DUNGEONS.FREMDUNG,new int[]{406,1047,419,435,417,437,427,411,421,481});
        AREAS.put(DUNGEONS.STRONGHOLDCAVE,new int[]{484,135,6,2514,2084});
        AREAS.put(DUNGEONS.SLAYERTOWER,new int[]{448,414,446,484,423,1543,11,415,453,2827,454,485,4,5,2,3,443,447,445,444,412,8});
        AREAS.put(DUNGEONS.KOUREND_CATACOMBS,new int[]{2098,135,85,891,2006,7276,7277,7278,1432,423,2235,975,7279,2514,7269,7258,7272,70,7268,274,4005,7274,7273,7275,2084,2827,3019,2854,1405,1401,1402,273,415,7244,2026,10,270});
        AREAS.put(DUNGEONS.MOUNTKARUULM,new int[]{8609,8610,8612,8614,2025,104});
        AREAS.put(DUNGEONS.LITHREKVAULT,new int[]{8031,8030});
        AREAS.put(DUNGEONS.FORTHOSDUNGEON,new int[]{2145,247});
        AREAS.put(DUNGEONS.CRYSTALCAVERN,new int[]{9026,9027,9028,9029,9030,9031,9032,9033,9034});
        AREAS.put(DUNGEONS.GWD,new int[]{3162,3163,3164,3165,2205,2206,2207,2208,2215,2216,2217,2218,3129,3130,3131,3132,2244,2234,2237,3137,3133,2235,2243,3140,3135,3138,2242,2245,3169,3168,3167,3166,2212,2213,2210,3161,3134,3139,3141,2233,2241,2211,2209});
        AREAS.put(PK.REV_CAVE,new int[]{7881,7931,7932,7933,7934,7935,7936,7937,7938,7939,7940});
        AREAS.put(PK.SLAYER_CAVE,new int[]{259,135,7864,2006,7878,7880,1432,7879,2026,249,264,437,423,415,7278});
        AREAS.put(PK.WILDY_GOD_WARS,new int[]{2237,2235,2242,2243,2244});
        AREAS.put(BOSSES.DAGANNOTH_KINGS,new int[]{2265,2266,2267});
    }
    public static int[] monsters(Teleport t) {
        int[] ids=AREAS.get(t);
        if(ids!=null)return ids.clone();
        int id=TeleportGuide.dropNpc(t);
        return id>0 && !(t instanceof MINIGAMES) ? new int[]{id} : new int[0];
    }
    public static String firstTab(Teleport t) {
        if(t instanceof SKILLING)return "Travel info";
        if(t instanceof MINIGAMES)return "Rewards";
        return monsters(t).length>1 ? "Monsters" : TeleportGuide.dropNpc(t)>0 ? "Drops" : "Area info";
    }
    public static String secondTab(Teleport t) {
        return t instanceof MINIGAMES ? "How to play" : t instanceof SKILLING ? "Nearby NPCs" : "Guide";
    }
    public static boolean textFirst(Teleport t) {
        return t instanceof SKILLING || TeleportGuide.dropNpc(t)<0 && monsters(t).length==0;
    }
    public static String howToPlay(MINIGAMES t) {
        switch(t) {
            case BLASTFURNACE:return "Deposit coins into the coffer. Put the ores and coal for your chosen bar on the conveyor belt. Keep the furnace running, then cool and collect the bars from the dispenser. Collect stored ores and bars before logging out.";
            case PYRAMID_PLUNDER:return "Enter the pyramid and disarm the room's trap. Search urns and loot the main chest before advancing. Try the doors until you find the way onward; you cannot return to earlier rooms. Bring food for failed searches and traps.";
            case ARIEL_FISHING:return "Speak to Alry the Angler and borrow his bird. You need 43 Fishing and 35 Hunter. Equip the cormorant glove, bring king worms or fish chunks, and click a fishing spot. Wait for the bird to return before catching again. Leave space for fish and pearls.";
            case PURO_PURO:return "Bring impling jars and a butterfly net. Catch implings that your Hunter level allows. Move through the maze to find rarer implings, then loot the filled jars for rewards.";
            case WINTERTODT:return "Bring an axe, knife, tinderbox and hammer. Chop bruma roots; fletch them into kindling for extra contribution. Feed a lit brazier, repair it when broken, and heal the pyromancer when needed. Bring food and warm clothing; earn points before the round ends.";
            case OUTLAST:return "Join while tournament registration is open. Follow the announced combat style and tournament rules. Win your assigned fights to advance through the bracket. Read the registration dialogue before joining.";
            case PEST_CONTROL:return "You need 40 combat to join the boat lobby. Protect the Void Knight and destroy all four portals before time runs out. Deal damage to portals yourself to qualify for commendations. Spend your points at the reward shop.";
            case WARRIORS_GUILD:return "Use a matching platebody, platelegs and full helm on the animator. Defeat the animated armour and collect guild tokens. Spend tokens fighting cyclopes for defenders; keep your current defender while working toward the next tier.";
            case DUEL_ARENA:return "Challenge another player. Review the equipment rules and any stakes together, then check the final confirmation carefully. Bring only what you intend to use in the duel.";
            case FIGHT_CAVES:return "Enter the cave with food and prayer supplies. Clear the waves, using walls to limit enemies attacking together. Prioritise dangerous ranged and magic enemies. On Jad, watch each attack animation and switch protection prayer before it lands.";
            case BARROWS:return "Bring a spade, food and prayer supplies. Search the burial mounds and defeat the brothers. Enter the tunnel, solve the route to the central chest, and claim the reward. Watch your prayer and preserve supplies for the final brother.";
            case INFERNO:return "Bring endgame equipment, combat-style switches and prayer supplies. Clear each wave while using pillars to separate enemies. Watch simultaneous ranged and magic attacks. In the final encounter, stay protected by the moving shield and deal with additional enemies.";
            case COX:return "Form your raid party at the entrance. Bring multiple combat styles and supplies. Clear the raid rooms together and handle each room's mechanics before moving on. Defeat Olm, then claim the raid chest; stay alive to protect your contribution.";
            case TOB:return "Form a team at the theatre entrance. Bring melee, ranged and magic switches with food and restores. Clear the boss rooms in order and coordinate movement and targets. Complete the final encounter before claiming the reward chest.";
            case ARBOGRAVE_SWAMP:return "Enter Raid of the Damned with your group and combat supplies. Work through the encounter stages together, clearing enemies before advancing. Bring combat-style switches and avoid ground attacks. Complete the raid to access its reward chest.";
            default:throw new IllegalArgumentException("Missing activity guide: "+t);
        }
    }
    public static String rewards(MINIGAMES t) {
        switch(t) {
            case BLASTFURNACE:return "Smelted bars and Smithing experience. Output depends on the ores supplied.";
            case PYRAMID_PLUNDER:return "Loot from urns, chests and sarcophagi, plus skill experience. Advance through the rooms to improve your opportunities.";
            case ARIEL_FISHING:return "Fish, skill experience and Molch pearls from aerial fishing.";
            case PURO_PURO:return "Hunter experience and the contents of filled impling jars. Each impling type has its own loot.";
            case WINTERTODT:return "Contribution points determine supply-crate rewards at the end of the round.";
            case PEST_CONTROL:return "Commendation points and coins for successful games with sufficient portal damage. Exchange points at the reward shop.";
            case WARRIORS_GUILD:return "Guild tokens from animated armour and successive defender upgrades from cyclopes.";
            case FIGHT_CAVES:return "Complete the encounter for the Fight Caves completion rewards, including the fire cape.";
            case INFERNO:return "Complete the encounter for Inferno completion rewards, including the infernal cape.";
            case OUTLAST:return "Tournament rewards depend on the event and your result. Check the tournament announcements and registration.";
            case DUEL_ARENA:return "Any agreed stakes depend on both players' confirmation. This is not a monster drop activity.";
            default:return "The reward preview shows the activity's chest loot, not ordinary monster drops.";
        }
    }
    public static List<String> firstPage(Teleport t) {
        if(t instanceof SKILLING)return cityLines(t,false);
        List<String> lines=new ArrayList<>();
        TeleportGuide.add(lines,t instanceof MINIGAMES ? "ACTIVITY REWARDS" : "AREA INFORMATION",
            t instanceof MINIGAMES ? rewards((MINIGAMES)t) : "This is an area or travel destination, not a single monster encounter.");
        TeleportGuide.add(lines,"PREPARATION",t instanceof MINIGAMES ? "Use How to play for the steps and supplies for this activity." :
            TeleportGuide.wildernessRisk(t) ? "Wilderness: prepare an escape route and only carry equipment you are prepared to lose." : "Safe arrival; check the surrounding area before venturing into the Wilderness.");
        return lines;
    }
    public static List<String> cityLines(Teleport t,boolean nearby) {
        List<String> lines=new ArrayList<>();
        if(nearby) {
            TeleportGuide.add(lines,"NEARBY NPCS","Configured NPCs near this arrival point:");
            List<String> names=CityNpcs.near(t);
            if(names.isEmpty())TeleportGuide.add(lines,"EXPLORING","No nearby NPCs are listed in the spawn data. Use the minimap to locate local facilities.");
            else for(String name:names)lines.addAll(TeleportGuide.wrap(name));
        } else {
            TeleportGuide.add(lines,"ARRIVAL",TeleportGuide.name(t)+". Teleporting here is free.");
            TeleportGuide.add(lines,"GETTING AROUND","Use Nearby NPCs to find people around the arrival point. Check the minimap for banks and facilities. Use + to save this city in Favourites.");
            TeleportGuide.add(lines,"TRAVEL TIP","Selecting a city does not move you. Press Teleport when ready; Previous repeats your last destination.");
        }
        return lines;
    }
}
