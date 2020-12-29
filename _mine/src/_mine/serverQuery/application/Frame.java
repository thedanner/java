package _mine.serverQuery.application;

import static _mine.serverQuery.util.CommonMethods.c_connectToServer;
import static _mine.serverQuery.util.CommonMethods.c_setupExecutablePaths;
import static _mine.serverQuery.util.properties.Vars.KEY_FRAME_HEIGHT;
import static _mine.serverQuery.util.properties.Vars.KEY_FRAME_WIDTH;
import static _mine.serverQuery.util.properties.Vars.KEY_LNF;
import static _mine.serverQuery.util.properties.Vars.KEY_ON_LAUNCH_ACTION;
import static _mine.serverQuery.util.properties.Vars.KEY_PATH_TO_EXECUTABLE;
import static _mine.serverQuery.util.properties.Vars.KEY_SERVERS_TO_SHOW;
import static _mine.serverQuery.util.properties.Vars.KEY_SERVER_LIST_URL;
import static _mine.serverQuery.util.properties.Vars.KEY_SHOW_CONFIRM_DIALOG;
import static _mine.serverQuery.util.properties.Vars.KEY_X_COORD;
import static _mine.serverQuery.util.properties.Vars.KEY_Y_COORD;
import static _mine.serverQuery.util.properties.Vars.MAXIMUM_SERVERS_TO_SHOW;
import static _mine.serverQuery.util.properties.Vars.MINIMUM_SERVERS_TO_SHOW;
import static _mine.serverQuery.util.properties.Vars.VALUE_FRAME_HEIGHT;
import static _mine.serverQuery.util.properties.Vars.VALUE_FRAME_WIDTH;
import static _mine.serverQuery.util.properties.Vars.VALUE_LNF;
import static _mine.serverQuery.util.properties.Vars.VALUE_ON_LAUNCH_ACTION;
import static _mine.serverQuery.util.properties.Vars.VALUE_SERVERS_TO_SHOW;
import static _mine.serverQuery.util.properties.Vars.VALUE_SERVER_LIST_URL;
import static _mine.serverQuery.util.properties.Vars.VALUE_SHOW_CONFIRM_DIALOG;
import static _mine.serverQuery.util.properties.Vars.VALUE_X_COORD;
import static _mine.serverQuery.util.properties.Vars.VALUE_Y_COORD;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import _mine.serverQuery.ServerQuery;
import _mine.serverQuery.Versions;
import _mine.serverQuery.gui.Dialog;
import _mine.serverQuery.gui.DisplayArea;
import _mine.serverQuery.gui.PathDialog;
import _mine.serverQuery.gui.application.MenuBar;
import _mine.serverQuery.gui.application.SettingsWindow;
import _mine.serverQuery.net.ConnectionManager;
import _mine.serverQuery.util.DragMover;
import _mine.serverQuery.util.Keyboard;
import _mine.serverQuery.util.Launcher;
import _mine.serverQuery.util.Text;
import _mine.serverQuery.util.Util;
import _mine.serverQuery.util.properties.PropertyManager;

/**
 * This is the top level window for the application version of the
 * <tt>ServerQuery</tt> program.
 * 
 * @author Dan
 * @version Jun 25, 2006
 */
public class Frame extends JFrame implements ServerQuery
{
	private static final long serialVersionUID = -4345168748402038030L;
	
	/* the preferences for this program */
	private PropertyManager properties;
	
	/* the width of the window */
	private int width;
	
	/* the height of the window */
	private int height;
	
	private int xCoord;
	
	private int yCoord;
	
	/* The maximum number of servers to show at one time. */
	private int serversToShow;
	
	/*
	 * This applet's ConnectionManager object, which handles remote connections
	 * (to fetch the server list, update info about servers, etc.).
	 */
	private ConnectionManager cm;
	
	/* The keyboard listener object, which handles key events. */
	private Keyboard keyboard;
	
	/*
	 * A File object that points to the file ET.exe. Initialized to default ET
	 * install path.
	 */
	private String pathToExecutable;
	
	/* The object responsible for most GUI operations */
	private Dialog dialog;
	
	/*
	 * A String containing a message about the Applet's current status.
	 */
	private String status;
	
	private int onLaunchAction;
	
	private boolean showConfirmDialog;
	
	// gui objects
	/* the Canvas extension that displays info about the servers. */
	private DisplayArea display;
	
	/*  */
	private String serverListURL;
	
	private DragMover dragger;
	
	// GUI objects
	/*  */
	private MenuBar menuBar;
	
	/*  */
	private SettingsWindow settingsWindow;
	
	// ************************
	// END VARIABLE DECLARATION
	// ************************
	
	/**
	 * 
	 */
	public Frame()
	{
		super();
		init();
	}
	
	/**
	 * 
	 *
	 */
	private void init()
	{
		// variable init
		this.properties = new PropertyManager(this,
				PropertyManager.PROPERTIES_FILE);
		
		this.serverListURL = properties.get(KEY_SERVER_LIST_URL,
				VALUE_SERVER_LIST_URL);
		this.width = properties.getInt(KEY_FRAME_WIDTH, VALUE_FRAME_WIDTH);
		this.height = properties.getInt(KEY_FRAME_HEIGHT, VALUE_FRAME_HEIGHT);
		this.xCoord = properties.getInt(KEY_X_COORD, VALUE_X_COORD);
		this.yCoord = properties.getInt(KEY_Y_COORD, VALUE_Y_COORD);
		this.serversToShow = properties.getInt(KEY_SERVERS_TO_SHOW,
				VALUE_SERVERS_TO_SHOW);
		this.pathToExecutable = properties.get(KEY_PATH_TO_EXECUTABLE, "");
		this.onLaunchAction = properties.getInt(KEY_ON_LAUNCH_ACTION,
				VALUE_ON_LAUNCH_ACTION);
		this.showConfirmDialog = properties.getBoolean(KEY_SHOW_CONFIRM_DIALOG,
				VALUE_SHOW_CONFIRM_DIALOG);
		
		this.status = "";
		// end var init
		
		// changes the look and feel of the program
		initLNF();
		
		// init other components
		initGui();
		initComponents();
		
		setupBounds();
	}
	
	/**
	 * 
	 *
	 */
	private void initLNF()
	{
		try
		{
			UIManager.setLookAndFeel(properties.get(KEY_LNF, VALUE_LNF));
		}
		catch (Exception e)
		{
			System.err.println("Error setting the look and feel (" + e + ")");
		}
	}
	
	/**
	 * 
	 *
	 */
	private void initGui()
	{
		setBackground(Text.BG_COLOR);
		getContentPane().setBackground(Text.BG_COLOR);
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("HYD Clan Servers");
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent evt)
			{
				quitApplication();
			}
			
			@Override
			public void windowIconified(WindowEvent evt)
			{
				iconify();
			}
		});
	}
	
	/**
	 * Repositions the the top level frame based on the current values of xCoord
	 * and yCoord. The top level window is placed such that the point (xCoord,
	 * yCoord) is repositioned to allow the entire window to apprear on screen
	 * (aside from being partially hidden by always on top windows).
	 */
	private void setupBounds()
	{
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice dev = ge.getDefaultScreenDevice();
		
		int w = dev.getDisplayMode().getWidth();
		int h = dev.getDisplayMode().getHeight();
		
		int maxX = w - width;
		int maxY = h - height;
		
		xCoord = Math.max(xCoord, 0);
		yCoord = Math.max(yCoord, 0);
		
		xCoord = Math.min(xCoord, maxX);
		yCoord = Math.min(yCoord, maxY);
		
		setBounds(xCoord, yCoord, width, height);
	}
	
	/**
	 * 
	 *
	 */
	private void initComponents()
	{
		final Frame parent = this;
		
		SwingWorker<?, ?> worker = new SwingWorker<Object, Object>()
		{
			@Override
			protected Object doInBackground() throws Exception
			{
				dragger = new DragMover(parent);
				dialog = new Dialog(parent);
				cm = new ConnectionManager(parent, properties);
				display = new DisplayArea(parent, properties);
				menuBar = new MenuBar(parent, dialog, cm, properties);
				settingsWindow = new SettingsWindow(parent, properties);
				keyboard = new Keyboard(parent, parent.getDialog(),
						parent.getConnectionManager());
				
				return null;
			}
			
			@Override
			protected void done()
			{
				dragger.addToComp(display);
				dragger.addToComp(menuBar);
				
				addKeyListener(keyboard);
				
				setJMenuBar(menuBar);
				
				setupConnectToGui();
				setScrollButtonsVisibile();
				setServersToShow(getServersToShow());
				getContentPane().add(display);
				
				display.start();
			}
		};
		
		worker.execute();
	}
	
	/**
	 * 
	 *
	 */
	public synchronized void quitApplication()
	{
		Util.pl("Closing, saving settings.");
		
		// cleanup with the other objects
		if (display != null)
			display.stop();
		
		// save the preferences
		saveSettings();
		// removes the app lock
		Lock.deleteLock();
		
		// terminates the VM
		Util.pl("Program has quit, task is done.");
		System.exit(0);
	}
	
	/**
	 * 
	 *
	 */
	public synchronized void saveSettings()
	{
		try
		{
			// saves vars for this object
			properties.putInt(KEY_FRAME_WIDTH, getWidth());
			properties.putInt(KEY_FRAME_HEIGHT, getHeight());
			properties.putInt(KEY_X_COORD, getX());
			properties.putInt(KEY_Y_COORD, getY());
			properties.putInt(KEY_SERVERS_TO_SHOW, getServersToShow());
			properties.put(KEY_PATH_TO_EXECUTABLE, getExecutablePath());
			properties.put(KEY_SERVER_LIST_URL, getServerListURL());
			properties.putInt(KEY_ON_LAUNCH_ACTION, getOnLaunchAction());
			properties.putBoolean(KEY_SHOW_CONFIRM_DIALOG,
					getShowConfirmDialog());
			
			// connection manager
			cm.syncSettings();
			Text.syncSettings(properties);
			
			// finally, store everything
			properties.store();
		}
		catch (Exception e)
		{
			System.err.println("Error saving settings: ");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 *
	 */
	public void iconify()
	{
		setExtendedState(JFrame.ICONIFIED);
	}
	
	/**
	 * 
	 *
	 */
	private void setupExecutablePaths()
	{
		c_setupExecutablePaths(this);
	}
	
	/**
	 * 
	 * @param n
	 */
	public void setScrollButtonsVisibile()
	{
		if (menuBar != null)
		{
			if (getServersToShow() <= 1 || cm.getServerCount() <= 1)
				menuBar.hideScrollButtons();
			else
				menuBar.showScrollButtons();
		}
	}
	
	/**
	 * 
	 * 
	 */
	@Override
	public void setStatus(String s)
	{
		status = s;
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public String getStatus()
	{
		return status == null ? "" : status;
	}
	
	/**
	 * 
	 */
	@Override
	public int getColumnWidth()
	{
		return display.getWidthDif();
	}
	
	/**
	 * 
	 */
	@Override
	public synchronized String getServerListURL()
	{
		URL url = null;
		try
		{
			url = new URL(serverListURL);
			return url.toString();
		}
		catch (Exception e)
		{
			return VALUE_SERVER_LIST_URL;
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void setExecutablePath(String f)
	{
		pathToExecutable = f;
		properties.put(KEY_PATH_TO_EXECUTABLE, getExecutablePath());
		
		if (settingsWindow != null)
		{
			settingsWindow.setExecutablePath(f);
		}
	}
	
	@Override
	public File getExecutableFile()
	{
		if (getExecutablePath() == null)
		{
			return null;
		}
		
		return new File(getExecutablePath());
	}
	
	/**
	 * 
	 */
	@Override
	public String getExecutablePath()
	{
		if (pathToExecutable == null
				|| pathToExecutable.toString().length() == 0)
			setupExecutablePaths();
		return pathToExecutable;
	}
	
	/**
	 * 
	 * 
	 */
	public void setServersToShow(int n)
	{
		if (n == 0)
			return;
		if (n < MINIMUM_SERVERS_TO_SHOW)
			n = MINIMUM_SERVERS_TO_SHOW;
		if (n > MAXIMUM_SERVERS_TO_SHOW)
			n = MAXIMUM_SERVERS_TO_SHOW;
		
		serversToShow = n;
		int newWidth = DisplayArea.BASE_X + n * display.getWidthDif();
		
		setSize(newWidth, getHeight());
		display.updateServersToShow();
		
		setScrollButtonsVisibile();
		
		validate();
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public int getServersToShow()
	{
		return serversToShow;
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public Dialog getDialog()
	{
		return dialog;
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public ConnectionManager getConnectionManager()
	{
		return cm;
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public FontMetrics getDisplayFontMetrics()
	{
		return display.getFontMetrics();
	}
	
	/**
	 * 
	 */
	@Override
	public String getVersion()
	{
		return Versions.APPLICATION;
	}
	
	/**
	 * 
	 */
	@Override
	public void adjustMinServerIndex(int n)
	{
		display.adjustMinServerIndex(n);
		updateDisplay();
	}
	
	/**
	 * 
	 */
	@Override
	public void updateDisplay()
	{
		if (display != null)
			display.updateImage();
	}
	
	/**
	 * 
	 *
	 */
	public void showSettingsWindow()
	{
		if (settingsWindow != null)
			settingsWindow.showWindow();
	}
	
	/**
	 * 
	 * @param id
	 */
	public void setOnCloseAction(int index)
	{
		onLaunchAction = index;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getOnLaunchAction()
	{
		return onLaunchAction;
	}
	
	/**
	 * 
	 *
	 */
	@Override
	public void onLaunchAction()
	{
		switch (onLaunchAction)
		{
		case SettingsWindow.ON_LAUNCH_MINIMIZE:
			iconify();
			break;
		case SettingsWindow.ON_LAUNCH_QUIT:
			quitApplication();
			break;
		}
	}
	
	/**
	 * 
	 * @param newState
	 */
	public void setShowConfirmDialog(boolean newState)
	{
		showConfirmDialog = newState;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getShowConfirmDialog()
	{
		return showConfirmDialog;
	}
	
	/**
	 * 
	 */
	@Override
	public void setupConnectToGui()
	{
		if (menuBar != null)
			menuBar.setupConnectToMenu();
	}
	
	/**
	 * 
	 */
	@Override
	public boolean isApplet()
	{
		return false;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean showLaunchConfirmDialog()
	{
		return showConfirmDialog;
	}
	
	/**
	 * 
	 */
	public void toggleControlFrame()
	{
	} // do nothing
	
	/**
	 * 
	 */
	@Override
	public void connectToServer(int serverIndex)
	{
		c_connectToServer(serverIndex, cm, dialog);
	}
	
	/**
	 * 
	 */
	@Override
	public Component getGuiContext()
	{
		return this;
	}
	
	/**
	 * 
	 */
	@Override
	public void launchURL(String url)
	{
		Launcher.launchURL(url);
	}
	
	/**
	 * 
	 */
	@Override
	public String chooseExecutable()
	{
		String path = PathDialog.getExecutablePath(this);
		if (path != null)
			setExecutablePath(path);
		
		return path;
	}
	
	@Override
	public void serverListFound()
	{
		setupConnectToGui();
	}
}
