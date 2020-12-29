package os;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class represents a high-ish-level output manager for an operating
 * system.  It is capable of printing output data to a console and a file
 * on the platform on which it is being implemented (which must, in turn,
 * support Java).
 * 
 * @author Dan Mangiarelli
 * @version Sept 23, 2007
 */
public class Writer
{
	public static String NEWLINE = System.getProperty("line.separator");
	
	private String outputFilePath;
	
	private FileWriter fw;
	private PrintWriter pw;
	
	public Writer() throws IOException
	{
		this(null);
	}
	
	public Writer(String outputFilePath) throws IOException
	{
		this.outputFilePath = outputFilePath;
		
		fw = new FileWriter(outputFilePath);
		pw = new PrintWriter(fw);
	}
	
	public String getOutputFilePath()
	{
		return outputFilePath;
	}
	
	public void flush()
	{
		System.out.flush();
		pw.flush();
	}
	
	public void close()
	{
		pw.close();
	}
	
	public void setOutputFilePath(String outputFilePath)
	{
		this.outputFilePath = outputFilePath;
	}
	
	public Writer printf(String format, Object... args)
	{
		String result = String.format(format, args);
		
		System.out.print(result);
		pw.print(result);
		return this;
	}
	
	public Writer print(String d)
	{
		System.out.print(d);
		pw.print(d);
		return this;
	}
	
	public Writer print(int d)
	{
		System.out.print(d);
		pw.print(d);
		return this;
	}
	
	public Writer print(char d)
	{
		System.out.print(d);
		pw.print(d);
		return this;
	}
	
	public Writer print(double d)
	{
		System.out.print(d);
		pw.print(d);
		return this;
	}
	
	public Writer println()
	{
		System.out.println();
		pw.println();
		return this;
	}
	
	public Writer println(String d)
	{
		System.out.println(d);
		pw.println(d);
		return this;
	}
	
	public Writer println(int d)
	{
		System.out.println(d);
		pw.println(d);
		return this;
	}
	
	public Writer println(char d)
	{
		System.out.println(d);
		pw.println(d);
		return this;
	}
	
	public Writer println(double d)
	{
		System.out.println(d);
		pw.println(d);
		return this;
	}
}
