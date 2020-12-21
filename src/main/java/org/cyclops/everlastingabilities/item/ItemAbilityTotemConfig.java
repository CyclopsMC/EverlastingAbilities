package org.cyclops.everlastingabilities.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.loot.LootTables;
import net.minecraftforge.fml.config.ModConfig;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.cyclopscore.helper.LootHelpers;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.Reference;

/**
 * Config for the ability totem.
 * @author rubensworks
 */
public class ItemAbilityTotemConfig extends ItemConfig {

    @ConfigurableProperty(category = "core", comment = "If totems should spawn in loot chests.", configLocation = ModConfig.Type.SERVER)
    public static boolean lootChests = true;

    @ConfigurableProperty(category = "core", comment = "This many totems combined in a crafting grid produces a new random totem (0 to disable)")
    public static int totemCraftingCount = 3;

    @ConfigurableProperty(category = "core", comment = "When combining totems, percentage chance of getting one higher rarity than normal.", configLocation = ModConfig.Type.SERVER)
    public static int totemCraftingRarityIncreasePercent = 15;

    public ItemAbilityTotemConfig() {
        super(EverlastingAbilities._instance,
                "ability_totem",
                (eConfig) -> new ItemAbilityTotem(new Item.Properties()
                        .maxStackSize(1)
                        .group(EverlastingAbilities._instance.getDefaultItemGroup())));
    }

    @Override
    public void onRegistered() {
        super.onRegistered();

        if (lootChests) {
            LootHelpers.injectLootTable(new ResourceLocation(Reference.MOD_ID, "inject/chests/ability_totem"),
                    LootTables.CHESTS_SPAWN_BONUS_CHEST,
                    LootTables.CHESTS_VILLAGE_VILLAGE_TOOLSMITH,
                    LootTables.CHESTS_VILLAGE_VILLAGE_WEAPONSMITH,
                    LootTables.CHESTS_VILLAGE_VILLAGE_SHEPHERD,
                    LootTables.CHESTS_NETHER_BRIDGE,
                    LootTables.CHESTS_SIMPLE_DUNGEON,
                    LootTables.CHESTS_ABANDONED_MINESHAFT,
                    LootTables.CHESTS_JUNGLE_TEMPLE);
        }
    }
}
