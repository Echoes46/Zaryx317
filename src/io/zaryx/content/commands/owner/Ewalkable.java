package io.zaryx.content.commands.owner;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.collisionmap.Region;
import io.zaryx.model.collisionmap.RegionProvider;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * Toggles a tile's walkability and saves to walkable_tiles.cfg.
 * Usage: ::ewalkable [x y z]
 * Without args: toggles at player position.
 * With args: toggles at specific coords.
 */
public class Ewalkable extends Command {

    private static final String CFG_FILE = "/cfg/obj/walkable_tiles.cfg";

    @Override
    public void execute(Player c, String commandName, String input) {
        int x, y, height;

        if (input != null && !input.isEmpty()) {
            String[] args = input.split(" ");
            if (args.length < 3) {
                c.sendMessage("Usage: ::ewalkable [x y z]");
                return;
            }
            try {
                x = Integer.parseInt(args[0]);
                y = Integer.parseInt(args[1]);
                height = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                c.sendMessage("Invalid coordinates.");
                return;
            }
        } else {
            x = c.absX;
            y = c.absY;
            height = c.heightLevel;
        }

        int currentClip = RegionProvider.getGlobal().getClipping(x, y, height);
        Region region = RegionProvider.getGlobal().get(x, y);

        if (region == null) {
            c.sendMessage("Region not loaded for tile (" + x + ", " + y + ")");
            return;
        }

        boolean wasBlocked = currentClip != 0;

        if (wasBlocked) {
            // Make walkable: set clip to 0
            region.setClipToZero(x, y, height);
            // Send client update: place then remove invisible object to force collision recalc
            final int fx = x, fy = y;
            Arrays.stream(PlayerHandler.players).forEach(p -> {
                if (p != null) {
                    p.getPA().object(-1, fx, fy, 0, 10, true);
                }
            });
            c.sendMessage("@gre@Tile (" + x + ", " + y + ", " + height + ") is now WALKABLE");
        } else {
            // Block it: add full block flag
            region.addClip(x, y, height, 0x200000); // full tile block
            // Send invisible blocking object to client
            final int fx = x, fy = y;
            Arrays.stream(PlayerHandler.players).forEach(p -> {
                if (p != null) {
                    p.getPA().object(14806, fx, fy, 0, 22, true); // invisible block object
                }
            });
            c.sendMessage("@red@Tile (" + x + ", " + y + ", " + height + ") is now BLOCKED");
        }

        // Save/update in cfg file
        try {
            saveTileCfg(x, y, height, !wasBlocked);
        } catch (IOException e) {
            c.sendMessage("Error saving: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("[EWALKABLE] " + c.getLoginName() + " toggled tile " + x + "," + y + "," + height + " walkable=" + wasBlocked);
    }

    private void saveTileCfg(int x, int y, int height, boolean blocked) throws IOException {
        String path = Server.getDataDirectory() + CFG_FILE;
        File file = new File(path);
        file.getParentFile().mkdirs();

        List<String> lines = new ArrayList<>();
        boolean found = false;

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && !line.trim().startsWith("//")) {
                        String[] parts = line.trim().split("\t");
                        if (parts.length >= 4) {
                            try {
                                int fx = Integer.parseInt(parts[0]);
                                int fy = Integer.parseInt(parts[1]);
                                int fh = Integer.parseInt(parts[2]);
                                if (fx == x && fy == y && fh == height) {
                                    found = true;
                                    if (blocked) {
                                        // Update to blocked
                                        lines.add(x + "\t" + y + "\t" + height + "\tblocked");
                                    }
                                    // If walkable, skip line (remove from cfg = default state)
                                    continue;
                                }
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                    lines.add(line);
                }
            }
        }

        if (!found && blocked) {
            lines.add(x + "\t" + y + "\t" + height + "\tblocked");
        } else if (!found && !blocked) {
            lines.add(x + "\t" + y + "\t" + height + "\twalkable");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (lines.isEmpty() || !lines.get(0).startsWith("//")) {
                writer.write("//x\ty\tz\tstate");
                writer.newLine();
            }
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                if (i < lines.size() - 1) writer.newLine();
            }
        }
    }
}
