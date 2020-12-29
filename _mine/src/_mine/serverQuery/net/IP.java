/* IP.java */
package _mine.serverQuery.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import _mine.serverQuery.util.Util;

/**
 * This class contains various utilities for getting portions of a given IP, and
 * for downloading url resources for use as in the form of a <tt>String</tt>. It
 * also contains a method for downloading a list of servers (one IP per line,
 * x.x.x.x:port).
 * 
 * @author Dan
 * @version May 14, 2006
 */
public class IP
{
	/*
	 * the size of the buffer, in bytes, to use when downloading info from a
	 * url.
	 */
	private static final int BUFFER_SIZE = 1024;
	
	/**
	 * Return a String arary of IP addresses fetched from the given URL. There
	 * are no blank lines within the results, and null is returned if the list
	 * cannot be accessed at all.
	 * 
	 * @param url
	 *            the url with the list of IP addresses.
	 * @return a String array of IP addresses, or null if list isn't accessable
	 * @throws IOException
	 *             if there is an error with the download.
	 */
	public static String[] getIpList(URL url)
	{
		String s = downloadTextResource(url);
		
		if (s == null)
		{
			return null;
		}
		
		String[] list = Util.trimStrings(s.split("\n"));
		
		ArrayList<String> al = new ArrayList<String>();
		
		for (int i = 0; i < list.length; i++)
		{
			String line = list[i];
			
			String[] parts = Util.trimStrings(line.split(";"));
			
			if (parts.length > 0)
			{
				line = parts[0];
			}
			
			if (line.length() > 0)
			{
				al.add(line);
			}
		}
		
		String[] retData = new String[al.size()];
		for (int i = 0; i < al.size(); i++)
		{
			retData[i] = al.get(i);
		}
		
		return retData;
	}
	
	/**
	 * Downloads the data from the given URL. The url must point to an ASCII
	 * (text) resource, otherwise the data will be corrupt.
	 * 
	 * @param url
	 *            the pointer to the resource to download
	 * @return a <tt>String</tt> with the contents of the downloaded file
	 * @throws IOException
	 *             it shouldn't be an issue, but if there is an error
	 *             transferring data from the url.
	 */
	public static String downloadTextResource(URL url)
	{
		StringBuffer s = new StringBuffer(45);
		
		InputStream in = null;
		try
		{
			in = url.openStream();
		}
		catch (IOException e)
		{
			return null;
		}
		
		byte[] buffer = new byte[BUFFER_SIZE];
		try
		{
			for (int i = 0; in.read(buffer) > 0; i++)
			{
				s.append(new String(buffer).trim());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		return s.toString();
	}
	
	/**
	 * Returns the IP portion of an IP in the format of <b>x.x.x.x</b>:port, or
	 * an empty String if it cannot be parsed.
	 * 
	 * @param ip
	 *            the full ip in String format.
	 * @return the IP from the given IP.
	 */
	public static String getIP(String ip)
	{
		try
		{
			int i = ip.lastIndexOf(":");
			
			if (i < 0)
			{
				return ip;
			}
			
			return ip.substring(0, i);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Returns the port portion of an IP in the format of x.x.x.x:<b>port</b>,
	 * or -1 if <tt>s</tt> cannot be parsed.
	 * 
	 * @param ip
	 *            the full ip as a String.
	 * @return the port from the given IP as an integer.
	 */
	public static int getPort(String ip)
	{
		try
		{
			int index = ip.lastIndexOf(":");
			String port = ip.substring(index + 1);
			return Integer.parseInt(port);
		}
		catch (Exception e)
		{
			return -1;
		}
	}
}
