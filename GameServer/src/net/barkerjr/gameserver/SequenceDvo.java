/*
 * Game Server Library - Querying server requests and returning them as XML
 * Copyright (C) 2007  BarkerJr <http://barkerjr.net/java/GameServer/>
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

/**
 * A simple structure to contain data about the packet sequence
 * 
 * @author BarkerJr
 * @since 2007-10-25
 */
public class SequenceDvo {
	/**
	 * The packet number on the current packet
	 * 
	 * @since 2007-10-25
	 */
	public int current;
	
	/**
	 * The total number of packets in this response
	 * 
	 * @since 2007-10-25
	 */
	public int total;

	/**
	 * @since 2007-11-28
	 */
	@Override
	public String toString() {
		return current + 1 + "/" + total;
	}
}