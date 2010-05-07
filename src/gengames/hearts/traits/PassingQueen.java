package gengames.hearts.traits;

import gengames.GAFrame;
import gengames.Trait;

public enum PassingQueen implements Trait {
	LOSEQUEEN, KEEPQUEEN;

	public Trait mutate() {
		PassingQueen choose = (PassingQueen) pickOne();
		if (choose == this)
			return mutate();
		return choose;
	}

	public Trait pickOne() {
		PassingQueen[] arr = values();
		int index = arr.length;
		return arr[GAFrame.rnd.nextInt(index)];
	}
}
