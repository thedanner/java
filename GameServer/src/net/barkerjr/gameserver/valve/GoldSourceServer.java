/*
 * Game Server Library - Querying server requests and returning them as XML
 * Copyright (C) 2007-2008  BarkerJr <http://barkerjr.net/java/GameServer/>
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.barkerjr.gameserver.ReplyStream;
import net.barkerjr.gameserver.SequenceDvo;
import net.barkerjr.gameserver.UdpServer;
import net.sourceforge.rconed.Rcon;
import net.sourceforge.rconed.exception.BadRcon;
import net.sourceforge.rconed.exception.ResponseEmpty;

/**
 * Provides parsing and Gold-Source-specific server query handling.  Gold Source
 * is the Half-Life 1 engine.
 * 
 * @author BarkerJr
 * @since 2007-10-25
 * @see <a href="http://developer.valvesoftware.com/wiki/Server_Queries">Specification</a>
 */
public class GoldSourceServer extends ValveServer {
	/**
	 * Creates a server for the Valve Gold Source engine, defaulting to port
	 * 27015
	 * 
	 * @param ip  the IP address of the server
	 * @since 2007-10-25
	 * @deprecated since 2007-11-10, use {@link #GoldSourceServer(InetSocketAddress)}
	 */
	@Deprecated
	public GoldSourceServer(InetAddress ip) {
		this(ip, 27015);
	}
	/**
	 * Creates a server for the Valve Gold Source engine
	 * 
	 * @param ip  the IP address of the server
	 * @param port  the port of the server
	 * @since 2007-10-25
	 * @deprecated since 2007-11-10, use {@link #GoldSourceServer(InetSocketAddress)}
	 */
	@Deprecated
	public GoldSourceServer(InetAddress ip, int port) {
		this(new InetSocketAddress(ip, port));
	}
	/**
	 * Creates a server for the Valve Gold Source engine
	 * 
	 * @param address  the address of the server
	 * @since 2007-11-10
	 */
	public GoldSourceServer(InetSocketAddress address) {
		super(address);
	}
	
	/**
	 * Gets or creates an instance of the server at the given address.
	 * 
	 * @param address  the address of the server
	 * @return  the server
	 * @since 2007-11-18
	 */
	public static GoldSourceServer getInstance(InetSocketAddress address) {
		GoldSourceServer server;
		UdpServer udp = servers.get(address);
		if (udp instanceof GoldSourceServer) {
			server = (GoldSourceServer)udp;
		} else {
			server = new GoldSourceServer(address);
		}
		return server;
	}

	/**
	 * Parses the next byte for the packet sequence numbers.
	 * 
	 * {@inheritDoc}
	 * 
	 * @since 2007-10-25
	 */
	@Override
	protected SequenceDvo parsePacketNumber(ReplyStream buffer) {
		SequenceDvo dvo = new SequenceDvo();
		int sequence = buffer.readUnsignedByte();
		dvo.total = sequence % 16;
		dvo.current = sequence / 16;
		return dvo;
	}

	/**
	 * @since  2008-02-03
	 * @see Rcon#send(int, String, int, String, String)
	 */
	@Override
	public String sendRcon(String command)
			throws BadRcon, ResponseEmpty, SocketTimeoutException {
		String reply = Rcon.send(0, address.getAddress().getHostAddress(),
				address.getPort(), rconPassword.toString(), command);
		//TODO Handle the Response
		return reply;
	}
	
	/**
	 * Workaround for broken A2S_SERVERQUERY_GETCHALLENGE
	 * 
	 * {@inheritDoc}
	 * 
	 * @throws IOException  if there was an error sending the request
	 * @since 2008-11-26
	 * @see <a href="http://developer.valvesoftware.com/wiki/Talk:Server_Queries#A2S_SERVERQUERY_GETCHALLENGE_not_working_since_last_HLDS_update">Problem</a>
	 */
	protected void loadChallenge() throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[9]);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(-1);
		byte data = 0x55;
		buffer.put(data);
		buffer.putInt(-1);
		sendData(buffer.array());
	}
}