/**
 * 
 */
package os;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Craig
 *
 */
public class Reader
{
	private String sourceFile;
	
	private BufferedReader in;
	
	private Program currentProgram;
	
	private String buffer;
	
	public Reader() throws IOException
	{
		this(null);
	}
	
	public Reader(String sourceFile) throws IOException
	{
		this.sourceFile = sourceFile;
		
		this.currentProgram = null;
		
		this.buffer = null;
		
		in = new BufferedReader(new FileReader(sourceFile));
	}
	
	public void processNextLine()
	{
		try
		{
			buffer = in.readLine();
			
			if(buffer != null)
				processLine(buffer);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setSourceFile(String sourceFile)
	{
		this.sourceFile = sourceFile;
	}
	
	public String getSourceFile()
	{
		return sourceFile;
	}
	
	public Program getCurrentProgram()
	{
		return currentProgram;
	}
	
	private void processLine(String line)
	{
		line = String.format("%1$-40s", line);
		
		buffer = line;
		
		if (line.startsWith("$JOB"))
		{
			createNewProgram(line);
		}
		else if (line.startsWith("$EOJ"))
		{
			endCurrentProgram(line);
		}
		else if (currentProgram == null)
		{

			throw new ProgramFormatException(
					"program is malformed (expecting a $JOB card)");
		}
	}
	
	private void createNewProgram(String line)
	{
		if(currentProgram == null)
		{
			String jobIDStr = line.substring(4, 8);
			String timeLimitStr = line.substring(8, 12);
			String outputLineLimitStr = line.substring(12, 16);
			
			int timeLimit = Integer.parseInt(timeLimitStr);
			int outputLineLimit = Integer.parseInt(outputLineLimitStr);
			
			currentProgram = new Program(jobIDStr, timeLimit, outputLineLimit);
		}
		else
			throw new ProgramFormatException("wasn't expecting a $JOB card.");
	}
	
	private void endCurrentProgram(String line)
	{
		if(currentProgram != null)
		{
			currentProgram = null;
		}
		else
		{
			throw new ProgramFormatException("wasn't expecting an $EOJ card.");
		}
	}
	
	public String getBuffer()
	{
		return buffer;
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		
		if(in != null)
			in.close();
	}
}
