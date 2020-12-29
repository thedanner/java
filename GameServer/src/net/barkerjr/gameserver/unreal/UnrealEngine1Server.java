/*
 * Game Server Library - Querying server requests
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
package net.barkerjr.gameserver.unreal;

import java.net.InetSocketAddress;

import net.barkerjr.gameserver.UdpServer;
import net.barkerjr.gameserver.gamespy.GameSpyServer;

import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides generic parsing and querying for Unreal Engine 1 servers
 * 
 * @author BarkerJr
 * @since 2007-11-18
 * @see
 * <a href="http://wiki.beyondunreal.com/Legacy:UT_Server_Query">Specification</a>
 */
public class UnrealEngine1Server extends GameSpyServer {
	private String mapTitle;
	private String minimumNetworkVersion;

	/**
	 * Sets up the basic server information and DOM
	 * 
	 * @param address  the address of the server
	 * @since 2007-11-18
	 */
	public UnrealEngine1Server(InetSocketAddress address) {
		super(address);
	}
	
	/**
	 * Gets or creates an instance of the server at the given address.
	 * 
	 * @param address  the address of the server
	 * @return  the server
	 * @since 2007-11-18
	 */
	public static UnrealEngine1Server getInstance(
			InetSocketAddress address) {
		UnrealEngine1Server server;
		UdpServer udp = servers.get(address);
		if (udp instanceof UnrealEngine1Server) {
			server = (UnrealEngine1Server)udp;
		} else {
			server = new UnrealEngine1Server(address);
		}
		return server;
	}

	/**
	 * @since 2007-11-18
	 */
	@Override
	protected Request storePair(String key, String value) {
		if (key.equals("location")) {
			location = getLocation(value);
			return Request.BASIC;
		}
		if (key.equals("gamever")) {
			version = value;
			return Request.INFORMATION;
		}
		if (key.equals("maptitle")) {
			mapTitle = value;
			return Request.INFORMATION;
		}
		if (key.equals("minnetver")) {
			minimumNetworkVersion = value;
			return Request.INFORMATION;
		}
		if (key.equals("listenserver")) {
			if (Boolean.parseBoolean(value)) {
				dedicated = Dedicated.LISTEN;
			} else {
				dedicated = Dedicated.DEDICATED;
			}
			return Request.INFORMATION;
		}
		if (key.startsWith("player_")) {
			getPlayer(key).name = value;
			return Request.PLAYERS;
		}
		if (key.startsWith("frags_")) {
			getPlayer(key).kills = Integer.parseInt(value);
			return Request.PLAYERS;
		}
		if (key.startsWith("mesh_")) {
			getPlayer(key).mesh = value.trim();
			return Request.PLAYERS;
		}
		if (key.startsWith("skin_")) {
			getPlayer(key).skin = value.trim();
			return Request.PLAYERS;
		}
		if (key.startsWith("face_")) {
			getPlayer(key).face = value.trim();
			return Request.PLAYERS;
		}
		if (key.startsWith("ngsecret_")) {
			getPlayer(key).ngStats = value.trim();
			return Request.PLAYERS;
		}
		return super.storePair(key, value);
	}

	public String getMapTitle() {
		return mapTitle;
	}

	public String getMinimumNetworkVersion() {
		return minimumNetworkVersion;
	}

	/**
	 * @since  2009-05-02
	 */
	@Override
	public Document toXmlDocument() {
		Document doc = super.toXmlDocument();
		Element server = doc.getRootElement();
		server.setAttribute("MapTitle", mapTitle);
		server.setAttribute("MinimumNetworkVersion", minimumNetworkVersion);
		return doc;
	}

	/**
	 * @since  2009-05-02
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject obj = super.toJsonObject();
		obj.put("mapTitle", mapTitle);
		obj.put("minimumNetworkVersion", minimumNetworkVersion);
		return obj;
	}
}