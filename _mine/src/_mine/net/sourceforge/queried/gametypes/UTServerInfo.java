package _mine.net.sourceforge.queried.gametypes;

import java.util.ArrayList;
import java.util.Collections;

import _mine.net.sourceforge.queried.PlayerInfo;
import _mine.net.sourceforge.queried.ScoreComparator;
import _mine.net.sourceforge.queried.ServerInfo;
import _mine.net.sourceforge.queried.Util;

public class UTServerInfo {

	public static ServerInfo getDetails(int localPort, String ipStr, int port, int infoType, int queryType,
        int gameType) {
        
        String queryResult = 
            Util.getInfo(localPort, ipStr, port, "\\info\\", infoType, queryType, gameType);

        ServerInfo serverInfo = null;;
        if(queryResult != null && queryResult.length() > 1) {
            serverInfo = new ServerInfo();
            serverInfo.setName(Util.getPart(queryResult, "hostname"));
            serverInfo.setPort(Util.getPart(queryResult, "hostport"));
            serverInfo.setMap(Util.getPart(queryResult, "mapname"));
            serverInfo.setPlayerCount(Util.getPart(queryResult, "numplayers"));
            serverInfo.setMaxPlayers(Util.getPart(queryResult, "maxplayers"));

            serverInfo.setGame(Util.getPart(queryResult, "gametype"));
            serverInfo.setGameVersion(Util.getPart(queryResult, "gamever"));
            serverInfo.setIp(ipStr);
            serverInfo.setFullResponse(queryResult);
        }

        return serverInfo;
	}

    public static ArrayList<PlayerInfo> getPlayers(int localPort, String ipStr, int port, int infoType, int queryType,
        int gameType) {
            
        String recStr = Util.getInfo(localPort, ipStr, port, "\\players\\", infoType, queryType, gameType);

        ArrayList<PlayerInfo> playerInfo = new ArrayList<PlayerInfo>();
        if(recStr != null && recStr.length() > 0) {
            String[] plyrs = recStr.split("player_");
            for(int x=1; x < plyrs.length; x++) {
                String plyrLine = "\\player_" + plyrs[x];
           
                PlayerInfo player = new PlayerInfo();
                player.setName(Util.getPart(plyrLine, "player_"+ (x-1)));
                player.setKills(Integer.valueOf(Util.getPart(plyrLine, "frags_"+ (x-1))).intValue());
                playerInfo.add(player);
            }
            Collections.sort(playerInfo, new ScoreComparator());
        }
        return playerInfo;
    }
}
