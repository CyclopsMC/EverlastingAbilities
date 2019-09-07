package org.cyclops.everlastingabilities.recipe;

import net.minecraft.item.crafting.SpecialRecipeSerializer;
import org.cyclops.cyclopscore.config.extendedconfig.RecipeConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;

/**
 * Config for {@link TotemRecycleRecipe}.
 * @author rubensworks
 */
public class TotemRecycleRecipeConfig extends RecipeConfig<TotemRecycleRecipe> {

    public TotemRecycleRecipeConfig() {
        super(EverlastingAbilities._instance,
                "crafting_special_totem_recycle",
                eConfig -> new SpecialRecipeSerializer<>(TotemRecycleRecipe::new));
    }

}
