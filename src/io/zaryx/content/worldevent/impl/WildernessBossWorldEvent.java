package io.zaryx.content.worldevent.impl;

import java.util.List;

import io.zaryx.content.commands.Command;
import io.zaryx.content.commands.all.Wildyevent;
import io.zaryx.content.events.monsterhunt.MonsterHunt;
import io.zaryx.content.worldevent.WorldEvent;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.entity.player.broadcasts.Broadcast;

public class WildernessBossWorldEvent implements WorldEvent {

    @Override
    public void init() {
        MonsterHunt.spawnNPC();
    }

    @Override
    public void dispose() {
        if (!isEventCompleted()) {
            MonsterHunt.despawn();
        }
    }

    @Override
    public boolean isEventCompleted() {
        return !MonsterHunt.spawned;
    }

    @Override
    public String getCurrentStatus() {
        return MonsterHunt.getTimeLeft();
    }

    @Override
    public String getEventName() {
        return "Wildy Boss";
    }

    @Override
    public String getStartDescription() {
        return "spawns";
    }

    @Override
    public Class<? extends Command> getTeleportCommand() {
        return Wildyevent.class;
    }

    @Override
    public void announce(List<Player> players) {
        new Broadcast("<img=58> " + MonsterHunt.getName() + " has spawned at "
                + MonsterHunt.getCurrentLocation().getLocationName() + ", use ::wildyevent to teleport!").addTeleport(new Position(MonsterHunt.getCurrentLocation().getX(), MonsterHunt.getCurrentLocation().getY(), 0)).copyMessageToChatbox().submit();
    }
}
