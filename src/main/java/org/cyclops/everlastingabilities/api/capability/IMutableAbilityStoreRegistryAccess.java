package org.cyclops.everlastingabilities.api.capability;

import net.minecraft.core.RegistryAccess;

/**
 * @author rubensworks
 */
public interface IMutableAbilityStoreRegistryAccess extends IMutableAbilityStore {

    public void setRegistryAccess(RegistryAccess registryAccess);

}
