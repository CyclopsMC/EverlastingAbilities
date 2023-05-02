package org.cyclops.everlastingabilities.core.config.extendedconfig;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.IForgeRegistry;
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
public abstract class AbilitySerializerConfig<T extends IAbilityType> extends ExtendedConfigForge<AbilitySerializerConfig<T>, Codec<T>> {

    public AbilitySerializerConfig(String namedId, Function<AbilitySerializerConfig<T>, ? extends Codec<T>> elementConstructor) {
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
    public IForgeRegistry<? super Codec<? extends IAbilityType>> getRegistry() {
        return AbilityTypeSerializers.REGISTRY;
    }
}
