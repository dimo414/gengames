package gengames.deck;

import java.util.Collections;

/** This class is a unit test which runs all the public functions of Deck in order to test them.  It should not need to be run by third party developers
 * but is here if they want it.  There should never be any uncaught exceptions or print outs to stderr when running this class.
*/
public class DeckTest {
	/**
	 * The DeckTest main method.
	 * @param args the input from the JVM
	 */
	public static void main(String[] args) {
		Deck deck = new Deck();
		Cards hand = new Cards();
		System.out.println("****** Dealing a hand.");
		for (int i = 0; i < 13; i++) {
			Card card = deck.deal();
			hand.add(card);
			// System.out.println(card);
		}

		System.out
				.println("****** Trying to sort the delt cards.  If the following output is not sorted by suit then value, sort failed.");
		Collections.sort(hand);
		for (int i = 0; i < hand.size(); i++) {
			System.out.println(hand.get(i));
		}

		hand = new Cards();

		System.out.println("****** Dealing another hand.");
		for (int i = 0; i < 13; i++) {
			Card card = deck.deal();
			hand.add(card);
			// System.out.println(card);
		}

		System.out
				.println("****** Trying to sort the delt cards.  If the following output is not sorted by value then suit, sort failed.");
		Collections.sort(hand, new ValueComparator());
		for (int i = 0; i < hand.size(); i++) {
			System.out.println(hand.get(i));
		}

		System.out.println("****** Trying to shuffle.");

		deck.shuffle();

		hand = new Cards();

		while (deck.hasCards()) {
			Card card = deck.deal();
			hand.add(card);
			// System.out.println(card);
		}
		System.out.println("****** Dealt the whole deck of " + hand.size()
				+ " cards.");

		System.out.println("****** Trying to deal a 53rd card.");
		try {
			deck.deal();
			System.err
					.println("Succeded in dealing a 53rd card!  That's not good...");
		} catch (RuntimeException e) {
			System.out.println("****** " + e.getMessage());
		}
	}
}
