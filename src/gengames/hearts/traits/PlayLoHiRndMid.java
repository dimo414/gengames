package gengames.hearts.traits;

import gengames.GAFrame;
import gengames.Trait;

/**
 * A trait describing simple choices a player could make.  Used primarily for testing.
 * @author Michael Diamond
 * @author Blake Lavender
 */
public enum PlayLoHiRndMid implements Trait {
	/** Play the lowest possible card */
	LOW,
	/** Play the highest possible card */
	HIGH,
	/** Play a random possible card */
	RANDOM,
	/** Play the middle valued possible card */
	MIDDLE;

	/**
	 * @see gengames.Trait#mutate()
	 */
	public Trait mutate() {
		PlayLoHiRndMid choose = (PlayLoHiRndMid) pickOne();
		if (choose == this)
			return mutate();
		return choose;
	}

	/**
	 * @see gengames.Trait#pickOne()
	 */
	public Trait pickOne() {
		PlayLoHiRndMid[] arr = values();
		int index = arr.length;
		return arr[GAFrame.rnd.nextInt(index)];
	}
}
