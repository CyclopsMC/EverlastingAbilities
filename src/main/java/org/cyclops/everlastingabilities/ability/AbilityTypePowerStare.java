package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.ability.config.AbilityPowerStareConfig;

import java.util.List;

/**
 * Ability type for pushing in the direction your looking mobs away.
 * @author rubensworks
 */
public class AbilityTypePowerStare extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 4;

    public AbilityTypePowerStare(String id, int rarity, int maxLevel, int baseXpPerLevel) {
        super(id, rarity, maxLevel, baseXpPerLevel);
    }

    @Override
    public void onTick(PlayerEntity player, int level) {
    
        if ( AbilityPowerStareConfig.requireSneak && !player.isSneaking() ) {
            return;
        }

        World world = player.world;
        if (!world.isRemote && player.world.getGameTime() % TICK_MODULUS == 0) {
            int range = level * 10;
            double eyeHeight = player.getEyeHeight();
            Vec3d lookVec = player.getLookVec();
            Vec3d origin = new Vec3d(player.posX, player.posY + eyeHeight, player.posZ);
            Vec3d direction = origin.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);

            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player,
                    player.getBoundingBox().expand(lookVec.x * range, lookVec.y * range, lookVec.z * range)
                            .grow((double) range));
            for (Entity e : list) {
                // TODO TameableEntity was IEntityOwnable
                if (e.canBeCollidedWith() && (!(e instanceof TameableEntity) || ((TameableEntity) e).getOwner() != player) && !player.isOnSameTeam(e)) {
                    Entity entity = null;
                    float f10 = e.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = e.getBoundingBox().expand((double) f10, (double) f10, (double) f10);
                    Vec3d hitVec = axisalignedbb.rayTrace(origin, direction).orElse(null);

                    if (axisalignedbb.contains(origin)) {
                        entity = e;
                    } else if (hitVec != null) {
                        double distance = origin.distanceTo(hitVec);
                        if (distance < range || range == 0.0D) {
                            if (e == player.getRidingEntity() && !player.canRiderInteract()) {
                                if (range == 0.0D) {
                                    entity = e;
                                }
                            } else {
                                entity = e;
                            }
                        }
                    }

                    if (entity != null) {
                        double dx = entity.posX - player.posX;
                        double dy = entity.posY - player.posY;
                        double dz = entity.posZ - player.posZ;
                        double d = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
                        double m = 1 / (2 * (Math.max(1, d)));
                        dx *= m;
                        dy *= m;
                        dz *= m;

                        double strength = 3F;

                        entity.setMotion(dx * strength, dy * strength, dz * strength);
                        break;
                    }
                }
            }
        }
    }
}
