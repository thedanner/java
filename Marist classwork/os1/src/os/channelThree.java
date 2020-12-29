package os;

public class channelThree extends Channel
{
	public static final int VALUE = 4;
	
	public channelThree()
	{
		super();
		
		maxTime = 2;
	}
	
	@Override
	public int value()
	{
		return VALUE;
	}
}
