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
     * @param err The message to explain why the error occurred.
     */
    public GenGameImplementationException(String err)
    {
        super(err);
    }
}
