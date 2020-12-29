/* Main.java */
package _mine.serverQuery.applet;

import static _mine.serverQuery.util.CommonMethods.c_connectToServer;
import static _mine.serverQuery.util.CommonMethods.c_setupExecutablePaths;

import java.applet.Applet;
import java.awt.Component;
import java.awt.FontMetrics;
import java.io.File;
import java.io.FilePermission;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;
import java.util.PropertyPermission;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import _mine.serverQuery.ServerQuery;
import _mine.serverQuery.Versions;
import _mine.serverQuery.gui.Dialog;
import _mine.serverQuery.gui.DisplayArea;
import _mine.serverQuery.gui.PathDialog;
import _mine.serverQuery.net.ConnectionManager;
import _mine.serverQuery.util.ExceptionHandler;
import _mine.serverQuery.util.Keyboard;
import _mine.serverQuery.util.Text;
import _mine.serverQuery.util.properties.PropertyManager;

/**
 * This is the "main class" for the applet version of the <tt>ServerQuery</tt>
 * program.
 * 
 * @author Dan
 * @version July 10, 2006
 */
public class Main extends JApplet implements ServerQuery
{
	private static final long serialVersionUID = -8236035246339067103L;
	
	/* the maximum number of servers to show at one time */
	private static final int MAX_SERVERS = 3;
	
	/*
	 * the estimated amount of space needed to display server and player
	 * information
	 */
	private static final int WIDTH_DIF = 220;
	
	/*  */
	private PropertyManager properties;
	
	/*  */
	private DisplayArea display;
	
	/*
	 * this applet's ConnectionManager object, which handles remote connections
	 * (to fetch the server list, update info about servers, etc.)
	 */
	private ConnectionManager cm;
	
	/* the keyboard listener object, which handles key events */
	private Keyboard keyboard;
	
	/*
	 * A string that points to the file executable file. Initialized to default
	 * install path.
	 */
	private String pathToExecutable;
	
	/* the object responsible for most GUI */
	private Dialog dialog;
	
	/*
	 * a string containing a message about the Applet's current status
	 */
	private String status;
	
	/*  */
	private int minServer;
	
	// ************************
	// END VARIABLE DECLARATION
	// ************************
	
	/** Initializes the applet ServerQuery */
	@Override
	public void init()
	{
		if (!checkPermissions())
		{ // if cert wasn't accepted
			showPermissionsError();
			return;
		}
		
		Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler
				.getInstance());
		// Execute a job on the event-dispatching thread:
		// creating this applet's GUI.
		try
		{
			javax.swing.SwingUtilities.invokeAndWait(new Runnable()
			{
				@Override
				public void run()
				{
					createGUI();
				}
			});
		}
		catch (Exception e)
		{
			System.err.println("init didn't successfully complete");
			e.printStackTrace();
		}
	}
	
	@Override
	public void start()
	{
		System.gc();
		
		if (display != null)
			display.start();
		
		if (cm != null)
			cm.start();
	}
	
	@Override
	public void stop()
	{
		if (display != null)
			display.stop();
		
		if (cm != null)
			cm.stop();
	}
	
	@Override
	public void destroy()
	{
		ExceptionHandler.getInstance().setEnabled(false);
		ExceptionHandler.destroy();
		
		display = null;
		cm = null;
	}
	
	public void createGUI()
	{
		// variable init
		this.properties = new PropertyManager(this);
		
		this.status = "";
		this.minServer = 0;
		this.pathToExecutable = "";
		
		// this command does nothing when running inside a browser
		this.setSize(700, 400);
		// end var init
		
		setBackground(Text.BG_COLOR);
		getContentPane().setBackground(Text.BG_COLOR);
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		initComponents();
	}
	
	private void initComponents()
	{
		final Main parent = this;
		
		SwingWorker<?, ?> worker = new SwingWorker<Object, Object>()
		{
			@Override
			protected Object doInBackground() throws Exception
			{
				cm = new ConnectionManager(parent, properties);
				display = new DisplayArea(parent, properties);
				dialog = new Dialog(parent);
				keyboard = new Keyboard(
						parent, parent.getDialog(), parent
						.getConnectionManager());
				
				return null;
			}
			
			@Override
			protected void done()
			{
				addKeyListener(keyboard);
				getContentPane().add(display);
				validate();
				
				display.start();
			}
		};
		
		worker.execute();
	}
	
	/**
	 * 
	 *
	 */
	private void setupExecutablePaths()
	{
		c_setupExecutablePaths(this);
	}
	
	@Override
	public String getServerListURL()
	{
		return getCodeBase() + "/serverlist.txt"; // relative to applet
		// return Vars.VALUE_SERVER_LIST_URL;
	}
	
	@Override
	public synchronized void adjustMinServerIndex(int n)
	{
		minServer += n;
		
		if (minServer < 0)
		{
			minServer += cm.getServerIPs().length;
		}
		
		if (minServer > cm.getServerIPs().length)
		{
			minServer -= cm.getServerIPs().length;
		}
		
		updateDisplay();
	}
	
	/**
	 * 
	 */
	@Override
	public void setExecutablePath(String f)
	{
		pathToExecutable = f;
	}
	
	/**
	 * 
	 */
	@Override
	public File getExecutableFile()
	{
		if (getExecutablePath() == null)
		{
			return null;
		}
		else
		{
			return new File(getExecutablePath());
		}
	}
	
	/**
	 * 
	 */
	@Override
	public String getExecutablePath()
	{
		if (pathToExecutable == null
				|| pathToExecutable.toString().length() == 0)
		{
			setupExecutablePaths();
		}
		
		return pathToExecutable;
	}
	
	@Override
	public void setStatus(String s)
	{
		status = s;
	}
	
	@Override
	public String getStatus()
	{
		return (status == null ? "" : status);
	}
	
	@Override
	public ConnectionManager getConnectionManager()
	{
		return cm;
	}
	
	public Keyboard getKeyboardManager()
	{
		return keyboard;
	}
	
	@Override
	public FontMetrics getDisplayFontMetrics()
	{
		return getFontMetrics(getFont());
	}
	
	@Override
	public int getColumnWidth()
	{
		return WIDTH_DIF;
	}
	
	public int getMaxX()
	{
		return getWidth();
	}
	
	public int getMaxY()
	{
		return getHeight();
	}
	
	@Override
	public String getVersion()
	{
		return Versions.APPLET;
	}
	
	@Override
	public void updateDisplay()
	{
		if (display != null)
		{
			display.updateImage();
		}
	}
	
	@Override
	public void setupConnectToGui()
	{
		// do nothing
	}
	
	@Override
	public Dialog getDialog()
	{
		return dialog;
	}
	
	@Override
	public int getServersToShow()
	{
		return MAX_SERVERS;
	}
	
	@Override
	public boolean isApplet()
	{
		return this instanceof Applet;
	}
	
	@Override
	public boolean showLaunchConfirmDialog()
	{
		return true;
	}
	
	@Override
	public void onLaunchAction()
	{
		// do nothing
	}
	
	@Override
	public void connectToServer(int serverIndex)
	{
		c_connectToServer(serverIndex, cm, dialog);
	}
	
	@Override
	public Component getGuiContext()
	{
		return this;
	}
	
	@Override
	public void launchURL(String urlTxt)
	{
		URL url = null;
		try
		{
			url = new URL(urlTxt);
		}
		catch (MalformedURLException e)
		{
			JOptionPane.showMessageDialog(this, "Error launching link to "
					+ urlTxt + "\n" + "The URL (" + urlTxt + ") is malformed.");
			return;
		}
		
		getAppletContext().showDocument(url, "_blank");
	}
	
	public boolean checkPermissions()
	{
		Permission[] permissions = new Permission[] {
				new RuntimePermission("setDefaultUncaughtExceptionHandler"),
				new RuntimePermission("modifyThread"),
				new java.net.SocketPermission("mysite.verizon.net:80",
						"connect,resolve"),
				new FilePermission("<<ALL FILES>>", "read,execute"),
				// "user.dir" is the program's working directory
				new PropertyPermission("user.dir", "read") };
		
		for (Permission p : permissions)
		{
			try
			{
				System.getSecurityManager().checkPermission(p);
			}
			catch (SecurityException e)
			{
				return false; // check failed
			}
		}
		
		return true;
	}
	
	@Override
	public String chooseExecutable()
	{
		String path = PathDialog.getExecutablePath(this);
		
		if (path != null)
		{
			setExecutablePath(path);
		}
		
		return path;
	}
	
	@Override
	public void serverListFound()
	{
	}
	
	private void showPermissionsError()
	{
		String url = "http://java.sun.com/docs/books/tutorial/security1.2/"
				+ "tour1/step3.html";
		
		String warning = "Permissions check failed."
				+ "\n\n"
				+ "It appears as if you didn't accept the "
				+ "certificate to let this program run."
				+ "\n\n"
				+ "At the very least, you will need to add the following to your "
				+ "policy file:"
				+ "\n\n"
				+ "grant SignedBy \"Daniel Mangiarelli\" {\n"
				+ "  permission java.lang.RuntimePermission "
				+ "\"setDefaultUncaughtExceptionHandler\";\n"
				+ "  permission java.lang.RuntimePermission \"modifyThread\";\n"
				+ "  permission java.net.SocketPermission "
				+ "\"mysite.verizon.net:80\", \"connect,resolve\";\n"
				+ "  permission java.io.FilePermission "
				+ "\"<<ALL FILES>>\", \"read,execute\"\n"
				+ "  permission java.util.PropertyPermission "
				+ "\"user.dir\", \"read\"\n"
				+ "};"
				+ "\n\n"
				+ "This allows any code signed by me with only those permissions "
				+ "to execute without the need to accept a certificate every time."
				+ "\nSee " + url + " for some information on policy files.";
		
		getContentPane().add(new JTextArea(warning));
	}
}
