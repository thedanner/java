package ads.treestuff;

class TreeUnderflowException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TreeUnderflowException ( ) {
		super( );
	}
	public TreeUnderflowException (String str) {
		super(str);
	}
}
