package game.entity;

/**
 * Implements the monsta can only be killed by jumping on it.
 * 
 * @author Ron Coleman
 * 
 */
public class Monster extends Enemy
{
    /** Going right frame in sprite */
    protected final static int FRAME_RIGHT = 0;
    
    /** Going left frame in sprite */
    protected final static int FRAME_LEFT = 1;
    
    /** Crushed */
    protected final static int FRAME_CRUSHED = 4;
    
    /** Speed: slightly faster than PC */
    protected static final int SPEED_MONSTER = 4;
    
    /** Wake for dying monster */
    protected static final long WAKE = 2000;
    
    private boolean firstTime;
    
    /** Time stamp enemy died */
    private long stamp;
    
    private int face;
    
    /**
     * Constructor
     * 
     * @param x
     *            Initial x position in world coordinates
     * @param y
     *            Initial y position in world coordinates
     */
    public Monster(int x, int y)
    {
        super("/resources/images/CQ_Laser_Penguin.png");
        
        firstTime = true;

        // Randomly choose the direction
        if (toss == 0)
        {
            face = FRAME_RIGHT;
        }
        else
        {
            face = FRAME_LEFT;
        }
        
        sprite.setFrame(face);
        
        // Set the position
        sprite.setPosition(x, y);
        
        // Set the initial speed
        xSpeed = SPEED_MONSTER;
        
        // If going left reverse x speed
        if (toss != 0)
        {
            xSpeed *= -1;
        }
        
        // Only travels horizontally
        ySpeed = 0;
    }
    
    /**
     * Updates monsta and overrides super class.
     */
    public void update()
    {
        // If monster passed, there's nothing to do
        if (state == DEAD)
        {
            return;
        }
        
        // Otherwise, update the enemy if alive
        if (state == ALIVE)
        {
            super.update();
        }
        
        // Update state of dying enemy
        else if (state == DYING && firstTime)
        {
            // Switch to dying sprite
            sprite.setFrame(FRAME_CRUSHED);
            
            firstTime = false;
            
            stamp = System.currentTimeMillis();
        }
        else if (state == DYING && !firstTime)
        {
            long now = System.currentTimeMillis();
            
            if ((now - stamp) > WAKE)
            {
                state = DEAD;
                
                sprite.setVisible(false);
            }
        }
    }
    
    /**
     * Handles a collision event
     */
    public void collideEvent()
    {
        xSpeed *= -1;
        
        face = (face + 1) % 2;
        
        sprite.setFrame(face);
    }
}
