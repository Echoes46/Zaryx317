package io.zaryx.content.events.monsterhunt;

import io.zaryx.Server;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.entity.player.broadcasts.Broadcast;
import io.zaryx.model.world.objects.GlobalObject;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;


import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CrystalTree {

    private static boolean DISABLED = false;

    private static final int STAR_START = 34918;
    private static final int STAR_PROGRESS_1 = 34917;
    private static final int STAR_PROGRESS_2 = 34916;
    private static final int STAR_PROGRESS_3 = 34915;
    private static final int STAR_PROGRESS_4 = 34914;
    private static final int STAR_PROGRESS_5 = 34913;
    private static final int STAR_FINISH = 34912;

    public static int CHOPS_REMAINING = 10000;
    public static CrystalTree ACTIVE;

    private static GlobalObject rock;

    private static final CrystalTree[] SPAWNS = {
            new CrystalTree(new Position(2994, 3839, 0)), //KBD
            new CrystalTree(new Position(3003, 3645, 0)), //DARK CASTLE
            new CrystalTree(new Position(3321, 3666, 0)), //GREEN DRAGONS
            new CrystalTree(new Position(3244, 3639, 0)), //CHAOS ELE
            new CrystalTree(new Position(3074, 3940, 0)), //MAGE ARENA
            new CrystalTree(new Position(3135, 3838, 0)), //Rev Caves
    };

    public static String getLocation() {
        if(ACTIVE.treeSpawn.equals(new Position(2994, 3839))) {
            return "King Black Dragon";
        }
        if(ACTIVE.treeSpawn.equals(new Position(3003, 3645))) {
            return "Dark Castle";
        }
        if(ACTIVE.treeSpawn.equals(new Position(3321, 3666))) {
            return "Green Dragons";
        }
        if(ACTIVE.treeSpawn.equals(new Position(3244, 3639))) {
            return "Chaos Druids";
        }
        if(ACTIVE.treeSpawn.equals(new Position(3045, 3470))) {
            return "Edgeville Mon.";
        }
        if(ACTIVE.treeSpawn.equals(new Position(3074, 3940))) {
            return "Mage Arena";
        }
        if(ACTIVE.treeSpawn.equals(new Position(3135, 3838))) {
            return "Revenant Caves";
        }
        return "Unknown";
    };

    /**
     * Separator
     */

    public final Position treeSpawn;
    public static long delay = 0;
    public static boolean progress = false;
    private static long timeRemaining = 0;
    public CrystalTree(Position treeSpawn) {
        this.treeSpawn = treeSpawn;
    }

    public static void Tick() {
        if (delay == 0) {
            delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
        }
        if (timeRemaining > 0 && timeRemaining < System.currentTimeMillis() && progress) {
            removeWildCrystalTree(false);
            progress = false;
            delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
        }
        if (progress) {
            return;
        }
        if (delay > System.currentTimeMillis()) {
            return;
        }

        CrystalTree next = Misc.get(SPAWNS);
        if (next == ACTIVE) {
            return;
        }
        ACTIVE = next;
        progress = true;
        if (DiscordBot.INSTANCE != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("[ WORLD EVENT: CRYSTAL TREE ]");
            embed.setImage("https://oldschool.runescape.wiki/images/thumb/Crystal_tree_%28choppable%29.png/125px-Crystal_tree_%28choppable%29.png?0c819");
            embed.setColor(Color.GREEN);
            embed.setTimestamp(java.time.Instant.now());
            embed.addField("There's been a sighting of a Crystal Tree around! Location: " + getLocation(), "'::tree'", false);
            DiscordBot.INSTANCE.sendWorldEvent(embed.build());
            //DiscordBot.INSTANCE.sendMessage(DiscordChannelType.WORLD_EVENTS, "@everyone");
        }
        new Broadcast("<img=43> There's been a sighting of a crystal tree around "+getLocation()+"! ::tree").submit();
        addWildCrystalTree();
        timeRemaining = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);

    }

    private static void addWildCrystalTree() {
        GlobalObject go = new GlobalObject(STAR_START, ACTIVE.treeSpawn.getX(), ACTIVE.treeSpawn.getY(), 0, 0, 10);
        Server.getGlobalObjects().add(go);
        rock = go;
        CHOPS_REMAINING = 10000;
        new Broadcast("<img=43> There's been a sighting of a crystal tree around "+getLocation()+"! ::tree").submit();
    }

    public static void removeWildCrystalTree(boolean success) {
        if (rock != null) {
            Server.getGlobalObjects().remove(rock);
            Server.getGlobalObjects().add(new GlobalObject(-1, ACTIVE.treeSpawn.getX(), ACTIVE.treeSpawn.getY(), 0, 0, 10));
            rock.setId(-1);
            rock = null;
            progress = false;
            delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
            if (success) {
                new Broadcast("<img=43> @gre@The Crystal tree has been chopped down!").submit();
            } else {
                new Broadcast("<img=43> @cya@The elves decided to chop down the Crystal Tree as players didn't!").submit();
            }
        }
    }

    public static void removeShards(int amt) {
        CHOPS_REMAINING -= amt;
        if (CHOPS_REMAINING <= 0)
            CHOPS_REMAINING = 0;
    }

    public static void inspect(Player player) {
        player.sendMessage("The tree looks like it has "+CHOPS_REMAINING+" x fragments in it.");
    }

    public static void rockCheck() {
        if(CHOPS_REMAINING > 9000 && CHOPS_REMAINING <= 9100 && rock.getObjectId() != STAR_PROGRESS_1) {
            rock.setId(STAR_PROGRESS_1);
            Server.getGlobalObjects().add(rock);
        }
        if(CHOPS_REMAINING >= 7000 && CHOPS_REMAINING <= 7100 && rock.getObjectId() != STAR_PROGRESS_2) {
            rock.setId(STAR_PROGRESS_2);
            Server.getGlobalObjects().add(rock);
        }
        if(CHOPS_REMAINING >= 5000 && CHOPS_REMAINING <= 5100 && rock.getObjectId() != STAR_PROGRESS_3) {
            rock.setId(STAR_PROGRESS_3);
            Server.getGlobalObjects().add(rock);
        }
        if(CHOPS_REMAINING >= 3000 && CHOPS_REMAINING <= 3100 && rock.getObjectId() != STAR_PROGRESS_4) {
            rock.setId(STAR_PROGRESS_4);
            Server.getGlobalObjects().add(rock);
        }
        if(CHOPS_REMAINING >= 1000 && CHOPS_REMAINING <= 1100 && rock.getObjectId() != STAR_PROGRESS_5) {
            rock.setId(STAR_PROGRESS_5);
            Server.getGlobalObjects().add(rock);
        }
        if(CHOPS_REMAINING >= 1 && CHOPS_REMAINING <= 300 && rock.getObjectId() != STAR_FINISH) {
            rock.setId(STAR_FINISH);
            Server.getGlobalObjects().add(rock);
        }
    }
}
