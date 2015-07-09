package gengames.hearts.traits;

import gengames.GAFrame;
import gengames.Trait;

/**
 * The primary trait used by {@link gengames.hearts.HeartsPlayer HeartsPlayer} to make choices.
 * An instance of this trait is used to define the player's decision at each identifiable phase of play.
 * @author Michael Diamond
 * @author Blake Lavender
 */
public enum NormalTrait implements Trait {
    /** Play highest possible card */
    HIGHEST,
    /** Play lowest possible card */
    LOWEST,
    /** Play the highest possible card of the suit with the fewest cards */
    SMALLESTSUITHIGH,
    /** Play the highest possible card of the suit with the most cards */
    LARGESTSUITHIGH,
    /** Play the lowest possible card of the suit with the fewest cards */
    SMALLESTSUITLOW,
    /** Play the lowest possible card of the suit with the most cards */
    LARGESTSUITLOW,
    /** Play the highest possible heart */
    HIGHHEARTS,
    /** Play the highest possible spade */
    HIGHSPADES,
    /** Play the lowest possible heart */
    LOWHEARTS,
    /** Play the lowest possible spade */
    LOWSPADES,
    /** Play a random possible card */
    RANDOM;

    /**
     * @see gengames.Trait#mutate()
     */
    @Override
    public Trait mutate() {
        NormalTrait choose = (NormalTrait) pickOne();
        if (choose == this)
            return mutate();
        return choose;
    }

    /**
     * @see gengames.Trait#pickOne()
     */
    @Override
    public Trait pickOne() {
        NormalTrait[] arr = values();
        int index = arr.length;
        return arr[GAFrame.rnd.nextInt(index)];
    }

}
