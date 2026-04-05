package org.cyclops.everlastingabilities.item;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
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
import java.util.Optional;

/**
 * Config for the ability totem.
 * @author rubensworks
 */
public class ItemAbilityTotemConfigFabric extends ItemAbilityTotemConfig<EverlastingAbilitiesFabric> {

    @ConfigurablePropertyCommon(category = "core", comment = "The loot tables in which totems should be spawned.", configLocation = ModConfigLocation.SERVER)
    public static List<String> lootTables = Lists.newArrayList(
            BuiltInLootTables.SPAWN_BONUS_CHEST.identifier().toString(),
            BuiltInLootTables.VILLAGE_TOOLSMITH.identifier().toString(),
            BuiltInLootTables.VILLAGE_WEAPONSMITH.identifier().toString(),
            BuiltInLootTables.VILLAGE_SHEPHERD.identifier().toString(),
            BuiltInLootTables.NETHER_BRIDGE.identifier().toString(),
            BuiltInLootTables.SIMPLE_DUNGEON.identifier().toString(),
            BuiltInLootTables.ABANDONED_MINESHAFT.identifier().toString(),
            BuiltInLootTables.JUNGLE_TEMPLE.identifier().toString(),
            BuiltInLootTables.ANCIENT_CITY.identifier().toString()
    );

    protected static Registry<LootTable> LOOT_TABLES_REGISTRY;

    public ItemAbilityTotemConfigFabric() {
        super(EverlastingAbilitiesFabric._instance,
                (eConfig, properties) -> new ItemAbilityTotemFabric(properties
                        .stacksTo(1)));
        DefaultItemComponentEvents.MODIFY.register(this::onSetDefaultComponents);
        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register(this::onCreativeModeTabBuildContents);
        ILootTableModifyEvent.EVENT.register(this::onLootTableModify);
        LootTableEvents.ALL_LOADED.register((resourceManager, lootRegistry) -> LOOT_TABLES_REGISTRY = lootRegistry);
    }

    private void onSetDefaultComponents(DefaultItemComponentEvents.ModifyContext modifyContext) {
        modifyContext.modify(getInstance(), (builder) -> builder.set(RegistryEntries.DATACOMPONENT_ABILITY_STORE.value(), new DefaultMutableAbilityStore()));
    }

    private void onLootTableModify(LootTable lootTable, LootContext context, ObjectArrayList<ItemStack> itemStacks) {
        Optional<ResourceKey<LootTable>> optionalKey = LOOT_TABLES_REGISTRY.getResourceKey(lootTable);
        if (optionalKey.isPresent()) {
            ResourceKey<LootTable> key = optionalKey.get();
            if (lootTables.contains(key.identifier().toString())) {
                EverlastingAbilitiesInstance.MOD.getAbilityHelpers().injectLootTotem(itemStacks::add, context);
            }
        }
    }

    private void onCreativeModeTabBuildContents(CreativeModeTab creativeModeTab, FabricCreativeModeTabOutput fabricItemGroupEntries) {
        this.onCreativeModeTabBuildContentsCommon(creativeModeTab, fabricItemGroupEntries.getContext(), fabricItemGroupEntries::accept);
    }
}
