package org.cyclops.everlastingabilities.core.config.configurabletypeaction;

import com.mojang.serialization.MapCodec;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeAction;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeActionForge;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilitySerializerConfig;

/**
 * The action used for {@link AbilitySerializerConfig}.
 * @author rubensworks
 * @see ConfigurableTypeAction
 */
public class AbilitySerializerAction<T extends IAbilityType> extends ConfigurableTypeActionForge<AbilitySerializerConfig<T>, MapCodec<T>> {

}
