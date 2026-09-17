package io.zaryx.content.holiday;

import com.google.gson.Gson;
import java.util.*;

/** Verified room anchors; stable seed keeps an unfinished round unchanged. */
final class HolidayLayouts {
    static final int[][] DOORS={{11470,3099,3366,1},{11470,3101,3371,2},{11470,3103,3364,3},{11470,3106,3368,1},{11470,3109,3358,3},{131,3107,3367,2}};
    static void openDoors(io.zaryx.model.collisionmap.RegionProvider provider,int height){
        for(int[] d:DOORS){var r=provider.get(d[1],d[2]);r.removeObject(d[0],d[1],d[2],height,0,d[3]);r.removeWorldObject(new io.zaryx.model.world.objects.GlobalObject(d[0],d[1],d[2],0,d[3],0));}
    }
    // Four tested positions in each of three rooms. The roles still swap rooms.
    static final int[][][] GHOST_SPOTS={
        {{3099,3370},{3106,3364},{3111,3361}},
        {{3101,3370},{3104,3364},{3112,3362}},
        {{3099,3371},{3106,3362},{3110,3360}},
        {{3102,3371},{3103,3365},{3113,3361}}
    };
    // Each row places the three supplies in separate accessible parts of the manor.
    // The first two rows retain the hidden bookcase room.
    static final int[][][] SUPPLY_SPOTS={
        {{3098,3354},{3096,3359},{3105,3361}},
        {{3100,3354},{3096,3359},{3107,3359}},
        {{3104,3354},{3105,3365},{3111,3358}},
        {{3098,3365},{3105,3370},{3107,3360}}
    };
    // Three compact garden arrangements. Santa stays at the entrance; helpers,
    // the penguin and all three supply stations change places between rounds.
    static final int[][][] CHRISTMAS_GUEST_SPOTS={
        {{2994,3387},{2980,3386},{2992,3389}},
        {{2992,3386},{2982,3384},{2990,3389}},
        {{2995,3386},{2983,3387},{2991,3388}}
    };
    static final int[][][] CHRISTMAS_SUPPLY_SPOTS={
        {{2980,3381},{2990,3388},{2994,3383}},
        {{2982,3382},{2990,3387},{2996,3385}},
        {{2983,3384},{2992,3387},{2995,3380}}
    };
    static final int[][] CHRISTMAS_PENGUIN_SPOTS={{2975,3381},{2977,3382},{2983,3379}};
    static final int LEGACY_LAYOUTS=19;
    static final int NEW_LAYOUTS=54;
    static HolidayEvents.Layout round(HolidayEvents.Layout base,int seed) {
        if(base.holiday==Holiday.CHRISTMAS)return christmasRound(base,seed);
        Gson gson=new Gson();
        HolidayEvents.Layout result=gson.fromJson(gson.toJson(base),HolidayEvents.Layout.class);
        boolean legacy=seed<LEGACY_LAYOUTS;
        int slot=legacy?seed:Math.floorMod(seed-LEGACY_LAYOUTS,NEW_LAYOUTS);
        int variant=legacy?0:1+slot/18;
        int[][] rooms=GHOST_SPOTS[variant];
        int[][] permutations={{0,1,2},{0,2,1},{1,0,2},{1,2,0},{2,0,1},{2,1,0}};
        int[] permutation=permutations[Math.floorMod(slot,6)];
        for(var n:result.npcs)if(!n.home&&n.role>=0){int[] tile=rooms[permutation[n.role]];n.x=tile[0];n.y=tile[1];}
        for(var n:result.npcs)if(n.id==8368&&!n.home){n.x=3097;n.y=3364;}
        // Rotate the three supply identities among their already tested station footprints.
        // All three have the same one-tile footprint; crafting and decorative objects stay fixed.
        List<HolidayEvents.Station> supplies=new ArrayList<>();
        for(var s:result.objects)if(s.role>=0&&s.role<3)supplies.add(s);
        if(!legacy)for(int i=0;i<3;i++){
            supplies.get(i).x=SUPPLY_SPOTS[variant][i][0];
            supplies.get(i).y=SUPPLY_SPOTS[variant][i][1];
        }
        int shift=Math.floorMod(slot/6,3);
        int[] ids=supplies.stream().mapToInt(s->s.id).toArray();
        int[] roles=supplies.stream().mapToInt(s->s.role).toArray();
        for(int i=0;i<3;i++){supplies.get(i).id=ids[(i+shift)%3];supplies.get(i).role=roles[(i+shift)%3];}
        return result;
    }
    private static HolidayEvents.Layout christmasRound(HolidayEvents.Layout base,int seed) {
        Gson gson=new Gson();
        HolidayEvents.Layout result=gson.fromJson(gson.toJson(base),HolidayEvents.Layout.class);
        int slot=Math.floorMod(seed-LEGACY_LAYOUTS,NEW_LAYOUTS);
        int variant=slot/18;
        int[][] permutations={{0,1,2},{0,2,1},{1,0,2},{1,2,0},{2,0,1},{2,1,0}};
        int[] permutation=permutations[slot%6];
        for(var npc:result.npcs) {
            if(npc.home||npc.role==-1)continue;
            int[] tile=npc.role>=0?CHRISTMAS_GUEST_SPOTS[variant][permutation[npc.role]]:CHRISTMAS_PENGUIN_SPOTS[variant];
            npc.x=tile[0];npc.y=tile[1];
        }
        List<HolidayEvents.Station> supplies=new ArrayList<>();
        for(var station:result.objects)if(station.role>=0&&station.role<3)supplies.add(station);
        int[] ids=supplies.stream().mapToInt(s->s.id).toArray();
        int[] roles=supplies.stream().mapToInt(s->s.role).toArray();
        int shift=(slot/6)%3;
        for(int i=0;i<3;i++) {
            HolidayEvents.Station station=supplies.get(i);
            station.x=CHRISTMAS_SUPPLY_SPOTS[variant][i][0];
            station.y=CHRISTMAS_SUPPLY_SPOTS[variant][i][1];
            station.id=ids[(i+shift)%3];
            station.role=roles[(i+shift)%3];
        }
        return result;
    }
}
