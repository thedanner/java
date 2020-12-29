package _mine.klok;

import javax.swing.JApplet;

public class KlokApplet extends JApplet {
	private static final long serialVersionUID = -6761922241854322287L;
	
	boolean initDone = false;
	
	/** Initializes the applet */
	@Override
	public void init() {
		initDone = true;
		
		int dim = 350;
    	
		// for local testing only
		setSize(dim, dim);
		
		KlokPanel kp = new KlokPanel();
		kp.setSize(dim, dim);
		
		getContentPane().add(kp);
		
		kp.setVisible(true);
	}
	
	/**
	 * 
	 */
	@Override
	public void start() {
		
	}
	
	/**
	 * 
	 */
	@Override
	public void stop() {
		
	}
	
	/**
	 * 
	 */
	@Override
	public void destroy() {
		
	}
}
