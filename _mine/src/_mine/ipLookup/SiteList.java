package _mine.ipLookup;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

public class SiteList
{
	public static String fetchFrom_dyn_dns_org()
	{
		String url = "http://checkip.dyndns.org/";
		String siteData = download(url);
		String ipString = "";
		
		if (ipString.length() <= 0)
		{
			StringTokenizer st = new StringTokenizer(siteData);
			boolean continueLoop = true;
			
			while (st.hasMoreTokens() && continueLoop)
			{
				if (st.nextToken().equals("Address:"))
				{
					continueLoop = false;
				}
			}
			
			st = new StringTokenizer(st.nextToken(), "<");
		}
		
		return ipString;
	}
	
	public static String fetchFrom_hyd_clan()
	{
		String url = "http://s90214777.onlinehome.us/HYDUPLOAD/KOTH/ip.php";
		String siteData = download(url);
		String ipString = "";
		
		if (ipString.length() <= 0)
		{
			ipString = siteData;
		}
		
		return ipString;
	}
	
	private static String download(String lookupAddr)
	{
		URL url = null;
		
		try
		{
			url = new URL(lookupAddr);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			System.exit(1);
			return null; // will never be reached
		}
		
		String siteData = "";
		
		try
		{
			InputStream in = url.openStream();
			
			byte[] buffer = new byte[1024];
			
			while (in.read(buffer) > 0)
			{
				siteData += new String(buffer).trim();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
			return null; // will never be reached
		}
		
		return siteData;
	}
}
