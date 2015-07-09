package gengames;

/**
 * Used by GAFrame and GAController to define what type of GA to run.  These map to the items in the 'Run Types' drop down.
 * @author Michael Diamond
 * @author Blake Lavender
 */
public enum RunLevel {
    /**
     * Runs the GA with the DummyGameController.  This is just a test of the GA.
     */
    TEST_GA,
    /**
     * Runs one game with dummy players.
     */
    TEST_GAME,
    /**
     * Runs one game with real players.
     */
    TEST_PLAYER,
    /**
     * Runs the GA with dummy players.
     */
    TEST_SYSTEM,
    /**
     * Runs the GA with real players.
     */
    FULL;
}
