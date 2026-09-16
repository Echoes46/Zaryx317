package io.zaryx.content.holiday;

import com.google.gson.Gson;
import io.zaryx.Server;
import io.zaryx.ServerConfiguration;
import io.zaryx.model.collisionmap.*;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class HolidayAssetTest {
 @Test void allRoundLayoutsAreReachable() throws Exception {for(int seed=0;seed<18;seed++)layoutUsesExistingAssetsAndReachableStations(seed);}
 void layoutUsesExistingAssetsAndReachableStations(int seed) throws Exception {
  var config=Server.class.getDeclaredField("configuration");config.setAccessible(true);Object old=config.get(null);config.set(null,ServerConfiguration.getDefault());
  var field=RegionProvider.class.getDeclaredField("regions");field.setAccessible(true);
  Map<Integer,Region> regions=(Map<Integer,Region>)field.get(RegionProvider.getGlobal());Map<Integer,Region> previous=new HashMap<>(regions);regions.clear();
  try {
   ObjectDef.loadConfig();
   HolidayEvents.Layout[] layouts=new Gson().fromJson(Files.readString(Path.of("etc/cfg/holiday-events.json")),HolidayEvents.Layout[].class);
   for(int i=0;i<layouts.length;i++)if(layouts[i].holiday==Holiday.HALLOWEEN)layouts[i]=HolidayLayouts.round(layouts[i],seed);
   Set<Integer> required=new HashSet<>();
   for(var layout:layouts){required.add(hash(layout.entryX,layout.entryY));for(var n:layout.npcs)required.add(hash(n.x,n.y));for(var o:layout.objects)required.add(hash(o.x,o.y));}
   var loader=Region.class.getDeclaredMethod("loadMap",RegionData.class);loader.setAccessible(true);
   try(var input=new java.io.DataInputStream(Files.newInputStream(Path.of("etc/mapdata/map_index")))){
    int count=input.readUnsignedShort();for(int i=0;i<count;i++){
     int region=input.readUnsignedShort(),land=input.readUnsignedShort(),objects=input.readUnsignedShort();
     if(required.contains(region)){
      assertTrue(Files.exists(Path.of("etc/mapdata/index4/"+land+".gz")));assertTrue(Files.exists(Path.of("etc/mapdata/index4/"+objects+".gz")));
      regions.put(region,new Region(RegionProvider.getGlobal(),region,false));loader.invoke(null,new RegionData(region,land,objects));
     }
    }
   }
   HolidayLayouts.openDoors(RegionProvider.getGlobal(),0);
   List<String> problems=new ArrayList<>();
   for(var layout:layouts){
    for(var node:layout.objects){ObjectDef def=ObjectDef.getObjectDef(node.id);assertNotNull(def);assertNotNull(def.name,"Object "+node.id);
     int w=node.face%2==0?def.xLength:def.yLength,h=node.face%2==0?def.yLength:def.xLength;
     if(!clear(node.x,node.y,w,h)){
      String suggestion="none";outer:for(int r=1;r<=8;r++)for(int dx=-r;dx<=r;dx++)for(int dy=-r;dy<=r;dy++)if(clear(node.x+dx,node.y+dy,w,h)){suggestion=(node.x+dx)+","+(node.y+dy);break outer;}
      problems.add("Blocked object "+node.id+" at "+node.x+","+node.y+" -> "+suggestion);
     }
    }
    for(var n:layout.npcs)if(!clear(n.x,n.y,1,1)){
      String suggestion="none";outer:for(int r=1;r<=8;r++)for(int dx=-r;dx<=r;dx++)for(int dy=-r;dy<=r;dy++)if(clear(n.x+dx,n.y+dy,1,1)){suggestion=(n.x+dx)+","+(n.y+dy);break outer;}
      problems.add("Blocked npc "+n.id+" at "+n.x+","+n.y+" -> "+suggestion);
    }
    assertTrue(clear(layout.entryX,layout.entryY,1,1),"Entry "+layout.holiday);
   }
   assertTrue(problems.isEmpty(),String.join("; ",problems));
   for(var layout:layouts){
    Set<String> occupied=new HashSet<>();
    for(var o:layout.objects){ObjectDef d=ObjectDef.getObjectDef(o.id);int w=o.face%2==0?d.xLength:d.yLength,h=o.face%2==0?d.yLength:d.xLength;
     for(int dx=0;dx<w;dx++)for(int dy=0;dy<h;dy++)if(!occupied.add((o.x+dx)+","+(o.y+dy)))problems.add("Scenery overlaps at "+o.x+","+o.y);
     RegionProvider.getGlobal().get(o.x,o.y).addObject(o.id,o.x,o.y,0,10,o.face);
    }
    for(var n:layout.npcs)assertFalse(occupied.contains(n.x+","+n.y),"NPC inside scenery: "+n.id);
    if(layout.holiday==Holiday.HALLOWEEN){
     assertTrue(clear(3096,3358,1,1),"Bookcase arrival must be clear");
     assertTrue(clear(3098,3357,1,1),"Lever arrival must be clear");
     var walker=new io.zaryx.model.entity.player.Player(null){@Override public void updateController(){}};
     walker.moveTo(new io.zaryx.model.entity.player.Position(layout.entryX,layout.entryY,0));walker.getNextPlayerMovement();
     for(var station:layout.objects)if(station.role>=0&&station.role<3){
      var def=ObjectDef.getObjectDef(station.id);
      io.zaryx.model.entity.player.PathFinder.getPathFinder().findRoute(walker,station.x,station.y,true,def.xLength,def.yLength);
      for(int tick=0;tick<100;tick++)walker.getNextPlayerMovement();
      assertTrue(Math.abs(walker.absX-station.x)<=1&&Math.abs(walker.absY-station.y)<=1,"Route seed "+seed+" station "+station.id+" stopped "+walker.absX+","+walker.absY+" target "+station.x+","+station.y);
     }
    }
    Set<String> reached=new HashSet<>();ArrayDeque<int[]> queue=new ArrayDeque<>();queue.add(new int[]{layout.entryX,layout.entryY});reached.add(layout.entryX+","+layout.entryY);
    while(!queue.isEmpty()){int[] at=queue.remove();for(int[] step:new int[][]{{1,0},{-1,0},{0,1},{0,-1}}){
     int x=at[0]+step[0],y=at[1]+step[1];if(Math.abs(x-layout.entryX)>30||Math.abs(y-layout.entryY)>30||reached.contains(x+","+y))continue;
     if(RegionProvider.getGlobal().get(at[0],at[1]).canMove(at[0],at[1],x,y,0,1,1)){reached.add(x+","+y);queue.add(new int[]{x,y});}
    }}
    for(var n:layout.npcs)if(!n.home&&!reached.contains(n.x+","+n.y))problems.add("Unreachable NPC "+n.id+" at "+n.x+","+n.y+" suggestion "+suggest(reached,n.x,n.y,1,1));
    for(var o:layout.objects)if(o.role>=0){ObjectDef d=ObjectDef.getObjectDef(o.id);if(!adjacent(reached,o.x,o.y,d.xLength,d.yLength))problems.add("Unreachable station "+o.id+" at "+o.x+","+o.y+" suggestion "+suggest(reached,o.x,o.y,d.xLength,d.yLength));}
   }
   assertTrue(problems.isEmpty(),String.join("; ",problems));
  }finally{regions.clear();regions.putAll(previous);config.set(null,old);}
 }
 private static String suggest(Set<String> reached,int x,int y,int w,int h){
  for(int r=1;r<30;r++)for(int dx=-r;dx<=r;dx++)for(int dy=-r;dy<=r;dy++)if(clear(x+dx,y+dy,w,h)&&adjacent(reached,x+dx,y+dy,w,h))return (x+dx)+","+(y+dy);return "none";
 }
 private static boolean adjacent(Set<String> reached,int x,int y,int w,int h){
  for(int dx=-1;dx<=w;dx++)for(int dy=-1;dy<=h;dy++)if((dx==-1||dx==w||dy==-1||dy==h)&&reached.contains((x+dx)+","+(y+dy)))return true;return false;
 }
 private static int hash(int x,int y){return ((x>>6)<<8)|(y>>6);}
 private static boolean clear(int x,int y,int width,int height){
  for(int dx=0;dx<width;dx++)for(int dy=0;dy<height;dy++)if((RegionProvider.getGlobal().getClipping(x+dx,y+dy,0)&0x200100)!=0)return false;return true;
 }
}
