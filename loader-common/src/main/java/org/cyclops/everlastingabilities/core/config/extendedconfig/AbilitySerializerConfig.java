package org.cyclops.everlastingabilities.core.config.extendedconfig;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigRegistry;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.everlastingabilities.api.AbilityTypeSerializers;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.config.ExtendedConfigurableType;

import java.util.function.Function;

/**
 * Config for ability serializer types.
 * @author rubensworks
 * @see ExtendedConfigRegistry
 */
public abstract class AbilitySerializerConfig<T extends IAbilityType, M extends IModBase> extends ExtendedConfigRegistry<AbilitySerializerConfig<T, M>, MapCodec<T>, M> {

    public AbilitySerializerConfig(M mod, String namedId, Function<AbilitySerializerConfig<T, M>, ? extends MapCodec<T>> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    @Override
    public String getTranslationKey() {
        return "ability_serializer." + getNamedId();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ExtendedConfigurableType.ABILITY_SERIALIZER;
    }

    @Override
    public Registry<? super MapCodec<? extends IAbilityType>> getRegistry() {
        return AbilityTypeSerializers.REGISTRY;
    }
}
