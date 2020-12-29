package game.entity;

import game.util.Rand;
import java.util.Random;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

/**
 * This is the base class for enemies.
 * 
 * @author Ron Coleman
 * 
 */
abstract public class Enemy extends Entity
{
    public final static int ALIVE = 0;
    public final static int DYING = 1;
    public final static int DEAD = 2;
    
    protected Sprite sprite;
    protected int toss;
    protected Random ran;
    protected int xSpeed;
    protected int ySpeed;
    protected int state;
    
    /**
     * Must be implemented by enemy subclasses.
     * 
     */
    public abstract void collideEvent();
    
    /** Constructor */
    public Enemy()
    {
        ran = Rand.getInstance();

        // Toss the coin
        toss = ran.nextInt(2);
        
        state = ALIVE;
    }
    
    /**
     * Constructor
     * 
     * @param path
     *            File path of image
     */
    public Enemy(String path)
    {
        this();
        
        try
        {
            Image img = Image.createImage(path);
            sprite = new Sprite(img, 16, 16);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Sets the enemy position -- assumes sprite has been initialized.
     * 
     * @param x
     *            X
     * @param y
     *            Y
     */
    public void setPosition(int x, int y)
    {
        if (sprite == null)
        {
            return;
        }
        
        sprite.setPosition(x, y);
    }
    
    /** Gets the sprite for this agent. */
    public Sprite getSprite()
    {
        return sprite;
    }
    
    /**
     * Updates the enemy which means move on condition defined by the subclass.
     * 
     * @see mg.util.agent.Entity
     */
    public void update()
    {
        moveOnCondition();
    }
    
    /** Moves enemy conditionally */
    protected void moveOnCondition()
    {
        move(xSpeed, ySpeed);
        
        // Ask level if we collided...
        if (level.collide(this))
        {
            // If so move back and tell agent we collided
            move(-xSpeed, -ySpeed);
            collideEvent();
        }
        
        return;
    }
    
    /**
     * Tests if enemy collides with a sprite
     * 
     * @param sprite
     *            Sprite
     */
    public boolean collidesWith(Sprite sprite)
    {
        return this.sprite.collidesWith(sprite, true);
    }
    
    /**
     * Tests if enemy collides with a tiled layer.
     * 
     * @param tiledLayer
     *            Tiled layer
     */
    public boolean collidesWith(TiledLayer tiledLayer)
    {
        return false;
    }
    
    /**
     * Gets X position.
     * 
     * @return X position
     */
    public int getX()
    {
        if (sprite == null)
        {
            return -1;
        }
        
        return sprite.getX();
    }
    
    /**
     * Gets Y position
     * 
     * @return Y position
     */
    public int getY()
    {
        if (sprite == null)
        {
            return -1;
        }
        
        return sprite.getY();
    }
    
    /**
     * Moves sprite relatively.
     * 
     * @param dx
     *            X amount
     * @param dy
     *            Y amount
     */
    public void move(int dx, int dy)
    {
        if (sprite == null)
        {
            return;
        }
        
        sprite.move(dx, dy);
    }
    
    /**
     * Sets direction
     * 
     * @param dir
     *            Direction
     */
    public void setDirection(int dir)
    {
        // Does nothing because enemies move under "AI".
    }
    
    /**
     * Sets enemy to dead state.
     * 
     */
    public void setDead()
    {
        state = DEAD;
    }
    
    /**
     * Sets enemy to dying state.
     * 
     */
    public void setDying()
    {
        state = DYING;
    }
    
    /**
     * Return true if the enemy is alive.
     * 
     * @return True or false
     */
    public boolean isAlive()
    {
        return state == ALIVE;
    }
    
    /**
     * Returns true if enemy is dead.
     * 
     * @return True or false
     */
    public boolean isDead()
    {
        return state == DEAD;
    }
}
