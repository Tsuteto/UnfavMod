package tsuteto.unfavmod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Date;

@Mod(modid = UnfavMod.modid, name = "あんふぁぼられったーMOD (UnfavMod)", version = "1.0.6-MC1.7.2")
public class UnfavMod
{
    public static final String modid = "UnfavMod";

    @Mod.Instance(modid)
    private static UnfavMod instance;

    public static boolean enableTweet;
    public static boolean enableTweetDeath;
    public static boolean enableTweetUnfav;
    public static boolean enableTweetControl;

    public static String unfavTweetStart;
    public static String unfavTweetStop;
    public static String unfavTweetDamage;
    public static String unfavTweetDeath;
    public static String unfavTweetDead;
    public static String unfavTweetCreative;

    public static boolean showUserNameOnGame;
    public static int unfavDamage;
    public static int tweetLimit;
    public static boolean autoStartup;
    public static boolean debug;

    public static String unfavTweetBlocking;
    public static String unfavTweetLimit;

    private TwitterController twitterController;



    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ModLog.modId = modid;

        Configuration conf = new Configuration(event.getSuggestedConfigurationFile());
        conf.load();

        enableTweet = conf.get("twitter", "enableTweet", true,
                "If true, the mod will tweet to your account.").getBoolean(true);
        enableTweetDeath = conf.get("twitter", "enableTweetOnUnfavDeath", true,
                "If true, the mod will also tweet when you died of unfavorite.").getBoolean(true);
        enableTweetUnfav = conf.get("twitter", "enableTweetOnUnfav", true,
                "If true, the mod will also tweet when you got an unfavorite.").getBoolean(true);
        enableTweetControl = conf.get("twitter", "enableTweetOnControl", true,
                "If true, the mod will also tweet when you start/stop the mod.").getBoolean(true);

        unfavTweetStart = conf.get("twitter", "tweetOnStart", "あんふぁぼられったーMODを起動しました。あんふぁぼってね♡ - %2$s",
                "Text to be tweeted when the mod started up. %1$s=your profile name, %2$s=current time").getString();
        unfavTweetStop = conf.get("twitter", "tweetOnStop", "あんふぁぼられったーMODを終了しました - %2$s",
                "Text to be tweeted when the mod stopped. %1$s=your profile name, %2$s=current time").getString();
        unfavTweetLimit = conf.get("twitter", "tweetOnLimit", "ツイート上限到達！これにてあんふぁぼられったーMOD終了です！ - %2$s",
                "Text to be tweeted when tweets reached the limit. %1$s=your profile name, %2$s=current time").getString();

        unfavTweetDamage = conf.get("twitter", "tweetOnUnfavDamage", "@%1$s %2$sはあんふぁぼられて%3$dのダメージ！ #%4$d",
                "Text to be tweeted when you took damage from unfavorite. %1$s=screen name of unfav user, %2$s=your profile name, %3$d=damage qty., %4$d=unfav counter").getString();
        unfavTweetBlocking = conf.get("twitter", "tweetOnUnfavDefended", "@%1$s %2$sはあんふぁぼを防御した！ #%4$d",
                "Text to be tweeted when you defended an unfavorite. %1$s=screen name of unfav user, %2$s=your profile name, %3$d=damage qty., %4$d=unfav counter").getString();
        unfavTweetDeath = conf.get("twitter", "tweetOnUnfavDeath", "%2$sはあんふぁぼられて死んでしまった！ #%3$d",
                "Text to be tweeted when you died of unfavorite. %1$s=screen name of unfav user, %2$s=your profile name, %3$d=unfav counter").getString();
        unfavTweetDead = conf.get("twitter", "tweetOnUnfavDead", "@%1$s 返事がない　%2$sはただの屍のようだ #%4$d",
                "Text to be tweeted when you got an unfavorite during death. %1$s=screen name of unfav user, %2$s=your profile name, %3$d=damage qty., %4$d=unfav counter").getString();
        unfavTweetCreative = conf.get("twitter", "tweetOnCreative", "@%1$s %2$sはクリエイティブモードで遊んでいる #%4$d",
                "Text to be tweeted when you got an unfavorite during death. %1$s=screen name of unfav user, %2$s=your profile name, %3$d=damage qty., %4$d=unfav counter").getString();

        unfavDamage = conf.get("game", "damage", 2,
                "Damage quantity when you take damage from an unfavorite.").getInt();
        showUserNameOnGame = conf.get("game", "showUserNameOnGame", true,
                "If true, the mod shows the profile name who unfavorited you on the game screen.").getBoolean(true);

        autoStartup = conf.get("general", "autoStartup", true,
                "If true, this mod will activate when you start the game; or you need manually strike a key to start - See the control settings in the game.").getBoolean(true);

        tweetLimit = conf.get("general", "tweetLimit", 50,
                "Limit number of tweets for automatic shut-down. 0 or a negative for no limit.").getInt();

        debug = conf.get("general", "debug", false).getBoolean(false);

        ModLog.isDebug = debug;

        conf.save();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(new ModKeyHandler());
        FMLCommonHandler.instance().bus().register(new UnfavDamageHandler());
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
        FMLCommonHandler.instance().bus().register(new PlayerTracker());

        this.twitterController = new TwitterController();
        TweetManager.createInstance(this.twitterController);
    }

    public void startTwitterStream(EntityPlayer player)
    {
        if (twitterController.isTwitterStreamWorking())
        {
            return;
        }

        if (!twitterController.startTwitterStream())
        {
            player.addChatMessage(new ChatComponentTranslation("unfavmod.failedStart"));
            return;
        }

        TweetManager.instance().setTweetsLeft(tweetLimit);

        Twitter twitter = twitterController.getTwitter();

        try
        {
            if (enableTweetControl)
            {
                TweetManager.instance().tweet(unfavTweetStart,
                        twitter.showUser(twitter.getId()).getName(),
                        String.format("%tT", new Date()));
            }
            if (player != null)
            {
                player.addChatMessage(new ChatComponentTranslation("unfavmod.started", twitter.getScreenName()));
            }
        }
        catch (TwitterException e)
        {
            ModLog.log(Level.WARN, e, "Failed to start twitter streaming");
            player.addChatMessage(new ChatComponentTranslation("unfavmod.failedStart"));
        }
    }

    public void stopTwitterStream(EntityPlayer player)
    {
        if (!twitterController.isTwitterStreamWorking())
        {
            return;
        }

        if (!twitterController.stopTwitterStream())
        {
            player.addChatMessage(new ChatComponentTranslation("unfavmod.failedStart"));
            return;
        }

        if (enableTweetControl)
        {
            try
            {
                Twitter twitter = twitterController.getTwitter();
                TweetManager.instance().tweet(unfavTweetStop, twitter.showUser(twitter.getId()).getName(),
                        String.format("%tT", new Date()));
            }
            catch (TwitterException e)
            {
                ModLog.log(Level.WARN, e, "Failed to tweet about stopping");
            }
        }

        if (player != null)
        {
            player.addChatMessage(new ChatComponentTranslation("unfavmod.stopped"));
        }
    }

    public static UnfavMod instance()
    {
        return instance;
    }

    public TwitterController getTwitterController()
    {
        return this.twitterController;
    }

    public boolean isTwitterReady()
    {
        return twitterController.getAuth().hasAccessToken();
    }
}
