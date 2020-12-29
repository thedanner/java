/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game.util;

/**
 * Singleton wrapper for the Random class.
 * 
 * @author
 */
public class Rand
{
    private static java.util.Random rand;
    
    static
    {
        rand = new java.util.Random();
    }
    
    public static java.util.Random getInstance()
    {
        return rand;
    }
    
    public static int nextInt()
    {
        return rand.nextInt();
    }
    
    public static int nextInt(int limit)
    {
        return rand.nextInt(limit);
    }
}
