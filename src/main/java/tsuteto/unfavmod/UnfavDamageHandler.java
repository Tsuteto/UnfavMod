package tsuteto.unfavmod;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.Level;
import twitter4j.TwitterException;

import java.util.LinkedList;

public class UnfavDamageHandler
{
    public static final LinkedList<UnfavInfo> unfavQueue = new LinkedList<UnfavInfo>();
    public static int unfavCount = 0;

    @SubscribeEvent
    public void tick(TickEvent event)
    {
        if (event.type == TickEvent.Type.CLIENT && event.phase == TickEvent.Phase.END)
        {
            doTick();
        }
    }

    private void doTick()
    {
        EntityPlayer clientPlayer = FMLClientHandler.instance().getClient().thePlayer;
        EntityPlayer player = null;

        if (clientPlayer != null)
        {
            player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(clientPlayer.getCommandSenderName());
        }

        synchronized (unfavQueue)
        {
            UnfavInfo unfav = unfavQueue.poll();

            if (unfav != null && player != null)
            {
                unfavCount++;

                String key = UnfavMod.showUserNameOnGame ? "unfavmod.unfav" : "unfavmod.unfav.anonymous";
                player.addChatMessage(new ChatComponentTranslation(key, unfav.name, unfavCount));

                if (player.isEntityAlive())
                {
                    if (player.capabilities.isCreativeMode)
                    {
                        this.tweet(unfav, UnfavMod.unfavTweetCreative);
                    } else if (!player.isBlocking())
                    {
                        this.tweet(unfav, UnfavMod.unfavTweetDamage);
                        player.attackEntityFrom(UnfavDamageSource.causeUnfavDamage(unfav), UnfavMod.unfavDamage);
                        player.hurtResistantTime = player.maxHurtResistantTime / 2;
                    } else
                    {
                        this.tweet(unfav, UnfavMod.unfavTweetBlocking);
                    }
                } else
                {
                    this.tweet(unfav, UnfavMod.unfavTweetDead);
                }
            }
        }
    }

    private void tweet(UnfavInfo unfav, String tweetFormat)
    {
        if (UnfavMod.enableTweetUnfav)
        {
            try
            {
                TweetManager.instance().tweet(tweetFormat, unfav.screenName, unfav.unfavdName, UnfavMod.unfavDamage, unfavCount);
            }
            catch (TwitterException e)
            {
                ModLog.log(Level.WARN, e, "Failed to tweet on damage");
            }
        }
    }
}
