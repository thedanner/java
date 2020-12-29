/*
 * Game Server Library - Querying server requests and returning them as XML
 * Copyright (C) 2007, 2009  BarkerJr <http://www.barkerjr.net/java/GameServer/>
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

import java.net.InetSocketAddress;

import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

import net.barkerjr.gameserver.UdpServer;

/**
 * Provides generic parsing and querying for Battlefield 1942 servers
 * 
 * @author BarkerJr
 * @since 2007-11-18
 * @see
 * <a href="http://web.archive.org/web/20070831185845/http://dev.kquery.com/index.php?article=25">Specification</a>
 */
public class Battlefield1942Server extends GameSpyServer {
	private String language;

	/**
	 * Sets up the basic server information and DOM
	 * 
	 * @param address  the address of the server
	 * @since 2007-11-18
	 */
	public Battlefield1942Server(InetSocketAddress address) {
		super(address);
	}
	
	/**
	 * Gets or creates an instance of the server at the given address.
	 * 
	 * @param address  the address of the server
	 * @return  the server
	 * @since 2007-11-18
	 */
	public static Battlefield1942Server getInstance(
			InetSocketAddress address) {
		Battlefield1942Server server;
		UdpServer udp = servers.get(address);
		if (udp instanceof Battlefield1942Server) {
			server = (Battlefield1942Server)udp;
		} else {
			server = new Battlefield1942Server(address);
		}
		return server;
	}

	/**
	 * @since 2007-11-18
	 */
	@Override
	protected Request storePair(String key, String value) {
		if (key.equals("language")) {
			language = value;
			return Request.BASIC;
		}
		if (key.equals("location")) {
			location = getLocation(value);
			return Request.BASIC;
		}
		if (key.equals("gamever")) {
			version = value;
			return Request.BASIC;
		}
		if (key.equals("dedicated")) {
			if (Boolean.parseBoolean(value)) {
				dedicated = Dedicated.DEDICATED;
			} else {
				dedicated = Dedicated.LISTEN;
			}
			return Request.INFORMATION;
		}
		if (key.equals("version")) {
			version = value;
			return Request.INFORMATION;
		}
		if (key.startsWith("playername_")) {
			getPlayer(key).name = value;
			return Request.PLAYERS;
		}
		if (key.startsWith("score_")) {
			getPlayer(key).score = Integer.parseInt(value);
			return Request.PLAYERS;
		}
		if (key.startsWith("kills_")) {
			getPlayer(key).kills = Integer.parseInt(value);
			return Request.PLAYERS;
		}
		if (key.startsWith("deaths_")) {
			getPlayer(key).deaths = Integer.parseInt(value);
			return Request.PLAYERS;
		}
		if (key.startsWith("keyhash_")) {
			return null;
		}
		return super.storePair(key, value);
	}

	/**
	 * @since  2009-05-02
	 */
	@Override
	public Document toXmlDocument() {
		Document doc = super.toXmlDocument();
		Element server = doc.getRootElement();
		server.setAttribute("Language", language);
		return doc;
	}

	/**
	 * @since  2009-05-02
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject obj = super.toJsonObject();
		obj.put("language", language);
		return obj;
	}

	/**
	 * The language of the server
	 * 
	 * @return  the language of the server
	 * @since  2009-05-02
	 */
	public String getLanguage() {
		return language;
	}
}