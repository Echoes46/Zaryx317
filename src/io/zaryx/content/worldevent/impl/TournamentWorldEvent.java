package io.zaryx.content.worldevent.impl;

import io.zaryx.content.commands.Command;
import io.zaryx.content.commands.all.Outlast;
import io.zaryx.content.tournaments.TourneyManager;
import io.zaryx.content.worldevent.WorldEvent;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.entity.player.broadcasts.Broadcast;
import io.zaryx.util.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
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
        if (DiscordBot.INSTANCE != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("[ OUTLAST TOURNAMENT ]");
            embed.setImage("https://oldschool.runescape.wiki/images/thumb/Ferox_Enclave.png/300px-Ferox_Enclave.png?c2035");
            embed.setColor(Color.GREEN);
            embed.setTimestamp(java.time.Instant.now());
            embed.addField(name + " style will begin soon, type ::outlast!", "\u200B", false);
            DiscordBot.INSTANCE.sendWorldEvent(embed.build());
            //DiscordBot.INSTANCE.sendMessage(DiscordChannelType.WORLD_EVENTS, "@everyone");
        }
        new Broadcast("<img=20> [SURVIVOR] " + name + " style will begin soon, type ::outlast!").addTeleport(new Position(2064, 6004, 0)).copyMessageToChatbox().submit();
    }
}
