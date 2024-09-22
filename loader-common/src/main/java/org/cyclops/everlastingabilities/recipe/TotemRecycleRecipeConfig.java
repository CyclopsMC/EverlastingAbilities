package org.cyclops.everlastingabilities.recipe;

import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import org.cyclops.cyclopscore.config.extendedconfig.RecipeConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;

/**
 * Config for {@link TotemRecycleRecipe}.
 * @author rubensworks
 */
public class TotemRecycleRecipeConfig<M extends IModBase> extends RecipeConfigCommon<TotemRecycleRecipe, M> {

    public TotemRecycleRecipeConfig(M mod) {
        super(mod,
                "crafting_special_totem_recycle",
                eConfig -> new SimpleCraftingRecipeSerializer<>(TotemRecycleRecipe::new));
    }

}
