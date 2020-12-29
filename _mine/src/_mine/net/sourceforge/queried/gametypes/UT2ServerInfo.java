package _mine.net.sourceforge.queried.gametypes;

import java.util.ArrayList;
import java.util.Collections;

import _mine.net.sourceforge.queried.PlayerInfo;
import _mine.net.sourceforge.queried.QueriEd;
import _mine.net.sourceforge.queried.ScoreComparator;
import _mine.net.sourceforge.queried.ServerInfo;
import _mine.net.sourceforge.queried.Util;

/**
 * http://unreal.student.utwente.nl/UT2003-queryspec.html
 *
 * @author Jonas Berlin <xkr47@outerspace.dyndns.org>
 */
public class UT2ServerInfo {

	/**
	 * Read an integer and advance offset.
	 *
	 * @param str the full response string
	 * @param offsetHolder the offset holder
	 * @return the integer
	 */
	private static int getInt(String str, int[] offsetHolder) {
		int v =
			((str.charAt(offsetHolder[0] + 0) & 255) << 0) |
			((str.charAt(offsetHolder[0] + 1) & 255) << 8) |
			((str.charAt(offsetHolder[0] + 2) & 255) << 16) |
			((str.charAt(offsetHolder[0] + 3) & 255) << 24);

		offsetHolder[0] += 4;
		return v;
	}

	/**
	 * Read a string and advance offset. Any color codes are stripped.
	 *
	 * @param str the full response string
	 * @param offsetHolder the offset holder
	 * @return the string
	 */
	private static String getString(String src, int[] offsetHolder) {
		int len = src.charAt(offsetHolder[0]++);
		if(len == 0 || len == 128) {
			return "";

		} else if(len > 0 && len <= 127) {
			String result = src.substring(offsetHolder[0], offsetHolder[0] + len - 1);
			offsetHolder[0] += len;
			return result;

		} else if(len > 128 && len <= 255) {
			len -= 128;
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<len-1; i++) {
				char ch = src.charAt(offsetHolder[0] + i*2);
				if(ch == '^' && src.charAt(offsetHolder[0] + i*2 + 2) == '#') {
					// color - skip it
					i += 2;
				} else {
					sb.append(ch);
				}
			}
			offsetHolder[0] += len * 2;
			return sb.toString();

		} else {
			throw new RuntimeException("Bad data in response");
		}
	}
 
	public static ServerInfo getDetails(int localPort, String ipStr, int port, int infoType, int queryType,
        int gameType) {
        
		if(queryType != QueriEd.QUERY_UT2S) {
			// fall back to gamespy etc
			return UTServerInfo.getDetails(localPort, ipStr, port, infoType, queryType, gameType);
		}

        String queryResult = 
            Util.getInfo(localPort, ipStr, port, "\u0078\u0000\u0000\u0000\u0000", infoType, queryType, gameType);

		if(queryResult == null || queryResult.length() == 0) return null;

		ServerInfo serverInfo = new ServerInfo();
		try {
			// verify we got what we wanted
			int[] offsetHolder = { 0 };
			if(queryResult.charAt(0) == 128) offsetHolder[0] = 4;
			if(queryResult.charAt(offsetHolder[0]) != 0) return null;
			offsetHolder[0]++;

            serverInfo.setIp(ipStr);

			getInt(queryResult, offsetHolder); // ignore server id
			getString(queryResult, offsetHolder); // ignore server ip (because it's not reliable)
			serverInfo.setPort(Integer.toString(getInt(queryResult, offsetHolder))); // ignore game port
			getInt(queryResult, offsetHolder); // ignore query port
			serverInfo.setName(getString(queryResult, offsetHolder)); // server name
			serverInfo.setMap(getString(queryResult, offsetHolder)); // map
			serverInfo.setGame(getString(queryResult, offsetHolder)); // game type
			serverInfo.setPlayerCount(Integer.toString(getInt(queryResult, offsetHolder))); // currently playing
			serverInfo.setMaxPlayers(Integer.toString(getInt(queryResult, offsetHolder))); // max players
			getInt(queryResult, offsetHolder); // ignore ping
            serverInfo.setFullResponse(queryResult);

		} catch(StringIndexOutOfBoundsException e) {
			return null;
		}

		// attempt to get game version also.. it's available through
		// the game info query

        queryResult = 
            Util.getInfo(localPort, ipStr, port, "\u0078\u0000\u0000\u0000\u0001", infoType, queryType, gameType);

		if(queryResult == null || queryResult.length() == 0) return serverInfo;

		try {
			// verify we got what we wanted
			int[] offsetHolder = { 0 };
			if(queryResult.charAt(0) == 128) offsetHolder[0] = 4;
			if(queryResult.charAt(offsetHolder[0]) == 1) {
				offsetHolder[0]++;

				// loop through the key-value pairs
				while(offsetHolder[0] < queryResult.length()) {
					String key = getString(queryResult, offsetHolder);
					String value = getString(queryResult, offsetHolder);
					if(key.equals("ServerVersion")) {
						serverInfo.setGameVersion(value);
						break;
					}
				}
			}

		} catch(StringIndexOutOfBoundsException e) {
			// on failure, jost continue without server version..
		}

		return serverInfo;
	}

    public static ArrayList<PlayerInfo> getPlayers(int localPort, String ipStr, int port, int infoType, int queryType,
        int gameType) {
            
		if(queryType != QueriEd.QUERY_UT2S) {
			// fall back to gamespy etc
			return UTServerInfo.getPlayers(localPort, ipStr, port, infoType, queryType, gameType);
		}

        String queryResult = 
            Util.getInfo(localPort, ipStr, port, "\u0078\u0000\u0000\u0000\u0002", infoType, queryType, gameType);

		if(queryResult == null || queryResult.length() == 0) return null;

        ArrayList<PlayerInfo> playerInfo = new ArrayList<PlayerInfo>();
		try {
			// verify we got what we wanted
			int[] offsetHolder = { 0 };
			if(queryResult.charAt(0) == 128) offsetHolder[0] = 4;
			if(queryResult.charAt(offsetHolder[0]) != 2) return null;
			offsetHolder[0]++;

			// loop through the players
			while(offsetHolder[0] < queryResult.length()) {
                PlayerInfo player = new PlayerInfo();

				getInt(queryResult, offsetHolder); // ignore player id
				player.setName(getString(queryResult, offsetHolder)); // player name
				getInt(queryResult, offsetHolder); // ignore player ping
				player.setScore(getInt(queryResult, offsetHolder)); // player score
				getInt(queryResult, offsetHolder); // ignore player stats id
				
				playerInfo.add(player);
			}
		} catch(StringIndexOutOfBoundsException e) {
			return null;
		}

		Collections.sort(playerInfo, new ScoreComparator());
		return playerInfo;
    }
}
