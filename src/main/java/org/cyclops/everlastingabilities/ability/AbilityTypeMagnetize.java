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
import java.util.function.Supplier;

/**
 * Ability type for attracting items in the area.
 * @author rubensworks
 */
public class AbilityTypeMagnetize extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 20;

    public AbilityTypeMagnetize(String id, Supplier<Integer> rarity, Supplier<Integer> maxLevel,
                                Supplier<Integer> baseXpPerLevel, Supplier<Boolean> obtainableOnPlayerSpawn, Supplier<Boolean> obtainableOnMobSpawn,
                                Supplier<Boolean> obtainableOnCraft, Supplier<Boolean> obtainableOnLoot) {
        super(id, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
    }

    @Override
    public void onTick(PlayerEntity player, int level) {
        World world = player.world;
        if (!world.isRemote && !player.isCrouching() && player.world.getGameTime() % TICK_MODULUS == 0) {
            // Center of the attraction
            double x = player.getPosX();
            double y = player.getPosY();
            double z = player.getPosZ();

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
                        && canKineticateItem(((ItemEntity) moveEntity))) ||
                        (moveEntity instanceof ExperienceOrbEntity)) {
                    double dx = moveEntity.getPosX() - x;
                    double dy = moveEntity.getPosY() - y + 1;
                    double dz = moveEntity.getPosZ() - z;
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

    protected boolean canKineticateItem(ItemEntity entityItem) {
        // Demagnetize mod support
        if(entityItem.getPersistentData().contains("PreventRemoteMovement")){
            return false;
        }
        return true;
    }
}
