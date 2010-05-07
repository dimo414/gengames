package gengames.hearts.traits;

import gengames.GAFrame;
import gengames.Trait;

public enum NormalTrait implements Trait {
	HIGHEST, LOWEST, SMALLESTSUITHIGH, LARGESTSUITHIGH, SMALLESTSUITLOW, LARGESTSUITLOW, HIGHHEARTS, HIGHSPADES, LOWHEARTS, LOWSPADES, RANDOM;

	public Trait mutate() {
		NormalTrait choose = (NormalTrait) pickOne();
		if (choose == this)
			return mutate();
		return choose;
	}

	public Trait pickOne() {
		NormalTrait[] arr = values();
		int index = arr.length;
		return arr[GAFrame.rnd.nextInt(index)];
	}

}
