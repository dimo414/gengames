package gengames.ga;

/**
 * Indicates the GAController was given an invalid request, and was ignored. 
 * @author Michael Diamond
 * @author Blake Lavender
 */
public class InvalidRequestException extends Exception {
	/**
	 * @param str error message
	 */
	public InvalidRequestException(String str) {
		super(str);
	}

	private static final long serialVersionUID = 1L;
}
