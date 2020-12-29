/* Lock.java */
package _mine.serverQuery.application;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @author Dan
 * @version Nov 20, 2006
 */
public class Lock extends Launcher {
	public static final File LOCK_FILE =
		new File(".sq-lock");
	
	/**
	 * 
	 * @return
	 */
	public static boolean isLocked() {
		return LOCK_FILE.exists();
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean setLockAndDeleteOnExit() {
		boolean locked = setLock();
		deleteLockOnExit();
		return locked;
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean setLock() {
		try {
			if(LOCK_FILE.exists())
				return true;
			return LOCK_FILE.createNewFile();
		} catch(IOException e) { }
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean deleteLock() {
		return LOCK_FILE.delete();
	}
	
	/**
	 * 
	 *
	 */
	public static void deleteLockOnExit() {
		LOCK_FILE.deleteOnExit();
	}
}
