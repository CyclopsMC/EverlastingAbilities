package org.cyclops.everlastingabilities.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.cyclops.everlastingabilities.Capabilities;
import org.cyclops.everlastingabilities.api.Ability;

import java.util.Optional;

/**
 * A totem with abilities.
 * @author rubensworks
 */
public class ItemAbilityTotem extends ItemGuiAbilityContainer {

    public ItemAbilityTotem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canMoveFromPlayer() {
        return false;
    }

    public static Rarity getRarity(ItemStack itemStack) {
        return Optional.ofNullable(itemStack.getCapability(Capabilities.MutableAbilityStore.ITEM))
                .map(abilityStore -> {
                    int maxRarity = 0;
                    for (Ability ability : abilityStore.getAbilities()) {
                        maxRarity = Math.max(maxRarity, ability.getAbilityType().getRarity().ordinal());
                    }
                    return Rarity.values()[maxRarity];
                })
                .orElse(Rarity.COMMON);
    }
}
