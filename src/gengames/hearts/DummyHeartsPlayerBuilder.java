package gengames.hearts;

import gengames.Player;
import gengames.PlayerBuilder;
import gengames.Trait;
import gengames.hearts.traits.PlayLoHiRndMid;

public class DummyHeartsPlayerBuilder extends PlayerBuilder {

	@Override
	public Player genPlayer() {
		Trait[] arr = new Trait[1];
		arr[0] = PlayLoHiRndMid.HIGH.pickOne();

		return new DummyHeartsPlayer(arr);
	}

	@Override
	public Player genPlayer(Trait[] str) {
		return new DummyHeartsPlayer(str);
	}

}
