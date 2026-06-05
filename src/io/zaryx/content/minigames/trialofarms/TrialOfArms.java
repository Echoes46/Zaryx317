package io.zaryx.content.minigames.trialofarms;

// Author: Khaos

import io.zaryx.content.dialogue.DialogueBuilder;
import io.zaryx.content.dialogue.DialogueOption;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.player.Boundary;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.Position;
import io.zaryx.model.entity.player.Right;
import io.zaryx.util.Misc;
import io.zaryx.util.discord.Discord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrialOfArms {

    public static final int ENTRY_FEE_GP = 5_000_000;
    public static final int COINS = 995;
    public static final int NPC_ID = 10388;

    public static final int MIN_PLAYERS = 3;
    public static final int MAX_LEVEL = 10;
    public static final int COUNTDOWN_SECONDS = 30;
    public static final int GAME_TIME_SECONDS = 900; // 15 minutes

    /*
     * Updated by Khaos
     * Trial of Arms supplies.
     */
    public static final int SUPPLY_AMOUNT = 2_500;

    public static final int RUNE_ARROW = 892;

    public static final int AIR_RUNE = 556;
    public static final int WATER_RUNE = 555;
    public static final int EARTH_RUNE = 557;
    public static final int FIRE_RUNE = 554;
    public static final int MIND_RUNE = 558;
    public static final int CHAOS_RUNE = 562;
    public static final int DEATH_RUNE = 560;
    public static final int BLOOD_RUNE = 565;
    public static final int SOUL_RUNE = 566;

    private static final List<Player> players = new ArrayList<>();
    private static final Map<String, Integer> levels = new HashMap<>();
    private static final Map<String, Integer> kills = new HashMap<>();
    private static final Map<String, String> lastKilledBy = new HashMap<>();
    private static final Map<String, Integer> sameKillerDeaths = new HashMap<>();

    private static boolean active = false;
    private static boolean countdownStarted = false;
    private static CombatStyle activeStyle;
    private static WeaponTrack activeTrack;
    private static int prizePool = 0;

    public enum CombatStyle {
        MELEE,
        RANGE,
        MAGE
    }

    public enum WeaponTrack {
        DAGGERS(CombatStyle.MELEE, true, new int[]{
                1205, 1203, 1207, 1209, 1211,
                1213, 1215, 1231, 5680, 5698
        }),

        SCIMITARS(CombatStyle.MELEE, false, new int[]{
                1321, 1323, 1325, 1327, 1329,
                1331, 1333, 4587, 6611, 20000
        }),

        SHORTBOWS(CombatStyle.RANGE, true, new int[]{
                841, 843, 849, 853, 857,
                861, 6724, 11235, 12765, 19478
        }),

        LONGBOWS(CombatStyle.RANGE, false, new int[]{
                839, 845, 847, 851, 855,
                859, 4734, 18357, 20997, 21012
        }),

        STAFFS(CombatStyle.MAGE, true, new int[]{
                1381, 1383, 1385, 1387, 1391,
                1393, 1395, 4710, 6914, 21006
        });

        private final CombatStyle style;
        private final boolean enabled;
        private final int[] weapons;

        WeaponTrack(CombatStyle style, boolean enabled, int[] weapons) {
            this.style = style;
            this.enabled = enabled;
            this.weapons = weapons;
        }

        public CombatStyle getStyle() {
            return style;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getWeaponForLevel(int level) {
            return weapons[Math.max(0, Math.min(level - 1, weapons.length - 1))];
        }
    }

    public static void handleNpc(Player player) {
        if (player == null) {
            return;
        }

        if (!isStaff(player)) {
            player.start(new DialogueBuilder(player).npc(NPC_ID,
                    "Trial of Arms is currently only available",
                    "to staff members."));
            return;
        }

        if (active) {
            player.start(new DialogueBuilder(player).npc(NPC_ID,
                    "Trial of Arms is already active.",
                    "Please wait for the current match to finish."));
            return;
        }

        if (players.contains(player)) {
            player.start(new DialogueBuilder(player).npc(NPC_ID,
                            "You are already signed up for Trial of Arms.",
                            "Would you like to leave the queue and get your money back?")
                    .option(
                            new DialogueOption("Yes, leave the queue.", TrialOfArms::leaveQueue),
                            new DialogueOption("No, stay in the queue.", p -> p.getPA().closeAllWindows())
                    ));
            return;
        }

        player.start(new DialogueBuilder(player).npc(NPC_ID,
                        "Welcome to the Trial of Arms.",
                        "Would you like to enter for 5 million GP?")
                .option(
                        new DialogueOption("Yes, enter Trial of Arms.", TrialOfArms::joinQueue),
                        new DialogueOption("No, thanks.", p -> p.getPA().closeAllWindows())
                ));
    }

    private static void joinQueue(Player player) {
        player.getPA().closeAllWindows();

        if (active) {
            player.sendMessage("@red@Trial of Arms is already active.");
            return;
        }

        if (players.contains(player)) {
            player.sendMessage("@red@You are already in the Trial of Arms queue.");
            return;
        }

        if (!canEnter(player)) {
            return;
        }

        takeEntryFee(player);
        players.add(player);
        prizePool += ENTRY_FEE_GP;

        player.sendMessage("@blu@You have entered Trial of Arms. Waiting for " + MIN_PLAYERS + " players.");

        /*
         * Updated by Khaos
         * Queue count only goes to queued Trial of Arms players.
         */
        announceToQueued("@blu@Trial of Arms: " + players.size() + "/" + MIN_PLAYERS + " players have joined!");

        if (players.size() >= MIN_PLAYERS && !countdownStarted) {
            startCountdown();
        }
    }

    private static void leaveQueue(Player player) {
        player.getPA().closeAllWindows();

        if (!players.contains(player)) {
            player.sendMessage("@red@You are not in the Trial of Arms queue.");
            return;
        }

        if (players.size() >= MIN_PLAYERS) {
            player.sendMessage("@red@You cannot leave the Trial of Arms queue because enough players have joined.");
            player.sendMessage("@red@The match is about to begin.");
            return;
        }

        players.remove(player);
        prizePool -= ENTRY_FEE_GP;

        player.getItems().addItemUnderAnyCircumstance(COINS, ENTRY_FEE_GP);
        player.sendMessage("@gre@You have left the Trial of Arms queue and received your 5,000,000 GP back.");

        /*
         * Updated by Khaos
         * Queue count only goes to queued Trial of Arms players.
         */
        announceToQueued("@blu@Trial of Arms: " + players.size() + "/" + MIN_PLAYERS + " players are queued.");
    }

    private static boolean hasDisallowedInventoryItems(Player player) {
        return player.getItems().getInventoryItems().stream()
                .anyMatch(item -> item.getId() != COINS);
    }

    private static boolean hasEquipmentItems(Player player) {
        return !player.getItems().getEquipmentItems().isEmpty();
    }

    private static boolean canEnter(Player player) {
        if (!isStaff(player)) {
            player.sendMessage("@red@Only staff members can enter Trial of Arms.");
            return false;
        }

        if (hasDisallowedInventoryItems(player)) {
            player.sendMessage("@red@You may only bring coins to enter Trial of Arms.");
            return false;
        }

        if (hasEquipmentItems(player)) {
            player.sendMessage("@red@You must remove all equipment before entering Trial of Arms.");
            return false;
        }

        if (player.getItems().getItemAmount(COINS) < ENTRY_FEE_GP) {
            player.sendMessage("@red@You need 5,000,000 GP to enter Trial of Arms.");
            return false;
        }

        return true;
    }

    private static boolean isStaff(Player player) {
        return player.getRights().isOrInherits(Right.MODERATOR);
    }

    private static void takeEntryFee(Player player) {
        player.getItems().deleteItem(COINS, ENTRY_FEE_GP);
    }

    private static void startCountdown() {
        countdownStarted = true;

        /*
         * Updated by Khaos
         * Countdown only goes to queued Trial of Arms players.
         */
        announceToQueued("@red@Trial of Arms has enough players! Starting in 30 seconds...");
        Discord.writeTrialOfArms("Trial of Arms has enough players! Starting in 30 seconds.");

        CycleEventHandler.getSingleton().addEvent("trial_of_arms_countdown", new CycleEvent() {
            int seconds = COUNTDOWN_SECONDS;

            @Override
            public void execute(CycleEventContainer container) {
                if (active) {
                    container.stop();
                    return;
                }

                if (players.size() < MIN_PLAYERS) {
                    countdownStarted = false;
                    announceToQueued("@red@Trial of Arms countdown cancelled. Not enough players.");
                    container.stop();
                    return;
                }

                if (seconds == 30 || seconds == 20 || seconds == 10 || seconds <= 5) {
                    announceToQueued("@red@Trial of Arms starts in " + seconds + " seconds!");
                }

                if (seconds <= 0) {
                    startGame();
                    container.stop();
                    return;
                }

                seconds--;
            }

            @Override
            public void onStopped() {
            }
        }, 1);
    }

    private static void startGame() {
        active = true;
        countdownStarted = false;

        activeTrack = randomEnabledTrack();
        activeStyle = activeTrack.getStyle();

        for (Player player : players) {
            levels.put(player.getLoginNameLower(), 1);
            kills.put(player.getLoginNameLower(), 0);
            sameKillerDeaths.put(player.getLoginNameLower(), 0);
            lastKilledBy.remove(player.getLoginNameLower());

            moveToArena(player);
            giveCurrentWeapon(player);
            player.sendMessage("@blu@Trial of Arms has started! Style: @red@" + activeStyle.name());
        }

        announce("@red@Trial of Arms has started! Combat style: " + activeStyle.name() + "!");
        Discord.writeTrialOfArms("Trial of Arms has started! Style: " + activeStyle.name());

        startGameTimer();
    }

    private static void startGameTimer() {
        CycleEventHandler.getSingleton().addEvent("trial_of_arms_game_timer", new CycleEvent() {
            int seconds = GAME_TIME_SECONDS;

            @Override
            public void execute(CycleEventContainer container) {
                if (!active) {
                    container.stop();
                    return;
                }

                if (players.isEmpty()) {
                    active = false;
                    reset();
                    container.stop();
                    return;
                }

                if (seconds == 600) {
                    announceToQueued("@red@Trial of Arms: 10 minutes remaining!");
                } else if (seconds == 300) {
                    announceToQueued("@red@Trial of Arms: 5 minutes remaining!");
                } else if (seconds == 60) {
                    announceToQueued("@red@Trial of Arms: 1 minute remaining!");
                } else if (seconds == 30) {
                    announceToQueued("@red@Trial of Arms: 30 seconds remaining!");
                } else if (seconds <= 10 && seconds > 0) {
                    announceToQueued("@red@Trial of Arms ends in " + seconds + " seconds!");
                }

                if (seconds <= 0) {
                    finishGameByTimer();
                    container.stop();
                    return;
                }

                seconds--;
            }

            @Override
            public void onStopped() {
            }
        }, 1);
    }

    private static void finishGameByTimer() {
        if (!active) {
            return;
        }

        if (players.isEmpty()) {
            active = false;
            reset();
            return;
        }

        Player winner = players.get(0);

        for (Player player : players) {
            String playerName = player.getLoginNameLower();
            String winnerName = winner.getLoginNameLower();

            int playerKills = kills.getOrDefault(playerName, 0);
            int winnerKills = kills.getOrDefault(winnerName, 0);

            int playerLevel = levels.getOrDefault(playerName, 1);
            int winnerLevel = levels.getOrDefault(winnerName, 1);

            if (playerKills > winnerKills || playerKills == winnerKills && playerLevel > winnerLevel) {
                winner = player;
            }
        }

        announce("@red@Trial of Arms time is up!");
        Discord.writeTrialOfArms("Trial of Arms time is up!");

        finishGame(winner);
    }

    private static WeaponTrack randomEnabledTrack() {
        List<WeaponTrack> available = new ArrayList<>();

        for (WeaponTrack track : WeaponTrack.values()) {
            if (track.isEnabled()) {
                available.add(track);
            }
        }

        return available.get(Misc.random(available.size() - 1));
    }

    /**
     * Updated by Khaos
     */
    public static boolean handleKill(Player killer, Player victim) {
        if (!active || killer == null || victim == null) {
            return false;
        }

        if (!isInGame(killer) || !isInGame(victim)) {
            return false;
        }

        String killerName = killer.getLoginNameLower();

        kills.put(killerName, kills.getOrDefault(killerName, 0) + 1);

        int newLevel = levels.getOrDefault(killerName, 1) + 1;
        levels.put(killerName, newLevel);

        killer.sendMessage("@gre@You advanced to Trial of Arms level " + newLevel + "!");

        checkSameKillerPenalty(victim, killer);

        if (newLevel > MAX_LEVEL) {
            finishGame(killer);
            return true;
        }

        /*
         * Updated by Khaos
         * Replenishes killer supplies after a kill.
         */
        giveCurrentWeapon(killer);

        /*
         * Updated by Khaos
         * Respawns victim and replenishes supplies after death.
         */
        respawnPlayer(victim);

        return true;
    }

    /**
     * Updated by Khaos
     */
    public static void handleDeathWithoutKiller(Player victim) {
        if (victim == null || !isInGame(victim)) {
            return;
        }

        respawnPlayer(victim);
    }

    private static void checkSameKillerPenalty(Player victim, Player killer) {
        String victimName = victim.getLoginNameLower();
        String killerName = killer.getLoginNameLower();

        String previousKiller = lastKilledBy.get(victimName);

        if (previousKiller != null && previousKiller.equals(killerName)) {
            int streak = sameKillerDeaths.getOrDefault(victimName, 0) + 1;
            sameKillerDeaths.put(victimName, streak);

            if (streak >= 2) {
                int currentLevel = levels.getOrDefault(victimName, 1);

                if (currentLevel > 1) {
                    levels.put(victimName, currentLevel - 1);
                    victim.sendMessage("@red@You died to the same player twice and dropped a weapon level.");
                }

                sameKillerDeaths.put(victimName, 0);
            }
        } else {
            lastKilledBy.put(victimName, killerName);
            sameKillerDeaths.put(victimName, 1);
        }
    }

    /**
     * Updated by Khaos
     */
    private static void respawnPlayer(Player player) {
        moveToArena(player);
        giveCurrentWeapon(player);
    }

    /**
     * Updated by Khaos
     * Uses the Trial of Arms boundary:
     * 3016, 3479 to 3041, 3510.
     */
    private static void moveToArena(Player player) {
        int minX = 3016;
        int minY = 3479;
        int maxX = 3041;
        int maxY = 3510;

        int x = minX + Misc.random(maxX - minX);
        int y = minY + Misc.random(maxY - minY);

        Position position = new Position(x, y, 0);

        if (!Boundary.isIn(position, Boundary.TRIAL_OF_ARMS_BOUNDARY)) {
            position = new Position(3028, 3494, 0);
        }

        player.moveTo(position);
    }

    /**
     * Updated by Khaos
     */
    private static void giveCurrentWeapon(Player player) {
        deleteAllExceptCoins(player);

        int level = levels.getOrDefault(player.getLoginNameLower(), 1);
        int weapon = activeTrack.getWeaponForLevel(level);

        player.getItems().addItem(weapon, 1);

        if (activeStyle == CombatStyle.RANGE) {
            giveRangeSupplies(player);
        }

        if (activeStyle == CombatStyle.MAGE) {
            giveMageSupplies(player);
        }

        player.sendMessage("@blu@Trial of Arms level: @red@" + level + "@blu@/" + MAX_LEVEL);
    }

    /**
     * Updated by Khaos
     */
    private static void giveRangeSupplies(Player player) {
        player.getItems().addItem(RUNE_ARROW, SUPPLY_AMOUNT);
    }

    /**
     * Updated by Khaos
     */
    private static void giveMageSupplies(Player player) {
        player.getItems().addItem(AIR_RUNE, SUPPLY_AMOUNT);
        player.getItems().addItem(WATER_RUNE, SUPPLY_AMOUNT);
        player.getItems().addItem(EARTH_RUNE, SUPPLY_AMOUNT);
        player.getItems().addItem(FIRE_RUNE, SUPPLY_AMOUNT);
        player.getItems().addItem(MIND_RUNE, SUPPLY_AMOUNT);
        player.getItems().addItem(CHAOS_RUNE, SUPPLY_AMOUNT);
        player.getItems().addItem(DEATH_RUNE, SUPPLY_AMOUNT);
        player.getItems().addItem(BLOOD_RUNE, SUPPLY_AMOUNT);
        player.getItems().addItem(SOUL_RUNE, SUPPLY_AMOUNT);
    }

    private static void finishGame(Player winner) {
        active = false;

        List<Player> ranked = new ArrayList<>(players);
        ranked.sort((a, b) -> Integer.compare(
                kills.getOrDefault(b.getLoginNameLower(), 0),
                kills.getOrDefault(a.getLoginNameLower(), 0)
        ));

        int payoutPool = prizePool / 2;
        int first = (int) (payoutPool * 0.50);
        int second = (int) (payoutPool * 0.30);
        int third = payoutPool - first - second;

        payRank(ranked, 0, first);
        payRank(ranked, 1, second);
        payRank(ranked, 2, third);

        announce("@red@Trial of Arms has ended! Winner: " + winner.getDisplayName() + "!");
        Discord.writeTrialOfArms("Trial of Arms has ended! Winner: " + winner.getDisplayName() + "!");

        for (Player player : players) {
            deleteAllExceptCoins(player);
            player.moveTo(new Position(3087, 3492, 0));
            player.sendMessage("@blu@Trial of Arms has ended.");
        }

        reset();
    }

    private static void payRank(List<Player> ranked, int index, int amount) {
        if (ranked.size() <= index || amount <= 0) {
            return;
        }

        Player player = ranked.get(index);
        player.getItems().addItemUnderAnyCircumstance(COINS, amount);
        player.sendMessage("@gre@You received " + Misc.formatCoins(amount) + " GP from Trial of Arms.");
    }

    private static boolean isInGame(Player player) {
        return players.contains(player);
    }

    /**
     * Updated by Khaos
     */
    public static void exitAndForfeit(Player player) {
        if (player == null) {
            return;
        }

        String name = player.getLoginNameLower();

        /*
         * Remove them from the Trial of Arms player list no matter what.
         * This handles both queued players and active players if "players" is your shared list.
         */
        boolean removed = players.remove(player);

        levels.remove(name);
        kills.remove(name);
        lastKilledBy.remove(name);
        sameKillerDeaths.remove(name);

        deleteAllExceptCoins(player);

        player.getPA().movePlayer(3087, 3492, 0);

        if (!removed) {
            player.sendMessage("@red@You are not currently in Trial of Arms.");
            return;
        }

        player.sendMessage("@red@You have forfeited Trial of Arms and lost the game.");
        announce("@red@Trial of Arms: " + player.getDisplayName() + " has forfeited.");

        if (active) {
            if (players.size() == 1) {
                finishGame(players.get(0));
                return;
            }

            if (players.isEmpty()) {
                active = false;
                reset();
            }
        } else {
            if (players.isEmpty()) {
                reset();
            } else {
                announceToQueued("@blu@Trial of Arms: " + players.size() + "/" + MIN_PLAYERS + " players are queued.");
            }
        }
    }

    private static void deleteAllExceptCoins(Player player) {
        int coins = player.getItems().getItemAmount(COINS);

        player.getItems().deleteAllItems();

        if (coins > 0) {
            player.getItems().addItemUnderAnyCircumstance(COINS, coins);
        }
    }

    private static void announce(String message) {
        for (Player player : PlayerHandler.players) {
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Updated by Khaos
     * Sends Trial of Arms queue/countdown/game timer messages only to players
     * currently signed up or active in Trial of Arms.
     */
    private static void announceToQueued(String message) {
        for (Player player : players) {
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    private static void reset() {
        players.clear();
        levels.clear();
        kills.clear();
        lastKilledBy.clear();
        sameKillerDeaths.clear();
        prizePool = 0;
        activeStyle = null;
        activeTrack = null;
        countdownStarted = false;
    }
}