package gengames.deck;

/**
 * A card object, has a Suit and a Value.
 * @author Michael Diamond
 * @author Blake Lavender
 */
public class Card implements Comparable<Card> {
    private Suit suit;
    private Value val;

    /**
     * Constructs a new card.
     * @param s the Suit of the card.
     * @param v the Value of the card.
     */
    /* package */Card(Suit s, Value v) {
        suit = s;
        val = v;
    }

    /**
     * Getter method for the card's Suit.
     * @return the card's Suit.
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Getter method for the card's Value.
     * @return the card's Value.
     */
    public Value getValue() {
        return val;
    }

    /**
     * Returns the name of the card
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return val + " OF " + suit;
    }

    /**
     * Compares this card to another card in the default manner, which is suit, then value.
     * To compare cards by value, then by suit, use the ValueComparator.
     * @param that The card to compare with.
     * @return The ordering of the two cards.
     * @see ValueComparator
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Card that) {
        if (!suit.equals(that.suit))
            return suit.compareTo(that.suit);
        return val.compareTo(that.val);
    }
}
