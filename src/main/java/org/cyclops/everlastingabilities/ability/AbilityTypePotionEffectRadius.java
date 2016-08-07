package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.World;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;

import java.util.List;

/**
 * Ability type for potion effects in an area.
 * @author rubensworks
 */
public class AbilityTypePotionEffectRadius extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 2;

    private final Potion potion;

    public AbilityTypePotionEffectRadius(String id, EnumRarity rarity, int maxLevel, int baseXpPerLevel, Potion potion) {
        super(id, rarity, maxLevel, baseXpPerLevel);
        this.potion = potion;
    }

    protected int getDurationMultiplier() {
        return 3;
    }

    @Override
    public void onTick(EntityPlayer player, int level) {
        World world = player.worldObj;
        if (!world.isRemote && player.worldObj.getWorldTime() % TICK_MODULUS == 0) {
            int radius = level * 2;
            List<EntityLivingBase> mobs = world.getEntitiesWithinAABB(EntityLivingBase.class,
                    player.getEntityBoundingBox().expandXyz(radius), EntitySelectors.NOT_SPECTATING);
            for (EntityLivingBase mob : mobs) {
                if (mob != player && !player.isOnSameTeam(mob)) {
                    mob.addPotionEffect(
                            new PotionEffect(potion, TICK_MODULUS * getDurationMultiplier(), level - 1, true, false));
                }
            }
        }
    }
}
