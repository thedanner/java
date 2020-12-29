package game.screen;

import com.mobigenix.dogz.nucleus.Dogz;
import com.mobigenix.dogz.nucleus.DogzManager;
import com.mobigenix.dogz.screen.ActionListener;
import com.mobigenix.dogz.screen.Blotter;
import game.util.Util;
import java.io.IOException;
import javax.microedition.lcdui.Command;

/**
 * This class handles the quit screen.
 * 
 * @author Ron Coleman
 * 
 */
public class InstructionsScreen extends Blotter implements ActionListener
{
    private final static String INSTRUCTIONS_FILE_PATH = "/resources/text/instructions.txt";

    private final static String TITLE = "Instructions";
    
	private static String instructionsText;
    
    static
    {
        try
        {
            instructionsText = Util.readTextFile(INSTRUCTIONS_FILE_PATH);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
	
	private DogzManager dmgr;
	
	public InstructionsScreen(DogzManager dmgr)
	{
		super(TITLE);
        
        this.dmgr = dmgr;
        
        this.addInstructionsText();
        
        this.addCommand(new Command("OK", Command.OK, 0));
        this.addCommand(new Command("Go Back", Command.CANCEL, 1));
        
        Util.setDefaultScreenColors(this);
        
        this.setListener(this);
        
        // Use an easier to read color than the default.
        this.setHighLight(Util.DEFAULT_LOWLIGHT_COLOR);
    }
    
    private void addInstructionsText()
    {
        this.append(instructionsText);
    }
    
    /**
     * Handles soft buttons.
     * 
     * @param dogz
     *            Dogz calling us
     * @param action
     *            Command action
     * @param selections
     *            NA for blotter
     */
    public void onAction(Dogz dogz, int action, boolean[] selections)
    {
        dmgr.pop();
    }
}
