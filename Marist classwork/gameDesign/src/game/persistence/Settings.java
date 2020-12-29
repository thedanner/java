package game.persistence;

import game.Game;

public class Settings extends PersistentData
{
    private static final String RECORD_STORE_NAME = "mga1_tortola_settings";
    
    private static final String KEY_AUDIO_ENABLED = "audio.enabled";
    private static final boolean DEFAULT_AUDIO_ENABLED = true;
    
    private static final String KEY_DIFFICULTY = "game.difficulty";
    private static final int DEFAULT_DIFFICULTY = Game.DIFFICULTY_EASY;
    
    public Settings()
    {
        super(RECORD_STORE_NAME);
    }
    
    protected void initWithDefaults()
    {
        if (!rs.hasKey(KEY_AUDIO_ENABLED))
        {
            setAudioEnabled(DEFAULT_AUDIO_ENABLED);
        }
        
        if (!rs.hasKey(KEY_DIFFICULTY))
        {
            setDifficulty(DEFAULT_DIFFICULTY);
        }
    }
    
    public void setAudioEnabled(boolean newState)
    {
        if (    !rs.hasKey(KEY_AUDIO_ENABLED) ||
                isAudioEnabled() != newState)
        {
            rs.putBoolean(KEY_AUDIO_ENABLED, newState);
        }
    }
    
    public boolean isAudioEnabled()
    {
        return rs.getBoolean(KEY_AUDIO_ENABLED);
    }
    
    public void setDifficulty(int newDifficulty)
    {
        if (!rs.hasKey(KEY_DIFFICULTY))
        {
            rs.putInt(KEY_DIFFICULTY, newDifficulty);
        }
        else if (   Game.DIFFICULTY_EASY == newDifficulty ||
                    Game.DIFFICULTY_HARD == newDifficulty )
        {
            if (getDifficulty() != newDifficulty)
            {
                rs.putInt(KEY_DIFFICULTY, newDifficulty);
            }
        }
        else
        {
            throw new IllegalArgumentException(
                    "invalid difficulty: " + newDifficulty);
        }
    }
    
    public int getDifficulty()
    {
        return rs.getInt(KEY_DIFFICULTY);
    }
}
