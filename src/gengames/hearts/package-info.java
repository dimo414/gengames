/**
 * <p>This package (and its {@link gengames.hearts.traits subpackage}, contains all the Hearts specific code
 * in GenGames.  You can look at the code in this package to help understand how to implement the classes and methods
 * you need to implement.  It's far from a perfect implementation (running it does not give you good Hearts players unfortunately)
 * however it is fully functional as a demonstration, and it contains some good (and bad practices).</p>
 * 
 * <p>The hardest part of any genetic algorithm is properly defining all the genes, which is where we ran into trouble.  We examined all the
 * different phases and states of play (for instance, when the Queen of Spades has not been played, and you have a higher Spade, you 
 * play differently) but failed to properly account for decisions made during an individual trick, e.g. deciding to play a card that will take
 * or lose the trick.  Our initial hope was that simply properly and granularly defining the different states of play we would genetically 
 * select for the correct decisions.  This was ultimately flawed, for at the end of the day (and without noticing) we had defined a 
 * player which made all of its decisions without even looking at the current trick.  In hindsight of course, it seems painfully obvious 
 * that we should have paid attention to this (and, when first building the player, even implemented the necessary functionality, just 
 * never used it) but we didn't.  This despite both of us playing through several games and talking through out trait strategy for quite
 * some time.  So just be aware, when you think you've got your traits all selected, check and double check that you actually do.</p>
 * 
 * <p>One other poor decision we made was to try to compartmentalize the GameController and the Player, by keeping <em>all</em> the business
 * logic in the GameController, and only giving the Player as little information as it needed to make a choice.  For example, the computation
 * of which cards a player can play is done by the GameController, rather than the Player.  This sort of compartmentalization would usually seem
 * like a good practice, especially since (as was the initial intent and rational) we hoped someone could build a different Player, and the
 * GameController should therefore not trust the Player.  A good idea in theory, but despite compartmentalizing the <em>logic</em>, it split
 * the code back and forth between the two classes, and made developing and debugging gameplay errors a nightmare.  It would have been far more
 * logical (and more reflective of an actual game of Hearts) for the GameController to let the Player make all the decisions, and simply double
 * check them, throwing an exception* if they made an illegal choice.</p>
 * 
 * <p>*It would be really nice if GenGames handled such exceptions more gracefully - perhaps prompting the user to decide to end the run or just
 * ignore that game - however such functionality hasn't been implemented.  That said, developers should not be building GameControllers or Players
 * which don't work, so this shouldn't be a terribly big issue.</p> 
 */
package gengames.hearts;