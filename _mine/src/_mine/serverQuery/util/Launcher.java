/* Launcher.java */
package _mine.serverQuery.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import _mine.serverQuery.net.IP;

import com.Ostermiller.util.Browser;

/**
 * @author Dan
 * @version Mar 7, 2006
 */
public class Launcher
{
	private static boolean browserReady = false;
	
	/**
	 * 
	 * @param urlPath
	 */
	public static void launchURL(String urlPath)
	{
		URL url = null;
		try
		{
			url = new URL(urlPath);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			return;
		}
		
		if (!browserReady)
		{
			Browser.init();
			browserReady = true;
		}
		
		try
		{
			Browser.displayURL(url.toString());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param ip
	 * @param port
	 * @param f
	 * @throws IOException
	 */
	public static void launchProgram(String ip, int port, String path)
			throws IOException
	{
		if (IP.getPort(ip) != -1)
		{
			String origIP = ip;
			ip = IP.getIP(origIP);
			port = IP.getPort(origIP);
		}
		
		String[] cmd = generateETLauchCommand(ip, port, path);
		
		File workingDir = new File(path).getParentFile();
		
		Util.p("Executing command :: ");
		
		for (int i = 0; i < cmd.length; i++)
		{
			Util.p(" ", cmd[i]);
		}
		
		Util.pl(" ::");
		
		Runtime.getRuntime().exec(cmd, null, workingDir);
	}
	
	/**
	 * 
	 * @param ip
	 * @param port
	 * @param path
	 * @return
	 */
	private static String[] generateETLauchCommand(
			String ip, int port, String path)
	{
		String q = "\""; // single quote char
		String cmd1 = q + path + q;
		String cmd2 = "+connect";
		String cmd3 = ip + ":" + port;
		
		if (Util.isWindowsOS())
		{
			return new String[] { cmd1, cmd2, cmd3 };
		}
		
		else if (Util.isLinuxOS())
		{
			return new String[] { "/bin/bash", "-c", cmd1, cmd2, cmd3 };
		}
		else
		{
			return new String[] { cmd1, cmd2, cmd3 };
		}
	}
}
