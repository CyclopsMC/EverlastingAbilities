package org.cyclops.everlastingabilities.ability;

import net.minecraft.block.BlockState;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.helper.WorldHelpers;

import javax.annotation.Nullable;

/**
 * Ability type for fertility.
 * @author rubensworks
 */
public class AbilityTypeBonemealer extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS;

    public AbilityTypeBonemealer(String id, int rarity, int maxLevel, int baseXpPerLevel) {
        super(id, rarity, maxLevel, baseXpPerLevel);
    }

    protected int getDurationMultiplier() {
        return 3;
    }

    @Override
    public void onTick(PlayerEntity player, int level) {
        World world = player.world;
        if (!world.isRemote && player.world.getGameTime() % (TICK_MODULUS / level) == 0) {
            int radius = level * 2;
            WorldHelpers.foldArea(world, new int[]{radius, 1, radius}, new int[]{radius, 1, radius}, player.getPosition(), new WorldHelpers.WorldFoldingFunction<Void, Void>() {
                @Nullable
                @Override
                public Void apply(Void from, World world, BlockPos pos) {
                    BlockState blockState = world.getBlockState(pos);
                    if (blockState.getBlock() instanceof IGrowable && !(blockState.getBlock() instanceof GrassBlock)) {
                        blockState.tick(world, pos, world.rand);
                    }
                    return null;
                }
            }, null);
        }
    }
}
