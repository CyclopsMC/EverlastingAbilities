package org.cyclops.everlastingabilities.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.cyclops.cyclopscore.helper.ItemStackHelpers;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.item.ItemAbilityTotem;
import org.cyclops.everlastingabilities.item.ItemAbilityTotemConfig;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class TotemRecycleRecipe extends SpecialRecipe {

    private final Random rand = new Random();
    private long seed = rand.nextLong();

    public TotemRecycleRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory invCrafting, World world) {
        if (ItemAbilityTotemConfig.totemCraftingCount <= 0) {
            return false;
        }

        int inputCount = 0;
        for (int i = 0; i < invCrafting.getSizeInventory(); i++) {
            ItemStack slot = invCrafting.getStackInSlot(i);
            if (!slot.isEmpty()) {
                if (slot.getItem() instanceof ItemAbilityTotem) {
                    inputCount++;
                }
                else {
                    // non-totem item found in recipe
                    return false;
                }
            }
        }
        return inputCount == ItemAbilityTotemConfig.totemCraftingCount;
    }
    
    @Override
    public ItemStack getCraftingResult(CraftingInventory invCrafting) {
        // Item is being taken out of crafting grid.

        // Select one of the inputs at random, and use its rarity for the rarity of the output.
        rand.setSeed(seed);
        int inputTargetIndex = rand.nextInt(ItemAbilityTotemConfig.totemCraftingCount);

        // Sort our input stacks, so we can deterministically select one specific one to determine the output rarity.
        NonNullList<ItemStack> sortedStacks = NonNullList.create();
        for (int i = 0; i < invCrafting.getSizeInventory(); i++) {
            ItemStack slot = invCrafting.getStackInSlot(i);
            if (!slot.isEmpty()) {
                if (slot.getItem() instanceof ItemAbilityTotem) {
                    sortedStacks.add(invCrafting.getStackInSlot(i));
                } else {
                    // non-totem item found in recipe
                    // this should never happen because matches() will return false.
                    // We should probably throw() here.
                    return ItemStack.EMPTY;
                }
            }
        }
        Collections.sort(sortedStacks, Comparator.comparingInt(ItemStackHelpers::getItemStackHashCode));

        if (inputTargetIndex >= sortedStacks.size()) {
            // Should not be able to happen, unless some mod is doing funky stuff.
            return ItemStack.EMPTY;
        }
        Rarity rarity = RegistryEntries.ITEM_ABILITY_TOTEM.getRarity(sortedStacks.get(inputTargetIndex));

        // 20% chance of a bump
        List<IAbilityType> abilityTypes = AbilityHelpers.getAbilityTypesCrafting();
        if (rand.nextInt(100) < ItemAbilityTotemConfig.totemCraftingRarityIncreasePercent) {
            Rarity newRarity = rarity;
            // This loop ensures that the new rarity has at least one registered ability
            do {
                if (newRarity.ordinal() < Rarity.EPIC.ordinal()) {
                    newRarity = Rarity.values()[newRarity.ordinal() + 1];
                } else {
                    // Fallback to the original rarity.
                    // By logical inference, this will have at least one rarity, i.e., the original ability.
                    newRarity = rarity;
                }
            } while (!AbilityHelpers.hasRarityAbilities(abilityTypes, newRarity));
            rarity = newRarity;
        }

        // Set the rand seed so that the resulting ability will always be different
        // (but deterministic) for different input abilities
        rand.setSeed(sortedStacks.stream().mapToInt(ItemStackHelpers::getItemStackHashCode).sum());
        return AbilityHelpers.getRandomTotem(abilityTypes, rarity, rand).get(); // This optional should always be present
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= ItemAbilityTotemConfig.totemCraftingCount;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(RegistryEntries.ITEM_ABILITY_TOTEM);
    }
    
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        seed++;

        // Code below is copied from IRecipe
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inv.getStackInSlot(i);
            if (item.hasContainerItem()) {
                nonnulllist.set(i, item.getContainerItem());
            }
        }

        return nonnulllist;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RegistryEntries.RECIPESERIALIZER_TOTEM_RECYCLE;
    }
}

