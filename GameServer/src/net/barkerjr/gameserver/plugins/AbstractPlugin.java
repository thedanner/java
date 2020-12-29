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
package net.barkerjr.gameserver.plugins;

import org.jdom.Document;
import org.json.JSONException;
import org.json.JSONObject;

import net.barkerjr.gameserver.GameServer;
import net.barkerjr.gameserver.Listener;

/**
 * Plugins get called when
 * {@link GameServer#load(net.barkerjr.gameserver.GameServer.Request...)} is
 * called.
 * 
 * @author BarkerJr
 * @since 2007-11-28
 * @see GameServer#addPlugin(Plugin...)
 */
public abstract class AbstractPlugin implements Plugin {
	/**
	 * The listener to use
	 * 
	 * @since 2007-11-28
	 */
	protected Listener listener;

	/**
	 * The server to interact with
	 * 
	 * @since 2007-11-28
	 */
	protected GameServer server;

	/**
	 * Stores the listener for later use
	 * 
	 * @param listener {@inheritDoc}
	 * @since 2007-11-28
	 */
	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	/**
	 * Stores the server for later use
	 * 
	 * @param server {@inheritDoc}
	 * @since 2007-11-28
	 */
	public void setserver(GameServer server) {
		this.server = server;
	}

	/**
	 * Specifies that the server should not wait
	 * 
	 * @return <tt>false</tt>
	 * @since 2007-11-28
	 */
	public boolean shouldWait() {
		return false;
	}

	/**
	 * Makes no changes to the document
	 * 
	 * @since  2009-05-02
	 */
	@Override
	public void updateXmlDoc(Document doc) {
	}

	/**
	 * Makes no changes to the object
	 * 
	 * @since  2009-05-02
	 */
	@Override
	public void updateJsonObject(JSONObject obj) throws JSONException {
	}
}