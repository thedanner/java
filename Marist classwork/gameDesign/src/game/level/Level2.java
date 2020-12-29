package game.level;

import game.util.Trace;
import java.io.IOException;

/**
 * 
 */
public class Level2 extends Level1
{
    private final static String LEVEL_FILE = "LEVEL3.CSV";
    
    /**
     * Constructor
     */
    public Level2()
    {
        super();
    }

    /**
     * Initializes the level
     */
    public void init()
    {
        try
        {
            int[] world = readMapFromCSV(LEVEL_FILE);
            
            setCells(world);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * Update the level.
     */
    public void update()
    {
        super.update();
        
        if (caught)
        {
            return;
        }
        
		if (pc.collidesWith(obstacleLayer,  false))
        {
			Trace.print("collided with obstacle layer");
			caught = true;
		}
    }
}
