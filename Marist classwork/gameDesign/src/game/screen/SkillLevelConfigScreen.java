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
public class SkillLevelConfigScreen
        extends MenuOptions
        implements ActionListener
{
    private final static String TITLE = "How man are you?";
    
    private final static int OPTION_EASY = 0;
    private final static int OPTION_HARDCORE = 1;
    
    private final static String[] OPTIONS = {
        "Easy",
        "Hardcore"
    };
    
    private DogzManager dmgr;
    
    /**
     * Constructor
     */
    public SkillLevelConfigScreen(DogzManager dmgr)
    {
        super(TITLE);
        
        this.dmgr = dmgr;
        
        this.setListener(this);
        
        this.addCommand(new Command("OK", Command.OK, 0));
        this.addCommand(new Command("Never mind", Command.CANCEL, 1));
        
        // ATTN Ron: MenuOptions appears to be bugged:
        // If I were to call setOptions(OPTIONS, 0) here (or in the
        // overloaded constructor), 0 will be set as selected in the parameter
        // to onAction().  Even if I change the selected index with a call
        // to setSelectIndex(1), the previous index isn't unset.  So, in the
        // parameter to onAction(), both of the 2 array entries will be true,
        // implying that both are selected, which is impossible in this case.
        if (Game.getDifficulty() == Game.DIFFICULTY_EASY)
        {
            this.setOptions(OPTIONS, OPTION_EASY);
        }
        else if (Game.getDifficulty() == Game.DIFFICULTY_HARD)
        {
            this.setOptions(OPTIONS, OPTION_HARDCORE);
        }
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
            if (selected[OPTION_EASY])
            {
                Game.setDifficulty(Game.DIFFICULTY_EASY);
            }
            else if (selected[OPTION_HARDCORE])
            {
                Game.setDifficulty(Game.DIFFICULTY_HARD);
            }
        }
        
        dmgr.pop();
    }
}
