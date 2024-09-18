package org.cyclops.everlastingabilities.ability;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.AbilityTypeAdapter;
import org.cyclops.everlastingabilities.api.IAbilityCondition;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.List;
import java.util.Objects;

/**
 * Ability type for fertility.
 * @author rubensworks
 */
public class AbilityTypeSpecialFertility extends AbilityTypeAdapter {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS;

    public AbilityTypeSpecialFertility(IAbilityCondition condition, String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                                       boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot) {
        super(condition, name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
    }

    @Override
    public MapCodec<? extends IAbilityType> codec() {
        return Objects.requireNonNull(RegistryEntries.ABILITYSERIALIZER_SPECIAL_FERTILITY.get());
    }

    protected int getDurationMultiplier() {
        return 3;
    }

    @Override
    public void onTick(Player player, int level) {
        Level world = player.level();
        if (!world.isClientSide && world.getGameTime() % TICK_MODULUS == 0) {
            int radius = level * 2;
            List<Animal> mobs = world.getEntitiesOfClass(Animal.class,
                    player.getBoundingBox().inflate(radius, radius, radius), EntitySelector.NO_SPECTATORS);
            for (Animal animal : mobs) {
                animal.setInLove(player);
            }
        }
    }
}
