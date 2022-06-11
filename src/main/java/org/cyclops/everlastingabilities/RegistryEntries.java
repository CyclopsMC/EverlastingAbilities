package org.cyclops.everlastingabilities;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.ObjectHolder;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;
import org.cyclops.everlastingabilities.recipe.TotemRecycleRecipe;

/**
 * Referenced registry entries.
 * @author rubensworks
 */
public class RegistryEntries {

    @ObjectHolder(registryName = "item", value = "everlastingabilities:ability_bottle")
    public static final Item ITEM_ABILITY_BOTTLE = null;
    @ObjectHolder(registryName = "item", value = "everlastingabilities:ability_totem")
    public static final Item ITEM_ABILITY_TOTEM = null;

    @ObjectHolder(registryName = "menu", value = "everlastingabilities:ability_container")
    public static final MenuType<ContainerAbilityContainer> CONTAINER_ABILITYCONTAINER = null;

    @ObjectHolder(registryName = "recipe_serializer", value = "everlastingabilities:crafting_special_totem_recycle")
    public static final SimpleRecipeSerializer<TotemRecycleRecipe> RECIPESERIALIZER_TOTEM_RECYCLE = null;

}
