package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.GeneralConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Ability type for potion effects in an area.
 * @author rubensworks
 */
public class AbilityTypePotionEffectRadius extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 2;

    private final Potion potion;
    private boolean hostile;

    public AbilityTypePotionEffectRadius(String id, int rarity, int maxLevel, int baseXpPerLevel, Potion potion, boolean isHostile) {
        super(id, rarity, maxLevel, baseXpPerLevel);
        this.potion = potion;
        this.hostile = isHostile;
        if (this.potion == null) {
            EverlastingAbilities.clog(Level.WARN, "Tried to register a null potion for ability " + id + ". This is possibly caused by a mod forcefully removing the potion effect for this ability.");
        }
    }
    public AbilityTypePotionEffectRadius(String id, int rarity, int maxLevel, int baseXpPerLevel, Potion potion) {
        this(id, rarity, maxLevel, baseXpPerLevel, potion, true);
    }

    protected int getDurationMultiplier() {
        return 6;
    }

    @Override
    public void onTick(EntityPlayer player, int level) {
        World world = player.world;
        if (potion != null && !world.isRemote && player.world.getTotalWorldTime() % TICK_MODULUS == 0) {
            int radius = level * 2;
            List<EntityLivingBase> mobs = world.getEntitiesWithinAABB(EntityLivingBase.class,
                    player.getEntityBoundingBox().grow(radius, radius, radius), EntitySelectors.NOT_SPECTATING);
            for (EntityLivingBase mob : mobs) {
                if (!(this.hostile && isFriendlyMob(mob, player))) {
                    mob.addPotionEffect(new PotionEffect(potion, TICK_MODULUS * getDurationMultiplier(), level - 1, true, false));
                }
            }
        }
    }

    private static String[] friendlyMobs = null;
    static boolean isFriendlyMob(EntityLivingBase mob, EntityPlayer player) {
        ResourceLocation resourceLocation = mob instanceof EntityPlayer
                ? new ResourceLocation("player") : EntityList.getKey(mob);
        String mobName = resourceLocation == null ? "null" : resourceLocation.toString();
        return (mob == player ||
                player.isOnSameTeam(mob) ||
                (mob instanceof IEntityOwnable && ((IEntityOwnable) mob).getOwner() == player) ||
                Arrays.stream(friendlyMobs).anyMatch(mobName::matches));
    }

    public static void loadBlacklist(String[] mobNames) {
        friendlyMobs = mobNames;
    }
}
