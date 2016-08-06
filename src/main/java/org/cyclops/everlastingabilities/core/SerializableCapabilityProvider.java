package org.cyclops.everlastingabilities.core;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;

/**
 * TODO: port to CyclopsCore
 * @author rubensworks
 */
public class SerializableCapabilityProvider<T> extends DefaultCapabilityProvider<T> implements INBTSerializable {

    private final Capability<T> capabilityType; // TODO: protected super
    private final T capability; // TODO: protected super

    public SerializableCapabilityProvider(Capability<T> capabilityType, T capability) {
        super(capabilityType, capability);
        this.capabilityType = capabilityType;
        this.capability = capability;
    }

    @Override
    public NBTBase serializeNBT() {
        return capabilityType.writeNBT(capability, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        capabilityType.readNBT(capability, null, nbt);
    }
}
