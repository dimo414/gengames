package gengames;

import gengames.ga.DummyGAController;
import gengames.ga.GAController;
import gengames.ga.InvalidRequestException;
import gengames.hearts.HeartsGameController;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * <p>This is the main class Genetic Games runs.  It is a GUI interface to the GA controller and loads all GameController classes dynamically.
 * GAFrame lets the user load any class that properly extends {@link gengames.GameController GameController} and run a genetic algorithm against user defined strategies.</p>
 * 
 *  <h2>Configuring GAFrame</h2>
 *  <p>There are two primary ways to execute a third party GameController, compile-time inclusion and runtime inclusion.
 *  Runtime inclusion is recommended, however both methods should work exactly the same once launched.</p>
 *  
 *  <h3>Compile-Time Inclusion of Custom GameControllers</h3>
 *  <p>The user should include the GenGames.jar file in the classpath, and their main method should consist of adding a title and the Class object
 *  for each GameController to include to the GAFrame.games Map, then calling the main method of GAFrame.  For example:</p>
<pre>public static void main(String[] args)
{
  gengames.GAFrame.games.put("My Game", MyGameController.class);
  gengames.GAFrame.main(args); // shouldn't be any args to pass
}</pre>
 *  
 *  <h3>Runtime Inclusion of Custom GameControllers</h3>
 *  <p>This is the advisable method of including custom GameControllers - the user runs GAFrame and passes as runtime parameter the title
 *  and name of the class to reference.  Multiple such pairs can be passed, all will be loaded.  This may seem more cumbersome than the compile-time
 *  version, however using an IDE like Eclipse it is very easy to load GenGames.jar as a referenced library and specify the runtime parameters.</p>
 *  
 * @author Michael Diamond
 * @author Blake Lavender
 */
public class GAFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    /*
     * For testing, rnd should be new Random(1) - or some other int, and deckRnd
     * should = rnd. This is distinctly less than optimal for real execution, so
     * we should be sure to set rnd to an instance of SecureRandom(), and
     * deckRnd to an instance of Random(), without a seed. This should provide a
     * bonus to the randomness since an external seed is defining the initial
     * structure of the deck, before being shuffled by rnd.
     */
    /**
     * For consistencies sake, it is suggested that all random calls utilize this field rather than new instances of Random or Math.random().
     * This field uses SecureRandom in the hope of generating a more random set of content.
     */
    public static final Random rnd = new SecureRandom();
    /**
     * This field is used solely to seed the construction of a new Deck.  (rnd is used for all other calls, like shuffling) 
     * It should generally not be used by third party tools unless they are similarly attempting to add randomness to their starting positions.
     */
    public static final Random seedRnd = new Random(); // for Deck's initial
    /**
     * This field stores all known classes extending GameController and, as the key, the name they should be displayed as.
     * See the sections on Inclusion of Custom GameControllers for more information on how to use this field.
     */
    public static final Map<String,Class<? extends GameController>> games = new Hashtable<>();
    
    private JScrollPane gameScrollPane;
    private JScrollPane gaScrollPane;
    private JTextArea gameTextArea;
    private JTextArea gaTextArea;
    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenu fileMenu;
    private JMenuItem setSaveLocItem;
    private JMenuItem savePopItem;
    private JMenuItem saveGameItem;
    private JMenuItem saveGenItem;
    private JMenuItem loadPopItem;
    private JMenu runsMenu;
    private JMenuItem testGAItem;
    private JMenuItem testGameItem;
    private JMenuItem testPlayerItem;
    private JMenuItem testSystemItem;
    private JMenuItem realRunItem;
    private JMenu controlMenu;
    private JMenuItem runRoundItem;
    private JMenuItem runGameItem;
    private JMenuItem runGenerationItem;
    private JMenuItem startStopItem;
    private JMenu gaMenu;
    private JMenuItem populationItem;
    private JMenuItem matingPoolItem;
    private JMenuItem mutationItem;
    private JMenuItem crossoverTypeItem;
    private JMenuItem tournamentStyleItem;

    private Hashtable<JMenuItem,Class<? extends GameController>> menus = new Hashtable<>();
    private Class<? extends GameController> gameContClass = null;
    private GAController ga = null;

    private boolean running = false;

    /**
     * The main method which launches the GAFrame and starts Genetic Games.
     * @param args optionally specify what GameController classes to be included at runtime.
     */
    public static void main(String[] args) {
        new GAFrame(args).setVisible(true);
    }

    /**
     * Creates the GAFrame and loads all GameController classes.
     * @param args passed down from main() it takes the names and locations of all GameController classes to load.
     */
    public GAFrame(String[] args) {
        initComponents(args);
        
        gameTextArea
                .setText("  _____ ______ _   _ ______ _______ _____ _____ \n"
                        + " / ____|  ____| \\ | |  ____|__   __|_   _/ ____|\n"
                        + "| |  __| |__  |  \\| | |__     | |    | || |     \n"
                        + "| | |_ |  __| | . ` |  __|    | |    | || |     \n"
                        + "| |__| | |____| |\\  | |____   | |   _| || |____ \n"
                        + " \\_____|______|_| \\_|______|  |_|  |_____\\_____|");
        gaTextArea.setText("  _____          __  __ ______  _____ \n"
                         + " / ____|   /\\   |  \\/  |  ____|/ ____|\n"
                         + "| |  __   /  \\  | \\  / | |__  | (___  \n"
                         + "| | |_ | / /\\ \\ | |\\/| |  __|  \\___ \\ \n"
                         + "| |__| |/ ____ \\| |  | | |____ ____) |\n"
                         + " \\_____/_/    \\_\\\\_|  |_|______|_____/ ");
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        try {
            Object src = evt.getSource();
            // Identify Games
            if (menus.containsKey(src)) {
                if (interrupt("Switch games")) {
                    ga = null;
                    gameContClass = menus.get(src);
                }
            }
            
            // Identify File Operations
            else if (src == setSaveLocItem || src == savePopItem || src == saveGameItem || src == saveGenItem || src == loadPopItem)
            {
                if (gameContClass == null)
                    throw new InvalidRequestException(
                            "You must select a game to play first.");
                if(src == setSaveLocItem)
                {
                    // TODO
                }
                else if(src == savePopItem)
                {
                    // TODO
                }
                else if(src == saveGameItem)
                {
                    // TODO
                }
                else if(src == saveGenItem)
                {
                    // TODO
                }
                else if(src == loadPopItem)
                {
                    // TODO
                }
            }

            // Identify Run Types
            else if (src == testGAItem || src == testGameItem
                    || src == testPlayerItem || src == testSystemItem
                    || src == realRunItem) {
                if (gameContClass == null)
                    throw new InvalidRequestException(
                            "You must select a game to play first.");
                if (interrupt("Change GAs")) {
                    if (src == testGAItem) {
                        ga = new GAController(gameContClass, gameTextArea,
                                gaTextArea, RunLevel.TEST_GA);
                    } else if (src == testGameItem) {
                        ga = new DummyGAController(gameContClass, gameTextArea,
                                gaTextArea, RunLevel.TEST_GAME);
                    } else if (src == testPlayerItem) {
                        ga = new DummyGAController(gameContClass, gameTextArea,
                                gaTextArea, RunLevel.TEST_PLAYER);
                    } else if (src == testSystemItem) {
                        ga = new GAController(gameContClass, gameTextArea,
                                gaTextArea, RunLevel.TEST_SYSTEM);
                    } else if (src == realRunItem) {
                        ga = new GAController(gameContClass, gameTextArea,
                                gaTextArea, RunLevel.FULL);
                    }
                }
            }

            // Identify Controls
            else if (src == runRoundItem || src == runGameItem
                    || src == runGenerationItem || src == startStopItem) {
                if (ga == null)
                    throw new InvalidRequestException(
                            "You must select a GA to run first.");
                if (src == runRoundItem) {
                    ga.setRunRound(true);
                } else if (src == runGameItem) {
                    ga.setRunGame(true);
                } else if (src == runGenerationItem) {
                    ga.setRunGeneration(true);
                } else if (src == startStopItem) {
                    running = !running;
                    startStopItem.setText(running ? "Stop" : "Start");
                    ga.setRunning(running);
                }
            }

            // Identify GA Settings
            else if (src == populationItem || src == matingPoolItem
                    || src == mutationItem || src == crossoverTypeItem
                    || src == tournamentStyleItem) {
                if (ga == null)
                    throw new InvalidRequestException(
                            "You must select a GA to configure first.");
                if (src == populationItem) {
                    String res = (String) JOptionPane.showInputDialog(this,
                            "How big should the population be?",
                            "Population Size", JOptionPane.QUESTION_MESSAGE,
                            null, null, ga.getPopulationSize());
                    if (res == null) // if they hit cancel
                        return;
                    int pop = Integer.parseInt(res);
                    ga.setPopulationSize(pop); // throws exception
                } else if (src == matingPoolItem) {
                    String res = (String) JOptionPane
                            .showInputDialog(
                                    this,
                                    "What percentage of the population should survive to reproduce?",
                                    "Mating Pool",
                                    JOptionPane.QUESTION_MESSAGE, null, null,
                                    ga.getMatingPoolSize());
                    if (res == null) // if they hit cancel
                        return;
                    double pool = Double.parseDouble(res);
                    ga.setMatingPoolSize(pool); // throws exception
                } else if (src == mutationItem) {
                    String res = (String) JOptionPane
                            .showInputDialog(
                                    this,
                                    "What percentage of the population should mutate each round?",
                                    "Mutation Rate",
                                    JOptionPane.QUESTION_MESSAGE, null, null,
                                    ga.getMutationRate());
                    if (res == null) // if they hit cancel
                        return;
                    double mute = Double.parseDouble(res);
                    ga.setMutationRate(mute); // throws exception
                } else if (src == crossoverTypeItem) {
                    String res = (String) JOptionPane
                            .showInputDialog(
                                    this,
                                    "How many crossover points would you like?  0 disables crossover.",
                                    "Set crossover points",
                                    JOptionPane.QUESTION_MESSAGE, null, null,
                                    ga.getCrossoverType());
                    if (res == null) // if they hit cancel
                        return;
                    int cross = Integer.parseInt(res);
                    ga.setCrossoverType(cross); // throws exception
                } else if (src == tournamentStyleItem) {
                    String[] possibleValues = {
                            ga.getPopulationSize() + " Games",
                            (int) Math.pow(ga.getPopulationSize(), 2)
                                    + " Games" };
                    String res = (String) JOptionPane.showInputDialog(this,
                            "Set The Tournament Style:", "Tournament Style",
                            JOptionPane.QUESTION_MESSAGE, null, possibleValues,
                            possibleValues[ga.getTournamentStyle() ? 1 : 0]);
                    ga.setTournamentStyle(res.equals(possibleValues[1]) ? true
                            : false);
                }
            } else
                throw new InvalidRequestException(
                        "Action triggered on an unexpected object: " + src);
        }
        /*
         * catch (UnimplementedException e) {
         * JOptionPane.showMessageDialog(this,
         * e.getMessage()+" not implemented.", "Error: Unimplemented Operation",
         * JOptionPane.ERROR_MESSAGE); }
         */
        catch (InvalidRequestException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error: Invalid Request", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "You must pass an integer.",
                    "Error: Malformed Number", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean interrupt(String label) {
        if (ga != null) {
            if (!ga.safeToInterrupt()) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "The program is in the middle of execution.  " + label
                                + " anyways?", "Attempting Unsafe " + label,
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.OK_OPTION) {
                    ga.setRunning(false);
                    ga.interrupt();
                } else {
                    return false;
                }
            }
            ga.interrupt();
        }
        // clean up from an interrupt
        running = false;
        startStopItem.setText("Start");
        return true;
    }

    @SuppressWarnings("unchecked")
    private void initComponents(String[] args) {
        // Window Close Operation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (!interrupt("Exit")) {
                    return;
                }
                setVisible(false);
                dispose();
                System.exit(0);
            }
        });
        
        // ADD DEFAULT GAMES
        games.put("Hearts", HeartsGameController.class);
        
        // CHECK FOR ADDITIONAL GAMES IN RUNTIME PARAMETERS
        for(int i = 0; i < args.length; i+=2)
        {
            try {
                Class<?> c = Class.forName(args[i+1]);
                if(!GameController.class.isAssignableFrom(c))
                    throw new ClassCastException(args[i+1]+" is not a GameController, please double check your configuration input.");
                games.put(args[i],(Class<? extends GameController>)c);
            } catch (ArrayIndexOutOfBoundsException e) {
                JOptionPane.showMessageDialog(this, "Your input was malformed, please double check your configuration input",
                        "Error: Malformed Configuration", JOptionPane.ERROR_MESSAGE);
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Class "+args[i+1]+" not found, please double check your configuration input",
                        "Error: Malformed Configuration", JOptionPane.ERROR_MESSAGE);
            } catch (ClassCastException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Error: Malformed Configuration", JOptionPane.ERROR_MESSAGE);
            }
        }
        

        gameScrollPane = new JScrollPane();
        gameTextArea = new JTextArea();
        gameTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        gaScrollPane = new JScrollPane();
        gaTextArea = new JTextArea();
        gaTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        menuBar = new JMenuBar();
        
        gameMenu = new JMenu();
        
        fileMenu = new JMenu();
        setSaveLocItem = new JMenuItem();
        savePopItem = new JMenuItem();
        saveGameItem = new JMenuItem();
        saveGenItem = new JMenuItem();
        loadPopItem = new JMenuItem();
        
        runsMenu = new JMenu();
        testGAItem = new JMenuItem();
        testGameItem = new JMenuItem();
        testPlayerItem = new JMenuItem();
        testSystemItem = new JMenuItem();
        realRunItem = new JMenuItem();
        
        controlMenu = new JMenu();
        runRoundItem = new JMenuItem();
        runGameItem = new JMenuItem();
        runGenerationItem = new JMenuItem();
        startStopItem = new JMenuItem();
        
        gaMenu = new JMenu();
        populationItem = new JMenuItem();
        matingPoolItem = new JMenuItem();
        mutationItem = new JMenuItem();
        crossoverTypeItem = new JMenuItem();
        tournamentStyleItem = new JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        gameTextArea.setEditable(false);
        gameScrollPane.setViewportView(gameTextArea);

        gaTextArea.setEditable(false);
        gaScrollPane.setViewportView(gaTextArea);

        gameMenu.setText("Game");
        
        for(String game : games.keySet())
        {
            JMenuItem menu = new JMenuItem();
            menu.setText(game);
            menu.addActionListener(this);
            gameMenu.add(menu);
            menus.put(menu, games.get(game));
        }
        
        menuBar.add(gameMenu);
        
        fileMenu.setText("File");
        
        setSaveLocItem.setText("Choose a Save Directory");
        setSaveLocItem.addActionListener(this);
        fileMenu.add(setSaveLocItem);
        
        savePopItem.setText("Save Population Data");
        savePopItem.addActionListener(this);
        fileMenu.add(savePopItem);
        
        saveGameItem.setText("Save Games");
        saveGameItem.addActionListener(this);
        fileMenu.add(saveGameItem);
        
        saveGenItem.setText("Save Generations");
        saveGenItem.addActionListener(this);
        fileMenu.add(saveGenItem);
        
        loadPopItem.setText("Load a Population");
        loadPopItem.addActionListener(this);
        fileMenu.add(loadPopItem);
        
        // TODO this needs to be implemented first, no?
    //  menuBar.add(fileMenu);

        runsMenu.setText("Run Types");

        testGAItem.setText("Test GA");
        testGAItem.addActionListener(this);
        runsMenu.add(testGAItem);

        testGameItem.setText("Test Game");
        testGameItem.addActionListener(this);
        runsMenu.add(testGameItem);

        testPlayerItem.setText("Test Player");
        testPlayerItem.addActionListener(this);
        runsMenu.add(testPlayerItem);

        testSystemItem.setText("Test System");
        testSystemItem.addActionListener(this);
        runsMenu.add(testSystemItem);

        realRunItem.setText("Real Run");
        realRunItem.addActionListener(this);
        runsMenu.add(realRunItem);

        menuBar.add(runsMenu);

        controlMenu.setText("Controls");

        runRoundItem.setText("Run One Round");
        runRoundItem.addActionListener(this);
        controlMenu.add(runRoundItem);

        runGameItem.setText("Run One Game");
        runGameItem.addActionListener(this);
        controlMenu.add(runGameItem);

        runGenerationItem.setText("Run One Generation");
        runGenerationItem.addActionListener(this);
        controlMenu.add(runGenerationItem);

        startStopItem.setText("Start");
        startStopItem.addActionListener(this);
        controlMenu.add(startStopItem);

        menuBar.add(controlMenu);

        gaMenu.setText("GA Settings");

        populationItem.setText("Set Population Size");
        populationItem.addActionListener(this);
        gaMenu.add(populationItem);

        matingPoolItem.setText("Set Mating Pool Size");
        matingPoolItem.addActionListener(this);
        gaMenu.add(matingPoolItem);

        mutationItem.setText("Set Mutation Rate");
        mutationItem.addActionListener(this);
        gaMenu.add(mutationItem);

        crossoverTypeItem.setText("Set Crossover Type");
        crossoverTypeItem.addActionListener(this);
        gaMenu.add(crossoverTypeItem);

        tournamentStyleItem.setText("Set Tournament Style");
        tournamentStyleItem.addActionListener(this);
        gaMenu.add(tournamentStyleItem);

        menuBar.add(gaMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
                getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addContainerGap().addComponent(
                        gameScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE,
                        400, Short.MAX_VALUE).addGap(18, 18, 18).addComponent(
                        gaScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE,
                        400, Short.MAX_VALUE).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,layout.createSequentialGroup().addContainerGap()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(
                                                gameScrollPane,
                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                600,
                                                Short.MAX_VALUE)
                                            .addComponent(
                                                gaScrollPane,
                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                600,
                                                Short.MAX_VALUE))
                                        .addContainerGap()));

        pack();
    }
}
