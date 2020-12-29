/* Launcher.java */
package _mine.serverQuery.application;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import _mine.serverQuery.util.ExceptionHandler;
import _mine.serverQuery.util.Util;

/**
 * 
 * @author Dan
 * @version Jun 21, 2006
 */
public class Launcher {
	/**
	 * 
	 *
	 */
	private static void run() {
		checkLock();
		setupExceptionHandler();
		
		initialize();
	}
	
	/**
	 * 
	 *
	 */
	private static void checkLock() {
		if(Lock.isLocked()) {
			String message = "This program appears to be running already,\n" +
				"would you like to launch it anyway?\n" +
				"Note that launching two instances at once could cause\n" +
				"problms.  If there was a problem shutting down last\n" +
				"time, choose \"Yes.\"\n\n" +
				"If this problem persists, ensure that you can delete\n" +
				"<html><u>" + Lock.LOCK_FILE.getAbsolutePath() + "</u></html>";
			
			String title = "Confirm lauch";
			
			int result = JOptionPane.showConfirmDialog(
					null, message, title, JOptionPane.YES_NO_OPTION);
			
			if(result == JOptionPane.NO_OPTION)
			{
				System.exit(0);
			}
		}
		
		if (!Lock.setLockAndDeleteOnExit())
		{
			System.err.println("Failed to set lock.");
		}
	}
	
	/**
	 * Sets the uncaught exception handler for all threads created in the
	 * future to the "default" <tt>ExceptionHandler</tt> object.
	 * 
	 */
	private static void setupExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(
				ExceptionHandler.getInstance());
	}
	
	/**
	 * 
	 *
	 */
	private static void initialize() {
		Util.pl("Launching program.");
		
		SwingWorker<Frame, ?> worker = new SwingWorker<Frame, Object>()
		{
			private Frame frame;
			
			@Override
			protected Frame doInBackground()
			{
				frame = new Frame();
				
				return frame;
			}
			
			@Override
			protected void done()
			{
				frame.setVisible(true);
			}
		};
		
		worker.execute();
	}
	
	/** @param args not used */
	public static void main(String[] args)
	{
		run();
	}
}
