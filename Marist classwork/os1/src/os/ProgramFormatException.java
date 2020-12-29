package os;

public class ProgramFormatException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8039561785369033165L;
	
	public ProgramFormatException() {
		super();
	}
	
	public ProgramFormatException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ProgramFormatException(String message) {
		super(message);
	}
	
	public ProgramFormatException(Throwable cause) {
		super(cause);
	}
}
