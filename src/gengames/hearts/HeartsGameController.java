package gengames.hearts;

import javax.swing.JTextArea;

import gengames.GameController;
import gengames.Player;
import gengames.PlayerBuilder;
import gengames.deck.Card;
import gengames.deck.Cards;
import gengames.deck.Deck;
import gengames.deck.Suit;
import gengames.deck.Value;

/**
 * Hearts implementation of the GameController, plays a game of hearts with four HeartsPlayers
 * @author Michael Diamond
 * @author Blake Lavender
 * @see gengames.GameController GameController
 */
public class HeartsGameController extends GameController {
    /** The PlayerBuilder for the GAController */
    protected static final PlayerBuilder PLAYER_BUILDER = new HeartsPlayerBuilder();
    /** The Dummy PlayerBuilder for the GAController */
    protected static final PlayerBuilder DUMMY_BUILDER = new DummyHeartsPlayerBuilder();
    /** The final score a player must reach to win */
    protected static final int GAME_OVER_SCORE = 100;
    /** The number of players in this game */
    protected static final int NUM_PLAYERS = 4;

    /** The group of players in this game */
    protected HeartsPlayer[] player = new HeartsPlayer[4];
    private int[] gameScore = new int[4];
    private int[] tempScore = new int[4];
    private Deck theDeck;
    private PassType passType;
    private int round;
    private int trick;
    private boolean running;
    private boolean runRound;
    private boolean heartsBroken;
    /** Tracks when the game is over. */
    protected boolean gameOver;

    private Thread gameThread;

    private JTextArea output;

    private boolean interrupted = false;

    /**
     * Do-nothing constructor for DummyGameController to extend.
     */
    public HeartsGameController() {
        // THIS DOES NOTHING. EXISTS SOLELY FOR SPECIAL CASES CALLS.
    }

    /**
     * Constructs a new HeartsGameController, starting a game with the passed players.
     * @param ps set of players to participate in this game
     * @param gameOutput the JTextArea to report output to
     * @param run starts the game playing or paused
     */
    public HeartsGameController(Player[] ps, JTextArea gameOutput, boolean run) {
        // playing the game should be in its own thread
        if (ps.length != NUM_PLAYERS)
            throw new RuntimeException("Expected " + NUM_PLAYERS + " players.");
        player = new HeartsPlayer[NUM_PLAYERS];
        for (int i = 0; i < ps.length; i++) {
            player[i] = (HeartsPlayer) ps[i];
        }
        running = run;
        output = gameOutput;
        gameOver = false;

        gameThread = new Thread(this);
        gameThread.setDaemon(true);
        gameThread.setPriority(Thread.MAX_PRIORITY);
        gameThread.start();
    }

    /**
     * @see gengames.GameController#interrupt()
     */
    @Override
    public void interrupt() {
        if (!gameOver()) {
            output.append("\n\nGame interrupted!");
            gameThread.interrupt();
        }
    }

    /**
     * @see gengames.GameController#gameOver()
     */
    @Override
    public boolean gameOver() {
        return gameOver;
    }

    /**
     * @see gengames.GameController#interrupted()
     */
    @Override
    public boolean interrupted() {
        return interrupted;
    }

    /**
     * @see gengames.GameController#run()
     */
    @Override
    public void run() {
        try {

            while (!(running || runRound))
                // wait to start game
                Thread.sleep(50);

            round = 1;
            output.setText("New Game Starting Between:\n");
            for (int i = 0; i < player.length; i++) {
                output.append(player[i] + "\n");
                player[i].startGame(i);
            }
            theDeck = new Deck();
            passType = PassType.LEFT;
            while (!gameOver()) {
                while (!(running || runRound))
                    Thread.sleep(50);
                playRound();
                runRound = false;
            }
            for (int i = 0; i < gameScore.length; i++) {
                player[i].addToFitness(gameScore[i]);
            }
        } catch (InterruptedException e) {
            output.append("\nERROR.  GAME INTERRUPTED BEFORE GAME OVER.");
            output.append("\n\nGame interrupted before game over.  Data may be damaged.");
            interrupted = true;
        }
    }

    private void playRound() {
        output.append("Starting round: " + round++ + "\n");
        tempScore = new int[4];
        theDeck.shuffle();
        Cards[] hands = new Cards[4];
        for (int i = 0; i < hands.length; i++) {
            hands[i] = new Cards();
        }
        while (theDeck.hasCards()) {
            for (int i = 0; i < hands.length; i++) {
                hands[i].add(theDeck.deal());
            }
        }

        for (int i = 0; i < hands.length; i++) {
            output.append("Dealing Player " + i + ": " + hands[i] + "\n");
            player[i].startRound(hands[i]);
        }

        passPhase();

        int startTrick = 0;
        for (int i = 0; i < player.length; i++) {
            if (hasTwoOfClubs(player[i].getHand()))
                startTrick = i;
        }

        int totalTricks = hands[0].size();
        heartsBroken = false;
        for (trick = 1; trick <= totalTricks; trick++) {
            startTrick = playTrick(startTrick);
        }

        updateScores();
    }

    private void passPhase() {
        // pass left
        Cards[] pass = new Cards[4];
        if (passType == PassType.LEFT) {
            for (int i = 0; i < 4; i++) {
                int passTo = (i + 1) % 4;
                pass[passTo] = player[i].passTo(passTo);
                output.append("Player " + i + " passes Player " + passTo + " "
                        + pass[passTo] + ".\n");
            }
        }
        // pass across
        else if (passType == PassType.ACCROSS) {
            for (int i = 0; i < 4; i++) {
                int passTo = (i + 2) % 4;
                pass[passTo] = player[i].passTo(passTo);
                output.append("Player " + i + " passes Player " + passTo + " "
                        + pass[passTo] + ".\n");
            }
        }
        // pass right
        else if (passType == PassType.RIGHT) {
            for (int i = 0; i < 4; i++) {
                int passTo = (i + 3) % 4;
                pass[passTo] = player[i].passTo(passTo);
                output.append("Player " + i + " passes Player " + passTo + " "
                        + pass[passTo] + ".\n");
            }
        } else // if(passType == PassType.NONE
        {
            output.append("No one passes this round.\n");
        }

        if (passType != PassType.NONE) {
            for (int i = 0; i < pass.length; i++) {
                player[i].receive(pass[i]);
            }
        }

        passType = passType.next();
    }

    private int playTrick(int start) {
        Cards localTrick = new Cards();

        // Gather cards from players
        for (int i = 0; i < 4; i++) {
            Cards playableCards = possibleCards(localTrick, player[(start + i) % 4]
                    .getHand());
            localTrick.add(player[(start + i) % 4].nextMove(localTrick, playableCards));
            output.append("Player " + ((start + i) % 4) + " played "
                    + localTrick.get(localTrick.size() - 1) + "\n");
            if (!heartsBroken && localTrick.get(i).getSuit().equals(Suit.HEARTS)) {
                output.append("HEARTS BROKE!\n");
                heartsBroken = true;
            }
        }

        // Determine player who wins
        Card bestCard = localTrick.get(0);
        int winningPos = 0;
        for (int i = 0; i < localTrick.size(); i++) {
            if (bestCard.getSuit().equals(localTrick.get(i).getSuit())
                    && bestCard.getValue().compareTo(localTrick.get(i).getValue()) < 0) {
                bestCard = localTrick.get(i);
                winningPos = i;
            }
        }
        winningPos = (start + winningPos) % 4; // move winning pos from position
                                                // in trick to player who played
                                                // it
        output.append("Player " + winningPos + " wins the trick, taking "); // no
                                                                            // \n
                                                                            // on
                                                                            // purpose
        // Update score, clear cards
        int origScore = tempScore[winningPos];
        for (int i = 0; i < localTrick.size(); i++) {
            Card card = localTrick.get(i);
            if (card.getSuit().equals(Suit.HEARTS))
                tempScore[winningPos] += 1;
            else if (card.getSuit().equals(Suit.SPADES)
                    && card.getValue().equals(Value.QUEEN))
                tempScore[winningPos] += 13;
        }
        output.append(tempScore[winningPos] - origScore + " points.\n");

        // Inform players
        for (HeartsPlayer p : player) {
            p.trickOver(winningPos, localTrick);
        }
        return winningPos;
    }

    /**
     * Determines which cards can be played at this time
     * @param trickCards the cards played so far this trick
     * @param hand the cards in the player's hand
     * @return the set of cards available to be played
     */
    protected Cards possibleCards(Cards trickCards, Cards hand) {
        if (trickCards.size() > 0) // respond to start of trick
        {
            Cards playableCards = new Cards();
            for (Card c : hand) {
                if (c.getSuit().equals(trickCards.get(0).getSuit()))
                    playableCards.add(c);
            }
            if (playableCards.size() > 0)
                return playableCards;

            for (Card c : hand) {
                if ((c.getSuit().equals(Suit.HEARTS) && !heartsBroken)
                        || (c.getSuit().equals(Suit.CLUBS)
                                && c.getValue().equals(Value.QUEEN) && trick == 1))
                    continue; // if hearts isn't broken, no hearts. If first
                                // trick, no Queen Clubs.

                playableCards.add(c);
            }

            if (playableCards.size() > 0)
                return playableCards;

            // still can't play anything, means we get to break hearts or queen
            // despite
            return (Cards) hand.clone();
        } else // starting the trick
        {
            Cards playableCards = new Cards();
            if (trick == 1) {
                for (Card c : hand) {
                    if (c.getSuit().equals(Suit.CLUBS)
                            && c.getValue().equals(Value.TWO)) {
                        playableCards.add(c);
                        return playableCards;
                    }
                }
                // didn't find the two of clubs.......
                throw new RuntimeException(
                        "Player was expected to have the two of clubs.");
            }

            for (Card c : hand) {
                if ((c.getSuit().equals(Suit.HEARTS) && !heartsBroken)
                        || (isQueenOfSpades(c) && trick == 1))
                    continue; // if hearts isn't broken, no hearts. If first
                                // trick, no Queen Spades.

                playableCards.add(c);
            }

            if (playableCards.size() > 0)
                return playableCards;

            // if can't play anything, can play anything.
            return (Cards) hand.clone();
        }
    }

    // Run Control methods
    /**
     * @see gengames.GameController#runRound()
     */
    @Override
    public void runRound() {
        runRound = true;
    }

    /**
     * @see gengames.GameController#setRunning(boolean)
     */
    @Override
    public void setRunning(boolean run) {
        running = run;
    }

    private void updateScores() {
        boolean moonShot = true;
        for (int i = 0; i < tempScore.length; i++) {
            if (tempScore[i] > 0 && tempScore[i] < 26) // if anyone's score is
                                                        // not 0 or 26, no one
                                                        // shot the moon.
            {
                moonShot = false;
                break;
            }
        }

        if (moonShot)
            output.append("Someone SHOT THE MOON!\n"); // can't detect who shot
                                                        // the moon at this
                                                        // time. Could change if
                                                        // we care.

        for (int i = 0; i < tempScore.length; i++) {
            gameScore[i] += moonShot ? 26 - tempScore[i] : tempScore[i];
            output.append("Player " + i + " score: " + gameScore[i] + "\n");
            if (gameScore[i] >= GAME_OVER_SCORE) {
                gameOver = true;
                output.append("Well that's game.\n");
            }
        }
    }

    /**
     * @see gengames.GameController#numPlayers()
     */
    @Override
    public int numPlayers() {
        return NUM_PLAYERS;
    }

    /**
     * @see gengames.GameController#getPlayerBuilder()
     */
    @Override
    public PlayerBuilder getPlayerBuilder() {
        return PLAYER_BUILDER;
    }

    /**
     * @see gengames.GameController#getDummyPlayerBuilder()
     */
    @Override
    public PlayerBuilder getDummyPlayerBuilder() {
        return DUMMY_BUILDER;
    }

    private enum PassType {
        LEFT, RIGHT, ACCROSS, NONE;
        public PassType next() {
            switch (this) {
            case LEFT:
                return RIGHT;
            case RIGHT:
                return ACCROSS;
            case ACCROSS:
                return NONE;
            case NONE:
                return LEFT;
            default:
                throw new RuntimeException(
                        "Switched Over All PassTypes, None Matched.  Critical Error.");
            }
        }
    }
    


    /**
     * Helper method to indicate a hand contains the two of clubs (indicating it's the starter hand)
     * @param hand the hand to check
     * @return if the hand has the two of clubs
     */
    /*package*/ static boolean hasTwoOfClubs(Cards hand) {
        for (Card card : hand) {
            if (card.getSuit().equals(Suit.CLUBS)
                    && card.getValue().equals(Value.TWO)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to indicate a card is the queen of spades
     * @param c the card to check
     * @return if the card is the queen of spades
     */
    /*package*/ static boolean isQueenOfSpades(Card c) {
        return c.equals(Suit.SPADES) && c.equals(Value.QUEEN);
    }
}
