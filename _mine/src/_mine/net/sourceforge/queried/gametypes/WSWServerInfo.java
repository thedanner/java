package _mine.net.sourceforge.queried.gametypes;


import java.util.ArrayList;
import java.util.Collections;

import _mine.net.sourceforge.queried.PlayerInfo;
import _mine.net.sourceforge.queried.ScoreComparator;
import _mine.net.sourceforge.queried.ServerInfo;
import _mine.net.sourceforge.queried.Util;


/**
 * Thanks GibbaTheHutt (quakenet:#warsow) for pointing me to the correct query string.
 * 
 * @author mcrandello
 */
public class WSWServerInfo {
	
    public static ServerInfo getDetails(int localPort, String ipStr, int port, int infoType, int queryType,
            int gameType) {
        String queryResult = Util.getInfo(localPort, ipStr, port, "getstatus",
                infoType, queryType, gameType);

        ServerInfo serverInfo = null;

        if (queryResult != null && queryResult.length() > 0) {
            queryResult = queryResult.substring(queryResult.indexOf("\n"));
            queryResult = queryResult.replaceAll(
                    "\\^([0-9a-wyzA-WYZ]|x[0-9a-fA-F]{6})", "");
            serverInfo = new ServerInfo();
            serverInfo.setGame(Util.getPart(queryResult, "gamename")); 
            serverInfo.setIp(ipStr);
            serverInfo.setPort(port + "");
            serverInfo.setName(Util.getPart(queryResult, "sv_hostname"));

            serverInfo.setPlayerCount(
                    queryResult.substring(queryResult.indexOf("\\clients\\") + 9,
                    queryResult.indexOf("\\clients\\") + 10));
            serverInfo.setMaxPlayers(Util.getPart(queryResult, "sv_maxclients"));
            serverInfo.setMap(Util.getPart(queryResult, "mapname"));
            serverInfo.setFullResponse(queryResult);
        }

        return serverInfo;
    }

    public static ArrayList<PlayerInfo> getPlayers(int localPort, String ipStr, int port, int infoType, int queryType,
            int gameType) {
            
        ArrayList<PlayerInfo> playerInfo = new ArrayList<PlayerInfo>();
        String queryResult = Util.getInfo(localPort, ipStr, port, "getstatus",
                infoType, queryType, gameType);

        if (queryResult == null || queryResult.length() < 1) {
            return playerInfo;
        }
        
        queryResult = queryResult.substring(
                queryResult.indexOf("\\clients\\") + 9,
                queryResult.indexOf("\\challenge\\"));
        queryResult = queryResult.replaceAll(
                "\\^([0-9a-wyzA-WYZ]|x[0-9a-fA-F]{6})", "");
        
        if (queryResult != null
                && Integer.parseInt(queryResult.substring(0, 1)) != 0) // if clients = 0 then forget it
        {
            String[] plyrs = queryResult.split("\n");

            for (int i = 0; i < plyrs.length; i++) {}
            for (int x = 1; x < plyrs.length; x++) {
                
                String plyr = plyrs[x];
                String score = plyr.substring(0, plyr.indexOf(" ")).trim();
                String whatsit = plyr.substring(plyr.indexOf(" "), plyr.lastIndexOf(" ")).trim();
                String name = plyr.substring(plyr.indexOf("\"") + 1, plyr.lastIndexOf("\"")).trim();
                PlayerInfo player = new PlayerInfo();

                player.setDeaths(-9999);
                player.setKills(Integer.valueOf(score).intValue());
                // player.setKills(-9999);
                player.setName(name);
                player.setScore(Integer.valueOf(whatsit).intValue());                
                playerInfo.add(player);
                
            }
                Collections.sort(playerInfo, new ScoreComparator());

        }
        return playerInfo;
        
    }
	
}
