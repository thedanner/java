package game.persistence.recordStore;

public class RecordStoreNotFoundException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RecordStoreNotFoundException()
	{
		super();
	}
	
	public RecordStoreNotFoundException(String message)
	{
		super(message);
	}
	
	public RecordStoreNotFoundException(Throwable cause)
	{
		super(cause);
	}
	
	public RecordStoreNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}
