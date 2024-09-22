package org.cyclops.everlastingabilities.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.api.Ability;

/**
 * A totem with abilities.
 * @author rubensworks
 */
public abstract class ItemAbilityTotem extends ItemGuiAbilityContainer {

    public ItemAbilityTotem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canMoveFromPlayer() {
        return false;
    }

    public static Rarity getRarity(ItemStack itemStack) {
        return EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getItemAbilityStore(itemStack)
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
