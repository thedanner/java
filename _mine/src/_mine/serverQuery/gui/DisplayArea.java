/* DisplayArea.java */
package _mine.serverQuery.gui;

import static _mine.serverQuery.util.Text.BLACK;
import static _mine.serverQuery.util.Text.BLUE;
import static _mine.serverQuery.util.Text.DEFAULT_COLOR;
import static _mine.serverQuery.util.Text.DEFAULT_FONT;
import static _mine.serverQuery.util.Text.GREEN;
import static _mine.serverQuery.util.Text.PURPLE;
import static _mine.serverQuery.util.Text.RED;
import static _mine.serverQuery.util.Text.getPattern;
import static _mine.serverQuery.util.Text.removeColorTags;
import static _mine.serverQuery.util.Text.resolveColor;
import static _mine.serverQuery.util.Text.trimQuotes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.JPanel;

import _mine.net.sourceforge.queried.PlayerInfo;
import _mine.net.sourceforge.queried.ServerInfo;
import _mine.serverQuery.ServerQuery;
import _mine.serverQuery.net.ConnectionManager;
import _mine.serverQuery.util.Text;
import _mine.serverQuery.util.properties.PropertyManager;
import _mine.serverQuery.util.properties.Vars;

/**
 * This is the display that shows the list of servers for the
 * application version of the <tt>ServerQuery</tt> program.
 * @author Dan
 * @version Nov 20, 2006
 */
public class DisplayArea extends JPanel implements Runnable {
	private static final long serialVersionUID = -1596040358176176026L;

	/* the constant base for the x-coordinate */
	public static final int BASE_X = 5;
	
	/* the constant base for the x-coordinate */
	public static final int BASE_Y = 15;
	
	/*  */
	private volatile Thread th;
	
	/*  */
	private PropertyManager prefs;
	
	/*  */
	private ServerQuery parent;
	
	/* the index of the server to show first */
	private int minServer;
	
	/*  */
	private ConnectionManager cm;
	
	// **********************
	// Variables for graphics
	// **********************
	
	/* used for double-buffered graphics */
	private Graphics2D gBuf;
	
	/* also used for double-buffered graphics */
	private Image imgBuf;
	
	/* the instance of Text to use for drawing operations
	 * specific to this class */
	private Text text;
	
	/* the base x-coordinate for painting */
	private int baseX;
	
	/* the base y-coordinate for painting */
	private int baseY;
	
	/* the current x-coordinate for painting */
	private int curX;
	
	/* the current y-coordinate for painting */
	private int curY;
	
	/* the estimated amount of space needed to display 
	 * server and player information */
	private int widthDif;
	
	/*  */
	private String status;
	
	// ************************
	// END VARIABLE DECLARATION
	// ************************
	
	public DisplayArea(ServerQuery parent, PropertyManager properties) {
		super();
		
		this.parent = parent;
		this.prefs = properties;
		
		this.init();
	}
	
	/**
	 * 
	 *
	 */
	private void init() {
		// var init
		if(parent.isApplet())
			this.widthDif = Vars.VALUE_WIDTH_DIF;
		else
			this.widthDif = prefs.getInt(
					Vars.KEY_WIDTH_DIF, Vars.VALUE_WIDTH_DIF);
		
		this.minServer = 0;
		this.cm = parent.getConnectionManager();
		this.text = new Text(parent);
		this.baseX = BASE_X;
		this.baseY = BASE_Y;
		this.curX = baseX;
		this.curY = baseY;
		this.status = "";
		// end var init
	}
	
	/**
	 * 
	 */
	@Override
	public void run() {
		int maxWidth = BASE_X +
			Vars.MAXIMUM_SERVERS_TO_SHOW * getWidthDif();
		
		// "forced" init, infinite loop if not realized !!!!!
		while(imgBuf == null || gBuf == null) {
			try {
				imgBuf = createImage(maxWidth, getHeight());
				if(imgBuf != null) {
					Graphics g = imgBuf.getGraphics();
					gBuf = (Graphics2D)g.create();
				}
			} catch(Exception e) {
				Thread.yield();
			}
		}
		
		// sets up threading
		Thread thisThread = Thread.currentThread();
		thisThread.setPriority(Thread.MIN_PRIORITY);
		
		int hack = 0;
		
		// main work loop
		while(thisThread == th) {
			// force the correct "image" to be displayed
			if(hack < 15) {
				updateImage();
				hack++;
			}
			
			repaint();
			
			// required at end of while
			try {
				Thread.sleep(Vars.THREAD_SLEEP_TIME);
			} catch(InterruptedException e) {}
			// set ThreadPriority to maximum value
			thisThread.setPriority(Thread.MAX_PRIORITY);
		}
	}
	
	/**
	 * 
	 *
	 */
	public void updateImage() {
		try {
			// clear what was drawn last time.
			clearScreen();
			
			setFont(DEFAULT_FONT);
			
			if(parent.isApplet()) {
				drawKeyPressInfo();
				newline();
				if(!hasStatusMessage())
					newline();
			}
			
			if(hasStatusMessage()) {
				write(getStatus(), RED);
				newline(2);
			}
			
			if(cm.listIsGood()) {
				setStatus(null);
				
				// begin printing server information
				for(int i = minServer, iterations = 0;
				iterations < parent.getServersToShow() &&
				iterations < cm.getServerCount();
				i++, iterations++) {
					
					if(i > cm.getServerIPs().length - 1)
						i -= cm.getServerIPs().length;
					
					write("Server " + (i + 1), BLACK);
					
					if(cm.serverInfoAge(i) > cm.getUpdateInterval() * 3 ||
							cm.serverInfoAge(i) == ConnectionManager.NO_INFO) {
						
						write("  (outdated)", PURPLE);
					}
					newline();
					
					listServer(cm.servers(i), cm.serverIP(i));
					listPlayers(cm.players(i));
					
					changeBases(getBaseX() + getWidthDif(), getBaseY());
					cleanUp();
				}
			} else {
				//reports an error if the list of servers is unavailable.
				setStatus("Error with server address lookup, retrying...");
			}
			// needed to keep the info in the same spot
			// when focus is returned after being lost.
			restoreBases();
			
			// repaints everything
			repaint();
		} catch(Exception e) {} // something bad happened (temporarily?)
	}
	
	/**
	 * 
	 *
	 */
	private void drawKeyPressInfo() {
		String info = "^B" + "Help " +
		"^P" + "F1 " +
		"^L" + ": " +
		"^B" + "About " +
		"^P" + "F2 " +
		"^L" + ": " +
		"^B" + "Join server " +
		"^P" + "Number Keys (1, 2, 3, etc.)";
		
		if(cm.getServerCount() > parent.getServersToShow()) {
			info += "^L" + " : " +
			"^B" + "Scroll servers " +
			"^P" + "Left, Right Arrow";
		}
		
		write(info);
	}
	
	// The following 2 methods take on the bulk of the drawing procedure
	
	/**
	 * 
	 */
	public void listServer(ServerInfo si, String ip) {
		if(si == null) {
			write("Error, no information available.", RED);
			newline();
			
			if(ip != null)
				write(String.format("IP: %1$s", ip), BLACK);
			
			return;
		}
		
		String name = si.getName();
		
		// truncates name if necessary
		if(text.drawnStringLength(name) >
				text.getTruncationThreshold()) {
			String end = "...";
			
			name = name.substring(0, name.length() - end.length());
			name = name.substring(0, text.truncate(name, end));
			
			if(name.endsWith("^") || name.endsWith(" "))
				name = name.substring(0, name.length() - 1);
			
			name += end;
		}
		
		write(name);
		newline();
		
		write(si.getIp() + ":" + si.getPort(), BLACK);
		newline();
		
		write("Players: ", BLUE);
		
		int curPlayers = Integer.parseInt(si.getPlayerCount());
		int maxPlayers = Integer.parseInt(si.getMaxPlayers());
		
		if(curPlayers == 0) setColor(RED);
		else if (curPlayers == maxPlayers) setColor(GREEN);
		else setColor(PURPLE);
		write(curPlayers + " of " + maxPlayers, getColor());
		
		newline();
		
		write("Map: ", BLUE);
		write(si.getMap(), RED);
		
		newline(2);
	}
	
	/**
	 * 
	 * @param list
	 */
	public void listPlayers(List<PlayerInfo> list) {
		if(list == null)
			return;
		
		for(PlayerInfo player : list) {
			if(player == null)
				return;
			
			// eliminates leading and trailing quotes,
			// after trimming whitespace
			String name = trimQuotes(player.getName().trim());
			
			// the number of kills a player has in purple (^P)
			String score = "^P" + "(" + player.getKills() + ")";
			
			// concatenate the two strings
			name += " " + score;
			
			// truncates name if necessary
			if(text.drawnStringLength(name) >
			text.getTruncationThreshold()) {
				score = "... " + score;
				
				name = name.substring(0, name.length() - score.length());
				name = name.substring(0, text.truncate(name, score));
				
				if(name.endsWith("^") || name.endsWith(" "))
					name = name.substring(0, name.length() - 1);
				
				name += score;
			}
			// paints the player's colored name and score
			write(name);
			
			newline();
		}
	}
	
	// **********************
	// Actual drawing methods
	// **********************
	
	/**
	 * 
	 */
	private void write(String s) {
		Matcher m = getPattern().matcher(
				s.subSequence(0, s.length()));
		
		int previous = 0;
		Color c = DEFAULT_COLOR;
		Color cPrev = c;
		
		while(m.find()) {
			c = resolveColor(m.group());
			if(previous >= 0) write(s.substring(previous, m.start()), cPrev);
			previous = m.end();
			cPrev = c;
		}
		write(s.substring(previous), cPrev);
	}
	
	/**
	 * 
	 * @param s
	 * @param c
	 */
	private void write(String s, Color c) {
		setColor(c);
		writeString(removeColorTags(s));
	}
	
	/**
	 * 
	 * @param s
	 */
	private void writeString(String s) {
		if(gBuf == null) return;
		gBuf.drawString(s, getCurX(), getCurY());
		for(int i = 0; i < s.length(); i++)
			curX += gBuf.getFontMetrics().charWidth(s.charAt(i));
	}
	
	/**
	 * 
	 */
	@Override
	protected void paintComponent(Graphics g) {
		// draw the offscreen buffer to the screen
		g.drawImage(imgBuf, 0, 0, this);
	}
	
	/**
	 * 
	 */
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	/**
	 * 
	 *
	 */
	private void clearScreen() {
		if(gBuf != null)
			gBuf.clearRect(0, 0, getMaxX(), getMaxY());
	}
	
	//*************
	//other methods
	//*************
	
	/**
	 * 
	 */
	public void updateServersToShow() {
		int newWidth = DisplayArea.BASE_X +
			parent.getServersToShow() * getWidthDif();
		setSize(newWidth, getHeight());
		
		try {
			imgBuf = createImage(getWidth(), getHeight());
			if(imgBuf != null) {
				Graphics g = imgBuf.getGraphics();
				gBuf = (Graphics2D)g.create();
			}
		} catch(Exception e) {}
		
		try {
			updateImage();
		} catch(NullPointerException e) { }
	}
	
	/**
	 * 
	 * @param n
	 */
	public void adjustMinServerIndex(int n) {
		minServer += n;
		if(minServer < 0)
			minServer += cm.getServerIPs().length; 
		if(minServer > cm.getServerIPs().length)
			minServer -= cm.getServerIPs().length;
	}
	
	/**
	 * 
	 *
	 */
	private void restoreBases() {
		changeBases(BASE_X, BASE_Y);
		cleanUp();
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	private void changeBases(int x, int y) {
		baseX = x;
		baseY = y;
	}
	
	/**
	 * 
	 *
	 */
	private void cleanUp() {
		curX = baseX;
		curY = baseY;
	}
	
	/**
	 * 
	 * @return
	 */
	private int newline() {
		return newline(1);
	}
	
	/**
	 * 
	 * @param n
	 * @return
	 */
	private int newline(int n) {
		try {
			curY += n * gBuf.getFontMetrics().getHeight();
			curX = getBaseX();
		} catch(NullPointerException e) { }
		return n;
	}
	
	/**
	 * 
	 * @param c
	 */
	public void setColor(Color c) {
		if(gBuf != null)
			gBuf.setColor(c);
	}
	
	/**
	 * 
	 */
	@Override
	public void setFont(Font f) {
		if(gBuf != null) {
			gBuf.setFont(f);
			text.setFontMetrics(gBuf.getFontMetrics());
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public FontMetrics getFontMetrics() {
		return getFontMetrics(getFont());
	}
	
	/**
	 * 
	 * @return
	 */
	public Color getColor() {
		if(gBuf == null || gBuf.getColor() == null)
			return DEFAULT_COLOR; 
		return gBuf.getColor();
	}
	
	/**
	 * 
	 * @return
	 */
	public int getBaseX() {
		return baseX;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getBaseY() {
		return baseY;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getWidthDif() {
		return widthDif;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCurX() {
		return curX;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCurY() {
		return curY;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMaxX() {
		return getWidth();
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMaxY() {
		return getHeight();
	}
	
	/**
	 * 
	 * @param s
	 */
	public void setStatus(String s) {
		status = s;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasStatusMessage() {
		if(status == null)
			return false;
		return getStatus().length() > 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getStatus() {
		return status == null ? "" : status;
	}
	
	public void start() {
		if(text != null)
			text.start();
		
		if(th == null) {
			th = new Thread(this);
			th.setPriority(Thread.MIN_PRIORITY);
			th.start();
		}
	}
	
	public void stop() {
		text.stop();
		th = null;
	}
}
