package org.cyclops.everlastingabilities.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.NonNullList;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.AbilityTypes;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

import net.minecraft.item.Item.Properties;

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
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            for (IAbilityType abilityType : AbilityTypes.REGISTRY.getValues()) {
                for (int level = 1; level <= abilityType.getMaxLevel(); level++) {
                    Ability ability = new Ability(abilityType, level);
                    items.add(getTotem(ability));
                }
            }
        }
    }

}
