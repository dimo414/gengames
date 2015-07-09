package gengames;

/**
 * <p>This is the parent class of all players to be implemented by developers.  A player plays a game (managed by a GameController)
 * by following a strategy.  This abstract class has no predefined game specific methods, developers are expected to implement their
 * own to be compatible with their GameController.  The player's strategy is represented by an array of Traits.</p>
 * 
 * <p>The player should not have any public constructors, instead constructors should be package accessible
 * (that is to say no access flag before the constructor) so that only the {@link gengames.PlayerBuilder PlayerBuilder} class can construct new Players.</p>
 * 
 * @author Michael Diamond
 * @author Blake Lavender
 */
public abstract class Player implements Comparable<Player> {
    private long totalScore;
    /**
     * The array where a player's strategy is stored.
     */
    protected Trait[] strategy;
    /**
     * Getter method for the set of Traits describing this player.
     * @return the Player's Traits.
     */
    public final Trait[] getStrategy()
    {
        return strategy;
    }

    /**
     * The expected size of the Trait array.
     * @return the size of the array this player expects.
     */
    public abstract int traitSize();

    /**
     * Increases the player's fitness by <tt>score</tt>.  This is used by the GAController to determine the fitness of a player.  Since there
     * is no way to decrease a player's score, it is suggested that implementations of GameController store the players' current scores elsewhere
     * and write them to the player at the end of the game.
     * @param score the player's score.
     */
    public final void addToFitness(int score)
    {
        totalScore += score;
    }

    /**
     * Return's the player's fitness.  It is used by GAController.
     * @return the player's current fitness.
     */
    public final long fitness()
    {
        return totalScore;
    }

    /**
     * Resets the player's fitness to zero.  It is used by GAController.
     */
    public final void clearFitness()
    {
        totalScore = 0;
    }

    /**
     * Selects a Trait at random from the Player's strategy and mutates it.
     */
    public final void mutate() {
        int index = GAFrame.rnd.nextInt(strategy.length);
        strategy[index] = strategy[index].mutate();
    }

    /** This should compare two player's fitness.  For games where low score is best, return <tt>fitness() - other.fitness()</tt>.  For
     * games where high scores are best, return <tt>other.fitness() - fitness()</tt>.
     * @param other another player to compare against.
     * @return an integer representing which player is better.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public abstract int compareTo(Player other);

    /**
     * A detailed description of the player suitable for logging.
     * @return a description of the player.
     */
    public abstract String traitDescription();
    
    /**
     * A shorter description of the player suitable for display in the GAFrame.
     * @see java.lang.Object#toString()
     */
    public abstract String toString();
}
