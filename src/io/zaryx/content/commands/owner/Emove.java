package io.zaryx.content.commands.owner;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.world.objects.GlobalObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Moves an object from one position to another and updates global_objects.cfg.
 * Usage: ::emove objectId oldX oldY oldZ newX newY newZ face type
 */
public class Emove extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        String[] args = input.split(" ");
        if (args.length < 9) {
            c.sendMessage("Usage: ::emove objId oldX oldY oldZ newX newY newZ face type");
            return;
        }
        try {
            int objId = Integer.parseInt(args[0]);
            int oldX = Integer.parseInt(args[1]);
            int oldY = Integer.parseInt(args[2]);
            int oldZ = Integer.parseInt(args[3]);
            int newX = Integer.parseInt(args[4]);
            int newY = Integer.parseInt(args[5]);
            int newZ = Integer.parseInt(args[6]);
            int face = Integer.parseInt(args[7]);
            int type = Integer.parseInt(args[8]);

            // Remove from old position
            Server.getGlobalObjects().remove(objId, oldX, oldY, oldZ);
            Arrays.stream(PlayerHandler.players).forEach(p -> {
                if (p != null) p.getPA().object(-1, oldX, oldY, 0, 10, true);
            });

            // Place at new position
            GlobalObject obj = new GlobalObject(objId, newX, newY, newZ, face, type, -1);
            Server.getGlobalObjects().add(obj);

            // Update cfg file: remove old line, add new line
            String path = Server.getDataDirectory() + "/cfg/obj/global_objects.cfg";
            File file = new File(path);
            List<String> lines = new ArrayList<>();
            boolean removed = false;

            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty() && !line.trim().startsWith("//")) {
                            String[] parts = line.trim().split("\t");
                            if (parts.length >= 4) {
                                try {
                                    if (Integer.parseInt(parts[0]) == objId && Integer.parseInt(parts[1]) == oldX
                                        && Integer.parseInt(parts[2]) == oldY && Integer.parseInt(parts[3]) == oldZ) {
                                        removed = true;
                                        continue;
                                    }
                                } catch (NumberFormatException ignored) {}
                            }
                        }
                        lines.add(line);
                    }
                }
            }

            // Add new position
            lines.add(objId + "\t" + newX + "\t" + newY + "\t" + newZ + "\t" + face + "\t" + type);

            // Write back
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (int i = 0; i < lines.size(); i++) {
                    writer.write(lines.get(i));
                    if (i < lines.size() - 1) writer.newLine();
                }
            }

            c.sendMessage("@gre@Moved object " + objId + " from (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
            System.out.println("[EMOVE] " + c.getLoginName() + " moved " + objId + " from " + oldX + "," + oldY + " to " + newX + "," + newY);

        } catch (NumberFormatException e) {
            c.sendMessage("Invalid number. Usage: ::emove objId oldX oldY oldZ newX newY newZ face type");
        } catch (IOException e) {
            c.sendMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
