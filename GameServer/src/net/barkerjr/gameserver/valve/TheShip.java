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
package net.barkerjr.gameserver.valve;

import org.jdom.Element;
import org.jdom.Namespace;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains information specific to The Ship servers
 * 
 * @author  BarkerJr
 * @since  2009-05-02
 */
public class TheShip {
	/**
	 * The mode of the server
	 * 
	 * @author BarkerJr
	 * @since  2009-05-02
	 */
	public enum GameMode {
		/**
		 * The server is in Hunt mode
		 * 
		 * @since  2009-05-02
		 */
		HUNT {
			/**
			 * @since  2009-05-02
			 */
			@Override
			public String toString() {
				return "Hunt";
			}
		},
		/**
		 * The server is in Elimiation mode
		 * 
		 * @since  2009-05-02
		 */
		ELIMINATION {
			/**
			 * @since  2009-05-02
			 */
			@Override
			public String toString() {
				return "Elimination";
			}
		},
		/**
		 * The server is in Duel mode
		 * 
		 * @since  2009-05-02
		 */
		DUEL {
			/**
			 * @since  2009-05-02
			 */
			@Override
			public String toString() {
				return "Duel";
			}
		},
		/**
		 * The server is in Deathmatch mode
		 * 
		 * @since  2009-05-02
		 */
		DEATHMATCH {
			/**
			 * @since  2009-05-02
			 */
			@Override
			public String toString() {
				return "Deathmatch";
			}
		},
		/**
		 * The server is in Team VIP mode
		 * 
		 * @since  2009-05-02
		 */
		TEAM_VIP {
			/**
			 * @since  2009-05-02
			 */
			@Override
			public String toString() {
				return "Team VIP";
			}
		},
		/**
		 * The server is in Team Elimination mode
		 * 
		 * @since  2009-05-02
		 */
		TEAM_ELIMINATION {
			/**
			 * @since  2009-05-02
			 */
			@Override
			public String toString() {
				return "Team Elimination";
			}
		}
	}
	
	GameMode gameMode;
	int witnessCount;
	int witnessSeconds;
	
	/**
	 * The game mode
	 * 
	 * @return  the game mode
	 * @since  2009-05-02
	 */
	public GameMode getGameMode() {
		return gameMode;
	}
	
	/**
	 * The witness count
	 * 
	 * @return  the witness count
	 * @since  2009-05-02
	 */
	public int getWitnessCount() {
		return witnessCount;
	}
	
	/**
	 * The witness seconds
	 * 
	 * @return  the witness seconds
	 * @since  2009-05-02
	 */
	public int getWitnessSeconds() {
		return witnessSeconds;
	}
	
	Element toXmlElement(Namespace namespace) {
		Element element = new Element("Ship", namespace);
		element.setAttribute("GameMode", gameMode.toString());
		element.setAttribute("WitnessCount",
				Integer.toString(witnessCount));
		element.setAttribute("WitnessSeconds",
				Integer.toString(witnessSeconds));
		return element;
	}

	JSONObject toJsonObject() throws JSONException {
		JSONObject ship = new JSONObject();
		ship.put("gameMode", gameMode.toString());
		ship.put("witnessCount", witnessCount);
		ship.put("witnessSeconds", witnessSeconds);
		return ship;
	}
}