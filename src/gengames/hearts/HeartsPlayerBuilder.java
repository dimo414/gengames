package gengames.hearts;

import gengames.PlayerBuilder;
import gengames.Trait;
import gengames.hearts.traits.NormalTrait;
import gengames.hearts.traits.PassingQueen;

/**
 * This class extends PlayerBuilder, allowing the GAController to construct new HeartsPlayers uniformly.
 * @author Michael Diamond
 * @author Blake Lavender
 * @see gengames.PlayerBuilder PlayerBuilder
 */
public class HeartsPlayerBuilder extends PlayerBuilder {

    /**
     * @see gengames.PlayerBuilder#genPlayer()
     */
    @Override
    public HeartsPlayer genPlayer() {
        Trait[] arr = new Trait[34];
        arr[0] = PassingQueen.KEEPQUEEN.pickOne();
        for (int i = 1; i < arr.length; i++) {
            arr[i] = NormalTrait.HIGHEST.pickOne();
        }

        return new HeartsPlayer(arr);
    }

    /**
     * @see gengames.PlayerBuilder#genPlayer(gengames.Trait[])
     */
    @Override
    public HeartsPlayer genPlayer(Trait[] str) {
        return new HeartsPlayer(str);
    }

}
