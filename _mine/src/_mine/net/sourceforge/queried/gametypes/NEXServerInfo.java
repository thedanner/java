package _mine.net.sourceforge.queried.gametypes;

/**
 * Game Modes, for future reference: DM 1 TDM 2 DOM 3 CTF 4 RUNE 5
 * 
 * @author mcrandello
 */

import java.util.ArrayList;
import java.util.Collections;

import _mine.net.sourceforge.queried.PlayerInfo;
import _mine.net.sourceforge.queried.ScoreComparator;
import _mine.net.sourceforge.queried.ServerInfo;
import _mine.net.sourceforge.queried.Util;

public class NEXServerInfo {

    public static ServerInfo getDetails(int localPort, String ipStr, int port, int infoType, 
        int queryType, int gameType) {

        String queryResult = Util.getInfo(localPort, ipStr, port, "getstatus", infoType, queryType, gameType);

        ServerInfo serverInfo = null;
        if (queryResult != null && queryResult.length() > 0) {
            queryResult = queryResult.substring(queryResult.indexOf("\\"));
            queryResult = queryResult.replaceAll("\\^([0-9a-wyzA-WYZ]|x[0-9a-fA-F]{6})", "");
            serverInfo = new ServerInfo();
            serverInfo.setGame(Util.getPart(queryResult, "gamename"));
            serverInfo.setIp(ipStr);
            serverInfo.setPort(port + "");
            serverInfo.setName(Util.getPart(queryResult, "hostname"));
            serverInfo.setPlayerCount(Util.getPart(queryResult, "clients"));
            serverInfo.setMaxPlayers(Util.getPart(queryResult, "sv_maxclients"));
            serverInfo.setMap(Util.getPart(queryResult, "mapname"));
            serverInfo.setFullResponse(queryResult);
        }

        return serverInfo;
    }

    public static ArrayList<PlayerInfo> getPlayers(int localPort, String ipStr, int port, int infoType, 
        int queryType, int gameType) {

        ArrayList<PlayerInfo> playerInfo = new ArrayList<PlayerInfo>();
        String queryResult = Util.getInfo(localPort, ipStr, port, "getstatus", infoType, queryType, gameType);
        if (queryResult == null || queryResult.length() < 1) {
            return playerInfo;
        }

        queryResult = queryResult.substring(queryResult.indexOf("\\"));
        queryResult = queryResult.replaceAll("\\^([0-9a-wyzA-WYZ]|x[0-9a-fA-F]{6})", "");

        if (queryResult != null && queryResult.length() > 0) {
            String[] plyrs = queryResult.split("\n");
            for (int x = 1; x < plyrs.length; x++) {
                String plyr = plyrs[x];
                String score = plyr.substring(0, plyr.indexOf(" "));
                String name = plyr.substring(plyr.lastIndexOf(" "));

                PlayerInfo player = new PlayerInfo();
                player.setDeaths(-9999);
                player.setKills(Integer.valueOf(score).intValue());
                player.setName(name);
                player.setScore(-9999);
                playerInfo.add(player);
            }
            Collections.sort(playerInfo, new ScoreComparator());
        }

        return playerInfo;
    }

}