package game.screen;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.mobigenix.dogz.nucleus.Dogz;
import com.mobigenix.dogz.nucleus.DogzManager;

import game.Game;
import game.util.Util;

/**
 * This class handles the game-over screen.
 * 
 * @author Ron Coleman
 * 
 */
public class GameOverScreen extends Canvas implements Dogz
{
    protected final static int MAGIC_Y = 120;
    
    /**
     * Constructor.
     * 
     * @param score
     */
    public GameOverScreen()
    {
        addCommand(new Command("OK", Command.OK, 1));
    }
    
    /**
     * Paint the inter-level screen.
     * 
     * @param g
     *            Graphics context
     */
    public void paint(Graphics g)
    {
        // Clear the screen
        
        int widthCanvas = getWidth();
        int heightCanvas = getHeight();

        // Draw the background
        g.setColor(Util.DEFAULT_BACKGROUND_COLOR);
        g.fillRect(0, 0, widthCanvas, heightCanvas);
        
        // Set the drawing color to black
        g.setColor(Util.DEFAULT_LOWLIGHT_COLOR);
        
        // Get a font
        Font f = Font.getFont(
                Font.FACE_PROPORTIONAL,
                Font.STYLE_BOLD,
                Font.SIZE_LARGE);
        
        g.setFont(f);
        
        if (Game.isGameOver())
        {
            g.drawString(
                    "Game Over",
                    getWidth() / 2,
                    0,
                    Graphics.HCENTER
                    | Graphics.TOP);
        }
        //else
        if (Game.hasLives())
        {
            g.drawString(
                    "Congratulations!",
                    getWidth() / 2,
                    MAGIC_Y - f.getHeight() * 3,
                    Graphics.HCENTER | Graphics.TOP);
        }
        
        // Render the tile somewhere above the middle of the screen
        g.drawString(
                "Your score: " + Game.getScore(),
                getWidth() / 2,
                MAGIC_Y,
                Graphics.HCENTER | Graphics.TOP);
    }
    
    /**
     * Process soft buttons.
     * 
     * @param c
     *            Command calling
     * @param smgr
     *            Screen manager to manipulate the stack
     */
    public void onSelect(Command c, DogzManager dmgr)
    {
        //dmgr.pop();
        dmgr.swap(new HighScoresScreen(dmgr, Game.getScore()));
    }
    
    public void onPop()
    {
        
    }
    
    public void onPush()
    {
        repaint();
    }
}
