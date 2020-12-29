package game.screen;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

import com.mobigenix.dogz.nucleus.Dogz;
import com.mobigenix.dogz.nucleus.DogzManager;

import game.Game;

/**
 * 
 */
public class GameScreen extends GameCanvas implements Runnable, Dogz
{
    private final static int FRAME_DELAY = 33;

    private static final String SOUND_OFF = "Sound is OFF";
    private static final String SOUND_ON = "Sound is ON";
    
    private DogzManager dmgr;

    private Command toggleSoundCommand;
    
    private Game game;
    
    private Thread gameThread;
    
    private final static int DELAY_FRAME = 15;
    
    private double dur = DELAY_FRAME;
    
    private double alpha = 2. / (DELAY_FRAME + 1);
    
    /**
     * Constructor
     * 
     * @param dmgr
     *            Dogz manager
     */
    public GameScreen(DogzManager dmgr)
    {
        super(true);
        
        this.dmgr = dmgr;
        
        // addCommand(new Command("Quit", Command.EXIT, 0));
        // addCommand(new Command("Pause", Command.STOP, 1));
        
        this.addCommand(this.createToggleSoundCommand());
        
        //this.addCommand(new Command("Options", Command.ITEM, 0));
        //addCommand("Pause", Command.ITEM, 0);
        //this.addCommand(new Command("Exit", Command.EXIT, 1));
        
        game = new Game(this);
    }
    
    /**
     * Runs the games loop.
     */
    public void run()
    {
        gameThread = Thread.currentThread();
        
        // Main game loop
        try
        {
            while (gameThread == Thread.currentThread())
            {
                int input = getKeyStates();
                
                long t0 = System.currentTimeMillis();
                
                game.loop(input);
                
                flushGraphics();
                
                if (gameThread != Thread.currentThread())
                {
                    return;
                }
                
                long t1 = System.currentTimeMillis();
                
                long latency = t1 - t0;
                
                long delay = smooth(latency);
                
                Thread.sleep(delay);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /** Advances to next level */
    public void nextLevel()
    {
        gameThread = null;
        
        // Go to next inter-level screen, if there is one
        if (!Game.isGameOver())
        {
            dmgr.swap(new InterLevelScreen(dmgr));
        }
        else
        {
            // Otherwise, player is done!
            dmgr.swap(new GameOverScreen());
        }
    }
    
    /**
     * Gets the graphics context for screen.
     * 
     * @return Graphics context
     */
    public Graphics getGraphics()
    {
        return super.getGraphics();
    }
    
    /**
     * Smooth latency using EMA
     * <p>
     * See http://en.wikipedia.org/wiki/Moving_average.
     * 
     * @param latency
     * @return Smoothed latency
     */
    protected long smooth(long latency)
    {
        long delay = FRAME_DELAY - latency;
        
        if (delay < 0)
        {
            delay = 0;
        }
        
        dur = alpha * delay + (1 - alpha) * dur;
        
        return (long) dur;
    }
    
    private Command createToggleSoundCommand()
    {
        toggleSoundCommand =
                new Command(this.getSoundLabel(), Command.ITEM, 0);

        return toggleSoundCommand;
    }
    
    private String getSoundLabel()
    {
        return (Game.isSoundEnabled() ? SOUND_ON : SOUND_OFF);
    }
    
    public void onPop()
    {
        gameThread = null;
    }
    
    /**
     * Starts processing going into stack
     */
    public void onPush()
    {
        new Thread(this).start();
    }
    
    /**
     * Process soft buttons
     * 
     * @param c
     *            Command button
     * @param dmgr
     *            Dogz manager
     */
    public void onSelect(Command c, DogzManager dmgr)
    {
        if (c == toggleSoundCommand)
        {
            this.removeCommand(toggleSoundCommand);
            
            Game.setSoundEnabled(!Game.isSoundEnabled());

            this.addCommand(this.createToggleSoundCommand());
        }
    }
}