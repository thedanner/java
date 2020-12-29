package ads.treestuff;

class ObjectNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ObjectNotFoundException ( ) {
		super( );
	}
	public ObjectNotFoundException (String str) {
		super(str);
	}
}
