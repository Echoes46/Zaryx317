package io.zaryx.content.games.blackjack;

import io.zaryx.Configuration;
import io.zaryx.model.cycleevent.CycleEvent;
import io.zaryx.model.cycleevent.CycleEventContainer;
import io.zaryx.model.cycleevent.CycleEventHandler;
import io.zaryx.model.entity.player.Player;
import io.zaryx.model.entity.player.PlayerHandler;
import io.zaryx.model.entity.player.lock.CompleteLock;
import io.zaryx.model.SoundType;
import io.zaryx.util.Misc;

import java.util.ArrayList;
import java.util.List;

public class BJManager {
    private Deck deck;
    private Player player;
    private List<Card> dealerCards;
    private List<Card> playerCards;
    private List<Card> splitCards;
    public long betAmount;
    private long splitBet;
    public int cardWidgetId = 60953;

    private static final int CARD_BACK_SPRITE = 52;

    // Card positions (DEFINITIEF - niet veranderen)
    private static final int CARD_X_START = 187;
    private static final int CARD_SPACING = 50;
    private static final int PLAYER_Y = 194;
    private static final int DEALER_Y = 36;
    private static final int DECK_X = 402;
    private static final int DECK_Y = 30;
    // Split hand positions
    private static final int SPLIT_HAND1_X = 80;
    private static final int SPLIT_HAND2_X = 275;

    private enum State { IDLE, PLAYING, ENDING }
    private State state = State.IDLE;
    private boolean playerTurnOver;

    // Split state
    private boolean isSplit;
    private int activeHand; // 0 = playerCards, 1 = splitCards
    private boolean splitAces;
    private boolean hand1Bust;
    private boolean hand2Bust;
    private boolean hand1Done;

    /** Prevents spam-clicking during card animations */
    private boolean animating;

    /** Incremented on each new game to cancel stale CycleEvents */
    private int gameId = 0;

    public BJManager(Player player) {
        this.player = player;
        resetBoard();
    }

    // ==================== RESET ====================

    private void resetBoard() {
        cardWidgetId = 60953;
        deck = new Deck(this, 8);
        deck.shuffle();
        dealerCards = new ArrayList<>();
        playerCards = new ArrayList<>();
        splitCards = new ArrayList<>();
        state = State.IDLE;
        playerTurnOver = false;
        isSplit = false;
        activeHand = 0;
        splitAces = false;
        hand1Bust = false;
        hand2Bust = false;
        hand1Done = false;
        splitBet = 0;
        animating = false;

        player.getPA().runClientScript(13_031);
        sendBalance();
        player.getPA().sendString(60983, "0");
        player.getPA().sendString(60984, "0");
        player.getPA().sendString(60969, "<col=65280>" + Misc.formatCoins(player.BjWins));
        player.getPA().sendString(60970, "<col=ff0000>" + Misc.formatCoins(player.BjLoss));
        player.getPA().sendString(60971, (player.BjPay > 0 ? "<col=65280>P: " : "<col=ff0000>P: ") + Misc.formatAmountWithNegative(player.BjPay));
        player.getPA().sendString(60986, "DEALER MUST DRAW TO 16 AND STAND ON ALL 17'S");
        player.getPA().sendString(60987, "BLACKJACK PAYS 3-2");
    }

    // ==================== OPEN ====================

    public void open() {
        if (Configuration.DISABLE_BLACKJACK) return;
        player.getPA().sendFrame248(60950, 61500);
        // Nudge close button 5px down (client has it at y=0, needs y=5)
        player.getPA().runClientScript(35, 60950, 60952, 466, 0, 466, 5, 1, false);
        restoreState();
    }

    private void restoreState() {
        sendBalance();
        player.getPA().sendString(60969, "<col=65280>" + Misc.formatCoins(player.BjWins));
        player.getPA().sendString(60970, "<col=ff0000>" + Misc.formatCoins(player.BjLoss));
        player.getPA().sendString(60971, (player.BjPay > 0 ? "<col=65280>P: " : "<col=ff0000>P: ") + Misc.formatAmountWithNegative(player.BjPay));

        if (state == State.IDLE) {
            player.getPA().sendString(60983, "0");
            player.getPA().sendString(60984, "0");
            player.getPA().sendString(60986, "DEALER MUST DRAW TO 16 AND STAND ON ALL 17'S");
            player.getPA().sendString(60987, "BLACKJACK PAYS 3-2");
            return;
        }

        // Restore cards that were already dealt
        if (!playerCards.isEmpty()) {
            int baseX = isSplit ? SPLIT_HAND1_X : CARD_X_START;
            moveCards(playerCards, baseX, PLAYER_Y);
            sendCards(playerCards);
        }
        if (isSplit && !splitCards.isEmpty()) {
            moveCards(splitCards, SPLIT_HAND2_X, PLAYER_Y);
            sendCards(splitCards);
        }
        if (!dealerCards.isEmpty()) {
            moveCards(dealerCards, CARD_X_START, DEALER_Y);
            if (playerTurnOver || state == State.ENDING) {
                sendDealerCards();
            } else {
                // Dealer first card face-up, rest face-down
                player.getPA().runClientScript(13_030, dealerCards.get(0).getWidgetId(), dealerCards.get(0).getSpriteId());
                for (int i = 1; i < dealerCards.size(); i++) {
                    player.getPA().runClientScript(13_030, dealerCards.get(i).getWidgetId(), CARD_BACK_SPRITE);
                }
                player.getPA().sendString(60983, String.valueOf(dealerCards.get(0).getRank().getValue()));
            }
        }

        // Restore totals
        if (isSplit) {
            updatePlayerTotal();
        } else if (!playerCards.isEmpty()) {
            player.getPA().sendString(60984, String.valueOf(handValue(playerCards)));
        }
    }

    // ==================== BETTING ====================

    public void placeBet(long amount) {
        if (Configuration.DISABLE_BLACKJACK) return;
        if (state == State.PLAYING) {
            player.sendErrorMessage("You can't bet while a game is in play!");
            return;
        }
        if (state == State.ENDING) {
            gameId++;
            resetBoard();
        }
        if (amount <= 0) {
            player.sendErrorMessage("You can't gamble that amount!");
            return;
        }
        if (player.getItems().getInventoryCount(995) < amount) {
            player.sendErrorMessage("You don't have enough funds to play BlackJack!");
            return;
        }

        player.getItems().deleteItem2(995, (int) amount);
        betAmount = amount;
        player.getPA().sendSound(10, SoundType.SOUND); // coins
        sendBalance();
        dealInitialCards();
    }

    // ==================== DEALING ====================

    private void dealInitialCards() {
        state = State.PLAYING;
        playerTurnOver = false;
        isSplit = false;
        activeHand = 0;
        final int thisGame = gameId;

        playerCards.add(deck.dealCard());
        playerCards.add(deck.dealCard());
        dealerCards.add(deck.dealCard());
        dealerCards.add(deck.dealCard());

        animating = true;
        moveCards(playerCards, CARD_X_START, PLAYER_Y);
        moveCards(dealerCards, CARD_X_START, DEALER_Y);
        player.lock(new CompleteLock());

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int cycle = 0;
            @Override
            public void execute(CycleEventContainer container) {
                if (thisGame != gameId) { container.stop(); return; }
                if (cycle == 2) {
                    player.unlock();
                    animating = false;
                    sendCards(playerCards);

                    // Dealer: first face-up, second face-down
                    player.getPA().runClientScript(13_030, dealerCards.get(0).getWidgetId(), dealerCards.get(0).getSpriteId());
                    player.getPA().runClientScript(13_030, dealerCards.get(1).getWidgetId(), CARD_BACK_SPRITE);
                    player.getPA().sendString(60983, String.valueOf(dealerCards.get(0).getRank().getValue()));

                    int playerTotal = handValue(playerCards);
                    int dealerTotal = handValue(dealerCards);
                    boolean playerBJ = playerTotal == 21;
                    boolean dealerBJ = dealerTotal == 21;

                    if (dealerBJ || playerBJ) {
                        sendDealerCards();
                        if (dealerBJ && playerBJ) {
                            showResult("push", playerTotal, dealerTotal);
                            announcePush();
                        } else if (dealerBJ) {
                            showResult("lose", playerTotal, dealerTotal);
                            announceLoss(false);
                        } else {
                            showResult("win", playerTotal, dealerTotal);
                            announceWin(betAmount + (betAmount * 3 / 2));
                        }
                    } else {
                        player.getPA().sendString(60984, String.valueOf(playerTotal));
                    }
                    container.stop();
                }
                cycle++;
            }
        }, 1);
    }

    // ==================== HELPERS ====================

    private List<Card> getActiveCards() {
        return (isSplit && activeHand == 1) ? splitCards : playerCards;
    }

    private int getActiveHandX() {
        if (!isSplit) return CARD_X_START;
        return activeHand == 0 ? SPLIT_HAND1_X : SPLIT_HAND2_X;
    }

    private void updatePlayerTotal() {
        if (isSplit) {
            int h1 = handValue(playerCards);
            int h2 = handValue(splitCards);
            String h1Str = hand1Bust ? "<col=ff0000>BUST" : (activeHand == 0 ? "<col=ffff00>" + h1 : "" + h1);
            String h2Str = hand2Bust ? "<col=ff0000>BUST" : (activeHand == 1 ? "<col=ffff00>" + h2 : "" + h2);
            if (hand1Done && !hand1Bust) h1Str = "<col=ffffff>" + h1;
            String arrow1 = activeHand == 0 && !hand1Done ? "<col=ffff00>>> " : "";
            String arrow2 = activeHand == 1 ? "<col=ffff00>>> " : "";
            player.getPA().sendString(60984, arrow1 + "H1: " + h1Str + "  <col=ffffff>|  " + arrow2 + "H2: " + h2Str);
            player.getPA().sendString(60986, activeHand == 0 ? "<col=ffff00>Playing Hand 1" : "<col=ffff00>Playing Hand 2");
            player.getPA().sendString(60987, "");
        } else {
            player.getPA().sendString(60984, String.valueOf(handValue(playerCards)));
        }
    }

    // ==================== CARD MOVEMENT ====================

    private void moveCards(List<Card> hand, int baseX, int y) {
        int count = hand.size();
        int spacing;
        int startX;
        if (count <= 2) {
            spacing = isSplit ? 40 : CARD_SPACING;
            startX = baseX;
        } else {
            int availableWidth = isSplit ? 150 : 240;
            spacing = Math.max(25, availableWidth / count);
            startX = isSplit ? baseX : 130;
        }

        for (int i = 0; i < count; i++) {
            Card card = hand.get(i);
            int endX = startX + (spacing * i);
            if (!card.sent) {
                card.sent = true;
                player.getPA().runClientScript(35, 60950, card.getWidgetId(), DECK_X, DECK_Y, endX, y, 20, false);
            } else {
                player.getPA().runClientScript(35, 60950, card.getWidgetId(), -10, -10, endX, y, 10, false);
            }
        }
    }

    private void sendCards(List<Card> hand) {
        for (Card c : hand) {
            player.getPA().runClientScript(13_030, c.getWidgetId(), c.getSpriteId());
        }
        player.getPA().sendSound(2739, SoundType.SOUND); // card flip
    }

    private void sendDealerCards() {
        sendCards(dealerCards);
        player.getPA().sendString(60983, String.valueOf(handValue(dealerCards)));
    }

    // ==================== PLAYER ACTIONS ====================

    public void hit() {
        if (state != State.PLAYING || playerTurnOver || animating) return;
        if (isSplit && splitAces) {
            player.sendErrorMessage("You can't hit after splitting aces.");
            return;
        }
        final int thisGame = gameId;
        final List<Card> hand = getActiveCards();

        animating = true;
        hand.add(deck.dealCard());
        moveCards(hand, getActiveHandX(), PLAYER_Y);
        player.lock(new CompleteLock());

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int cycle = 0;
            @Override
            public void execute(CycleEventContainer container) {
                if (thisGame != gameId) { container.stop(); return; }
                if (cycle == 2) {
                    sendCards(hand);
                    player.unlock();
                    animating = false;
                    int total = handValue(hand);
                    updatePlayerTotal();

                    if (total > 21) {
                        handleHandBust();
                    } else if (total == 21) {
                        handleHandStand();
                    }
                    container.stop();
                }
                cycle++;
            }
        }, 1);
    }

    public void stand() {
        if (state != State.PLAYING || playerTurnOver || animating) return;
        handleHandStand();
    }

    public void doubleDown() {
        if (state != State.PLAYING || playerTurnOver || animating) return;
        final List<Card> hand = getActiveCards();
        if (hand.size() != 2) {
            player.sendErrorMessage("You can only double down on your first two cards.");
            return;
        }
        long currentBet = (isSplit && activeHand == 1) ? splitBet : betAmount;
        if (player.getItems().getInventoryCount(995) < currentBet) {
            player.sendErrorMessage("You don't have enough funds to double down.");
            return;
        }
        final int thisGame = gameId;

        player.getItems().deleteItem2(995, (int) currentBet);
        if (isSplit && activeHand == 1) {
            splitBet *= 2;
        } else {
            betAmount *= 2;
        }
        sendBalance();

        animating = true;
        hand.add(deck.dealCard());
        moveCards(hand, getActiveHandX(), PLAYER_Y);
        player.lock(new CompleteLock());

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int cycle = 0;
            @Override
            public void execute(CycleEventContainer container) {
                if (thisGame != gameId) { container.stop(); return; }
                if (cycle == 2) {
                    sendCards(hand);
                    player.unlock();
                    animating = false;
                    int total = handValue(hand);
                    updatePlayerTotal();

                    if (total > 21) {
                        handleHandBust();
                    } else {
                        handleHandStand();
                    }
                    container.stop();
                }
                cycle++;
            }
        }, 1);
    }

    public void split() {
        if (state != State.PLAYING || playerTurnOver || animating) return;
        if (isSplit) {
            player.sendErrorMessage("You can only split once.");
            return;
        }
        if (playerCards.size() != 2) {
            player.sendErrorMessage("You can only split on your first two cards.");
            return;
        }
        if (playerCards.get(0).getRank().getValue() != playerCards.get(1).getRank().getValue()) {
            player.sendErrorMessage("You can only split cards of the same value.");
            return;
        }
        if (player.getItems().getInventoryCount(995) < betAmount) {
            player.sendErrorMessage("You don't have enough funds to split.");
            return;
        }
        final int thisGame = gameId;

        player.getItems().deleteItem2(995, (int) betAmount);
        splitBet = betAmount;
        sendBalance();

        splitAces = playerCards.get(0).getRank() == Rank.ACE;
        isSplit = true;
        activeHand = 0;

        // Move second card to split hand
        Card splitCard = playerCards.remove(1);
        splitCards.add(splitCard);

        // Deal one new card to each hand
        playerCards.add(deck.dealCard());
        splitCards.add(deck.dealCard());

        // Re-animate all cards to new split positions
        animating = true;
        for (Card c : playerCards) c.sent = false;
        for (Card c : splitCards) c.sent = false;
        moveCards(playerCards, SPLIT_HAND1_X, PLAYER_Y);
        moveCards(splitCards, SPLIT_HAND2_X, PLAYER_Y);

        player.lock(new CompleteLock());

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int cycle = 0;
            @Override
            public void execute(CycleEventContainer container) {
                if (thisGame != gameId) { container.stop(); return; }
                if (cycle == 2) {
                    sendCards(playerCards);
                    sendCards(splitCards);
                    player.unlock();
                    animating = false;
                    updatePlayerTotal();

                    if (splitAces) {
                        playerTurnOver = true;
                        revealDealerAndFinish();
                    }
                    container.stop();
                }
                cycle++;
            }
        }, 1);
    }

    // ==================== HAND COMPLETION ====================

    private void handleHandBust() {
        if (isSplit) {
            if (activeHand == 0) {
                hand1Bust = true;
                hand1Done = true;
                activeHand = 1;
                updatePlayerTotal();
                if (splitAces) {
                    // Both hands auto-played, check hand 2
                    playerTurnOver = true;
                    revealDealerAndFinish();
                }
            } else {
                hand2Bust = true;
                if (hand1Bust) {
                    // Both hands bust
                    announceSplitLoss();
                } else {
                    playerTurnOver = true;
                    revealDealerAndFinish();
                }
            }
        } else {
            sendDealerCards();
            int playerTotal = handValue(playerCards);
            int dealerTotal = handValue(dealerCards);
            showResult("lose", playerTotal, dealerTotal);
            announceLoss(true);
        }
    }

    private void handleHandStand() {
        if (isSplit && activeHand == 0) {
            hand1Done = true;
            activeHand = 1;
            updatePlayerTotal();
        } else {
            playerTurnOver = true;
            revealDealerAndFinish();
        }
    }

    // ==================== DEALER TURN ====================

    private void revealDealerAndFinish() {
        if (isSplit && hand1Bust && hand2Bust) {
            announceSplitLoss();
            return;
        }

        sendDealerCards();
        final int thisGame = gameId;

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int step = 0;
            @Override
            public void execute(CycleEventContainer container) {
                if (thisGame != gameId) { container.stop(); return; }
                step++;
                if (step % 2 != 0) return;

                int dealerTotal = handValue(dealerCards);
                if (dealerTotal < 17) {
                    dealerCards.add(deck.dealCard());
                    moveCards(dealerCards, CARD_X_START, DEALER_Y);
                    sendDealerCards();
                } else {
                    if (isSplit) {
                        determineSplitWinner();
                    } else {
                        determineWinner();
                    }
                    container.stop();
                }
            }
        }, 1);
    }

    // ==================== WIN/LOSS (NORMAL) ====================

    private void determineWinner() {
        int playerTotal = handValue(playerCards);
        int dealerTotal = handValue(dealerCards);

        if (dealerTotal > 21) {
            showResult("win", playerTotal, dealerTotal);
            announceWin(betAmount * 2);
        } else if (playerTotal > dealerTotal) {
            showResult("win", playerTotal, dealerTotal);
            announceWin(betAmount * 2);
        } else if (playerTotal < dealerTotal) {
            showResult("lose", playerTotal, dealerTotal);
            announceLoss(false);
        } else {
            showResult("push", playerTotal, dealerTotal);
            announcePush();
        }
    }

    private void showResult(String result, int playerTotal, int dealerTotal) {
        switch (result) {
            case "win":
                player.getPA().sendString(60984, "<col=00ff00>" + playerTotal);
                player.getPA().sendString(60983, "<col=ff0000>" + dealerTotal);
                player.getPA().sendString(60986, "<col=00ff00>YOU WIN!");
                player.getPA().sendString(60987, "");
                break;
            case "lose":
                player.getPA().sendString(60984, "<col=ff0000>" + playerTotal);
                player.getPA().sendString(60983, "<col=00ff00>" + dealerTotal);
                player.getPA().sendString(60986, "<col=ff0000>DEALER WINS!");
                player.getPA().sendString(60987, "");
                break;
            case "push":
                player.getPA().sendString(60984, "<col=ffff00>" + playerTotal);
                player.getPA().sendString(60983, "<col=ffff00>" + dealerTotal);
                player.getPA().sendString(60986, "<col=ffff00>PUSH - BET RETURNED");
                player.getPA().sendString(60987, "");
                break;
        }
    }

    private void announceWin(long payout) {
        player.BjPay += (int)(payout - betAmount);
        player.BjWins += 1;
        player.getItems().addItemUnderAnyCircumstance(995, (int) payout);
        player.getPA().sendSound(3929, SoundType.SOUND); // win fanfare
        PlayerHandler.executeGlobalMessage("@cya@" + player.getDisplayName() + " won " + Misc.formatCoins((int)(payout - betAmount)) + " at Blackjack!");
        betAmount = 0;
        sendBalance();
        endGame();
    }

    private void announceLoss(boolean busted) {
        player.BjPay -= (int) betAmount;
        player.BjLoss += 1;
        player.getPA().sendSound(2304, SoundType.SOUND); // loss
        if (busted) {
            PlayerHandler.executeGlobalMessage("@red@" + player.getDisplayName() + " busted at Blackjack losing " + Misc.formatCoins((int) betAmount) + "!");
        } else {
            PlayerHandler.executeGlobalMessage("@red@" + player.getDisplayName() + " lost " + Misc.formatCoins((int) betAmount) + " at Blackjack!");
        }
        betAmount = 0;
        endGame();
    }

    private void announcePush() {
        player.getItems().addItemUnderAnyCircumstance(995, (int) betAmount);
        player.getPA().sendSound(2277, SoundType.SOUND); // push/neutral
        player.sendErrorMessage("[BJ] Push! Your bet has been returned.");
        betAmount = 0;
        sendBalance();
        endGame();
    }

    // ==================== WIN/LOSS (SPLIT) ====================

    private void determineSplitWinner() {
        int dealerTotal = handValue(dealerCards);
        long totalPayout = 0;

        // Evaluate hand 1
        if (!hand1Bust) {
            int h1 = handValue(playerCards);
            if (dealerTotal > 21 || h1 > dealerTotal) {
                totalPayout += betAmount * 2;
            } else if (h1 == dealerTotal) {
                totalPayout += betAmount; // push = return bet
            }
        }

        // Evaluate hand 2
        if (!hand2Bust) {
            int h2 = handValue(splitCards);
            if (dealerTotal > 21 || h2 > dealerTotal) {
                totalPayout += splitBet * 2;
            } else if (h2 == dealerTotal) {
                totalPayout += splitBet; // push = return bet
            }
        }

        long totalBet = betAmount + splitBet;
        long profit = totalPayout - totalBet;

        if (totalPayout > 0) {
            player.getItems().addItemUnderAnyCircumstance(995, (int) totalPayout);
        }

        player.BjPay += (int) profit;

        String resultType;
        if (profit > 0) {
            player.BjWins++;
            PlayerHandler.executeGlobalMessage("@cya@" + player.getDisplayName() + " won " + Misc.formatCoins((int) profit) + " across 2 hands at Blackjack!");
            resultType = "win";
        } else if (profit < 0) {
            player.BjLoss++;
            PlayerHandler.executeGlobalMessage("@red@" + player.getDisplayName() + " lost " + Misc.formatCoins((int) Math.abs(profit)) + " across 2 hands at Blackjack!");
            resultType = "lose";
        } else {
            resultType = "push";
        }

        showSplitResult(resultType, dealerTotal);
        betAmount = 0;
        splitBet = 0;
        sendBalance();
        endGame();
    }

    private void announceSplitLoss() {
        long totalLoss = betAmount + splitBet;
        player.BjPay -= (int) totalLoss;
        player.BjLoss++;
        PlayerHandler.executeGlobalMessage("@red@" + player.getDisplayName() + " busted both hands at Blackjack losing " + Misc.formatCoins((int) totalLoss) + "!");
        showSplitResult("lose", handValue(dealerCards));
        betAmount = 0;
        splitBet = 0;
        endGame();
    }

    private void showSplitResult(String result, int dealerTotal) {
        int h1 = handValue(playerCards);
        int h2 = handValue(splitCards);
        String h1Str = hand1Bust ? "BUST" : String.valueOf(h1);
        String h2Str = hand2Bust ? "BUST" : String.valueOf(h2);

        switch (result) {
            case "win":
                player.getPA().sendString(60983, "<col=ff0000>" + dealerTotal);
                player.getPA().sendString(60984, "<col=00ff00>H1: " + h1Str + " | H2: " + h2Str);
                player.getPA().sendString(60986, "<col=00ff00>YOU WIN!");
                break;
            case "lose":
                player.getPA().sendString(60983, "<col=00ff00>" + dealerTotal);
                player.getPA().sendString(60984, "<col=ff0000>H1: " + h1Str + " | H2: " + h2Str);
                player.getPA().sendString(60986, "<col=ff0000>DEALER WINS!");
                break;
            case "push":
                player.getPA().sendString(60983, "<col=ffff00>" + dealerTotal);
                player.getPA().sendString(60984, "<col=ffff00>H1: " + h1Str + " | H2: " + h2Str);
                player.getPA().sendString(60986, "<col=ffff00>PUSH - BETS RETURNED");
                break;
        }
        player.getPA().sendString(60987, "");
    }

    // ==================== END GAME ====================

    private void endGame() {
        state = State.ENDING;
        final int thisGame = gameId;

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int cycle = 0;
            @Override
            public void execute(CycleEventContainer container) {
                if (thisGame != gameId) { container.stop(); return; }
                if (cycle == 8) {
                    resetBoard();
                    container.stop();
                }
                cycle++;
            }
        }, 1);
    }

    // ==================== UTILS ====================

    public void sendBalance() {
        String bal = " " + Misc.getPriceFormat(player.getItems().getInventoryCount(995));
        player.getPA().sendString(60982, bal);
        player.getPA().sendString(61502, bal + " GP");
    }

    private int handValue(List<Card> hand) {
        int total = 0;
        int numAces = 0;

        for (Card card : hand) {
            if (card.getRank() == Rank.ACE) {
                numAces++;
            } else {
                total += card.getRank().getValue();
            }
        }

        for (int i = 0; i < numAces; i++) {
            total += (total + 11 <= 21) ? 11 : 1;
        }

        return total;
    }
}
