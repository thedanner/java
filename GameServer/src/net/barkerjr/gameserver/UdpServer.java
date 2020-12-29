/*
 * Game Server Library - Querying server requests and returning them as XML
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
package net.barkerjr.gameserver;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jdom.IllegalDataException;

/**
 * Provides generic parsing and querying servers via UDP
 * 
 * @author BarkerJr
 * @since 2007-10-25
 */
public abstract class UdpServer extends GameServer implements Closeable {
	/** The thread which reads data */
	private static Reader reader;
	
	/**
	 * The thread which reads data.
	 * 
	 * @author BarkerJr
	 */
	private static class Reader extends Thread {
		/**
		 * Sets the reader as a daemon.
		 */
		private Reader() {
			super("Reader");
			setDaemon(true);
		}

		/**
		 * Reads data until the socket is closed
		 * 
		 * @since 2007-10-25
		 */
		@Override
		public void run() {
			try {
				while (!servers.isEmpty()) {
					try {
						readData(getSocket());
					} catch (SocketTimeoutException e) {}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (socket != null) {
					socket.close();
				}
				reader = null;
				socket = null;
			}
		}
	}
	
	/**
	 * The servers which are active
	 * 
	 * @since 2007-11-18
	 */
	protected static final Map<InetSocketAddress, UdpServer> servers =
		Collections.synchronizedMap(new HashMap<InetSocketAddress, UdpServer>());
	
	/**
	 * The type-of-service to send queries under
	 * 
	 * @see DatagramSocket#setTrafficClass(int)
	 */
	private static final int IPTOS_RELIABILITY = 0x04;
	
	/** The socket used to query the server */
	private static DatagramSocket socket;
	
	/**
	 * Sets up the basic server information and DOM
	 * 
	 * @param ip  the IP address of the server
	 * @param port  the port of the server
	 * @since 2007-10-25
	 * @deprecated since 2007-11-10, use {@link #UdpServer(InetSocketAddress)}
	 */
	@Deprecated
	public UdpServer(InetAddress ip, int port) {
		this(new InetSocketAddress(ip, port));
	}
	/**
	 * Sets up the basic server information and DOM
	 * 
	 * @param address  the address of the server
	 * @since 2007-11-10
	 */
	public UdpServer(InetSocketAddress address) {
		super(address);
		servers.put(address, this);
		if (reader == null) {
			startReader();
		}
	}
	
	/**
	 * Creates a reader if there is none
	 */
	private static synchronized void startReader() {
		if (reader == null) {
			reader = new Reader();
			reader.start();
		}
	}
	
	/**
	 * Reads a packet parses it
	 * 
	 * @param sock  the socket to read from
	 * @throws IOException  if we fail to read data
	 * @since 2007-10-25
	 */
	private static void readData(DatagramSocket sock) throws IOException {
		byte[] buff = new byte[1400];
		DatagramPacket packet = new DatagramPacket(buff, buff.length);
		sock.receive(packet);
		UdpServer server = servers.get(packet.getSocketAddress());
		if (server != null) {
			try {
				server.parseData(packet.getData());
			} catch (IllegalDataException e) {
				server.handleError(e);
			}
		}
	}
	
	/**
	 * Parses the given data
	 * 
	 * @param data  the bytes in the packet
	 * @throws IOException  if there is a problem
	 * @since 2007-11-10
	 */
	protected abstract void parseData(byte[] data) throws IOException;
	
	/**
	 * Creates or returns an existing socket to the server
	 * 
	 * @return  the socket to send data through
	 * @throws SocketException  if there was an error creating the socket
	 * @since 2007-10-25
	 */
	private static DatagramSocket getSocket() throws SocketException {
		if (socket == null) {
			createSocket();
		}
		return socket;
	}
	
	/**
	 * Creates a socket if there is none already
	 * 
	 * @throws SocketException  if there was an error creating the socket
	 */
	private static synchronized void createSocket() throws SocketException {
		if (socket == null) {
			socket = new DatagramSocket();
			try {
				getSocket().setSoTimeout(60000);
				socket.setTrafficClass(IPTOS_RELIABILITY);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Removes internal references to this server
	 * 
	 * @since 2007-10-25
	 */
	public void close() {
		servers.remove(this);
	}
	
	/**
	 * Sends the given data to the server
	 * 
	 * @param data  the data to send
	 * @throws IOException  if there was an error sending the request
	 * @since 2007-11-18
	 */
	protected void sendData(String data) throws IOException {
		sendData(data.getBytes("UTF-8"));
	}
	/**
	 * Sends the given data to the server
	 * 
	 * @param data  the data to send
	 * @throws IOException  if there was an error sending the request
	 * @since 2007-10-25
	 */
	protected void sendData(byte[] data) throws IOException {
		DatagramPacket req = new DatagramPacket(data, data.length, address);
		DatagramSocket sock = getSocket();
		sock.send(req);
	}
}