package game.util;

import com.mobigenix.dogz.screen.AbstractOptions;
import com.mobigenix.dogz.screen.Blotter;
import java.io.IOException;
import java.io.InputStream;


public class Util
{
    public final static String GAME_NAME = "Cholesterol Quest!";
    public final static String VERSION = "0.1 Alfa";
    public final static String COPYRIGHT = "© 2009 by US [with nukes]";
    
    // BE SURE TO UPDATE THE COMMENTS IF THE COLOR VALUES CHANGE !!!
    public final static int DEFAULT_BACKGROUND_COLOR = 0x000000; // Black
    public final static int DEFAULT_FOREGROUND_COLOR = 0xC0C0C0; // Gray
    public final static int DEFAULT_HIGHLIGHT_COLOR = 0xF00000; // Red
    public final static int DEFAULT_LOWLIGHT_COLOR = 0x00CCCC; // Cyan
    
    public static void setDefaultScreenColors(AbstractOptions display)
    {
        //display.setBackground(DEFAULT_BACKGROUND_COLOR);
        display.setForeground(DEFAULT_FOREGROUND_COLOR);
        display.setHighLight(DEFAULT_HIGHLIGHT_COLOR);
        display.setLowLight(DEFAULT_LOWLIGHT_COLOR);
    }
    
    public static void setDefaultScreenColors(Blotter display)
    {
        display.setBgColor(DEFAULT_BACKGROUND_COLOR);
        display.setFgColor(DEFAULT_FOREGROUND_COLOR);
        display.setHighLight(DEFAULT_HIGHLIGHT_COLOR);
        display.setLowLight(DEFAULT_LOWLIGHT_COLOR);
    }
    
    public static int arrayIndexOf(int[] array, int value)
    {
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == value)
            {
                return i;
            }
        }
        
        return -1;
    }
    
    public static boolean arrayContains(int[] array, int value)
    {
        return (arrayIndexOf(array, value) >= 0);
    }
    
    public static int arrayIndexOf(Object[] array, Object value)
    {
        for (int i = 0; i < array.length; i++)
        {
            if (array[i].equals(value))
            {
                return i;
            }
        }
        
        return -1;
    }
    
    public static boolean arrayContains(Object[] array, Object value)
    {
        return (arrayIndexOf(array, value) >= 0);
    }
    
    public static String readTextFile(String path) throws IOException
    {
        InputStream in = Util.class.getResourceAsStream(path);
        StringBuffer buf = new StringBuffer();

        byte[] bytes = new byte[512];
        int bytesRead = in.read(bytes);

        while (bytesRead > 0)
        {
            buf.append(new String(bytes, 0, bytesRead));

            bytesRead = in.read(bytes);
        }

        return buf.toString();
    }
}

