package org.cyclops.everlastingabilities.item;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.ConfigurableTypeCategory;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.cyclopscore.helper.LootHelpers;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.Reference;

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
            LootHelpers.injectLootTable(new ResourceLocation(Reference.MOD_ID, "inject/chests/ability_totem"),
                    LootTableList.CHESTS_SPAWN_BONUS_CHEST,
                    LootTableList.CHESTS_VILLAGE_BLACKSMITH,
                    LootTableList.CHESTS_NETHER_BRIDGE,
                    LootTableList.CHESTS_SIMPLE_DUNGEON,
                    LootTableList.CHESTS_ABANDONED_MINESHAFT,
                    LootTableList.CHESTS_JUNGLE_TEMPLE);
        }
    }
}
