package org.cyclops.everlastingabilities.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.ability.AbilityTypes;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

import java.util.List;

/**
 * A totem with abilities.
 * @author rubensworks
 */
public class ItemAbilityTotem extends ItemGuiAbilityContainer {

    private static ItemAbilityTotem _instance = null;

    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static ItemAbilityTotem getInstance() {
        return _instance;
    }

    /**
     * Make a new item instance.
     *
     * @param eConfig Config for this blockState.
     */
    public ItemAbilityTotem(ExtendedConfig eConfig) {
        super(eConfig);
    }

    @Override
    public boolean canMoveFromPlayer() {
        return false;
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        IAbilityStore abilityStore = itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        int maxRarity = 0;
        for (Ability ability : abilityStore.getAbilities()) {
            maxRarity = Math.max(maxRarity, ability.getAbilityType().getRarity().ordinal());
        }
        return EnumRarity.values()[maxRarity];
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (IAbilityType abilityType : AbilityTypes.REGISTRY.getAbilityTypes()) {
            for (int level = 1; level <= abilityType.getMaxLevel(); level++) {
                Ability ability = new Ability(abilityType, level);
                ItemStack itemStack = new ItemStack(itemIn);
                IMutableAbilityStore mutableAbilityStore = itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
                mutableAbilityStore.addAbility(ability, true);
                subItems.add(itemStack);
            }
        }
    }
}
