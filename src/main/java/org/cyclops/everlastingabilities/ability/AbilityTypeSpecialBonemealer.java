package org.cyclops.everlastingabilities.ability;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.cyclopscore.helper.WorldHelpers;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.AbilityTypeAdapter;
import org.cyclops.everlastingabilities.api.IAbilityType;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Ability type for fertility.
 * @author rubensworks
 */
public class AbilityTypeSpecialBonemealer extends AbilityTypeAdapter {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS;

    public AbilityTypeSpecialBonemealer(ICondition condition, String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                                        boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot) {
        super(condition, name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
    }

    @Override
    public Codec<? extends IAbilityType> codec() {
        return Objects.requireNonNull(RegistryEntries.ABILITYSERIALIZER_SPECIAL_BONEMEALER);
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
