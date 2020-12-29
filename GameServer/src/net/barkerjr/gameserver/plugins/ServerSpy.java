/*
 * Game Server Library - Querying server requests and returning them as XML
 * Copyright (C) 2007-2009  BarkerJr <http://www.barkerjr.net/java/GameServer/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.barkerjr.gameserver.plugins;

import java.io.IOException;

import net.barkerjr.gameserver.GameServer.Request;
import net.barkerjr.gameserver.util.Calendar;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides support for ServerSpy data.  The server must have a FlasyMonitor
 * (paid SS service) in order to use this.
 * 
 * @author BarkerJr
 * @since 2007-12-30
 * @see <a href="http://www.serverspy.net">ServerSpy</a>
 */
public class ServerSpy extends AbstractPlugin {
	/** If there is an update in progress */
	private boolean updating;
	
	/**
	 * The namespace for this plugin
	 * 
	 * @since  2008-02-03
	 * @deprecated  2009-05-02
	 */
	protected static final Namespace namespace =
		Namespace.getNamespace("http://barkerjr.net/java/GameServer/ServerSpy");
	
	/** The monitor ID */
	private final int mid;
	
	private Calendar updated;
	private String game;
	private String region;
	private String mod;
	private String location;
	
	/**
	 * Sets up this plugin
	 * 
	 * @param mid  The Flashy Monitor ID, displayed on the Flashy Monitor page
	 * @since 2007-12-30
	 * @see <a href="http://www.serverspy.net/site/monitor2/">Flashy Monitors</a>
	 */
	public ServerSpy(int mid) {
		super();
		this.mid = mid;
	}

	/**
	 * Loads the server data from ServerSpy.  This method only runs if the data
	 * has not been fetched in the past day.
	 * 
	 * @since 2007-12-30
	 */
	@Override
	public void onLoad() {
		if (updating) {
			return;
		}
		boolean update;
		if (updated == null) {
			update = true;
		} else {
			Calendar before = new Calendar();
			before.add(Calendar.DATE, -1);
			if (updated.before(before)) {
				update = true;
			} else {
				update = false;
			}
		}
		if (update && !updating) {
			update();
		} else {
			listener.loaded(Request.PLUGIN);
		}
	}

	private void update() {
		updating = true;
		new Thread("Server Spy") {
			@Override
			public void run() {
				setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						listener.errorHandler(e, server);
					}
				});
				try {
					Element data = new SAXBuilder().build(
						"http://www.serverspy.net/bin/smonV2.mpl?mid=" +
						mid).getRootElement().getChild("server");
					if (data.getAttributeValue("status").equals("ERROR")) {
						listener.errorHandler(new Exception(
							"This server may not be registered with ServerSpy"),
							server);
					} else {
						updated = new Calendar();
						Element additional = data.getChild("additional");
						server.gameDirectory =
							additional.getChildText("gametype");
						game = additional.getChildText("rankgame");
						region = additional.getChildText("rankgeo");
						mod = additional.getChildText("rankmod");
						Element general = data.getChild("general");
						server.name = general.getChildText("name");
						location = general.getChildText("country");
						server.map = general.getChildText("map");
						String[] players = general.getChildText(
								"players").split(" / ");
						if (players.length > 1) {
							server.numberOfPlayers = Integer.parseInt(players[0]);
							server.maximumPlayers = Integer.parseInt(players[1]);
						}
						server.updated = updated;
					}
				} catch (JDOMException e) {
					listener.errorHandler(e, server);
				} catch (IOException e) {
					listener.errorHandler(e, server);
				} finally {
					updating = false;
				}
				listener.loaded(Request.PLUGIN);
				listener.changed(Request.INFORMATION);
			}
		}.start();
	}

	/**
	 * Specifies that the server should wait
	 * 
	 * @since 2007-12-30
	 */
	@Override
	public boolean shouldWait() {
		return true;
	}

	/**
	 * @since  2007-12-30
	 * @return  <code>true</code> if the other object is also an instance of
	 * this class
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ServerSpy) {
			ServerSpy other = (ServerSpy)obj;
			return other.mid == mid;
		}
		return false;
	}
	
	/**
	 * @since 2007-12-30
	 */
	@Override
	public int hashCode() {
		return getClass().hashCode() ^ mid;
	}

	/**
	 * Updates the XML document with plugin-specific data
	 * 
	 * @since  2009-05-02
	 */
	@Override
	public void updateXmlDoc(Document doc) {
		Element serverElement = doc.getRootElement();
		Namespace gsNamespace = serverElement.getNamespace();
		Element ranking = serverElement.getChild("Ranking", gsNamespace);
		if (ranking == null) {
			ranking = new Element("Ranking", gsNamespace);
			serverElement.addContent(ranking);
		}
		Namespace namespace =
			Namespace.getNamespace(
					"http://barkerjr.net/java/GameServer/ServerSpy");
		Element serverSpy = ranking.getChild("ServerSpy",
				namespace);
		if (serverSpy == null) {
			serverSpy = new Element("ServerSpy",
					namespace);
			ranking.addContent(serverSpy);
		}
		if (updated != null) {
			serverSpy.setAttribute("Updated", updated.toString());
		}
		serverSpy.setAttribute("Game", game);
		serverSpy.setAttribute("Region", region);
		serverSpy.setAttribute("Mod", mod);
		serverElement.setAttribute("Location", location);
	}

	/**
	 * Updates the JSON object with plugin-specific data
	 * 
	 * @since  2009-05-02
	 */
	@Override
	public void updateJsonObject(JSONObject obj) throws JSONException {
		JSONObject ranking;
		try {
			ranking = obj.getJSONObject("ranking");
		} catch (JSONException e) {
			ranking = new JSONObject();
			obj.put("ranking", ranking);
		}
		JSONObject serverSpy;
		try {
			serverSpy = obj.getJSONObject("serverSpy");
		} catch (JSONException e) {
			serverSpy = new JSONObject();
			ranking.put("serverSpy", serverSpy);
		}
		if (updated != null) {
			serverSpy.put("updated", updated.toString());
		}
		serverSpy.put("game", game);
		serverSpy.put("region", region);
		serverSpy.put("mod", mod);
		obj.put("location", location);
	}
}