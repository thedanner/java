package game.entity;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

import game.level.AbstractLevel;
import game.level.Rid;

/**
 * This class is the base class for agents like Grace, monsta, etc.
 * 
 * @author Ron
 * 
 */
public abstract class Entity
{
    protected static int code = 0;
    
    /** Heading is up. */
    public final static int GO_UP = code++;
    
    /** Heading is left. */
    public final static int GO_LEFT = code++;
    
    /** Heading is right. */
    public final static int GO_RIGHT = code++;
    
    /** Heading is down. */
    public final static int GO_DOWN = code++;
    
    /** Spider magic number */
    public final static int RID_SPIDA = 15;
    
    /** Vertical travelling ghost magic number */
    public final static int RID_VGHOST = 16;
    
    /** Horizontal travelling ghost magic number */
    public final static int RID_HGHOST = 17;
    
    /** Down travelling monsta magic number */
    public final static int RID_MONSTA_DOWN = 14;
    
    /** Right travelling monsta magic number */
    public final static int RID_MONSTA_RIGHT = 20;
    
    /** Left travelling monsta magic number */
    public final static int RID_MONSTA_LEFT = 21;
    
    /** Up travelling monsta magic number */
    public final static int RID_MONSTA_UP = 22;
    
    /** Default heading */
    protected int dir = GO_DOWN;
    
    /** Agent is PC if trues */
    protected boolean pc = false;
    
    /** Agent is on this levels */
    protected AbstractLevel level;
    
    protected TiledLayer collisionLayer;
    
    /** Gets sprite for this agent. */
    abstract public Sprite getSprite();
    
    abstract public void setPosition(int x, int y);
    
    /**
     * Moves agent relatively.
     * 
     * @param dx
     *            X increment
     * @param dy
     *            Y increment
     */
    abstract public void move(int dx, int dy);
    
    /**
     * Gets X position
     * 
     * @return X position
     */
    abstract public int getX();
    
    /**
     * Gets Y position
     * 
     * @return Y position
     */
    abstract public int getY();
    
    /**
     * Sets direction heading.
     * 
     * @param dir
     *            Heading
     */
    abstract public void setDirection(int dir);
    
    /**
     * Tests if entity is colliding.
     * 
     * @param sprite
     *            Other sprite
     * @return True if colliding, false otherwise
     */
    abstract public boolean collidesWith(Sprite sprite);
    
    /**
     * Tests if entity is colliding
     * 
     * @param tiledLayer
     *            Tiled layer
     * @return True if colliding, false otherwise
     */
    abstract public boolean collidesWith(TiledLayer tiledLayer);
    
    /** Constructor */
    protected Entity()
    {
        pc = false;
    }
    
    /**
     * Sets the level this agent is on.
     * 
     * @param level
     *            Level
     */
    public void setLevel(AbstractLevel level)
    {
        this.level = level;
        this.collisionLayer = level.getCollisionLayer();
    }
    
    /**
     * Tests if this agent is the PC.
     * 
     * @return True if agent is PC.
     */
    public boolean isPC()
    {
        return pc;
    }
    
    /**
     * Tests if the rid is the PC.
     * 
     * @param rid
     *            RID
     * @return True if the rid is the PC.
     */
    public static boolean isPC(int rid)
    {
        return rid == Rid.RID_PC;
    }
    
    /** Sets grace's direction left. */
    public void setLeft()
    {
        setDirection(GO_LEFT);
    }
    
    /** Sets grace's direction right. */
    public void setRight()
    {
        setDirection(GO_RIGHT);
    }
    
    /** Sets grace's direction up. */
    public void setUp()
    {
        setDirection(GO_UP);
    }
    
    /** Set grace's direction down. */
    public void setDown()
    {
        setDirection(GO_DOWN);
    }
    
    public int getDirection()
    {
        return dir;
    }
}
