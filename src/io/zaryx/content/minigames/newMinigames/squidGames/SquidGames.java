package io.zaryx.content.minigames.newMinigames.squidGames;

//import io.zaryx.content.worldevent.impl.SquidGameWorldEvent;


//public class SquidGames implements MinigameController {

//    private static volatile SquidGames singleton;
//
//    private int gameNpc = 901;
//    private int STARTX = 1827;
//    private int STARTY = 4481;
//    private int LOBBYX = 2888 + (Misc.random(3, 5));
//    private int LOBBYY = 2696 + (Misc.random(3, 5));
//    private final ArrayList<String> victors = new ArrayList<>();
//    public static Maps map;
//    private static final int LOBBY_TICK_INTERVAL = 1;
//    private static int _secondsUntilLobbyEnds;
//    private int secondsUntilLobbyEnds = _secondsUntilLobbyEnds;
//    private static int _playersToStart;
//    private final Object timerObject = new Object();
//    private final int[] itemsToRemove = {157, 159, 161, 145, 147, 149, 6687, 6689, 6691, 3026, 3028, 229, 3030, 3024, 12695, 12697, 12699, 12701, 3024, 6685, 229, 229, 229, 229, 385, 3144};
//    private boolean lobbyOpen;
//    private boolean gameActive;
//    private int amountOfPlayers;
//    private static final ArrayList<String> currentPlayers = new ArrayList<>();
//    private static final Object SQUID_OBJECT = new Object();
//
//    public boolean isInLobbyBounds(Player player) {
//        if (Boundary.isIn(player, new Boundary(2888, 2696, 2914, 2705, 0))) {
//            return true;
//        }
//        return player.absX >= 2100 && player.absX <= 2179 && player.absY >= 5100 && player.absY <= 5572;
//    }
//
//    @Override
//    public void start() {
//        amountOfPlayers = currentPlayers.size();
//        ArrayList<String> toRemove = Lists.newArrayList();
//        for (String p : currentPlayers) {
//            Player player = PlayerHandler.getPlayerByLoginName(p);
//                if (player != null) {
//                    toRemove.add(p);
//                    continue;
//            }
//                if (!isInLobbyBounds(player)) {
//                    player.sendMessage("You will not longer participate in the games, as you were");
//                    player.sendMessage("not in the lobby when it began.");
//                    toRemove.add(p);
//                    continue;
//                }
//                if (player.hasOverloadBoost) {
//                    player.getPotions().resetOverload();
//                }
//                player.sendMessage("@red@ Welcome to the Squid Games get ready!");
//        }
//        for (String r : toRemove) {
//            PlayerHandler.getOptionalPlayerByLoginName(r).ifPresent(plr -> leaveLobby(plr, false));
//            currentPlayers.remove(r);
//        }
//        if (currentPlayers.size() != 1) {
//            if (currentPlayers.size() == 0) {
//                handleVictors();
//            }
//            gameActive = true;
//            lobbyOpen = false;
//            checkPlayerList();
//            CycleEventHandler.getSingleton().addEvent(SQUID_OBJECT, new CycleEvent() {
//                int tick;
//                @Override
//                public void execute(CycleEventContainer container) {
//                    tick++;
//                    updateInterface();
//                    for (String name : currentPlayers) {
//                        Player player = PlayerHandler.getPlayerByLoginName(name);
//                        if (player != null) {
//                            if (tick == 10) {
//                                addWearItems(player);
//                                spawnEnemies();
//                                player.forcedChat("The Squid Games have begun!");
//                            } else {
//                                player.forcedChat("" + (10 - tick) + " seconds until the games begin!");
//                            }
//                        }
//                    }
//                    while (currentPlayers.size() >= 1) {
//                        moveAndKillPlayers(NPCHandler.npcs[gameNpc]);
//                    }
//                    updateInterface();
//                    if (tick == 10) {
//                        container.stop();
//                    }
//                }
//            }, 1);
//        }
//
//    }
//
//    public void moveAndKillPlayers(NPC npc) {
//        // Move the NPC 5-7 tiles north
//        int moveTiles = 5 + Misc.random(2);
//        NPCDumbPathFinder.walkTowards(npc, npc.getX(), npc.getY() + moveTiles);
//
//        // Make the NPC stop and turn around to face south
//        npc.setFacePlayer(true);
//
//        // Check if there are still players moving
//        for (Player player : PlayerHandler.players) {
//            if (player != null && player.isMoving) {
//                // If a player is still moving, kill them
//                //player.applyDead = true;
//                //TODO player kill
//            }
//        }
//    }
//
//    private void checkPlayerList() {
//        currentPlayers.stream().filter(Objects::nonNull).forEach(p -> {
//        });
//    }
//
//    public String getTimeLeft() {
//        if (isGameActive()) {
//            return "SquidGames: @gre@Active";
//        } else if (isLobbyOpen()) {
//            return "SquidGames @gre@" + getLobbyTime();
//        } else {
//            return "SquidGames: @red@" + WorldEventContainer.getInstance().getTimeUntilEvent(new SquidGameWorldEvent()) + " minutes";
//        }
//    }
//
//    private String getLobbyTime() {
//        int minutes = secondsUntilLobbyEnds / 60;
//        int seconds = secondsUntilLobbyEnds % 60;
//
//        if (secondsUntilLobbyEnds == 0)
//            return "Moving to game..";
//        return "Starts in: "+String.format("%d:%02d", minutes, seconds);
//    }
//
//    public static void initialiseSingleton() {
//        if (!Server.isDebug()) {
//            _secondsUntilLobbyEnds = 120;
//            _playersToStart = 2;
//        } else {
//            _secondsUntilLobbyEnds = 10;
//            _playersToStart = 1;
//        }
//        singleton = new SquidGames();
//    }
//
//    public static SquidGames getSingleton() {
//        return singleton;
//    }
//
//    public void addWearItems(Player player) {
//        player.getItems().equipItem(8953, 1, 0);
//        player.getItems().equipItem(8954, 1, 4);
//        player.getItems().equipItem(8993, 1, 7);
//    }
//
//    public static boolean handleObjects(Player player, WorldObject object, int optionId) {
//
//        if (object == null || object.getId() != 4388)
//            return false;
//
//        switch (optionId) {
//            case 1:
//                if (!SquidGames.getSingleton().isLobbyOpen()) {
//                    player.sendMessage("There is no Squid Games active currently.");
//                    return true;
//                }
//                getSingleton().join(player);
//                return true;
//            case 2:
//                getSingleton().leaveLobby(player, false);
//                return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void end() {
//    getActivePlayers().forEach(p -> leaveLobby(p, false));
//    currentPlayers.clear();
//    victors.clear();
//    gameActive = false;
//    lobbyOpen = false;
//    }
//
//    @Override
//    public void addPlayer(Player player) {
//        if (player.getItems().isWearingItems()) {
//            player.sendMessage("You must bank all worn items to join the lobby.");
//            return;
//        }
//        if (player.getItems().freeSlots() < 28) {
//            player.sendMessage("You must have no items in your Inventory to join the lobby.");
//            return;
//        }
//    }
//
//    public void join(Player player) { //TODO MAYBE ADD MORE JOIN REQUIREMENTS?
//        if (!SquidGames.getSingleton().isLobbyOpen()) {
//            player.sendMessage("The Squid games lobby is not currently open.");
//            return;
//        }
//        if (checkMacAddress(player)) {
//            player.sendMessage("You can only play with one account per computer.");
//        }
//        if (player.getPotions().hasPotionBoost()) {
//            player.getPotions().resetPotionBoost();
//        }
//        player.setRunEnergy(100, true);
//        player.playerLevel[5] = player.getPA().getLevelForXP(player.playerXP[5]);
//        player.getHealth().removeAllStatuses();
//        player.getHealth().reset();
//        player.getPA().refreshSkill(5);
//        CombatPrayer.resetPrayers(player);
//        player.resetVengeance();
//        player.specRestore = 120;
//        player.specAmount = 10.0;
//        player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
//        currentPlayers.add(player.getLoginName());
//        player.moveTo(new Position(LOBBYX, LOBBYY, 0));
//        //TODO ADD LOBBY COORDS HERE
//        player.sendMessage("You have joined the Squid Games Lobby");
//        player.sendMessage("You will be transferred to the game when it starts.");
//        updateInterface();
//    }
//
//    /**
//     * Checks a players mac address against all in the current lobby
//     */
//    public boolean checkMacAddress(Player player) {
//        for (String playerName : currentPlayers) {
//            Player p = PlayerHandler.getPlayerByLoginName(playerName);
//            if (p != null) {
//                if (p.getMacAddress().equalsIgnoreCase(player.getMacAddress())) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     *
//     * New Methods add to Minigame Controller
//     */
//
//    public boolean isGameActive() {
//        return gameActive;
//    }
//
//    private List<Player> getActivePlayers() {
//        return currentPlayers.stream().map(PlayerHandler::getPlayerByLoginName).filter(Objects::nonNull).collect(Collectors.toList());
//    }
//
//
//    /**
//     * End
//     *
//     */
//
//    @Override
//    public void removePlayer(Player player) {
//
//    }
//
//    @Override
//    public boolean isActive() {
//        return false;
//    }
//
//    @Override
//    public int getLobbyCountdown() {
//        return 670;
//    }
//
//    @Override
//    public void setLobbyCountdown(int seconds) {
//
//    }
//
//    @Override
//    public int getPlayers() {
//        return currentPlayers.size();
//    }
//
//    @Override
//    public void managePlayerHealth(Player player) {
//
//    }
//
//    @Override
//    public void managePlayerInventory(Player player) {
//
//    }
//
//    @Override
//    public void spawnEnemies() {
//        NPCHandler.newNPC(gameNpc, 1827, 4488, 0, 0, 0);
//    }
//
//    @Override
//    public void manageGameEnvironment() {
//
//    }
//
//    @Override
//    public void handleVictors() {
//        currentPlayers.clear();
//        for (int i = victors.size() - 1; i >= 0; i--) {
//            System.out.println("SquidGames: " + victors.size() + " / " + i);
//            String name = victors.get(i);
//            Player player = PlayerHandler.getPlayerByLoginName(name);
//            if (player != null) {
//                if (victors.size() > 2) {
//                    player.sendMessage("Congratulations! You Place" + ((2 - i) + 1) + " in the Squid Games!");
//                } else {
//                    player.sendMessage("Congratulations! You Place" + ((victors.size() - i) + 1) + " in the Squid Games!");
//
//                }
//                if (player.getMode().getCoinRewardsFromTournaments()) {
//                    if (victors.size() > 2) {
//                        if (i == 2) {
//                            player.getItems().addItemToBankOrDrop(696, 10);//1st place gets 6m + 1m every 2 people
//                            //TODO REWARD
//                        } else {
//                            player.getItems().addItemToBankOrDrop(696, 5);//2nd and 3rd get 2m + 500k ever 2 people
//                            //TODO REWARD
//                        }
//                    } else {
//                        if (i == 1) {
//                            player.getItems().addItemToBankOrDrop(696, 10);//1st place gets 6m + 1m every 2 people
//                            //TODO REWARD
//                        } else {
//                            player.getItems().addItemToBankOrDrop(696, 5);//2nd and 3rd get 2m + 500k ever 2 people
//                            //TODO REWARD
//
//                        }
//                    }
//                } else {
//                    if (victors.size() > 2) {
//                        if (i == 2) {
//                            player.getItems().addItemToBankOrDrop(696, 10);//1st place gets 6m + 1m every 2 people
//                        } else if (i != 2) {
//                            player.getItems().addItemToBankOrDrop(696, 5);//2nd and 3rd get 2m + 500k ever 2 people
//                        }
//                        //TODO player attacking reset?
//                        player.attacking.reset();
//                    } else {
//                        if (i == 1) {
//                            player.getItems().addItemToBankOrDrop(696, 10);//1st place gets 6m + 1m every 2 people
//                        } else if (i == 0) {
//                            player.getItems().addItemToBankOrDrop(696, 5);//2nd and 3rd get 2m + 500k ever 2 people
//                        }
//                        player.attacking.reset();
//                    }
//                }
///** TODO WIN STREAKS
// *
// */
//  /*              if (victors.size() >2) {
//                    if (i == 2) {
//                        player.streak += 1;
//                        if (player.streak == 5) {
//                            player.sendMessage("@red@You receive an extra 5m coins for having a 5 win streak.");
//                            announce("@blu@" + name + " has just reached 5 wins in a row in Outlast!");
//                            player.getItems().addItemToBankOrDrop(696, 10);
//                            player.attacking.reset();
//                        } else if (player.streak == 10) {
//                            player.sendMessage("@red@You receive an extra 10m coins for having a 10 win streak.");
//                            announce("@blu@" + name + " has just reached 10 wins in a row in Outlast!");
//                            player.getItems().addItemToBankOrDrop(696, 20);
//                        }
//                        announce(player.getDisplayNameFormatted() + " has won the Tournament. Congratulations!");
//                        announce(player.getDisplayNameFormatted() + " Current streak is " + player.streak + "!");
//                        player.tournamentWins += 1; //winner of tournament gets  a tournament win
//                        player.tournamentPoints += 3; //winner of tournament gets  a tournament win
//                        player.getEventCalendar().progress(EventChallenge.WIN_AN_OUTLAST_TOURNAMENT);
//                        player.getEventCalendar().progress(EventChallenge.PARTICIPATE_IN_X_OUTLAST_TOURNIES);
////                        LeaderboardUtils.addCount(LeaderboardType.OUTLAST_WINS, player, 1);
//                        player.sendMessage("@blu@You now have a total of @bla@" + player.tournamentPoints + " tournament points.");
//                        player.sendMessage("@blu@You have won a total of @bla@" + player.tournamentWins + " @blu@tournament wins.");
//                        player.sendMessage("@blu@You have gained an extra point for participating.");
//                        player.attacking.reset();
//                        player.sendMessage("You current winstreak is @red@" + player.streak + ".");
//                        WINNER = "" + name + "";
//                        Server.getDatabaseManager().exec(new OutlastRecentWinnersAdd(new OutlastRecentWinner(player)));
//                        Server.getDatabaseManager().exec(new OutlastLeaderboardAdd(new OutlastLeaderboardEntry(player)));
//                        ViewingOrb.kickSpectators();
//                    }
//                    if (i != 2) {
//                        player.streak = 0;
//                        player.sendMessage("@blu@You now have a total of @bla@" + player.tournamentPoints + " tournament points.");
//                        player.getEventCalendar().progress(EventChallenge.PARTICIPATE_IN_X_OUTLAST_TOURNIES);
//                        player.attacking.reset();
//                    }
//                } else {
//                    if (i == 1) {
//                        player.streak += 1;
//                        if (player.streak == 5) {
//                            player.sendMessage("@red@You receive an extra 5m coins for having a 5 win streak.");
//                            announce("@blu@" + name + " has just reached 5 wins in a row in Outlast!");
//                            player.getItems().addItemToBankOrDrop(696, 5);
//                            player.attacking.reset();
//                        } else if (player.streak == 10) {
//                            player.sendMessage("@red@You receive an extra 10m coins for having a 10 win streak.");
//                            announce("@blu@" + name + " has just reached 10 wins in a row in Outlast!");
//                            player.getItems().addItemToBankOrDrop(696, 10);
//                        }
//                        announce(player.getDisplayNameFormatted() + " has won the Tournament. Congratulations!");
//                        announce(player.getDisplayNameFormatted() + " Current streak is " + player.streak + "!");
//                        player.tournamentWins += 1; //winner of tournament gets  a tournament win
//                        player.tournamentPoints += 3; //winner of tournament gets  a tournament win
//                        player.getEventCalendar().progress(EventChallenge.WIN_AN_OUTLAST_TOURNAMENT);
//                        player.getEventCalendar().progress(EventChallenge.PARTICIPATE_IN_X_OUTLAST_TOURNIES);
////                        LeaderboardUtils.addCount(LeaderboardType.OUTLAST_WINS, player, 1);
//                        player.sendMessage("@blu@You now have a total of @bla@" + player.tournamentPoints + " tournament points.");
//                        player.sendMessage("@blu@You have won a total of @bla@" + player.tournamentWins + " @blu@tournament wins.");
//                        player.sendMessage("@blu@You have gained an extra point for participating.");
//                        player.attacking.reset();
//                        player.sendMessage("You current winstreak is @red@" + player.streak + ".");
//                        WINNER = "" + name + "";
//                        Server.getDatabaseManager().exec(new OutlastRecentWinnersAdd(new OutlastRecentWinner(player)));
//                        Server.getDatabaseManager().exec(new OutlastLeaderboardAdd(new OutlastLeaderboardEntry(player)));
//                        ViewingOrb.kickSpectators();
//                    }
//                    if (i == 0) {
//                        player.streak = 0;
//                        player.sendMessage("@blu@You now have a total of @bla@" + player.tournamentPoints + " tournament points.");
//                        player.getEventCalendar().progress(EventChallenge.PARTICIPATE_IN_X_OUTLAST_TOURNIES);
//                        player.attacking.reset();
//                    }
//                }
//            }
//        }
//
//   */
//
//                victors.clear();
//                lobbyOpen = false;
//                gameActive = false;
//            }
//        }
//    }
//
//    public void openLobby() {
//        initializeLobbyTimer();
//
//    }
//
//    private void initializeLobbyTimer() {
//        clearActiveLobby();
//        CycleEventHandler.getSingleton().stopEvents(timerObject);
//        secondsUntilLobbyEnds = _secondsUntilLobbyEnds;
//        lobbyOpen = true;
//        CycleEventHandler.getSingleton().addEvent(timerObject, new CycleEvent() {
//            @Override
//            public void execute(CycleEventContainer container) {
//                int remaining = secondsUntilLobbyEnds < 1 ? 0 : (secondsUntilLobbyEnds -= LOBBY_TICK_INTERVAL);
//                if (currentPlayers.size() < _playersToStart) {
//                    secondsUntilLobbyEnds = _secondsUntilLobbyEnds;
//                } else if (remaining <= 0) {
//                    start();
//                    container.stop();
//                }
//                updateInterface();
//            }
//        }, 2);
//    }
//
//    public void leaveLobby(Player player, boolean xlog) {
//        currentPlayers.remove(player.getLoginName());
//        if (xlog) {
//            if (player.getMode().equals(Mode.forType(ModeType.GROUP_WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
//                player.getPA().forceMove(3126, 3629, 0, true);
//            } else {
//                player.getPA().forceMove(2095, 6003, 0, true);
//            }
//        } else {
//            if (player.getMode().equals(Mode.forType(ModeType.GROUP_WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
//                player.getPA().forceMove(3126, 3629, 0, true);
//            } else {
//                player.getPA().forceMove(2095, 6003, 0, true);
//            }
//        }
//    }
//    private void clearActiveLobby() {
//        if (isLobbyOpen()) {
//            lobbyOpen = false;
//            ArrayList<Player> toLeave = Lists.newArrayList();
//            for (String name : currentPlayers) {
//            Player p = PlayerHandler.getPlayerByLoginName(name);
//            if (p != null) {
//                toLeave.add(p);
//                }
//            }
//            for (Player p : toLeave) {
//                if (p != null) {
//                    leaveLobby(p, false);
//                }
//            }
//            toLeave.clear();;
//            currentPlayers.clear();
//        }
//    }
//
//    private void updateInterface() {
//        for (String name : currentPlayers) {
//            Player player = PlayerHandler.getPlayerByLoginName(name);
//            player.getPA().sendFrame126("Next Game Begins In : " + getLobbyTime(), 2805);
//            player.getPA().sendFrame126("Winner: " + victors, 2806);
//            player.getPA().sendFrame36(560, 1);
//            player.getPA().walkableInterface(2804);
//    }
//    }
//
//    public boolean isLobbyOpen() {
//        return lobbyOpen;
//    }

//}
