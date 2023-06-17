package org.cyclops.everlastingabilities.ability;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.AbilityTypeAdapter;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.List;
import java.util.Objects;

/**
 * Ability type for pushing in the direction your looking mobs away.
 * @author rubensworks
 */
public class AbilityTypeSpecialPowerStare extends AbilityTypeAdapter {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 4;

    private final boolean requireSneak;

    public AbilityTypeSpecialPowerStare(ICondition condition, String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                                        boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot,
                                        boolean requireSneak) {
        super(condition, name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
        this.requireSneak = requireSneak;
    }

    @Override
    public Codec<? extends IAbilityType> codec() {
        return Objects.requireNonNull(RegistryEntries.ABILITYSERIALIZER_SPECIAL_POWER_STARE);
    }

    public boolean isRequireSneak() {
        return requireSneak;
    }

    @Override
    public void onTick(Player player, int level) {
        if (isRequireSneak() && !player.isCrouching()) {
            return;
        }

        Level world = player.level();
        if (!world.isClientSide && world.getGameTime() % TICK_MODULUS == 0) {
            int range = level * 10;
            double eyeHeight = player.getEyeHeight();
            Vec3 lookVec = player.getLookAngle();
            Vec3 origin = new Vec3(player.getX(), player.getY() + eyeHeight, player.getZ());
            Vec3 direction = origin.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);

            List<Entity> list = world.getEntities(player,
                    player.getBoundingBox().expandTowards(lookVec.x * range, lookVec.y * range, lookVec.z * range)
                            .inflate((double) range));
            for (Entity e : list) {
                // TODO TameableEntity was IEntityOwnable
                if (e.isPickable() && (!(e instanceof TamableAnimal) || ((TamableAnimal) e).getOwner() != player) && !player.isAlliedTo(e)) {
                    Entity entity = null;
                    float f10 = e.getPickRadius();
                    AABB axisalignedbb = e.getBoundingBox().expandTowards((double) f10, (double) f10, (double) f10);
                    Vec3 hitVec = axisalignedbb.clip(origin, direction).orElse(null);

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
                        double d = Mth.sqrt((float) (dx * dx + dy * dy + dz * dz));
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
