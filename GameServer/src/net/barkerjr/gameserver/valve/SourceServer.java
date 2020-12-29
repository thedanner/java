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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

import net.barkerjr.gameserver.ReplyStream;
import net.barkerjr.gameserver.SequenceDvo;
import net.barkerjr.gameserver.UdpServer;
import net.sourceforge.rconed.SourceRcon;
import net.sourceforge.rconed.exception.BadRcon;
import net.sourceforge.rconed.exception.ResponseEmpty;

/**
 * Provides parsing and Source-specific server query handling.  Source is the
 * Half-Life 2 engine.
 * 
 * @author BarkerJr
 * @since 2007-10-25
 * @see <a href="http://developer.valvesoftware.com/wiki/Server_Queries">Specification</a>
 */
public class SourceServer extends ValveServer {
	/**
	 * Creates a server for the Valve Source engine, defaulting to port 27015
	 * 
	 * @param ip  the IP address of the server
	 * @since 2007-10-25
	 * @deprecated since 2007-11-10, use {@link #SourceServer(InetSocketAddress)}
	 */
	@Deprecated
	public SourceServer(InetAddress ip) {
		this(new InetSocketAddress(ip, 27015));
	}
	/**
	 * Creates a server for the Valve Source engine
	 * 
	 * @param ip  the IP address of the server
	 * @param port  the port of the server
	 * @since 2007-10-25
	 * @deprecated since 2007-11-10, use {@link #SourceServer(InetSocketAddress)}
	 */
	@Deprecated
	public SourceServer(InetAddress ip, int port) {
		this(new InetSocketAddress(ip, port));
	}
	/**
	 * Creates a server for the Valve Source engine
	 * 
	 * @param address  the address of the server
	 * @since 2007-11-10
	 */
	public SourceServer(InetSocketAddress address) {
		super(address);
	}

	/**
	 * Gets or creates an instance of the server at the given address.
	 * 
	 * @param address  the address of the server
	 * @return  the server
	 * @since 2007-11-18
	 */
	public static SourceServer getInstance(InetSocketAddress address) {
		SourceServer server;
		UdpServer udp = servers.get(address);
		if (udp instanceof SourceServer) {
			server = (SourceServer)udp;
		} else {
			server = new SourceServer(address);
		}
		return server;
	}
	
	/**
	 * Parses the next two bytes for the packet sequence numbers
	 * 
	 * {@inheritDoc}
	 * 
	 * @since 2007-10-25
	 */
	@Override
	protected SequenceDvo parsePacketNumber(ReplyStream buffer) {
		SequenceDvo dvo = new SequenceDvo();
		dvo.total = buffer.readUnsignedByte();
		dvo.current = buffer.readUnsignedByte();
		return dvo;
	}

	/**
	 * @since  2008-02-03
	 * @see SourceRcon#send(String, int, String, String)
	 */
	@Override
	public String sendRcon(String command)
			throws BadRcon, ResponseEmpty, SocketTimeoutException {
		String reply = SourceRcon.send(address.getAddress().getHostAddress(),
				address.getPort(), rconPassword.toString(), command);
		//TODO Handle the Response
		return reply;
	}
}