package compilerDesign.hw5.grammar;

public class Message
{
	private String message;
	private boolean verbose;
	
	public Message(String message, boolean verbose)
	{
		this.message = message;
		this.verbose = verbose;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public boolean isVerbose()
	{
		return verbose;
	}
	
	public void setVerbose(boolean verbose)
	{
		this.verbose = verbose;
	}
	
	@Override
	public String toString()
	{
		return "[" + verbose + "," + message + "]";
	}
}
