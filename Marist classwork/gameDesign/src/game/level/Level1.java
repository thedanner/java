package game.level;

import java.io.IOException;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import game.Game;
import game.entity.Enemy;
import game.entity.Monster;

/**
 * Implements level 1
 * 
 * @author Ron Coleman
 * 
 */
public class Level1 extends Level0
{
    private final static String LEVEL_FILE = "LEVEL2.CSV";

    private Image enemyBlock;
    
    /**
     * Constructor
     */
    public Level1()
    {
        super();
        
        // Load the enemy block used to change enemy direction
        try
        {
            enemyBlock = Image.createImage("/resources/images/enemy_block.png");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes the level
     */
    public void init()
    {
        try
        {
            int[] world = readMapFromCSV(LEVEL_FILE);
            
            setCells(world);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * Deploy enemies into the world.
     */
    public void deployEnemies()
    {
        for (int row = 0; row < WORLD_TILE_ROWS; row++)
        {
            for (int col = 0; col < WORLD_TILE_COLS; col++)
            {
                int tile = getCell(worldMap, col, row);
                
                int x = col * TILE_WIDTH;
                int y = row * TILE_HEIGHT;
                
                if (tile == Rid.RID_ENEMY_BLOCK)
                {
                    Sprite block = new Sprite(enemyBlock, 16, 16);
                    block.setPosition(x, y);
                    enemyBlocks.addElement(block);
                }
                else if (tile == Rid.RID_ENEMY)
                {
                    enemies.addElement(new Monster(x, y));
                }
            }
        }
    }
    
    /**
     * Update the level.
     */
    public void update()
    {
        // Game updates, i.e., moves Grace and Grace, in turn, manages
        // collisions with platforms.
        
        // Grace-enemy collisions are processed by collide method (see below).
        
        // Level 0 tests if Grace moves collides with rewards or goal
        // -- so we invoke Level0.update.
        super.update();
        
        if (complete)
        {
            return;
        }
        
        // If PC did not complete the level, then update enemies on this level
        for (int i = 0; i < enemies.size(); )
        {
            Enemy enemy = (Enemy) enemies.elementAt(i);
            
            if (!enemy.isDead())
            {
                // Update enemy -- ultimately calls Level1.collide (see below)
                enemy.update();

                i++;
            }
            else
            {
                this.enemies.removeElementAt(i);
                
                this.getGame().removeEntity(enemy);
            }
        }
    }
    
    /**
     * Checks if enemy collides with something, invoked only by
     * Enemy.moveOnCondition
     * 
     * @param enemy
     *            Enemy
     * @see mga.entity.Enemy
     */
    public boolean collide(Enemy enemy)
    {
        // Check if enemy collided with a wall
        if (super.collide(enemy))
        {
            return true;
        }
        
        // Otherwise, check if enemy collides with Grace
        if (enemy.isAlive() && pc.collidesWith(enemy))
        {
            if (enemy instanceof Monster && landsOn(enemy))
            {
                enemy.setDying();
                
                Game.addScore(Game.POINTS_MONSTA);
                
                makeSound(Rid.RID_ENEMY);
                
                return false;
            }
            
            caught = true;

            return true;
        }
        
        // If enemy does not collide with PC or wall, check if it collides
        // with a block
        boolean collided = enemyBlockCollision(enemy);
        
        return collided;
    }
    
    /**
     * Tests of an enemy collides with a block.
     * 
     * @param enemy
     *            Enemy
     * @return True or false
     */
    protected boolean enemyBlockCollision(Enemy enemy)
    {
        for (int j = 0; j < enemyBlocks.size(); j++)
        {
            Sprite block = (Sprite) enemyBlocks.elementAt(j);
            
            if (enemy.collidesWith(block))
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Returns true if Grace is landing on the enemy -- assuming a collision
     * 
     * @param enemy
     *            Enemy
     * @return True or false
     */
    private boolean landsOn(Enemy enemy)
    {
        boolean graceAbove = pc.getY() < enemy.getY();
        
        boolean graceFalling = pc.getVelocityY() > 0;
        
        return graceAbove && graceFalling;
    }
}
