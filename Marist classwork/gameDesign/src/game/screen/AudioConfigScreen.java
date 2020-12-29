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
public class AudioConfigScreen extends MenuOptions implements ActionListener
{
    private final static String TITLE = "Want sounds?";

    private final static int OPTION_ON = 0;
    private final static int OPTION_OFF = 1;
    
    private final static String[] OPTIONS = {
        "On",
        "Off"
    };
    
    private DogzManager dmgr;
    
    /**
     * Constructor
     */
    public AudioConfigScreen(DogzManager dmgr)
    {
        super(TITLE, OPTIONS, 0);
        
        this.dmgr = dmgr;
        
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
            if (selected[OPTION_ON])
            {
                Game.setSoundEnabled(true);
            }
            else if (selected[OPTION_OFF])
            {
                Game.setSoundEnabled(false);
            }
        }
        
        dmgr.pop();
    }

    public void onPush()
    {
        if (Game.isSoundEnabled())
        {
            this.setSelectIndex(OPTION_ON);
        }
        else
        {
            this.setSelectIndex(OPTION_OFF);
        }
    }
}

