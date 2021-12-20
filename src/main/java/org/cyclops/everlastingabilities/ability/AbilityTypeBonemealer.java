package org.cyclops.everlastingabilities.ability;

import net.minecraft.block.BlockState;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.helper.WorldHelpers;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Ability type for fertility.
 * @author rubensworks
 */
public class AbilityTypeBonemealer extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS;

    public AbilityTypeBonemealer(String id, Supplier<Integer> rarity, Supplier<Integer> maxLevel,
                                 Supplier<Integer> baseXpPerLevel, Supplier<Boolean> obtainableOnPlayerSpawn, Supplier<Boolean> obtainableOnMobSpawn,
                                 Supplier<Boolean> obtainableOnCraft, Supplier<Boolean> obtainableOnLoot) {
        super(id, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
    }

    protected int getDurationMultiplier() {
        return 3;
    }

    @Override
    public void onTick(PlayerEntity player, int level) {
        World world = player.level;
        if (!world.isClientSide() && player.level.getGameTime() % (TICK_MODULUS / level) == 0) {
            int radius = level * 2;
            WorldHelpers.foldArea(world, new int[]{radius, 1, radius}, new int[]{radius, 1, radius}, player.blockPosition(), new WorldHelpers.WorldFoldingFunction<Void, Void, World>() {
                @Nullable
                @Override
                public Void apply(Void from, World world, BlockPos pos) {
                    BlockState blockState = world.getBlockState(pos);
                    if (blockState.getBlock() instanceof IGrowable && !(blockState.getBlock() instanceof GrassBlock)) {
                        blockState.randomTick((ServerWorld) world, pos, world.random);
                    }
                    return null;
                }
            }, null);
        }
    }
}
