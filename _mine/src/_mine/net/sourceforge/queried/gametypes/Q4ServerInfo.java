package _mine.net.sourceforge.queried.gametypes;


import java.util.ArrayList;
import java.util.regex.*;
import _mine.net.sourceforge.queried.PlayerInfo;
import _mine.net.sourceforge.queried.ServerInfo;
import _mine.net.sourceforge.queried.Util;


public class Q4ServerInfo {

	public static ServerInfo getDetails(int localPort, String ipStr, int port, int infoType, int queryType,
        int gameType) {
            
        String queryResult = Util.getInfo(localPort, ipStr, port, "getInfo", infoType, queryType, gameType);
        if(queryResult == null || queryResult.length() < 1) {
            return null;
        }
        queryResult = queryResult.replaceAll("\\^([0-9a-wyzA-WYZ]|x[0-9a-fA-F]{6})", "");
        
        String[] masterServerPacketArray = queryResult.split("\u0000\u0000\u0000");
                
        ServerInfo serverInfo = null;
        if(queryResult != null && masterServerPacketArray[1].length() > 0) {
            serverInfo = new ServerInfo();
            
            String plyrs = masterServerPacketArray[2].substring(1);
            Pattern playerPattern = Pattern.compile("(.)(.)\\x00(.{2})\\x00\\x00([\\w\\s/\\x19-\\x2f\\x51-\\x5e\\x7d\\x7b@!:~_]+)\\x00?([\\w\\s/\\x19-\\x2f\\x51-\\x5e\\x7d\\x7b@!:~_]+|\\x00?)");
            Matcher m = playerPattern.matcher(plyrs);
            int plyrcnt = 0;
            while(m.find()) {
            	plyrcnt++;
            }
            String playercount = String.valueOf(plyrcnt);
            
            String tempGameVersion =Util.getPartGS2(masterServerPacketArray[1], "si_version");
            tempGameVersion = tempGameVersion.replaceAll("[a-zA-Z]","").trim();
            String GameVersion = tempGameVersion.substring((tempGameVersion.indexOf("  ") + 2), tempGameVersion.indexOf(" ", tempGameVersion.indexOf("  ") + 3));// there's a whole lot of fluff in the version field.
            
    		serverInfo.setGame(Util.getPartGS2(masterServerPacketArray[1], "si_gameType")); 
            serverInfo.setIp(ipStr);
            serverInfo.setPort(port +"");
            serverInfo.setName(Util.getPartGS2(masterServerPacketArray[1], "si_name"));
            serverInfo.setGameVersion(GameVersion);
            serverInfo.setPlayerCount(playercount);
            serverInfo.setMaxPlayers(Util.getPartGS2(masterServerPacketArray[1], "si_maxPlayers"));
            serverInfo.setMap(Util.getPartGS2(masterServerPacketArray[1], "si_map"));
            serverInfo.setFullResponse(queryResult);
        }

		return serverInfo;
	}

    public static ArrayList<PlayerInfo> getPlayers(int localPort, String ipStr, int port, int infoType, int queryType,
        int gameType) {
        
        String queryResult = Util.getInfo(localPort, ipStr, port, "getInfo", infoType, queryType, gameType);
        ArrayList<PlayerInfo> playerInfo = new ArrayList<PlayerInfo>();
        if(queryResult == null || queryResult.length() < 1) {
            return playerInfo;
            }else {
        queryResult = queryResult.replaceAll("\\^([0-9a-wyzA-WYZ]|x[0-9a-fA-F]{6})", "");
        
        String[] masterServerPacketArray = queryResult.split("\u0000\u0000\u0000");
      
        String plyrs = masterServerPacketArray[2].substring(1);
       
/*        
 *  Thanks to Ronny Witzgall for the use of the regexp pattern, it's a lot
 *  more reliable than just splitting it by the 0's like I was doing
 *  -mcrandello
*/         
         
         Pattern playerPattern = Pattern.compile("(.)(.)\\x00(.{2})\\x00\\x00([\\w\\s/\\x19-\\x2f\\x51-\\x5e\\x7d\\x7b@!:~_]+)\\x00?([\\w\\s/\\x19-\\x2f\\x51-\\x5e\\x7d\\x7b@!:~_]+|\\x00?)");
         Matcher m = playerPattern.matcher(plyrs);
         
         while(m.find()) {
        	 PlayerInfo player = new PlayerInfo();
        	 
        	 byte[]rawPing = m.group(2).getBytes();
             if (rawPing.length >1) {
                 player.setPing(PosInt(rawPing[0]) + PosInt(rawPing[1])); 
                 } else if (rawPing.length >0){
                     player.setPing(PosInt(rawPing[0]));
                 } else {
                 	player.setPing(0);
                 }
             byte[]rawRate = m.group(3).getBytes();
             if (rawRate.length >1) {
             player.setRate(PosInt(rawRate[0]) + PosInt(rawRate[1]));
             } else if (rawRate.length >0){
                 player.setRate(PosInt(rawRate[0]));
             } else {
             	player.setRate(15000); //that's probably what it is anyway :-( 
             }
     
             player.setName("\"" + (String)m.group(4) + "\"");
             
             if (m.group(5).hashCode() != 0 ) {
             player.setClan("\"" + (String)m.group(5) + "\"");
             } else {
            	 player.setClan("");
             }
             player.setScore(-9999); 
             player.setDeaths(-9999);
             player.setKills(-9999);
             
             playerInfo.add(player);
        	 // testing
             //System.out.println(m.group(4) + " : " + m.group(5).hashCode());
         } // end while iterator over regexp matches.
         
        } // end else

        return playerInfo;


    } // end getPlayers
	
   static int PosInt(byte b) {
   int IntegerFromByte = (int) b;
    // to make the range 0 to 255 requires a simple shift of the negative numbers by adding 256
    int PositiveInteger = (IntegerFromByte < 0)
            ? IntegerFromByte + 256
            : IntegerFromByte;
    return PositiveInteger;
   }
}
/*
id (1 byte)
ping (2 bytes)
rate (2 bytes)
spacer (2 bytes)
name (string)
clantag (string)*/
