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

import net.barkerjr.gameserver.GameServer.Request;

/**
 * Provides methods to be called when specific events fire.  By default, these
 * implementations just return, so there is no need to call the superclass when
 * overriding them.  Note that <code>changed</code> is always called immediately
 * following <code>loaded</code>.
 * 
 * @author BarkerJr
 * @since 2007-10-25
 */
public abstract class Listener {
	/**
	 * Triggers when data is loaded
	 * 
	 * @param request  the type of data loaded
	 * @since 2007-11-10
	 */
	public void loaded(Request request) {}
	/**
	 * Triggers when data is loaded
	 * 
	 * @param server  the server which triggered this event
	 * @param request  the type of data loaded
	 * @since 2007-11-10
	 */
	public void loaded(GameServer server, Request request) {
		if (request == Request.CHALLENGE) {
			challengeLoaded();
		} else if (request == Request.INFORMATION) {
			informationLoaded();
			informationLoaded(server);
		} else if (request == Request.PLAYERS) {
			playersLoaded();
			playersLoaded(server);
		} else if (request == Request.RULES) {
			rulesLoaded();
			rulesLoaded(server);
		}
	}
	
	/**
	 * Triggers when data is changed
	 * 
	 * @param request  the type of data loaded
	 * @since 2007-11-10
	 */
	public void changed(Request request) {}
	/**
	 * Triggers when data is changed
	 * 
	 * @param server  the server which triggered this event
	 * @param request  the type of data loaded
	 * @since 2007-11-10
	 */
	public void changed(GameServer server, Request request) {
		if (request == Request.INFORMATION) {
			informationChanged();
			informationChanged(server);
		} else if (request == Request.PLAYERS) {
			playersChanged();
			playersChanged(server);
		} else if (request == Request.RULES) {
			rulesChanged();
			rulesChanged(server);
		}
	}
	
	/**
	 * Triggers when the challenge is loaded
	 * 
	 * @since 2007-10-25
	 */
	public void challengeLoaded() {}
	
	/**
	 * Triggers when server information is loaded
	 * 
	 * @param server  the server which triggered this event
	 * @since 2007-10-25
	 */
	public void informationLoaded(GameServer server) {}
	/**
	 * Triggers when server information is loaded
	 * 
	 * @since 2007-10-25
	 */
	protected void informationLoaded() {}
	
	/**
	 * Triggers when something changes the server information 
	 * 
	 * @param server  the server which triggered this event
	 * @since 2007-10-25
	 */
	public void informationChanged(GameServer server) {}
	/**
	 * Triggers when something changes the server information 
	 * 
	 * @since 2007-10-25
	 */
	protected void informationChanged() {}
	
	
	/**
	 * Triggers when the player list is loaded
	 * 
	 * @param server  the server which triggered this event
	 * @since 2007-10-25
	 */
	public void playersLoaded(GameServer server) {}
	/**
	 * Triggers when the player list is loaded
	 * 
	 * @since 2007-10-25
	 */
	protected void playersLoaded() {}
	
	/**
	 * Triggers when the player list is changed
	 * 
	 * @param server  the server which triggered this event
	 * @since 2007-10-25
	 */
	public void playersChanged(GameServer server) {}
	/**
	 * Triggers when the player list is changed
	 * 
	 * @since 2007-10-25
	 */
	protected void playersChanged() {}
	
	/**
	 * Triggers when the rules are loaded
	 * 
	 * @param server  the server which triggered this event
	 * @since 2007-10-25
	 */
	public void rulesLoaded(GameServer server) {}
	/**
	 * Triggers when the rules are loaded
	 * 
	 * @since 2007-10-25
	 */
	protected void rulesLoaded() {}

	/**
	 * Triggers when the rules are changed
	 * 
	 * @param server  the server which triggered this event
	 * @since 2007-10-25
	 */
	public void rulesChanged(GameServer server) {}
	/**
	 * Triggers when the rules are changed
	 * 
	 * @since 2007-10-25
	 */
	protected void rulesChanged() {}
	
	/**
	 * Triggers when an error is caught.  Many errors will be thrown to the
	 * caller, but others, running in different threads, will be sent to this
	 * method.
	 * 
	 * @param error  the error that triggered this event
	 * @param server  the server where the error took place
	 * @since 2007-10-25
	 */
	public void errorHandler(Throwable error, GameServer server) {}
}