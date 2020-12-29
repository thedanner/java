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
package net.barkerjr.gameserver;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * Wraps the byte array to provide convenient getters for different data types
 * 
 * @author BarkerJr
 * @since 2007-10-25
 */
public class ReplyStream {
	/**
	 * The buffer to get the data from
	 * 
	 * @since 2007-10-25
	 */
	private ByteBuffer buffer;
	
	/**
	 * Sets up the stream with the given data
	 * 
	 * @param data  the data to read from
	 * @since 2007-10-25
	 */
	public ReplyStream(byte[] data) {
		this(ByteBuffer.wrap(data));
	}
	/**
	 * Sets up the stream with the given data
	 * 
	 * @param buffer  a buffer to read data from
	 * @since 2007-10-25
	 */
	public ReplyStream(ByteBuffer buffer) {
		this.buffer = buffer;
		buffer.order(ByteOrder.LITTLE_ENDIAN);
	}

	/**
	 * Determines the position we are currently at within the data
	 * 
	 * @return  the position
	 * @see ByteBuffer#position()
	 * @since 2007-10-25
	 */
	public int getPosition() {
		return buffer.position();
	}
	
	/**
	 * Reads the next byte in the data
	 * 
	 * @return  the byte
	 * @see ByteBuffer#get()
	 * @since 2007-10-25
	 */
	public int readByte() {
		return buffer.get();
	}
	
	/**
	 * Reads the next unsigned byte in the data
	 * 
	 * @return  the byte
	 * @since 2007-10-25
	 */
	public int readUnsignedByte() {
		int data = readByte();
		if (data < 0) {
			data += (Byte.MAX_VALUE + 1) * 2;
		}
		return data;
	}
	
	/**
	 * Reads the next float in the data
	 * 
	 * @return  the float
	 * @see ByteBuffer#getFloat()
	 * @since 2007-10-25
	 */
	public float readFloat() {
		return buffer.getFloat();
	}
	
	/**
	 * Reads the next integer in the data
	 * 
	 * @return  the integer
	 * @see ByteBuffer#getInt()
	 * @since 2007-10-25
	 */
	public int readInt() {
		return buffer.getInt();
	}
	
	/**
	 * Reads the next short in the data
	 * 
	 * @return  the short
	 * @see ByteBuffer#getShort()
	 * @since 2007-10-25
	 */
	public int readShort() {
		return buffer.getShort();
	}
	
	/**
	 * Reads the next String in the data
	 * 
	 * @return  the String
	 * @since 2007-10-25
	 */
	public String readString() {
		byte[] buff = new byte[1400];
		int x;
		for (x = 0; x < buff.length; x++) {
			buff[x] = buffer.get();
			if (buff[x] == 0) {
				break;
			}
		}
		return new String(buff, 0, x, Charset.forName("UTF-8"));
	}
	
	/**
	 * @return  the entire data array
	 * @since 2007-10-25
	 */
	@Override
	public String toString() {
		return buffer.toString();
	}
}