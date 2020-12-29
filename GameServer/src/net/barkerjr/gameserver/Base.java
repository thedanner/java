/*
 * Game Server Library - Querying server requests and returning them as XML
 * Copyright (C) 2008  BarkerJr <http://barkerjr.net/java/GameServer/>
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

import java.util.ArrayList;
import java.util.List;

/**
 * Provides standard methods for classes in the library
 * 
 * @author BarkerJr
 * @since  2008-02-03
 */
public abstract class Base {
	/**
	 * Any listeners which should be triggered when data is loaded or changed
	 * 
	 * @since 2007-10-25
	 */
	protected List<Listener> listeners = new ArrayList<Listener>();

	/**
	 * Hooks a change listener 
	 * 
	 * @param listener  the listener to hook into this server
	 * @since 2007-10-25
	 */
	public void addListener(final Listener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes the given hook from this object
	 * 
	 * @param listener  the hook to remove
	 * @since 2007-10-25
	 */
	public void removeListener(final Listener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Sends error to the listeners, if there are any
	 * 
	 * @param error  the error to send
	 * @since  2008-02-03
	 */
	protected void handleError(Throwable error) {
		Listener[] listenerArray = listeners.toArray(new Listener[0]);
		for (Listener listener: listenerArray) {
			listener.errorHandler(error, null);
		}
	}
}