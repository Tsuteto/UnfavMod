package tsuteto.unfavmod;

import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.Properties;

public class AuthProp extends Properties
{
    private File file;
    private boolean loaded = false;
    
    enum Keys
    {
        KEY_TOKEN("oauth.accessToken"),
        KEY_TOKEN_SECRET("oauth.accessTokenSecret"),
        ;
        
        public String key;
        
        Keys(String key)
        {
            this.key = key;
        }
    }
    
    public AuthProp(File file) throws IOException
    {
        super();
        this.file = file;
        
        if (file.exists())
        {
            InputStream is = null;
            
            try
            {
                is = new FileInputStream(file);
                this.load(is);
                this.loaded = true;
            }
            finally
            {
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException ignore)
                    {}
                }
            }
        }
    }
    
    public String getProperty(Keys key)
    {
        return getProperty(key.key);
    }
    
    public void setProperty(Keys key, String val)
    {
        setProperty(key.key, val);
    }
    
    public boolean loaded()
    {
        return this.loaded;
    }
    
    public void storeProp()
    {
        // Store tokens
        OutputStream os = null;
        
        try
        {
            os = new FileOutputStream(file);
            this.store(os, "Auth prop for UnfavMod");
            os.close();
            
            this.loaded = true;
            ModLog.log(Level.INFO, "Successfully stored access token to " + file.getAbsolutePath() + ".");
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException ignore)
                {}
            }
        }
    }

}
