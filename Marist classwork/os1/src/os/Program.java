package os;

public class Program
{
	private String id;
	
	private int timeLimit;
	private int outputLimit;
	
	private int timeCounter;
	private int outputCounter;
	
	private StringBuilder output;
	
	public Program(String programID, int timeLimit, int lineLimit)
	{
		this.id = programID;
		
		this.timeLimit = timeLimit;
		this.outputLimit = lineLimit;
		
		this.timeCounter = 0;
		this.outputCounter = 0;
		
		this.output = new StringBuilder();
	}
	
	public String getID()
	{
		return id;
	}
	
	public boolean incrementTimeCounter()
	{
		timeCounter++;
		
		return (timeCounter <= timeLimit); 
	}
	
	public boolean incrementOutputCounter()
	{
		outputCounter++;
		
		return (outputCounter <= outputLimit);
	}
	
	public String getOutput()
	{
		return output.toString();
	}
	
	public void clear()
	{
		output.setLength(0);
	}
	
	public Program print(String d)
	{
		output.append(d);
		return this;
	}
	
	public Program print(int d)
	{
		output.append(d);
		return this;
	}
	
	public Program print(char d)
	{
		output.append(d);
		return this;
	}
	
	public Program print(double d)
	{
		output.append(d);
		return this;
	}
	
	public Program println()
	{
		output.append(Writer.NEWLINE);
		return this;
	}
	
	public Program println(String d)
	{
		output.append(d).append(Writer.NEWLINE);
		return this;
	}
	
	public Program println(int d)
	{
		output.append(d).append(Writer.NEWLINE);
		return this;
	}
	
	public Program println(char d)
	{
		output.append(d).append(Writer.NEWLINE);
		return this;
	}
	
	public Program println(double d)
	{
		output.append(d).append(Writer.NEWLINE);
		return this;
	}
}
