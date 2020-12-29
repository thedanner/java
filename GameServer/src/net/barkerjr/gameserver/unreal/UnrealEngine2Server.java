/*
 * Game Server Library - Querying server requests
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
package net.barkerjr.gameserver.unreal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

import net.barkerjr.gameserver.Player;
import net.barkerjr.gameserver.Players;
import net.barkerjr.gameserver.ReplyStream;
import net.barkerjr.gameserver.UdpServer;

/**
 * Provides generic parsing and querying for Unreal Engine 2 servers
 * 
 * @author  BarkerJr
 * @since  2009-08-30
 * @see
 * <a href="http://unreal.student.utwente.nl/UT2003-queryspec.html">Specification</a>
 */
public class UnrealEngine2Server extends UdpServer {
	/** The game port */
	private int gamePort = -1;
	
	/** The game type */
	private String gameType;

	/** The skill */
	private String skill;

	/**
	 * Creates a parser for an Unreal Engine 2 server
	 * 
	 * @param  address
	 * @since  2009-08-30
	 */
	public UnrealEngine2Server(InetSocketAddress address) {
		super(address);
	}

	/**
	 * Gets or creates an instance of the server at the given address.
	 * 
	 * @param address  the address of the server
	 * @return  the server
	 * @since 2009-08-30
	 */
	public static UnrealEngine2Server getInstance(
			InetSocketAddress address) {
		UnrealEngine2Server server;
		UdpServer udp = servers.get(address);
		if (udp instanceof UnrealEngine2Server) {
			server = (UnrealEngine2Server)udp;
		} else {
			server = new UnrealEngine2Server(address);
		}
		return server;
	}

	/**
	 * @since  2009-08-30
	 */
	@Override
	protected void parseData(byte[] data) throws IOException {
		ReplyStream stream = new ReplyStream(data);
		stream.readInt(); // Protocol version
		switch (stream.readByte()) {
			case 0: handleInformation(stream); break;
			case 1: handleRules(stream); break;
			case 2: handlePlayers(stream); break;
		}
	}
	
	/**
	 * Parses general server information
	 * 
	 * @since  2009-08-30
	 * @param  stream  the data to parse
	 */
	private void handleInformation(ReplyStream stream) {
		stream.readInt(); // Server ID
		stream.readByte(); // IP
		gamePort = stream.readInt(); // Game Port
		stream.readInt(); // Query Port
		stream.readByte(); // Server Name Length
		name = stream.readString(); // Server Name
		stream.readByte(); // Map Length
		map = stream.readString(); // Map
		stream.readByte(); // Game Type Length
		gameType = stream.readString(); // Game Type
		numberOfPlayers = stream.readInt(); // Player Count
		maximumPlayers = stream.readInt(); // Player Max
		stream.readInt(); // Ping
		stream.readInt(); // Server Flags
		stream.readByte(); // Skill Length
		skill = stream.readString(); // Skill
		fireLoadEvent(Request.INFORMATION);
	}

	/**
	 * Parses the server rules
	 * 
	 * @since  2009-08-30
	 * @param  stream  the data to parse
	 */
	private void handleRules(ReplyStream stream) {
		rules = new HashMap<String, String>();
		while (stream.readByte() > 1) {
			String option = stream.readString();
			String value;
			if (stream.readByte() > 0) {
				value = stream.readString();
			} else {
				value = "";
			}
			rules.put(option, value);
		}
		fireLoadEvent(Request.RULES);
	}

	/**
	 * Parses the players on the server
	 * 
	 * @since  2009-08-30
	 * @param  stream  the data to parse
	 */
	private void handlePlayers(ReplyStream stream) {
		players = new Players();
		while (true) {
			int id = stream.readInt();
			if (id == 0) {
				break;
			}
			Player player = players.get(id);
			stream.readByte(); // Name Length
			player.name = stream.readString();
			player.ping = stream.readInt();
			player.score = stream.readInt();
			stream.readInt(); // TODO Stats ID
		}
		fireLoadEvent(Request.PLAYERS);
	}

	/**
	 * Supports:
	 * <ul>
	 *  <li>Request.INFORMATION</li>
	 *  <li>Request.PLAYERS</li>
	 *  <li>Request.RULES</li>
	 * </ul>
	 * 
	 * {@inheritDoc}
	 * 
	 * @since 2009-08-30
	 */
	@Override
	public void load(Request... requests) throws IOException {
		List<Request> requestList = Arrays.asList(requests);
		if (requestList.contains(Request.INFORMATION)) {
			sendData(new byte[] {0, 0, 0, 0, 0});
		}
		if (requestList.contains(Request.PLAYERS)) {
			players = null;
			sendData(new byte[] {0, 0, 0, 0, 2});
		}
		if (requestList.contains(Request.RULES)) {
			rules = null;
			sendData(new byte[] {0, 0, 0, 0, 1});
		}
		super.load(requests);
	}

	/**
	 * The game port
	 * 
	 * @return  the game port
	 * @since  2009-08-30
	 */
	public int getGamePort() {
		return gamePort;
	}

	/**
	 * The game type
	 * 
	 * @return  the game type
	 * @since  2009-08-30
	 */
	public String getGameType() {
		return gameType;
	}

	/**
	 * @since  2009-08-30
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject json = super.toJsonObject();
		json.put("gamePort", gamePort);
		json.put("gameType", gameType);
		json.put("skill", skill);
		return json;
	}

	/**
	 * @since  2009-08-30
	 */
	@Override
	public Document toXmlDocument() {
		Document doc = super.toXmlDocument();
		Element server = doc.getRootElement();
		server.setAttribute("GamePort", Integer.toString(gamePort));
		server.setAttribute("GameType", gameType);
		server.setAttribute("Skill", skill);
		return doc;
	}
}