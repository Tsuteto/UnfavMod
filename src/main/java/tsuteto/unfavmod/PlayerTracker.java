package tsuteto.unfavmod;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import org.lwjgl.input.Keyboard;

public class PlayerTracker
{

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;

        if (UnfavMod.autoStartup)
        {
            if (UnfavMod.instance().getTwitterController().getAuth().hasAccessToken())
            {
                UnfavMod.instance().startTwitterStream(player);
            }
            else
            {
                player.addChatMessage(new ChatComponentTranslation(
                        "unfavmod.needAuth", Keyboard.getKeyName(ModKeyHandler.controlKey.getKeyCode())));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        EntityPlayer player = event.player;

        if (UnfavMod.instance().getTwitterController().getAuth().hasAccessToken())
        {
            UnfavMod.instance().stopTwitterStream(player);
        }
    }
}
