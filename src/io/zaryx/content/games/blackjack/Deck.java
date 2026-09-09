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
        int index = bjManager.cardWidgetId++ - 60953;
        if (index >= 64) throw new IllegalStateException("Blackjack card widget limit exceeded");
        card.setWidgetId(index < 16 ? 60953 + index : 61600 + index - 16);
        return card;
    }
}
