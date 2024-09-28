package org.cyclops.everlastingabilities.item;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.cyclops.cyclopscore.config.ConfigurablePropertyCommon;
import org.cyclops.cyclopscore.config.ModConfigLocation;
import org.cyclops.cyclopscore.events.ILootTableModifyEvent;
import org.cyclops.everlastingabilities.EverlastingAbilitiesFabric;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;

import java.util.Set;

/**
 * Config for the ability totem.
 * @author rubensworks
 */
public class ItemAbilityTotemConfigFabric extends ItemAbilityTotemConfig<EverlastingAbilitiesFabric> {

    @ConfigurablePropertyCommon(category = "core", comment = "If totems should be added to loot tables.", configLocation = ModConfigLocation.SERVER)
    public static boolean totemInjectLootTables = true;

    protected static Registry<LootTable> LOOT_TABLES_REGISTRY;
    protected static Set<ResourceKey<LootTable>> LOOT_TABLES = Sets.newHashSet(
            BuiltInLootTables.SPAWN_BONUS_CHEST,
            BuiltInLootTables.VILLAGE_TOOLSMITH,
            BuiltInLootTables.VILLAGE_WEAPONSMITH,
            BuiltInLootTables.VILLAGE_SHEPHERD,
            BuiltInLootTables.NETHER_BRIDGE,
            BuiltInLootTables.SIMPLE_DUNGEON,
            BuiltInLootTables.ABANDONED_MINESHAFT,
            BuiltInLootTables.JUNGLE_TEMPLE,
            BuiltInLootTables.ANCIENT_CITY
    );

    public ItemAbilityTotemConfigFabric() {
        super(EverlastingAbilitiesFabric._instance,
                (eConfig) -> new ItemAbilityTotemFabric(new Item.Properties()
                        .stacksTo(1)));
        DefaultItemComponentEvents.MODIFY.register(this::onSetDefaultComponents);
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register(this::onCreativeModeTabBuildContents);
        ILootTableModifyEvent.EVENT.register(this::onLootTableModify);
        LootTableEvents.ALL_LOADED.register((resourceManager, lootRegistry) -> LOOT_TABLES_REGISTRY = lootRegistry);
    }

    private void onSetDefaultComponents(DefaultItemComponentEvents.ModifyContext modifyContext) {
        modifyContext.modify(getInstance(), (builder) -> builder.set(RegistryEntries.DATACOMPONENT_ABILITY_STORE.value(), new DefaultMutableAbilityStore()));
    }

    private void onLootTableModify(LootTable lootTable, LootContext context, ObjectArrayList<ItemStack> itemStacks) {
        if (totemInjectLootTables) {
            ResourceKey<LootTable> key = LOOT_TABLES_REGISTRY.getResourceKey(lootTable).get();
            if (LOOT_TABLES.contains(key)) {
                EverlastingAbilitiesInstance.MOD.getAbilityHelpers().injectLootTotem(itemStacks::add, context);
            }
        }
    }

    private void onCreativeModeTabBuildContents(CreativeModeTab creativeModeTab, FabricItemGroupEntries fabricItemGroupEntries) {
        this.onCreativeModeTabBuildContentsCommon(creativeModeTab, fabricItemGroupEntries.getContext(), fabricItemGroupEntries::accept);
    }
}
