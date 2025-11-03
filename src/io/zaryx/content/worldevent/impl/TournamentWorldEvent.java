package io.zaryx.content.worldevent.impl;

import io.zaryx.content.commands.Command;
import io.zaryx.content.commands.all.Outlast;
import io.zaryx.content.tournaments.TourneyManager;
import io.zaryx.content.worldevent.WorldEvent;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.entity.player.broadcasts.Broadcast;
import io.zaryx.util.discord.Discord;

import java.util.List;

public class TournamentWorldEvent implements WorldEvent {

    private final TourneyManager tourney = TourneyManager.getSingleton();

    @Override
    public void init() {
        tourney.openLobby();
    }

    @Override
    public void dispose() {
        tourney.endGame();
    }

    @Override
    public boolean isEventCompleted() {
        return !tourney.isLobbyOpen() && !tourney.isArenaActive();
    }

    @Override
    public String getCurrentStatus() {
        return tourney.getTimeLeft();
    }

    @Override
    public String getEventName() {
        return "Outlast";
    }

    @Override
    public String getStartDescription() {
        return "starts";
    }

    @Override
    public Class<? extends Command> getTeleportCommand() {
        return Outlast.class;
    }

    @Override
    public void announce(List<Player> players) {
        String name = tourney.getTournamentType();
        if (tourney.getTournamentType().equalsIgnoreCase("DYOG")) {
            name = "Dig Your Own Grave";
        }
        Discord.writeWorldEvent("[TOURNAMENT]",  name + " style will begin soon, type ::outlast!``` <@&1248350477154783321>");
        new Broadcast("<img=20> [SURVIVOR] " + name + " style will begin soon, type ::outlast!").addTeleport(new Position(2064, 6004, 0)).copyMessageToChatbox().submit();
    }
}
