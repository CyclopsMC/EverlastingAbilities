package org.cyclops.everlastingabilities.ability;

import com.google.common.base.Predicate;
import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.AbilityTypeAdapter;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.List;
import java.util.Objects;

/**
 * Ability type for attracting items in the area.
 * @author rubensworks
 */
public class AbilityTypeSpecialMagnetize extends AbilityTypeAdapter {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 20;
    private final boolean moveXp;

    public AbilityTypeSpecialMagnetize(String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                                       boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot,
                                       boolean moveXp) {
        super(name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
        this.moveXp = moveXp;
    }

    @Override
    public Codec<? extends IAbilityType> codec() {
        return Objects.requireNonNull(RegistryEntries.ABILITYSERIALIZER_SPECIAL_MAGNETIZE);
    }

    public boolean isMoveXp() {
        return moveXp;
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
                            || (isMoveXp() && entity instanceof ExperienceOrb);
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
