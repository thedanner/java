package game.highScores;

import game.persistence.PersistentData;
import game.persistence.RecordStoreFacade;
import java.util.Vector;
import javax.microedition.rms.RecordStoreException;

/**
 * Keys will be the time of day; values will be "score=name" format.
 */
public class HighScores extends PersistentData
{
    private static final String RECORD_STORE_NAME = "mga1_tortola_high_scores";
    
    private Vector highScores; // <HighScore>, ordered by score, descending
    
    public HighScores()
    {
        super(RECORD_STORE_NAME);
        
        highScores = new Vector();
        
        loadScoresFromStore();
    }
    
    protected void initWithDefaults()
    {
        // Not much to do here...
    }
    
    private void loadScoresFromStore()
    {
        String[][] scoreData = rs.getPairs();
        
        for (int i = 0; i < scoreData.length; i++)
        {
            try
            {
                String scoreEntry = scoreData[i][1];
                
                HighScore score = this.parseSocreEntry(scoreEntry);
                
                this.insertHighScore(score);
            }
            catch (NumberFormatException ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    public void add(String name, int score)
    {
        this.add(new HighScore(name, score));
    }
    
    private void add(HighScore score)
    {
        this.insertHighScore(score);
        
        this.writeScore(score);
    }
    
    private void insertHighScore(HighScore score)
    {
        int spot = -1;

        for (int i = 0; i < highScores.size() && (-1 == spot); i++)
        {
            HighScore currentScore = (HighScore) highScores.elementAt(i);

            if (score.getScore() >= currentScore.getScore())
            {
                spot = i;
            }
        }

        if (-1 == spot)
        {
            highScores.addElement(score);
        }
        else
        {
            highScores.insertElementAt(score, spot);
        }
    }

    private void writeScore(HighScore score)
    {
        String timeKey = Long.toString(System.currentTimeMillis());

        rs.put(timeKey, marshalHighScore(score));
    }
    
    public HighScore get(int scoreIndex)
    {
        return (HighScore) highScores.elementAt(scoreIndex);
    }
    
    public int size()
    {
        return highScores.size();
    }
    
    private String marshalHighScore(HighScore score)
    {
        return  score.getName() +
                RecordStoreFacade.KEY_VALUE_SEPARATOR +
                score.getScore();
    }
    
    private HighScore parseSocreEntry(String recordData)
    {
        int index = recordData.indexOf(RecordStoreFacade.KEY_VALUE_SEPARATOR);

        if (index >= 0)
        {
            try
            {
                String name = recordData.substring(0, index);
                int score = Integer.parseInt(recordData.substring(index + 1));
                
                return new HighScore(name, score);
            }
            catch (NumberFormatException ex)
            {
                ex.printStackTrace();
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public void clear()
    {
        highScores.removeAllElements();

        try
        {
            rs.removeAll();
        }
        catch (RecordStoreException ex)
        {
            ex.printStackTrace();
        }
    }
}
