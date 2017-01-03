package org.cyclops.everlastingabilities.core.config.configurabletypeaction;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeAction;
import org.cyclops.everlastingabilities.ability.AbilityTypes;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * The action used for {@link AbilityConfig}.
 * @author rubensworks
 * @see ConfigurableTypeAction
 */
public class AbilityAction<T> extends ConfigurableTypeAction<AbilityConfig<T>> {

    @Override
    public void preRun(AbilityConfig<T> eConfig, Configuration config, boolean startup) {
        // Get property in config file and set comment
        Property property = config.get(eConfig.getHolderType().getCategory(), eConfig.getNamedId(), eConfig.isEnabled());
        property.setRequiresMcRestart(true);
        property.setComment(eConfig.getComment());
        property.setLanguageKey(eConfig.getFullUnlocalizedName());
        
        if(startup) {
	        eConfig.setEnabled(property.getBoolean(false));
        }
    }

    @Override
    public void postRun(AbilityConfig<T> eConfig, Configuration config) {
        // Save the config inside the correct element
        eConfig.save();
        
        // Register ability
        AbilityTypes.REGISTRY.register(eConfig.getAbilityType());
    }

}
