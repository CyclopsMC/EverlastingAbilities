package org.cyclops.everlastingabilities.ability;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.AbilityTypeAdapter;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.List;

/**
 * Ability type for fertility.
 * @author rubensworks
 */
public class AbilityTypeFertility extends AbilityTypeAdapter {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS;

    public AbilityTypeFertility(String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                                boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot) {
        super(name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
    }

    @Override
    public Codec<? extends IAbilityType> codec() {
        return RegistryEntries.ABILITYSERIALIZER_FERTILITY;
    }

    protected int getDurationMultiplier() {
        return 3;
    }

    @Override
    public void onTick(Player player, int level) {
        Level world = player.level;
        if (!world.isClientSide && player.level.getGameTime() % TICK_MODULUS == 0) {
            int radius = level * 2;
            List<Animal> mobs = world.getEntitiesOfClass(Animal.class,
                    player.getBoundingBox().inflate(radius, radius, radius), EntitySelector.NO_SPECTATORS);
            for (Animal animal : mobs) {
                animal.setInLove(player);
            }
        }
    }
}
