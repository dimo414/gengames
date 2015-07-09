package gengames.hearts.traits;

import gengames.GAFrame;
import gengames.Trait;

/**
 * Indicates the player should try to keep the queen of spades if he gets it, or should try to pass it.
 * @author Michael Diamond
 * @author Blake Lavender
 */
public enum PassingQueen implements Trait {
    /** Attempt to pass Q&spades; */
    LOSEQUEEN,
    /** Hold onto the queen */
    KEEPQUEEN;

    /**
     * @see gengames.Trait#mutate()
     */
    public Trait mutate() {
        PassingQueen choose = (PassingQueen) pickOne();
        if (choose == this)
            return mutate();
        return choose;
    }

    /**
     * @see gengames.Trait#pickOne()
     */
    public Trait pickOne() {
        PassingQueen[] arr = values();
        int index = arr.length;
        return arr[GAFrame.rnd.nextInt(index)];
    }
}
