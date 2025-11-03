package io.zaryx.content.worldevent.impl;

import java.util.List;

import io.zaryx.content.bosses.hespori.Hespori;
import io.zaryx.content.bosses.hespori.HesporiSpawner;
import io.zaryx.content.commands.Command;
import io.zaryx.content.commands.all.Worldevent;
import io.zaryx.content.worldevent.WorldEvent;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.entity.player.broadcasts.Broadcast;
import io.zaryx.util.discord.Discord;

public class HesporiWorldEvent implements WorldEvent {
    @Override
    public void init() {
        HesporiSpawner.spawnNPC();
    }

    @Override
    public void dispose() {
        if (!isEventCompleted()) {
            Hespori.rewardPlayers(false);
        }
    }

    @Override
    public boolean isEventCompleted() {
        return !HesporiSpawner.isSpawned();
    }

    @Override
    public String getCurrentStatus() {
        return "World Event: @gre@Hespori";
    }

    @Override
    public String getEventName() {
        return "Hespori";
    }

    @Override
    public String getStartDescription() {
        return "spawns";
    }

    @Override
    public Class<? extends Command> getTeleportCommand() {
        return Worldevent.class;
    }

    @Override
    public void announce(List<Player> players) {
        Discord.writeIngameEvents("```Hespori world boss has spawned, use ::worldevent to fight!``` <@&1248350477154783321>");
        new Broadcast("<img=11> Hespori world boss has spawned, use ::worldevent to fight!").addTeleport(new Position(2457, 3553, 0)).copyMessageToChatbox().submit();
    }
}
