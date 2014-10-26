package tsuteto.unfavmod;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;

public class UnfavDamageSource extends DamageSource
{
    private UnfavInfo unfav;

    protected UnfavDamageSource(String par1Str, UnfavInfo unfav)
    {
        super(par1Str);
        this.unfav = unfav;
        this.setProjectile();
    }

    /**
     * returns EntityDamageSourceIndirect of an arrow
     */
    public static DamageSource causeUnfavDamage(UnfavInfo unfav)
    {
        return (new UnfavDamageSource("unfav", unfav));
    }

    @Override
    public IChatComponent func_151519_b(EntityLivingBase par1EntityLiving) // getDeathMessage
    {
        String key = UnfavMod.showUserNameOnGame ? "death.attack." + this.damageType : "death.attack." + this.damageType + ".anonymous";
        return new ChatComponentTranslation(key, unfav.name, par1EntityLiving.getCommandSenderName());
    }

    public UnfavInfo getUnfav()
    {
        return unfav;
    }
}
