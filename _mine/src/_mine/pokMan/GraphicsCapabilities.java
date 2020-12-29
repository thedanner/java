package _mine.pokMan;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class GraphicsCapabilities {
	private static GraphicsDevice myDevice = null;
	// supported
	private static boolean fsSupportedChecked = false;
	private static boolean fsSupported = false;
	// allowed
	private static boolean fsAllowedChecked = false;
	private static boolean fsAllowed = false;
	
	public static boolean isFullScreenSupported() {
		if(!fsSupportedChecked) {
			if(myDevice == null)
				initMyDevice();
			
			fsSupported = myDevice.isFullScreenSupported();
			fsSupportedChecked = true;
		}
		return fsSupported; 
	}
	
	public static boolean isFullScreenAllowed() {
		if(!fsAllowedChecked) {
			try {
				System.getSecurityManager().checkPermission(
						new java.awt.AWTPermission("fullScreenWindow"));
				fsAllowed = true;
			} catch(SecurityException e) {
				fsAllowed = false;
			}
			fsAllowedChecked = true;
		}
		return fsAllowed;
	}
	
	public static boolean isDisplayChangeSupported() {
		if(myDevice == null)
			initMyDevice();
		return myDevice.isDisplayChangeSupported();
	}
	
	public static GraphicsDevice getScreenDevice() {
		GraphicsEnvironment ge = GraphicsEnvironment.
		getLocalGraphicsEnvironment();
		return ge.getDefaultScreenDevice();
	}
	
	private static void initMyDevice() {
		myDevice = getScreenDevice();
	}
}
