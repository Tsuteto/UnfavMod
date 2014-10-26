package tsuteto.unfavmod;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import org.apache.logging.log4j.Level;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Date;

public class TweetManager
{
    private static TweetManager instance;

    private TwitterController controller;
    private int tweetsLeft = 0;

    public static void createInstance(TwitterController controller)
    {
        instance = new TweetManager(controller);
    }

    public static TweetManager instance()
    {
        return instance;
    }

    private TweetManager(TwitterController controller) {
        this.controller = controller;
    }

    public void tweet(String format, Object... data) throws TwitterException
    {
        if (UnfavMod.enableTweet && (tweetsLeft > 0 || isTweetNoLimit()))
        {
            try
            {
                controller.getTwitter().updateStatus(String.format(format, data) + " #UnfavMod");

                if (!isTweetNoLimit())
                {
                    tweetsLeft--;
                }
            }
            catch (TwitterException e)
            {
                if (e.getErrorCode() == 187)
                {
                    ModLog.log(Level.WARN, "A duplicate tweet");
                }
                else
                {
                    throw e;
                }
            }

            if (tweetsLeft == 1 && !isTweetNoLimit())
            {
                this.onReachLimit();
            }
        }
    }

    private void onReachLimit()
    {
        Minecraft mc = FMLClientHandler.instance().getClient();

        UnfavMod.instance().getTwitterController().stopTwitterStream();

        if (UnfavMod.enableTweetControl)
        {
            try
            {
                Twitter twitter = controller.getTwitter();
                tweet(UnfavMod.unfavTweetLimit,
                        twitter.showUser(twitter.getId()).getName(),
                        String.format("%tT", new Date()));
            }
            catch (TwitterException e)
            {
                ModLog.log(Level.WARN, e, "Failed to tweet about stopping");
            }
        }

        if (mc.thePlayer != null)
        {
            mc.thePlayer.sendChatMessage(StatCollector.translateToLocalFormatted("unfavmod.reachedLimit"));
        }
    }

    public void setTweetsLeft(int num)
    {
        this.tweetsLeft = num;
    }

    private boolean isTweetNoLimit()
    {
        return UnfavMod.tweetLimit <= 0;
    }
}
