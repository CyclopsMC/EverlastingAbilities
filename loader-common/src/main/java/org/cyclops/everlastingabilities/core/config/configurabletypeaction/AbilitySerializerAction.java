package org.cyclops.everlastingabilities.core.config.configurabletypeaction;

import com.mojang.serialization.MapCodec;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeActionRegistry;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilitySerializerConfig;

/**
 * The action used for {@link AbilitySerializerConfig}.
 * @author rubensworks
 * @see ConfigurableTypeActionRegistry
 */
public class AbilitySerializerAction<T extends IAbilityType, M extends IModBase> extends ConfigurableTypeActionRegistry<AbilitySerializerConfig<T, M>, MapCodec<T>, M> {

}
