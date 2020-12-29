package game.entity;


import java.util.Vector;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

import game.level.AbstractLevel;
import game.util.Point;
import game.util.Trace;

/**
 * This class manages most of PlayerCharacter's functions.
 * 
 * @author Ron Coleman
 * 
 */
public class PlayerCharacter extends Entity
{
    /** Velocity in X and Y directions */
    private final static int VELOCITY = 4;
    
    /** Maximum velocity in gravity field */
    private final static int VELOCITY_TERMINAL = VELOCITY * 3;
    
    /** Instantaneous velocity if launched from a platform */
    private final static int VELOCITY_LAUNCH = 12;
    
    /** Istanteous velocity if falling from platform */
    private final static int VELOCITY_DROP = 4;
    
    /** Minimum (non-zero) velocity if ricocheting off platform */
    private final static double VELOCITY_RSIDUAL = 0.5;
    
    /** Energy conserved in ricochet */
    private final static double BOUNCE_FACTOR = 0.75;
    
    /** Force of gravity in pixels per game loop cycle */
    private final static double GRAVITY = 0.75;
    
    private final static int MAX_ROCKET_COUNT = 1;

    /** Rung step size */
    private final static int STEP = 4;
    
    /** Ball's x velocity component */
    private int vx = 0;
    
    /** Ball's y velocity component */
    private double vy = 0;
    
    /** Directional sprites */
    protected Sprite[] sprites = new Sprite[4];
    
    /** Bread crumbs left by PlayerCharacter */
    protected Vector crumbs = new Vector();
    
    /** Tracking flag to leave bread crumbs */
    protected boolean tracking = false;
    
    private boolean climbing;
    private boolean hasRockets;
    
    /** Constructor */
    public PlayerCharacter()
    {
        hasRockets = false;
        
        try
        {
            // Load in the sprites
            sprites[GO_UP] = new Sprite(Image
                    .createImage("/resources/images/CQ_dog_left.png"), 16,
                    16);
            
            sprites[GO_DOWN] = new Sprite(Image
                    .createImage("/resources/images/CQ_dog_left.png"), 16,
                    16);
            
            sprites[GO_LEFT] = new Sprite(Image
                    .createImage("/resources/images/CQ_dog_left.png"), 16,
                    16);
            
            sprites[GO_RIGHT] = new Sprite(Image
                    .createImage("/resources/images/CQ_dog_right.png"), 16,
                    16);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        pc = true;
    }
    
    /**
     * Updates PlayerCharacter
     * 
     * @param input
     *            Player input
     */
    public void update(int input)
    {
        // Updates the climbing state
        updateClimb(input);
        
        // Move normally only if we're not climbing
        if (climbing == false)
        {
            moveX(input);
            
            moveY(input);
        }
        
        updateRocketFired(input);
    }
    
    /**
     * Updates climb
     * 
     * @param input
     *            Input
     */
    protected void updateClimb(int input)
    {
        // Climb if we're on ladda
        if (goUpDown(input) && onLadda())
        {
            climb(input);
        }
        // Climb inertially
        else if (climbing && (getY() % AbstractLevel.TILE_HEIGHT) != 0)
        {
            if (dir == GO_UP)
            {
                move(0, -STEP);
            }
            else if (dir == GO_DOWN && !onPlatform())
            {
                move(0, STEP);
            }
        }
        // Get off ladd and stop climbing player goes <- or ->
        else if (goLeftRight(input))
        {
            climbing = false;
        }
    }
    
    private void updateRocketFired(int input)
    {
        // Allow either FIRE or DOWN to fire the rocket.
        if (    ((input & GameCanvas.FIRE_PRESSED) != 0) ||
                ((input & GameCanvas.DOWN_PRESSED) != 0) )
        {
            fireRocket();
        }
    }
    
    /**
     * Returns true if player presses UP or DOWN
     * 
     * @param input
     *            Input
     * @return True or false
     */
    protected boolean goUpDown(int input)
    {
        return  (input & GameCanvas.UP_PRESSED) != 0 ||
                (input & GameCanvas.DOWN_PRESSED) != 0;
    }
    
    /**
     * Returns true if player presses LEFT or RIGHT
     * 
     * @param input
     *            Input
     * @return True or false
     */
    protected boolean goLeftRight(int input)
    {
        return (input & GameCanvas.LEFT_PRESSED) != 0
                || (input & GameCanvas.RIGHT_PRESSED) != 0;
    }
    
    /**
     * Returns true if player on ladda
     * 
     * @return True or false
     */
    protected boolean onLadda()
    {
        int x = getX();
        int y = getY() + AbstractLevel.TILE_HEIGHT;
        
        Vector rungs = level.getRungs();
        
        for (int j = 0; j < rungs.size(); j++)
        {
            Point pt = (Point) rungs.elementAt(j);
            
            if (x == pt.getX() && y == pt.getY())
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Moves up or down the ladda
     * 
     * @param input
     *            Input
     */
    protected void climb(int input)
    {
        if ((input & GameCanvas.UP_PRESSED) != 0)
        {
            move(0, -STEP);
            setDirection(GO_UP);
        }
        else if ((input & GameCanvas.DOWN_PRESSED) != 0 && !onPlatform())
        {
            move(0, STEP);
            setDirection(GO_DOWN);
        }
        
        climbing = true;
    }
    
    /**
     * Moves the pc in the x direction. Note the pc may move under own
     * inertia.
     * 
     * @param input
     *            Player's input
     */
    protected void moveX(int input)
    {
        int vxLast = vx;
        
        if ((input & GameCanvas.LEFT_PRESSED) != 0)
        {
            vx = -PlayerCharacter.VELOCITY;
            setLeft();
        }
        
        else if ((input & GameCanvas.RIGHT_PRESSED) != 0)
        {
            vx = PlayerCharacter.VELOCITY;
            setRight();
        }
        else
        {
            vx = 0;
        }
        
        boolean onTile = (((getX() % AbstractLevel.TILE_WIDTH)) == 0);
        
        if (vx == 0 && !onTile)
        {
            vx = vxLast;
        }
        
        move(vx, 0);
        
        if (vx != 0 && collidesWith(collisionLayer))
        {
            move(-vx, 0);
        }
        
        else if (vx > 0)
        {
            sprites[dir].nextFrame();
        }
        
        else if (vx < 0)
        {
            sprites[dir].prevFrame();
        }
    }
    
    /**
     * Moves the pc in the y direction.
     * 
     * @param input
     *            Player input
     */
    private void moveY(int input)
    {
        // Check if player "jumping" // Added the && on this platform to fix jumping bug
        if (vy == 0 && (input & GameCanvas.UP_PRESSED) != 0 && this.onPlatform())
        {
            vy = -VELOCITY_LAUNCH;
            // Trace.print("set up "+inputno++);
        }
        
        // Check if ball falls from a platform
        else if (vy == 0 && !onPlatform())
        {
            vy = VELOCITY_DROP;
            // Trace.print("set down "+inputno++);
        }
        
        // If ball is in motion, update the velocity per gravity
        if (vy != 0)
        {
            vy += GRAVITY;
            
            if (vy >= VELOCITY_TERMINAL)
                vy = VELOCITY_TERMINAL;
            
            move(0, (int) vy);
            
            // If we collide with the platform layer, cancel motion
            if (collidesWith(collisionLayer))
            {
                move(0, -(int) vy);
                
                moveToContact();
                
                // If we collide going down, stop movement
                if (vy > 0)
                {
                    vy = 0;
                    // moveToContact();
                }
                
                // If we collide going up, bounce off platform
                else
                {
                    // "-" means reverse direction
                    // "bounce factor" means platform absorbs some energy
                    vy = -BOUNCE_FACTOR * vy;
                    
                    // By chance velocity may be zero...which could be a problem
                    if (vy == 0)
                    {
                        vy = -VELOCITY_RSIDUAL;
                    }
                }
            }
        }
    }
    
    /**
     * Moves PlayerCharacter relative
     * 
     * @param x
     *            X amount
     * @param y
     *            Y amount
     */
    public void move(int x, int y)
    {
        if (x == 0 && y == 0)
        {
            return;
        }
        
        sprites[dir].move(x, y);
    }
    
    /**
     * Gets the current directional sprite.
     * 
     * @return
     */
    public Sprite getSprite()
    {
        return sprites[dir];
    }
    
    /**
     * Sets the position.
     * 
     * @param x
     *            X coordinate
     * @param y
     *            Y coordinate
     */
    public void setPosition(int x, int y)
    {
        // sprites[dir].setPosition(x - GameScreen.TILE_W, y -
        // GameScreen.TILE_H);
        sprites[dir].setPosition(x, y);
    }
    
    /**
     * Gets X position
     * 
     * @return X position
     */
    public int getX()
    {
        // return sprites[dir].getX() + GameScreen.TILE_W;
        return sprites[dir].getX();
    }
    
    /**
     * Gets Y position
     * 
     * @return Y position
     */
    public int getY()
    {
        // return sprites[dir].getY() + GameScreen.TILE_H;
        return sprites[dir].getY();
    }
    
    /**
     * Sets pc's direction.
     * 
     * @param newdir
     *            Direction
     */
    public void setDirection(int newdir)
    {
        int oldX = sprites[dir].getX();
        
        int oldY = sprites[dir].getY();
        
        sprites[dir].setVisible(false);
        
        sprites[newdir].setPosition(oldX, oldY);
        
        sprites[newdir].setVisible(true);
        
        dir = newdir;
    }
    
    /** Advances pc's frame */
    public void nextFrame()
    {
        sprites[dir].nextFrame();
    }
    
    public boolean collidesWith(Entity agent)
    {
        if (agent instanceof Rocket)
        {
            return false;
        }

        return collidesWith(agent.getSprite());
    }
    
    /**
     * Tests if pc is colliding.
     * 
     * @param sprite
     *            Other sprite
     * @return True if colliding, false otherwise
     */
    public boolean collidesWith(Sprite sprite)
    {
        return sprites[dir].collidesWith(sprite, true);
    }
    
    /**
     * Tests if pc is colliding
     * 
     * @param tiledLayer
     *            Tiled layer
     * @return True if colliding, false otherwise
     */
    public boolean collidesWith(TiledLayer tiledLayer)
    {
        return sprites[dir].collidesWith(tiledLayer, true);
    }
    
    /**
     * Tests if pc is colliding -- use with reward layers only!
     * 
     * @param tiledLayer
     *            Tiled layer
     * @param pixel
     *            True uses pixel-level collision detection.
     * @return True if colliding, false otherwise
     */
    public boolean collidesWith(TiledLayer tiledLayer, boolean pixel)
    {
        if (pixel)
        {
            return sprites[dir].collidesWith(tiledLayer, true);
        }
        
        int x = sprites[dir].getX();
        int y = sprites[dir].getY();
        
        // TODO: Do we want to get the center of the tile here?
        x += AbstractLevel.TILE_WIDTH / 2;
        y += AbstractLevel.TILE_HEIGHT / 2;
        
        int tilex = x / AbstractLevel.TILE_WIDTH;
        int tiley = y / AbstractLevel.TILE_HEIGHT;
        
        if (tilex < 0 || tiley < 0)
        {
            return false;
        }
        
        int tile = tiledLayer.getCell(tilex , tiley);
        if (tile != 0)
        {
            return true; // no srsly return true
        }
        
        return false;
    }
    
    /**
     * Gets the array of sprites.
     * 
     * @return Sprites.
     */
    public Sprite[] getSprites()
    {
        return sprites;
    }
    
    /**
     * Tests if pc is on a platform
     * 
     * @return True if on platform
     */
    protected boolean onPlatform()
    {
        // Probe for platform below
        move(0, 1);
        
        boolean upheld = sprites[dir].collidesWith(collisionLayer, true);
        
        // Undoo the probe
        move(0, -1);
        
        // If there's something below, then we're on a platform
        if (upheld)
        {
            return true;
        }
        
        return false;
    }
    
    /**
     * Moves to the contact point. This prevents the pc from being left in
     * the air because it is moving too fast to land exactly on a platform.
     * 
     */
    protected void moveToContact()
    {
        int creep = 1;
        
        if (vy < 0)
        {
            creep = -1;
        }
        
        while (sprites[dir].collidesWith(collisionLayer, true) == false)
        {
            move(0, creep);
        }
        
        move(0, -creep);
    }
    
    /** Resets pc */
    public void reset()
    {
        for (int j = 0; j < sprites.length; j++)
        {
            sprites[j].setVisible(false);
        }
        
        dir = GO_RIGHT;
        
        sprites[dir].setVisible(true);
        
        // Ask level where we are and go there
        Point pt = level.getPCPosition();
        
        setPosition(pt.getX(), pt.getY());
        Trace.print("grace reset " + pt.getX() + " " + pt.getY());
        
        crumbs.removeAllElements();
        
        tracking = false;
    }
    
    /**
     * Starts dropping crumbs
     * 
     */
    public void startCrumbs()
    {
        tracking = true;
        
        crumbs.addElement(new Point(getX(), getY()));
        
        track();
    }
    
    /**
     * Tracks Graces movement.
     * 
     */
    protected void track()
    {
        crumbs.addElement(new Point(getX(), getY()));
    }
    
    /**
     * Gets crumbs left by PlayerCharacter.
     * 
     * @return Crumbs
     */
    public Vector getCrumbs()
    {
        return crumbs;
    }
    
    /**
     * Gets PlayerCharacter's veritical speed.
     * 
     * @return
     */
    public double getVelocityY()
    {
        return vy;
    }

    public int getRocketsFired()
    {
        return level.getRockets().size();
    }
    
    public void pickUpRocket()
    {
        hasRockets = true;
    }
    
    public boolean hasRockets()
    {
        return hasRockets;
    }
    
    public void fireRocket()
    {
        if (this.hasRockets() && this.getRocketsFired() < MAX_ROCKET_COUNT)
        {
            int x = this.getX();
            int y = this.getY();
            
            if (GO_LEFT == dir)
            {
                x -= this.getSprite().getWidth();
            }
            else if (GO_RIGHT == dir)
            {
                x += this.getSprite().getWidth();
            }
            
            Trace.print("Firing rocket: " + x + ", " + y);
            
            level.addRocket(x, y, this.getDirection());
        }
        else
        {
            Trace.print("cannot fire rocket");
        }
    }
}
