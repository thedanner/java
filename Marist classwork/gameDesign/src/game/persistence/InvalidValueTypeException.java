package game.persistence;

/**
 *
 */
public class InvalidValueTypeException extends RuntimeException
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidValueTypeException(
            String keyName, String expectedType, String actualValue)
    {
        super("key '" + keyName + "' not of type " +
                expectedType + ": " + actualValue);
    }
}
