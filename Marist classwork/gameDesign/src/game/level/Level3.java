package game.level;

import game.Game;
import game.entity.Enemy;
import game.entity.Rocket;
import game.util.Trace;
import java.io.IOException;
import java.util.Vector;

/**
 * 
 */
public class Level3 extends Level2
{
    private final static String LEVEL_FILE = "LEVEL4.CSV";
    
    /**
     * Constructor
     */
    public Level3()
    {
        super();

        rockets = new Vector();
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
     * Update the level.
     */
    public void update()
    {
        super.update();

        if (caught)
        {
            return;
        }
        
        for (int i = 0; i < rockets.size(); )
        {
            Rocket rocket = (Rocket) rockets.elementAt(i);
            
            if (!rocket.isDead())
            {
                rocket.update();
                i++;
            }
            else
            {
                this.rockets.removeElementAt(i);
                
                this.getGame().removeEntity(rocket);
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
        if (super.collide(enemy))
        {
            return true;
        }
        
        if (enemy instanceof Rocket && enemy.isAlive())
        {
            for (int i = 0; i < enemies.size(); i++)
            {
                Enemy otherEnemy = (Enemy) enemies.elementAt(i);
                
                if (    otherEnemy.isAlive() &&
                        otherEnemy.collidesWith(enemy.getSprite()))
                {
                    Trace.print("Rocket hit enemy!!");
                    
                    otherEnemy.setDying();
                    
                    Game.addScore(Game.POINTS_MONSTA);
                    
                    makeSound(Rid.RID_ENEMY);
                    
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * Tests of an enemy collides with a block, unless given
     * enemy is a rocket.
     *
     * @param enemy
     *            Enemy
     * @return True or false
     */
    protected boolean enemyBlockCollision(Enemy enemy)
    {
        if (enemy instanceof Rocket)
        {
            return false;
        }

        return super.enemyBlockCollision(enemy);
    }
    
    public void addRocket(int x, int y, int dir)
    {
        Rocket rocket = new Rocket(x, y, dir);
        
        rocket.setLevel(this);
        
        this.rockets.addElement(rocket);
        this.getGame().addProjectile(rocket);
    }
}
