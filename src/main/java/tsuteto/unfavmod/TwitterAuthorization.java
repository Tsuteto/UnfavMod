package tsuteto.unfavmod;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Level;
import tsuteto.unfavmod.AuthProp.Keys;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TwitterAuthorization
{
    public static final String consumerKey = "PMnDm6fGzNSeAfISjVoYaA";
    public static final String consumerKeySecret = "Ekd9VLIgywcnsolo47XEJO8ITSYA1q3EgvyLY48zXEU";
    public static final String propFile = "unfavmod-token.properties";

    private Twitter twitter = null;
    private AuthProp authProp;
    private AccessToken accessToken;

    public TwitterAuthorization()
    {
        try
        {
            this.authProp = new AuthProp(new File(Minecraft.getMinecraft().mcDataDir + "/config", propFile));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to load Twitter token file", e);
        }

        this.initTwitter();
    }

    public void initTwitter()
    {
        this.twitter = new TwitterFactory().getInstance();
        this.twitter.setOAuthConsumer(consumerKey, consumerKeySecret);
        this.updateAccessToken();

    }

    public void updateAccessToken()
    {
        if (authProp.loaded())
        {
            this.accessToken = new AccessToken(
                    authProp.getProperty(Keys.KEY_TOKEN),
                    authProp.getProperty(Keys.KEY_TOKEN_SECRET));
        }
        else
        {
            this.accessToken = null;
        }
    }

    public boolean grantAccess(String pin)
    {
        ModLog.debug("PIN code: " + pin);

        if (pin != null && pin.length() > 0)
        {
            try
            {
                this.accessToken = twitter.getOAuthAccessToken(pin);
            }
            catch (TwitterException te)
            {
                if (401 == te.getStatusCode())
                {
                    ModLog.log(Level.INFO, "Failed to get the access token.");
                }
                else
                {
                    te.printStackTrace();
                }
                return false;
            }
            ModLog.log(Level.INFO, "Got access token.");
            ModLog.log(Level.INFO, "Access token: " + accessToken.getToken());
            ModLog.log(Level.INFO, "Access token secret: " + accessToken.getTokenSecret());

            this.saveToken(accessToken);
            return true;
        }
        else
        {
            return false;
        }
    }

    private void saveToken(AccessToken at)
    {
        authProp.setProperty(Keys.KEY_TOKEN, at.getToken());
        authProp.setProperty(Keys.KEY_TOKEN_SECRET, at.getTokenSecret());
        authProp.storeProp();
    }

    public boolean openAuthorizationUrl()
    {
        this.initTwitter();

        RequestToken requestToken;

        try
        {
            requestToken = twitter.getOAuthRequestToken();
        }
        catch (TwitterException te)
        {
            ModLog.log(Level.WARN, te, "Failed to get accessToken");
            return false;
        }

        try
        {
            Desktop.getDesktop().browse(new URI(requestToken.getAuthorizationURL()));
            return true;
        }
        catch (UnsupportedOperationException ignore)
        {}
        catch (IOException ignore)
        {}
        catch (URISyntaxException e)
        {
            throw new AssertionError(e);
        }
        return false;
    }

    public AccessToken getAccessToken() throws TwitterException
    {
        return this.accessToken;
    }

    public boolean hasAccessToken()
    {
        try
        {
            return this.getAccessToken() != null;
        }
        catch (TwitterException e)
        {
            return false;
        }
    }

}