package gengames;

/**
 * This exception is thrown whenever a GameController or other third party class fails to operate in the anticipated way.
 * @author Michael Diamond
 * @author Blake Lavender
 */
public class GenGameImplementationException extends RuntimeException {
    private static final long serialVersionUID = -141567286582424176L;
    
    /**
     * Constructor takes an error message.
     * @param msg The message to explain why the error occurred.
     */
    public GenGameImplementationException(String msg)
    {
        super(msg);
    }
    
    /**
     * Constructor takes an error message and a cause. 
     * @param msg The message to explain why the error occurred.
     * @param c The exception that caused this exception.
     */
    public GenGameImplementationException(String msg, Throwable c) {
        super(msg, c);
    }
}
