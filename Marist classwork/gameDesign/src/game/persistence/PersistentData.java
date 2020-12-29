package game.persistence;

import game.persistence.recordStore.RecordStoreException;

public abstract class PersistentData
{
    protected RecordStoreFacade rs;
    protected String recordStoreName;

    protected PersistentData(String recordStoreName)
    {
        this.recordStoreName = recordStoreName;

        this.init();
    }
    
    private void init()
    {
        try
        {
            rs = new RecordStoreFacade(this.recordStoreName);
            
            initWithDefaults();
        }
        catch (RecordStoreException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void recreate()
    {
        try
        {
            RecordStore.deleteRecordStore(this.recordStoreName);
        }
        catch (RecordStoreException ex)
        {
            ex.printStackTrace();
        }
        
        this.init();
    }
	
	/**
	 * This should initialize the cache instance variables if they were saved.
	 * If not, it should set the default variables in a manner so they will be
	 * saved to persistent storage.
	 */
	abstract protected void initWithDefaults();
}
