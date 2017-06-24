package org.cyclops.everlastingabilities.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.capability.AbilityStoreDisplayType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.cyclops.everlastingabilities.item.ItemAbilityTotem;
import org.cyclops.everlastingabilities.item.ItemAbilityTotemConfig;

import java.util.Random;

public class TotemRecycleRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    private final Random rand = new Random();
    
    @Override
    public boolean matches(InventoryCrafting invCrafting, World world) {
    
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
    public ItemStack getCraftingResult(InventoryCrafting invCrafting) {
        if (MinecraftHelpers.isClientSide()) {
        
            // Getting item for display in crafting output.
            // User hasn't picked it up yet, so display an obfuscated name.
            ItemStack stack = new ItemStack(ItemAbilityTotem.getInstance());
            IMutableAbilityStore abilityStore = stack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
            abilityStore.setDisplayType(AbilityStoreDisplayType.OBFUSCATED);
        
            return stack;
        }
        else {
            // Item is being taken out of crafting grid.
            
            // Select one of the inputs at random, and use its rarity for the rarity of the output.
            int inputIndex = 0;
            int inputTargetIndex = rand.nextInt(ItemAbilityTotemConfig.totemCraftingCount);
            EnumRarity rarity = EnumRarity.COMMON;
            
            for (int i = 0; i < invCrafting.getSizeInventory(); i++) {
                ItemStack slot = invCrafting.getStackInSlot(i);
                if (!slot.isEmpty()) {
                    if (slot.getItem() instanceof ItemAbilityTotem) {
                        if (inputIndex >= inputTargetIndex) { 
                            rarity = ItemAbilityTotem.getInstance().getRarity(slot);
                            break;
                        }
                        inputIndex++;
                    }
                    else {
                        // non-totem item found in recipe
                        // this should never happen because matches() will return false.
                        // We should probably throw() here.
                        return ItemStack.EMPTY;
                    }
                }
            }
            
            // 20% chance of a bump
            if (rarity.ordinal() < EnumRarity.EPIC.ordinal() && rand.nextInt(100) < ItemAbilityTotemConfig.totemCraftingRarityIncreasePercent) {
                rarity = EnumRarity.values()[rarity.ordinal()+1];
            }
            
            return AbilityHelpers.getRandomTotem(rarity, rand);
        }
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= ItemAbilityTotemConfig.totemCraftingCount;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(ItemAbilityTotem.getInstance());
    }
    
    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }
}

