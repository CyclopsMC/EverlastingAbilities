package org.cyclops.everlastingabilities.item;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.cyclopscore.helper.LootHelpers;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Collection;

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
                        .stacksTo(1)));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCreativeModeTabBuildContents);
    }

    @Override
    protected Collection<ItemStack> getDefaultCreativeTabEntries() {
        return Lists.newArrayList();
    }

    protected void onCreativeModeTabBuildContents(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == this.getMod().getDefaultCreativeTab() && Minecraft.getInstance().level != null) { // Level can be null during game loading
            Registry<IAbilityType> registry = AbilityHelpers.getRegistry(Minecraft.getInstance().level.registryAccess());
            registry.forEach(abilityType -> {
                for (int level = 1; level <= abilityType.getMaxLevel(); level++) {
                    Ability ability = new Ability(abilityType, level);
                    event.accept(ItemAbilityTotem.getTotem(ability));
                }
            });
        }
    }

    @Override
    public void onRegistered() {
        super.onRegistered();

        if (lootChests) {
            LootHelpers.injectLootTable(new ResourceLocation(Reference.MOD_ID, "inject/chests/ability_totem"),
                    BuiltInLootTables.SPAWN_BONUS_CHEST,
                    BuiltInLootTables.VILLAGE_TOOLSMITH,
                    BuiltInLootTables.VILLAGE_WEAPONSMITH,
                    BuiltInLootTables.VILLAGE_SHEPHERD,
                    BuiltInLootTables.NETHER_BRIDGE,
                    BuiltInLootTables.SIMPLE_DUNGEON,
                    BuiltInLootTables.ABANDONED_MINESHAFT,
                    BuiltInLootTables.JUNGLE_TEMPLE);
        }
    }
}
