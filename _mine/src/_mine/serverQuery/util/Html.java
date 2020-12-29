/* Html.java */
package _mine.serverQuery.util;

import java.util.List;

import _mine.net.sourceforge.queried.PlayerInfo;
import _mine.net.sourceforge.queried.ServerInfo;

/**
 * 
 * @author Dan
 * @version Jan 21, 2006
 */
public class Html
{
	// tags
	/** open html */
	public static final String HTML1 = "<html>";
	/** close html */
	public static final String HTML2 = "</html>";
	/** open bold */
	public static final String B1 = "<b>";
	/** close bold */
	public static final String B2 = "</b>";
	/** open italics */
	public static final String I1 = "<i>";
	/** close */
	public static final String I2 = "</i>";
	/** open underline */
	public static final String U1 = "<u>";
	/** close underline */
	public static final String U2 = "</u>";
	/** paragraph (open/close) */
	public static final String P = "<p />";
	/** open paragraph */
	public static final String P1 = "<p>";
	/** close parabraph */
	public static final String P2 = "</p>";
	/** line break */
	public static final String BR = "<br />";
	
	// non-html strings
	/** newline */
	public static final String N = String.format("%n");
	/** space */
	public static final String S = " ";
	
	/**
	 * 
	 * @param serverAddrs
	 * @param servers
	 * @param players
	 * @return
	 */
	public static String generatePlayerList(String[] serverAddrs,
			ServerInfo[] servers, List<PlayerInfo>[] players)
	{
		if (serverAddrs == null)
			return "";
		
		StringBuilder text = new StringBuilder();
		
		text.append(HTML1);
		
		for (int i = 0; i < serverAddrs.length; i++)
		{
			ServerInfo si = servers[i];
			List<PlayerInfo> al = players[i];
			
			if (si != null)
			{
				if (si.getName() != null)
				{
					text
						.append(P1)
							.append(U1)
								.append(S).append("Server ")
								.append(B1).append((i + 1)).append(B2)
							.append(U2)
							.append(":").append(BR)
							.append(B1)
								.append(S).append(si.getName())
							.append(B2)
							.append(BR)
							.append(B1)
								.append(S).append(si.getIp()).append(":")
								.append(si.getPort())
							.append(B2)
							.append(BR)
							.append(S)
							.append(B1)
								.append(si.getPlayerCount())
							.append(B2)
							.append(I1).append(" of ").append(I2)
							.append(B1).append(si.getMaxPlayers()).append(B2)
							.append(" players")
						.append(P).append(P1);
					
					for (int j = 0; j < al.size(); j++)
					{
						PlayerInfo pi = al.get(j);
						
						text
    						.append((j + 1)).append(": ")
    						.append(B1).append(pi.getName()).append(B2)
    						.append(I1)
    							.append(" (K:").append(pi.getKills())
    							.append(",P:").append(pi.getPing())
    							.append(")")
    						.append(I2)
    						.append(BR);
					}
					text.append(P2);
					
					if (i != serverAddrs.length - 1)
					{
						text.append(P).append(P);
					}
				}
			}
		}
		
		text.append(HTML2);
		
		return text.toString();
	}
	
	/**
	 * 
	 * @param si
	 * @param executablePath
	 * @return
	 */
	public static String generateJoinMessage(ServerInfo si,
			String executablePath)
	{
		return HTML1 + I1 + "Connect to" + I2 + " " + B1
				+ Text.removeColorTags(si.getName()) + HTML2 + N + HTML1 + B1
				+ si.getIp() + ":" + si.getPort() + B2 + " " + I1 + "with" + I2
				+ " " + B1 + si.getPlayerCount() + B2 + I1 + " of " + I2 + B1
				+ si.getMaxPlayers() + B2 + " " + I1 + "players" + I2 + HTML2
				+ N + HTML1 + I1 + "using" + I2 + " \"" + B1 + executablePath
				+ B2 + "\"" + I1 + "?" + I2 + HTML2;
	}
	
	/**
	 * 
	 * @param ip
	 * @param port
	 * @param executablePath
	 * @return
	 */
	public static String generateJoinMessage(String ip, int port,
			String executablePath)
	{
		return HTML1 + I1 + "Connect to" + I2 + " " + B1 + ip + ":" + port + B2
				+ HTML2 + N + HTML1 + I1 + "using" + I2 + " \"" + B1
				+ executablePath + B2 + "\"" + I1 + "?" + I2 + HTML2 + N
				+ HTML1 + I1
				+ "If the server is down, you will be unable to join." + I2
				+ HTML2;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String generateFileDoesNotExistMessgae(String path)
	{
		return HTML1 + I1 + "The selected file " + I2 + "\"" + B1 + path + B2
				+ "\"" + I1 + " does not exist." + I2 + HTML2 + N + HTML1 + I1
				+ "You may not be able to launch Enemy Territory "
				+ "using that file." + I2 + HTML2;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String generateDirectoryMessgae(String path)
	{
		return HTML1 + I1 + "The selected path " + I2 + "\"" + B1 + path + B2
				+ "\"" + I1 + " denotes a directory." + I2 + HTML2 + N + HTML1
				+ I1 + "You may not be able to launch Enemy Territory "
				+ "using that file." + I2 + HTML2;
	}
}
