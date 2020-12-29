package game.persistence;

/**
 *
 */
public class MissingKeyException extends RuntimeException
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MissingKeyException(String keyName)
    {
        super("key not found: " + keyName);
    }
}
