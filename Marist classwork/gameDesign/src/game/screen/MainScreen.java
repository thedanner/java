package game.screen;

import com.mobigenix.dogz.nucleus.Dogz;
import com.mobigenix.dogz.nucleus.DogzManager;
import com.mobigenix.dogz.screen.ActionListener;
import com.mobigenix.dogz.screen.MenuOptions;
import game.Game;
import game.util.Util;
import javax.microedition.lcdui.Command;

/**
 * This class handles the main screen and all sub-screens.
 * 
 * @author Ron
 */
public class MainScreen extends MenuOptions implements ActionListener
{
    private final static String OPTION_NEW_GAME = "New Game";
    private final static String OPTION_RESUME_GAME = "Resume Game";
    private final static String OPTION_OPTIONS = "Options";
    private final static String OPTION_INSTRUCTIONS = "Instructions";
    private final static String OPTION_QUIT = "Quit";
    
    /**
     * List of choices on with a "new game" option
     */
    private String[] CHOICES_FRESH = {
        OPTION_NEW_GAME,
        OPTION_OPTIONS,
        OPTION_INSTRUCTIONS,
        OPTION_QUIT
    };
    
    /**
     * List of choices with a "resume game" option
     */
    private String[] CHOICES_RESUME = {
        OPTION_RESUME_GAME,
        OPTION_NEW_GAME,
        OPTION_OPTIONS,
        OPTION_INSTRUCTIONS,
        OPTION_QUIT
    };
    
    /**
     * Current state of choices
     */
    private String[] choices = null;
    
    private DogzManager dmgr;
    
    /**
     * Constructor
     * 
     * @param fresh
     *            If true there is no resume option.
     */
    public MainScreen(boolean fresh, DogzManager dmgr)
    {
        super(Util.GAME_NAME);
        
        this.dmgr = dmgr;
        
        choices = fresh ? CHOICES_FRESH : CHOICES_RESUME;
        
        setOptions(choices, 0);
        
        setListener(this);
        
        addCommand(new Command("Go", Command.OK, 0));
        
        Util.setDefaultScreenColors(this);
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
        int selection = -1;
        
        for (int i = 0; i < selected.length; i++)
        {
            if (selected[i])
            {
                selection = i;
                break;
            }
        }
        
        if (selection == -1)
        {
            return;
        }
        
        // Enumerate over the choices.
        if (choices[selection].equals(OPTION_NEW_GAME)
                || choices[selection].equals(OPTION_RESUME_GAME))
        {
            Game.reset();
            
            dmgr.push(new InterLevelScreen(dmgr));
        }
        else if (choices[selection].equals(OPTION_OPTIONS))
        {
            dmgr.push(new OptionsScreen(dmgr));
        }
        else if (choices[selection].equals(OPTION_INSTRUCTIONS))
        {
            dmgr.push(new InstructionsScreen(dmgr));
        }
        else if (choices[selection].equals(OPTION_QUIT))
        {
            dmgr.push(new QuitScreen(dmgr));
        }
    }
}
