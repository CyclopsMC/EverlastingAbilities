package org.cyclops.everlastingabilities;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.registries.ObjectHolder;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;
import org.cyclops.everlastingabilities.recipe.TotemRecycleRecipe;

/**
 * Referenced registry entries.
 * @author rubensworks
 */
public class RegistryEntries {

    @ObjectHolder("everlastingabilities:ability_bottle")
    public static final Item ITEM_ABILITY_BOTTLE = null;
    @ObjectHolder("everlastingabilities:ability_totem")
    public static final Item ITEM_ABILITY_TOTEM = null;

    @ObjectHolder("everlastingabilities:ability_container")
    public static final ContainerType<ContainerAbilityContainer> CONTAINER_ABILITYCONTAINER = null;

    @ObjectHolder("everlastingabilities:crafting_special_totem_recycle")
    public static final SpecialRecipeSerializer<TotemRecycleRecipe> RECIPESERIALIZER_TOTEM_RECYCLE = null;

}
