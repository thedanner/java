/*
 * Game Server Library - Querying server requests and returning them as XML
 * Copyright (C) 2008, 2009  BarkerJr <http://www.barkerjr.net/java/GameServer/>
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

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.barkerjr.gameserver.Base;

import org.xbill.DNS.Address;

/**
 * Fetches lists of servers from the master servers provided by Valve.
 * 
 * @author  BarkerJr
 * @since  2008-02-03
 * @param <T>  the type of server supported
 * @see <a href="http://developer.valvesoftware.com/wiki/Master_Server_Query_Protocol">Specification</a>
 */
public abstract class ValveServerList<T extends ValveServer>
		extends Base implements Iterable<T> {
	/**
	 * Provides an iterator to fetch and parse the servers
	 * 
	 * @author BarkerJr
	 * @since  2008-02-03
	 */
	public class ServerIterator implements Iterator<T>, Closeable {
		/**
		 * The type-of-service to send queries under
		 * 
		 * @see DatagramSocket#setTrafficClass(int)
		 */
		private static final int IPTOS_RELIABILITY = 0x04;
		
		/**
		 * The region we're fetching servers from
		 * 
		 * @since  2008-02-03
		 */
		public final Region region;
		
		/** The next server to return */
		private T nextServer;
		
		/** The last server returned */
		private T lastServer;
		
		/**
		 * The number of milliseconds to wait for each response from the master
		 * server
		 */
		private int msToWait;

		/** The filters to send in the query */
		public byte[] filter;

		/** The raw data from the master server */
		private ByteBuffer rawData;

		/** The socket to send the request through */
		private DatagramSocket socket;
		
		/** The address to use */
		private InetAddress masterAddress;
		
		/**
		 * Generates an iterator and gets ready to fetch servers
		 * 
		 * @param region  the region to fetch from
		 * @param type  the server type
		 * @param secure  only secure servers should be fetched
		 * @param gameDir  the mod to fetch servers from.  For example,
		 * "cstrike" or "synergy"
		 * @param map  the map to restrict the servers to
		 * @param linux  only Linux servers should be fetched
		 * @param empty  if empty servers should not be included
		 * @param full  if full servers should not be included
		 * @param proxy  only proxy (TV) servers should be fetched
		 * @param notApplicationId  the application ID to filter out
		 * @param noPlayers  only empty servers should be fetched
		 * @param whitelisted  only whitelisted servers should be fetched
		 * @param msToWait  the number of milliseconds to wait for each reply
		 * from the master server
		 */
		private ServerIterator(Region region, Type type, Boolean secure,
				String gameDir, String map, Boolean linux, Boolean empty,
				Boolean full, Boolean proxy, int notApplicationId,
				Boolean noPlayers, Boolean whitelisted, int msToWait) {
			this(region, type, secure, gameDir, map, linux, empty, full, proxy,
					notApplicationId, noPlayers, whitelisted);
			this.msToWait = msToWait;
		}
		/**
		 * Generates an iterator and gets ready to fetch servers
		 * 
		 * @param region  the region to fetch from
		 * @param type  the server type
		 * @param secure  only secure servers should be fetched
		 * @param gameDir  the mod to fetch servers from.  For example,
		 * "cstrike" or "synergy"
		 * @param map  the map to restrict the servers to
		 * @param linux  only Linux servers should be fetched
		 * @param empty  if empty servers should not be included
		 * @param full  if full servers should not be included
		 * @param proxy  only proxy (TV) servers should be fetched
		 * @param notApplicationId  the application ID to filter out
		 * @param msToWait  the number of milliseconds to wait for each reply
		 * from the master server
		 * @since  2009-01-26
		 * @deprecated  Since 2009-05-02
		 */
		@Deprecated
		protected ServerIterator(Region region, Type type, Boolean secure,
				String gameDir, String map, Boolean linux, Boolean empty,
				Boolean full, Boolean proxy, int notApplicationId,
				int msToWait) {
			this(region, type, secure, gameDir, map, linux, empty, full, proxy,
					notApplicationId, null, null, msToWait);
		}
		/**
		 * Generates an iterator and gets ready to fetch servers
		 * 
		 * @param region  the region to fetch from
		 * @param type  the server type
		 * @param secure  only secure servers should be fetched
		 * @param gameDir  the mod to fetch servers from.  For example,
		 * "cstrike" or "synergy"
		 * @param map  the map to restrict the servers to
		 * @param linux  only Linux servers should be fetched
		 * @param empty  if empty servers should not be included
		 * @param full  if full servers should not be included
		 * @param proxy  only proxy (TV) servers should be fetched
		 * @since  2008-02-03
		 * @deprecated  Since 2009-01-26, use
		 * {@link ValveServerList.ServerIterator#ServerIterator(Region, Type, Boolean, String, String, Boolean, Boolean, Boolean, Boolean, int)}
		 */
		@Deprecated
		protected ServerIterator(Region region, Type type, Boolean secure,
				String gameDir, String map, Boolean linux, Boolean empty,
				Boolean full, Boolean proxy) {
			this(region, type, secure, gameDir, map, linux, empty, full, proxy, -1);
		}
		/**
		 * Generates an iterator and gets ready to fetch servers
		 * 
		 * @param region  the region to fetch from
		 * @param type  the server type
		 * @param secure  only secure servers should be fetched
		 * @param gameDir  the mod to fetch servers from.  For example,
		 * "cstrike" or "synergy"
		 * @param map  the map to restrict the servers to
		 * @param linux  only Linux servers should be fetched
		 * @param empty  if empty servers should not be included
		 * @param full  if full servers should not be included
		 * @param proxy  only proxy (TV) servers should be fetched
		 * @param notApplicationId  the application ID to filter out
		 * @since  2009-01-26
		 * @deprecated  Since 2009-05-02
		 */
		@Deprecated
		protected ServerIterator(Region region, Type type, Boolean secure,
				String gameDir, String map, Boolean linux, Boolean empty,
				Boolean full, Boolean proxy, int notApplicationId) {
			this(region, type, secure, gameDir, map, linux, empty, full, proxy, notApplicationId, null, null);
		}
		/**
		 * Generates an iterator and gets ready to fetch servers
		 * 
		 * @param region  the region to fetch from
		 * @param type  the server type
		 * @param secure  only secure servers should be fetched
		 * @param gameDir  the mod to fetch servers from.  For example,
		 * "cstrike" or "synergy"
		 * @param map  the map to restrict the servers to
		 * @param linux  only Linux servers should be fetched
		 * @param empty  if empty servers should not be included
		 * @param full  if full servers should not be included
		 * @param proxy  only proxy (TV) servers should be fetched
		 * @param notApplicationId  the application ID to filter out
		 * @param noPlayers  only empty servers should be fetched
		 * @param whitelisted  only whitelisted servers should be fetched
		 */
		private ServerIterator(Region region, Type type, Boolean secure,
				String gameDir, String map, Boolean linux, Boolean empty,
				Boolean full, Boolean proxy, int notApplicationId,
				Boolean noPlayers, Boolean whitelisted) {
			super();
			this.region = region;
			StringBuilder buff = new StringBuilder();
			if (type != null) {
				buff.append("\\type\\").append(type);
			}
			if (secure != null) {
				buff.append("\\secure\\");
				if (secure.booleanValue()) {
					buff.append('1');
				} else {
					buff.append('0');
				}
			}
			if (gameDir != null) {
				buff.append("\\gamedir\\").append(gameDir);
			}
			if (map != null) {
				buff.append("\\map\\").append(map);
			}
			if (linux != null) {
				buff.append("\\linux\\");
				if (linux.booleanValue()) {
					buff.append('1');
				} else {
					buff.append('0');
				}
			}
			if (empty != null) {
				buff.append("\\empty\\");
				if (empty.booleanValue()) {
					buff.append('1');
				} else {
					buff.append('0');
				}
			}
			if (full != null) {
				buff.append("\\full\\");
				if (full.booleanValue()) {
					buff.append('1');
				} else {
					buff.append('0');
				}
			}
			if (proxy != null) {
				buff.append("\\proxy\\");
				if (proxy.booleanValue()) {
					buff.append('1');
				} else {
					buff.append('0');
				}
			}
			if (notApplicationId >= 0) {
				buff.append("\\napp\\").append(notApplicationId);
			}
			if (noPlayers != null) {
				buff.append("\\noplayers\\");
				if (noPlayers.booleanValue()) {
					buff.append('1');
				} else {
					buff.append('0');
				}
			}
			if (whitelisted != null) {
				buff.append("\\white\\");
				if (whitelisted.booleanValue()) {
					buff.append('1');
				} else {
					buff.append('0');
				}
			}
			buff.append('\0');
			try {
				filter = buff.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				handleError(e);
			}
		}

		/**
		 * @since  2008-02-03
		 */
		@Override
		public boolean hasNext() {
			if (nextServer == null) {
				nextServer = readNextServer();
				if (nextServer == null) {
					try {
						loadServers();
					} catch (IOException e) {
						return false;
					}
					nextServer = readNextServer();
				}
			}
			return nextServer != null;
		}
		
		/**
		 * @since  2008-02-03
		 */
		@Override
		public T next() {
			if (nextServer == null) {
				nextServer = readNextServer();
				if (nextServer == null) {
					try {
						loadServers();
					} catch (IOException e) {
						throw new NoSuchElementException(e.getMessage());
					}
					nextServer = readNextServer();
					if (nextServer == null) {
						throw new NoSuchElementException();
					}
				}
			}
			T next = nextServer;
			lastServer = nextServer;
			nextServer = null;
			return next;
		}

		/**
		 * Reads the next server from the master server reply.
		 * 
		 * @return  the next server
		 */
		private T readNextServer() {
			T server = null;
			if (rawData != null) {
				if (rawData.remaining() > 5) {
					byte[] ip = new byte[4];
					rawData.get(ip);
					int port = rawData.getShort();
					if (port < 0) {
						port += (Short.MAX_VALUE + 1) * 2;
					}
					if (ip[0] == 0) {
						close();
					} else {
						try {
							server = loadServer(new InetSocketAddress(
									InetAddress.getByAddress(ip), port));
						} catch (UnknownHostException e) {
							handleError(e);
						}
					}
				}
			}
			return server;
		}

		/**
		 * Fetches more servers from the master server
		 * 
		 * @throws IOException  if there is a problem fetching the servers
		 */
		private void loadServers() throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(0x31);
			switch(region) {
				case UsEastCoast: out.write(0); break;
				case UsWestCoast: out.write(1); break;
				case SouthAmerica: out.write(2); break;
				case Europe: out.write(3); break;
				case Asia: out.write(4); break;
				case Australia: out.write(5); break;
				case MiddleEast: out.write(6); break;
				case Africa: out.write(7); break;
				case Earth: out.write(-1); break;
			}
			if (lastServer == null) {
				out.write("0.0.0.0:0".getBytes("UTF-8"));
			} else {
				out.write(
						lastServer.address.getAddress().getHostAddress().getBytes(
								"UTF-8"));
				out.write(':');
				out.write(Integer.toString(
						lastServer.address.getPort()).getBytes("UTF-8"));
			}
			out.write(0);
			out.write(filter);
			byte[] data = out.toByteArray();
			if (masterAddress == null) {
				InetAddress[] ips = null;
				if (useDnsJavaLibrary) {
					try {
						ips = Address.getAllByName(hostname);
					} catch (NoClassDefFoundError noclass) {
						useDnsJavaLibrary = false;
					}
				}
				if (ips == null) {
					ips = InetAddress.getAllByName(hostname);
				}
				IOException lastException = null;
				for (InetAddress address: ips) {
					try {
						data = fetchServers(address, data);
						lastException = null;
						masterAddress = address;
						break;
					} catch (IOException io) {
						socket.close();
						lastException = io;
						socket = null;
					}
				}
				if (lastException != null) {
					throw lastException;
				}
			} else {
				data = fetchServers(masterAddress, data);
			}
			rawData = ByteBuffer.wrap(data);
			rawData.getInt();	//FFFF
			rawData.get();	//0x66
			rawData.get();	//0x0A
		}
		
		/**
		 * Fetches the servers from the specified address, for the request
		 * 
		 * @param address  the address to query
		 * @param request  the filter to apply
		 * @return  the servers
		 * @throws IOException  if an error occurs
		 */
		private byte[] fetchServers(InetAddress address, byte[] request)
				throws IOException {
			DatagramPacket pack = new DatagramPacket(request, request.length,
					new InetSocketAddress(address, port));
			if (socket == null) {
				socket = new DatagramSocket();
				try {
					socket.setSoTimeout(msToWait);
					socket.setTrafficClass(IPTOS_RELIABILITY);
				} catch (SocketException e) {
					e.printStackTrace();
				}
			}
			socket.send(pack);
			byte[] reply = new byte[1392];
			pack.setData(reply);
			socket.receive(pack);
			return reply;
		}
		
		/**
		 * This iterator does not support removing servers
		 * 
		 * @throws UnsupportedOperationException  because it is unsupported
		 * @deprecated  this method should not be used
		 */
		@Override
		@Deprecated
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * @since  2008-02-03
		 */
		@Override
		public void close() {
			if (socket != null) {
				socket.close();
			}
		}
		
		/**
		 * Calls {@link #close()} to close the UDP socket
		 * 
		 * @since  2008-02-03
		 */
		@Override
		protected void finalize() {
			close();
		}
		
		/**
		 * The IP master server used
		 * 
		 * @return  the address of the master server
		 * @since  2009-05-02
		 */
		public InetAddress getMaster() {
			return masterAddress;
		}
	}

	/**
	 * Regions defined by Valve
	 * 
	 * @author  BarkerJr
	 * @since  2008-02-03
	 */
	public enum Region {
		/**
		 * East Coast USA (0x0)
		 * 
		 * @since  2008-02-03
		 */
		UsEastCoast,
		
		/**
		 * West Coast USA (0x1)
		 * 
		 * @since  2008-02-03
		 */
		UsWestCoast,
		
		/**
		 * South America (0x2)
		 * 
		 * @since  2008-02-03
		 */
	 	SouthAmerica,
	 	
	 	/**
	 	 * Europe (0x3)
		 * 
		 * @since  2008-02-03
	 	 */
	 	Europe,
	 	
	 	/**
	 	 * Asia (0x4)
		 * 
		 * @since  2008-02-03
	 	 */
	 	Asia,
	 	
	 	/**
	 	 * Australia (0x5)
		 * 
		 * @since  2008-02-03
	 	 */
	 	Australia,
	 	
	 	/**
	 	 * Middle East (0x6)
		 * 
		 * @since  2008-02-03
	 	 */
	 	MiddleEast,
	 	
	 	/**
	 	 * Africa (0x7)
		 * 
		 * @since  2008-02-03
	 	 */
	 	Africa,
	 	
	 	/**
	 	 * Elsewhere (0xFF)
		 * 
		 * @since  2008-02-03
	 	 */
	 	Earth
	}
	
	/**
	 * Server types to filter by
	 * 
	 * @author  BarkerJr
	 * @since  2008-02-03
	 */
	public enum Type {
		/**
		 * Dedicated servers, as opposed to listen servers\
		 * 
		 * @since  2008-02-03
		 */
		Dedicated {
			/**
			 * @since  2008-02-03
			 */
			@Override
			public String toString() {
				return "d";
			}
		}
	}
	
	/**
	 * The region to fetch from
	 * 
	 * @since  2008-02-03
	 */
	public Region region = Region.Earth;
	
	/**
	 * The server type
	 * 
	 * @since  2008-02-03
	 */
	public Type type;
	
	/**
	 * Only secure servers should be fetched
	 * 
	 * @since  2008-02-03
	 */
	public Boolean secure;
	
	/**
	 * The mod to fetch servers from.  For example, "cstrike" or "synergy"
	 * 
	 * @since  2008-02-03
	 */
	public String gameDir;
	
	/**
	 * The map to restrict the servers to
	 * 
	 * @since  2008-02-03
	 */
	public String map;
	
	/**
	 * Only Linux servers should be fetched
	 * 
	 * @since  2008-02-03
	 */
	public Boolean linux;
	
	/**
	 * If empty servers should not be included
	 * 
	 * @since  2008-02-03
	 */
	public Boolean empty;
	
	/**
	 * If full servers should not be included
	 * 
	 * @since  2008-02-03
	 */
	public Boolean full;
	
	/**
	 * Only proxy (TV) servers should be fetched
	 * 
	 * @since  2008-02-03
	 */
	public Boolean proxy;
	
	/**
	 * The application ID to filter out
	 * 
	 * @since  2009-01-26
	 */
	public int notApplicationId = -1;
	
	/**
	 * Only empty servers should be fetched
	 * 
	 * @since  2009-05-02
	 */
	public Boolean noPlayers;
	
	/**
	 * Only whitelisted servers should be fetched
	 * 
	 * @since  2009-05-02
	 */
	public Boolean whitelisted;
	
	/** If the dnsjava library should be used */
	private static boolean useDnsJavaLibrary = true;
	
	/** The hostname of the master server */
	private String hostname;
	
	/** The port of the master server */
	private int port;
	
	/**
	 * Creates a new server list with the given master server
	 * 
	 * @param hostname  the hostname of the master server
	 * @param port  the port of the master server
	 */
	ValveServerList(String hostname, int port) {
		super();
		this.hostname = hostname;
		this.port = port;
	}
	
	/**
	 * Loads a server for the given address
	 * 
	 * @param address  the address of the server
	 * @return  a server for the given address
	 * @since  2008-02-03
	 */
	protected abstract T loadServer(InetSocketAddress address);

	/**
	 * @since  2008-02-03
	 */
	@Override
	public ServerIterator iterator() {
		return new ServerIterator(region, type, secure, gameDir, map, linux,
				empty, full, proxy, notApplicationId, noPlayers, whitelisted);
	}
	/**
	 * Generates an iterator of servers
	 * 
	 * @param msToWait  the number of milliseconds to wait for each reply from
	 * the master server
	 * @return  the iterator of servers
	 * @since  2008-02-03
	 */
	public ServerIterator iterator(int msToWait) {
		return new ServerIterator(region, type, secure, gameDir, map, linux,
				empty, full, proxy, notApplicationId, noPlayers, whitelisted, msToWait);
	}
}