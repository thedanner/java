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
package net.barkerjr.gameserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import net.barkerjr.gameserver.plugins.Plugin;
import net.barkerjr.gameserver.util.Calendar;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides generic parsing and querying servers
 * 
 * @author BarkerJr
 * @since 2007-10-25
 */
public abstract class GameServer extends Base {
	/**
	 * Provides a container for the requests which failed to load
	 * 
	 * @author BarkerJr
	 * @since  2007-12-30
	 */
	public class RequestTimeoutException extends TimeoutException {
		/** The serial ID for this class */
		private static final long serialVersionUID = 1L;
		
		/**
		 * The requests which failed to load
		 * 
		 * @since  2007-12-30
		 */
		public final Collection<Request> failedRequests;

		/**
		 * Creates an exception with a message specifying which requests failed
		 * to load, and also store them for later use
		 * 
		 * @param requests  the requests which failed to load
		 */
		private RequestTimeoutException(HashSet<Request> requests) {
			super("Timed out waiting for " + requests +
					((requests.contains(Request.PLUGIN)?
							"; (all plugins installed: " + plugins + ')': "")));
			failedRequests = Collections.unmodifiableCollection(requests);
		}
	}
	
	/**
	 * The listener used for synchronous load requests
	 */
	private class LoadingListener extends Listener {
		/** If the thread has been interrupted yet */
		private boolean calledAlready;
		
		/** The number of plugins to wait for */
		private int pluginCount;
		
		/** The requests which were requested */
		private HashSet<Request> requestsRequested;
		
		/** If everything is loaded */
		private boolean loaded;
		
		/** The thread that is waiting */
		private Thread waiting = Thread.currentThread();
		
		/**
		 * Stored the requests for later
		 * 
		 * @param requestsRequested  the requests that were requested
		 */
		private LoadingListener(HashSet<Request> requestsRequested) {
			this.requestsRequested = requestsRequested;
		}

		@Override
		public void loaded(Request request) {
			if (request.equals(Request.PLUGIN)) {
				pluginCount--;
				if (pluginCount < 1) {
					requestsRequested.remove(request);
				}
			} else {
				requestsRequested.remove(request);
			}
			if (requestsRequested.size() < 1) {
				if (!calledAlready) {
					loaded = true;
					waiting.interrupt();
					removeListener(this);
					calledAlready = true;
				}
			}
		}
	}
	
	/**
	 * The type of request to place
	 * 
	 * @author BarkerJr
	 * @since 2007-11-10
	 */
	public enum Request {
		/**
		 * A challenge key, used internally in other requests.  You will not
		 * need to use this.
		 * 
		 * @since 2007-11-10
		 */
		CHALLENGE,
		
		/**
		 * Very basic server information
		 * 
		 * @since 2007-11-18
		 */
		BASIC,
		
		/**
		 * General server information
		 * 
		 * @since 2007-11-10
		 */
		INFORMATION,
		
		/**
		 * The list of players and their scores
		 * 
		 * @since 2007-11-10
		 */
		PLAYERS,
		
		/**
		 * The list of server rules and cvars
		 * 
		 * @since 2007-11-10
		 */
		RULES,
		
		/**
		 * Don't request this, as all plugins are loaded automatically.  This
		 * is only used in the Listener methods.
		 * 
		 * @since 2007-11-28
		 */
		PLUGIN
	}

	/**
	 * The address of this server
	 * 
	 * @since 2007-11-28
	 */
	public final InetSocketAddress address;
	
	/**
	 * Any listeners which should be triggered when a load is in progress
	 * 
	 * @since 2007-11-28
	 */
	protected Set<Plugin> plugins =
		Collections.synchronizedSet(new HashSet<Plugin>());
	
	/**
	 * The namespace for this server
	 * 
	 * @since  2008-02-03
	 * @deprecated 2009-05-02, use toXmlDocument().getRootElement().getNamespace()
	 */
	public static final Namespace namespace =
		Namespace.getNamespace("http://barkerjr.net/java/GameServer");
	
	/**
	 * The time the server was last updated
	 * 
	 * @since  2009-05-02
	 */
	public Calendar updated;
	
	/**
	 * The name of the server
	 * 
	 * @since  2009-05-02
	 */
	public String name;
	
	/**
	 * The map the server is playing
	 * 
	 * @since  2009-05-02
	 */
	public String map;
	
	/**
	 * If a password is required to connect
	 * 
	 * @since  2009-05-02
	 */
	public boolean passwordRequired;
	
	/**
	 * The number of players in the server
	 * 
	 *  @since  2009-05-02
	 */
	public int numberOfPlayers = -1;
	
	/**
	 * The maximum players allowed
	 * 
	 * @since  2009-05-02
	 */
	public int maximumPlayers;
	
	/**
	 * The number of bots on the server
	 * 
	 * @since  2009-05-02
	 */
	public int botCount = -1;
	
	/**
	 * The players in the server
	 * 
	 *  @since  2009-05-02
	 */
	protected Players players;

	/**
	 * The directory of the game
	 * 
	 * @since  2009-05-02
	 */
	public String gameDirectory;

	/**
	 * The rules on the server
	 * 
	 * @since  2009-05-02
	 */
	protected HashMap<String, String> rules;
	
	/**
	 * The version of the server
	 * 
	 * @since  2009-05-02
	 */
	protected String version;
	
	/**
	 * The server type
	 * 
	 * @author BarkerJr
	 * @since  2009-05-02
	 */
	public enum Dedicated {
		/**
		 * The server is dedicated
		 * 
		 * @since  2009-05-02
		 */
		DEDICATED {
			/**
			 * @since  2009-05-02
			 */
			@Override
			public String toString() {
				return "Dedicated";
			}
		},
		/**
		 * The server is a listen server
		 * 
		 * @since  2009-05-02
		 */
		LISTEN {
			/**
			 * @since  2009-05-02
			 */
			@Override
			public String toString() {
				return "Listen";
			}
		},
		/**
		 * The server is source TV
		 * 
		 * @since  2009-05-02
		 */
		TV
	}
	
	/**
	 * The dedicated status of the server
	 * 
	 * @since  2009-05-02
	 */
	protected Dedicated dedicated;
	
	/**
	 * Stores the address for later use
	 * 
	 * @param address  the address of the server
	 * @since 2007-10-25
	 */
	public GameServer(InetSocketAddress address) {
		this.address = address;
	}
	
	/**
	 * @return  the data as an XML string
	 * @since 2007-10-25
	 */
	@Override
	public String toString() {
		return new XMLOutputter().outputString(toXmlDocument());
	}
	
	/**
	 * Fetches the data in this server as an XML document
	 * 
	 * @return  the document describing this server
	 * @since 2007-10-25
	 * @deprecated 2009-05-02, use {@link #toXmlDocument()}
	 */
	public Document getDocument() {
		return toXmlDocument();
	}
	
	/**
	 * Converts the server into an XML Document
	 * 
	 * @return the document describing this server
	 * @since 2009-05-02
	 */
	public Document toXmlDocument() {
		Namespace namespace =
			Namespace.getNamespace("http://barkerjr.net/java/GameServer");
		Element server = new Element("Server", namespace);
		server.setAttribute("IP", getIP());
		server.setAttribute("Port", Integer.toString(getPort()));
		if (name != null) {
			server.setAttribute("Name", name);
		}
		if (map != null) {
			server.setAttribute("Map", map);
		}
		if (numberOfPlayers > -1) {
			server.setAttribute("NumberOfPlayers",
				Integer.toString(numberOfPlayers));
		}
		if (maximumPlayers > 0) {
			server.setAttribute("MaximumPlayers",
					Integer.toString(maximumPlayers));
		}
		server.setAttribute("BotCount", Integer.toString(botCount));
		server.setAttribute("PasswordRequired",
				Boolean.toString(passwordRequired));
		if (players != null) {
			server.addContent(players.toXmlElement(namespace));
		}
		if (gameDirectory != null) {
			server.setAttribute("GameDirectory", gameDirectory);
		}
		Document doc = new Document(server);
		for (Plugin plugin : plugins) {
			plugin.updateXmlDoc(doc);
		}
		if (updated != null) {
			server.setAttribute("Updated", updated.toString());
		}
		if (rules != null) {
			Element rulesEl = new Element("Rules", namespace);
			for (Entry<String, String> entry : rules.entrySet()) {
				Element rule = new Element("Rule", namespace);
				rule.setAttribute("Name", entry.getKey());
				rule.setAttribute("Value", entry.getValue());
				rulesEl.addContent(rule);
			}
			server.addContent(rulesEl);
		}
		if (version != null) {
			server.setAttribute("Version", version);
		}
		if (dedicated != null) {
			server.setAttribute("Dedicated", dedicated.toString());
		}
		return doc;
	}
	
	/**
	 * Converts the server into a JSON object
	 * 
	 * @return the object describing this server
	 * @throws JSONException  if an error occurs
	 * @since 2009-05-02
	 */
	public JSONObject toJsonObject() throws JSONException {
		JSONObject server = new JSONObject();
		server.put("ip", getIP());
		server.put("port", getPort());
		server.put("name", name);
		server.put("map", map);
		if (numberOfPlayers > -1) {
			server.put("numberOfPlayers", numberOfPlayers);
		}
		if (maximumPlayers > 0) {
			server.put("maximumPlayers", maximumPlayers);
		}
		server.put("botCount", botCount);
		server.put("passwordRequired", passwordRequired);
		if (players != null) {
			server.put("players", players.toJsonObject());
		}
		server.put("gameDirectory", gameDirectory);
		for (Plugin plugin : plugins) {
			plugin.updateJsonObject(server);
		}
		if (updated != null) {
			server.put("updated", updated.toString());
		}
		if (rules != null) {
			JSONObject collection = new JSONObject();
			for (Entry<String, String> entry : rules.entrySet()) {
				collection.put(entry.getKey(), entry.getValue());
			}
			server.put("rules", collection);
		}
		server.put("version", version);
		if (dedicated != null) {
			server.put("dedicated", dedicated.toString());
		}
		return server;
	}

	/**
	 * Fires the load event in all listeners for this server
	 * 
	 * @param request  the type of request to fire
	 * @since 2007-11-10
	 */
	protected void fireLoadEvent(Request request) {
		Listener[] listenerArray = listeners.toArray(new Listener[0]);
		for (Listener listener: listenerArray) {
			listener.loaded(this, request);
			listener.loaded(request);
		}
		fireChangeEvent(request);
	}
	
	/**
	 * Fires the change event in all listeners for this server
	 * 
	 * @param request  the type of request to fire
	 * @since 2007-11-10
	 */
	protected void fireChangeEvent(Request request) {
		Listener[] listenerArray = listeners.toArray(new Listener[0]);
		for (Listener listener: listenerArray) {
			listener.changed(this, request);
			listener.changed(request);
		}
	}
	
	/**
	 * Replaces any child with the same tag name with this one.  This is a
	 * shallow replacement
	 * 
	 * @param element  the parent element
	 * @param child  the child element
	 * @since 2007-10-25
	 */
	protected void replaceChild(Element element, Element child) {
		element.removeChild(child.getName(), child.getNamespace());
		element.addContent(child);
	}
	
	/**
	 * Sends error to the listeners, if there are any
	 * 
	 * @param error  the error to send
	 * @since 2007-10-25
	 */
	@Override
	protected void handleError(Throwable error) {
		Listener[] listenerArray = listeners.toArray(new Listener[0]);
		for (Listener listener: listenerArray) {
			listener.errorHandler(error, this);
		}
	}
	
	/**
	 * Loads the given requests synchronously and returns once they are all
	 * loaded
	 * 
	 * @param msToWait  the number of milliseconds to block while waiting for
	 * a reply from the server
	 * @param requests  the requests to load
	 * @throws IOException  if there was an error sending or receiving the data
	 * @throws RequestTimeoutException  if the server did not respond in the
	 * specified time
	 * @throws InterruptedException  if the thread was interrupted before the
	 * data was loaded
	 * @since 2007-11-10
	 */
	public void load(int msToWait, final Request... requests)
			throws IOException, InterruptedException, RequestTimeoutException {
		HashSet<Request> requestsRequested =
			new HashSet<Request>(requests.length + 1);
		requestsRequested.addAll(Arrays.asList(requests));
		LoadingListener listener = new LoadingListener(requestsRequested);
		synchronized (plugins) {
			for (Plugin plugin: plugins) {
				if (plugin.shouldWait()) {
					listener.pluginCount++;
				}
			}
		}
		if (listener.pluginCount > 0) {
			requestsRequested.add(Request.PLUGIN);
		}
		listeners.add(listener);
		try {
			load(requests);
			Thread.sleep(msToWait);
		} catch (InterruptedException e) {
			if (listener.loaded) {
				return;
			} else {
				throw e;
			}
		}
		throw new RequestTimeoutException(requestsRequested);
	}
	
	/**
	 * Loads the given requests asynchronously
	 * 
	 * @param requests  the requests to load
	 * @throws IOException  if there was an error sending or receiving the data
	 * @since 2007-11-10
	 */
	public void load(Request... requests) throws IOException {
		synchronized (plugins) {
			for (Plugin plugin: plugins) {
				plugin.onLoad();
			}
		}
	}
	
	/**
	 * Adds the given plugins to the server
	 * 
	 * @param plugins  the plugins to install
	 * @since 2007-11-28
	 */
	public void addPlugin(Plugin... plugins) {
		for (Plugin plugin: plugins) {
			plugin.setListener(new Listener() {
				@Override
				public void changed(Request request) {
					fireChangeEvent(request);
				}

				@Override
				public void loaded(Request request) {
					fireLoadEvent(request);
				}
				
				@Override
				public void errorHandler(Throwable error, GameServer server) {
					handleError(error);
				}
			});
			plugin.setserver(this);
			this.plugins.add(plugin);
		}
	}

	/**
	 * Determines if this server's address is the same as the other server's
	 * address
	 * 
	 * @since  2008-02-03
	 * @see  InetSocketAddress#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GameServer) {
			GameServer other = (GameServer)obj;
			return address.equals(other.address);
		}
		return false;
	}

	/**
	 * Fetches the hashcode of the address of this server
	 * 
	 * @since  2008-02-03
	 * @see  InetSocketAddress#hashCode()
	 */
	@Override
	public int hashCode() {
		return address.hashCode();
	}
	
	/**
	 * The address of this server
	 * 
	 * @return  the address of this server
	 * @since  2009-05-02
	 */
	public String getIP() {
		InetAddress ip = address.getAddress();
		if (ip != null) {
			return ip.getHostAddress();
		}
		return null;
	}

	/**
	 * The port of this server
	 * 
	 * @return  the port of this server
	 * @since  2009-05-02
	 */
	public int getPort() {
		return address.getPort();
	}

	/**
	 * The time the server was last updated
	 * 
	 * @return  the time the server was last updated
	 * @since  2009-05-02
	 */
	public String getUpdated() {
		return updated.toString();
	}

	/**
	 * The name of the server
	 * 
	 * @return  the name of the server
	 * @since  2009-05-02
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * If a password is required to connect
	 * 
	 * @return  if a password is required to connect
	 * @since  2009-05-02
	 */
	public boolean isPasswordRequired() {
		return passwordRequired;
	}

	/**
	 * The number of players in the server
	 * 
	 * @return  the number of players in the server
	 * @since  2009-05-02
	 */
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	/**
	 * The maximum players allowed by the server
	 * 
	 * @return  the maximum players allowed by the server
	 * @since  2009-05-02
	 */
	public int getMaximumPlayers() {
		return maximumPlayers;
	}

	/**
	 * The number of bots on the server
	 * 
	 * @return  the number of bots on the server
	 * @since  2009-05-02
	 */
	public int getBotCount() {
		return botCount;
	}

	/**
	 * The players in the server
	 * 
	 * @return  the players in the server
	 * @since  2009-05-02
	 */
	public Players getPlayers() {
		if (players == null) {
			players = new Players();
		}
		return players;
	}
	
	/**
	 * The map the server is running
	 * 
	 * @return  the map the server is running
	 * @since  2009-05-02
	 */
	public String getMap() {
		return map;
	}
	
	/**
	 * The directory the game is running
	 * 
	 * @return  the directory the game is running
	 * @since  2009-05-02
	 */
	public String getGameDirectory() {
		return gameDirectory;
	}
	
	/**
	 * The server rules
	 * 
	 * @return  the server rules
	 * @since  2009-05-02
	 */
	public Map<String, String> getRules() {
		if (rules == null) {
			return null;
		}
		return Collections.unmodifiableMap(rules);
	}

	/**
	 * The version the server is running
	 * 
	 * @return  the version the server is running
	 * @since  2009-05-02
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * The type of server this is
	 * 
	 * @return  the type of server this is
	 * @since  2009-05-02
	 */
	public Dedicated getDedicated() {
		return dedicated;
	}
}