package io.zaryx.content.minigames.newMinigames;

import io.zaryx.model.entity.player.Player;

public interface MinigameController {

    // Method to start the minigame
    void start();

    // Method to end the minigame
    void end();

    // Method to add a player to the minigame
    void addPlayer(Player player);

    // Method to remove a player from the minigame
    void removePlayer(Player player);

    // Method to check if the minigame is active
    boolean isActive();

    // Method to get the lobby countdown time
    int getLobbyCountdown();

    // Method to set the lobby countdown time
    void setLobbyCountdown(int seconds);

    // Method to get the players in the minigame
    public int getPlayers();

    void managePlayerHealth(Player player);
    void managePlayerInventory(Player player);
    void spawnEnemies();
    void manageGameEnvironment();
    void handleVictors();
}
