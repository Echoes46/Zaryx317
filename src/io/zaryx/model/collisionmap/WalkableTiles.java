package io.zaryx.model.collisionmap;

import io.zaryx.Server;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Shared config expansion and application for reloads and individual Owner edits. */
public final class WalkableTiles {
    private WalkableTiles() { }
    private static Map<Tile,Boolean> active=Collections.emptyMap();
    public static Path config() { return Paths.get(Server.getDataDirectory(),"cfg","obj","walkable_tiles.cfg"); }
    public static final class Tile {
        public final int x,y,z;
        public Tile(int x,int y,int z) {this.x=x;this.y=y;this.z=z;}
        @Override public boolean equals(Object other) {
            if(!(other instanceof Tile))return false;Tile t=(Tile)other;return x==t.x && y==t.y && z==t.z;
        }
        @Override public int hashCode() {return Objects.hash(x,y,z);}
    }
    public static Map<Tile,Boolean> parse(List<String> lines) {
        Map<Tile,Boolean> tiles=new LinkedHashMap<>();int number=0;
        for(String original:lines) {
            number++;String line=original.split("//",2)[0].trim();
            if(line.isEmpty())continue;
            try {
                String[] parts=line.split("\\s+");
                if(parts.length!=4)throw new IllegalArgumentException("Expected x y plane state");
                int[] xs=range(parts[0],16383),ys=range(parts[1],16383),zs=range(parts[2],3);
                boolean walkable;
                if(parts[3].equalsIgnoreCase("walkable"))walkable=true;
                else if(parts[3].equalsIgnoreCase("blocked"))walkable=false;
                else throw new IllegalArgumentException("Unknown state: "+parts[3]);
                long count=(long)(xs[1]-xs[0]+1)*(ys[1]-ys[0]+1)*(zs[1]-zs[0]+1);
                if(count>262144)throw new IllegalArgumentException("Range exceeds 262144 tiles");
                for(int x=xs[0];x<=xs[1];x++)for(int y=ys[0];y<=ys[1];y++)for(int z=zs[0];z<=zs[1];z++)
                    tiles.put(new Tile(x,y,z),walkable); // Last line wins, including single-tile exceptions.
            } catch(IllegalArgumentException ex) {
                throw new IllegalArgumentException("Walkable config line "+number+": "+ex.getMessage(),ex);
            }
        }
        return tiles;
    }
    private static int[] range(String value,int limit) {
        String[] bounds=value.split("-",-1);
        if(bounds.length<1 || bounds.length>2)throw new IllegalArgumentException("Invalid range: "+value);
        int a=Integer.parseInt(bounds[0]),b=bounds.length==2?Integer.parseInt(bounds[1]):a;
        if(a<0 || b<0 || a>limit || b>limit)throw new IllegalArgumentException("Out of bounds: "+value);
        return new int[]{Math.min(a,b),Math.max(a,b)};
    }
    public static void reload() throws IOException {
        if(!Files.exists(config())) {System.out.println("[WALKABLE] No override file: "+config());return;}
        Map<Tile,Boolean> next=parse(Files.readAllLines(config()));
        apply(next);
    }
    private static void apply(Map<Tile,Boolean> next) {
        int skipped=applyCollision(next,RegionProvider.getGlobal());
        // Remove an earlier invisible floor blocker when an Owner switches that tile back.
        for(Player p:PlayerHandler.players)if(p!=null && p.getOutStream()!=null) {
            for(Map.Entry<Tile,Boolean> e:next.entrySet()) {
                Tile t=e.getKey();
                if(e.getValue() && Boolean.FALSE.equals(active.get(t)) &&
                    visible(t,p.heightLevel,p.getMapRegionX()*8,p.getMapRegionY()*8))
                    p.getPA().object(-1,t.x,t.y,0,22,false);
            }
        }
        active=Collections.unmodifiableMap(new LinkedHashMap<>(next));
        for(Player p:PlayerHandler.players)if(p!=null)sync(p);
        System.out.println("[WALKABLE] Loaded "+(next.size()-skipped)+" unique tile overrides. Skipped "+skipped+" unloaded tiles.");
    }
    static int applyCollision(Map<Tile,Boolean> next,RegionProvider provider) {
        int skipped=0;
        for(Map.Entry<Tile,Boolean> e:next.entrySet()) {
            Tile t=e.getKey();Region region=provider.get(t.x,t.y);
            if(region==null) {skipped++;continue;}
            if(e.getValue()) {
                region.setClipToZero(t.x,t.y,t.z);
                for(int type=0;type<=22;type++)for(int face=0;face<4;face++)
                    region.removeObject(0,t.x,t.y,t.z,type,face);
            }
        }
        // Object clipping touches neighbouring tiles: enforce final states after ALL removals.
        for(Map.Entry<Tile,Boolean> e:next.entrySet()) {
            Tile t=e.getKey();Region region=provider.get(t.x,t.y);
            if(region==null)continue;
            if(e.getValue())region.setClipToZero(t.x,t.y,t.z);
            else region.addClip(t.x,t.y,t.z,0x200000);
        }
        return skipped;
    }
    public static boolean visible(Tile t,int plane,int baseX,int baseY) {
        // Instanced raids use heights 4, 8, ... for the same base map plane.
        return t.z==Math.floorMod(plane,4) && t.x>=baseX && t.x<baseX+104 && t.y>=baseY && t.y<baseY+104;
    }
    public static void sync(Player p) {
        if(p.getOutStream()==null)return;
        for(Map.Entry<Tile,Boolean> e:active.entrySet()) {
            Tile t=e.getKey();
            if(!visible(t,p.heightLevel,p.getMapRegionX()*8,p.getMapRegionY()*8))continue;
            // Match the existing individual command; restrict packets to the player's plane/map.
            p.getPA().object(e.getValue()?-1:14806,t.x,t.y,0,e.getValue()?10:22,false);
        }
        p.flushOutStream();
    }
    public static List<String> withOverride(List<String> old,Tile tile,boolean walkable) {
        List<String> updated=new ArrayList<>(old);
        // Append so a single tile wins over any earlier rectangle, without deleting that rectangle.
        updated.add(tile.x+"\t"+tile.y+"\t"+tile.z+"\t"+(walkable?"walkable":"blocked"));
        return updated;
    }
    public static void saveAndApply(int x,int y,int z,boolean walkable) throws IOException {
        Path path=config();List<String> old=Files.exists(path)?Files.readAllLines(path):new ArrayList<>();
        List<String> updated=withOverride(old,new Tile(x,y,z),walkable);
        Map<Tile,Boolean> next=parse(updated);
        Files.createDirectories(path.getParent());Files.write(path,updated);
        apply(next);
    }
}
