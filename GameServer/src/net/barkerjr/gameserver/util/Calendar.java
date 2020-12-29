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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Provides formatting and parsing for dates in a specific format.
 * 
 * @author BarkerJr
 * @since 2007-11-28
 */
public class Calendar extends GregorianCalendar {
	/** The serialization number */
	private static final long serialVersionUID = 1L;
	
	/**  The date format we use */
	private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";
	
	/**
	 * Initializes the class
	 * 
	 * @since 2007-11-28
	 */
	public Calendar() {
		super();
	}
	/**
	 * Parses the given date
	 * 
	 * @param dateTime  a representation of a date
	 * @throws ParseException  if the given date is not a date
	 * @since 2007-11-28
	 */
	public Calendar(String dateTime) throws ParseException {
		super();
		setTime(new SimpleDateFormat(DATE_FORMAT, Locale.US).parse(dateTime));
	}

	/**
	 * Formats the calendar for human reading
	 * 
	 * @since 2007-11-28
	 */
	@Override
	public String toString() {
		return toString(this);
	}
	/**
	 * Formats the calendar for human reading
	 * 
	 * @param c the calendar to format
	 * @return a human-readable date
	 * @since 2007-11-28
	 */
	public static String toString(java.util.Calendar c) {
		return new SimpleDateFormat(DATE_FORMAT, Locale.US).format(c.getTime());
	}
}