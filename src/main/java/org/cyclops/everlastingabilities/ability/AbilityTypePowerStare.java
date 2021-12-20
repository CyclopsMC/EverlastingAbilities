package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.ability.config.AbilityPowerStareConfig;

import java.util.List;
import java.util.function.Supplier;

/**
 * Ability type for pushing in the direction your looking mobs away.
 * @author rubensworks
 */
public class AbilityTypePowerStare extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 4;

    public AbilityTypePowerStare(String id, Supplier<Integer> rarity, Supplier<Integer> maxLevel,
                                 Supplier<Integer> baseXpPerLevel, Supplier<Boolean> obtainableOnPlayerSpawn, Supplier<Boolean> obtainableOnMobSpawn,
                                 Supplier<Boolean> obtainableOnCraft, Supplier<Boolean> obtainableOnLoot) {
        super(id, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
    }

    @Override
    public void onTick(PlayerEntity player, int level) {
    
        if ( AbilityPowerStareConfig.requireSneak && !player.isCrouching() ) {
            return;
        }

        World world = player.level;
        if (!world.isClientSide && player.level.getGameTime() % TICK_MODULUS == 0) {
            int range = level * 10;
            double eyeHeight = player.getEyeHeight();
            Vector3d lookVec = player.getLookAngle();
            Vector3d origin = new Vector3d(player.getX(), player.getY() + eyeHeight, player.getZ());
            Vector3d direction = origin.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);

            List<Entity> list = world.getEntities(player,
                    player.getBoundingBox().expandTowards(lookVec.x * range, lookVec.y * range, lookVec.z * range)
                            .inflate((double) range));
            for (Entity e : list) {
                // TODO TameableEntity was IEntityOwnable
                if (e.isPickable() && (!(e instanceof TameableEntity) || ((TameableEntity) e).getOwner() != player) && !player.isAlliedTo(e)) {
                    Entity entity = null;
                    float f10 = e.getPickRadius();
                    AxisAlignedBB axisalignedbb = e.getBoundingBox().expandTowards((double) f10, (double) f10, (double) f10);
                    Vector3d hitVec = axisalignedbb.clip(origin, direction).orElse(null);

                    if (axisalignedbb.contains(origin)) {
                        entity = e;
                    } else if (hitVec != null) {
                        double distance = origin.distanceTo(hitVec);
                        if (distance < range || range == 0.0D) {
                            if (e == player.getVehicle() && !player.canRiderInteract()) {
                                if (range == 0.0D) {
                                    entity = e;
                                }
                            } else {
                                entity = e;
                            }
                        }
                    }

                    if (entity != null) {
                        double dx = entity.getX() - player.getX();
                        double dy = entity.getY() - player.getY();
                        double dz = entity.getZ() - player.getZ();
                        double d = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
                        double m = 1 / (2 * (Math.max(1, d)));
                        dx *= m;
                        dy *= m;
                        dz *= m;

                        double strength = 3F;

                        entity.setDeltaMovement(dx * strength, dy * strength, dz * strength);
                        break;
                    }
                }
            }
        }
    }
}
