package os;

/**
 * @author Craigger
 *
 */
public class Drum
{
	private final String[][][] drum;
	private boolean[] trackUsed;
	
	public Drum()
	{
		drum = new String[100][10][4];
		trackUsed = new boolean[100];	
		
		for(int i=0; i < trackUsed.length; i++)
			trackUsed[i] = false;
	}
	
	public boolean isTrackUsed(int trackNumber)
	{
		return trackUsed[trackNumber];
	}

	public void setTrackUsed(int trackNumber, boolean isTrackUsed) 
	{
		trackUsed[trackNumber] = isTrackUsed;
	}
	
	public int findEmptyTrack()
	{
		int ret = -1;
		
		for(int i = 0; (i < trackUsed.length && ret < 0); i++)
		{			
			if (trackUsed[i] == false)
			{
				ret = i;
			}
		}
		
		return ret;	
	}
	
	public boolean hasEmptyTrack()
	{
		return (findEmptyTrack() >= 0);
	}
	
	public String[][] returnTrack(int trackNum)
	{
		String[][] temp = new String[10][4];
		
		for(int i = 0; i < 10; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				temp[i][j] = drum[trackNum][i][j];
			}
		}
		
		return temp;
	}
	
	public void deleteTrack(int trackNum)
	{
		trackUsed[trackNum] = false;
		
		// for debugging
		drum[trackNum] = new String[10][4];
	}
	
	public void writeTrack(int trackNum, String array[][])
	{
		for(int i = 0; i < 10; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				if(array[i][j] != null)
					drum[trackNum][i][j] = array[i][j];
				
				else
					drum[trackNum][i][j] = " ";				
			}
		}
		
		trackUsed[trackNum] = true;
	}
}
