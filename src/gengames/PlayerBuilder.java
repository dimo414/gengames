package gengames;

/**
 * This class allows the GAController to construct new Players uniformly.
 * @author Michael Diamond
 * @author Blake Lavender
 */
public abstract class PlayerBuilder {
    /**
     * Builds a player with a randomly generated set of traits.
     * @return a new player with a random strategy.
     */
    public abstract Player genPlayer();

    /**
     * Builds a player with a defined set of traits.
     * @param str the strategy to construct the player with.
     * @return a new player with the defined strategy.
     */
    public abstract Player genPlayer(Trait[] str);

    /**
     * Returns the trait size for the player being built.
     * @return expected trait size.
     */
    public int traitSize() {
        return genPlayer().traitSize();
    }
}
