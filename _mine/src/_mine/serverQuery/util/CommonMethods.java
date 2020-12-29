/* CommonMethods.java */
package _mine.serverQuery.util;

import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import _mine.net.sourceforge.queried.ServerInfo;
import _mine.serverQuery.ServerQuery;
import _mine.serverQuery.gui.Dialog;
import _mine.serverQuery.net.ConnectionManager;

/**
 * 
 * @author Dan
 * @version Mar 8, 2006
 */
public class CommonMethods {
	/**
	 * 
	 */
	public static void c_connectToServer(int serverIndex,
			ConnectionManager cm, Dialog dialog) {
		ServerInfo si = cm.servers(serverIndex);
		if(si == null) {
			String ip = cm.getServerIP(serverIndex);
			if(ip != null)
				dialog.joinServerAction(ip);
		} else // if si isn't not null
			dialog.joinServerAction(si);
	}
	
	/**
	 * 
	 *
	 */
	public static void c_setupExecutablePaths(ServerQuery parent) {
		String win32 = "C:\\Progra~1\\Wolfen~1\\ET.exe";
		String linux = "et";
		String other = "et";
		
		if(Util.isWindowsOS())
			parent.setExecutablePath(win32);
		else if(Util.isLinuxOS())
			parent.setExecutablePath(linux);
		else parent.setExecutablePath(other);
	}
	
	/**
	 * 
	 * @param current
	 * @param parent
	 */
	public static void c_setupLocation(Component comp, Component parent) {
		GraphicsEnvironment ge =
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice dev = ge.getDefaultScreenDevice();
		
		int x = parent.getX();
		int y = parent.getY();
		
		int w = dev.getDisplayMode().getWidth();
		int h = dev.getDisplayMode().getHeight();
		
		int maxX = w - comp.getWidth();
		int maxY = h - comp.getHeight();
		
		int midX = parent.getWidth() / 2;
		int midY = parent.getHeight() / 2;
		
		x += midX - comp.getWidth() / 2;
		y += midY - comp.getHeight() / 2;
		
		x = Math.max(x, 0);
		y = Math.max(y, 0);
		
		x = Math.min(x, maxX);
		y = Math.min(y, maxY);
		
		comp.setLocation(x, y);
	}
}
