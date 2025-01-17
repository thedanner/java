package _mine.net.sourceforge.queried.gametypes;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import _mine.net.sourceforge.queried.KillsComparator;
import _mine.net.sourceforge.queried.PlayerInfo;
import _mine.net.sourceforge.queried.ServerInfo;
import _mine.net.sourceforge.queried.Util;


public class SourceServerInfo {

	public static ArrayList<PlayerInfo> getPlayers(int localPort, String ipStr, int port, int infoType, int queryType,
        int gameType) {
            
        byte[] buf = null;
        try {
            buf = Util.getInfo(localPort, ipStr, port, "U", infoType, queryType, gameType).getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

        if(buf == null || buf.length == 0) {
            return null;            
        } else if(buf[0] != buf[1] || buf[1] != buf[2] || buf[2] != buf[3] || buf[4] != 'D') { 
            return null;            
        }
        
        ArrayList<PlayerInfo> sorted = new ArrayList<PlayerInfo>();
               
		int off = 7;

        while(off < buf.length) {
            PlayerInfo playerInfo = new PlayerInfo();
			StringBuffer playerName = new StringBuffer(20);
			while(buf[off] != 0) {
				playerName.append((char)(buf[off++] & 255));
			}
			off++;
            playerInfo.setName(playerName.toString().trim());
            playerInfo.setKills((buf[off] & 255) | ((buf[off+1] & 255) << 8) | 
                ((buf[off+2] & 255) << 16) | ((buf[off+3] & 255) << 24));
            sorted.add(playerInfo);
			off += 9;
		}
		Collections.sort(sorted, new KillsComparator());
        
        return sorted;
	}

	public static ServerInfo getDetails(int localPort, String ipStr, int port, int infoType, int queryType,
        int gameType) {
            
        byte[] buf = null;
        try {
            buf = Util.getInfo(localPort, ipStr, port, "TSource Engine Query", infoType, queryType, gameType).getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

        if(buf == null || buf.length == 0) {
            return null;            
        } else if(buf[0] != buf[1] || buf[1] != buf[2] || buf[2] != buf[3] || buf[4] != 'I') { 
            return null;            
		}

        ServerInfo serverInfo = new ServerInfo();
        
        InetAddress inettst;
        try {
            inettst = InetAddress.getByName(ipStr);
            serverInfo.setIp(inettst.getHostAddress());
        } catch (UnknownHostException e) {
            serverInfo.setIp(ipStr);
        }

        serverInfo.setPort(port +"");

    	int off = 6;

        StringBuffer netName = new StringBuffer(20);
        while(buf[off] != 0) {
            netName.append((char)(buf[off++] & 255));
        }
        serverInfo.setName(netName.toString());

        off++;
                
        StringBuffer mapName = new StringBuffer(20);
        while(buf[off] != 0) {
            mapName.append((char)(buf[off++] & 255));
        }
        serverInfo.setMap(mapName.toString());
        
        off++;

		// skip game directory
		while(buf[off] != 0) {
			off++;
		}
		
		off++;

		StringBuffer gameDesc = new StringBuffer(20);
		while(buf[off] != 0) {
			gameDesc.append((char)(buf[off++] & 255));
		}
        serverInfo.setGame(gameDesc.toString());

        off++;
        
        // skip app id
        off++;
        
        off++;
		
		int playerCount = buf[off] & 255;
        serverInfo.setPlayerCount(playerCount +"");
		
		off++;
		
		int maxPlayerCount = buf[off] & 255;
        serverInfo.setMaxPlayers(maxPlayerCount +"");
        serverInfo.setFullResponse(new String(buf));

		return serverInfo;
	}
	
}
