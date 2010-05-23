package gengames.hearts;

import java.util.Collections;

import gengames.GAFrame;
import gengames.Trait;
import gengames.deck.Card;
import gengames.deck.Cards;
import gengames.deck.ValueComparator;
import gengames.hearts.traits.*;

/**
 * This is a dummy class for a simple player which will be easy to implement and use during testing
 * It should extend and nullify the effects of EVERY method of Player
 * @author Michael Diamond
 * @author Blake Lavender
 * @see gengames.hearts.HeartsPlayer HeartsPlayer
 */
public class DummyHeartsPlayer extends HeartsPlayer {
	private static final int TRAIT_SIZE = 1;

	/**
	 * @param str creates a DummyHeartsPlayer with the passed strategy
	 */
	public DummyHeartsPlayer(Trait[] str) {
		if (str.length != TRAIT_SIZE)
			throw new RuntimeException("HeartsPlayer expects an array of "
					+ TRAIT_SIZE + " traits.  Received " + str.length + ".");
		strategy = str;
	}

	// Game Methods
	/**
	 * @see gengames.hearts.HeartsPlayer#passTo(int)
	 */
	@Override
	public Cards passTo(int player) {
		Collections.sort(myHand, new ValueComparator());
		Cards passedCards = new Cards();
		if (strategy[0] == PlayLoHiRndMid.LOW) { // get rid of least desirable
													// cards
			passedCards.add(myHand.remove(myHand.size() - 1));
			passedCards.add(myHand.remove(myHand.size() - 1));
			passedCards.add(myHand.remove(myHand.size() - 1));
		} else if (strategy[0] == PlayLoHiRndMid.HIGH) {
			passedCards.add(myHand.remove(0));
			passedCards.add(myHand.remove(0));
			passedCards.add(myHand.remove(0));
		} else if (strategy[0] == PlayLoHiRndMid.RANDOM) {
			passedCards.add(myHand.remove((int) (myHand.size() * GAFrame.rnd
					.nextDouble())));
			passedCards.add(myHand.remove((int) (myHand.size() * GAFrame.rnd
					.nextDouble())));
			passedCards.add(myHand.remove((int) (myHand.size() * GAFrame.rnd
					.nextDouble())));
		} else if (strategy[0] == PlayLoHiRndMid.MIDDLE) {
			passedCards.add(myHand.remove(myHand.size() / 2));
			passedCards.add(myHand.remove(myHand.size() / 2));
			passedCards.add(myHand.remove(myHand.size() / 2));
		}
		return passedCards;
	}

	/**
	 * @see gengames.hearts.HeartsPlayer#nextMove(gengames.deck.Cards, gengames.deck.Cards)
	 */
	// Obviously this will change depending on the strategy (in the complete
	// version)
	@Override
	public Card nextMove(Cards trick, Cards playableCards) {
		// sort playable cards and take the highest one
		Collections.sort(playableCards, new ValueComparator());
		Card play;
		switch ((PlayLoHiRndMid) strategy[0]) {
		case HIGH:
			play = playableCards.get(playableCards.size() - 1);
			break;
		case LOW:
			play = playableCards.get(0);
			break;
		case RANDOM:
			play = playableCards.get(GAFrame.rnd.nextInt(playableCards.size()));
			break;
		case MIDDLE:
			play = playableCards.get(playableCards.size() / 2);
			break;
		default:
			throw new RuntimeException("Player utilized an unexpected trait...");
		}

		myHand.remove(play);
		return play;
	}

	/**
	 * @see gengames.hearts.HeartsPlayer#traitSize()
	 */
	@Override
	public int traitSize() {
		return TRAIT_SIZE;
	}
}