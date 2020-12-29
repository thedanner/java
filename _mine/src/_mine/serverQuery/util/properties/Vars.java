/* Vars.java */
package _mine.serverQuery.util.properties;

import _mine.serverQuery.gui.application.SettingsWindow;

/**
 * A collection of variables for the application version of the
 * <tt>ServerQuery</tt> program to use.  This contains variables that are,
 * almost exclusively, <tt>final</tt>, and server as "Key-Value" names for
 * the properties manager.  "<tt>VALUE_</tt>" variables should be considered
 * defaults, and can be used when an actual or changed value is inaccessable.
 * @author Dan
 * @version May 20, 2006
 */
public class Vars {
	/* internal book keeping for the properties manager, the version should
	 * only be incremented if changes are significant, and require that any
	 * old information must be cleared out */
	public static final String KEY_PROPERTIES_VERSION = "properties.version";
	public static final int VALUE_PROPERTIES_VERSION = 11;
	/* Version History:
	 * 11:	Mar 08, 2006: added: save window position */
	
	// - for Frame
	/* look and feel to use for the program */
	public static final String KEY_LNF = "frame.lnf";
	public static final String VALUE_LNF = 
		"com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	
	/* dimensions for the top-level container */
	public static final String KEY_FRAME_WIDTH = "frame.width";
	public static final int VALUE_FRAME_WIDTH = 700;
	public static final String KEY_FRAME_HEIGHT = "frame.height";
	public static final int VALUE_FRAME_HEIGHT = 430;
	public static final String KEY_X_COORD = "frame.x";
	public static final int VALUE_X_COORD = 0;
	public static final String KEY_Y_COORD = "frame.y";
	public static final int VALUE_Y_COORD = 0;
	
	/* path to executable */
	public static final String KEY_PATH_TO_EXECUTABLE =
		"frame.path_to_executable";
	public static final String VALUE_EXECUTABLE_PATH = "";
	
	/* default operation to complete after launching the program */
	public static final String KEY_ON_LAUNCH_ACTION =
		"frame.on_launch_action";
	public static final int VALUE_ON_LAUNCH_ACTION =
		SettingsWindow.ON_LAUNCH_DEFAULT;
	
	/* show confirm box when launching */
	public static final String KEY_SHOW_CONFIRM_DIALOG =
		"frame.show_confirm_dialog";
	public static final boolean VALUE_SHOW_CONFIRM_DIALOG =	true;
	
	
	// - for DisplayArea
	/* number of servers to show at once */
	public static final String KEY_SERVERS_TO_SHOW = "display.servers_to_show"; 
	public static final int VALUE_SERVERS_TO_SHOW  = 3;
	
	/* dimenions for the display area */
	public static final String KEY_WIDTH_DIF = "display.width_difference";
	public static final int VALUE_WIDTH_DIF = 230;
	
	// - for CM
	/* local port to use to fetch info */
	public static final String KEY_LOCAL_PORT = "cm.local_port";
	public static final int VALUE_LOCAL_PORT = 27777;
	
	/* update inteval */
	public static final String KEY_UPDATE_INTERVAL = "cm.update_interval";
	public static final int VALUE_UPDATE_INTERVAL = 8;
	
	// - URLs
	/* url for the list of current servers */
	public static final String KEY_SERVER_LIST_URL =
		"url.server_list";
	public static final String VALUE_SERVER_LIST_URL =
		"http://mysite.verizon.net/vze3bxfh/sq/serverlist.txt";
		
	/* url for the help text */
	public static final String KEY_HELP_TEXT_URL = "url.help_text";
	public static final String VALUE_HELP_TEXT_URL =
		"http://mysite.verizon.net/dan198792/sq/sq_help.html";
	
	/* url for the about text */
	public static final String KEY_ABOUT_TEXT_URL = "url.about_text";
	public static final String VALUE_ABOUT_TEXT_URL =
		"http://mysite.verizon.net/dan198792/sq/sq_about.html";
	
	/* url for hyd-clan-forums site */
	public static final String KEY_HYD_CLAN_FORUMS_URL = "url.hyd_clan_forums";
	public static final String VALUE_HYD_CLAN_FORUMS_URL = 
		"http://hyd-clan-forums.com/";
	
	/* url for hyd-clan site */
	public static final String KEY_HYD_CLAN_URL = "url.hyd_clan";
	public static final String VALUE_HYD_CLAN_URL = 
		"http://hyd-clan.com/";
	
	/* thread in forums for info abot the applet */
	public static final String KEY_INFO_PAGE_URL = "url.info";
	public static final String VALUE_INFO_PAGE_URL =
		"http://www.hyd-clan-forums.com/index.php?showtopic=936";
	
	/* project "home page" url */
	public static final String KEY_HOME_PAGE_URL = "url.home";
	public static final String VALUE_HOME_PAGE_URL = 
		"http://mysite.verizon.net/dan198792/sq/";
	
	// constants
	
	/* the minimum rate to update the server infomation */
	public static final int MINIMUM_UPDATE_INTERVAL = 3;
	
	/* the maximum amout of time (seconds) between server refreshes */
	public static final int MAXIMUM_UPDATE_INTERVAL = 60;
	
	/* the lowest port that can be used for outgoing connections */
	public static final int MINIMUM_LOCAL_PORT = 0;
	
	/* the highest port that can be used for outgoing connections */
	public static final int MAXIMUM_LOCAL_PORT = 65536;
	
	/* the minimum amount of servers to view at once */
	public static final int MINIMUM_SERVERS_TO_SHOW = 1;
	
	/* the maximum amout of servers to show at one time */
	public static final int MAXIMUM_SERVERS_TO_SHOW = 4;
	
	/* the number of ms/10 for threads in runnable objects to sleep at the end
	 * of a single iteration */
	public static final int THREAD_SLEEP_TIME = 100;
	
	/**
	 * 
	 * @param target the <tt>PropertyManager</tt> object to add these
	 * variables to.
	 */
	public static synchronized void generateNewProperties(
			PropertyManager target) {
		// poperties version
		target.putInt(KEY_PROPERTIES_VERSION, VALUE_PROPERTIES_VERSION);
		
		// look and feel
		target.put(KEY_LNF, VALUE_LNF);
		// window width
		target.putInt(KEY_FRAME_WIDTH, VALUE_FRAME_WIDTH);
		//window height
		target.putInt(KEY_FRAME_HEIGHT, VALUE_FRAME_HEIGHT);
		// path to executable
		target.put(KEY_PATH_TO_EXECUTABLE, VALUE_EXECUTABLE_PATH);
		// on launch action
		target.putInt(KEY_ON_LAUNCH_ACTION, VALUE_ON_LAUNCH_ACTION);
		// show confirm dialog
		target.putBoolean(KEY_SHOW_CONFIRM_DIALOG, VALUE_SHOW_CONFIRM_DIALOG);
		
		// servers to show at once
		target.putInt(KEY_SERVERS_TO_SHOW, VALUE_SERVERS_TO_SHOW);
		// the space, in pixels, between the start of each column
		target.putInt(KEY_WIDTH_DIF, VALUE_WIDTH_DIF);
		
		// local port to initiate connections from
		target.putInt(KEY_LOCAL_PORT, VALUE_LOCAL_PORT);
		// the delay between updates of the server information
		target.putInt(KEY_UPDATE_INTERVAL, VALUE_UPDATE_INTERVAL);
		
		// url for server list
		target.put(KEY_SERVER_LIST_URL, VALUE_SERVER_LIST_URL);
		// html file containing the help text
		target.put(KEY_HELP_TEXT_URL, VALUE_HELP_TEXT_URL);
		// html file containing the about text
		target.put(KEY_ABOUT_TEXT_URL, VALUE_ABOUT_TEXT_URL);
		// url to forums
		target.put(KEY_HYD_CLAN_FORUMS_URL, VALUE_HYD_CLAN_FORUMS_URL);
		// url to main clan site
		target.put(KEY_HYD_CLAN_URL, VALUE_HYD_CLAN_URL);
		// url to the thread containing info about the applet
		target.put(KEY_INFO_PAGE_URL, VALUE_INFO_PAGE_URL);
		// url to project "homepage"
		target.put(KEY_HOME_PAGE_URL, VALUE_HOME_PAGE_URL);
		
		// saves properties
		target.store();
	}
}
