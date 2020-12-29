package algorithms.hw5;

public class HeapUnderflowException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public HeapUnderflowException() {
		super();
	}
	
	public HeapUnderflowException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public HeapUnderflowException(String message) {
		super(message);
	}
	
	public HeapUnderflowException(Throwable cause) {
		super(cause);
	}
}
