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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;

import net.barkerjr.gameserver.Player;
import net.barkerjr.gameserver.Players;
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
 * Provides support for Game-Monitor.com data.  The server must be Premium
 * Server (paid GM service) in order to use this.
 * 
 * @author BarkerJr
 * @since 2007-11-28
 * @see <a href="http://www.game-monitor.com">Game-Monitor.com</a>
 */
public class GameMonitor extends AbstractPlugin {
	/** If there is an update in progress */
	private boolean updating;
	
	/**
	 * The namespace for this plugin
	 * 
	 * @since  2008-02-03
	 * @deprecated  2009-05-02
	 */
	protected static final Namespace namespace =
		Namespace.getNamespace("http://barkerjr.net/java/GameServer/GameMonitor");
	
	private Calendar updated;
	
	private String averagePlayers;
	
	private String uptimePercentage;
	
	private String url;

	private String mod;

	private String all;

	/**
	 * Loads the server data from Game-Monitor.  This method only runs if the
	 * data has not been fetched in the past three minutes.  Server data and
	 * player data will be updated if it's not newer.
	 * 
	 * @since 2007-11-28
	 */
	public void onLoad() {
		if (updating) {
			return;
		}
		boolean update;
		if (updated == null) {
			update = true;
		} else {
			Calendar before = new Calendar();
			before.add(Calendar.MINUTE, -3);
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
		new Thread("Game Monitor") {
			@Override
			public void run() {
				setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						listener.errorHandler(e, server);
					}
				});
				try {
					InetSocketAddress address = server.address;
					StringBuilder buff = new StringBuilder();
					buff.append("http://module.game-monitor.com/");
					buff.append(address.getAddress().getHostAddress());
					buff.append(':').append(address.getPort());
					buff.append("/data/server_all.xml");
					String xmlUrl = buff.toString();
					Element data = new SAXBuilder().build(xmlUrl).getRootElement();
					if (data == null) {
						throw new FileNotFoundException("Empty Document from: "
										+ url);
					}
					String[] lastUpdate =
						data.getChildText("lastUpdate").split(":");
					int i = lastUpdate.length;
					updated = new Calendar();
					updated.add(Calendar.SECOND,
							-Integer.parseInt(lastUpdate[--i]));
					updated.add(Calendar.MINUTE,
							-Integer.parseInt(lastUpdate[--i]));
					updated.add(Calendar.HOUR,
							-Integer.parseInt(lastUpdate[--i]));
					if (i > 0) {
						updated.add(Calendar.DAY_OF_MONTH,
								-Integer.parseInt(lastUpdate[--i]));							
					}
					Calendar serverUpdateTime = server.updated;
					boolean newer;
					if (serverUpdateTime == null) {
						newer = true;
					} else {
						if (serverUpdateTime.before(updated)) {
							newer = true;
						} else {
							newer = false;
						}
					}
					if (newer || (server.name == null)) {
						server.name = data.getChildText("name");
					}
					if (newer) {
						if (data.getChildText("public").equals("1")) {
							server.passwordRequired = false;
						} else {
							server.passwordRequired = true;
						}
					}
					Element playerData = data.getChild("players");
					if (newer || (server.numberOfPlayers == -1)) {
						server.numberOfPlayers =
							Integer.parseInt(
									playerData.getChildText("current"));
					}
					if (newer || (server.maximumPlayers == -1)) {
						server.maximumPlayers =
							Integer.parseInt(playerData.getChildText("max"));
					}
					averagePlayers = playerData.getChildText("average");
					if (newer || (server.botCount == -1)) {
						server.botCount =
							Integer.parseInt(playerData.getChildText("bot"));
					}
					if (newer) {
						server.updated = updated;
					}
					Players players = server.getPlayers();
					boolean newerPlayers;
					if (players == null) {
						newerPlayers = false;
					} else {
						Calendar playersUpdateTime = players.getUpdated();
						if ((playersUpdateTime == null) ||
								playersUpdateTime.before(updated)) {
							newerPlayers = true;
						} else {
							newerPlayers = false;
						}
					}
					if (newerPlayers) {
						boolean updatedPlayers = false;
						for (Object playerDObject:
								playerData.getChildren("player")) {
							Element playerD = (Element)playerDObject;
							for (Player player: players.values()) {
								if (playerD.getChildText("name").equals(
										player.getName())) {
									player.kills =
										Integer.parseInt(
												playerD.getChildText("score"));
									player.secondsConnected =
										Integer.parseInt(
												playerD.getChildText("duration"));
									updatedPlayers = true;
									break;
								}
							}
						}
						if (updatedPlayers) {
							players.updated = updated;
							listener.changed(Request.PLAYERS);
						}
					}
					if (newer || (server.map == null)) {
						server.map = data.getChildText("map");
					}
					uptimePercentage = data.getChildText("uptime");
					url = data.getChildText("link");
					Element rank = data.getChild("rank");
					mod = rank.getChildText("game");
					all = rank.getChildText("overall");
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
	 * @since 2007-11-28
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
		return obj instanceof GameMonitor;
	}

	/**
	 * @since  2007-12-30
	 */
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	/**
	 * Updates the XML document with plugin-specific data
	 * 
	 * @since  2009-05-02
	 */
	@Override
	public void updateXmlDoc(Document doc) {
		Element server = doc.getRootElement();
		server.setAttribute("AveragePlayers", averagePlayers);
		server.setAttribute("UptimePercentage", uptimePercentage);
		Namespace gsNamespace = server.getNamespace();
		Element ranking = server.getChild("Ranking", gsNamespace);
		if (ranking == null) {
			ranking = new Element("Ranking", gsNamespace);
			server.addContent(ranking);
		}
		Namespace namespace =
			Namespace.getNamespace(
					"http://barkerjr.net/java/GameServer/GameMonitor");
		Element gameMonitor = ranking.getChild("GameMonitor",
				namespace);
		if (gameMonitor == null) {
			gameMonitor = new Element("GameMonitor", namespace);
			ranking.addContent(gameMonitor);
			gameMonitor.setAttribute("URL", url);
		}
		if (updated != null) {
			gameMonitor.setAttribute("Updated", updated.toString());
		}
		gameMonitor.setAttribute("Mod", mod);
		gameMonitor.setAttribute("All", all);
	}

	/**
	 * Updates the JSON object with plugin-specific data
	 * 
	 * @since  2009-05-02
	 */
	@Override
	public void updateJsonObject(JSONObject obj) throws JSONException {
		obj.put("averagePlayers", averagePlayers);
		obj.put("uptimePercentage", uptimePercentage);
		JSONObject ranking;
		try {
			ranking = obj.getJSONObject("ranking");
		} catch (JSONException e) {
			ranking = new JSONObject();
			obj.put("ranking", ranking);
		}
		JSONObject gameMonitor;
		try {
			gameMonitor = obj.getJSONObject("gameMonitor");
		} catch (JSONException e) {
			gameMonitor = new JSONObject();
			ranking.put("gameMonitor", gameMonitor);
			gameMonitor.put("url", url);
		}
		if (updated != null) {
			gameMonitor.put("updated", updated.toString());
		}
		gameMonitor.put("mod", mod);
		gameMonitor.put("all", all);
	}
}