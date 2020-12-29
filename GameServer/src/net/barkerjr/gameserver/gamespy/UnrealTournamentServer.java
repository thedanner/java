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
package net.barkerjr.gameserver.gamespy;

import java.net.InetSocketAddress;

import net.barkerjr.gameserver.UdpServer;
import net.barkerjr.gameserver.unreal.UnrealEngine1Server;

/**
 * Provides generic parsing and querying for Unreal Tournament servers
 * 
 * @author BarkerJr
 * @since 2007-11-18
 * @see
 * <a href="http://wiki.beyondunreal.com/Legacy:UT_Server_Query">Specification</a>
 * @deprecated  since 2009-08-30, use {@link UnrealEngine1Server}
 */
public class UnrealTournamentServer extends UnrealEngine1Server {
	/**
	 * Sets up the basic server information and DOM
	 * 
	 * @param address  the address of the server
	 * @since 2007-11-18
	 */
	public UnrealTournamentServer(InetSocketAddress address) {
		super(address);
	}
	
	/**
	 * Gets or creates an instance of the server at the given address.
	 * 
	 * @param address  the address of the server
	 * @return  the server
	 * @since 2007-11-18
	 */
	public static UnrealTournamentServer getInstance(
			InetSocketAddress address) {
		UnrealTournamentServer server;
		UdpServer udp = servers.get(address);
		if (udp instanceof UnrealTournamentServer) {
			server = (UnrealTournamentServer)udp;
		} else {
			server = new UnrealTournamentServer(address);
		}
		return server;
	}
}