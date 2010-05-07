package gengames;

/**
 *<p>This is the primary class to be extended by third party developers looking to create their own game.  Developers will need to implement
 * extensions to the following classes in addition to GameController: {@link Player}, {@link PlayerBuilder}, and {@link Trait}.</p>
 * 
 * <h2>Requirements for Proper Extension of GameController</h2>
 * <h3>Constructors</h3>
 * <p>Genetic Games expects two constructors from the GameController, a constructor which takes no parameters, and does nothing (or as little as possible)
 * this is used to load an instance to retrieve information like the Player Builders and the number of players per game.</p>
 * 
 * <p>The second constructor is the 'real' constructor, and should start a game between the players passed.  The signature of this constructor is:
 * (Player[], JTextArea, boolean) - the array of players, which will be the same size as the return value of numPlayers(); the JTextArea to 
 * write game output to; and the boolean indicates the game should start off running or not.</p>
 * 
 * <p>The GameController is expected to run its game asynchronously in a separate thread, therefore the second constructor should do little more than
 * validate the players and start a new thread, where the game itself runs.</p>
 * 
 * <h2>Updating The Players</h2>
 * <p>It is the responsibility of the GameController to call each player's addToFitness() method to record their success that round.  The developer may decide
 * by what metric the GameController assigns scores, and high scores may be either positive (for instance, Cribbage) or negative (like Hearts).
 * The Player's compareTo method is used to define whether lower or higher scores are better.</p>
 * 
 * <h2>Controlling The Game</h2>
 * <p>Genetic Games allows the user to control how quickly games and generations proceed by being able to run either continuously, or one generation,
 * game, or even round at a time.  The GAController handles most of this management, however classes implementing GameController are expected
 * to be able to pause and resume, and to run one round* at a time.  The suggested way of implementing this is to instruct the current thread to
 * sleep while setRunning() has been set to false and setRunRound() has not been set to true.  In other words something like:</p>
<pre>while (!gameOver()) {
  while (!(running || runRound))
    Thread.sleep(50);
  playRound();
  runRound = false;
}</pre>
 * 
 * <p>*As each game may have different ranges of time for which it would be desirable to pause between, what constitutes a round is left undefined.
 * The developer may decide what amount of play constitutes a round when developing their extension of GameController.</p>
 * 
 * @author Michael Diamond
 * @author Blake Lavender
 */
public abstract class GameController implements Runnable {
	
	/**
	 * This method should contain the gameplay, including pausing mechanisms if the game is not supposed to be running.
	 * @see java.lang.Runnable#run()
	 */
	public abstract void run();
	
	/**
	 * Call to interrupt the game, the game is expected to then stop execution and end the game thread immediately.  If the game is already
	 * over (as defined by gameOver()) it is acceptable for this method to do nothing and just return, as the game thread should have already
	 * ended.
	 */
	public abstract void interrupt();

	/**
	 * Indicates the game was interrupted and therefore should not be expected to ever end.
	 * @return true if interrupt() is called, false otherwise.
	 */
	public abstract boolean interrupted();

	/**
	 * Indicates the game is over, players fitness has been updated, and the game thread has terminated.
	 * @return true if the game is over, false otherwise.
	 */
	public abstract boolean gameOver();

	/**
	 * Instructs the GameController to run one round.
	 */
	public abstract void runRound();

	/**
	 * Instructs the game controller to run the game.
	 * @param run if true, game runs until instructed to pause, interrupted, or game over.  If false, instructs a running game to pause.
	 */
	public abstract void setRunning(boolean run);

	/**
	 * Returns the PlayerBuilder used to create proper players for this GameController.  It is recommended, though not required, that
	 * the GameController store one PlayerBuilder statically, and return the same one for all to getPlayerBuilder().
	 * @return a PlayerBuilder to generate new players.
	 */
	public abstract PlayerBuilder getPlayerBuilder();

	/**
	 * Returns the DummyPlayerBuilder used to create dummy players for this GameController.  It is recommended, though not required, that
	 * the GameController store one DummyPlayerBuilder statically, and return the same one for all to getDummyPlayerBuilder().
	 * @return a DummyPlayerBuilder to generate new players.
	 */
	public abstract PlayerBuilder getDummyPlayerBuilder();

	/**
	 * Indicates the number of players per game.
	 * @return the number of players to delegate to each game.
	 */
	public abstract int numPlayers();
}
