package org.cyclops.everlastingabilities.item;

import com.google.common.collect.Lists;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.everlastingabilities.Capabilities;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.RegistryEntriesCommon;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.ItemDataMutableAbilityStore;

import java.util.Collection;

/**
 * Config for the ability totem.
 * @author rubensworks
 */
public class ItemAbilityTotemConfig extends ItemConfig {

    @ConfigurableProperty(category = "core", comment = "This many totems combined in a crafting grid produces a new random totem (0 to disable)")
    public static int totemCraftingCount = 3;

    @ConfigurableProperty(category = "core", comment = "When combining totems, percentage chance of getting one higher rarity than normal.", configLocation = ModConfig.Type.SERVER)
    public static int totemCraftingRarityIncreasePercent = 15;

    public ItemAbilityTotemConfig() {
        super(EverlastingAbilities._instance,
                "ability_totem",
                (eConfig) -> new ItemAbilityTotem(new Item.Properties()
                        .stacksTo(1)));
        EverlastingAbilities._instance.getModEventBus().addListener(this::onCreativeModeTabBuildContents);
        EverlastingAbilities._instance.getModEventBus().addListener(this::modifyComponents);
        EverlastingAbilities._instance.getModEventBus().addListener(this::registerCapability);
    }

    @Override
    protected Collection<ItemStack> getDefaultCreativeTabEntries() {
        return Lists.newArrayList();
    }

    protected void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == this.getMod().getDefaultCreativeTab()) { // Level can be null during game loading
            EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getRegistryLookup(event.getParameters().holders()).listElements().forEach(abilityType -> {
                for (int level = 1; level <= abilityType.value().getMaxLevel(); level++) {
                    Ability ability = new Ability(abilityType, level);
                    if (EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getPredicateAbilityEnabled().test(abilityType)) {
                        event.accept(EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getTotem(ability));
                    }
                }
            });
        }
    }

    protected void modifyComponents(ModifyDefaultComponentsEvent event) {
        event.modify(getInstance(), (builder) -> builder.set(RegistryEntriesCommon.DATACOMPONENT_ABILITY_STORE.value(), new DefaultMutableAbilityStore()));
    }

    protected void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.MutableAbilityStore.ITEM, (stack, context) -> new ItemDataMutableAbilityStore(stack, () -> stack.set(DataComponents.RARITY, ItemAbilityTotem.getRarity(stack))), getInstance());
    }
}
