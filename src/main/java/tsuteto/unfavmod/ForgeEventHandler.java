package tsuteto.unfavmod;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.apache.logging.log4j.Level;
import twitter4j.TwitterException;

public class ForgeEventHandler
{

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if (UnfavMod.enableTweet && UnfavMod.enableTweetDeath)
        {
            if (event.source instanceof UnfavDamageSource && event.entityLiving instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer)event.entityLiving;
                try
                {
                    UnfavInfo unfav = ((UnfavDamageSource)event.source).getUnfav();
                    TweetManager.instance().tweet(UnfavMod.unfavTweetDeath,
                            unfav.screenName, unfav.unfavdName, UnfavDamageHandler.unfavCount);
                }
                catch (TwitterException e)
                {
                    ModLog.log(Level.WARN, e, "Failed to tweet on death");
                }
            }
        }
    }

}
