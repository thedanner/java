package os;

public class Buffer 
{
	private String buffer;
	
	public Buffer()
	{
		buffer = "";
	}
	
	public Buffer(String in)
	{
		buffer = in;
	}
	
	public String getBuffer()
	{
		return buffer;
	}
	
	public void setBuffer(String bufferIn)
	{
		buffer = bufferIn;
	}
	
	public void clearBuffer()
	{
		buffer = "";
	}
	
	public String toString()
	{
		return buffer;
	}
}
