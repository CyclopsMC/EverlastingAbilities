package org.cyclops.everlastingabilities.api.capability;

import lombok.NonNull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

import java.util.Collection;
import java.util.Map;

/**
 * Wrapper for an item ability store.
 * TODO: This is just to avoid a Forge bug where cap NBT is not always sent to the client.
 * @author rubensworks
 */
public class ItemStackMutableAbilityStore implements IMutableAbilityStore {

    private static final String NBT_STORE = Reference.MOD_ID + ":abilityStoreStack";

    private final ItemStack itemStack;

    public ItemStackMutableAbilityStore(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    protected IMutableAbilityStore getInnerStore() {
        IMutableAbilityStore store = new DefaultMutableAbilityStore();
        CompoundNBT root = itemStack.getOrCreateTag();
        if (!root.contains(NBT_STORE)) {
            root.put(NBT_STORE, new ListNBT());
        }
        INBT nbt = root.get(NBT_STORE);
        MutableAbilityStoreConfig.CAPABILITY.readNBT(store, null, nbt);
        return store;
    }

    protected IMutableAbilityStore setInnerStore(IMutableAbilityStore store) {
        CompoundNBT root = itemStack.getOrCreateTag();
        INBT nbt = MutableAbilityStoreConfig.CAPABILITY.writeNBT(store, null);
        root.put(NBT_STORE, nbt);
        return store;
    }

    @NonNull
    @Override
    public Ability addAbility(Ability ability, boolean doAdd) {
        IMutableAbilityStore store = getInnerStore();
        Ability ret = store.addAbility(ability, doAdd);
        setInnerStore(store);
        return ret;
    }

    @NonNull
    @Override
    public Ability removeAbility(Ability ability, boolean doRemove) {
        IMutableAbilityStore store = getInnerStore();
        Ability ret = store.removeAbility(ability, doRemove);
        setInnerStore(store);
        return ret;
    }

    @Override
    public void setAbilities(Map<IAbilityType, Integer> abilityTypes) {
        IMutableAbilityStore store = getInnerStore();
        store.setAbilities(abilityTypes);
        setInnerStore(store);
    }

    @Override
    public boolean hasAbilityType(IAbilityType abilityType) {
        IMutableAbilityStore store = getInnerStore();
        return store.hasAbilityType(abilityType);
    }

    @Override
    public Collection<IAbilityType> getAbilityTypes() {
        IMutableAbilityStore store = getInnerStore();
        return store.getAbilityTypes();
    }

    @Override
    public Collection<Ability> getAbilities() {
        IMutableAbilityStore store = getInnerStore();
        return store.getAbilities();
    }

    @Override
    public Map<IAbilityType, Integer> getAbilitiesRaw() {
        IMutableAbilityStore store = getInnerStore();
        return store.getAbilitiesRaw();
    }

    @Override
    public Ability getAbility(IAbilityType abilityType) {
        IMutableAbilityStore store = getInnerStore();
        return store.getAbility(abilityType);
    }
}
