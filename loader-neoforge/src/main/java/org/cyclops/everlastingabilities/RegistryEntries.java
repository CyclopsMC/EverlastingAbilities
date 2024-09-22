package org.cyclops.everlastingabilities;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.cyclops.everlastingabilities.recipe.TotemRecycleRecipe;

/**
 * Referenced registry entries.
 * @author rubensworks
 */
@Deprecated
public class RegistryEntries {

    // TODO: move all entries below

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<TotemRecycleRecipe>> RECIPESERIALIZER_TOTEM_RECYCLE = DeferredHolder.create(Registries.RECIPE_SERIALIZER, ResourceLocation.parse("everlastingabilities:crafting_special_totem_recycle"));

}
