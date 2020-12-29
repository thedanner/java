/* Lock.java
 * 8 Oct 2005 */
package _mine.ipLookup;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Lock {
	private static final long LOCK_TIME = 10 * Date.SEC_PER_MIN;
	private File lockFile;
	
	public Lock(File lockFile) {
		this.lockFile = lockFile;
	}
	
	public boolean setLock() {
		try {
			lockFile.delete();
			lockFile.createNewFile();
			PrintWriter f = new PrintWriter(
					new FileWriter(lockFile));
			f.println(new Date().toString());
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean isRunnable() {
		try {
			Scanner s = new Scanner(lockFile);
			Date oldDate = Date.parse(s.nextLine());
			long dif = new Date().getDifference(oldDate);
			return dif - LOCK_TIME > 0;
		} catch(Exception e) {
			return true;
		}
	}
}
