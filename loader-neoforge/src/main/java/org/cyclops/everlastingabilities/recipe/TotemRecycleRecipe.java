package org.cyclops.everlastingabilities.recipe;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.RegistryEntriesCommon;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.item.ItemAbilityTotem;
import org.cyclops.everlastingabilities.item.ItemAbilityTotemConfig;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TotemRecycleRecipe extends CustomRecipe {

    private final RandomSource rand = RandomSource.create();
    private long seed = rand.nextLong();

    public TotemRecycleRecipe(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
    }

    @Override
    public boolean matches(CraftingInput invCrafting, Level world) {
        if (ItemAbilityTotemConfig.totemCraftingCount <= 0) {
            return false;
        }

        int inputCount = 0;
        for (int i = 0; i < invCrafting.size(); i++) {
            ItemStack slot = invCrafting.getItem(i);
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
    public ItemStack assemble(CraftingInput invCrafting, HolderLookup.Provider holderLookupProvider) {
        // Crafting is simulated

        // Select one of the inputs at random, and use its rarity for the rarity of the output.
        rand.setSeed(seed);
        int inputTargetIndex = rand.nextInt(ItemAbilityTotemConfig.totemCraftingCount);

        // Sort our input stacks, so we can deterministically select one specific one to determine the output rarity.
        NonNullList<ItemStack> sortedStacks = NonNullList.create();
        for (int i = 0; i < invCrafting.size(); i++) {
            ItemStack slot = invCrafting.getItem(i);
            if (!slot.isEmpty()) {
                if (slot.getItem() instanceof ItemAbilityTotem) {
                    sortedStacks.add(invCrafting.getItem(i));
                } else {
                    // non-totem item found in recipe
                    // this should never happen because matches() will return false.
                    // We should probably throw() here.
                    return ItemStack.EMPTY;
                }
            }
        }
        Collections.sort(sortedStacks, Comparator.comparingInt(itemStack -> itemStack.getComponents().hashCode()));

        if (inputTargetIndex >= sortedStacks.size()) {
            // Should not be able to happen, unless some mod is doing funky stuff.
            return ItemStack.EMPTY;
        }
        Rarity rarity = sortedStacks.get(inputTargetIndex).getRarity();

        // A chance of a bump
        List<Holder<IAbilityType>> abilityTypes = EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getAbilityTypesCrafting(holderLookupProvider);
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
            } while (!EverlastingAbilitiesInstance.MOD.getAbilityHelpers().hasRarityAbilities(abilityTypes, newRarity));
            rarity = newRarity;
        }

        // Set the rand seed so that the resulting ability will always be different
        // (but deterministic) for different input abilities
        rand.setSeed(seed + sortedStacks.stream().mapToInt(itemStack -> itemStack.getComponents().hashCode()).sum());
        return EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getRandomTotem(abilityTypes, rarity, rand).get(); // This optional should always be present
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= ItemAbilityTotemConfig.totemCraftingCount;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return new ItemStack(RegistryEntriesCommon.ITEM_ABILITY_TOTEM);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        // Item is being taken out of crafting grid.

        seed++;

        // Code below is copied from IRecipe
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.size(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inv.getItem(i);
            if (item.hasCraftingRemainingItem()) {
                nonnulllist.set(i, item.getCraftingRemainingItem());
            }
        }

        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RegistryEntries.RECIPESERIALIZER_TOTEM_RECYCLE.get();
    }
}
