package game.screen;

import com.mobigenix.dogz.nucleus.Dogz;
import com.mobigenix.dogz.nucleus.DogzManager;
import game.Game;
import game.util.Util;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

public class InterLevelScreen extends GameCanvas implements Runnable, Dogz
{
    protected Thread interLevelThread;
    
    protected DogzManager dmgr;
    
    /**
     * Inter-level checks this flag before going to game screen.
     */
    public InterLevelScreen(DogzManager dmgr)
    {
        super(false);
        
        this.dmgr = dmgr;
        
        addCommand(new Command("Cancel", Command.CANCEL, 0));
    }
    
    public void run()
    {
        interLevelThread = Thread.currentThread();
        
        try
        {
            Graphics g = this.getGraphics();
            
            render(g);
            
            Thread.sleep(2000);
            
            if (interLevelThread != Thread.currentThread())
            {
                return;
            }

            dmgr.swap(new GameScreen(dmgr));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * Cancels the inter-level.
     */
    public void cancel()
    {
        interLevelThread = null;
    }
    
    private void render(Graphics g)
    {
        // Clear the screen
        int widthCanvas = getWidth();
        int heightCanvas = getHeight();
        
        // Draw the background
        g.setColor(Util.DEFAULT_BACKGROUND_COLOR);
        g.fillRect(0, 0, widthCanvas, heightCanvas);
        
        // Set the drawing color
        g.setColor(Util.DEFAULT_LOWLIGHT_COLOR);
        
        // Get a font
        Font f = Font.getFont(
                Font.FACE_PROPORTIONAL,
                Font.STYLE_BOLD,
                Font.SIZE_LARGE);
        g.setFont(f);
        
        // Render the tile somewhere above the middle of the screen
        g.drawString(
                "Starting Level " + (1 + Game.getLevelNum()),
                getWidth() / 2,
                100,
                Graphics.HCENTER | Graphics.TOP);
        
        flushGraphics();
    }
    
    public void onPop()
    {
    }
    
    public void onPush()
    {
        new Thread(this).start();
    }
    
    public void onSelect(Command action, DogzManager dogz)
    {
        cancel();
        
        dmgr.pop();
    }
}
