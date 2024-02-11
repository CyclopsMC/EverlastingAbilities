package org.cyclops.everlastingabilities.item;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.cyclops.everlastingabilities.core.helper.WorldHelpers;

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

    @Override
    public Rarity getRarity(ItemStack itemStack) {
        return itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null)
                .map(abilityStore -> {
                    int maxRarity = 0;
                    for (Ability ability : abilityStore.getAbilities()) {
                        maxRarity = Math.max(maxRarity, ability.getAbilityType().getRarity().ordinal());
                    }
                    return Rarity.values()[maxRarity];
                })
                .orElse(super.getRarity(itemStack));
    }

    public static ItemStack getTotem(Ability ability) {
        ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_TOTEM);
        itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null)
                .ifPresent(mutableAbilityStore -> mutableAbilityStore.addAbility(ability, true));
        return itemStack;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        RegistryAccess registryAccess = WorldHelpers.getRegistryAccess();
        if (this.allowedIn(group) && registryAccess != null) { // Level can be null during game loading
            Registry<IAbilityType> registry = AbilityHelpers.getRegistry(registryAccess);
            registry.forEach(abilityType -> {
                for (int level = 1; level <= abilityType.getMaxLevel(); level++) {
                    Ability ability = new Ability(abilityType, level);
                    if (AbilityHelpers.PREDICATE_ABILITY_ENABLED.test(abilityType)) {
                        items.add(getTotem(ability));
                    }
                }
            });
        }
    }

}
