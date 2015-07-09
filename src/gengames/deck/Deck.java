package gengames.deck;

import java.util.Collections;

import gengames.GAFrame;

/**
 * A deck class, useful as a tool for GameControllers implementing games that use a standard deck of cards.
 * @author Michael Diamond
 * @author Blake Lavender
 */
public class Deck {
    private Cards deck;
    private int nextCard;

    /**
     * Constructs a new deck of cards with a random inital order, seeded by GAFrame.seedRnd, rather than GAFrame.rnd.
     */
    public Deck() {
        nextCard = 0;
        deck = new Cards();
        /*
         * Creating the initial positions of Deck can use a different random
         * seed intentionally, in the hope that initializing the deck in
         * positions not defined by the same seed will improve randomness. This
         * could only possibly be true if rnd and deckRnd are using different
         * generators, not just differently seeded instances of Random. See
         * initialization of both variables in GAFrame for more info.
         */
        int suitStart = (int) (GAFrame.seedRnd.nextDouble() * 4);
        int valueStart = (int) (GAFrame.seedRnd.nextDouble() * 13);
        Suit[] suits = Suit.values();
        Value[] values = Value.values();
        for (int i = 0; i < suits.length; i++)
            for (int j = 0; j < values.length; j++)
                deck.add(new Card(suits[(suitStart++) % suits.length],
                        values[(valueStart++) % values.length]));
        shuffle();
    }

    /**
     * Shuffles the deck, creating a new random ordering and starting back at the top of the deck.
     */
    public void shuffle() {
        nextCard = 0;
        Collections.shuffle(deck, GAFrame.rnd);
        /*
        implementation of http://en.wikipedia.org/wiki/Fisher-Yates_shuffle
        but collections already has it, so why bother?
        int n = deck.size(); // The number of items left to shuffle (loop invariant).
        while (n > 1) {
          int k = GAFrame.rnd.nextInt(n); // 0 <= k < n.
          n--; // n is now the last pertinent index;
          Card temp = deck.get(n); // swap array[n] with array[k] (does nothing if k == n).
          deck.set(n,deck.get(k));
          deck.set(k,temp);
        }*/
    }

    /**
     * Retrieves the top card in the deck and deals it.
     * @return the next Card.
     * @throws RuntimeException if the deck is empty.
     */
    public Card deal() {
        if (hasCards()) {
            return deck.get(nextCard++);
        }
        throw new RuntimeException("Deck is empty - nothing to deal.");
    }

    /**
     * Checks to see if the deck has more cards to deal.
     * @return true if the deck conains more cards, false if the whole deck has been dealt.
     */
    public boolean hasCards() {
        return nextCard < deck.size();
    }
}
