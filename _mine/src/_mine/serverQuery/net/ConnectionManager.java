/* ConnectionManager.java */
package _mine.serverQuery.net;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import _mine.net.sourceforge.queried.PlayerInfo;
import _mine.net.sourceforge.queried.QueriEd;
import _mine.net.sourceforge.queried.ServerInfo;
import _mine.serverQuery.ServerQuery;
import _mine.serverQuery.util.properties.PropertyManager;
import _mine.serverQuery.util.properties.Vars;

/**
 * @author Dan
 * @version May 5, 2006
 */
public class ConnectionManager implements Runnable {
	public static final int NO_INFO = -1;
	
	/* game type string, used for QueriEd */
	private static final String GAMETYPE = "ET";
	
	private volatile Thread th;
	
	/* the parent ServerQuery object */
	private ServerQuery parent;
	
	/* the collection of preferences, if one exists */
	private PropertyManager properties;
	
	/* the local port to use for querying servers */
	private int localPort;
	
	/* the delay, in seconds, between information refreshes */
	private int updateInterval;
	
	/* used to regularly update the server information */
	private Timer timer;
	
	/* flag determining whether updates were scheduled or not */
	private boolean updatesScheduled;
	
	/* a boolean variable determining if the list found at the
	 * specified URL was valid */
	private boolean listIsGood;
	
	/* The IPs for the servers which will be looked up at a later time */ 
	private String[] serverAddrs;
	
	/* boolean variable to determine if info is available for
	 * the corresponding server by index number */
	private int[] serverInfoAge;
	
	/* the ServerInfo objects once they have been determined */
	private ServerInfo[] servers;
	
	/* the list of players for the servers */
	private List<PlayerInfo>[] players;
	
	/**
	 * 
	 * @param parent
	 */
	public ConnectionManager(ServerQuery parent,
			PropertyManager properties) {
		this.parent = parent;
		this.properties = properties;
		
		this.init();
		
		this.start();
	}
	
	/**
	 *
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void init() {
		this.updateInterval = properties.getInt(
				Vars.KEY_UPDATE_INTERVAL, Vars.VALUE_UPDATE_INTERVAL);
		this.localPort = properties.getInt(
				Vars.KEY_LOCAL_PORT, Vars.VALUE_LOCAL_PORT);
		
		this.listIsGood = true;
		this.serverAddrs = new String[0];
		this.serverInfoAge = new int[0];
		this.servers = new ServerInfo[0];
		this.players = new ArrayList[0];
		this.timer = new Timer(true); // runs as a daemon
		
		this.getServersList();
	}
	
	/**
	 * 
	 *
	 */
	private void getServersList() {
		parent.setStatus("Retrieving list of IP addresses.");
		
		serverAddrs = getServerListAddrs();
		
		listIsGood = serverAddrs != null;
		
		if(listIsGood) {
			setupServerList();
			parent.serverListFound();
		} else {
			String reason =
				"Cannot retrieve list of servers.  Retrying...";
			parent.setStatus(reason);
		}
		// want it to freshen the list of IPs if it doesn't come in at first
		scheduleUpdates();
	}
	
	/**
	 * 
	 * @return
	 */
	private String[] getServerListAddrs() {
		String[] list = null;
		URL url = null;
		
		try {
			url = new URL(parent.getServerListURL());
		} catch(MalformedURLException e) {
			try {
				url = new URL(Vars.VALUE_SERVER_LIST_URL);
			} catch(MalformedURLException e1) { }
		}
		list = IP.getIpList(url);
		return list;
	}
	
	/**
	 * 
	 *
	 */
	@SuppressWarnings("unchecked")
	private void setupServerList() {
		servers = new ServerInfo[serverAddrs.length];
		players = new ArrayList[serverAddrs.length];
		serverInfoAge = new int[serverAddrs.length];
		
		parent.setStatus(null);
		
		queryServers(0);
	}
	
	/**
	 * 
	 *
	 */
	@SuppressWarnings("unchecked")
	public void queryServers(int age) {
		if(!listIsGood)
			return;
		
		ServerInfo[] serversTmp = new ServerInfo[getServerCount()];
		ArrayList<PlayerInfo>[] playersTmp = new ArrayList[getServerCount()];
		
		for(int i = 0; i < serverAddrs.length; i++) {
			String ip = IP.getIP(serverAddrs[i]);
			int port = IP.getPort(serverAddrs[i]);
			
			serversTmp[i] = QueriEd.serverQuery(localPort, GAMETYPE, ip, port);
			playersTmp[i] = QueriEd.playerQuery(localPort, GAMETYPE, ip, port);
			
			if(serversTmp[i] == null ) {
				serverInfoAge[i] += age;
			} else {
				serverInfoAge[i] = age;
				servers = serversTmp;
				players = playersTmp;
			}
		}
		serversTmp = null;
		playersTmp = null;
		
		parent.updateDisplay();
	}
	
	/**
	 * 
	 *
	 */
	private void scheduleUpdates() {
		if(updatesScheduled)
			return;
		
		if(timer == null)
			timer = new Timer(true);
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(!listIsGood)
					getServersList();
				
				if(listIsGood) {
					queryServers(getUpdateInterval());
					// This is called here to make sure that the JVM doesn't
					// hog all of the system resources.  It is carried out
					// after each refresh of the server info
					//System.gc();
				}
			}
		},  updateInterval * 1000, // initial delay
		updateInterval * 1000);    // subsequent rate
		
		updatesScheduled = true;
		
		// update error messgae text, if any
		if(!listIsGood) {
			String plural = updateInterval == 1 ? " second." : " seconds.";
			String reason =
				"Cannot retrieve list of servers.  " +
				"Retrying in " + updateInterval + plural;
			parent.setStatus(reason);
		}
	}
	
	/**
	 * 
	 * @param n
	 */
	public void setUpdateInterval(int n) {
		if(n < Vars.MINIMUM_UPDATE_INTERVAL)
			n = Vars.MINIMUM_UPDATE_INTERVAL;
		
		updateInterval = n;
		
		cancelTimer();
		
		scheduleUpdates();
	}
	
	public void cancelTimer() {
		timer.cancel();
		timer = null;
		
		updatesScheduled = false;
	}
	
	/**
	 * 
	 */
	public synchronized void syncSettings() {
		if(properties != null) {
			properties.putInt(Vars.KEY_LOCAL_PORT, localPort);
			properties.putInt(Vars.KEY_UPDATE_INTERVAL, updateInterval);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int getUpdateInterval() {
		return updateInterval;
	}
	
	/**
	 * 
	 * @param n
	 */
	public void setLocalPort(int n) {
		localPort = n;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getLocalPort() {
		return localPort;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean listIsGood() {
		return listIsGood;
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getServerIPs() {
		try {
			return serverAddrs;
		} catch(NullPointerException e) {
			return new String[0];
		}
		
	}
	
	/**
	 * 
	 * @param n
	 * @return
	 */
	public String serverIP(int n) {
		try {
			return serverAddrs[n];
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int[] getServerInfoAges() {
		try {
			return serverInfoAge;
		} catch(NullPointerException e) {
			return new int[0];
		}
	}
	
	/**
	 * 
	 * @param i
	 * @return
	 */
	public int serverInfoAge(int i) {
		try {
			return serverInfoAge[i];
		} catch(NullPointerException e) {
			return NO_INFO;
		}
	}
	
	/**
	 * 
	 * @param i
	 * @return
	 */
	public boolean serverHasInfo(int i) {
		try {
			return serverInfoAge[i] == NO_INFO;
		} catch(NullPointerException e) {
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ServerInfo[] getServers() {
		try {
			return servers;
		} catch(NullPointerException e) {
			return new ServerInfo[0];
		}
	}
	
	/**
	 * 
	 * @param n
	 * @return
	 */
	public ServerInfo servers(int n) {
		try {
			return servers[n];
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PlayerInfo>[] getPlayers() {
		try {
			return players;
		} catch(NullPointerException e) {
			return new ArrayList[0];
		}
	}
	
	/**
	 * 
	 * @param n
	 * @return
	 */
	public List<PlayerInfo> players(int n) {
		try {
			return players[n];
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int getServerCount() {
		try {
			return servers.length;
		} catch(NullPointerException e) {
			return 0;
		}
	}
	
	/**
	 * 
	 * @param i
	 * @return
	 */
	public String getServerIP(int i) {
		try {
			return serverAddrs[i];
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * 
	 *
	 */
	public void start() {
		if(th == null) {
			th = new Thread(this);
			th.run();
		}
		
		scheduleUpdates();
	}
	
	public void stop() {
		th = null;
		cancelTimer();
	}
	
	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		thisThread.setPriority(Thread.MIN_PRIORITY);
		
		while(th == thisThread) {
			// required at end of while
			try {
				// Stop thread for the "specified" time/10 ms
				Thread.sleep(Vars.THREAD_SLEEP_TIME);
			} catch (InterruptedException ex) { } // do nothing
		}
	}
}
