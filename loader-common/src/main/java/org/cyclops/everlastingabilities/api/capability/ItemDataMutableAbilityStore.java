package org.cyclops.everlastingabilities.api.capability;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import org.cyclops.everlastingabilities.RegistryEntriesCommon;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Collection;
import java.util.Map;

/**
 * Wrapper for an item-based ability store.
 * @author rubensworks
 */
public class ItemDataMutableAbilityStore implements IMutableAbilityStore {

    private final ItemStack itemStack;
    private final Runnable onModified;

    public ItemDataMutableAbilityStore(ItemStack itemStack, Runnable onModified) {
        this.itemStack = itemStack;
        this.onModified = onModified;
    }

    public ItemDataMutableAbilityStore(ItemStack itemStack) {
        this(itemStack, () -> {});
    }

    protected IMutableAbilityStore getInnerStore() {
        return new DefaultMutableAbilityStore(itemStack.get(RegistryEntriesCommon.DATACOMPONENT_ABILITY_STORE.value()));
    }

    protected IMutableAbilityStore setInnerStore(IMutableAbilityStore store) {
        itemStack.set(RegistryEntriesCommon.DATACOMPONENT_ABILITY_STORE.value(), new DefaultAbilityStore(store));
        this.onModified.run();
        return store;
    }

    @Override
    public Ability addAbility(Ability ability, boolean doAdd) {
        IMutableAbilityStore store = getInnerStore();
        Ability ret = store.addAbility(ability, doAdd);
        setInnerStore(store);
        return ret;
    }

    @Override
    public Ability removeAbility(Ability ability, boolean doRemove) {
        IMutableAbilityStore store = getInnerStore();
        Ability ret = store.removeAbility(ability, doRemove);
        setInnerStore(store);
        return ret;
    }

    @Override
    public void setAbilities(Map<Holder<IAbilityType>, Integer> abilityTypes) {
        IMutableAbilityStore store = getInnerStore();
        store.setAbilities(abilityTypes);
        setInnerStore(store);
    }

    @Override
    public boolean hasAbilityType(Holder<IAbilityType> abilityType) {
        IMutableAbilityStore store = getInnerStore();
        return store.hasAbilityType(abilityType);
    }

    @Override
    public Collection<Holder<IAbilityType>> getAbilityTypes() {
        IMutableAbilityStore store = getInnerStore();
        return store.getAbilityTypes();
    }

    @Override
    public Collection<Ability> getAbilities() {
        IMutableAbilityStore store = getInnerStore();
        return store.getAbilities();
    }

    @Override
    public Map<Holder<IAbilityType>, Integer> getAbilitiesRaw() {
        IMutableAbilityStore store = getInnerStore();
        return store.getAbilitiesRaw();
    }

    @Override
    public Ability getAbility(Holder<IAbilityType> abilityType) {
        IMutableAbilityStore store = getInnerStore();
        return store.getAbility(abilityType);
    }
}
