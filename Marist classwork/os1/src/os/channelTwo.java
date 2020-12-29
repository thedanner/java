package os;

public class channelTwo extends Channel
{
	public static final int VALUE = 2;
	
	public channelTwo()
	{
		super();
		
		maxTime = 5;
	}
	
	@Override
	public int value()
	{
		return VALUE;
	}
}
