package game.entity;

import game.Game;
import java.io.IOException;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import game.util.Trace;
import javax.microedition.media.MediaException;

/**
 * 
 */
public class Rocket extends Enemy
{
    /** Speed: a bit faster than Monster */
    protected static final int SPEED_ROCKET = 6;

    protected static final int MAX_TRAVEL_DISTANCE = 200;

    protected static final int[] FRAME_SEQUENCE = { 0, 1, 2, 1 };

    protected int startX;
    
    public Rocket(int initialX, int initialY, int dir)
    {
        super();
        
        startX = initialX;
        
        setDirection(dir);
        setPosition(initialX, initialY);
        
        this.playLaunchSound();
    }
    
    private void playLaunchSound()
    {
        try
        {
            if (Game.isSoundEnabled())
            {
                Game.playRocketLaunch();
            }
        }
        catch (MediaException ex)
        {
            ex.printStackTrace();
        }
    }

    public Sprite getSprite()
    {
        return sprite;
    }
    
    public void setPosition(int x, int y)
    {
         sprite.setPosition(x, y);
    }
    
    public void move(int dx, int dy)
    {
        sprite.move(dx, dy);
    }
    
    public int getX()
    {
        return sprite.getX();
    }
    
    public int getY()
    {
        return sprite.getY();
    }
    
    public void setDirection(int newdir)
    {
        dir = newdir;
        
        try
        {
            if (Entity.GO_LEFT == dir)
            {
                xSpeed = -SPEED_ROCKET;
                
                Image image = Image.createImage(
                        "/resources/images/CQ_rocket_left.png");
                sprite = new Sprite(image, 16, 16);
            }
            else if (Entity.GO_RIGHT == dir)
            {
                xSpeed = SPEED_ROCKET;
                
                Image image = Image.createImage(
                        "/resources/images/CQ_rocket_right.png");
                sprite = new Sprite(image, 16, 16);
            }
            
            sprite.setFrameSequence(FRAME_SEQUENCE);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        ySpeed = 0;
    }
    
    public void update()
    {
        if (state == DEAD)
        {
            return;
        }
        
        // Otherwise, update the rocket if alive
        if (state == ALIVE)
        {
            super.update();
        }
        
        if (Math.abs(startX - this.getX()) >= MAX_TRAVEL_DISTANCE)
        {
            this.state = DEAD;

            Trace.print(
                    "Rocket travelled too far: destroying at " +
                    this.getX() + ", " + this.getY());
        }
        
        if (state == ALIVE)
        {
            sprite.nextFrame();
        }
    }

    public void collideEvent()
    {
        Trace.print("Rocket collided");
        
        this.state = DEAD;
    }
}
