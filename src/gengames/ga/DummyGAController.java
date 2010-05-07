package gengames.ga;

import java.lang.reflect.Constructor;

import gengames.GameController;
import gengames.Player;
import gengames.PlayerBuilder;
import gengames.RunLevel;

import javax.swing.JTextArea;

// This is a dummy class for a simple GAController which will be easy to implement and use during testing
// It should extend and nullify the effects of EVERY method of GAController

/**
 * A Dummy Genetic Algorithm controller which runs one game and then terminates.  Useful for testing games and players without needing to run the GA system.
 * @author Michael Diamond
 * @author Blake Lavender
 */
public class DummyGAController extends GAController {

	/**
	 * Constructor, same as GAController
	 * @param gcc Game Controller class
	 * @param gameOutput Game Controller output
	 * @param gaOutput GA Controller output
	 * @param rl Run Level, only accepts TEST_PLAYER and TEST_GAME
	 */
	public DummyGAController(Class<? extends GameController> gcc,
			JTextArea gameOutput, JTextArea gaOutput, RunLevel rl) {
		super(gcc, gameOutput, gaOutput, rl);
		PlayerBuilder pb;
		switch (rl) {
		case TEST_PLAYER:
			pb = playerBuilder;
			break;
		case TEST_GAME:
			pb = dummyBuilder;
			break;
		default:
			throw new RuntimeException(
					"Error: DummyGAController should not handle the " + rl
							+ " run level.");
		}

		gameOutput.setText("");
		Player[] players = new Player[gameSize];
		for (int i = 0; i < gameSize; i++)
			players[i] = pb.genPlayer();
		Object[] arr = new Object[3];
		arr[0] = players;
		arr[1] = gameOutput;
		arr[2] = running || runGen || runGame;
		Constructor<?>[] constructs = gameClass.getConstructors();
		Exception exc = null;
		for (Constructor<?> con : constructs) {
			try {
				game = (GameController) con.newInstance(arr);
			} catch (Exception e) {
				exc = e;
			}
		}
		if (game == null) {
			exc.printStackTrace();
			throw new RuntimeException("No acceptable constructor found."); // FIXME
		}

		gaOutput.setText("Starting Game Between Real Players:\n");
		for (Player p : players)
			gaOutput.append(p + "\n");
	}

	/**
	 * Indicates the GAController is in a stable state and can be safely interrupted.
	 * @see gengames.ga.GAController#safeToInterrupt()
	 */
	@Override
	public boolean safeToInterrupt() {
		return game == null || game.gameOver();
	}

	/**
	 * Interrupts the current game.
	 * @see gengames.ga.GAController#interrupt()
	 */
	@Override
	public void interrupt() {
		if (game != null)
			game.interrupt();
	}
}
