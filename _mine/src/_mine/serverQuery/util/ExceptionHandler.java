/* ExceptionHandler.java */
package _mine.serverQuery.util;

import static _mine.serverQuery.util.Html.HTML1;
import static _mine.serverQuery.util.Html.HTML2;
import static _mine.serverQuery.util.Html.I1;
import static _mine.serverQuery.util.Html.I2;
import static java.util.Calendar.AM;
import static java.util.Calendar.AM_PM;
import static java.util.Calendar.DATE;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JOptionPane;

/**
 * 
 * @author Dan
 * @version Jun 21, 2006
 */
public class ExceptionHandler implements UncaughtExceptionHandler {
	/* the instance of this exception handler to use */
	private static ExceptionHandler instance;
	
	/* A list of exceptiont that have been seen and acknowledged
	 * so that they won't be seen if they are repeated.
	 * This list is created freshly every run. */
	private ArrayList<String> seenExceptions;
	
	/*  */
	private boolean enabled;
	
	/**
	 * 
	 *
	 */
	public ExceptionHandler() {
		enabled = true;
		seenExceptions = new ArrayList<String>();
	}
	
	/**
	 * 
	 */
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		// if a "bad" Throwable was passed, return immediatey
		if(t == null || e == null)
			return;
		
		// special case
		if(e instanceof ThreadDeath)
			throw (ThreadDeath)e;
		
		// blank lines for visibility
		pl();
		pl();
		
		// always print the stack trace
		pl("Exception raised (" + getCurrentTime() + ")");
		e.printStackTrace(); // output to System.err
		
		// if disabled, don't show a pop up
		if(!isEnabled()) {
			pl("  --  (thrown while the exception handler was disabled)");
			return;
		}
		
		pl();
		
		// if an exception with the same message was already seen, don't popup
		boolean seen = alreadySeen(e);
		if(seen) {
			pl("Ignoring exception: " + e);
			return;
		}
		
		// text to use on buttons
		int hideIndex = 1;
		String[] options = {
				"OK", "Don't show this error again"
		};
		
		String outputMessage =
			"The stack trace has been printed to standard error.";
		
		// the message to show the user with HTML formatting
		String message =
			HTML1 + I1 +
			"Error:" +
			I2 + HTML2 +
			"\n" + e + "\n\n" + // the actual error messgae
			HTML1 + I1 +
			outputMessage +
			I2 + HTML2;
		
		// show a dialog about the exception
		int result = JOptionPane.showOptionDialog(
				null,
				message,
				"Error: \"" + e.getMessage() + "\"",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null,
				options,
				options[0]);
		
		// add it to the list if the user doesn't want to be warned again
		if(result == hideIndex)
			seenExceptions.add(e.getMessage());
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * 
	 * @param newState
	 */
	public void setEnabled(boolean newState) {
		enabled = newState;
	}
	
	/**
	 * Prints the given String to System.err and a log file.
	 * @param x the String to print
	 */
	public void pl(String x) {
		System.err.println(x);
	}
	
	/**
	 * Prints a newline to System.err and a log file.
	 */
	public void pl() {
		System.err.println();
	}
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	private boolean alreadySeen(Throwable e) {
		for(int i = 0; i < seenExceptions.size(); i++)
			if(seenExceptions.get(i).equals(e.getMessage()))
				return true;
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	private String getCurrentTime() {
		StringBuffer timeStr = new StringBuffer(25);
		Calendar now = Calendar.getInstance(); // current time
		
		// construct date
		timeStr.append(now.get(YEAR) + "-");
		timeStr.append(castAndPad(now.get(MONTH)) + "-");
		timeStr.append(castAndPad(now.get(DATE)) + " ");
		
		// construct time
		timeStr.append(castAndPad(now.get(HOUR)) + ":");
		timeStr.append(castAndPad(now.get(MINUTE)) + ":");
		timeStr.append(castAndPad(now.get(SECOND)) + ".");
		timeStr.append(castAndPad(now.get(Calendar.MILLISECOND), 3, '0') + " ");
		timeStr.append( now.get(AM_PM) == AM ? "AM" : "PM" );
		
		return timeStr.toString();
	}
	
	private String castAndPad(int number) {
		return castAndPad(number, 2, '0');
	}
	
	private String castAndPad(int number, int length, char pad) {
		StringBuffer buf = new StringBuffer(length + 4);
		buf.append(number+"");
		
		while(buf.length() < length)
			buf.insert(0, pad);
		
		return buf.toString();
	}
	
	public static void destroy() {
		instance = null;
	}
	
	/**
	 * 
	 * @return
	 */
	public static ExceptionHandler getInstance() {
		if(instance == null)
			instance = new ExceptionHandler();
		return instance;
	}
}
