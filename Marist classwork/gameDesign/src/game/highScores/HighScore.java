package game.highScores;

/**
 * 
 */
public class HighScore
{
    private String name;
    private int score;

    public HighScore(String name, int score)
    {
        this.name = name;
        this.score = score;
    }

    public String getName()
    {
        return name;
    }

    public int getScore()
    {
        return score;
    }

    public String toString()
    {
        return name + "=" + score;
    }
}
