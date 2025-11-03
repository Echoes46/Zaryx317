package io.zaryx.content.skills.dungeoneering;

import com.google.api.client.util.Lists;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.ClientGameTimer;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.lobby.Lobby;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Author @Kai
 * Discord - ZDS_KAI
 */

public class DungLobby extends Lobby {

    private int LOBBY_X = 3087; //Change Lobby Coords here after
    private int LOBBY_Y = 3500; //Change lobby Y here

    /**
     * Handles events for a player successfully joining the lobby
     *
     * @param player The player joining
     */
    @Override
    public void onJoin(Player player) {
        player.getPA().movePlayer(LOBBY_X, LOBBY_Y);
        player.sendMessage("You have joined the Dungeoneering Lobby!");
        String timeLeftString = formattedTimeLeft();
        player.sendMessage("Raid starts in: " + timeLeftString);
        player.getPA().sendGameTimer(ClientGameTimer.DUNGEON_TIMER, TimeUnit.SECONDS, 29);
    }

    /**
     * Handles events for a player successfully leaving the lobby
     *
     * @param player The player leaving
     */
    @Override
    public void onLeave(Player player) {

    }

    /**
     * Checks whether the supplied player has requirements to join this lobby
     *
     * @param player The player to check
     * @return <value>true</value> if the player passes all requirements, otherwise <value>false</value>
     */
    @Override
    public boolean canJoin(Player player) {
        return false;
    }

    /**
     * The event that is called when the lobby timer ends
     *
     * @param lobbyPlayers The players that were in the lobby when the timer finished
     */
    @Override
    public void onTimerFinished(List<Player> lobbyPlayers) {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        container.stop();
                        List<Player> dungPlayers;

                        dungPlayers = Lists.newArrayList(lobbyPlayers);
                        //new DungMain().startDung(dungPlayers, false);
                    }
                }, 0);
    }

    /**
     * Called each time the timer cycles, used for updating interfaces or sending messages
     *
     * @param player The player to update
     */
    @Override
    public void onTimerUpdate(Player player) {

        player.addQueuedAction( plr -> {

            String timeLeftString = formattedTimeLeft();
            plr.getPA().sendString("Dungeon begins in: @gre@" + timeLeftString, 6570);
            plr.getPA().sendString("", 6572);
            plr.getPA().sendString("", 6664);
        });

    }

    /**
     * Used at the start of the timer event to initilize the countdown
     *
     * @return The time in milliseconds between each lobby being sent out
     */
    @Override
    public long waitTime() {
        return 0;
    }

    /**
     * Checked when the player attemps to join the lobby
     *
     * @return The maximum number of players that this lobby supports
     */
    @Override
    public int capacity() {
        return 4;
    }

    /**
     * Sent to the player via a game message
     *
     * @return A string to display when the lobby is full
     */
    @Override
    public String lobbyFullMessage() {
        return null;
    }

    /**
     * Checks whether the timer event should reset the timer
     *
     * @return <value>true</value> if the timer should reset back to <code>waitTime()</code>
     */
    @Override
    public boolean shouldResetTimer() {
        return false;
    }

    /**
     * Used to check if the player is within the bounds of the lobby
     *
     * @return The boundary of the lobby area
     */
    @Override
    public Boundary getBounds() {
        return Boundary.DUNG_LOBBY;
    }
}
