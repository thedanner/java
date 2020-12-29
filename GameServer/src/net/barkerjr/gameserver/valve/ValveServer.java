/*
 * Game Server Library - Querying server requests and returning them as XML
 * Copyright (C) 2007-2009  BarkerJr <http://www.barkerjr.net/java/GameServer/>
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
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import net.barkerjr.gameserver.Listener;
import net.barkerjr.gameserver.Player;
import net.barkerjr.gameserver.Players;
import net.barkerjr.gameserver.ReplyStream;
import net.barkerjr.gameserver.SequenceDvo;
import net.barkerjr.gameserver.UdpServer;
import net.barkerjr.gameserver.util.BZip2Compression;
import net.barkerjr.gameserver.util.Calendar;
import net.barkerjr.gameserver.valve.TheShip.GameMode;
import net.sourceforge.rconed.exception.BadRcon;
import net.sourceforge.rconed.exception.ResponseEmpty;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides generic parsing and querying for Valve servers
 * 
 * @author BarkerJr
 * @since 2007-10-25
 * @see <a href="http://developer.valvesoftware.com/wiki/Server_Queries">Specification</a>
 */
public abstract class ValveServer extends UdpServer {
	/**
	 * The application ID of The Ship servers
	 */
	private static final int THE_SHIP = 2400;
	
	/**
	 * Listens for a challenge and loads the given data after that
	 */
	private class ChallengeListener extends Listener {
		/**
		 * The request type to load
		 */
		private final Request request;

		/**
		 * Sets up the listener
		 * 
		 * @param request  the request to load
		 */
		private ChallengeListener(Request request) {
			super();
			this.request = request;
		}

		/**
		 * Loads the given request once the challenge is loaded
		 * 
		 * @since 2007-11-10
		 */
		@Override
		public void challengeLoaded() {
			removeListener(this);
			try {
				load(request);
			} catch (IOException e) {
				handleError(e);
			}
		}
	}
	
	/** The challenge number for the server */
	private int challenge;
	
	/** When the challenge was last updated */
	private Calendar challengeUpdated;
	
	/** Any partial multi-packet responses we're receiving */
	private HashMap<Integer, TreeMap<Integer, byte[]>> responses =
		new HashMap<Integer, TreeMap<Integer, byte[]>>();
	
	/**
	 * The rcon password for this server
	 * 
	 * @since  2008-02-03
	 */
	protected String rconPassword;
	
	/**
	 * The operating system the server is running
	 * 
	 * @author BarkerJr
	 * @since  2009-05-02
	 */
	public enum OperatingSystem {
		/**
		 * The server is running on GNU/Linux
		 * 
		 * @since  2009-05-02
		 */
		LINUX {
			/**
			 * @since  2009-05-02
			 */
			@Override
			public String toString() {
				return "Linux";
			}
		},
		/**
		 * The server is running on Windows
		 * 
		 * @since  2009-05-02
		 */
		WINDOWS {
			/**
			 * @since  2009-05-02
			 */
			@Override
			public String toString() {
				return "Windows";
			}
		}
	}
	
	private String gameDescription;
	private int applicationId;
	private OperatingSystem operatingSystem;
	private boolean vacSecure;
	private TheShip ship;
	
	/**
	 * Sets up the basic server information and DOM
	 * 
	 * @param address  the address of the server
	 * @since 2007-11-10
	 */
	protected ValveServer(InetSocketAddress address) {
		super(address);
	}
	
	/**
	 * Parses the packet header and reassembles reply fragments.  If this
	 * is the only packet in the reply, we send it to the handler now,
	 * otherwise we put it in a map for later. If we have all the pieces,
	 * we send the parts to be reassembled.
	 * 
	 * @param data  the packet we received
	 * @throws IOException  if there is an error decompressing the packets
	 * @since 2007-10-25
	 */
	@Override
	protected void parseData(byte[] data) throws IOException {
		ReplyStream stream = new ReplyStream(data);
		if (stream.readInt() == -1) {
			handleMessage(stream.readUnsignedByte(), stream);
		} else {
			int requestId = stream.readInt();
			TreeMap<Integer, byte[]> sequence = responses.get(requestId);
			if (sequence == null) {
				sequence = new TreeMap<Integer, byte[]>();
				responses.put(requestId, sequence);
			}
			SequenceDvo dvo = parsePacketNumber(stream);
			stream.readInt();	//FFFFFFFF
			sequence.put(dvo.current,
					Arrays.copyOfRange(data, stream.getPosition(),
							data.length));
			if (sequence.size() == dvo.total) {
				mergePackets(requestId);
			}
		}
	}
	
	/**
	 * Merges the packets together into a stream and pass it to the handler
	 * 
	 * @param requestId  the packets to reassemble
	 * @throws IOException  if there is an error decompressing the packets
	 */
	private void mergePackets(int requestId) throws IOException {
		TreeMap<Integer, byte[]> sequence = responses.get(requestId);
		responses.remove(sequence);
		byte[] buff = new byte[sequence.size() * 1400];
		ByteBuffer buffer = ByteBuffer.wrap(buff);
		for (byte[] data: sequence.values()) {
			buffer.put(data);
		}
		buffer.rewind();
		if ((requestId & Integer.MIN_VALUE) == Integer.MIN_VALUE) {
			ReplyStream stream = new ReplyStream(buffer);
			//TODO Use this data
			stream.readShort();	//Number of bytes the data uses after decompression;
			stream.readShort();	//CRC32 checksum of the uncompressed data for validation.
			
			byte[] in = new byte[buffer.remaining()];
			buffer.get(in);
			buffer = ByteBuffer.wrap(BZip2Compression.decompress(in));
			buffer.getInt();	//FFFFFFFF
		}
		ReplyStream stream = new ReplyStream(buffer);
		handleMessage(stream.readUnsignedByte(), stream);
	}
	
	/**
	 * Determines which packet in the multi-packet response this is
	 * 
	 * @param buffer  the packet data
	 * @return  a DVO containing this packet's sequence number, and the total
	 * number of packets to expect
	 * @since 2007-10-25
	 */
	protected abstract SequenceDvo parsePacketNumber(ReplyStream buffer);

	/**
	 * Handles <tt>A</tt> (Challenge), <tt>D</tt> (Players), <tt>E</tt> (Rules),
	 * and <tt>I</tt>nformation responses
	 * 
	 * @param code  the message type
	 * @param stream  the message itself
	 * @since 2007-10-25
	 */
	protected void handleMessage(int code, ReplyStream stream) {
		switch (code) {
			case 'A': handleChallenge(stream); break;
			case 'D': handlePlayers(stream); break;
			case 'E': handleRules(stream); break;
			case 'I': handleInformation(stream); break;
			default: handleError(new Exception("Unexpected message: " + code));
		}
	}
	
	/**
	 * Handles <tt>A</tt> (Challenge) responses from the server
	 * 
	 * @param stream  the message from the server
	 * @since 2007-10-25
	 */
	private void handleChallenge(ReplyStream stream) {
		challenge = stream.readInt();
		challengeUpdated = new Calendar();
		fireLoadEvent(Request.CHALLENGE);
	}

	/**
	 * Handles <tt>D</tt> (Players) responses from the server
	 * 
	 * @param stream  the message from the server
	 * @since  2009-10-06
	 */
	protected void handlePlayers(ReplyStream stream) {
		int playerCount = stream.readUnsignedByte();
		if (playerCount != numberOfPlayers) {
			numberOfPlayers = playerCount;
			fireChangeEvent(Request.INFORMATION);
		}
		if (players == null) {
			players = new Players(numberOfPlayers);
		}
		HashSet<Player> newList =
			new HashSet<Player>(numberOfPlayers);
		for (int x = 0; x < numberOfPlayers; x++) {
			Player player = players.get(stream.readUnsignedByte());
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
	
	/**
	 * Handles <tt>E</tt> (Rules) responses from the server
	 * 
	 * @param stream  the message from the server
	 * @since 2007-10-25
	 */
	private void handleRules(ReplyStream stream) {
		int numberOfRules = stream.readShort();
		rules = new HashMap<String, String>(numberOfRules);
		for (int x = 0; x < numberOfRules; x++) {
			rules.put(stream.readString(), stream.readString());
		}
		fireLoadEvent(Request.RULES);
	}
	
	/**
	 * Parses the <tt>I</tt>nformation message and places the data into the DOM
	 * 
	 * @param stream  the stream to read from
	 * @since 2007-10-25
	 */
	private void handleInformation(ReplyStream stream) {
		stream.readByte();	//Version
		name = stream.readString();
		map = stream.readString();
		gameDirectory = stream.readString();
		gameDescription = stream.readString();
		applicationId = stream.readShort();
		numberOfPlayers = stream.readUnsignedByte();
		maximumPlayers = stream.readUnsignedByte();
		botCount = stream.readUnsignedByte();
		switch (stream.readByte()) {
			case 'd': dedicated = Dedicated.DEDICATED; break;
			case 'l': dedicated = Dedicated.LISTEN; break;
			case 'p': dedicated = Dedicated.TV;
		}
		switch (stream.readByte()) {
			case 'l': operatingSystem = OperatingSystem.LINUX; break;
			case 'w': operatingSystem = OperatingSystem.WINDOWS;
		}
		passwordRequired = stream.readByte() == 1;
		vacSecure = stream.readByte() == 1;
		if (applicationId == THE_SHIP) {
			ship = new TheShip();
			switch (stream.readByte()) {
				case 0: ship.gameMode = GameMode.HUNT;
				case 1: ship.gameMode = GameMode.ELIMINATION;
				case 2: ship.gameMode = GameMode.DUEL;
				case 3: ship.gameMode = GameMode.DEATHMATCH;
				case 4: ship.gameMode = GameMode.TEAM_VIP;
				case 5: ship.gameMode = GameMode.TEAM_ELIMINATION;
			}
			ship.witnessCount = stream.readUnsignedByte();
			ship.witnessSeconds = stream.readUnsignedByte();
		} else {
			ship = null;
		}
		version = stream.readString();
		updated = new Calendar();
		fireLoadEvent(Request.INFORMATION);
	}
	
	/**
	 * Supports:
	 * <ul>
	 *  <li>Request.INFORMATION</li>
	 *  <li>Request.PLAYERS</li>
	 *  <li>Request.RULES</li>
	 * </ul>
	 * 
	 * {@inheritDoc}
	 * 
	 * @since 2007-11-10
	 */
	@Override
	public void load(Request... requests) throws IOException {
		List<Request> requestList = Arrays.asList(requests);
		if (requestList.contains(Request.INFORMATION)) {
			loadInformation();
		}
		if (requestList.contains(Request.PLAYERS)) {
			loadPlayers();
		}
		if (requestList.contains(Request.RULES)) {
			loadRules();
		}
		super.load(requests);
	}
	
	/**
	 * Asynchronously loads the generic server information.
	 * This method will return as soon as the request is sent to the server,
	 * without waiting for a reply.  Add a listener if you need to be notified
	 * after the reply is parsed.
	 * 
	 * @throws IOException  if there was an error sending the request
	 * @see #addListener(Listener)
	 * @since 2007-10-25
	 */
	public void loadInformation() throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[25]);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(-1);
		buffer.put("TSource Engine Query\0".getBytes("UTF-8"));
		sendData(buffer.array());
	}
	
	/**
	 * Asynchronously loads the player list.
	 * This method will return as soon as the request is sent to the server,
	 * without waiting for a reply.  Add a listener if you need to be notified
	 * after the reply is parsed.
	 * 
	 * @throws IOException  if there was an error sending the request
	 * @see #addListener(Listener)
	 * @since 2007-10-25
	 */
	public void loadPlayers() throws IOException {
		int challenge = getChallenge(Request.PLAYERS);
		if (challenge > 0) {
			ByteBuffer buffer = ByteBuffer.wrap(new byte[9]);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			buffer.putInt(-1);
			byte data = 0x55;
			buffer.put(data);
			buffer.putInt(challenge);
			sendData(buffer.array());
		}
	}

	/**
	 * Asynchronously loads the rules.
	 * This method will return as soon as the request is sent to the server,
	 * without waiting for a reply.  Add a listener if you need to be notified
	 * after the reply is parsed.
	 * 
	 * @throws IOException  if there was an error sending the request
	 * @see #addListener(Listener)
	 * @since 2007-10-25
	 */
	public void loadRules() throws IOException {
		int challenge = getChallenge(Request.RULES);
		if (challenge > 0) {
			ByteBuffer buffer = ByteBuffer.wrap(new byte[9]);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			buffer.putInt(-1);
			byte data = 0x56;
			buffer.put(data);
			buffer.putInt(challenge);
			sendData(buffer.array());
		}
	}
	
	/**
	 * Requests (Asynchronously) the challenge code from the server
	 * 
	 * @throws IOException  if there was an error sending the request
	 * @since 2007-10-25
	 */
	protected void loadChallenge() throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(new byte[5]);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(-1);
		byte data = 0x57;
		buffer.put(data);
		sendData(buffer.array());
	}
	
	/**
	 * Returns the challenge if it has not expired
	 * 
	 * @param request  the request being processed
	 * @return  the challenge, or zero if it's not yet ready
	 * @throws IOException  if there was an error requesting a challenge key
	 */
	private int getChallenge(Request request) throws IOException {
		boolean update;
		if (challengeUpdated == null) {
			update = true;
		} else {
			Calendar before = new Calendar();
			before.add(Calendar.MINUTE, -1);
			if (challengeUpdated.before(before)) {
				update = true;
			} else {
				update = false;
			}
		}
		if (update) {
			challengeUpdated = null;
			challenge = 0;
			addListener(new ChallengeListener(request));
			loadChallenge();
		}
		return challenge;
	}
	
	/**
	 * Sets the server rcon password
	 * 
	 * @param password  the password to store
	 * @since  2008-02-03
	 */
	public void setRconPassword(String password) {
		rconPassword = password;
	}
	
	/**
	 * Sends the given command to the server and waits for a reply.
	 * 
	 * @param command  the command to send
	 * @return  the response from the server
	 * @throws BadRcon  if the password set by
	 * {@link #setRconPassword(String)} is bad
	 * @throws ResponseEmpty  if there was no response from the server
	 * @throws SocketTimeoutException  if there was an error contacting the server
	 * @since  2008-02-03
	 */
	public abstract String sendRcon(String command)
			throws BadRcon, ResponseEmpty, SocketTimeoutException;
	/**
	 * Sends the given command to the server
	 * 
	 * @param commands  the commands to send
	 * @since  2008-02-03
	 */
	public void sendRcon(String... commands) {
		for (final String command: commands) {
			new Thread("Rcon Command") {
				@Override
				public void run() {
					setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
						@Override
						public void uncaughtException(Thread t, Throwable e) {
							handleError(e);
						}
					});
					try {
						sendRcon(command);
					} catch (BadRcon e) {
						handleError(e);
					} catch (ResponseEmpty e) {
						handleError(e);
					} catch (SocketTimeoutException e) {
						handleError(e);
					}
				}
			}.start();
		}
	}

	/**
	 * The description of the game that is running
	 * 
	 * @return  the description of the game that is running
	 * @since  2009-05-02
	 */
	public String getGameDescription() {
		return gameDescription;
	}

	/**
	 * The application ID
	 * 
	 * @return  the application ID
	 * @since  2009-05-02
	 */
	public int getApplicationId() {
		return applicationId;
	}

	/**
	 * The operating system running on the server
	 * 
	 * @return  the operating system running on the server
	 * @since  2009-05-02
	 */
	public OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}

	/**
	 * If the server uses VAC security
	 * 
	 * @return  if the server uses VAC security
	 * @since  2009-05-02
	 */
	public boolean isVacSecure() {
		return vacSecure;
	}

	/**
	 * Statistics about The Ship servers
	 * 
	 * @return  statistics about The Ship servers
	 * @since  2009-05-02
	 */
	public TheShip getShip() {
		return ship;
	}

	/**
	 * @since 2009-05-02
	 */
	@Override
	public Document toXmlDocument() {
		Document doc = super.toXmlDocument();
		Element server = doc.getRootElement();
		if (gameDescription != null) {
			server.setAttribute("GameDescription", gameDescription);
		}
		server.setAttribute("ApplicationId", Integer.toString(applicationId));
		if (operatingSystem != null) {
			server.setAttribute("OperatingSystem", operatingSystem.toString());
		}
		server.setAttribute("VacSecure", Boolean.toString(vacSecure));
		Namespace namespace = server.getNamespace();
		if (ship != null) {
			server.addContent(ship.toXmlElement(namespace));
		}
		return doc;
	}
	
	/**
	 * @since 2009-05-02
	 */
	@Override
	public JSONObject toJsonObject() throws JSONException {
		JSONObject server = super.toJsonObject();
		server.put("gameDescription", gameDescription);
		server.put("applicationId", applicationId);
		if (operatingSystem != null) {
			server.put("operatingSystem", operatingSystem.toString());
		}
		server.put("vacSecure", vacSecure);
		if (ship != null) {
			server.put("theShip", ship.toJsonObject());
		}
		return server;
	}
}