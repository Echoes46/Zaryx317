package io.zaryx.content.worldevent.impl;

import io.zaryx.content.WeaponGames.WGManager;
import io.zaryx.content.commands.Command;
import io.zaryx.content.commands.all.WGames;
import io.zaryx.content.worldevent.WorldEvent;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.entity.player.broadcasts.Broadcast;

import java.util.List;

public class WGWorldEvent implements WorldEvent {

    private final WGManager wgManager = WGManager.getSingleton();

    @Override
    public void init() {
        wgManager.openLobby();
    }

    @Override
    public void dispose() {
        wgManager.endGame();
    }

    @Override
    public boolean isEventCompleted() {
        return !wgManager.isLobbyOpen() && !wgManager.isArenaActive();
    }

    @Override
    public String getCurrentStatus() {
        return wgManager.getTimeLeft();
    }

    @Override
    public String getEventName() {
        return "WeaponGames";
    }

    @Override
    public String getStartDescription() {
        return "starts";
    }

    @Override
    public Class<? extends Command> getTeleportCommand() {
        return WGames.class;
    }

    @Override
    public void announce(List<Player> players) {
        new Broadcast("<img=20><col=ff7000><shad=ffff00>[WG]:</shad><img=20> <col=A0002C> starting soon speak to lisa outside outlast, ;;WGames").addTeleport(new Position(3094, 3501, 0)).copyMessageToChatbox().submit();
    }
}
