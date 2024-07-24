package org.cyclops.everlastingabilities;

import com.mojang.serialization.Codec;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.ObjectHolder;
import org.cyclops.everlastingabilities.api.IAbilityType;
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
    public static final SimpleCraftingRecipeSerializer<TotemRecycleRecipe> RECIPESERIALIZER_TOTEM_RECYCLE = null;

    @ObjectHolder(registryName = "everlastingabilities:ability_serializers", value = "everlastingabilities:effect")
    public static final Codec<? extends IAbilityType> ABILITYSERIALIZER_EFFECT = null;
    @ObjectHolder(registryName = "everlastingabilities:ability_serializers", value = "everlastingabilities:attribute_modifier")
    public static final Codec<? extends IAbilityType> ABILITYSERIALIZER_ATTRIBUTE_MODIFIER = null;
    @ObjectHolder(registryName = "everlastingabilities:ability_serializers", value = "everlastingabilities:special_bonemealer")
    public static final Codec<? extends IAbilityType> ABILITYSERIALIZER_SPECIAL_BONEMEALER = null;
    @ObjectHolder(registryName = "everlastingabilities:ability_serializers", value = "everlastingabilities:special_fertility")
    public static final Codec<? extends IAbilityType> ABILITYSERIALIZER_SPECIAL_FERTILITY = null;
    @ObjectHolder(registryName = "everlastingabilities:ability_serializers", value = "everlastingabilities:special_flight")
    public static final Codec<? extends IAbilityType> ABILITYSERIALIZER_SPECIAL_FLIGHT = null;
    @ObjectHolder(registryName = "everlastingabilities:ability_serializers", value = "everlastingabilities:special_magnetize")
    public static final Codec<? extends IAbilityType> ABILITYSERIALIZER_SPECIAL_MAGNETIZE = null;
    @ObjectHolder(registryName = "everlastingabilities:ability_serializers", value = "everlastingabilities:special_power_stare")
    public static final Codec<? extends IAbilityType> ABILITYSERIALIZER_SPECIAL_POWER_STARE = null;
    @ObjectHolder(registryName = "everlastingabilities:ability_serializers", value = "everlastingabilities:special_step_assist")
    public static final Codec<? extends IAbilityType> ABILITYSERIALIZER_SPECIAL_STEP_ASSIST = null;

}
