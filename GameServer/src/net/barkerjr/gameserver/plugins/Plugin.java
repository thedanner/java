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
public interface Plugin {
	/**
	 * Provides a listener to signal the game server on events and errors
	 * 
	 * @param listener  the listener to use
	 * @since 2007-11-28
	 */
	public void setListener(Listener listener);
	
	/**
	 * Provides the server which this plugin is used in
	 * 
	 * @param server  the server to work with
	 * @since 2007-11-28
	 */
	public void setserver(GameServer server);
	
	/**
	 * Triggered when
	 * {@link GameServer#load(net.barkerjr.gameserver.GameServer.Request...)}
	 * is called
	 * 
	 * @since 2007-11-28
	 */
	public void onLoad();
	
	/**
	 * Specifies if calls to
	 * {@link GameServer#load(int, net.barkerjr.gameserver.GameServer.Request...)}
	 * should sleep until this plugin triggers the load event for
	 * Request.PLUGIN.  If this method returns true,
	 * {@link Listener#loaded(GameServer, net.barkerjr.gameserver.GameServer.Request)}
	 * must be called with the parameter Request.PLUGIN when it's done loading.
	 * 
	 * @return  if the load() method should wait for a trigger
	 * @since 2007-11-28
	 */
	public boolean shouldWait();

	/**
	 * Specifies that the plugin should update the given document with its data
	 * 
	 * @param doc  the document to update
	 * @since  2009-05-02
	 */
	public void updateXmlDoc(Document doc);

	/**
	 * Specifies that the plugin should update the given object with its data
	 * 
	 * @param obj  the object to update
	 * @throws JSONException  if an error occurs
	 * @since  2009-05-02
	 */
	public void updateJsonObject(JSONObject obj) throws JSONException;
}