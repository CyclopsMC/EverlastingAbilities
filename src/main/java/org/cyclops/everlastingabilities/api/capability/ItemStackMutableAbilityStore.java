package org.cyclops.everlastingabilities.api.capability;

import lombok.NonNull;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.helper.WorldHelpers;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * Wrapper for an item ability store.
 * TODO: This is just to avoid a Forge bug where cap NBT is not always sent to the client.
 * @author rubensworks
 */
public class ItemStackMutableAbilityStore implements IMutableAbilityStoreRegistryAccess {

    private static final String NBT_STORE = Reference.MOD_ID + ":abilityStoreStack";

    private final ItemStack itemStack;
    @Nullable
    private RegistryAccess registryAccess;

    public ItemStackMutableAbilityStore(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void setRegistryAccess(RegistryAccess registryAccess) {
        this.registryAccess = registryAccess;
    }

    protected Registry<IAbilityType> getRegistry() {
         return AbilityHelpers.getRegistry(this.registryAccess != null ? this.registryAccess : WorldHelpers.getActiveLevel().registryAccess());
    }

    protected IMutableAbilityStore getInnerStore() {
        IMutableAbilityStore store = new DefaultMutableAbilityStore();
        CompoundTag root = itemStack.getOrCreateTag();
        if (!root.contains(NBT_STORE)) {
            root.put(NBT_STORE, new ListTag());
        }
        Tag nbt = root.get(NBT_STORE);
        AbilityStoreCapabilityProvider.deserializeNBTStatic(getRegistry(), store, nbt);
        return store;
    }

    protected IMutableAbilityStore setInnerStore(IMutableAbilityStore store) {
        CompoundTag root = itemStack.getOrCreateTag();
        Tag nbt = AbilityStoreCapabilityProvider.serializeNBTStatic(getRegistry(), store);
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
