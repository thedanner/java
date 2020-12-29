package game.screen;

import com.mobigenix.dogz.nucleus.Dogz;
import com.mobigenix.dogz.nucleus.DogzManager;
import com.mobigenix.dogz.screen.ActionListener;
import com.mobigenix.dogz.screen.MenuOptions;
import javax.microedition.lcdui.Command;

/**
 * 
 */
public class QuitScreen extends MenuOptions implements ActionListener
{
    private final static String TITLE = "Had enough, punk?";
    
    private final static int OPTION_NO = 0;
    private final static int OPTION_YES = 1;
    
    private final static String[] OPTIONS = {
        "No",
        "Yes"
    };
    
    private DogzManager dmgr;
    
    /**
     * Constructor
     */
    public QuitScreen(DogzManager dmgr)
    {
        super(TITLE, OPTIONS, 0);
        
        this.dmgr = dmgr;
        
        // setOptions(OPTIONS, 0);
        
        this.setListener(this);
        
        this.addCommand(new Command("OK", Command.OK, 0));
        this.addCommand(new Command("Never mind", Command.CANCEL, 0));
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
        if (Command.OK == action && selected[OPTION_YES])
        {
            dmgr.quit();
        }
        else
        {
            dmgr.pop();
        }
    }
}
