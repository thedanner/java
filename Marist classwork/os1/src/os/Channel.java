package os;

public abstract class Channel
{
	private static final int DEFAULT_MAXIMUM_TIME = 3;
	
	protected int timer;
	protected int maxTime;
	protected boolean busy;
	
	public Channel()
	{
		timer = 0;
		maxTime = DEFAULT_MAXIMUM_TIME;
		busy = false;
	}
	
	public void setBusy(boolean newBusy)
	{
		busy = newBusy;
	}
	
	public boolean isBusy()
	{
		return busy;
	}
	
	public void incrementTimer()
	{
		incrementTimer(1);
	}
	
	public void incrementTimer(int value)
	{
		timer += value;
	}
	
	public void resetTimer()
	{
		timer = 0;
	}
	
	public int getTimer()
	{
		return timer;
	}
	
	public int getMaxTime()
	{
		return maxTime;
	}
	
	public abstract int value();
}
