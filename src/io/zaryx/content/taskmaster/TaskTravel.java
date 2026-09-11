package io.zaryx.content.taskmaster;
import io.zaryx.content.teleportv2.inter.TeleportInterface;
import io.zaryx.content.teleportv2.inter.TeleportInterface.Teleport;
import io.zaryx.model.entity.player.Player;
import java.util.*;

/** Uses existing destination entries and their normal teleport button handler. */
public final class TaskTravel {
    private TaskTravel() { }
    private static String key(String value) { return TaskMaster.clean(value).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", ""); }
    static int[] destination(TaskMasterKills task) {
        String target=key(task.getDesc());
        Map<String,String> aliases=new HashMap<>();
        aliases.put("chambers","chambersofxeric"); aliases.put("inferno","theinferno");
        aliases.put("tztokjad","fightcaves"); aliases.put("dagannoth","dagannothkings");
        aliases.put("nightmare","thenightmare"); aliases.put("rockcrab","rockcrabs"); aliases.put("sandcrab","sandcrabs");
        aliases.put("hillgiant","hillgiantsmulti"); aliases.put("crazyarchaeologist","crazyarch");
        for(String boss:new String[]{"general graardor","kree'arra","k'ril tsutsaroth","commander zilyana"}) aliases.put(key(boss),"godwarsdungeon");
        target=aliases.getOrDefault(target,target);
        Teleport[][] groups={TeleportInterface.MONSTERS.values(),TeleportInterface.BOSSES.values(),TeleportInterface.MINIGAMES.values(),
                TeleportInterface.DUNGEONS.values(),TeleportInterface.SKILLING.values(),TeleportInterface.PK.values()};
        for(int group=0;group<groups.length;group++) for(int i=0;i<groups[group].length;i++)
            if(key(groups[group][i].getName()).equals(target)) return new int[]{group,i};
        return null;
    }
    public static List<String> directions(TaskMasterKills t) {
        int[] route=destination(t);
        if(route!=null) {
            Teleport[][] groups={TeleportInterface.MONSTERS.values(),TeleportInterface.BOSSES.values(),TeleportInterface.MINIGAMES.values(),
                    TeleportInterface.DUNGEONS.values(),TeleportInterface.SKILLING.values(),TeleportInterface.PK.values()};
            return Arrays.asList("Location: "+TaskMaster.clean(groups[route[0]][route[1]].getName())+".",
                    "Travel opens this destination. Use its normal teleport button.", "Entry requirements and Wilderness restrictions still apply.");
        }
        if(t.getTaskType()==TaskType.SKILLING) return Arrays.asList("Complete the named skilling action with the required supplies.", "Travel opens the locations menu so you can choose your area.");
        return Arrays.asList("Use the existing boss, dungeon or minigame entry for this activity.", "Travel opens the teleport menu; select your activity's entrance.");
    }
    public static void open(Player p,TaskMasterKills t) {
        int[] route=destination(t);
        int group=route==null?(t.getTaskType()==TaskType.SKILLING?4:1):route[0];
        TeleportInterface.setUp(p,group);
        TeleportInterface.open(p);
        if(route!=null && route[1]<TeleportInterface.TELEPORT_BUTTONS.length)
            TeleportInterface.handleButton(p,TeleportInterface.TELEPORT_BUTTONS[route[1]]);
    }
}
