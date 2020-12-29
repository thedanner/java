package game.level;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.Manager;
import javax.microedition.media.Player;

import game.Game;

/**
 * This class manages level 0.
 * 
 * @author Ron Coleman
 * 
 */
public class Level0 extends AbstractLevel
{
    private final static String LEVEL_FILE = "LEVEL1.CSV";

    private Player enemySound;
    private Player baconSound;
    
    /**
     * Constructor
     */
    public Level0()
    {
        super();

        loadSounds();
    }
    
    /**
     * Initializes the level.
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
    
    protected void deployEnemies()
    {
        // There are no enemies or obstacles on this level
    }
    
    /**
     * Updates the objects on this level.
     */
    public void update()
    {
        // If Grace collides with a reward, handle it.
        if (pc.collidesWith(rewardLayer, false))
        {
            // Trace.print("collsion detected");
            handlePCRewardCollision();
        }
        // If Grace collides with the goal, the level is complete
        else if (pc.collidesWith(goal))
        {
            complete = true;
        }
    }
    
    protected void handlePCRewardCollision()
    {
        // Find the reward and remove it from level
        int tileX = pc.getX() / AbstractLevel.TILE_WIDTH;
        int tileY = pc.getY() / AbstractLevel.TILE_HEIGHT;
        
        int tile = getCell(worldMap, tileX, tileY);
//        if (tile == 0)
//        {
//            tileX += 1;
//            tile = getCell(worldMap, tileX, tileY);
//        }
        
        if (isBacon(tile))
        {
            baconCount--;
            
            Game.addScore(Game.POINTS_BACON);
            
            makeSound(Rid.RID_BACON);
        }
        if (isRocket(tile))
        {
            pc.pickUpRocket();
        }
        
        rewardLayer.setCell(tileX, tileY, Rid.RID_NO_TILE);
        
        setCell(worldMap, tileX, tileY, Rid.RID_NO_TILE);
    }
    
    protected void makeSound(int rid)
    {
        if (!Game.isSoundEnabled())
        {
            return;
        }
        
        try
        {
            if (rid == Rid.RID_BACON)
            {
                baconSound.start();
            }
            else if (rid == Rid.RID_ENEMY)
            {
                enemySound.start();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /** Load sounds */
    protected void loadSounds()
    {
        try
        {
            // Source: http://frogstar.com/wav/effects.asp ??
            InputStream is = getClass().getResourceAsStream(
                    "/resources/audio/bonus.wav");
            enemySound = Manager.createPlayer(is, "audio/X-wav");
            enemySound.prefetch();
            
            is = getClass().getResourceAsStream("/resources/audio/nomnomnom.wav");
            baconSound = Manager.createPlayer(is, "audio/X-wav");
            baconSound.prefetch();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // To be implemented in L3
    public void addRocket(int x, int y, int dir) {}
}
