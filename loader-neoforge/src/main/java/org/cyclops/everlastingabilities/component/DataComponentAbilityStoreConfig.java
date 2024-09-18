package org.cyclops.everlastingabilities.component;

import org.cyclops.cyclopscore.config.extendedconfig.DataComponentConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.core.helper.CodecHelpers;

/**
 * @author rubensworks
 */
public class DataComponentAbilityStoreConfig extends DataComponentConfig<IAbilityStore> {
    public DataComponentAbilityStoreConfig() {
        super(EverlastingAbilities._instance, "ability_store", builder -> builder
                .persistent(CodecHelpers.CODEC_ABILITY_STORE)
                .networkSynchronized(CodecHelpers.STREAM_CODEC_ABILITY_STORE));
    }
}
