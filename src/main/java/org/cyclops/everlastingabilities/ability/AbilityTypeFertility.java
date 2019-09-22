package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.World;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;

import java.util.List;
import java.util.function.Supplier;

/**
 * Ability type for fertility.
 * @author rubensworks
 */
public class AbilityTypeFertility extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS;

    public AbilityTypeFertility(String id, Supplier<Integer> rarity, Supplier<Integer> maxLevel,
                                Supplier<Integer> baseXpPerLevel, Supplier<Boolean> obtainableOnPlayerSpawn, Supplier<Boolean> obtainableOnMobSpawn,
                                Supplier<Boolean> obtainableOnCraft, Supplier<Boolean> obtainableOnLoot) {
        super(id, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
    }

    protected int getDurationMultiplier() {
        return 3;
    }

    @Override
    public void onTick(PlayerEntity player, int level) {
        World world = player.world;
        if (!world.isRemote && player.world.getGameTime() % TICK_MODULUS == 0) {
            int radius = level * 2;
            List<AnimalEntity> mobs = world.getEntitiesWithinAABB(AnimalEntity.class,
                    player.getBoundingBox().grow(radius, radius, radius), EntityPredicates.NOT_SPECTATING);
            for (AnimalEntity animal : mobs) {
                animal.setInLove(player);
            }
        }
    }
}
