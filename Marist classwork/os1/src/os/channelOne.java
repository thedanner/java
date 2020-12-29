package os;

public class channelOne extends Channel
{
	public static final int VALUE = 1;
	
	public channelOne()
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
