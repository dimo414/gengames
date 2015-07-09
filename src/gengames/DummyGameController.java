package gengames;

import javax.swing.JTextArea;

/**
 * A dummy game controller to test players and ensure the GA works.  It does not need to be extended.
 * 
 * @author Michael Diamond
 * @author Blake Lavender
 */
public class DummyGameController extends GameController {
    private boolean gameOver = false;
    
    /**
     * Empty constructor does nothing.
     */
    public DummyGameController()
    {
        
    }
    
    /**
     * Takes in a set of players and assigns them a score.  This mimics the operational signature of a real GameController.
     * @param players A group of players to 'play' against each other.
     * @param gameOutput The location to output content to.
     * @param running A boolean flag indicating if the game should start off running.
     */
    public DummyGameController(Player[] players, JTextArea gameOutput,
            boolean running) {
        for (int i = 0; i < players.length; i++) {
            players[i].addToFitness(i * 2 + 2);
            gameOutput.setText("Player " + i + " has " + players[i].fitness()
                    + " points.\n");
        }
        gameOver = true;
        return;
    }

    /**
     * @see gengames.GameController#gameOver()
     */
    @Override
    public boolean gameOver() {
        return gameOver;
    }

    /**
     * @see gengames.GameController#getPlayerBuilder()
     */
    @Override
    public PlayerBuilder getPlayerBuilder() {
        throw new RuntimeException(
                "Dummy Game Should Not Need To Generate Players.");
    }

    /**
     * @see gengames.GameController#getDummyPlayerBuilder()
     */
    @Override
    public PlayerBuilder getDummyPlayerBuilder() {
        throw new RuntimeException(
                "Dummy Game Should Not Need To Generate Players.");
    }

    /**)
     * @see gengames.GameController#interrupt()
     */
    @Override
    public void interrupt() {
        // Not threaded. Do nothing.
    }

    /**
     * @see gengames.GameController#interrupted()
     */
    @Override
    public boolean interrupted() {
        return false;
    }

    /**
     * @see gengames.GameController#runRound()
     */
    @Override
    public void runRound() {
        // No rounds. Do nothing.
    }

    /**
     * @see gengames.GameController#setRunning(boolean)
     */
    @Override
    public void setRunning(boolean run) {
        // Finishes immediately. Do nothing.
    }

    /**
     * @see gengames.GameController#run()
     */
    @Override
    public void run() {
        // Finishes immediately. Do nothing.
    }

    /**
     * Returns 4.
     * @see gengames.GameController#numPlayers()
     */
    @Override
    public int numPlayers() {
        return 4;
    }

}
