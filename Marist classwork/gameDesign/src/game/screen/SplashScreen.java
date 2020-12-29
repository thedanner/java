package game.screen;

import com.mobigenix.dogz.nucleus.Dogz;
import com.mobigenix.dogz.nucleus.DogzManager;
import game.Game;
import game.util.Util;
import java.io.InputStream;
//import javax.microedition.lcdui.Command;
//import javax.microedition.lcdui.Font;
//import javax.microedition.lcdui.Graphics;
//import javax.microedition.lcdui.Image;
//import javax.microedition.lcdui.game.GameCanvas;
//import javax.microedition.lcdui.game.Sprite;
//import javax.microedition.media.Manager;
//import javax.microedition.media.MediaException;
//import javax.microedition.media.Player;

/**
 *
 */
public class SplashScreen extends GameCanvas implements Runnable, Dogz
{
    protected final static int TEXT_COLOR = 0xDBB7FF; // Light pink
    
    protected final static int MAGIC_Y = 200;
    
    /** Number of timeout steps: 30 steps = 1 second */
    protected final static int TIMEOUT_STEPS = 120;
    
    /** Delay between splash frames -- one step */
    protected final static int FRAME_DELAY = 33;
    
    protected DogzManager dmgr;
    
    protected Thread splashThread;
    
    protected Sprite logo;

    protected Player loadingSound;
    
    /**
     * Constructor
     * 
     * @param fresh
     *            If true there is no resume option.
     */
    public SplashScreen(DogzManager dmgr)
    {
        super(false);
        
        this.dmgr = dmgr;
        
        this.addCommand(new Command("Start", Command.OK, 0));
        
        try
        {
            Image img = Image.createImage("/resources/images/CQ_dog_left.png");
            
            logo = new Sprite(img, 16, 16);
            
            // Awesome loading sound
            InputStream is = getClass().getResourceAsStream(
                "/resources/audio/nina.wav");
            
            loadingSound = Manager.createPlayer(is, "audio/X-wav");
            loadingSound.prefetch();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void run()
    {
        // To abort the game loop, set gameThread to null.
        // To check if we should still run, check that:
        // (gameThread == Thread.currentThread())
        splashThread = Thread.currentThread();
        
        int steps = 0;
        
        Graphics g = this.getGraphics();
        
        try
        {
            while (splashThread == Thread.currentThread())
            {
                draw(g);
                
                Thread.sleep(FRAME_DELAY);
                
                // Move logo to the next frame
                // logo.nextFrame();
                
                steps++;
                
                if (steps >= TIMEOUT_STEPS)
                {
                    break;
                }
            }
            
            if (splashThread == Thread.currentThread())
            {
                goMainScreen();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Renders splash info to the graphics context.
     * 
     * @param g
     *            Graphics context
     */
    public void draw(Graphics g)
    {
        // Clear the screen
        int widthLogo = logo.getWidth();
        int heightLogo = logo.getHeight();
        
        int widthCanvas = getWidth();
        int heightCanvas = getHeight();
        
        // Draw the background.
        g.setColor(Util.DEFAULT_BACKGROUND_COLOR);
        g.fillRect(0, 0, widthCanvas, heightCanvas);
        
        // Position the logo and text
        int logoX = (widthCanvas - widthLogo) / 2;
        int logoY = (heightCanvas - heightLogo) / 2;
        
        // Set the drawing color to black
        // // wtf that's not black
        // // this is why comments suck.
        g.setColor(TEXT_COLOR);
        
        g.drawString(Util.COPYRIGHT, getWidth() / 2, MAGIC_Y, Graphics.HCENTER
                | Graphics.TOP);
        
        g.drawString(Util.VERSION, getWidth() / 2, MAGIC_Y
                + g.getFont().getHeight() * 2, Graphics.HCENTER | Graphics.TOP);
        
        Font f = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD,
                Font.SIZE_LARGE);
        g.setFont(f);
        
        // Render the tile somewhere above the middle of the screen
        // and above the logo
        int fontHeight = f.getHeight();
        
        g.drawString(Util.GAME_NAME, getWidth() / 2, logoX - fontHeight,
                Graphics.HCENTER | Graphics.TOP);
        
        logo.setPosition(logoX, logoY);
        
        logo.paint(g);
        
        flushGraphics();
    }
    
    protected void goMainScreen()
    {
        dmgr.swap(new MainScreen(true, dmgr));
    }
    
    /**
     * Processes a soft button.
     * 
     * @param c
     *            Command soft button
     * @param dmgr
     *            Screen manager to manipulate the screen stack
     */
    public void onSelect(Command c, DogzManager dmgr)
    {
        try
        {
            splashThread = null;
            
            goMainScreen();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void onPush()
    {
        new Thread(this).start();
        
        try
        {
            if (Game.isSoundEnabled())
            {
                loadingSound.start();
            }
        }
        catch (MediaException ex)
        {
            ex.printStackTrace();
        }
    }

    public void onPop()
    {
        splashThread = null;
        
        /*
        try
        {
            loadingSound.stop();
        }
        catch (MediaException ex)
        {
            ex.printStackTrace();
        }
        /*/
    }
}
