package org.cyclops.everlastingabilities.ability;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
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
    public void onTick(Player player, int level) {
        Level world = player.level;
        if (!world.isClientSide() && player.level.getGameTime() % (TICK_MODULUS / level) == 0) {
            int radius = level * 2;
            WorldHelpers.foldArea(world, new int[]{radius, 1, radius}, new int[]{radius, 1, radius}, player.blockPosition(), new WorldHelpers.WorldFoldingFunction<Void, Void, Level>() {
                @Nullable
                @Override
                public Void apply(Void from, Level world, BlockPos pos) {
                    BlockState blockState = world.getBlockState(pos);
                    if (blockState.getBlock() instanceof BonemealableBlock && !(blockState.getBlock() instanceof GrassBlock)) {
                        blockState.randomTick((ServerLevel) world, pos, world.random);
                    }
                    return null;
                }
            }, null);
        }
    }
}
