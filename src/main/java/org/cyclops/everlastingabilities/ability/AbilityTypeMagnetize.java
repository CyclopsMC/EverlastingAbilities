package org.cyclops.everlastingabilities.ability;

import com.google.common.base.Predicate;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
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
    public void onTick(Player player, int level) {
        Level world = player.level;
        if (!world.isClientSide && !player.isCrouching() && player.level.getGameTime() % TICK_MODULUS == 0) {
            // Center of the attraction
            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();

            // Get items in calculated area.
            int area = level * 2;
            AABB box = new AABB(x, y, z, x, y, z).inflate(area, area, area);
            List<Entity> entities = world.getEntities(player, box, new Predicate<Entity>() {

                @Override
                public boolean apply(Entity entity) {
                    return entity instanceof ItemEntity
                            || (GeneralConfig.magnetizeMoveXp && entity instanceof ExperienceOrb);
                }

            });

            // Move all those items in the direction of the player.
            for(Entity moveEntity : entities) {
                if((moveEntity instanceof ItemEntity && !((ItemEntity) moveEntity).hasPickUpDelay()
                        && canKineticateItem(((ItemEntity) moveEntity))) ||
                        (moveEntity instanceof ExperienceOrb)) {
                    double dx = moveEntity.getX() - x;
                    double dy = moveEntity.getY() - y + 1;
                    double dz = moveEntity.getZ() - z;
                    double strength = -1;

                    double d = Mth.sqrt((float) (dx * dx + dy * dy + dz * dz));
                    if(d > 0.5D) {
                        double m = 1 / (2 * (Math.max(1, d)));
                        dx *= m;
                        dy *= m;
                        dz *= m;
                        if (moveEntity instanceof ItemEntity && d < 5.0D) {
                            ((ItemEntity) moveEntity).setPickUpDelay(0);
                        }
                        moveEntity.setDeltaMovement(dx * strength, moveEntity.horizontalCollision ? 0.3 : dy * strength, dz * strength);
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
