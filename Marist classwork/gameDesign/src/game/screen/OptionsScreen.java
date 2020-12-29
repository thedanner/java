package game.screen;

import com.mobigenix.dogz.nucleus.Dogz;
import com.mobigenix.dogz.nucleus.DogzManager;
import com.mobigenix.dogz.screen.ActionListener;
import com.mobigenix.dogz.screen.MenuOptions;
import javax.microedition.lcdui.Command;

/**
 * 
 */
public class OptionsScreen extends MenuOptions implements ActionListener
{
    private final static String TITLE = "You wanna change something?";
    
    private final static int OPTION_OPTIONS = 0;
    private final static int OPTION_AUDIO_OPTIONS = 1;
    private final static int OPTION_SKILL_LEVEL = 2;
    
    /**
     * Current state of choices
     */
    private final static String[] OPTIONS = {
        "High Scores",
        "Sounds",
        "Difficulty"
    };
    
    private DogzManager dmgr;
    
    /**
     * Constructor
     * 
     * @param fresh
     *            If true there is no resume option.
     */
    public OptionsScreen(DogzManager dmgr)
    {
        super(TITLE, OPTIONS, 0);
        
        this.dmgr = dmgr;
        
        this.setListener(this);
        
        this.addCommand(new Command("Go", Command.OK, 0));
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
        if (action == Command.CANCEL)
        {
            dmgr.pop();
        }
        else if (selected[OPTION_OPTIONS])
        {
            dmgr.push(new HighScoresConfigScreen(dmgr));
        }
        else if (selected[OPTION_AUDIO_OPTIONS])
        {
            dmgr.push(new AudioConfigScreen(dmgr));
        }
        else if (selected[OPTION_SKILL_LEVEL])
        {
            dmgr.push(new SkillLevelConfigScreen(dmgr));
        }
    }
}
