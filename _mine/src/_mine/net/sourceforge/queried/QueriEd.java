package _mine.net.sourceforge.queried;

import java.util.ArrayList;
import java.util.HashMap;

import _mine.net.sourceforge.queried.gametypes.AAServerInfo;
import _mine.net.sourceforge.queried.gametypes.BF1942ServerInfo;
import _mine.net.sourceforge.queried.gametypes.BF2142ServerInfo;
import _mine.net.sourceforge.queried.gametypes.BF2ServerInfo;
import _mine.net.sourceforge.queried.gametypes.BFVServerInfo;
import _mine.net.sourceforge.queried.gametypes.D3ServerInfo;
import _mine.net.sourceforge.queried.gametypes.ETServerInfo;
import _mine.net.sourceforge.queried.gametypes.HLServerInfo;
import _mine.net.sourceforge.queried.gametypes.NEXServerInfo;
import _mine.net.sourceforge.queried.gametypes.NWNServerInfo;
import _mine.net.sourceforge.queried.gametypes.Q4ServerInfo;
import _mine.net.sourceforge.queried.gametypes.SOF2Q3ServerInfo;
import _mine.net.sourceforge.queried.gametypes.SourceServerInfo;
import _mine.net.sourceforge.queried.gametypes.UT2ServerInfo;
import _mine.net.sourceforge.queried.gametypes.UTServerInfo;
import _mine.net.sourceforge.queried.gametypes.WSWServerInfo;

/**
 * Query a game server.<br/>
 * <br/>
 * Query a game server for server details and player information.<br/>
 * <br/>
 * Server types supported:
 * <ul>
 *   <li>Americas Army (AA)</li>
 *   <li>Battlefield 1942 (BF1942)</li>
 *   <li>Battlefield 2 (BF2)</li>
 *   <li>Battlefield Vietnam (BFV)</li>
 *   <li>Call Of Duty (COD)</li>
 *   <li>Doom 3 (D3)</li>
 *   <li>Enemy Territory (ET)</li>
 *   <li>Enemy Territory Quake Wars (ETQW)</li>
 *   <li>Halflife (HL)</li>
 *   <li>Halflife 2 (HL2) / Source</li>
 *   <li>Medal of Honor (MOH)</li>
 *   <li>Neverwinter Nights (NWN)</li>
 *   <li>Nexuiz (NEX)</li>
 *   <li>Quake 3 (Q3)</li>
 *   <li>Quake 4 (Q4)</li>
 *   <li>Unreal Tournament (UT)</li>
 *   <li>Unreal Tournament 2003 (UT2003|UT2K3)</li>
 *   <li>Unreal Tournament 2004 (UT2004|UT2K4)</li>
 *	 <li>Warsow (WSW)</li>
 * </ul>
 * 
 * @author DeadEd
 */
public class QueriEd {

    /**
     * Timeout used for the sockets 
     */
    public static final int TIMEOUT = 2000;

    /**
     * Server details 
     */
    public static final int INFO_DETAILS = 0;
    /**
     *  Player details 
     */
    public static final int INFO_PLAYERS = 1;
 
    /**
     * Any query type 
     */
    public static final int QUERY_ANY = 0;
    /**
     * Halflife query type 
     */
    public static final int QUERY_HALFLIFE = 1;
    /**
     * Gamespy query type 
     */
    public static final int QUERY_GAMESPY = 2;
    /**
     * Gamespy 2 query type 
     */
    public static final int QUERY_GAMESPY2 = 3;
    /**
     * Halflife 2 / Source query type 
     */
    public static final int QUERY_SOURCE = 4;
    /**
     * UnrealEngine2 query type 
     */
    public static final int QUERY_UT2S = 5;

    /**
     * Any game type 
     */
    public static final int GAME_ANY = 0;
    /**
     * Unreal Tournament game type 
     */
    public static final int GAME_UT = 1; 
    /**
     * Unreal Tournament 2003 game type 
     */
    public static final int GAME_UT2003 = 2; 
    /**
     * Unreal Tournament 2004 game type 
     */
    public static final int GAME_UT2004 = 3; 
    /**
     * Enemy Territory game type 
     */
    public static final int GAME_ET = 4; 
    /**
     * Halflife game type 
     */
    public static final int GAME_HL = 5; 
    /**
     * Quake 3 game type 
     */
    public static final int GAME_Q3 = 6; 
    /**
     * Battlefield 1942 game type 
     */
    public static final int GAME_BF1942 = 7; 
    /**
     * Battlefield Vietnam game type 
     */
    public static final int GAME_BFV = 8; 
    /**
     * Call Of Duty game type 
     */
    public static final int GAME_COD = 9;
    /**
     * Doom 3 game type 
     */
    public static final int GAME_D3 = 10;
    /**
     * Halflife 2 / Source game type 
     */
    public static final int GAME_HL2 = 11; 
    /**
     * Neverwinter Nights 
     */
    public static final int GAME_NWN = 12; 
    /**
     * Battlefield 2 game type 
     */
    public static final int GAME_BF2 = 13; 
    /**
     * Americas Army game type 
     */
    public static final int GAME_AA = 14; 
    /**
     * Nexuiz game type 
     */
    public static final int GAME_NEX = 15; 
    /**
     * Warsow game type 
     */
    public static final int GAME_WSW = 16;
    /**
     * Quake 4 game type 
     */
    public static final int GAME_Q4 = 17;
    /**
     * Battlefield 2142 game type 
     */
    public static final int GAME_BF2142 = 18;
    /**
     * Enemy Territory: Quake Wars game type 
     */
    public static final int GAME_ETQW = 19;
    /**
     * Call of Duty 4 game type 
     */
    public static final int GAME_COD4 = 20;
    
    
    private static HashMap<String, String> supportedGames = new HashMap<String, String>();
    static {
        supportedGames.put("AA", "Americas Army");
        supportedGames.put("BF", "Battlefield 1942");
        supportedGames.put("BF2", "Battlefield 2");
//        supportedGames.put("BF2142", "Battlefield 2142");
        supportedGames.put("BFV", "Battlefield Vietname");
        supportedGames.put("COD", "Call of Duty");
        supportedGames.put("COD2", "Call of Duty 2");
        supportedGames.put("COD4", "Call of Duty 4");
        supportedGames.put("D3", "Doom 3");
        supportedGames.put("ET", "Enemy Territory");
        supportedGames.put("ETQW", "Enemy Territory Quake Wars");
        supportedGames.put("HL", "Halflife");
        supportedGames.put("HL2", "Halflife 2");
        supportedGames.put("MOH", "Medal of Honor");
        supportedGames.put("NWN", "Never Winter Nights");
        supportedGames.put("NEX", "Nexuiz");
        supportedGames.put("Q3", "Quake 3");
        supportedGames.put("Q4", "Quake 4");
        supportedGames.put("UT", "Unreal Tournament");
        supportedGames.put("UT2003|UT2K3", "Unreal Tournament 2003");
        supportedGames.put("UT2004|UT2K4", "Unreal Tournament 2004");
        supportedGames.put("WSW", "Warsow");
    }

    /**
     * Returns a HashMap of the games that QueriEd supports.
     * The key is the game code.  The full game name is the value.
     * 
     * @return a HashMap of the support games
     */
    public static HashMap<String, String> getSupportedGames() {
        return supportedGames;
    }
    
    /**
     * Query a game server for server details.<br/>
     * <br/>
     * Ask for server details: name, ip, port, game, gameVersion, map, playerCount, maxPlayers. This
     * will use the specified local port to create the socket (useful if you need to configure your firewall)<br/>
     * <br/>
     * Valid game types:
     * <ul>
     *   <li>AA</b> - Americas Army</li>
     *   <li>BF</b> - Battlefield 1942</li>
     *   <li>BF2</b> - Battlefield 2</li>
     *   <li>BFV</b> - Battlefield Vietnam</li>
     *   <li>COD</b> - Call of Duty</li>
     *   <li>COD2</b> - Call of Duty 2</li>
     *   <li>COD4</b> - Call of Duty 4</li>
     *   <li>D3</b> - Doom 3</li>
     *   <li>ET</b> - Enemy Territory</li>
     *   <li>ETQW</b> - Enemy Territory Quake Wars</li>
     *   <li>HL</b> - Halflife</li>
     *   <li>HL2</b> - Halflife 2</li>
     *   <li>MOH</b> - Medal of Honor</li>
     *   <li>NWN</b> - Neverwinter Nights</li>
     *   <li>NEX</b> - Nexuiz</li>
     *   <li>Q3</b> - Quake 3</li>
     *   <li>Q4</b> - Quake 4</li>
     *   <li>UT</b> - Unreal Tournament</li>
     *   <li>UT2003|UT2K3</b> - Unreal Tournament 2003</li>
     *   <li>UT2004|UT2K4</b> - Unreal Tournament 2004</li>
     *   <li>WSW</b> - Warsow</li>
     * </ul>
     * <br/> 
     * Example: <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>ServerInfo serverInfo = QueriEd.serverQuery(27777, "HL", ip, port);</code>
     * 
     * @param localPort a port on the machine that the bot is running from that will be used to make the query
     * @param gameType one of the supported game types, defaults to Halflife
     * @param ipStr the ip (numerical or hostname) of the server
     * @param port the query port of the server
     * @return a ServerInfo object, or null if there was some problem wheil querying the server
     */
    public static ServerInfo serverQuery(int localPort, String gameType, String ipStr, int port) {
        int resolvedGameType = resolve(gameType);

        return serverQuery(localPort, resolvedGameType, ipStr, port, INFO_DETAILS);
    }
    
    /**
     * Query a game server for server details.<br/>
     * <br/>
     * Ask for server details: name, ip, port, game, gameVersion, map, playerCount, maxPlayers.  This
     * will try to find an open socket on the local machine.<br/>
     * <br/>
     * Valid game types:
     * <ul>
     *   <li>AA</b> - Americas Army</li>
     *   <li>BF</b> - Battlefield 1942</li>
     *   <li>BF2</b> - Battlefield 2</li>
     *   <li>BFV</b> - Battlefield Vietnam</li>
     *   <li>COD</b> - Call of Duty</li>
     *   <li>COD2</b> - Call of Duty 2</li>
     *   <li>COD4</b> - Call of Duty 4</li>
     *   <li>D3</b> - Doom 3</li>
     *   <li>ET</b> - Enemy Territory</li>
     *   <li>ETQW</b> - Enemy Territory Quake Wars</li>
     *   <li>HL</b> - Halflife</li>
     *   <li>HL2</b> - Halflife 2</li>
     *   <li>MOH</b> - Medal of Honor</li>
     *   <li>NWN</b> - Neverwinter Nights</li>
     *   <li>NEX</b> - Nexuiz</li>
     *   <li>Q3</b> - Quake 3</li>
     *   <li>Q4</b> - Quake 4</li>
     *   <li>UT</b> - Unreal Tournament</li>
     *   <li>UT2003|UT2K3</b> - Unreal Tournament 2003</li>
     *   <li>UT2004|UT2K4</b> - Unreal Tournament 2004</li>
     *   <li>WSW</b> - Warsow</li>
     * </ul>
     * <br/> 
     * Example: <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>ServerInfo serverInfo = QueriEd.serverQuery("HL", ip, port);</code>
     * 
     * @param gameType one of the supported game types, defaults to Halflife
     * @param ipStr the ip (numerical or hostname) of the server
     * @param port the query port of the server
     * @return a ServerInfo object, or null if there was some problem wheil querying the server
     */
    public static ServerInfo serverQuery(String gameType, String ipStr, int port) {
        return serverQuery(0, gameType, ipStr, port);
    }
    
    /**
     * Query a game server for player information.<br/>
     * <br/>
     * Ask for player details: name, kills, deaths, score, objectives completed.  This
     * will use the specified local port to create the socket (useful if you need to configure your firewall)<br/>
     * <br/>
     * Valid game types:
     * <ul>
     *   <li>AA</b> - Americas Army</li>
     *   <li>BF</b> - Battlefield 1942</li>
     *   <li>BF2</b> - Battlefield 2</li>
     *   <li>BFV</b> - Battlefield Vietnam</li>
     *   <li>COD</b> - Call of Duty</li>
     *   <li>COD2</b> - Call of Duty 2</li>
     *   <li>COD4</b> - Call of Duty 4</li>
     *   <li>D3</b> - Doom 3</li>
     *   <li>ET</b> - Enemy Territory</li>
     *   <li>ETQW</b> - Enemy Territory Quake Wars</li>
     *   <li>HL</b> - Halflife</li>
     *   <li>HL2</b> - Halflife 2</li>
     *   <li>MOH</b> - Medal of Honor</li>
     *   <li>NEX</b> - Nexuiz</li>
     *   <li>Q3</b> - Quake 3</li>
     *   <li>Q4</b> - Quake 4</li>
     *   <li>UT</b> - Unreal Tournament</li>
     *   <li>UT2003|UT2K3</b> - Unreal Tournament 2003</li>
     *   <li>UT2004|UT2K4</b> - Unreal Tournament 2004</li>
     *   <li>WSW</b> - Warsow</li>
     * </ul>
     * <br/> 
     * Example: <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>ArrayList playerInfo = QueriEd.playerQuery(27777, "HL", ip, port);</code>
     * 
     * @param localPort a port on the machine that the bot is running from that will be used to make the query
     * @param gameType one of the supported game types, defaults to Halflife
     * @param ipStr the ip (numerical or hostname) of the server
     * @param port the query port of the server
     * @return an ArrayList of PlayerInfo objects, the list will be empty if there aren't any players 
     * on the server
     */
    public static ArrayList<PlayerInfo> playerQuery(int localPort, String gameType, String ipStr, int port) {
        int resolvedGameType = resolve(gameType);

        return playerQuery(localPort, resolvedGameType, ipStr, port, INFO_PLAYERS);
    }

    /**
     * Query a game server for player information.<br/>
     * <br/>
     * Ask for player details: name, kills, deaths, score, objectives completed. This
     * will try to find an open socket on the local machine.<br/>
     * <br/>
     * Valid game types:
     * <ul>
     *   <li>AA</b> - Americas Army</li>
     *   <li>BF</b> - Battlefield 1942</li>
     *   <li>BF2</b> - Battlefield 2</li>
     *   <li>BFV</b> - Battlefield Vietnam</li>
     *   <li>COD</b> - Call of Duty</li>
     *   <li>COD2</b> - Call of Duty 2</li>
     *   <li>COD4</b> - Call of Duty 4</li>
     *   <li>D3</b> - Doom 3</li>
     *   <li>ET</b> - Enemy Territory</li>
     *   <li>ETQW</b> - Enemy Territory Quake Wars</li>
     *   <li>HL</b> - Halflife</li>
     *   <li>HL2</b> - Halflife 2</li>
     *   <li>MOH</b> - Medal of Honor</li>
     *   <li>NEX</b> - Nexuiz</li>
     *   <li>Q3</b> - Quake 3</li>
     *   <li>Q4</b> - Quake 4</li>
     *   <li>UT</b> - Unreal Tournament</li>
     *   <li>UT2003|UT2K3</b> - Unreal Tournament 2003</li>
     *   <li>UT2004|UT2K4</b> - Unreal Tournament 2004</li>
     *   <li>WSW</b> - Warsow</li>
     * </ul>
     * <br/> 
     * Example: <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>ArrayList playerInfo = QueriEd.playerQuery("HL", ip, port);</code>
     * 
     * @param gameType one of the supported game types, defaults to Halflife
     * @param ipStr the ip (numerical or hostname) of the server
     * @param port the query port of the server
     * @return an ArrayList of PlayerInfo objects, the list will be empty if there aren't any players 
     * on the server
     */
    public static ArrayList<PlayerInfo> playerQuery(String gameType, String ipStr, int port) {
        return playerQuery(0, gameType, ipStr, port);
    }
    
    private static int resolve(String gameType) {
        if(gameType.equalsIgnoreCase("UT") || gameType.equalsIgnoreCase("MOH")) {
            return GAME_UT;
        } else if(gameType.equalsIgnoreCase("UT2003") || gameType.equalsIgnoreCase("UT2K3")) {
            return GAME_UT2003;
        } else if(gameType.equalsIgnoreCase("UT2004") || gameType.equalsIgnoreCase("UT2K4")) {
            return GAME_UT2004;
        } else if(gameType.equalsIgnoreCase("ET")) {
            return GAME_ET;
        } else if(gameType.equalsIgnoreCase("ETQW")) {
            return GAME_ETQW;
        } else if(gameType.equalsIgnoreCase("Q3")) {
            return GAME_Q3;
        } else if(gameType.equalsIgnoreCase("HL")) {
            return GAME_HL;
        } else if(gameType.equalsIgnoreCase("HL2")) {
            return GAME_HL2;
        } else if(gameType.equalsIgnoreCase("BF")) {
            return GAME_BF1942;
        } else if(gameType.equalsIgnoreCase("BF2")) {
            return GAME_BF2;
        } else if(gameType.equalsIgnoreCase("BF2142")) {
            return GAME_BF2142;
        } else if(gameType.equalsIgnoreCase("BFV")) {
            return GAME_BFV;
        } else if(gameType.equalsIgnoreCase("COD") || gameType.equalsIgnoreCase("COD2") || gameType.equalsIgnoreCase("COD4")) {
            return GAME_COD;
        } else if(gameType.equalsIgnoreCase("D3")) {
            return GAME_D3;
        } else if(gameType.equalsIgnoreCase("NWN")) {
            return GAME_NWN;
        } else if(gameType.equalsIgnoreCase("AA")) {
            return GAME_AA;
        } else if(gameType.equalsIgnoreCase("NEX")) {
            return GAME_NEX;
        } else if(gameType.equalsIgnoreCase("WSW")) {
            return GAME_WSW;
        } else if(gameType.equalsIgnoreCase("Q4"))  {
        	return GAME_Q4;
        } else {
            return GAME_HL;
        }
    }
    
    private static ServerInfo serverQuery(int localPort, int gameType, String ipStr, int port, int infoType) {
        
        switch (gameType) {
            case GAME_UT:
                return UTServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_GAMESPY, GAME_UT);
            case GAME_UT2003:
                return UT2ServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_UT2S, GAME_UT2003);
            case GAME_UT2004:
                return UT2ServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_UT2S, GAME_UT2004);
            case GAME_HL:
                return HLServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_HALFLIFE, GAME_HL);
            case GAME_HL2:
                return SourceServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_SOURCE, GAME_HL2);
            case GAME_ET:
                return ETServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_ANY, GAME_ET);
            case GAME_Q3:
                return SOF2Q3ServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_ANY, GAME_Q3);
            case GAME_Q4:
                return Q4ServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_ANY, GAME_Q4);
            case GAME_COD:
                return SOF2Q3ServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_ANY, GAME_COD);
            case GAME_BF1942:
                return BF1942ServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_GAMESPY, GAME_BF1942);
            case GAME_BF2:
                return BF2ServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_GAMESPY2, GAME_BF2);
            case GAME_BF2142:
                return BF2142ServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_GAMESPY2, GAME_BF2142);
            case GAME_BFV:
                return BFVServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_GAMESPY2, GAME_BFV);
            case GAME_D3:
                return D3ServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_ANY, GAME_D3);
            case GAME_AA:
                return AAServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_GAMESPY2, GAME_AA);
            case GAME_NWN:
                return NWNServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_GAMESPY2, GAME_NWN);
            case GAME_NEX:
                return NEXServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_ANY, GAME_NEX);
            case GAME_WSW:
                return WSWServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_ANY, GAME_WSW);
            default:
                return HLServerInfo.getDetails(localPort, ipStr, port, infoType, QUERY_HALFLIFE, GAME_HL);
        }

    }

    private static ArrayList<PlayerInfo> playerQuery(int localPort, int gameType, String ipStr, int port, int infoType) {
        
        switch (gameType) {
            case GAME_UT :
                return UTServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_GAMESPY, GAME_UT);
            case GAME_UT2003 :
                return UT2ServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_UT2S, GAME_UT2003);
            case GAME_UT2004 :
                return UT2ServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_UT2S, GAME_UT2004);
            case GAME_HL :
                return HLServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_HALFLIFE, GAME_HL);
            case GAME_HL2 :
                return SourceServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_SOURCE, GAME_HL2);
            case GAME_ET :
                return ETServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_ANY, GAME_ET);
            case GAME_Q3 :
                return SOF2Q3ServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_ANY, GAME_Q3);
            case GAME_Q4 :
                return Q4ServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_ANY, GAME_Q4);
            case GAME_COD :
                return SOF2Q3ServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_ANY, GAME_COD);
            case GAME_BF1942:
                return BF1942ServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_GAMESPY, GAME_BF1942);
            case GAME_BF2:
                return BF2ServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_GAMESPY2, GAME_BF2);
            case GAME_BF2142:
                return BF2142ServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_GAMESPY2, GAME_BF2142);
            case GAME_BFV:
                return BFVServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_GAMESPY2, GAME_BFV);
            case GAME_D3:
                return D3ServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_ANY, GAME_D3);
            case GAME_AA:
                return AAServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_GAMESPY2, GAME_AA);
            case GAME_NEX :
                return NEXServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_ANY, GAME_NEX);
            case GAME_WSW :
                return WSWServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_ANY, GAME_WSW);
            case GAME_NWN:
                return null; // not supported
            default :
                return HLServerInfo.getPlayers(localPort, ipStr, port, infoType, QUERY_HALFLIFE, GAME_HL);
        }

    }

}
