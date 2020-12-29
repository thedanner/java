package _mine.net.sourceforge.queried;

/**
 * Server information that is returned by the server query.
 * 
 * @author DeadEd
 */
public class ServerInfo {

    private String name = "";
    private String ip = "";
    private String port = "";
    private String game = "";
    private String gameVersion = "";
    private String map = "";
    private String playerCount = "";
    private String maxPlayers = "";
    private String team1Tickets = "";
    private String team2Tickets = "";
    
    //private String pbEnabled = "";
    private String autoBalance = "";
    private String passworded = "";
    private String teamDamage = "";
    private String timeLimit = "";
    private String fragLimit = "";
    
    private String fullResponse = "";
    
    /**
     * Get the game type.
     * 
     * @return the game type.
     */
    public String getGame() {
        return game;
    }

    /**
     * Get the game version.
     * 
     * @return the game version.
     */
    public String getGameVersion() {
        return gameVersion;
    }

    /**
     * Get the server IP.
     * 
     * @return the server IP.
     */
    public String getIp() {
        return ip;
    }

    /**
     * Get the map currently being played on the server.
     * 
     * @return the map.
     */
    public String getMap() {
        return map;
    }

    /**
     * Get the maximum number of players allowed on the server.
     * 
     * @return max players.
     */
    public String getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Get the name of the server.
     * 
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get how many players are currently on the server.
     * 
     * @return the player count.
     */
    public String getPlayerCount() {
        return playerCount;
    }

    /**
     * Get the port the server is using.
     * 
     * @return the port.
     */
    public String getPort() {
        return port;
    }
    
    public int getPortInt()
    {
    	return Integer.parseInt(this.getPort());
    }

    public String getAutoBalance()	{
    	return autoBalance;
    }
    
    public String getPassworded()	{
    	return passworded;
    }

    public String getTeamDamage()	{
    	return teamDamage;
    }
    
    public String getTimeLimit()	{
    	return timeLimit;
    }
    
    public String getFragLimit()	{
    	return fragLimit;
    }
    
    public void setGame(String string) {
        game = string;
    }

    public void setGameVersion(String string) {
        gameVersion = string;
    }

    public void setIp(String string) {
        ip = string;
    }

    public void setMap(String string) {
        map = string;
    }

    public void setMaxPlayers(String string) {
        maxPlayers = string;
    }

    public void setName(String string) {
        name = string;
    }

    public void setPlayerCount(String string) {
        playerCount = string;
    }

    public void setPort(String string) {
        port = string;
    }

    public String getTeam1Tickets() {
        return team1Tickets;
    }

    public String getTeam2Tickets() {
        return team2Tickets;
    }

    public void setTeam1Tickets(String string) {
        team1Tickets = string;
    }

    public void setTeam2Tickets(String string) {
        team2Tickets = string;
    }

    public void setAutoBalance(String string)	{
    	autoBalance = string;
    }
    
    public void setPassworded(String string)	{
    	passworded = string;
    }

    public void setTeamDamage(String string)	{
    	teamDamage = string;
    }
    
    public void setTimeLimit(String string)	{
    	timeLimit = string;
    }
    
    public void setFragLimit(String string)	{
    	fragLimit = string;
    }

	public String getFullResponse() {
		return fullResponse;
	}

	public void setFullResponse(String fullResponse) {
		this.fullResponse = fullResponse;
	}

}
