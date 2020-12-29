package game.persistence.recordStore;

public class RecordStoreException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RecordStoreException()
	{
		super();
	}

	public RecordStoreException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public RecordStoreException(String message)
	{
		super(message);
	}

	public RecordStoreException(Throwable cause)
	{
		super(cause);
	}
}
