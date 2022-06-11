package org.cyclops.everlastingabilities.command.argument;

import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import org.cyclops.cyclopscore.config.extendedconfig.ArgumentTypeConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;

/**
 * An argument type for an ability.
 * @author rubensworks
 */
public class ArgumentTypeAbilityConfig extends ArgumentTypeConfig<ArgumentTypeAbility, SingletonArgumentInfo<ArgumentTypeAbility>.Template> {
    public ArgumentTypeAbilityConfig() {
        super(EverlastingAbilities._instance, "ability", SingletonArgumentInfo.contextFree(ArgumentTypeAbility::new), ArgumentTypeAbility.class);
    }
}
