package org.cyclops.everlastingabilities.component;

import org.cyclops.cyclopscore.config.extendedconfig.DataComponentConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.core.helper.CodecHelpers;

/**
 * @author rubensworks
 */
public class DataComponentAbilityStoreConfig<M extends IModBase> extends DataComponentConfigCommon<IAbilityStore, M> {
    public DataComponentAbilityStoreConfig(M mod) {
        super(mod, "ability_store", builder -> builder
                .persistent(CodecHelpers.CODEC_ABILITY_STORE)
                .networkSynchronized(CodecHelpers.STREAM_CODEC_ABILITY_STORE));
    }
}
