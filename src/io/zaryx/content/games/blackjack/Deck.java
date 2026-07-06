package io.zaryx.content.games.blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;
    private BJManager bjManager;
    private int numDecks;

    public Deck(BJManager bjManager, int numDecks) {
        this.bjManager = bjManager;
        this.numDecks = numDecks;
        cards = new ArrayList<>();
        for (int i = 0; i < numDecks; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    cards.add(new Card(bjManager, rank, suit));
                }
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            for (int i = 0; i < numDecks; i++) {
                for (Suit suit : Suit.values()) {
                    for (Rank rank : Rank.values()) {
                        cards.add(new Card(bjManager, rank, suit));
                    }
                }
            }
            Collections.shuffle(cards);
        }
        Card card = cards.remove(0);
        card.setWidgetId(bjManager.cardWidgetId++);
        return card;
    }
}
