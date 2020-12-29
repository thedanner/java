package _mine.net.sourceforge.queried.gametypes;

import java.util.ArrayList;
import java.util.Collections;

import _mine.net.sourceforge.queried.PlayerInfo;
import _mine.net.sourceforge.queried.ScoreComparator;
import _mine.net.sourceforge.queried.ServerInfo;
import _mine.net.sourceforge.queried.Util;

public class BF1942ServerInfo {

	public static ServerInfo getDetails(int localPort, String ipStr, int port, int infoType, int queryType,
        int gameType) {
            
        String queryResult = Util.getInfo(localPort, ipStr, port, "\\info\\", infoType, queryType, gameType);
        ServerInfo serverInfo = null;
        if(queryResult != null && queryResult.length() > 0) {
            serverInfo = new ServerInfo();
            serverInfo.setGame(Util.getPart(queryResult, "gameId"));
            serverInfo.setGameVersion(Util.getPart(queryResult, "version"));
            serverInfo.setIp(ipStr);
            serverInfo.setPort(Util.getPart(queryResult, "hostport"));
            serverInfo.setName(Util.getPart(queryResult, "hostname"));
            serverInfo.setMap(Util.getPart(queryResult, "mapname"));
            serverInfo.setPlayerCount(Util.getPart(queryResult, "numplayers"));
            serverInfo.setMaxPlayers(Util.getPart(queryResult, "maxplayers"));
            serverInfo.setTeam1Tickets(Util.getPart(queryResult, "tickets1"));
            serverInfo.setTeam2Tickets(Util.getPart(queryResult, "tickets2"));
            serverInfo.setFullResponse(queryResult);
        }

		return serverInfo;
	}

    public static ArrayList<PlayerInfo> getPlayers(int localPort, String ipStr, int port, int infoType, int queryType, 
        int gameType) {
            
        String recStr = Util.getInfo(localPort, ipStr, port, "\\players\\", infoType, queryType, gameType);
        ArrayList<PlayerInfo> playerInfo = new ArrayList<PlayerInfo>();
        if(recStr != null && recStr.length() > 0) {
            String[] plyrs = recStr.split("deaths_");
            for(int x=1; x < plyrs.length; x++) {
                String plyrLine = "\\deaths_" + plyrs[x];
           
                PlayerInfo player = new PlayerInfo();
                player.setDeaths(Integer.valueOf(Util.getPart(plyrLine, "deaths_"+ (x-1))).intValue());
                player.setKills(Integer.valueOf(Util.getPart(plyrLine, "kills_"+ (x-1))).intValue());
                player.setName(Util.getPart(plyrLine, "playername_"+ (x-1)));
                player.setScore(Integer.valueOf(Util.getPart(plyrLine, "score_"+ (x-1))).intValue());
                playerInfo.add(player);
            }
            Collections.sort(playerInfo, new ScoreComparator());
        }
        return playerInfo;
    }
}
