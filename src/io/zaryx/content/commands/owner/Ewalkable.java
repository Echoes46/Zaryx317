package io.zaryx.content.commands.owner;
import io.zaryx.content.commands.Command;
import io.zaryx.model.collisionmap.RegionProvider;
import io.zaryx.model.collisionmap.WalkableTiles;
import io.zaryx.model.entity.player.Player;
/** Toggles one tile and persists an explicit exception after any configured ranges. */
public class Ewalkable extends Command {
    @Override public void execute(Player c,String commandName,String input) {
        int x=c.absX,y=c.absY,z=c.heightLevel;
        try {
            if(input!=null && !input.trim().isEmpty()) {
                String[] args=input.trim().split("\\s+");
                if(args.length!=3) {c.sendMessage("Usage: ::ewalkable [x y z]");return;}
                x=Integer.parseInt(args[0]);y=Integer.parseInt(args[1]);z=Integer.parseInt(args[2]);
            }
            if(x<0 || y<0 || x>16383 || y>16383 || z<0 || z>3) {c.sendMessage("Coordinates must use plane 0-3 and valid world tiles.");return;}
            if(RegionProvider.getGlobal().get(x,y)==null) {c.sendMessage("Region not loaded for that tile.");return;}
            boolean walkable=RegionProvider.getGlobal().getClipping(x,y,z)!=0;
            WalkableTiles.saveAndApply(x,y,z,walkable);
            c.sendMessage("Tile ("+x+", "+y+", "+z+") is now "+(walkable?"WALKABLE":"BLOCKED")+" and saved.");
        } catch(java.io.IOException | IllegalArgumentException ex) {
            c.sendMessage("Unable to update walkability: "+ex.getMessage());
        }
    }
}
