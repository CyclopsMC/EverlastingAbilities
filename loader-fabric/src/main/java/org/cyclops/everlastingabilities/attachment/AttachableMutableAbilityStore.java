package org.cyclops.everlastingabilities.attachment;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.core.Holder;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.DefaultAbilityStore;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IInitializableMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;

import java.util.Collection;
import java.util.Map;

/**
 * Wrapper for an attachable ability store.
 * @author rubensworks
 */
public class AttachableMutableAbilityStore implements IMutableAbilityStore, IInitializableMutableAbilityStore {

    private final AttachmentTarget target;
    private final Runnable onModified;

    public AttachableMutableAbilityStore(AttachmentTarget target, Runnable onModified) {
        this.target = target;
        this.onModified = onModified;
    }

    public AttachableMutableAbilityStore(AttachmentTarget target) {
        this(target, () -> {});
    }

    protected IMutableAbilityStore getInnerStore() {
        return new DefaultMutableAbilityStore(target.getAttachedOrGet(Attachments.ABILITY_STORE, DefaultAbilityStore::new));
    }

    protected IMutableAbilityStore setInnerStore(IMutableAbilityStore store) {
        target.setAttached(Attachments.ABILITY_STORE, new DefaultAbilityStore(store));
        this.onModified.run();
        return store;
    }

    @Override
    public boolean isInitialized() {
        return target.hasAttached(Attachments.ABILITY_STORE);
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
