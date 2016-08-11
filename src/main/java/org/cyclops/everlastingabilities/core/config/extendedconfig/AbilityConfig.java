package org.cyclops.everlastingabilities.core.config.extendedconfig;

import org.cyclops.cyclopscore.config.ConfigurableType;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.config.ExtendedConfigurableType;

/**
 * Config for ability types.
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class AbilityConfig<T> extends ExtendedConfig<AbilityConfig<T>> {

    private final IAbilityType abilityType;

    /**
     * Make a new instance.
     * @param enabled If this should is enabled.
     * @param namedId The unique name ID for the configurable.
     * @param comment The comment to add in the config file for this configurable.
     * @param abilityType the ability instance
     */
    public AbilityConfig(boolean enabled, String namedId, String comment, IAbilityType abilityType) {
        super(EverlastingAbilities._instance, enabled, namedId, comment, null);
        this.abilityType = abilityType;
    }
    
    @Override
	public String getUnlocalizedName() {
		return "ability." + getNamedId();
	}
    
    @Override
	public ConfigurableType getHolderType() {
		return ExtendedConfigurableType.ABILITY;
	}

    public IAbilityType getAbilityType() {
        return abilityType;
    }
}
