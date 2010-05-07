package gengames.deck;

import java.util.ArrayList;

/**
 * A wrapper class for an ArrayList of Card objects.  This is primarily useful for constructing arrays of ArrayLists of Cards, as Java does not allow
 * arrays of generic objects.  For consistency it is suggested that developers always use the Cards object, rather than an ArrayList<Card> object.
 * @author Michael Diamond
 * @author Blake Lavender
 */
public class Cards extends ArrayList<Card> {
	private static final long serialVersionUID = 1L;

}
