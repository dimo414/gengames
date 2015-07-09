package gengames.ga;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;

import gengames.DummyGameController;
import gengames.GAFrame;
import gengames.GameController;
import gengames.GenGameImplementationException;
import gengames.Player;
import gengames.PlayerBuilder;
import gengames.RunLevel;
import gengames.Trait;

import javax.swing.JTextArea;

/**
 * The Genetic Algorithm Controller, which maintains the Genetic Algorithm, manages players, and runs games.
 * @author Michael Diamond
 * @author Blake Lavender
 */
public class GAController implements Runnable {
    // running variables
    /** Indicates that the GA is running. */
    protected boolean running;
    /** Indicates the GA should run for one generation. */
    protected boolean runGen;
    /** Indicates the GA should run for one game. */
    protected boolean runGame;
    /** Indicates the GA should run for one round. */
    protected boolean runRound;
    private Thread GAThread;

    /** The current game the GAController is managing. */
    protected GameController game;
    /** The PlayerBuilder for normal players. */
    protected PlayerBuilder playerBuilder;
    /** The PlayerBuilder for dummy players. */
    protected PlayerBuilder dummyBuilder;
    /** The number of players a game should have. */
    protected int gameSize;

    /** The GA output area. */
    protected JTextArea output;
    /** The Game output area. */
    protected JTextArea gameOutput;

    // default GA variables
    /** The default population size. */
    protected static final int defaultPopulationSize = 16;
    /** The default mating pool size. */
    protected static final double defaultMatingPoolSize = .6;
    /** The default mutation rate. */
    protected static final double defaultMutationRate = .05;
    /** The default Crossover Type */
    protected static final int defaultCrossoverType = 1;
    /** The default tournament style. */
    protected static final boolean defaultTournametStyle = false;

    // GA variables
    private int populationSize = defaultPopulationSize;
    private double matingPoolSize = defaultMatingPoolSize;
    private double mutationRate = defaultMutationRate;
    private int crossoverType = defaultCrossoverType;
    private boolean tournamentStyle = defaultTournametStyle;

    /** The class of the GameController to run */
    protected Class<? extends GameController> gameClass;

    private ArrayList<Player> thePopulation;

    private int generations = 0;

    /** Boolean flag indicating this GA uses dummy games */
    protected boolean dummyGame;
    /** Boolean flag indicating this GA uses dummy players */
    protected boolean dummyPlayer;

    /**
     * GAController constructor called by GAFRame.
     * @param gcc Class object indicating the GameController to play with
     * @param gameOutput JTextArea to output game data to
     * @param gaOutput JTextArea to output GA data to
     * @param rl The {@link gengames.RunLevel RunLevel} for this GA instance
     */
    public GAController(Class<? extends GameController> gcc,
            JTextArea gameOutput, JTextArea gaOutput, RunLevel rl) {
        gameClass = gcc;
        output = gaOutput;
        this.gameOutput = gameOutput;
        GameController tempGC;
        try {
            tempGC = gameClass.newInstance();
        } catch (InstantiationException e) {
            throw new GenGameImplementationException("Attempted to run an invalid game.");
        } catch (IllegalAccessException e) {
            throw new GenGameImplementationException("Attempted to run an invalid game.");
        }
        playerBuilder = tempGC.getPlayerBuilder();
        dummyBuilder = tempGC.getDummyPlayerBuilder();
        gameSize = tempGC.numPlayers();

        switch (rl) {
        case TEST_GA:
            dummyGame = true;
            dummyPlayer = true;
            break;

        case TEST_GAME:
            // see DummyGAController
            return;

        case TEST_PLAYER:
            // see DummyGAController
            return;

        case TEST_SYSTEM:
            dummyGame = false;
            dummyPlayer = true;
            break;

        case FULL:
            dummyGame = false;
            dummyPlayer = false;
            break;

        default:
            throw new RuntimeException("Noneixistant RunLevel");
        }

        GAThread = new Thread(this);
        GAThread.setDaemon(true);
        GAThread.start();
    }

    // GA methods
    private void initializePopulation(int popSize) {
        if (dummyPlayer) {
            output.setText("Starting a new dummy population with a size of "
                    + populationSize + "\n");

            thePopulation = new ArrayList<Player>(popSize);

            for (int i = 0; i < popSize; i++) {
                thePopulation.add(dummyBuilder.genPlayer());
            }
        } else {
            output.setText("Starting a new population with a size of "
                    + populationSize + "\n");

            thePopulation = new ArrayList<Player>(popSize);

            for (int i = 0; i < popSize; i++) {
                thePopulation.add(playerBuilder.genPlayer());
            }
        }

        if (thePopulation.size() != popSize) {
            throw new RuntimeException(
                    "The population was not completely initialized! Things will go wrong.");
        }
        for (Player p : thePopulation) {
            output.append(p.toString() + "\n");
        }
        output.append("Population successfully initialized!\n");
        output.append("Mating Pool Size is " + matingPoolSize + "\n");
        output.append("Mutation Rate is " + mutationRate + "\n");
        output.append("Crossover Type is " + crossoverType + "\n");
    }

    /**
     * Starts the GA thread, which runs games and generations, or waits for run instructions from the GAFrame.
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            while (!(running || runGen || runGame || runRound)) {
                Thread.sleep(50);// wait to run the GA
            }
            initializePopulation(populationSize);
            // output.setText("");
            while (true) {
                while (!(running || runGen || runGame || runRound)) {
                    Thread.sleep(50);// wait to run the GA
                }
                tournament(tournamentStyle);
                updatePopulation();
                generations++;
                output.append("Finished generation " + generations + "\n");
                runGen = false;
            }

        } catch (InterruptedException e) {
            output.append("\nERROR.  GA CONTROLLER WAS INTERRUPTED WHILE RUNNING.\n");
            output.append("\n\nGeneration interrupted before generation over.  Data may be damaged.");
        }
    }

    private void tournament(boolean complex) throws InterruptedException {
        Collections.shuffle(thePopulation, GAFrame.rnd);
        if (!complex) // O(n) games
        {
            playOrdering();
        } else // O(n^2) games
        {
            for (int i = 0; i < thePopulation.size(); i++) {
                playOrdering();
                thePopulation.add(thePopulation.remove(0)); // puts the first
                                                            // player last,
                                                            // slides everyone
                                                            // down one.
            }
        }
    }

    private void playOrdering() throws InterruptedException {
        for (int i = 0; i < thePopulation.size(); i++) {
            Player[] players = new Player[gameSize];
            for (int j = 0; j < gameSize; j++) {
                players[j] = thePopulation.get((i + j) % thePopulation.size());
            }
            runGame(players);
        }
    }

    private void runGame(Player[] ps) throws InterruptedException {
        if (game != null) {
            throw new RuntimeException(
                    "The GAController tried to start a new game while another was running!");
        }
        if (dummyGame) {
            game = new DummyGameController(ps, gameOutput, running || runGen
                    || runGame);
        } else {
            Constructor<?>[] constructs = gameClass.getConstructors();
            Object[] arr = new Object[3];
            arr[0] = ps;
            arr[1] = gameOutput;
            arr[2] = running || runGen || runGame;

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
                throw new GenGameImplementationException("No acceptable constructor found.");
            }
        }
        runGame = false;
        if(runRound)
            game.runRound();
        runRound = false;

        while (!game.gameOver()) {
            if (game.interrupted())
                throw new InterruptedException("Game Controller Interrupted.");
            Thread.sleep(50);
        }
        output.append("Game over.");
        game = null;
    }

    private void updatePopulation() {
        // prune the population and perform mutation/crossover
        prunePopulation(matingPoolSize);

        output.append("performing crossover with " + crossoverType
                + " slices\n");
        crossover(crossoverType);

        refillPopulation();

        output.append("performing mutation with a rate of " + mutationRate
                + "\n");
        mutate(mutationRate);

        output.append("The new population is:\n");
        for (Player p : thePopulation) {
            output.append(p.toString() + "\n");
        }

        // reset each player's fitness
        for (Player p : thePopulation) {
            p.clearFitness();
        }
    }

    private void refillPopulation() {
        // if the population is not full at this point, it will be filled with
        // replicas of its existing members
        int index = 0;
        while (thePopulation.size() < populationSize) {
            Trait[] randTrait = thePopulation.get(
                    GAFrame.rnd.nextInt(thePopulation.size())).getStrategy()
                    .clone();
            if (dummyPlayer) {
                thePopulation.add(dummyBuilder.genPlayer(randTrait));
            } else {
                thePopulation.add(playerBuilder.genPlayer(randTrait));
            }
            index++;
        }
        if (index > 0) {
            output.append("refilling the population with " + index
                    + " individuals\n");
        }
    }

    private void prunePopulation(double survivors) {
        ArrayList<Player> newPopulation = new ArrayList<Player>(populationSize);
        // sort the population by their fitness
        Collections.sort(thePopulation);
        // find the cutoff (survivors from dead)
        int splitPoint = (int) ((populationSize * survivors));

        if (splitPoint == 0) {
            throw new RuntimeException(
                    "There was no surviving population! Turn the survival rate up!");
        }
        // add the most fit of the population to the newPopulation - checks
        // against the size of the arrays is for resized populations
        for (int index = 0; index < splitPoint && index < thePopulation.size(); index++) {
            newPopulation.add(thePopulation.get(index));
        }
        thePopulation = newPopulation;
        output.append("\nAfter pruning, the population now has "
                + thePopulation.size() + " individuals and they are:\n");
        for (Player p : thePopulation) {
            output.append(p + " with fitness: " + p.fitness() + "\n");
        }
    }

    private void crossover(int slices) {
        Player male = null;
        Player female = null;
        Trait[] childDNA = null;

        if (slices == 0)
            return; // nothing to do, we're not doing crossover.

        if (thePopulation.size() < 2)
            return;

        while (thePopulation.size() < populationSize) {
            male = thePopulation.get((GAFrame.rnd.nextInt(thePopulation
                    .size())));
            female = thePopulation.get((GAFrame.rnd.nextInt(thePopulation
                    .size())));
            if (male == female) // == on purpose since we want to prevent the
                                // same player breeding, not two identical
                                // players
                continue;
            childDNA = new Trait[male.getStrategy().length];

            int crossoverRange = childDNA.length / slices;
            int[] slicePoints = new int[slices + 1];
            for (int i = 0; i < slices; i++) {
                slicePoints[i] = i * crossoverRange
                        + GAFrame.rnd.nextInt(crossoverRange);
            }
            slicePoints[slices] = childDNA.length;

            int lastSlot = 0;
            for (int i = 0; i < slicePoints.length; i++) {
                Trait[] selected = i % 2 == 0 ? male.getStrategy() : female
                        .getStrategy();
                System.arraycopy(selected, lastSlot, childDNA, lastSlot,
                        slicePoints[i] - lastSlot);

                lastSlot = slicePoints[i];
            }

            // System.out.println("Crosover Points: "+Arrays.toString(slicePoints));
            // System.out.println("Male:   "+Arrays.toString(male.getStrategy()));
            // System.out.println("Female: "+Arrays.toString(female.getStrategy()));
            // System.out.println("Child:  "+Arrays.toString(childDNA));

            if (dummyPlayer) {
                thePopulation.add(dummyBuilder.genPlayer(childDNA));
            } else {
                thePopulation.add(playerBuilder.genPlayer(childDNA));
            }
        }
    }

    private void mutate(double rate) {
        for (Player p : thePopulation) {
            double rnd = GAFrame.rnd.nextDouble();
            // if the rate is zero, mutation is effectively off
            if (rate > rnd) {
                p.mutate();
            }
        }
    }

    /**
     * Called by GAFrame to start running games and generations.
     * @param running true to set game/GA running, false to stop it
     */
    // Running status methods
    public void setRunning(boolean running) {
        this.running = running;
        if (game != null)
            game.setRunning(running);
    }

    /**
     * Called by GAFrame, instructs GA to run one generation.
     * @param run boolean flag - no change if false
     */
    public void setRunGeneration(boolean run) {
        runGen = run;
        if (game != null)
            game.setRunning(run);
    }

    /**
     * Called by GAFrame, instructs GA to run one game.
     * @param run boolean flag - no change if false
     */
    public void setRunGame(boolean run) {
        runGame = run;
        if (game != null)
            game.setRunning(run);
    }

    /**
     * Called by GAFrame, instructs GA to run one round.
     * @param run boolean flag - no change if false
     */
    public void setRunRound(boolean run) {
        runRound = run;
        if (game != null)
            game.runRound();
    }

    /**
     * Called by GAFrame, sets the GA's mutation rate.  Can only be called in between generations.
     * @param mutate a percentage between 0 and 100
     * @throws InvalidRequestException if the request is made in between generations or is out of the acceptable range
     */
    public void setMutationRate(double mutate) throws InvalidRequestException {
        if (safeToInterrupt()) {
            if (mutate > 100.0 || mutate < 0.0)
                throw new InvalidRequestException(
                        "Mutation requires a percentage between 0.0 and 100.0");
            mutationRate = mutate / 100;
        } else
            throw new InvalidRequestException(
                    "Cannot change mutation rate inbetween generations.");
    }

    /**
     * Called by GAFrame, sets the GA's crossover type.  This can range from no crossover to single point crossover up to crossover at every point.
     * If the crossover type is zero, no crossover will occur and clones of currently existing 'good' players will be made.
     * @param cross the number of crossover points to use.
     * @throws InvalidRequestException if the request is made in between generations or is out of the acceptable range
     */
    public void setCrossoverType(int cross) throws InvalidRequestException {
        if (safeToInterrupt()) {
            if (cross > playerBuilder.traitSize() || cross < 0)
                throw new InvalidRequestException(
                        "Crossover requires an integer between 0 and "
                                + playerBuilder.traitSize());
            crossoverType = cross;
        } else
            throw new InvalidRequestException(
                    "Cannot change crossover type inbetween generations.");
    }

    /**
     * Called by GAFrame, sets the size of the population of players.
     * @param size a positive integer indicating the desired population size.
     * @throws InvalidRequestException if the request is made in between generations or is out of the acceptable range
     */
    public void setPopulationSize(int size) throws InvalidRequestException {
        if (safeToInterrupt()) {
            if (size < gameSize || size < 2)
                throw new InvalidRequestException(
                        "Population must be at least as large as the number of players in a game, and no less than 2.");
            populationSize = size;
        } else
            throw new InvalidRequestException(
                    "Cannot change population size inbetween generations.");
    }

    /**
     * Called by GAFrame, sets the size of the mating pool, as a percentage of the population size.  This many players will survive each generation.
     * @param pool the percentage of players who should survive each generation.
     * @throws InvalidRequestException if the request is made in between generations or is out of the acceptable range
     */
    public void setMatingPoolSize(double pool) throws InvalidRequestException {
        if (safeToInterrupt()) {
            if (pool > 100.0 || pool <= 0)
                throw new InvalidRequestException(
                        "The mating pool requires a percentage greater than 0 and less than or equal to 100.0");
            matingPoolSize = pool / 100;
        } else
            throw new InvalidRequestException(
                    "Cannot change mating pool size inbetween generations.");
    }

    /**
     * Sets the tournament style, if every player should play at least one game (false), or at least every player (true).
     * This equates to population size number of games for false, or population size squared number of games for true.  This can be quite
     * expensive in terms of time, however is recommended for games where it is likely a good player could be still lose a game (that is to say
     * games with a fair bit of chance involved).
     * @param style false play simple round robin tournament, true play complex one.
     * @throws InvalidRequestException if the request is made in between generations or is out of the acceptable range
     */
    public void setTournamentStyle(boolean style)
            throws InvalidRequestException {
        if (safeToInterrupt()) {
            this.tournamentStyle = style;
        } else
            throw new InvalidRequestException(
                    "Cannot change tournament style inbetween generations.");
    }

    /**
     * Instructs the GA controller it should interrupt any running games and terminate its thread.
     */
    public void interrupt() {
        running = false;
        if (game != null)
            game.interrupt();

        if (!safeToInterrupt()) {
            output.append("\n\nGA INTERRUPTED!");
        }
        GAThread.interrupt();
    }

    /**
     * @return true if the GA controller is in a stable state and can be safely interrupted.
     */
    public boolean safeToInterrupt() {
        return !(running || runGen) && (game == null || game.gameOver());
    }

    /**
     * @return the current population size
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * @return the current mating pool size
     */
    public double getMatingPoolSize() {
        return matingPoolSize * 100;
    }

    /**
     * @return the current mutation rate
     */
    public double getMutationRate() {
        return mutationRate * 100;
    }

    /**
     * @return the current crossover type
     */
    public int getCrossoverType() {
        return crossoverType;
    }

    /**
     * @return the current tournament style
     */
    public boolean getTournamentStyle() {
        return tournamentStyle;
    }
}
