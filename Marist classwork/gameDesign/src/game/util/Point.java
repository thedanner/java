package game.util;

public class Point
{
    protected int x;
    protected int y;
    
    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Gets x coordinate.
     * 
     * @return the x
     */
    public int getX()
    {
        return x;
    }
    
    /**
     * Sets x coordinate.
     * 
     * @param x
     *            the x to set
     */
    public void setX(int x)
    {
        this.x = x;
    }
    
    /**
     * Gets y coordinate.
     * 
     * @return the y
     */
    public int getY()
    {
        return y;
    }
    
    /**
     * Sets y coordinate.
     * 
     * @param y
     *            the y to set
     */
    public void setY(int y)
    {
        this.y = y;
    }
    
    public String toString()
    {
        return "(" + x + " " + y + ")";
    }
    
}
