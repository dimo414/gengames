package gengames.hearts.traits;

import gengames.GAFrame;
import gengames.Trait;

public enum PlayLoHiRndMid implements Trait {
	LOW, HIGH, RANDOM, MIDDLE;

	public Trait mutate() {
		PlayLoHiRndMid choose = (PlayLoHiRndMid) pickOne();
		if (choose == this)
			return mutate();
		return choose;
	}

	public Trait pickOne() {
		PlayLoHiRndMid[] arr = values();
		int index = arr.length;
		return arr[GAFrame.rnd.nextInt(index)];
	}
}
