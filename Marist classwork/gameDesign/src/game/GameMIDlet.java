package game;

import javax.microedition.midlet.MIDletStateChangeException;
import com.mobigenix.dogz.nucleus.DogzMIDlet;
import game.screen.SplashScreen;

/**
 * 
 * @author
 */
public class GameMIDlet extends DogzMIDlet
{
    public GameMIDlet()
    {
        
    }
    
    /**
     * Starts the midlet.
     */
    protected void startApp() throws MIDletStateChangeException
    {
        dmgr.push(new SplashScreen(dmgr));
    }
}
