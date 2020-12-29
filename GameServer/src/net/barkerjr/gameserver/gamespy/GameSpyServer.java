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
package net.barkerjr.gameserver.gamespy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

import net.barkerjr.gameserver.Player;
import net.barkerjr.gameserver.ReplyStream;
import net.barkerjr.gameserver.UdpServer;

/**
 * Provides generic parsing and querying for GameSpy protocol servers
 * <br/>
 * <br/>
 * TODO Due to lack of reply format information in replies:
 * <ul>
 *  <li>the listener *loaded() methods may be called prematurely</li>
 *  <li>the listener *updated() methods may be called more frequently</li>
 *  <li>the {@link #load(int, Request...)} method may return before the data is
 *  completely loaded</li>
 * </ul>
 * 
 * @author BarkerJr
 * @since 2007-11-18
 * @see  <a href="http://www.int64.org/docs/gamestat-protocols/gamespy.html">Specification</a>
 */
public abstract class GameSpyServer extends UdpServer {
	/**
	 * The location of the server
	 * 
	 * @since  2009-05-02
	 */
	protected String location;
	
	/**
	 * Sets up the basic server information and DOM
	 * 
	 * @param address  the address of the server
	 * @since 2007-11-18
	 */
	public GameSpyServer(InetSocketAddress address) {
		super(address);
	}

	/**
	 * @since 2007-11-18
	 */
	@Override
	protected void parseData(byte[] packet) {
		String[] data = new ReplyStream(packet).readString().split("\\\\");
		String key = null;
		HashSet<Request> updated = new HashSet<Request>();
		for (String item: data) {
			if (key == null) {
				if (item.length() > 0) {
					key = item;
				}
			} else {
				Request update = storePair(key, item);
				if (update != null) {
					updated.add(update);
				}
				key = null;
			}
		}
		for (Request type: updated) {
			fireLoadEvent(type);
		}
	}

	/**
	 * Processes and persists the given key/value pair in the DOM
	 * 
	 * @param key  the key for the rule, etc
	 * @param value  the value of the given key
	 * @return  the request type this data belongs to, or <tt>null</tt> if it
	 * was not persisted
	 * @since 2007-11-18
	 */
	protected Request storePair(String key, String value) {
		if (key.equals("hostname")) {
			name = value;
			return Request.INFORMATION;
		}
		if (key.equals("mapname")) {
			map = value;
			return Request.INFORMATION;
		}
		if (key.equals("numplayers")) {
			numberOfPlayers = Integer.parseInt(value);
			return Request.INFORMATION;
		}
		if (key.equals("maxplayers")) {
			maximumPlayers = Integer.parseInt(value);
			return Request.INFORMATION;
		}
		if (key.startsWith("ping_")) {
			getPlayer(key).ping = Integer.parseInt(value.trim());
			return Request.PLAYERS;
		}
		if (key.startsWith("team_")) {
			getPlayer(key).team = Integer.parseInt(value.trim());
			return Request.PLAYERS;
		}
		if (key.equals("queryid") || key.equals("final")) {
			return null;
		}
		if (rules == null) {
			rules = new HashMap<String, String>(1);
		}
		rules.put(key, value);
		return Request.RULES;
	}

	/**
	 * Fetches or creates the given player element
	 * 
	 * @param key  the player number
	 * @return  the DOM element representing the player
	 * @since 2007-11-18
	 */
	protected Player getPlayer(String key) {
		int id = Integer.parseInt(key.substring(key.indexOf('_') + 1));
		return getPlayers().get(id);
	}

	/**
	 * Supports:
	 * <ul>
	 *  <li>Request.BASIC</li>
	 *  <li>Request.INFORMATION</li>
	 *  <li>Request.PLAYERS</li>
	 *  <li>Request.RULES</li>
	 * </ul>
	 * 
	 * {@inheritDoc}
	 * 
	 * @since 2007-11-18
	 */
	@Override
	public void load(Request... requests) throws IOException {
		List<Request> requestList = Arrays.asList(requests);
		if (requestList.contains(Request.BASIC)) {
			sendData("\\basic\\");
		}
		if (requestList.contains(Request.INFORMATION)) {
			sendData("\\info\\");
		}
		if (requestList.contains(Request.PLAYERS)) {
			players = null;
			sendData("\\players\\");
		}
		if (requestList.contains(Request.RULES)) {
			rules = null;
			sendData("\\rules\\");
		}
		super.load(requests);
	}

	/**
	 * Determines the region that matches the given number
	 * 
	 * @param code  the region number
	 * @return  the region name
	 */
	protected String getLocation(String code) {
		switch (Integer.parseInt(code)) {
			case 2: return "Southeastern USA";
			case 3: return "Western USA";
			case 4: return "Midwestern USA";
			case 5: return "Northwestern USA / Western Canada";
			case 6: return "Northeastern USA / Eastern Canada";
			case 7: return "United Kingdom";
			case 8: return "Continental Europe";
			case 9: return "Central Asia / Middle East";
			case 10: return "Southeast Asia / Pacific";
			case 11: return "Africa";
			case 12: return "Australia / New Zealand / Pacific";
			case 13: return "South America";
			default: return "Earth";
		}
	}

	/**
	 * @since  2009-05-02
	 */
	@Override
	public Document toXmlDocument() {
		Document doc = super.toXmlDocument();
		Element server = doc.getRootElement();
		server.setAttribute("Location", location);
		return doc;
	}

	/**
	 * @since  2009-05-02
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject obj = super.toJsonObject();
		obj.put("location", location);
		return obj;
	}

	/**
	 * The location of the server
	 * 
	 * @return  the location of the server
	 * @since  2009-05-02
	 */
	public String getLocation() {
		return location;
	}
}