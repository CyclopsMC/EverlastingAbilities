package org.cyclops.everlastingabilities.ability;

import net.minecraft.block.BlockGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
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
    public void onTick(EntityPlayer player, int level) {
        World world = player.world;
        if (!world.isRemote && player.world.getWorldTime() % (TICK_MODULUS / level) == 0) {
            int radius = level * 2;
            WorldHelpers.foldArea(world, new int[]{radius, 1, radius}, new int[]{radius, 1, radius}, player.getPosition(), new WorldHelpers.WorldFoldingFunction<Void, Void>() {
                @Nullable
                @Override
                public Void apply(Void from, World world, BlockPos pos) {
                    IBlockState blockState = world.getBlockState(pos);
                    if (blockState.getBlock() instanceof IGrowable && !(blockState.getBlock() instanceof BlockGrass)) {
                        blockState.getBlock().updateTick(world, pos, blockState, world.rand);
                    }
                    return null;
                }
            }, null);
        }
    }
}
