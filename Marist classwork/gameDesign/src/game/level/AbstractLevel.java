package game.level;

import game.Game;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

import game.util.Trace;
import game.entity.Enemy;
import game.entity.PlayerCharacter;
import game.util.Point;
import game.util.Util;
import gov.nist.core.StringTokenizer;

/**
 * This class is the base class for all levels.
 * 
 * @author Ron Coleman
 * 
 */
public abstract class AbstractLevel
{
    public final static int WORLD_WIDTH = 640;
    public final static int WORLD_HEIGHT = 480;
    
    public final static int BG_TILE_HEIGHT = 160;
    public final static int BG_TILE_WIDTH = 160;
    
    public final static int TILE_HEIGHT = 16;
    public final static int TILE_WIDTH = 16;
    
    public final static int WORLD_TILE_ROWS = WORLD_HEIGHT / TILE_HEIGHT;
    public final static int WORLD_TILE_COLS = WORLD_WIDTH / TILE_WIDTH;

    private final static String BASE_LEVEL_CSV_PATH = "/resources/levels/";

    protected boolean caught;
    protected boolean complete;
    
    protected int baconCount;
    
    protected PlayerCharacter pc;
    protected Point pcInitPos;
    
    protected Sprite goal;
    
    protected TiledLayer worldLayer;
    protected TiledLayer collisionLayer;
    protected TiledLayer rewardLayer;
    protected TiledLayer obstacleLayer;
    protected TiledLayer backgroundLayer;
    
    protected int[] worldMap;
    
    protected boolean trapsPresent;
    
    protected Vector enemies;
    protected Vector rockets;

    protected Vector enemyBlocks;
    protected Vector rungs;
    
    private Game game;
    
    /**
     * Constructor.
     */
    public AbstractLevel()
    {
        caught = false;
        complete = false;
        
        baconCount = 0;
        
        trapsPresent = false;
        
        enemies = new Vector();
        rockets = new Vector();
        enemyBlocks = new Vector();
        rungs = new Vector();

        game = null;
    }
    
    /** Initializes the level -- invoked by Game */
    abstract public void init();

    /** Deploys enemies for a given level */
    abstract protected void deployEnemies();
    
    abstract public void addRocket(int x, int y, int dir);
    
    /**
     * Initializes background, maze, and reward layers and the goal.
     * 
     * @param map
     *            1D map
     */
    public void setCells(int[] mapWorld)
    {
        this.worldMap = mapWorld;
        
        try
        {
            // Load in the goal
            Image goalImg = Image.createImage("/resources/images/CQ_exit.png");
            
            goal = new Sprite(goalImg);
            
            createBackground();
            
            createLayers();
            
            createPlatforms(mapWorld);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /** Creates the backgroud layer. */
    protected void createBackground() throws Exception
    {
        backgroundLayer = new TiledLayer(
                Rid.BG_COLS, Rid.BG_ROWS,
                Image.createImage("/resources/images/background_160.png"),
                BG_TILE_WIDTH, BG_TILE_HEIGHT);
        
        // Setup the background tiled layer map -- all 1's
        for (int row = 0; row < Rid.BG_ROWS; row++)
        {
            for (int col = 0; col < Rid.BG_COLS; col++)
            {
                backgroundLayer.setCell(col, row, Rid.RID_BG);
            }
        }
    }
    
    /** Creates the map and reward layers. */
    protected void createPlatforms(int[] mapWorld) throws Exception
    {
        // Populate the map
        for (int row = 0; row < WORLD_TILE_ROWS; row++)
        {
            for (int col = 0; col < WORLD_TILE_COLS; col++)
            {
                // Extract the tile
                int tile = getCell(mapWorld, col, row);
                
                // If we find PlayerCharacter don't set this tile -- it'll be own sprite
                if (tile == Rid.RID_PC)
                {
                    Trace.print("createPlatforms: got RoboDog's pos col=" + col
                            + " row=" + row);
                    
                    pcInitPos = new Point(col * TILE_WIDTH, row
                            * TILE_HEIGHT);
                }
                // Ditto...if we find the goal, position it.
                else if (tile == Rid.RID_GOAL)
                {
                    goal.setPosition(col * TILE_WIDTH, row * TILE_HEIGHT);
                }
                // Load tile into the collisions or reward layers
                else if (isBacon(tile))
                {
                    rewardLayer.setCell(col, row, tile);
                    
                    Trace.print("bacon: " + baconCount +
                            " (" + (col * TILE_WIDTH) +
                            " " + (row * TILE_HEIGHT) + ")");
                    
                    baconCount++;
                }
                else if (isRocket(tile))
                {
                    rewardLayer.setCell(col, row, tile);

                    Trace.print("Rocket: " + " (" + (col * TILE_WIDTH) +
                            " " + (row * TILE_HEIGHT) + ")");
                }
                else if (tile == Rid.RID_ENEMY_BLOCK || isEnemy(tile))
                {
                    // This is handled by deployEnemies method
                    // enemyBlocks.addElement(new
                    // Point(col*TILE_WIDTH,row*TILE_HEIGHT));
                }
                else if (isTrap(tile))
                {
                    obstacleLayer.setCell(col, row, tile);
                    trapsPresent = true;
                }
                else
                {
                    worldLayer.setCell(col, row, tile);
                }
                
                if (isLadda(tile))
                {
                    rungs.addElement(
                            new Point(col * TILE_WIDTH, row * TILE_HEIGHT));
                }
                
                // Set the collision layer
                if (isPlatform(tile) || tile == Rid.RID_WALL)
                {
                    collisionLayer.setCell(col, row, Rid.RID_BLOCK_PLATFORM);
                }
                else
                {
                    collisionLayer.setCell(col, row, Rid.RID_NO_TILE);
                }
            }
        }
        
        deployEnemies();
    }
    
    /**
     * Creates world, collision, and reward layers
     * 
     * @throws Exception
     */
    protected void createLayers() throws Exception
    {
        // Load in the tiles
        Image img = Image.createImage("/resources/tilesets/QC_tiles.png");
        
        worldLayer = new TiledLayer(
                WORLD_TILE_COLS, WORLD_TILE_ROWS,
                img,
                TILE_WIDTH, TILE_HEIGHT);
        
        collisionLayer = new TiledLayer(
                WORLD_TILE_COLS, WORLD_TILE_ROWS,
                img,
                TILE_WIDTH, TILE_HEIGHT);
        
        rewardLayer = new TiledLayer(
                WORLD_TILE_COLS, WORLD_TILE_ROWS,
                img,
                TILE_WIDTH, TILE_HEIGHT);
        
        obstacleLayer = new TiledLayer(
                WORLD_TILE_COLS, WORLD_TILE_ROWS,
                img,
                TILE_WIDTH, TILE_HEIGHT);
    }
    
    protected int[] readMapFromCSV(String path) throws IOException
    {
        String fullPath = BASE_LEVEL_CSV_PATH + path;
        
        String fileData = Util.readTextFile(fullPath);
        
        Vector tilesVector = new Vector();
        
        StringTokenizer st = new StringTokenizer(fileData, ',');
        
        while (st.hasMoreChars())
        {
            // trim whitespace and the included delim

            String token = st.nextToken().trim();
            int delimIndex = token.lastIndexOf(',');

            if (delimIndex < 0)
            {
                delimIndex = token.length();
            }

            if (delimIndex > 0)
            {
                String numberPortion = token.substring(0, delimIndex);
                
                int tile = Integer.parseInt(numberPortion);

                tilesVector.addElement(new Integer(tile));
            }
        }
        
        int[] tiles = new int[tilesVector.size()];

        for (int i = 0; i < tiles.length; i++)
        {
            tiles[i] = ((Integer) tilesVector.elementAt(i)).intValue();
        }
        
        return tiles;
    }
    
    /** Opens the trap doors. */
    public void openTraps()
    {
        
    }
    
    /**
     * Gets the maze layer.
     * 
     * @return Maze layer.
     */
    public TiledLayer getWorldLayer()
    {
        return worldLayer;
    }
    
    /**
     * Gets the collision layer
     * 
     * @return Collision layer
     */
    public TiledLayer getCollisionLayer()
    {
        return collisionLayer;
    }
    
    /**
     * Gets the background layer.
     * 
     * @return Background layer
     */
    public Layer getBackgroundLayer()
    {
        return backgroundLayer;
    }
    
    /**
     * Gets the reward layer.
     * 
     * @return Reward layer.
     */
    public TiledLayer getRewardLayer()
    {
        return rewardLayer;
    }
    
    public TiledLayer getObstacleLayer()
    {
        return obstacleLayer;
    }
    
    /**
     * Get the goal sprite.
     * 
     * @return Goal sprite
     */
    public Layer getGoal()
    {
        return goal;
    }
    
    /**
     * Checks if enemy collides with walls, blocks, or PlayerCharacter
     * 
     * @param agent
     *            Enemy
     * @return True only if there's a collision.
     */
    public boolean collide(Enemy agent)
    {
        return agent.getSprite().collidesWith(worldLayer, false);
    }
    
    /**
     * Returns true if the level is complete, i.e., player reaches goal.
     * 
     * @return True or false.
     */
    public boolean isComplete()
    {
        return complete;
    }
    
    /**
     * Returns true if the player is caugt on the level.
     * 
     * @return True or false
     */
    public boolean isPlayerCaught()
    {
        return caught;
    }
    
    /**
     * Sets the player caught state.
     * 
     * @param tf
     *            True or false
     */
    public void setPlayerCaught(boolean newState)
    {
        caught = newState;
    }
    
    /**
     * Gets a tile given the map and a row, col
     * 
     * @param map
     *            Map (usually from Mappy)
     * @param col
     *            Column
     * @param row
     *            Row
     * @return Tile number
     */
    public int getCell(int[] map, int col, int row)
    {
        if (row < 0 || row >= WORLD_TILE_ROWS || col < 0
                || col >= WORLD_TILE_COLS)
        {
            return Rid.RID_NO_TILE;
        }
        
        int tile = map[row * WORLD_TILE_COLS + col];
        
        return tile;
    }
    
    /**
     * Gets a tile given the world map
     * 
     * @param col
     *            Column
     * @param row
     *            Row
     * @return Tile
     */
    public int getCell(int col, int row)
    {
        return getCell(worldMap, col, row);
    }
    
    /**
     * Sets a cell with a tile number given a map.
     * 
     * @param map
     *            Map (usually from Mappy)
     * @param col
     *            Column
     * @param row
     *            Row
     * @return Tile number
     */
    public void setCell(int[] map, int col, int row, int tile)
    {
        if (row < 0 || row >= WORLD_TILE_ROWS || col < 0
                || col >= WORLD_TILE_COLS)
        {
            return;
        }
        
        map[row * WORLD_TILE_COLS + col] = tile;
    }
    
    /**
     * Updates the level
     * 
     */
    public void update()
    {
        // Does nothing for now but if goal were animated
        // it would be done here
    }
    
    /**
     * Gets PlayerCharacter's initial position in the world.
     * 
     * @return
     */
    public Point getPCPosition()
    {
        return pcInitPos;
    }
    
    /**
     * Gets the world width in pixels
     * 
     * @return Width
     */
    public int getWidth()
    {
        return backgroundLayer.getWidth();
    }
    
    /**
     * Gets the world height in pixels
     * 
     * @return Height
     */
    public int getHeight()
    {
        return backgroundLayer.getHeight();
    }
    
    /**
     * Gets the Player Character from the level
     * 
     * @return PlayerCharacter
     */
    public PlayerCharacter getPC()
    {
        return pc;
    }
    
    /**
     * Sets the player character for the level.
     * 
     * @param pc
     *            PlayerCharacter
     */
    public void setPC(PlayerCharacter pc)
    {
        this.pc = pc;
    }
    
    /**
     * Gets enemies on this level
     * 
     * @return Vector of sprites
     */
    public Vector getEnemies()
    {
        return enemies;
    }
    public Vector getRockets()
    {
        return rockets;
    }
    /**
     * Gets the bacon count.
     * 
     * @return count
     */
    public int getBaconCount()
    {
        return baconCount;
    }
    
    /**
     * Returns true if trap doors are present
     * 
     * @return True or false
     */
    public boolean areTrapsPresent()
    {
        return trapsPresent;
    }
    
    /**
     * Resets the level.
     * 
     */
    public void reset()
    {
        caught = false;
    }
    
    /**
     * Returns true if a tile is bacon!
     * 
     * @param tile
     *            Tile number
     * @return True or false
     */
    public boolean isBacon(int tile)
    {
        if (tile == Rid.RID_BACON)
        {
            return true;
        }
        
        return false;
    }

    public boolean isRocket(int tile)
    {
        if (tile == Rid.RID_ROCKET)
        {
            return true;
        }

        return false;
    }
    
    /**
     * Returns true if tile is a platform
     * 
     * @param tile
     *            Tile number
     * @return True or false
     */
    public boolean isPlatform(int tile)
    {
        for (int i = 0; i < Rid.RID_PLATFORMS.length; i++)
        {
            if (tile == Rid.RID_PLATFORMS[i])
            {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Returns true if tile is an enemy.
     * 
     * @param tile
     *            Tile number
     * @return True or false
     */
    public boolean isEnemy(int tile)
    {
        return (tile == Rid.RID_ENEMY);
    }
    
    /**
     * Return true if tile is a ladda'.
     * 
     * @param tile
     *            Tile number
     * @return True or false
     */
    public boolean isLadda(int tile)
    {
        for (int j = 0; j < Rid.RID_LADDAS.length; j++)
        {
            if (tile == Rid.RID_LADDAS[j])
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isTrap(int tile)
    {
        return Util.arrayContains(Rid.RID_TRAPS, tile);
    }
    
    /**
     * Gets the ladda rungs
     * 
     * @return
     */
    public Vector getRungs()
    {
        return rungs;
    }
    
    /**
     * Gets enemy (sprite) blocks.
     * 
     * @return Sprite blocks
     */
    public Vector getEnemyBlocks()
    {
        return enemyBlocks;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }
    
    public Game getGame()
    {
        return game;
    }
}

