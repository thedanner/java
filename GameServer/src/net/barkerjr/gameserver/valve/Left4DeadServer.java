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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;

import net.barkerjr.gameserver.Player;
import net.barkerjr.gameserver.Players;
import net.barkerjr.gameserver.ReplyStream;
import net.barkerjr.gameserver.UdpServer;
import net.barkerjr.gameserver.util.Calendar;

/**
 * Provides workarounds for bugs Valve introduced to Left4Dead circa September
 * 29, 2009
 * 
 * @author BarkerJr
 * @since  2009-10-06
 */
public class Left4DeadServer extends SourceServer {
	/**
	 * Creates a server for the Left 4 Dead game
	 * 
	 * @param address  the address of the server
	 * @since  2009-10-06
	 */
	public Left4DeadServer(InetSocketAddress address) {
		super(address);
	}

	/**
	 * Gets or creates an instance of the server at the given address.
	 * 
	 * @param address  the address of the server
	 * @return  the server
	 * @since  2009-10-06
	 */
	public static Left4DeadServer getInstance(InetSocketAddress address) {
		Left4DeadServer server;
		UdpServer udp = servers.get(address);
		if (udp instanceof SourceServer) {
			server = (Left4DeadServer)udp;
		} else {
			server = new Left4DeadServer(address);
		}
		return server;
	}
	
	/**
	 * Requests A2S_USERS instead of A2S_SERVERQUERY_GETCHALLENGE, which is
	 * broken
	 * 
	 * {@inheritDoc}
	 * 
	 * @since  2009-10-06
	 */
	@Override
	protected void loadChallenge() throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[9]);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(-1);
		byte data = 0x55;
		buffer.put(data);
		buffer.putInt(-1);
		sendData(buffer.array());
	}
	
	/**
	 * Generates its own Index, since Left 4 Dead servers always use an Index
	 * of zero
	 * 
	 * {@inheritDoc}
	 * 
	 * @since  2009-10-06
	 */
	@Override
	protected void handlePlayers(ReplyStream stream) {
		int playerCount = stream.readUnsignedByte();
		if (players == null) {
			players = new Players(playerCount);
		}
		HashSet<Player> newList =
			new HashSet<Player>(playerCount);
		for (int x = 0; x < playerCount; x++) {
			stream.readUnsignedByte();	// Index
			Player player = players.get(x);
			String name = stream.readString();
			if (name.length() > 0) {
				player.name = name;
			}
			player.kills = stream.readInt();
			player.secondsConnected = stream.readFloat();
			newList.add(player);
		}
		players.values().retainAll(newList);
		players.updated = new Calendar();
		updated = new Calendar();
		fireLoadEvent(Request.PLAYERS);
	}
}