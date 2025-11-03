//package io.zaryx.content.worldevent.impl;
//
//import io.zaryx.content.commands.Command;
//import io.zaryx.content.minigames.newMinigames.squidGames.SquidGames;
//import io.zaryx.content.worldevent.WorldEvent;
//import io.zaryx.model.entity.player.Player;
//import io.zaryx.model.entity.player.Position;
//import io.zaryx.model.entity.player.broadcasts.Broadcast;
//
//import java.util.List;
//
//public class SquidGameWorldEvent implements WorldEvent {
//
//    private final SquidGames squid = SquidGames.getSingleton();
//
//    @Override
//    public void init() {
//      squid.openLobby();
//    }
//
//    @Override
//    public void dispose() {
//    squid.end();
//    }
//
//    @Override
//    public boolean isEventCompleted() {
//        return !squid.isLobbyOpen();
//    }
//
//    @Override
//    public String getCurrentStatus() {
//        return squid.getTimeLeft();
//    }
//
//    @Override
//    public String getEventName() {
//        return "Squid Games";
//    }
//
//    @Override
//    public String getStartDescription() {
//        return "starts";
//    }
//
//    @Override
//    public Class<? extends Command> getTeleportCommand() {
//        return null;
//    }
//
//    @Override
//    public void announce(List<Player> players) {
//        //new Broadcast("[<img=20>Squid Games will begin soon!").addTeleport(new Position(3109, 3480, 0)).copyMessageToChatbox().submit();
//
//    }
//}
