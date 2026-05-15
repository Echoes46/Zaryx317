package io.zaryx.content.commands.admin;

import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;

public class Coords extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        int absX = c.absX;
        int absY = c.absY;
        int height = c.heightLevel;

        int regionX = absX >> 6;
        int regionY = absY >> 6;
        int regionId = (regionX << 8) + regionY;

        int localX = absX & 63;
        int localY = absY & 63;

        c.sendMessage("<col=ff0000>Coords: X=" + absX + " Y=" + absY + " Z=" + height);
        c.sendMessage("<col=00ff00>Region: " + regionId + " (RegionX=" + regionX + ", RegionY=" + regionY + ")");
        c.sendMessage("<col=0000ff>Local: X=" + localX + " Y=" + localY);
    }
}