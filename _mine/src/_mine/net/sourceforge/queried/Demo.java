package _mine.net.sourceforge.queried;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;


public class Demo {
    public static void main(String[] args) {
        try {
            //check("COD2", "79.99.24.12", 28962); 
            //check("COD2", "79.133.33.48", 27015);
        	check("COD2", "194.24.252.244", 28960);
            //check("COD2", "193.47.83.175", 28943);
            //check("COD2", "80.239.200.160", 32960);
        	//check("COD2", "82.193.210.1", 28964);
            //check("COD2", "193.47.83.181", 28932);
            //check("COD2", "77.75.212.207", 28960);
            //check("COD2", "tdm.callofduty.se", 32960);
            //check("BF2", "195.12.56.171", 29900);
            //check("BF2142", "85.236.101.62", 29900);
            //check("ETQW", "85.21.79.66", );
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void check(String gameType, String ip, int port) throws UnsupportedEncodingException {
        System.out.println("ServerInfo [" + gameType + "]:");

        ServerInfo serverInfo = QueriEd.serverQuery(gameType, ip, port);

        if (serverInfo == null) {
            System.out.println("ServerInfo == null");
        } else {
            System.out.println(
                    serverInfo.getName() + 
                    "\nIP: " + serverInfo.getIp() + ":" + serverInfo.getPort() + 
                    "\nGame: " + serverInfo.getGame() + 
                    "\nMap: " + serverInfo.getMap() + 
                    "\nPlayers: " + serverInfo.getPlayerCount() + "/" + serverInfo.getMaxPlayers() + 
                    "\nVersion: " + serverInfo.getGameVersion());
            System.out.println(
                    "Tickets: Team1: " + serverInfo.getTeam1Tickets()
                    + " :: Team2: " + serverInfo.getTeam2Tickets());
        }
         
        System.out.println("PlayerInfo:");

        ArrayList<PlayerInfo> playerInfo = QueriEd.playerQuery(gameType, ip, port);

        if (playerInfo != null && playerInfo.size() > 0) {
            Iterator<PlayerInfo> iter = playerInfo.iterator();
            int count = 1;

            while (iter.hasNext()) {
                PlayerInfo pInfo = iter.next();

                System.out.println(
                        count + ") " + pInfo.getName() + " [" + pInfo.getScore()
                        + "/" + pInfo.getKills() + "/" + pInfo.getDeaths() + "]"
                        + "ping: " + pInfo.getPing() + " rate: "
                        + pInfo.getRate());
                System.out.println("Clan: " + pInfo.getClan());
                count++;
            }
        } else {
            System.out.println("No players");
        }
        System.out.println("");
        System.out.println("");

        Iterator<String> games = QueriEd.getSupportedGames().keySet().iterator();

        System.out.print("Supported games");
        while (games.hasNext()) {
            String gameKey = games.next();

            System.out.print(" : " + gameKey);
        }
        System.out.println();
    }

}
