package org.cyclops.everlastingabilities.core.config.configurabletypeaction;

import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeAction;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * The action used for {@link AbilityConfig}.
 * @author rubensworks
 * @see ConfigurableTypeAction
 */
public class AbilityAction<T> extends ConfigurableTypeAction<AbilityConfig<T>, IAbilityType> {

    @Override
    public void onRegisterForge(AbilityConfig<T> eConfig) {
        register(eConfig.getInstance(), eConfig);
    }

}
