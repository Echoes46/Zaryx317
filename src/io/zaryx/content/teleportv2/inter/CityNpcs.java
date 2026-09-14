package io.zaryx.content.teleportv2.inter;
import com.google.gson.*;
import io.zaryx.Server;
import io.zaryx.model.definitions.NpcDef;
import java.nio.file.*;
import java.util.*;
/** Reads configured positions, so dead or wandering NPCs do not change the travel guide. */
final class CityNpcs {
    private static List<int[]> cached;
    static synchronized List<String> near(TeleportInterface.Teleport t) {
        if(cached==null) {
            List<int[]> entries=new ArrayList<>();
            Path base=Paths.get(Server.getDataDirectory(),"cfg","npc");
            List<Path> files=new ArrayList<>();files.add(base.resolve("npc_spawns.json"));
            try {
                for(String folder:new String[]{"spawns","osrsspawns"})
                    try(var paths=Files.list(base.resolve(folder))) {paths.filter(p->p.toString().endsWith(".json")).sorted().forEach(files::add);}
                for(Path file:files) {
                    for(JsonElement element:JsonParser.parseString(Files.readString(file)).getAsJsonArray()) {
                        JsonObject n=element.getAsJsonObject(), pos=n.has("position")?n.getAsJsonObject("position"):n;
                        entries.add(new int[]{n.get("id").getAsInt(),pos.get("x").getAsInt(),pos.get("y").getAsInt(),pos.has("height")?pos.get("height").getAsInt():pos.has("z")?pos.get("z").getAsInt():0});
                    }
                }
                cached=entries;
            } catch(Exception ex) {
                org.slf4j.LoggerFactory.getLogger(CityNpcs.class).warn("Could not load city NPC guide",ex);
                return Collections.emptyList();
            }
        }
        var pos=t.getPosition();Map<String,int[]> closest=new TreeMap<>();
        for(int[] n:cached) {
            int dx=n[1]-pos.getX(),dy=n[2]-pos.getY(),distance=Math.max(Math.abs(dx),Math.abs(dy));
            if(n[3]!=pos.getHeight() || distance>48)continue;
            String name=NpcDef.forId(n[0]).getName();
            if(name==null || name.equalsIgnoreCase("null") || name.equalsIgnoreCase("unknown"))continue;
            int[] old=closest.get(name);
            if(old==null || distance<old[0])closest.put(name,new int[]{distance,dx,dy});
        }
        List<String> result=new ArrayList<>();
        closest.entrySet().stream().sorted(Comparator.comparingInt(e->e.getValue()[0])).limit(20).forEach(e->{
            int[] d=e.getValue();String direction=(d[2]>0?"N":d[2]<0?"S":"")+(d[1]>0?"E":d[1]<0?"W":"");
            result.add(e.getKey()+" - "+d[0]+" tiles "+direction);
        });return result;
    }
}
