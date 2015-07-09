package gengames.hearts;

import java.util.ArrayList;
import java.util.Collections;

import gengames.GAFrame;
import gengames.Player;
import gengames.Trait;
import gengames.deck.Card;
import gengames.deck.Cards;
import gengames.deck.Suit;
import gengames.deck.ValueComparator;
import gengames.hearts.traits.*;

/**
 * Hearts implementation of the Player object, follows a strategy for playing hearts defined genetically. 
 * @author Michael Diamond
 * @author Blake Lavender
 * @see gengames.Player Player
 */
public class HeartsPlayer extends Player {
    /**
     * The cards in the player's hand
     */
    protected Cards myHand;

    // Tracking variables
    private int myIndex;
    @SuppressWarnings("unused")
    private int passedTo;
    @SuppressWarnings("unused")
    private boolean passedQueen;
    private boolean queenPlayed;
    private int tookQueen;
    private boolean heartsBroken;
    private int tookFirstHeart;
    private boolean multipleHearts; // if multiple people have taken hearts
    private boolean hadQueen; // had the queen at the start of the game (before
                                // the pass)

    // Trait positions
    private static final int PassWithQueen = 0;
    private static final int Passing = 1;
    private static final int SBPHSQ = 2;
    private static final int SBPESQ = 3;
    private static final int SBPEMN = 4;
    private static final int SBNHSN = 5;
    private static final int SBNHMN = 6;
    private static final int SBNESN = 7;
    private static final int SBNEMN = 8;
    private static final int SNPHSQ = 9;
    private static final int SNPEMN = 10;
    private static final int SNNHMN = 11;
    private static final int SNNEMN = 12;
    private static final int PBPEMN = 13;
    private static final int PNPHSQ = 14;
    private static final int PNPEMN = 15;
    private static final int SBPHSN = 16;
    private static final int SBPESN = 17;
    private static final int SBPHMN = 18;
    private static final int SNPESQ = 19;
    private static final int SNPHMN = 20;
    private static final int PBPHSQ = 21;
    private static final int PBPESQ = 22;
    private static final int PBPHMN = 23;
    private static final int PBPHSN = 24;
    private static final int PBPESN = 25;
    private static final int PNPESQ = 26;
    private static final int PNPHMN = 27;
    private static final int PNNHMN = 28;
    private static final int PNNEMN = 29;
    private static final int PBNHSN = 30;
    private static final int PBNESN = 31;
    private static final int PBNHMN = 32;
    private static final int PBNEMN = 33;

    private static final int TRAIT_SIZE = 34;

    /**
     * Do-nothing constructor for DummyPlayer to extend.
     */
    protected HeartsPlayer() {
        // For DummyPlayer
    }

    /**
     * Constructs a new player.  The {@link gengames.hearts.HeartsPlayerBuilder} builds the player, populating it with Traits which defines how the Player will behave.
     * @param str an array of Trait objects.
     */
    /* package */HeartsPlayer(Trait[] str) {
        if (str.length != TRAIT_SIZE)
            throw new RuntimeException("HeartsPlayer expects an array of "
                    + TRAIT_SIZE + " traits.  Received " + str.length + ".");
        strategy = str;
    }

    /**
     * Indicates to the player a game is starting, and defines its numerical position at the table.
     * @param index the player's position in the game.
     */
    // Game Methods
    public void startGame(int index) {
        myIndex = index;
    }

    /**
     * Indicates to the player a round is starting, and tells it what cards are in its hand.
     * @param hand the cards in the player's hand.
     */
    public void startRound(Cards hand) {
        myHand = hand;
        passedTo = -1;
        passedQueen = false;
        queenPlayed = false;
        tookQueen = -1;
        heartsBroken = false;
        tookFirstHeart = -1;
        multipleHearts = false;
        hadQueen = false;
    }

    /**
     * Asks the player to chose cards to pass to another player.  Cards are chosen genetically.
     * @param player the player to pass to.
     * @return a list of Cards being passed.
     */
    public Cards passTo(int player) {
        passedTo = player;
        Cards pass = new Cards();
        Cards possible = (Cards) myHand.clone();
        int toPass = 3;
        Card queen = queenOfSpades();
        if (queen != null) {
            hadQueen = true;
        }
        if (queen != null
                && (PassingQueen) strategy[PassWithQueen] == PassingQueen.LOSEQUEEN) {
            toPass--;
            pass.add(queen);
            passedQueen = true;
        }
        if (queen != null)
            possible.remove(queen);

        for (int i = 0; i < toPass; i++) {
            Card pick = pickCard((NormalTrait) strategy[Passing], possible);
            pass.add(pick);
            possible.remove(pick);
        }

        for (Card c : pass)
            myHand.remove(c);

        return pass;
    }

    /**
     * Receives the cards passed by another player.
     * @param cards a list of cards to add to the player's hand.
     */
    public void receive(Cards cards) {
        myHand.addAll(cards);
    }

    private static Card pickCard(NormalTrait trait, Cards possible) {
        Collections.sort(possible, new ValueComparator());
        switch (trait) {
        case HIGHEST:
            return possible.get(possible.size() - 1);
        case LOWEST:
            return possible.get(0);
        case SMALLESTSUITLOW:
            return smallestSuit(possible).get(0);
        case LARGESTSUITLOW:
            return largestSuit(possible).get(0);
        case SMALLESTSUITHIGH:
            Cards cards = smallestSuit(possible);
            return cards.get(cards.size() - 1);
        case LARGESTSUITHIGH:
            cards = largestSuit(possible);
            return cards.get(cards.size() - 1);
        case LOWHEARTS:
            Cards ret = getSuit(possible, Suit.HEARTS);
            if (ret.size() == 0)
                return possible.get(GAFrame.rnd.nextInt(possible.size()));
            return ret.get(0);
        case LOWSPADES:
            ret = getSuit(possible, Suit.SPADES);
            if (ret.size() == 0)
                return possible.get(GAFrame.rnd.nextInt(possible.size()));
            return ret.get(0);
        case HIGHHEARTS:
            ret = getSuit(possible, Suit.HEARTS);
            if (ret.size() == 0)
                return possible.get(GAFrame.rnd.nextInt(possible.size()));
            return ret.get(ret.size() - 1);
        case HIGHSPADES:
            ret = getSuit(possible, Suit.SPADES);
            if (ret.size() == 0)
                return possible.get(GAFrame.rnd.nextInt(possible.size()));
            return ret.get(ret.size() - 1);
        case RANDOM:
            return possible.get(GAFrame.rnd.nextInt(possible.size()));
        default:
            throw new RuntimeException("Unexpected trait.");
        }
    }

    /**
     * Given the trick, and the set of cards known to be playable, determines the appropriate card to play.
     * @param trick the cards played thus far in the trick.
     * @param playableCards the cards the player can play.
     * @return the card the player choses to play.
     */
    public Card nextMove(Cards trick, Cards playableCards) {
        NormalTrait tr = identifyCase(trick);
        Card ret = pickCard(tr, playableCards);
        int size = myHand.size();
        myHand.remove(ret);
        if (size == myHand.size())
            System.err.println("If trait is " + tr + " trick is " + trick
                    + " and choices are " + playableCards
                    + " we failed to remove anything.");
        return ret;
    }

    private NormalTrait identifyCase(Cards trick) {
        if (trick.size() == 0)// Starting Trick
        {
            if (heartsBroken)// Hearts Broken
            {
                if (queenPlayed)// Queen has been played
                {
                    // queen cannot be in hand at this point (disregard H/E
                    // cases)

                    if (multipleHearts)// multiple players have hearts
                    {
                        if (tookQueen == myIndex)// I took the queen already
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[SBPHSQ];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[SBPESQ];
                            }

                        } else// someone else took the queen already
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[SBPHSN];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[SBPESN];
                            }
                        }
                    } else// one player has hearts
                    {
                        if (tookQueen == myIndex)// I took the queen already
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[SBPHSQ];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[SBPESQ];
                            }
                        } else// someone else took the queen already
                        {
                            if (tookQueen == tookFirstHeart)// this player could
                                                            // shoot the moon
                            {
                                if (hadQueen)// I had the queen to start
                                {
                                    return (NormalTrait) strategy[SBPHMN];
                                } else// another player had the queen to start
                                {
                                    return (NormalTrait) strategy[SBPEMN];
                                }
                            } else// one has the queen, another took hearts, no
                                    // moon risk
                            {
                                if (hadQueen)// I had the queen to start
                                {
                                    return (NormalTrait) strategy[SBPHSN];
                                } else// another player had the queen to start
                                {
                                    return (NormalTrait) strategy[SBPESN];
                                }
                            }
                        }
                    }
                } else// Queen has not been played
                {
                    if (queenOfSpades() != null)// queen is in hand
                    {
                        if (multipleHearts)// multiple players have hearts
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[SBNHSN];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[SBNESN];
                            }
                        } else// one player has hearts
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[SBNHMN];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[SBNEMN];
                            }
                        }
                    } else// queen is not in hand
                    {
                        if (multipleHearts)// multiple players have hearts
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[SBNHSN];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[SBNESN];
                            }

                        } else// one player has hearts
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[SBNHMN];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[SBNEMN];
                            }
                        }
                    }
                }
            } else// Hearts Not Broken
            {
                // nobody can have hearts at this point

                if (queenPlayed)// Queen has been played
                {
                    // queen cannot be in hand at this point
                    if (tookQueen == myIndex)// I took the queen already
                    {
                        if (hadQueen)// I had the queen to start
                        {
                            return (NormalTrait) strategy[SNPHSQ];
                        } else// another player had the queen to start
                        {
                            return (NormalTrait) strategy[SNPESQ];
                        }
                    } else// someone else took the queen already
                    {
                        if (hadQueen)// I had the queen to start
                        {
                            return (NormalTrait) strategy[SNPHMN];
                        } else// another player had the queen to start
                        {
                            return (NormalTrait) strategy[SNPEMN];
                        }

                    }

                } else// Queen has not been played
                {
                    if (queenOfSpades() != null)// queen is in hand
                    {
                        if (hadQueen)// I had the queen to start
                        {
                            return (NormalTrait) strategy[SNNHMN];
                        } else// another player had the queen to start
                        {
                            return (NormalTrait) strategy[SNNEMN];
                        }

                    } else// queen is not in hand
                    {
                        if (hadQueen)// I had the queen to start
                        {
                            return (NormalTrait) strategy[SNNHMN];
                        } else// another player had the queen to start
                        {
                            return (NormalTrait) strategy[SNNEMN];
                        }

                    }
                }
            }
        } else// In Play
        {
            if (heartsBroken)// Hearts Broken
            {
                if (queenPlayed)// Queen has been played
                {
                    // queen cannot be in hand at this point (disregard H/E
                    // cases)

                    if (multipleHearts)// multiple players have hearts
                    {
                        if (tookQueen == myIndex)// I took the queen already
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[PBPHSQ];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[PBPESQ];
                            }

                        } else// someone else took the queen already
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[PBPHSN];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[PBPESN];
                            }

                        }
                    } else// one player has hearts
                    {
                        if (tookQueen == myIndex)// I took the queen already
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[PBPHSQ];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[PBPESQ];
                            }

                        } else// someone else took the queen already
                        {
                            if (tookQueen == tookFirstHeart)// this player could
                                                            // shoot the moon
                            {
                                if (hadQueen)// I had the queen to start
                                {
                                    return (NormalTrait) strategy[PBPHMN];
                                } else// another player had the queen to start
                                {
                                    return (NormalTrait) strategy[PBPEMN];
                                }

                            } else// one has the queen, another took hearts, no
                                    // moon risk
                            {
                                if (hadQueen)// I had the queen to start
                                {
                                    return (NormalTrait) strategy[PBPHSN];
                                } else// another player had the queen to start
                                {
                                    return (NormalTrait) strategy[PBPESN];
                                }

                            }
                        }
                    }
                } else// Queen has not been played
                {
                    if (queenOfSpades() != null)// queen is in hand
                    {
                        if (multipleHearts)// multiple players have hearts
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[PBNHSN];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[PBNESN];
                            }
                        } else// one player has hearts
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[PBNHMN];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[PBNEMN];
                            }
                        }
                    } else// queen is not in hand
                    {
                        if (multipleHearts)// multiple players have hearts
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[PBNHSN];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[PBNESN];
                            }

                        } else// one player has hearts
                        {
                            if (hadQueen)// I had the queen to start
                            {
                                return (NormalTrait) strategy[PBNHMN];
                            } else// another player had the queen to start
                            {
                                return (NormalTrait) strategy[PBNEMN];
                            }
                        }
                    }
                }
            } else// Hearts NotBroken
            {
                // nobody can have hearts at this point

                if (queenPlayed)// Queen has been played
                {
                    // queen cannot be in hand at this point
                    if (tookQueen == myIndex)// I took the queen already
                    {
                        if (hadQueen)// I had the queen to start
                        {
                            return (NormalTrait) strategy[PNPHSQ];
                        } else// another player had the queen to start
                        {
                            return (NormalTrait) strategy[PNPESQ];
                        }

                    } else// someone else took the queen already
                    {
                        if (hadQueen)// I had the queen to start
                        {
                            return (NormalTrait) strategy[PNPHMN];
                        } else// another player had the queen to start
                        {
                            return (NormalTrait) strategy[PNPEMN];
                        }

                    }

                } else// Queen has not been played
                {
                    if (queenOfSpades() != null)// queen is in hand
                    {
                        if (hadQueen)// I had the queen to start
                        {
                            return (NormalTrait) strategy[PNNHMN];
                        } else// another player had the queen to start
                        {
                            return (NormalTrait) strategy[PNNEMN];
                        }

                    } else// queen is not in hand
                    {
                        if (hadQueen)// I had the queen to start
                        {
                            return (NormalTrait) strategy[PNNHMN];
                        } else// another player had the queen to start
                        {
                            return (NormalTrait) strategy[PNNEMN];
                        }
                    }
                }
            }
        }

        // To test if there's danger of a player shooting the moon, check:
        // if(queenPlayed && tookQueen != myIndex && !multipleHearts)
    }

    /**
     * Indicates to the player that the trick is over, who won, and what cards were played.
     * @param winner the player who won the trick.
     * @param trick the contents of the trick.
     */
    public void trickOver(int winner, Cards trick) {
        // Important - the winner is the 'real' position of the winner, it does
        // not change between rounds
        // However the trick is passed in order played, so index 0 is the start
        // of the trick, rather than the first player
        // Therefore winner cannot be used to lookup the winning card in the
        // trick
        for (Card c : trick) {
            if (HeartsGameController.isQueenOfSpades(c)) {
                queenPlayed = true;
                tookQueen = winner;
            }
            if (c.getSuit().equals(Suit.HEARTS)) {
                if (!heartsBroken) {
                    heartsBroken = true;
                    tookFirstHeart = winner;
                } else if (winner != tookFirstHeart)
                    multipleHearts = true;
            }
        }
    }

    private static Cards getSuit(Cards set, Suit suit) {
        Cards cards = new Cards();
        for (Card c : set) {
            if (c.getSuit().equals(suit))
                cards.add(c);
        }
        return cards;
    }

    private static Cards selectSuit(Cards possible, boolean smallest) {

        ArrayList<Cards> suits = new ArrayList<>();
        ArrayList<Cards> nonEmptySuits = new ArrayList<>();

        suits.add(new Cards()); // clubs
        suits.add(new Cards()); // diamonds
        suits.add(new Cards()); // spades
        suits.add(new Cards()); // hearts
        for (Card c : possible) {
            switch (c.getSuit()) {
            case CLUBS:
                suits.get(0).add(c);
                break;
            case DIAMONDS:
                suits.get(1).add(c);
                break;
            case SPADES:
                suits.get(2).add(c);
                break;
            case HEARTS:
                suits.get(3).add(c);
                break;
            }
        }

        for (Cards suit : suits) {
            if (suit.size() > 0)
                nonEmptySuits.add(suit);
        }

        int selected = 0;
        for (int i = 0; i < nonEmptySuits.size(); i++) {
            boolean smaller = nonEmptySuits.get(i).size() < nonEmptySuits.get(
                    selected).size();
            if (smallest && smaller || !smallest && !smallest)
                selected = i;
        }
        return nonEmptySuits.get(selected);
    }

    private static Cards largestSuit(Cards possible) {
        return selectSuit(possible, false);
    }

    private static Cards smallestSuit(Cards possible) {
        return selectSuit(possible, true);
    }

    /**
     * Returns the contents of the player's hand.
     * @return the player's hand.
     */
    public Cards getHand() {
        return myHand;
    }

    /**
     * Compares this player with another player to determine genetic superiority.
     * @param other the player to compare with.
     * @see gengames.Player#compareTo(gengames.Player) Player
     */
    @Override
    public int compareTo(Player other) {
        if (!(other instanceof HeartsPlayer))
            throw new RuntimeException(
                    "Attempting to compare two different types of player.  This is unacceptable.");
        long comp = fitness() - other.fitness();
        if (comp != (int) comp)
            throw new RuntimeException("Two players fitness differs by " + comp
                    + " which is more than an int can store.");
        return (int) (comp);
    }

    /**
     * toString() method.
     * @see gengames.Player#toString() Player
     */
    @Override
    public String toString() {
        String returnMe = "Player ";
        for (Trait t : strategy) {
            returnMe += t + " ";
        }
        return returnMe;
    }

    /**
     * Returns a structured description of the player's strategy.
     * @see gengames.Player#traitDescription() Player
     */
    @Override
    public String traitDescription() {
        String ret = "\tQueenPassTrait:" + getStrategy()[0] + "\n";
        ret += "\tPassTrait:" + getStrategy()[1] + "\n";
        for (int i = 2; i < getStrategy().length; i++) {
            ret += "\t" + getCase(i) + ":" + getStrategy()[i].toString() + "\n";
        }
        return ret;
    }

    /**
     * Checks if the player is holding the Queen of Spades.
     * @return null or the Queen of Spades object if in hand.
     */
    // tests if has card, returns it if it does
    public Card queenOfSpades() {
        for (Card c : myHand) {
            if (HeartsGameController.isQueenOfSpades(c))
                return c;
        }
        return null;
    }

    /**
     * Given a trait index returns a name to describe trait being looked up.
     * @param trait index of the trait in question.
     * @return briefe description of trait.
     */
    public String getCase(int trait) {
        switch (trait) {
        case 0:
            return "PassWithQueen";
        case 1:
            return "Passing";
        case 2:
            return "SBPHSQ";
        case 3:
            return "SBPESQ";
        case 4:
            return "SBPEMN";
        case 5:
            return "SBNHSN";
        case 6:
            return "SBNHMN";
        case 7:
            return "SBNESN";
        case 8:
            return "SBNEMN";
        case 9:
            return "SNPHSQ";
        case 10:
            return "SNPEMN";
        case 11:
            return "SNNHMN";
        case 12:
            return "SNNEMN";
        case 13:
            return "PBPEMN";
        case 14:
            return "PNPHSQ";
        case 15:
            return "PNPEMN";
        case 16:
            return "SBPHSN";
        case 17:
            return "SBPESN";
        case 18:
            return "SBPHMN";
        case 19:
            return "SNPESQ";
        case 20:
            return "SNPHMN";
        case 21:
            return "PBPHSQ";
        case 22:
            return "PBPESQ";
        case 23:
            return "PBPHMN";
        case 24:
            return "PBPHSN";
        case 25:
            return "PBPESN";
        case 26:
            return "PNPESQ";
        case 27:
            return "PNPHMN";
        case 28:
            return "PNNHMN";
        case 29:
            return "PNNEMN";
        case 30:
            return "PBNHSN";
        case 31:
            return "PBNESN";
        case 32:
            return "PBNHMN";
        case 33:
            return "PBNEMN";
        default:
            throw new RuntimeException("Unable to locate proper case");
        }
    }

    /**
     * Indicates the expected size of the trait array.
     * @see gengames.Player#traitSize() Player
     */
    @Override
    public int traitSize() {
        return TRAIT_SIZE;
    }
}