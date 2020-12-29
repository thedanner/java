package _mine.net.sourceforge.queried.gametypes;

import java.util.ArrayList;
import java.util.Collections;

import _mine.net.sourceforge.queried.PlayerInfo;
import _mine.net.sourceforge.queried.ScoreComparator;
import _mine.net.sourceforge.queried.ServerInfo;
import _mine.net.sourceforge.queried.Util;

public class AAServerInfo {

	public static ServerInfo getDetails(int localPort, String ipStr, int port, int infoType, int queryType,
        int gameType) {

        String queryResult = Util.getInfo(localPort, ipStr, port, infoType, queryType, gameType);
        ServerInfo serverInfo = null;
        if(queryResult != null && queryResult.length() > 0) {
            serverInfo = new ServerInfo();
            serverInfo.setGame(Util.getPartGS2(queryResult, "gamename"));
            serverInfo.setGameVersion(Util.getPartGS2(queryResult, "gamever"));
            serverInfo.setIp(ipStr);
            serverInfo.setPort(Util.getPartGS2(queryResult, "hostport"));
            serverInfo.setName(Util.getPartGS2(queryResult, "hostname"));
            serverInfo.setMap(Util.getPartGS2(queryResult, "mapname"));
            serverInfo.setPlayerCount(Util.getPartGS2(queryResult, "numplayers"));
            serverInfo.setMaxPlayers(Util.getPartGS2(queryResult, "maxplayers"));
            serverInfo.setFullResponse(queryResult);
        }

		return serverInfo;
	}

    public static ArrayList<PlayerInfo> getPlayers(int localPort, String ipStr, int port, int infoType, int queryType, 
        int gameType) {
            
        String recStr = Util.getInfo(localPort, ipStr, port, infoType, queryType, gameType);
        char chr = 00;
        //char chr2 = 02;
        int start = recStr.indexOf("enemy_"+ chr + chr) + 8;
        int end = recStr.indexOf("score_t" + chr) - 1;
        String stripped = recStr.substring(start, end);
        ArrayList<PlayerInfo> playerInfo = new ArrayList<PlayerInfo>();
        String[] pieces = stripped.split(chr+"");
        for(int i=0; i < pieces.length; i++) {
            //System.out.println("piece["+ i +"]: "+ pieces[i]);
            PlayerInfo player = new PlayerInfo();
            //leader
            int leader = Integer.parseInt(pieces[i++]);
            // goal
            int goal = Integer.parseInt(pieces[i++]);
            player.setScore(leader + goal);
            // honor
            i++;
            // name
            player.setName(pieces[i++]);
            // ping
            i++;
            // roe
            i++;
            // kia
            player.setDeaths(Integer.parseInt(pieces[i++]));
            // enemy
            player.setKills(Integer.parseInt(pieces[i]));
            playerInfo.add(player);
        }
        Collections.sort(playerInfo, new ScoreComparator());
        
        return playerInfo;
    }
}
