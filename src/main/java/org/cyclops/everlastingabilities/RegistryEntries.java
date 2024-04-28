package org.cyclops.everlastingabilities;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.cyclops.everlastingabilities.api.AbilityTypeSerializers;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;
import org.cyclops.everlastingabilities.recipe.TotemRecycleRecipe;

/**
 * Referenced registry entries.
 * @author rubensworks
 */
public class RegistryEntries {

    public static final DeferredHolder<Item, Item> ITEM_ABILITY_BOTTLE = DeferredHolder.create(Registries.ITEM, new ResourceLocation("everlastingabilities:ability_bottle"));
    public static final DeferredHolder<Item, Item> ITEM_ABILITY_TOTEM = DeferredHolder.create(Registries.ITEM, new ResourceLocation("everlastingabilities:ability_totem"));

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerAbilityContainer>> CONTAINER_ABILITYCONTAINER = DeferredHolder.create(Registries.MENU, new ResourceLocation("everlastingabilities:ability_container"));

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<TotemRecycleRecipe>> RECIPESERIALIZER_TOTEM_RECYCLE = DeferredHolder.create(Registries.RECIPE_SERIALIZER, new ResourceLocation("everlastingabilities:crafting_special_totem_recycle"));

    public static final DeferredHolder<Codec<? extends IAbilityType>, Codec<? extends IAbilityType>> ABILITYSERIALIZER_EFFECT = DeferredHolder.create(AbilityTypeSerializers.REGISTRY_KEY, new ResourceLocation("everlastingabilities:effect"));
    public static final DeferredHolder<Codec<? extends IAbilityType>, Codec<? extends IAbilityType>> ABILITYSERIALIZER_SPECIAL_BONEMEALER = DeferredHolder.create(AbilityTypeSerializers.REGISTRY_KEY, new ResourceLocation("everlastingabilities:special_bonemealer"));
    public static final DeferredHolder<Codec<? extends IAbilityType>, Codec<? extends IAbilityType>> ABILITYSERIALIZER_SPECIAL_FERTILITY = DeferredHolder.create(AbilityTypeSerializers.REGISTRY_KEY, new ResourceLocation("everlastingabilities:special_fertility"));
    public static final DeferredHolder<Codec<? extends IAbilityType>, Codec<? extends IAbilityType>> ABILITYSERIALIZER_SPECIAL_FLIGHT = DeferredHolder.create(AbilityTypeSerializers.REGISTRY_KEY, new ResourceLocation("everlastingabilities:special_flight"));
    public static final DeferredHolder<Codec<? extends IAbilityType>, Codec<? extends IAbilityType>> ABILITYSERIALIZER_SPECIAL_MAGNETIZE = DeferredHolder.create(AbilityTypeSerializers.REGISTRY_KEY, new ResourceLocation("everlastingabilities:special_magnetize"));
    public static final DeferredHolder<Codec<? extends IAbilityType>, Codec<? extends IAbilityType>> ABILITYSERIALIZER_SPECIAL_POWER_STARE = DeferredHolder.create(AbilityTypeSerializers.REGISTRY_KEY, new ResourceLocation("everlastingabilities:special_power_stare"));
    public static final DeferredHolder<Codec<? extends IAbilityType>, Codec<? extends IAbilityType>> ABILITYSERIALIZER_SPECIAL_STEP_ASSIST = DeferredHolder.create(AbilityTypeSerializers.REGISTRY_KEY, new ResourceLocation("everlastingabilities:special_step_assist"));

}
