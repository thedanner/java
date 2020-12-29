/* MenuBar.java */
package _mine.serverQuery.gui.application;

import static _mine.serverQuery.gui.ComponentFactory.createMenu;
import static _mine.serverQuery.gui.ComponentFactory.createMenuItem;
import static _mine.serverQuery.gui.ComponentFactory.createURLMenuItem;
import static java.awt.event.KeyEvent.VK_1;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_COMMA;
import static java.awt.event.KeyEvent.VK_EQUALS;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_I;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_M;
import static java.awt.event.KeyEvent.VK_MINUS;
import static java.awt.event.KeyEvent.VK_PERIOD;
import static java.awt.event.KeyEvent.VK_Q;
import static java.awt.event.KeyEvent.VK_S;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import _mine.net.sourceforge.queried.ServerInfo;
import _mine.serverQuery.application.Frame;
import _mine.serverQuery.gui.Dialog;
import _mine.serverQuery.net.ConnectionManager;
import _mine.serverQuery.util.properties.PropertyManager;
import _mine.serverQuery.util.properties.Vars;

/**
 * This class serves as the <tt>JMenuBar<tt> instance for the application
 * version of the <tt>ServerQuery</tt> program.
 * @author Dan
 * @version Jun 21, 2006
 */
public class MenuBar extends JMenuBar implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4163838517318787728L;
	
	// general instance fields
	private PropertyManager prefs;
	private Frame parent;
	private Dialog dialog;
	private ConnectionManager cm;
	// file menu
	private JMenu fileMenu;
	private JMenuItem settingsMenuItem;
	private JMenuItem miniminzeMenuItem;
	private JMenuItem quitMenuItem;
	// actions menu
	private JMenu actionMenu;
	private JMenuItem adjustMinServerLeftActionMenuItem;
	private JMenuItem adjustMinServerRightActionMenuItem;
	private JMenuItem listPlayersMenuItem;
	private JMenuItem showExecutablePathMenuItem;
	// connect to menu
	private JMenu connectMenu;
	private JMenuItem[] connectMenuItems;
	// scroll buttons
	private JMenuItem adjustMinServerLeftMenuItem;
	private JMenuItem adjustMinServerRightMenuItem;
	// help menu
	private JMenu helpMenu;
	// links
	private JMenuItem clanSiteMenuItem;
	private JMenuItem clanForumMenuItem;
	private JMenuItem infoSiteMenuItem;
	private JMenuItem homeSiteMenuItem;
	// help and about
	private JMenuItem helpMenuItem;
	private JMenuItem aboutMenuItem;
	
	/**
	 * 
	 * @param parent
	 */
	public MenuBar(Frame parent, Dialog dialog,
			ConnectionManager cm, PropertyManager properties) {
		super();
		this.parent = parent;
		this.dialog = dialog;
		this.cm = cm;
		this.prefs = properties;
		
		init();
	}
	
	/**
	 * 
	 *
	 */
	private void init() {
		createFileMenu();
		createConnectMenu();
		createActionMenu();
		createScrollMenuItems();
		
		// adds spacing to help is on the right
		add(Box.createHorizontalGlue());
		
		createHelpMenu();
	}
	
	/**
	 * 
	 *
	 */
	private void createFileMenu() {
		fileMenu = createMenu(VK_F, "File");
		
		settingsMenuItem = createMenuItem(VK_S, "Settings...", this);
		fileMenu.add(settingsMenuItem);
		
		fileMenu.addSeparator();
		
		miniminzeMenuItem = createMenuItem(VK_M, "Minimize", this);
		fileMenu.add(miniminzeMenuItem);
		
		quitMenuItem = createMenuItem(VK_Q, "Quit", this);
		fileMenu.add(quitMenuItem);
		
		add(fileMenu);
	}
	
	/**
	 * 
	 *
	 */
	private void createConnectMenu() {
		connectMenu = createMenu(VK_C, "Connect");
		add(connectMenu);
	}
	
	/**
	 * 
	 *
	 */
	private void createActionMenu() {
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				adjustMinServerIndexAction((JMenuItem)e.getSource());
			}
		};
		actionMenu = createMenu(VK_A, "Actions");
		
		adjustMinServerLeftActionMenuItem = createMenuItem(
				VK_COMMA,
				"Scroll Servers Left  <---",
				al);
		adjustMinServerRightActionMenuItem = createMenuItem(
				VK_PERIOD,
				"Scroll Servers Right  --->",
				al);
		
		actionMenu.add(adjustMinServerRightActionMenuItem);
		actionMenu.add(adjustMinServerLeftActionMenuItem);
		
		//actionMenu.addSeparator();
		
		listPlayersMenuItem = createMenuItem(VK_L, "List players", 
				"Lists the players in a separate dialog box.  This is used to" +
				"see which color codes need tweaking.", this);
		actionMenu.add(listPlayersMenuItem);
		
		showExecutablePathMenuItem = createMenuItem(VK_S,
				"Show Enemy Territory executable", this);
		actionMenu.add(showExecutablePathMenuItem);
		
		add(actionMenu);
	}
	
	/**
	 * 
	 *
	 */
	private void createScrollMenuItems() {
		String toolTip = "Only usable is some servers are hidden.";
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				adjustMinServerIndexAction((JMenuItem)e.getSource());
			}
		};
		
		adjustMinServerLeftMenuItem = createMenuItem(VK_EQUALS,
				"Scroll Servers " +
				"<---", al);
		//adjustMinServerUpMenuItem.addActionListener(al);
		adjustMinServerLeftMenuItem.setToolTipText(toolTip);
		
		adjustMinServerRightMenuItem = createMenuItem(VK_MINUS,
				"--->", al);
		//adjustMinServerDownMenuItem.addActionListener(al);
		adjustMinServerRightMenuItem.setToolTipText(toolTip);
		
		add(adjustMinServerLeftMenuItem);
		add(adjustMinServerRightMenuItem);
		
		if(parent.getServersToShow() <= 1)
			hideScrollButtons();
	}
	
	/**
	 * 
	 *
	 */
	private void createHelpMenu() {
		String text = "Help";
		helpMenu = createMenu(VK_H, text);
		
		addLinks(helpMenu);
		
		helpMenu.addSeparator();
		
		text = "Help...";
		helpMenuItem = createMenuItem(VK_H, text, this);
		helpMenu.add(helpMenuItem);
		
		text = "About...";
		aboutMenuItem = createMenuItem(VK_A, text, this);
		helpMenu.add(aboutMenuItem);
		
		add(helpMenu);
	}
	
	/**
	 * 
	 *
	 */
	private void addLinks(JMenu target) {
		String key = "";
		String defn = "";
		String url = "";
		
		// forum launcher
		key = Vars.KEY_HYD_CLAN_FORUMS_URL;
		defn = Vars.VALUE_HYD_CLAN_FORUMS_URL;
		url = prefs.get(key, defn);
		clanForumMenuItem = createURLMenuItem(VK_F, "HYD Clan Forums", url);
		target.add(clanForumMenuItem);
		
		// clan site launcher
		key = Vars.KEY_HYD_CLAN_URL;
		defn = Vars.VALUE_HYD_CLAN_URL;
		url = prefs.get(key, defn);
		clanSiteMenuItem = createURLMenuItem(VK_C, "HYD Clan", url);
		target.add(clanSiteMenuItem);
		
		// info page launcher
		key = Vars.KEY_INFO_PAGE_URL;
		defn = Vars.VALUE_INFO_PAGE_URL;
		url = prefs.get(key, defn);
		infoSiteMenuItem = createURLMenuItem(VK_I, "Program Information", url);
		target.add(infoSiteMenuItem);
		
		// "home site" launcher
		key = Vars.KEY_HOME_PAGE_URL;
		defn = Vars.VALUE_HOME_PAGE_URL;
		url = prefs.get(key, defn);
		homeSiteMenuItem = createURLMenuItem(VK_H, "Project Home", url);
		target.add(homeSiteMenuItem);
	}
	
	/**
	 * 
	 *
	 */
	private JMenuItem[] createConnectMenuItems() {
		if(checkVariables()) {
			int numServers = cm.getServerCount();
			JMenuItem[] items = new JMenuItem[numServers];
			
			for(int i = 0; i < items.length; i++) {
				int mn = VK_1 + i;
				String title = "Server #" + (i + 1);
				String toolTip = cm.getServerIP(i);
				
				final int n = i;
				ActionListener l = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						connectMenuButtonAction(n);
					}
				};
				items[i] = createMenuItem(mn, title, toolTip, l);
			}
			return items;
		}
		return null;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if(src == settingsMenuItem){
			settingsAction();
		} else if(src == listPlayersMenuItem) {
			listPlayersAction();
		} else if(src == miniminzeMenuItem) {
			 minimizeAction();
		} else if(src == quitMenuItem) {
			quitAction();
		} else if(src == showExecutablePathMenuItem) {
			showExecutablePathAction();
		} else if(src == helpMenuItem) {
			helpMenuItemAction();
		} else if(src == aboutMenuItem) {
			aboutMenuItemAction();
		}
	}
	
	/**
	 * 
	 *
	 */
	private void addConnectMenuItems() {
		if(connectMenu != null) {
			connectMenu.removeAll();
			
			for(int i = 0; i < connectMenuItems.length; i++)
				connectMenu.add(connectMenuItems[i]);
		}
	}
	
	/**
	 * 
	 * @param src
	 */
	private void adjustMinServerIndexAction(JMenuItem src) {
		int i = 0;
		if(src == adjustMinServerLeftMenuItem ||
				src == adjustMinServerLeftActionMenuItem ) i--;
		else if(src == adjustMinServerRightMenuItem ||
				src == adjustMinServerRightActionMenuItem) i++;
		else return;
		
		parent.adjustMinServerIndex(i);
	}
	
	/**
	 * 
	 *
	 */
	private void helpMenuItemAction() {
		Dialog.showHelpDialog(parent);
	}
	
	/**
	 * 
	 *
	 */
	private void aboutMenuItemAction() {
		Dialog.showAboutDialog(parent, parent.getVersion());
	}
	
	/**
	 * 
	 *
	 */
	private void listPlayersAction() {
		if(checkVariables())
			dialog.listPlayers(cm.getServerIPs(),
					cm.getServers(), cm.getPlayers());
	}
	
	/**
	 * 
	 *
	 */
	private void settingsAction() {
		parent.showSettingsWindow();
	}
	
	/**
	 * 
	 *
	 */
	private void minimizeAction() {
		parent.iconify();
	}
	
	/**
	 * 
	 *
	 */
	private void quitAction() {
		parent.quitApplication();
	}
	
	/**
	 * 
	 *
	 */
	private void showExecutablePathAction() {
		if(checkVariables())
			dialog.showPathAction();
	}
	
	/**
	 * 
	 *
	 */
	private void connectMenuButtonAction(int n) {
		if(checkVariables()) {
			ServerInfo si = cm.servers(n);
			if(si != null)
				dialog.joinServerAction(si);
			else {
				String ip = cm.getServerIP(n);
				if(ip != null)
					dialog.joinServerAction(ip);
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getScrollButtonsVisible() {
		try {
			return adjustMinServerLeftMenuItem.isVisible() &&
				adjustMinServerRightMenuItem.isVisible();
		} catch(NullPointerException e) {
			return false;
		}
	}
	
	/**
	 * 
	 *
	 */
	public void toggleScrollButtonsVisible() {
		boolean currentState = getScrollButtonsVisible();
		setScrollButtonsVisible(!currentState);
	}
	
	/**
	 * 
	 * @param newState
	 */
	public void setScrollButtonsVisible(boolean newState) {
		try {
			adjustMinServerLeftMenuItem.setVisible(newState);
			adjustMinServerRightMenuItem.setVisible(newState);
			adjustMinServerLeftActionMenuItem.setVisible(newState);
			adjustMinServerRightActionMenuItem.setVisible(newState);
		} catch(NullPointerException e) { } // nothing to do
	}
	
	/**
	 * 
	 *
	 */
	public void showScrollButtons() {
		setScrollButtonsVisible(true);
	}
	
	/**
	 * 
	 *
	 */
	public void hideScrollButtons() {
		setScrollButtonsVisible(false);
	}
	
	/**
	 * 
	 *
	 */
	public void setupConnectToMenu() {
		connectMenuItems = createConnectMenuItems();
		
		addConnectMenuItems();
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkVariables() {
		if(cm == null)
			cm = parent.getConnectionManager();
		if(dialog == null)
			dialog = parent.getDialog();
		
		return cm != null && dialog != null;
	}
}
