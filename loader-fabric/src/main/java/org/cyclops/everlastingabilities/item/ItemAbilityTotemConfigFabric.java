package org.cyclops.everlastingabilities.item;

import com.google.common.collect.Lists;
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

import java.util.List;

/**
 * Config for the ability totem.
 * @author rubensworks
 */
public class ItemAbilityTotemConfigFabric extends ItemAbilityTotemConfig<EverlastingAbilitiesFabric> {

    @Deprecated // TODO: rm in next major. Not needed anymore since we have the list.
    @ConfigurablePropertyCommon(category = "core", comment = "If totems should be added to loot tables.", configLocation = ModConfigLocation.SERVER)
    public static boolean totemInjectLootTables = true;

    @ConfigurablePropertyCommon(category = "core", comment = "The loot tables in which totems should be spawned.", configLocation = ModConfigLocation.SERVER)
    public static List<String> lootTables = Lists.newArrayList(
            BuiltInLootTables.SPAWN_BONUS_CHEST.location().toString(),
            BuiltInLootTables.VILLAGE_TOOLSMITH.location().toString(),
            BuiltInLootTables.VILLAGE_WEAPONSMITH.location().toString(),
            BuiltInLootTables.VILLAGE_SHEPHERD.location().toString(),
            BuiltInLootTables.NETHER_BRIDGE.location().toString(),
            BuiltInLootTables.SIMPLE_DUNGEON.location().toString(),
            BuiltInLootTables.ABANDONED_MINESHAFT.location().toString(),
            BuiltInLootTables.JUNGLE_TEMPLE.location().toString(),
            BuiltInLootTables.ANCIENT_CITY.location().toString()
    );

    protected static Registry<LootTable> LOOT_TABLES_REGISTRY;

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
            if (lootTables.contains(key.location().toString())) {
                EverlastingAbilitiesInstance.MOD.getAbilityHelpers().injectLootTotem(itemStacks::add, context);
            }
        }
    }

    private void onCreativeModeTabBuildContents(CreativeModeTab creativeModeTab, FabricItemGroupEntries fabricItemGroupEntries) {
        this.onCreativeModeTabBuildContentsCommon(creativeModeTab, fabricItemGroupEntries.getContext(), fabricItemGroupEntries::accept);
    }
}
