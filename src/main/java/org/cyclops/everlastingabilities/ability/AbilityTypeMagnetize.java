package org.cyclops.everlastingabilities.ability;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.GeneralConfig;

import java.util.List;

/**
 * Ability type for attracting items in the area.
 * @author rubensworks
 */
public class AbilityTypeMagnetize extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 20;

    public AbilityTypeMagnetize(String id, int rarity, int maxLevel, int baseXpPerLevel) {
        super(id, rarity, maxLevel, baseXpPerLevel);
    }

    @Override
    public void onTick(PlayerEntity player, int level) {
        World world = player.world;
        if (!world.isRemote && !player.isSneaking() && player.world.getGameTime() % TICK_MODULUS == 0) {
            // Center of the attraction
            double x = player.posX;
            double y = player.posY;
            double z = player.posZ;

            // Get items in calculated area.
            int area = level * 2;
            AxisAlignedBB box = new AxisAlignedBB(x, y, z, x, y, z).grow(area, area, area);
            List<Entity> entities = world.getEntitiesInAABBexcluding(player, box, new Predicate<Entity>() {

                @Override
                public boolean apply(Entity entity) {
                    return entity instanceof ItemEntity
                            || (GeneralConfig.magnetizeMoveXp && entity instanceof ExperienceOrbEntity);
                }

            });

            // Move all those items in the direction of the player.
            for(Entity moveEntity : entities) {
                if((moveEntity instanceof ItemEntity && !((ItemEntity) moveEntity).cannotPickup()
                        && canKineticateItem(((ItemEntity) moveEntity).getItem())) ||
                        (moveEntity instanceof ExperienceOrbEntity)) {
                    double dx = moveEntity.posX - x;
                    double dy = moveEntity.posY - y + 1;
                    double dz = moveEntity.posZ - z;
                    double strength = -1;

                    double d = (double) MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
                    if(d > 0.5D) {
                        double m = 1 / (2 * (Math.max(1, d)));
                        dx *= m;
                        dy *= m;
                        dz *= m;
                        if (moveEntity instanceof ItemEntity && d < 5.0D) {
                            ((ItemEntity) moveEntity).setPickupDelay(0);
                        }
                        moveEntity.setMotion(dx * strength, moveEntity.collidedHorizontally ? 0.3 : dy * strength, dz * strength);
                    }
                }
            }
        }
    }

    protected boolean canKineticateItem(ItemStack entityItem) {
        return true;
    }
}
