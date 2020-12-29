package game.screen;

import com.mobigenix.dogz.nucleus.Dogz;
import com.mobigenix.dogz.nucleus.DogzManager;
import com.mobigenix.dogz.screen.ActionListener;
import com.mobigenix.dogz.screen.MenuOptions;
import game.Game;
import javax.microedition.lcdui.Command;

/**
 * 
 */
public class HighScoresConfigScreen extends MenuOptions implements
        ActionListener
{
    private final static String TITLE = "High Scores";
    
    private final static int OPTION_SHOW = 0;
    private final static int OPTION_CLEAR = 1;
    
    private final static String[] OPTIONS = { "Show High Scores", "Clear" };
    
    private DogzManager dmgr;
    
    /**
     * Constructor
     */
    public HighScoresConfigScreen(DogzManager dmgr)
    {
        super(TITLE, OPTIONS, 0);
        
        this.dmgr = dmgr;
        
        this.setOptions(OPTIONS, 0);
        
        this.setListener(this);
        
        this.addCommand(new Command("OK", Command.OK, 0));
        this.addCommand(new Command("Never mind", Command.CANCEL, 1));
    }
    
    /**
     * Handles screen selections.
     * 
     * @param dogz
     *            Dogz calling
     * @param action
     *            Command action
     * @param selected
     *            Selected option only one true
     */
    public void onAction(Dogz dogz, int action, boolean[] selected)
    {
        if (Command.OK == action)
        {
            if (selected[OPTION_SHOW])
            {
                dmgr.push(new HighScoresScreen(dmgr));
            }
            else if (selected[OPTION_CLEAR])
            {
                Game.getHighScores().clear();
            }
        }
        
        dmgr.pop();
    }
}
