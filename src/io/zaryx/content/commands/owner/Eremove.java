package io.zaryx.content.commands.owner;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Permanently removes an object from memory AND from global_objects.cfg.
 * Usage: ::eremove objectId x y z
 */
public class Eremove extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        String[] args = input.split(" ");
        if (args.length < 4) {
            c.sendMessage("Usage: ::eremove objectId x y z");
            return;
        }
        try {
            int objId = Integer.parseInt(args[0]);
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            int height = Integer.parseInt(args[3]);

            // Remove from memory
            Server.getGlobalObjects().remove(objId, x, y, height);

            // Send visual removal to all players
            Arrays.stream(PlayerHandler.players).forEach(p -> {
                if (p != null) {
                    p.getPA().object(-1, x, y, 0, 10, true);
                }
            });

            // Remove from global_objects.cfg
            String path = Server.getDataDirectory() + "/cfg/obj/global_objects.cfg";
            File file = new File(path);
            boolean removed = false;

            if (file.exists()) {
                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty() || line.trim().startsWith("//")) {
                            lines.add(line);
                            continue;
                        }
                        String[] parts = line.trim().split("\t");
                        if (parts.length >= 4) {
                            try {
                                int fileObjId = Integer.parseInt(parts[0]);
                                int fileX = Integer.parseInt(parts[1]);
                                int fileY = Integer.parseInt(parts[2]);
                                int fileH = Integer.parseInt(parts[3]);
                                if (fileObjId == objId && fileX == x && fileY == y && fileH == height) {
                                    removed = true;
                                    continue; // skip this line (remove it)
                                }
                            } catch (NumberFormatException ignored) {}
                        }
                        lines.add(line);
                    }
                }

                if (removed) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        for (int i = 0; i < lines.size(); i++) {
                            writer.write(lines.get(i));
                            if (i < lines.size() - 1) writer.newLine();
                        }
                    }
                }
            }

            if (removed) {
                c.sendMessage("Object " + objId + " permanently removed from (" + x + ", " + y + ", " + height + ")");
            } else {
                c.sendMessage("Object " + objId + " removed visually at (" + x + ", " + y + ", " + height + ") (not in cfg)");
            }
            System.out.println("[EREMOVE] " + c.getLoginName() + " removed object " + objId + " at " + x + "," + y + "," + height + " permanent=" + removed);

        } catch (NumberFormatException e) {
            c.sendMessage("Invalid number format. Usage: ::eremove objectId x y z");
        } catch (IOException e) {
            c.sendMessage("Error editing cfg file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
