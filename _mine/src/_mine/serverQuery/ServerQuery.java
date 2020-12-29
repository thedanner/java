package _mine.serverQuery;

import java.awt.Component;
import java.awt.FontMetrics;
import java.io.File;

import _mine.serverQuery.gui.Dialog;
import _mine.serverQuery.net.ConnectionManager;

/**
 * 
 * @author Dan
 * @version May 16, 2006
 */
public interface ServerQuery {
	String getVersion();
	
	String getServerListURL();
	
	int getColumnWidth();
	
	void setExecutablePath(String file);
	
	String chooseExecutable();
	
	File getExecutableFile();
	
	String getExecutablePath();
	
	FontMetrics getDisplayFontMetrics();
	
	void adjustMinServerIndex(int i);
	
	void setStatus(String s);
	
	String getStatus();
	
	void updateDisplay();
	
	void setupConnectToGui();
	
	ConnectionManager getConnectionManager();
	
	Dialog getDialog(); 
	
	int getServersToShow();
	
	boolean isApplet();
	
	void onLaunchAction();
	
	void connectToServer(int serverIndex);
	
	boolean showLaunchConfirmDialog();
	
	Component getGuiContext();
	
	void launchURL(String url);
	
	void serverListFound();
}
