package org.cyclops.everlastingabilities.command.argument;

import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import org.cyclops.cyclopscore.config.extendedconfig.ArgumentTypeConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;

/**
 * An argument type for an ability.
 * @author rubensworks
 */
public class ArgumentTypeAbilityConfig<M extends IModBase> extends ArgumentTypeConfigCommon<ArgumentTypeAbility, SingletonArgumentInfo<ArgumentTypeAbility>.Template, M> {
    public ArgumentTypeAbilityConfig(M mod) {
        super(mod, "ability", SingletonArgumentInfo.contextAware(ArgumentTypeAbility::new), ArgumentTypeAbility.class);
    }
}
