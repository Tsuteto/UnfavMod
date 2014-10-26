package tsuteto.unfavmod;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class ModKeyHandler
{
    public static KeyBinding controlKey = new KeyBinding("key.unfavmod.control", Keyboard.KEY_U, "key.categories.unfavmod");

    public ModKeyHandler()
    {
        ClientRegistry.registerKeyBinding(controlKey);
    }

    @SubscribeEvent
    public void keyDown(InputEvent.KeyInputEvent event)
    {
        Minecraft mc = FMLClientHandler.instance().getClient();
        
        if (mc.currentScreen == null && mc.thePlayer != null && controlKey.isPressed())
        {
            if (UnfavMod.instance().isTwitterReady())
            {
                if (UnfavMod.instance().getTwitterController().isTwitterStreamWorking())
                {
                    UnfavMod.instance().stopTwitterStream(mc.thePlayer);
                }
                else
                {
                    UnfavMod.instance().startTwitterStream(mc.thePlayer);
                }
            }
            else
            {
                mc.displayGuiScreen(new GuiTwitterAuth());
            }
        }
    }

}
