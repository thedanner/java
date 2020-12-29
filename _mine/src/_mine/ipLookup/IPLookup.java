/* IPLookup.java
 * 19 Nov 2004
 */
package _mine.ipLookup;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;

public class IPLookup {
	private static final File LOCK_FILE = new File(".ip-lock");
	
	private Clipboard clipboard;
	private String ipString;
	private String prefix;
	private String postfix;
	private Lock lock;
	
	public IPLookup() {
		this("");
	}
	
	public IPLookup(String prefix) {
		this(prefix, "");
	}
	
	public IPLookup(String prefix, String postfix) {
		this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		this.ipString = "";
		this.prefix = prefix;
		this.postfix = postfix;
		this.lock = new Lock(LOCK_FILE);
	}
	
	public String getIP() {
		if(ipString == null || ipString.length() == 0) {
			ipString = SiteList.
			/* list of usable sites */
			// fetchFrom_dyn_dns_org();
			fetchFrom_hyd_clan();
			/* end list of sites */
		}
		return ipString;
	}
	
	public String getFullString() {
		return prefix + getIP() + postfix;
	}
	
	public void toClipboard() throws IOException {
		StringSelection selection =
			new StringSelection(getFullString());
		clipboard.setContents(selection, selection);
	}
	
	public boolean isRunnable() {
		return lock.isRunnable();
	}
	
	public boolean setLock() {
		return lock.setLock();
	}
	
	public static void main(String[] args) throws IOException {
		String prefix = "";
		String postfix = "";
		
		if(args.length == 0) {
			prefix = "";
			postfix = "";
		}
		
		if(args.length == 1) {
			prefix = args[0];
			postfix = "";
		}
		
		if(args.length >= 2) {
			prefix = args[0];
			postfix = args[1];
		}
		
		IPLookup ip = new IPLookup(prefix, postfix);
		if(!ip.isRunnable()) {
			System.out.println("The lock has not yet expired.");
			System.out.println("This program cannot run; terminating.");
			return;
		}
		
		ip.toClipboard();
		System.out.println(ip.getFullString());
		ip.setLock();
	}
}
