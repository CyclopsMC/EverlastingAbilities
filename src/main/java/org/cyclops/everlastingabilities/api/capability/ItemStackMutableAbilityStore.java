package org.cyclops.everlastingabilities.api.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

import javax.annotation.Nullable;
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
        NBTTagCompound root = ItemStackHelpers.getSafeTagCompound(itemStack);
        if (!root.hasKey(NBT_STORE)) {
            root.setTag(NBT_STORE, new NBTTagList());
        }
        NBTBase nbt = root.getTag(NBT_STORE);
        MutableAbilityStoreConfig.CAPABILITY.readNBT(store, null, nbt);
        return store;
    }

    protected IMutableAbilityStore setInnerStore(IMutableAbilityStore store) {
        NBTTagCompound root = ItemStackHelpers.getSafeTagCompound(itemStack);
        NBTBase nbt = MutableAbilityStoreConfig.CAPABILITY.writeNBT(store, null);
        root.setTag(NBT_STORE, nbt);
        return store;
    }

    @Nullable
    @Override
    public Ability addAbility(Ability ability, boolean doAdd) {
        IMutableAbilityStore store = getInnerStore();
        Ability ret = store.addAbility(ability, doAdd);
        setInnerStore(store);
        return ret;
    }

    @Nullable
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
