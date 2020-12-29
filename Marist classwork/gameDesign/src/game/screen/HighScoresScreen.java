package game.screen;

import com.mobigenix.dogz.nucleus.Dogz;
import com.mobigenix.dogz.nucleus.DogzManager;
import game.Game;
import game.highScores.HighScore;
import game.highScores.HighScores;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

/**
 * 
 */
public class HighScoresScreen extends Form implements Dogz
{
    private final static String TITLE = "The Best Robodogs";
    
    private DogzManager dmgr;
    
    private TextField nameField;
    private int playerScore;
    
    public HighScoresScreen(DogzManager dmgr)
    {
        this(dmgr, -1);
    }
    
    public HighScoresScreen(DogzManager dmgr, int playerScore)
    {
        super(TITLE);
        
        this.dmgr = dmgr;
        this.playerScore = playerScore;

        addCommand(new Command("OK", Command.OK, 0));
        addCommand(new Command("Go Back", Command.CANCEL, 1));
        
        init();
    }
    
    private void init()
    {
        if (playerScore >= 0)
        {
            // Make the entry box.
            nameField = new TextField(
                    "Please enter your name:",
                    null,
                    20,
                    TextField.ANY);
            
            append(nameField);
            
            append(new StringItem(Integer.toString(playerScore), "You!"));
        }
        
        populatScoresList();
    }
    
    private void populatScoresList()
    {
        HighScores highScores = Game.getHighScores();

        for (int i = 0; i < highScores.size(); i++)
        {
            HighScore score = highScores.get(i);
            
            Item item = new StringItem(
                    Integer.toString(score.getScore()), score.getName());
            
            append(item);
        }
    }
    
    public void onSelect(Command c, DogzManager dmgr)
    {
        if (Command.OK == c.getCommandType())
        {
            Game.getHighScores().add(nameField.getString(), playerScore);
        }

        dmgr.pop();
    }
    
    public void onPush()
    {
    }
    
    public void onPop()
    {
    }
}
