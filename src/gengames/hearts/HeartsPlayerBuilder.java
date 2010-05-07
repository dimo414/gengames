package gengames.hearts;

import gengames.PlayerBuilder;
import gengames.Trait;
import gengames.hearts.traits.NormalTrait;
import gengames.hearts.traits.PassingQueen;

public class HeartsPlayerBuilder extends PlayerBuilder {

	@Override
	public HeartsPlayer genPlayer() {
		Trait[] arr = new Trait[34];
		arr[0] = PassingQueen.KEEPQUEEN.pickOne();
		for (int i = 1; i < arr.length; i++) {
			arr[i] = NormalTrait.HIGHEST.pickOne();
		}

		return new HeartsPlayer(arr);
	}

	@Override
	public HeartsPlayer genPlayer(Trait[] str) {
		return new HeartsPlayer(str);
	}

}
