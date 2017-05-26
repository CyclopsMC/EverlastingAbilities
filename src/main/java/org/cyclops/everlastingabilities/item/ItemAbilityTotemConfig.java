package org.cyclops.everlastingabilities.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.ConfigurableTypeCategory;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.cyclopscore.helper.LootHelpers;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

import java.util.Random;

/**
 * Config for the ability totem.
 * @author rubensworks
 */
public class ItemAbilityTotemConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemAbilityTotemConfig _instance;

    /**
     * If totems should spawn in loot chests.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "If totems should spawn in loot chests.")
    public static boolean lootChests = true;

    /**
     * Can totems be combined in the crafting grid
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "This many totems combined in a crafting grid produces a new random totem (0 to disable)")
    public static int totemCraftingCount = 3;

    /**
     * Percent chance that combined totem will have a rarity bump
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.CORE, comment = "When combining totems, percentage chance of getting one higher rarity than normal.")
    public static int totemCraftingRarityIncreasePercent = 15;

    /**
     * Make a new instance.
     */
    public ItemAbilityTotemConfig() {
        super(
                EverlastingAbilities._instance,
                true,
                "ability_totem",
                null,
                ItemAbilityTotem.class
        );
    }

    @Override
    public void onRegistered() {
        super.onRegistered();

        if (lootChests) {
            LootHelpers.addVanillaLootChestLootEntry(
                    new LootEntryItem(getItemInstance(), 1, 5, new LootFunction[]{
                            new LootFunction(new LootCondition[0]) {
                                @Override
                                public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
                                    EnumRarity rarity = AbilityHelpers.getRandomRarity(rand);
                                    IAbilityType abilityType = AbilityHelpers.getRandomAbility(rand, rarity);

                                    IMutableAbilityStore mutableAbilityStore = stack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
                                    mutableAbilityStore.addAbility(new Ability(abilityType, 1), true);
                                    return stack;
                                }
                            }
                    }, new LootCondition[0], getMod().getModId() + ":" + getSubUniqueName()));
        }
    }
}
