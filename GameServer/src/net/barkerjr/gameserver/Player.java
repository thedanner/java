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

import org.jdom.Element;
import org.jdom.Namespace;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains a player
 * 
 * @author  BarkerJr
 * @since  2009-05-02
 */
public class Player {
	/**
	 * The player's name
	 * 
	 * @since  2009-05-02
	 */
	public String name;
	
	/**
	 * The number of kills
	 * 
	 * @since  2009-05-02
	 */
	public int kills;
	
	/**
	 * The number of seconds the player has been connected
	 * 
	 * @since  2009-05-02
	 */
	public float secondsConnected;
	
	private int index;

	/**
	 * The ping of the user
	 * 
	 * @since  2009-05-02
	 */
	public int ping;

	/**
	 * The team the user is on
	 * 
	 * @since  2009-05-02
	 */
	public int team;

	/**
	 * The number of points
	 * 
	 * @since  2009-05-02
	 */
	public int score;

	/**
	 * The number of deaths
	 * 
	 * @since  2009-05-02
	 */
	public int deaths;

	public String mesh;
	public String skin;
	public String face;
	public String ngStats;
	
	Player(int index) {
		this.index = index;
	}
	
	/**
	 * The name of the player
	 * 
	 * @return  the name of the player
	 * @since  2009-05-02
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * The number of kills
	 * 
	 * @return  the number of kills
	 * @since  2009-05-02
	 */
	public int getKills() {
		return kills;
	}
	
	/**
	 * The number of seconds the player has been connected
	 * 
	 * @return  the number of seconds the player has been connected
	 * @since  2009-05-02
	 */
	public float getSecondsConnected() {
		return secondsConnected;
	}
	
	/**
	 * The player index
	 * 
	 * @return  the player index
	 * @since  2009-05-02
	 */
	public int getIndex() {
		return index;
	}
	
	Element toXmlElement(Namespace namespace) {
		Element element = new Element("Player", namespace);
		element.setAttribute("Index", Integer.toString(index));
		element.setAttribute("Name", name);
		element.setAttribute("Kills", Integer.toString(kills));
		element.setAttribute("SecondsConnected",
				Float.toString(secondsConnected));
		element.setAttribute("Ping", Integer.toString(ping));
		element.setAttribute("Team", Integer.toString(team));
		element.setAttribute("Score", Integer.toString(score));
		element.setAttribute("Deaths", Integer.toString(deaths));
		if (mesh != null) {
			element.setAttribute("Mesh", mesh);
		}
		if (skin != null) {
			element.setAttribute("Skin", skin);
		}
		if (face != null) {
			element.setAttribute("Face", face);
		}
		if (ngStats != null) {
			element.setAttribute("NgStats", ngStats);
		}
		return element;
	}

	JSONObject toJsonObject() throws JSONException {
		JSONObject player = new JSONObject();
		player.put("index", index);
		player.put("name", name);
		player.put("kills", kills);
		player.put("secondsConnected", secondsConnected);
		player.put("ping", ping);
		player.put("team", team);
		player.put("score", score);
		player.put("deaths", deaths);
		player.put("mesh", mesh);
		player.put("skin", skin);
		player.put("face", face);
		player.put("ngStats", ngStats);
		return player;
	}
	
	/**
	 * The player ping
	 * 
	 * @return  the player ping
	 * @since  2009-05-02
	 */
	public int getPing() {
		return ping;
	}
	
	/**
	 * The player team
	 * 
	 * @return  the player team
	 * @since  2009-05-02
	 */
	public int getTeam() {
		return team;
	}
	
	/**
	 * The number of points
	 * 
	 * @return  the number of points
	 * @since  2009-05-02
	 */
	public int getScore() {
		return score;
	}

	/**
	 * The number of deaths
	 * 
	 * @return  the number of deaths
	 * @since  2009-05-02
	 */
	public int getDeaths() {
		return deaths;
	}

	public String getMesh() {
		return mesh;
	}

	public String getSkin() {
		return skin;
	}

	public String getFace() {
		return face;
	}

	public String getNgStats() {
		return ngStats;
	}
}