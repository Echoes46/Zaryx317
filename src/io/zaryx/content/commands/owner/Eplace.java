package io.zaryx.content.commands.owner;

import io.zaryx.Server;
import io.zaryx.content.commands.Command;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.world.objects.GlobalObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Places an object at specific coordinates and saves permanently to global_objects.cfg.
 * Usage: ::eplace objectId x y z face type
 */
public class Eplace extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        String[] args = input.split(" ");
        if (args.length < 6) {
            c.sendMessage("Usage: ::eplace objectId x y z face type");
            return;
        }
        try {
            int objId = Integer.parseInt(args[0]);
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            int height = Integer.parseInt(args[3]);
            int face = Integer.parseInt(args[4]);
            int type = Integer.parseInt(args[5]);

            // Spawn the object in the live world
            GlobalObject obj = new GlobalObject(objId, x, y, height, face, type, -1);
            Server.getGlobalObjects().add(obj);

            // Append to global_objects.cfg
            String line = objId + "\t" + x + "\t" + y + "\t" + height + "\t" + face + "\t" + type;
            String path = Server.getDataDirectory() + "/cfg/obj/global_objects.cfg";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
                writer.newLine();
                writer.write(line);
            }

            c.sendMessage("Object " + objId + " placed + saved at (" + x + ", " + y + ", " + height + ") face=" + face + " type=" + type);
            System.out.println("[EPLACE] " + c.getLoginName() + " placed object " + objId + " at " + x + "," + y + "," + height);

        } catch (NumberFormatException e) {
            c.sendMessage("Invalid number format. Usage: ::eplace objectId x y z face type");
        } catch (IOException e) {
            c.sendMessage("Error writing to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
