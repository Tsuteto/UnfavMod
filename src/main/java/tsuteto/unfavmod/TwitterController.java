package tsuteto.unfavmod;

import org.apache.logging.log4j.Level;
import twitter4j.*;

public class TwitterController
{
    private Twitter twitter;
    private TwitterStream twitterStream;
    private TwitterAuthorization auth;

    public TwitterController()
    {
        this.auth = new TwitterAuthorization();
    }

    public boolean startTwitterStream()
    {
        if (twitterStream != null) return false;

        try
        {
            if (twitter == null)
            {
                Twitter twitter = new TwitterFactory().getInstance();
                twitter.setOAuthConsumer(TwitterAuthorization.consumerKey, TwitterAuthorization.consumerKeySecret);
                twitter.setOAuthAccessToken(auth.getAccessToken());
                this.twitter = twitter;
            }

            twitterStream = new TwitterStreamFactory().getInstance();
            twitterStream.setOAuthConsumer(TwitterAuthorization.consumerKey, TwitterAuthorization.consumerKeySecret);
            twitterStream.setOAuthAccessToken(auth.getAccessToken());
            twitterStream.addListener(new ModUserStreamListener(twitter));
            twitterStream.user();

            ModLog.log(Level.INFO, "Started twitter streaming");
            return true;
        }
        catch (TwitterException e)
        {
            ModLog.log(Level.WARN, e, "Failed to start twitter streaming");
            return false;
        }
    }

    public boolean stopTwitterStream()
    {
        if (twitterStream == null) return false;

        twitterStream.shutdown();
        twitterStream = null;

        synchronized (UnfavDamageHandler.unfavQueue)
        {
            UnfavDamageHandler.unfavQueue.clear();
        }

        ModLog.log(Level.INFO, "Stopped twitter streaming");
        return true;
    }

    public TwitterAuthorization getAuth()
    {
        return auth;
    }

    public Twitter getTwitter()
    {
        return this.twitter;
    }

    public boolean isTwitterStreamWorking()
    {
        return twitterStream != null;
    }
}
