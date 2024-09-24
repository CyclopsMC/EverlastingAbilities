package org.cyclops.everlastingabilities.core.config.configurabletypeaction;

import net.minecraftforge.registries.RegisterEvent;
import org.cyclops.cyclopscore.config.ConfigHandlerCommon;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeActionRegistry;
import org.cyclops.cyclopscore.init.ModBaseForge;
import org.cyclops.everlastingabilities.EverlastingAbilitiesForge;
import org.cyclops.everlastingabilities.api.AbilityTypeSerializers;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilitySerializerConfig;

/**
 * The action used for {@link AbilitySerializerConfig}.
 * @author rubensworks
 * @see ConfigurableTypeActionRegistry
 */
public class AbilitySerializerActionForge<T extends IAbilityType, M extends ModBaseForge<?>> extends AbilitySerializerAction<T, M> {

    @Override
    public void onRegisterForge(AbilitySerializerConfig<T, M> eConfig) {
        super.onRegisterForge(eConfig);

        eConfig.getMod().getModEventBus().addListener((RegisterEvent event) -> {
            if (event.getRegistryKey().equals(AbilityTypeSerializers.REGISTRY_KEY)) {
                EverlastingAbilitiesForge.REGISTRY_ABILITY_SERIALIZERS.register(ConfigHandlerCommon.getConfigId(eConfig), eConfig.getInstance());
            }
        });
    }

    @Override
    public void onRegisterForgeFilled(AbilitySerializerConfig<T, M> eConfig) {
        // Do nothing, as we register to a different type of registry above
    }

}
