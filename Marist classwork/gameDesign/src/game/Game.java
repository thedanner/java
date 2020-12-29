package game;

import java.io.InputStream;
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;

import game.screen.GameScreen;
import game.util.Trace;
import game.entity.Enemy;
import game.entity.Entity;
import game.entity.PlayerCharacter;
import game.entity.Rocket;
import game.highScores.HighScores;
import game.level.AbstractLevel;
import game.persistence.Settings;
import game.util.Rand;

/**
 * This class manages the game world, including game states, levels, and input.
 * <p>
 * MIDI and WAV sound sources:
 * <p>
 * http://www.aganazzar.com/midi.html
 * <p>
 * http://www.midi4u.com/games/
 * <p>
 * http://frogstar.com/wav/effects.asp
 * 
 * @author The Danner, Jake Rot, Crazy Cat Lady
 * 
 */
public class Game
{
    // *********
    // CONSTANTS
    // *********
    
    /** Game levels (and sequence) */
    private static final String[] LEVELS = {
        "game.level.Level0",
        "game.level.Level1",
        "game.level.Level2",
        "game.level.Level3",
    };
    
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_HARD = 1;
    
    /** Maximum number of levels */
    private final static int MAX_LEVELS = LEVELS.length;
    
    /** Points for taking bacon */
    public static final int POINTS_BACON = 9001;
    
    /** Points for taking the bonus */
    public static final int POINTS_MONSTA = 8999;
    
    /** Number of lives remaining */
    public static final int DEFAULT_LIVES_EASY = 10;
    public static final int DEFAULT_LIVES_HARD = 4;

    /* Game states */
    private static int code = 0;
    // private final static int GAME_STATE_START = code++;
    private final static int GAME_STATE_LEVEL_START = code++;
    private final static int GAME_STATE_LEVEL_END = code++;
    private final static int GAME_STATE_RUNNING = code++;
    private final static int GAME_STATE_PLAYER_CAUGHT = code++;
    private final static int GAME_STATE_OVER = code++;
    
    /** Generic delay of game */
    private final static int GAME_DELAY_CAUGHT = 30;
    
    /** Flag for game state management */
    private final static int FIRST_TIME = -1;
    
    public final static int EXTRA_LIFE_INTERVAL = 250000;
    
    private static HighScores highScores;
    
    // ****************
    // STATIC VARIABLES
    // ****************
    
    /* Persistent storage of settings. */
    private static Settings settings;

    /** Level number */
    private static int levelNum;

    /** Current score */
    private static int score;
    
    /** Lives remaining */
    private static int lives;
    
    private static int extraLifeStep;
    
    /** Life sprite on screen */
    private static Sprite life;

    /** Rocket sprite on screen */
    private static Sprite rocketSprite;

    /* Sounds, text for opening trap door(s) and enemy-player collision */
    private static boolean soundsLoaded;
    private static Player extraLifeSound;
    private static Player ouchSound;
    private static Player successSound;
    private static Player rocketLaunchSound;
    private static Player lafSound;
    
    static
    {
        settings = new Settings();
        highScores = new HighScores();
        
        soundsLoaded = false;
        
        levelNum = 0;
        score = 0;
        
        lives = -1;
        extraLifeStep = 0;
    }

    // ******************
    // INSTANCE VARIABLES
    // ******************
    
    /** Start state when world begins */
    private int state;
    
    /** Steps through loop where a step is about 33 ms */
    private int steps;
    
    private int stepsStart;
    
    /** PlayerCharacter in world */
    private PlayerCharacter pc;
    
    /** Current level */
    private AbstractLevel level;
    
    /** Global layers contains every visible object */
    private LayerManager layers;
    
    /* Screen dimensions */
    private int scrW;
    private int scrH;
    
    /** X center of physical screen */
    private int xCenter;
    
    /** Y center of physical screen */
    private int yCenter;
    
    private GameScreen gs;
    
    private Random random = Rand.getInstance();
    
    /* X, Y coordinate of view window in world */
    private int xView;
    private int yView;
    
    /** Graphics context for writing to game screen */
    private Graphics g;
    
    private int choice;

    private int projectileLayer;
    
    private static final String[] CAUGHT_BUBBLES = {
        "PEW PEW",
        "OMG LAZ0RZ",
        "WTF PENGUINS DON'T EAT BACON",
        "...NEED...OIL..."
    };
    
    /**
     * 
     * @param gs
     */
    public Game(GameScreen gs)
    {
        this.gs = gs;

        this.steps = 0;
        
        this.stepsStart = FIRST_TIME;
        
        this.choice = 0;
        
        this.g = gs.getGraphics();
        
        this.scrW = gs.getWidth();
        this.scrH = gs.getHeight();
        
        this.xCenter = this.scrW / 2;
        this.yCenter = this.scrH / 2;
        
        this.state = GAME_STATE_LEVEL_START;

        this.projectileLayer = -1;
        
        this.pc = new PlayerCharacter();
        
        // Load images and sounds
        try
        {
            life = new Sprite(Image
                    .createImage("/resources/images/CQ_life.png"), 16, 16);
            
            rocketSprite = new Sprite(Image
                    .createImage("/resources/images/CQ_rocket.png"), 16, 16);
            
            loadSounds();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Runs one cycle of game loop.
     * 
     * @param input
     *            Player input
     */
    public void loop(int input) throws Exception
    {
        steps++;
        
        // I guess I stand corrected.
        // Java 1.3 (which ME uses) doesn't support
        // int constants in case statements.
        if (state == GAME_STATE_LEVEL_START)
        {
            openLevel();
        }
        else if (state == GAME_STATE_LEVEL_END)
        {
            closeLevel();
        }
        else if (state == GAME_STATE_RUNNING)
        {
            runLevel(input);
        }
        else if (state == GAME_STATE_PLAYER_CAUGHT)
        {
            playerCaught();
        }
        else if (state == GAME_STATE_OVER)
        {
            closeLevel();
        }
    }
    
    /** Opens a new level */
    private void openLevel() throws Exception
    {
        Trace.print("opening level " + levelNum);
        
        // Set up the new level
        level = getLevel();
        
        level.setPC(pc);
        
        pc.setLevel(level);
        
        pc.reset();
        
        buildLayers();
        
        resetView();
        
        // Transition to running state
        state = GAME_STATE_RUNNING;
    }
    
    /** Closes a level and goes to next level */
    private void closeLevel()
    {
        try
        {
            if (Game.hasLives())
            {
                levelNum++;
                
                if (Game.isSoundEnabled())
                {
                    successSound.start();
                }
            }
            
            if (Game.isGameOver())
            {
                if (Game.isSoundEnabled())
                {
                    // play theme sound
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        gs.nextLevel();
    }
    
    /** Handles player getting caught by enemy */
    private void playerCaught()
    {
        if (stepsStart == FIRST_TIME)
        {
            choice = random.nextInt(CAUGHT_BUBBLES.length);
            
            lives--;

            stepsStart = steps;

            for (int i = 0; i < level.getRockets().size(); i++)
            {
                Rocket rocket = (Rocket) level.getRockets().elementAt(i);
                
                rocket.setDead();
            }
            
            try
            {
                int toss = random.nextInt(2);
                
                if (Game.isSoundEnabled())
                {
                    ouchSound.start();
                    
                    if (toss == 1)
                    {
                        lafSound.start();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        int elapsed = steps - stepsStart;
        
        if (elapsed < GAME_DELAY_CAUGHT)
        {
            // Render state of game
            render();
            
            // Put text above PlayerCharacter's head
            Font f = Font.getFont(
                    Font.FACE_PROPORTIONAL,
                    Font.STYLE_BOLD,
                    Font.SIZE_LARGE);
            g.setFont(f);
            
            g.setColor(255, 255, 0);
            
            int x = scrW / 2; // screenw / 2; //pc.getX() - xView;
            int y = scrH / 2; // screenh / 2; //pc.getY() - yView - tileh;
            
            g.drawString(
                    CAUGHT_BUBBLES[choice],
                    x, y,
                    Graphics.HCENTER | Graphics.TOP);
        }
        else
        {
            // Clear steps start
            stepsStart = FIRST_TIME;
            
            // If no more lives, game is over
            if (!hasLives())
            {
                state = GAME_STATE_OVER;
                return;
            }
            
            // Return PlayerCharacter to home position
            pc.reset();
            
            // Reset the view
            resetView();
            
            // Reset level's state
            level.reset();
            
            // Transition to running
            state = GAME_STATE_RUNNING;
        }
    }
    
    /**
     * Runs a "game running" cycle.
     * 
     * @param input
     *            Player input
     */
    private void runLevel(int input)
    {
        update(input);
        
        render();
    }
    
    /**
     * Updates the world.
     * 
     * @param input
     *            Player input
     */
    private void update(int input)
    {
        // Get PlayerCharacter's last known location.
        int xOld = pc.getX();
        int yOld = pc.getY();
        
        // Update PlayerCharacter
        pc.update(input);
        
        // Update the view based on PlayerCharacter's current position
        scrollView(xOld, yOld);
        
        // Update everything in the level
        level.update();
        
        // Update the state -- anything NOT handled by level
        updateState();
    }
    
    /** Renders the world */
    private void render()
    {
        layers.paint(g, 0, 0);
        
        showStatus();
        
        // int tilex = pc.getX() / 16;
        // int tiley = pc.getY() / 16;
        // if(tilex < 0 || tiley < 0)
        // return;
        //		
        // String s = "(" + pc.getX() + " " + pc.getY() + ")";
        // g.setColor(0);
        // g.drawString(s,10,0,Graphics.TOP|Graphics.LEFT);
        
        // gs.flushGraphics();
    }
    
    /** Handle top-level collisions */
    private void updateState()
    {
        if (level.isComplete())
        {
            state = GAME_STATE_LEVEL_END;
            return;
        }
        
        try
        {
            if (level.isPlayerCaught())
            {
                state = GAME_STATE_PLAYER_CAUGHT;
                return;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Scroll the view paraemters.
     * 
     * @param xOld
     *            PlayerCharacter's last known x
     * @param yOld
     *            PlayerCharacter's last known y
     */
    private void scrollView(int xOld, int yOld)
    {
        // Get PC's delta position
        int x = pc.getX();
        int y = pc.getY();
        
        int dx = x - xOld;
        int dy = y - yOld;
        
        // Get ball's screen coordinates
        int xScr = x - xView;
        int yScr = y - yView;
        
        // Make viewport play catch-up
        if (dx > 0 && xScr > xCenter || dx < 0 && xScr < xCenter)
        {
            xView += dx;
        }
        
        if (dy > 0 && yScr > yCenter || dy < 0 && yScr < yCenter)
        {
            yView += dy;
        }
        
        // if(dy < 0 && yScr < yCenter)
        // yView += dy;
        
        // Check boundary conditions
        if (xView < 0 || (xView + scrW) > AbstractLevel.WORLD_WIDTH)
        {
            xView -= dx;
        }
        
        if (yView < 0 || (yView + scrH) > AbstractLevel.WORLD_HEIGHT)
        {
            yView -= dy;
        }
        
        // Move the window
        layers.setViewWindow(xView, yView, scrW, scrH);
    }
    
    /**
     * Gets and initializes level n.
     * 
     * @return Level
     */
    private AbstractLevel getLevel() throws Exception
    {
        level = null;
        
        if (levelNum >= MAX_LEVELS)
        {
            throw new IndexOutOfBoundsException("level number (" + levelNum
                    + ") >= max (" + MAX_LEVELS + ")");
        }
        
        String path = LEVELS[levelNum];
        Trace.print("starting level " + path);
        
        Class cl = Class.forName(path);
        
        level = (AbstractLevel) cl.newInstance();
        
        level.init();

        level.setGame(this);

        Trace.print("level created and initialized");

        return level;
    }
    
    /**
     * Sets view so that PlayerCharacter is as centered as possible.
     */
    private void resetView()
    {
        xView = level.getPC().getX() +
                (level.getPC().getSprite().getWidth() / 2) -
                (scrW / 2);
        
        yView = level.getPC().getY() +
                (level.getPC().getSprite().getHeight() / 2) -
                (scrH / 2);
        
        xView = Math.max(xView, 0);
        xView = Math.min(xView, level.getWidth() - scrW);
        
        yView = Math.max(yView, 0);
        yView = Math.min(yView, level.getHeight() - scrH);
        
        layers.setViewWindow(xView, yView, scrW, scrH);
    }
    
    /**
     * Builds the layer manager layers.
     * 
     */
    private void buildLayers()
    {
        // Create the layer stack
        layers = new LayerManager();
        
        // Add pc's sprites
        Sprite[] graceSprites = pc.getSprites();
        for (int j = 0; j < graceSprites.length; j++)
        {
            layers.append(graceSprites[j]);
        }
        
        this.projectileLayer = layers.getSize();
        
        // Add the enemies
        Vector enemies = level.getEnemies();
        for (int j = 0; j < enemies.size(); j++)
        {
            Enemy enemy = (Enemy) enemies.elementAt(j);
            
            enemy.setLevel(level);
            
            layers.append(enemy.getSprite());
        }
        
        // Add the goal
        layers.append(level.getGoal());
        
        // Add the various layers
        layers.append(level.getRewardLayer());
        
        layers.append(level.getWorldLayer());
        
        layers.append(level.getObstacleLayer());
        
        layers.append(level.getBackgroundLayer());
        
        layers.append(level.getCollisionLayer());
        
        // Add the enemy blocks
        Vector enemyBlocks = level.getEnemyBlocks();
        for (int j = 0; j < enemyBlocks.size(); j++)
        {
            Sprite block = (Sprite) enemyBlocks.elementAt(j);
            layers.append(block);
        }
    }

    /** Show the status */
    private void showStatus()
    {
        Font f = Font.getFont(
                Font.FACE_PROPORTIONAL,
                Font.STYLE_BOLD,
                Font.SIZE_LARGE);
        
        g.setFont(f);
        
        g.setColor(0, 0, 0);
        
        String status = " " + score;
        
        g.drawString(
                status,
                scrW, 0,
                Graphics.TOP | Graphics.RIGHT);
        
        int offset = 0;
        for (int j = 0; j < (lives - 1); j++)
        {
            life.setPosition(offset, 0);
            
            life.paint(g);
            
            offset += life.getWidth();
        }
        
        if (pc.hasRockets())
        {
            rocketSprite.setPosition(0, life.getHeight() + 2);
            
            rocketSprite.paint(g);
        }
    }
    
    public void addProjectile(Entity agent)
    {
        layers.insert(agent.getSprite(), projectileLayer);
    }
    
    public void removeEntity(Entity agent)
    {
        layers.remove(agent.getSprite());
    }
    
    /**
     * Gets the level number.
     * 
     * @return Level number.
     */
    public static int getLevelNum()
    {
        return levelNum;
    }
    
    /**
     * Sets the level number.
     * 
     * @param levelNum
     *            Level number
     */
    public static void setLevelNum(int levelNum)
    {
        Game.levelNum = levelNum;
    }
    
    /** Resets the game state. */
    public static void reset()
    {
        Game.levelNum = 0;
        Game.score = 0;
        Game.extraLifeStep = 0;

        resetLives();
    }
    
    private static void resetLives()
    {
        if (getDifficulty() == DIFFICULTY_EASY)
        {
            Game.lives = DEFAULT_LIVES_EASY;
        }
        else if (getDifficulty() == DIFFICULTY_HARD)
        {
            Game.lives = DEFAULT_LIVES_HARD;
        }
    }
    
    /**
     * Gets the game score.
     * 
     * @return Score.
     */
    public static int getScore()
    {
        return Game.score;
    }
    
    /**
     * Sets the score.
     * 
     * @param score
     *            Score
     */
    public static void setScore(int score)
    {
        Game.score = score;
    }
    
    /**
     * Gets the lives remaining.
     * 
     * @return Lives
     */
    public static int getLives()
    {
        return Game.lives;
    }
    
    /**
     * Sets the lives
     * 
     * @param lives
     *            Lives remaining
     */
    public static void setLives(int lives)
    {
        Game.lives = lives;
    }
    
    public static boolean hasLives()
    {
        return (lives > 0);
    }
    
    public static int checkExtraLifeAward()
    {
        int addedLives = 0;
        
        while (Game.score / Game.EXTRA_LIFE_INTERVAL > Game.extraLifeStep)
        {
            Game.lives ++;
            Game.extraLifeStep ++;
            addedLives ++;
            
            Trace.print(
                    "life added, score step passed: " +
                    Game.EXTRA_LIFE_INTERVAL);
            
            if (Game.isSoundEnabled())
            {
                try
                {
                    extraLifeSound.start();
                }
                catch (MediaException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        
        return addedLives;
    }
    
    /**
     * Increments the score.
     * 
     * @param incr
     *            Increment
     */
    public static void addScore(int amount)
    {
        Game.score += amount;

        checkExtraLifeAward();
    }
    
    public static void setSoundEnabled(boolean newState)
    {
        settings.setAudioEnabled(newState);
    }
    
    public static boolean isSoundEnabled()
    {
        return settings.isAudioEnabled();
    }
    
    public static boolean isGameOver()
    {
        return (!hasLives()) || (levelNum >= MAX_LEVELS);
    }
    
    public static HighScores getHighScores()
    {
        return highScores;
    }
    
    public static void setDifficulty(int newDifficulty)
    {
        settings.setDifficulty(newDifficulty);
    }

    public static int getDifficulty()
    {
        return settings.getDifficulty();
    }

    public static Player playRocketLaunch() throws MediaException
    {
        rocketLaunchSound.start();
        return rocketLaunchSound;
    }

    private static void loadSounds() throws Exception
    {
        if (soundsLoaded)
        {
            return;
        }
        
        InputStream is = Game.class.getResourceAsStream("/resources/audio/gong.wav");
        extraLifeSound = Manager.createPlayer(is, "audio/X-wav");
        extraLifeSound.prefetch();
        
        is = Game.class.getResourceAsStream("/resources/audio/puppy_yip.wav");
        ouchSound = Manager.createPlayer(is, "audio/X-wav");
        ouchSound.prefetch();
        
        // Source: http://frogstar.com/wav/effects.asp
        is = Game.class.getResourceAsStream("/resources/audio/evil_laf.wav");
        lafSound = Manager.createPlayer(is, "audio/X-wav");
        lafSound.prefetch();
        
        is = Game.class.getResourceAsStream("/resources/audio/w00t.wav");
        successSound = Manager.createPlayer(is, "audio/X-wav");
        successSound.prefetch();

        is = Game.class.getResourceAsStream("/resources/audio/rocket_launch.wav");
        rocketLaunchSound = Manager.createPlayer(is, "audio/X-wav");
        rocketLaunchSound.prefetch();
        
        soundsLoaded = true;
    }
}
