package gengames.deck;

import java.util.Comparator;

/**
 * A comparator which does the opposite of the Card's default compareTo, and compares cards first by their value, and then by their suit.
 * @author Michael Diamond
 * @author Blake Lavender
 *
 */
public class ValueComparator implements Comparator<Card> {
    /**
     * Compares two cards by their value then by their suit.
     * @param one The first card to compare.
     * @param two The second card to compare.
     * @return An integer representing which is larger.
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Card one, Card two) {
        if (!one.getValue().equals(two.getValue()))
            return one.getValue().compareTo(two.getValue());
        return one.getSuit().compareTo(two.getSuit());
    }

}
