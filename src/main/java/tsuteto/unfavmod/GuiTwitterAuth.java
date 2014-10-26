package tsuteto.unfavmod;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;

public class GuiTwitterAuth extends GuiScreen
{
    private GuiTextField textboxPin;
    private String statusMsgKey;
    private String pin;

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, StatCollector.translateToLocal("done")));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, StatCollector.translateToLocal("gui.cancel")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, 45, 200, 20, StatCollector.translateToLocal("unfavmod.twitterauth.jumpToWeb")));

        this.textboxPin = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 100, 200, 20);

        statusMsgKey = "unfavmod.twitterauth.guide.1";
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {

        if (par1GuiButton.enabled)
        {
            switch (par1GuiButton.id)
            {
            case 0:
                boolean hasSucceeded = UnfavMod.instance().getTwitterController().getAuth().grantAccess(this.pin);
                if (hasSucceeded)
                {
                    this.mc.displayGuiScreen(null);
                    this.mc.thePlayer.addChatMessage(new ChatComponentTranslation("unfavmod.twitterauth.granted"));

                    UnfavMod.instance().startTwitterStream(mc.thePlayer);
                }
                else
                {
                    statusMsgKey = "unfavmod.twitterauth.guide.failed";
                }
                break;
            case 1:
                this.mc.displayGuiScreen(null);
                break;
            case 2:
                UnfavMod.instance().getTwitterController().getAuth().openAuthorizationUrl();
                statusMsgKey = "unfavmod.twitterauth.guide.2";
                this.textboxPin.setFocused(true);
                break;
            }
        }
    }

    @Override
    protected void keyTyped(char par1, int par2)
    {
        if (this.textboxPin.isFocused())
        {
            this.textboxPin.textboxKeyTyped(par1, par2);
            this.pin = this.textboxPin.getText();
        }

        if (par1 == 13)
        {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.textboxPin.mouseClicked(par1, par2, par3);
   }

    @Override
    public void updateScreen()
    {
        this.textboxPin.updateCursorCounter();
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("unfavmod.twitterauth.title"), this.width / 2, 20, 16777215);

        this.drawString(this.fontRendererObj, StatCollector.translateToLocal("unfavmod.twitterauth.enterPin"), this.width / 2 - 100, 87, 10526880);
        this.textboxPin.drawTextBox();

        this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal(statusMsgKey), this.width / 2, 150, 0xffffff);

        super.drawScreen(par1, par2, par3);
    }
}
