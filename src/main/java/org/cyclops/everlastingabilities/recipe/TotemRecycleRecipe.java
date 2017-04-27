package org.cyclops.everlastingabilities.recipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import org.cyclops.everlastingabilities.item.ItemAbilityTotem;
import org.cyclops.everlastingabilities.item.ItemAbilityTotemConfig;
import java.util.Random;

public class TotemRecycleRecipe implements IRecipe {

	static final Random rand = new Random();

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
		return ItemAbilityTotem.getRandomTotem(rand);
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(ItemAbilityTotem.getInstance());
	}
	
	@Override
	public int getRecipeSize() {
		return 3;
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}