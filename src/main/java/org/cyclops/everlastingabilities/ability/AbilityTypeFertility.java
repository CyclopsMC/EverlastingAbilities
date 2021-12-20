package org.cyclops.everlastingabilities.ability;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
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
