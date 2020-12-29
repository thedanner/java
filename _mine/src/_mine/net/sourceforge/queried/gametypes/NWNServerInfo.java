package _mine.net.sourceforge.queried.gametypes;

import _mine.net.sourceforge.queried.ServerInfo;
import _mine.net.sourceforge.queried.Util;

public class NWNServerInfo {

	public static ServerInfo getDetails(int localPort, String ipStr, int port, int infoType, int queryType,
        int gameType) {

        String queryResult = Util.getInfo(localPort, ipStr, port, infoType, queryType, gameType);
        ServerInfo serverInfo = null;
        if(queryResult != null && queryResult.length() > 0) {
            serverInfo = new ServerInfo();
            serverInfo.setGame("Neverwinter Nights");
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
}
