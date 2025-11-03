package io.zaryx.content.minigames.newMinigames.squidGames;

import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.lobby.Lobby;

import java.util.List;

public class SquidLobby extends Lobby {
    @Override
    public void onJoin(Player player) {

    }

    @Override
    public void onLeave(Player player) {

    }

    @Override
    public boolean canJoin(Player player) {
        if (player.getItems().isWearingItems()) {
            player.sendMessage("You must bank all worn items to join the lobby.");
            return false;
        }
        if (player.getItems().freeSlots() < 28) {
            player.sendMessage("You must have no items in your Inventory to join the lobby.");
            return false;
        }
        return true;
    }

    @Override
    public void onTimerFinished(List<Player> lobbyPlayers) {

    }

    @Override
    public void onTimerUpdate(Player player) {
        String timeLeftString = formattedTimeLeft();
        player.getPA().sendFrame126("Squid Games Begins in: @gre@" + timeLeftString, 6570);
        player.getPA().sendFrame126("", 6571);
        player.getPA().sendFrame126("", 6572);
        player.getPA().sendFrame126("", 6664);
        player.getPA().walkableInterface(6673);
    }

    @Override
    public long waitTime() {
        return 0;
    }

    @Override
    public int capacity() {
        return 20;
    }

    @Override
    public String lobbyFullMessage() {
        return "The lobby is currently full please wait for another game!";
    }

    @Override
    public boolean shouldResetTimer() {
        return this.getWaitingPlayers().isEmpty();
    }

    @Override
    public Boundary getBounds() {
        return null;
    }
}
