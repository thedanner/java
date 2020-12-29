/*
 * Game Server Library - Querying server requests and returning them as XML
 * Copyright (C) 2008, 2009  BarkerJr <http://barkerjr.net/java/GameServer/>
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

import java.net.InetSocketAddress;

/**
 * Fetches lists of servers from the master servers provided by Valve.
 * 
 * @author  BarkerJr
 * @since  2008-02-03
 */
public class SourceServerList extends ValveServerList<SourceServer> {
	/**
	 * Initializes the list for Source servers
	 * 
	 * @since  2008-02-03
	 */
	public SourceServerList() {
		// Copied from C:\Program Files\Steam\config\MasterServers.vdf
		super("hl2master.barkerjr.net", 27011);
	}

	/**
	 * @since  2008-02-03
	 */
	@Override
	protected SourceServer loadServer(InetSocketAddress address) {
		return SourceServer.getInstance(address);
	}
}