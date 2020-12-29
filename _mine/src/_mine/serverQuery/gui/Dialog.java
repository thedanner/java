/* Dialog.java */
package _mine.serverQuery.gui;

import static _mine.serverQuery.util.Html.generateDirectoryMessgae;
import static _mine.serverQuery.util.Html.generateFileDoesNotExistMessgae;
import static _mine.serverQuery.util.Html.generateJoinMessage;
import static _mine.serverQuery.util.Html.generatePlayerList;
import static javax.swing.JOptionPane.CLOSED_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_CANCEL_OPTION;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.showOptionDialog;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;

import _mine.net.sourceforge.queried.PlayerInfo;
import _mine.net.sourceforge.queried.ServerInfo;
import _mine.serverQuery.ServerQuery;
import _mine.serverQuery.Versions;
import _mine.serverQuery.net.IP;
import _mine.serverQuery.util.Launcher;
import _mine.serverQuery.util.Text;
import _mine.serverQuery.util.Util;
import _mine.serverQuery.util.properties.PropertyManager;
import _mine.serverQuery.util.properties.Vars;

/**
 * @author Dan
 * @version June 27, 2006
 */
public class Dialog
{
	/*  */
	private static final int YES_INDEX = 0;
	
	/*  */
	private static final int CHANGE_FILE_INDEX = 1;
	
	/*  */
	private static final int NO_INDEX = 2;
	
	/*  */
	private static final String[] BUTTON_NAMES = new String[] { "Launch",
			"Change executable", "Cancel" };
	
	/*
	 * a short description message stating that there is an open dialog window
	 */
	private String statusMessage;
	
	/*
	 * the parent ServerQuery object to which this object's instance is
	 * responsible to
	 */
	private ServerQuery parent;
	
	/*
	 * The gui-object version of the parent object. This is used to avoid
	 * multiple redundant casts.
	 */
	private Component parentGui;
	
	/**
	 * 
	 * @param parent
	 */
	public Dialog(ServerQuery parent)
	{
		this.parent = parent;
		this.init();
	}
	
	/**
	 *
	 *
	 */
	private void init()
	{
		this.statusMessage = "Dialog box, please wait...";
		this.parentGui = parent.getGuiContext();
	}
	
	/**
	 * 
	 * @param si
	 */
	public void joinServerAction(ServerInfo si)
	{
		if (si == null)
			return;
		
		confirmLaunchGame(si);
	}
	
	/**
	 * 
	 * @param ip
	 */
	public void joinServerAction(String ip)
	{
		String origIP = ip;
		ip = IP.getIP(origIP);
		int port = IP.getPort(origIP);
		
		confirmLaunchGame(ip, port);
	}
	
	/**
	 * 
	 * @param si
	 */
	private void confirmLaunchGame(ServerInfo si)
	{
		if (parent.showLaunchConfirmDialog())
		{
			String title = "Connect to server?";
			int optionType = YES_NO_CANCEL_OPTION;
			int messageType = QUESTION_MESSAGE;
			
			Component guiParent = (Component) parent;
			String message = generateJoinMessage(si, parent.getExecutablePath());
			
			int result = showOptionDialog(guiParent, message, title,
					optionType, messageType, null, BUTTON_NAMES,
					BUTTON_NAMES[YES_INDEX]);
			
			if (result == CHANGE_FILE_INDEX)
			{
				if (parent.chooseExecutable() != null)
					confirmLaunchGame(si);
				return;
			}
			else if (result == NO_INDEX || result == CLOSED_OPTION)
				return;
		}
		
		launchProgram(si);
	}
	
	/**
	 * 
	 * @param ip
	 * @param port
	 */
	private void confirmLaunchGame(String ip, int port)
	{
		if (parent.showLaunchConfirmDialog())
		{
			String title = "Connect to server?";
			int optionType = YES_NO_CANCEL_OPTION;
			int messageType = QUESTION_MESSAGE;
			
			Component guiParent = (Component) parent;
			String message = generateJoinMessage(ip, port, parent
					.getExecutablePath());
			
			int result = showOptionDialog(guiParent, message, title,
					optionType, messageType, null, BUTTON_NAMES,
					BUTTON_NAMES[YES_INDEX]);
			
			if (result == CHANGE_FILE_INDEX)
			{
				if (parent.chooseExecutable() != null)
					confirmLaunchGame(ip, port);
				return;
			}
			else if (result == NO_INDEX || result == CLOSED_OPTION)
				return;
		}
		launchProgram(ip, port);
	}
	
	/**
	 * 
	 * @param si
	 * @param executablePath
	 */
	private void launchProgram(ServerInfo si)
	{
		if (Util.isWindowsOS())
		{
			String message = null;
			String title = null;
			File executable = parent.getExecutableFile();
			
			if (!executable.exists())
			{
				message = generateFileDoesNotExistMessgae(executable.toString())
						+ "\nDo you want to use that file anyway?";
				title = "File doesn't exist";
			}
			else if (executable.isDirectory())
			{
				message = generateDirectoryMessgae(executable.toString())
						+ "\nDo you want to use that path anyway?";
				title = "Not a file";
			}
			
			if (message != null)
			{
				int result = showOptionDialog(parentGui, message, title,
						YES_NO_CANCEL_OPTION, WARNING_MESSAGE, null,
						BUTTON_NAMES, BUTTON_NAMES[CHANGE_FILE_INDEX]);
				
				if (result == CHANGE_FILE_INDEX)
				{
					if (parent.chooseExecutable() != null)
					{
						launchProgram(si);
					}
					
					return;
				}
				else if (result == NO_INDEX || result == CLOSED_OPTION)
				{
					return;
				}
			}
		}
		
		launchProgram(si.getIp(), si.getPortInt());
	}
	
	/**
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	private boolean launchProgram(String ip, int port)
	{
		try
		{
			Launcher.launchProgram(ip, port, parent.getExecutablePath());
			parent.onLaunchAction();
			return true;
		}
		catch (IOException e)
		{
			showMessageDialog(parentGui,
					"An error has occured while trying to launch the "
							+ "program:\n" + e.getMessage() + ".\n\n"
							+ "Make sure you are using the correct executable.");
		}
		return false;
	}
	
	/**
	 * 
	 * @param serverAddrs
	 * @param servers
	 * @param players
	 */
	public void listPlayers(String[] serverAddrs, ServerInfo[] servers,
			List<PlayerInfo>[] players)
	{
		if (serverAddrs == null)
		{
			return;
		}
		
		String list = generatePlayerList(serverAddrs, servers, players);
		
		if (list != null)
		{
			TextPane.showText(parent, list, "Current Players");
		}
	}
	
	/**
	 * 
	 *
	 */
	public void showPathAction()
	{
		parent.chooseExecutable();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getStatusMessage()
	{
		return statusMessage;
	}
	
	/**
	 * 
	 * @param parent
	 */
	public static void showHelpDialog(ServerQuery parent)
	{
		String url = PropertyManager.getInstance(parent).get(
				Vars.KEY_HELP_TEXT_URL, Vars.VALUE_HELP_TEXT_URL);
		TextPane.showPage(parent, url, Text.HELP_BOX_TITLE);
	}
	
	/**
	 * 
	 * @param parent
	 * @param ver
	 */
	public static void showAboutDialog(ServerQuery parent, String ver)
	{
		String title = Text.ABOUT_BOX_TITLE + ", Version " + ver + ", core "
				+ Versions.CORE;
		String url = PropertyManager.getInstance(parent).get(
				Vars.KEY_ABOUT_TEXT_URL, Vars.VALUE_ABOUT_TEXT_URL);
		TextPane.showPage(parent, url, title);
	}
}
