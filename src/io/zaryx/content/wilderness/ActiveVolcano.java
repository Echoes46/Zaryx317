package io.zaryx.content.wilderness;

import io.zaryx.Server;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.entity.player.broadcasts.Broadcast;
import io.zaryx.model.world.objects.GlobalObject;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class ActiveVolcano {

    private static boolean DISABLED = false;

    private static final int BOULDER = 31037;
    public static int BOULDER_STABILITY = 500;

    private static ActiveVolcano ACTIVE;
    private static long timeRemaining = 0;
    private static GlobalObject boulder;

    private static final ActiveVolcano[] SPAWNS = {
            new ActiveVolcano(new Position(3366, 3936, 0)),
            new ActiveVolcano(new Position(3353, 3934, 0)),
            new ActiveVolcano(new Position(3374, 3937, 0)),
            new ActiveVolcano(new Position(3361, 3924, 0)),
    };

    public static boolean progress = false;
    private final Position boulderSpawn;

    public ActiveVolcano(Position boulderSpawn) {
        this.boulderSpawn = boulderSpawn;
    }

    public static long delay = 0;

    public static void Tick() {
        if (DISABLED) {
            return;
        }

        if (delay == 0) {
            delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10);
        }

        if (timeRemaining > 0 && timeRemaining < System.currentTimeMillis() && progress) {
            removeBoulder(false);
            progress = false;
            delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
        }

        if (progress) {
            return;
        }

        if (delay > System.currentTimeMillis()) {
            return;
        }

        ActiveVolcano next = Misc.get(SPAWNS);

        if (next == ACTIVE) {
            return;
        }

        ACTIVE = next;
        progress = true;

        new Broadcast("<img=95> [WILDY] There's been a disturbance reported at the Volcano! Get there now! ::volcano").submit();
        Discord.writeIngameEvents("There's been a disturbance reported at the Volcano! Get there now! ::volcano");

        addBoulder();

        timeRemaining = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(20);
    }

    private static void addBoulder() {
        if (ACTIVE == null) {
            return;
        }

        GlobalObject go = new GlobalObject(BOULDER, ACTIVE.boulderSpawn.getX(), ACTIVE.boulderSpawn.getY(), 0, 0, 10);
        Server.getGlobalObjects().add(go);

        boulder = go;
        BOULDER_STABILITY = 500;
    }

    public static void removeBoulder(boolean success) {
        if (boulder == null || ACTIVE == null) {
            return;
        }

        Server.getGlobalObjects().remove(boulder);
        Server.getGlobalObjects().add(new GlobalObject(-1, ACTIVE.boulderSpawn.getX(), ACTIVE.boulderSpawn.getY(), 0, 0, 10));

        boulder.setId(-1);
        boulder = null;
        progress = false;
        ACTIVE = null;
        delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);

        if (success) {
            new Broadcast("<img=95> [WILDY] @gre@The volcano has been subdued! Well done everyone!").submit();
            Discord.writeIngameEvents("The Volcano has been subdued! Well done everyone!");
        } else {
            new Broadcast("<img=95> [WILDY] @red@The Volcano has erupted! Help subdue it next time for blood money!").submit();
            Discord.writeIngameEvents("The Volcano has erupted! Help subdue it next time for blood money!");
        }
    }

    public static void removeShards(int amt) {
        BOULDER_STABILITY -= amt;

        if (BOULDER_STABILITY <= 0) {
            BOULDER_STABILITY = 0;
        }
    }

}