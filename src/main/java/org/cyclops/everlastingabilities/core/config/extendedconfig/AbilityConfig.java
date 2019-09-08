package org.cyclops.everlastingabilities.core.config.extendedconfig;

import net.minecraftforge.registries.IForgeRegistry;
import org.cyclops.cyclopscore.config.ConfigurableType;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigForge;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.AbilityTypes;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.config.ExtendedConfigurableType;

import java.util.function.Function;

/**
 * Config for ability types.
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class AbilityConfig<T> extends ExtendedConfigForge<AbilityConfig<T>, IAbilityType> {

    public AbilityConfig(String namedId, Function<AbilityConfig<T>, ? extends IAbilityType> elementConstructor) {
        super(EverlastingAbilities._instance, namedId, elementConstructor);
    }
    
    @Override
	public String getTranslationKey() {
		return "ability." + getNamedId();
	}

    @Override
    public String getFullTranslationKey() {
        return "ability.abilities." + getMod().getModId()  + "." +getNamedId() + ".name";
    }

    @Override
    public ConfigurableType getConfigurableType() {
        return ExtendedConfigurableType.ABILITY;
    }

    @Override
    public IForgeRegistry<? super IAbilityType> getRegistry() {
        return AbilityTypes.REGISTRY;
    }
}
