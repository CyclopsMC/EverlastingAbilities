package org.cyclops.everlastingabilities.item;

import net.minecraft.world.item.Item;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;

import java.util.function.Function;

/**
 * Config for the ability bottle.
 * @author rubensworks
 */
public class ItemAbilityBottleConfig<M extends IModBase> extends ItemConfigCommon<M> {

    public ItemAbilityBottleConfig(M mod, Function<ItemConfigCommon<M>, ? extends Item> elementConstructor) {
        super(mod,
                "ability_bottle",
                elementConstructor);
    }

}
