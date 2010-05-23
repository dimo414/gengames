package gengames.hearts;

import gengames.Player;
import gengames.PlayerBuilder;
import gengames.Trait;
import gengames.hearts.traits.PlayLoHiRndMid;

/**
 * Constructs a {@link gengames.hearts.DummyHeartsPlayer DummyHeartsPlayer}
 * @author Michael Diamond
 * @author Blake Lavender
 * @see gengames.PlayerBuilder PlayerBuilder
 */
public class DummyHeartsPlayerBuilder extends PlayerBuilder {

	/**
	 * @see gengames.PlayerBuilder#genPlayer()
	 */
	@Override
	public Player genPlayer() {
		Trait[] arr = new Trait[1];
		arr[0] = PlayLoHiRndMid.HIGH.pickOne();

		return new DummyHeartsPlayer(arr);
	}

	/**
	 * @see gengames.PlayerBuilder#genPlayer(gengames.Trait[])
	 */
	@Override
	public Player genPlayer(Trait[] str) {
		return new DummyHeartsPlayer(str);
	}

}
