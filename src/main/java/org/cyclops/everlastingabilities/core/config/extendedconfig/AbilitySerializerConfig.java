package org.cyclops.everlastingabilities.core.config.extendedconfig;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import org.cyclops.cyclopscore.config.ConfigurableType;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigForge;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.AbilityTypeSerializers;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.config.ExtendedConfigurableType;

import java.util.function.Function;

/**
 * Config for ability serializer types.
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class AbilitySerializerConfig<T extends IAbilityType> extends ExtendedConfigForge<AbilitySerializerConfig<T>, MapCodec<T>> {

    public AbilitySerializerConfig(String namedId, Function<AbilitySerializerConfig<T>, ? extends MapCodec<T>> elementConstructor) {
        super(EverlastingAbilities._instance, namedId, elementConstructor);
    }

    @Override
	public String getTranslationKey() {
		return "ability_serializer." + getNamedId();
	}

    @Override
    public ConfigurableType getConfigurableType() {
        return ExtendedConfigurableType.ABILITY_SERIALIZER;
    }

    @Override
    public Registry<? super MapCodec<? extends IAbilityType>> getRegistry() {
        return AbilityTypeSerializers.REGISTRY;
    }
}
