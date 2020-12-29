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
package net.barkerjr.gameserver.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.tools.bzip2.CBZip2InputStream;

/**
 * Wraps Apache's compression with convenience methods
 * 
 * @author BarkerJr
 * @since 2007-12-30
 * @see <a href="http://ant.apache.org">Apache Ant</a>
 */
public abstract class BZip2Compression {
	/**
	 * Decompresses the given bytes
	 * 
	 * @param compressed  the compressed data
	 * @return  the decompressed data
	 * @throws IOException  if the Ant library throws it
	 * @since 2007-12-30
	 */
	public static byte[] decompress(byte[] compressed) throws IOException {
		byte[] trimmed = trim(compressed);
		ByteArrayInputStream stream = new ByteArrayInputStream(trimmed);
		stream.read();	//B
		stream.read();	//Z
		CBZip2InputStream decompressor = new CBZip2InputStream(stream);
		ByteBuffer buff = ByteBuffer.allocate(trimmed.length * 10);
		int data = 0;
		while (data > -1) {
			data = decompressor.read();
			buff.put((byte)data);
		}
		decompressor.close();
		return trim(buff.array());
	}
	
	/**
	 * Trims trailing zeros from the end of the array, but leaves one.  This
	 * leaves one trailing zero because it might be the terminator on a string.
	 * 
	 * @param full  the data to trim
	 * @return  the trimmed data
	 */
	private static byte[] trim(byte[] full) {
		int lastIndex = full.length - 1;
		while(full[lastIndex] == 0) {
			lastIndex--;
		}
		return Arrays.copyOf(full, lastIndex + 2);
	}
}