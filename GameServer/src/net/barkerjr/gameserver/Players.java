/*
 * Game Server Library - Querying server requests and returning them as XML
 * Copyright (C) 2009  BarkerJr <http://www.barkerjr.net/java/GameServer/>
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
package net.barkerjr.gameserver;

import java.util.Collection;
import java.util.HashMap;

import org.jdom.Element;
import org.jdom.Namespace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.barkerjr.gameserver.util.Calendar;

/**
 * Contains the players in the server
 * 
 * @author  BarkerJr
 * @since  2009-05-02
 */
public class Players {
	private static final long serialVersionUID = 1L;
	
	private HashMap<Integer, Player> backing;
	
	/**
	 * The time the players were updated
	 * 
	 * @since  2009-05-02
	 */
	public Calendar updated;
	
	/**
	 * Create a new collection of players
	 * 
	 * @since  2009-05-02
	 */
	public Players() {
		backing = new HashMap<Integer, Player>();
	}

	/**
	 * Create a new collection of players
	 * 
	 * @param  capacity  the number of players to expect
	 * @since  2009-05-02
	 */
	public Players(int capacity) {
		backing = new HashMap<Integer, Player>(capacity);
	}

	/**
	 * The time the players were last updated
	 * 
	 * @return  the time the players were last updated
	 * @since  2009-05-02
	 */
	public Calendar getUpdated() {
		return updated;
	}

	/**
	 * Fetches or creates the player for the given index
	 * 
	 * @param index  the player to fetch
	 * @return  the player requested
	 * @since  2009-05-02
	 */
	public Player get(int index) {
		Player player = backing.get(index);
		if (player == null) {
			player = new Player(index);
			backing.put(index, player);
			updated = new Calendar();
		}
		return player;
	}
	
	Element toXmlElement(Namespace namespace) {
		Element element = new Element("Players", namespace);
		for (Player player : backing.values().toArray(new Player[0]).clone()) {
			element.addContent(player.toXmlElement(namespace));
		}
		if (updated != null) {
			element.setAttribute("Updated", updated.toString());
		}
		return element;
	}

	JSONObject toJsonObject() throws JSONException {
		JSONObject players = new JSONObject();
		if (updated != null) {
			players.put("updated", updated.toString());
		}
		JSONArray array = new JSONArray();
		for (Player player : backing.values()) {
			array.put(player.toJsonObject());
		}
		players.put("players", array);
		return players;
	}
	
	public Collection<Player> values() {
		return backing.values();
	}
}