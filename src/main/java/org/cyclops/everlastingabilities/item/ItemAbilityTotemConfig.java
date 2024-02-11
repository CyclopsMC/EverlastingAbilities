package org.cyclops.everlastingabilities.item;

import com.google.common.collect.Lists;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.helper.WorldHelpers;

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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCreativeModeTabBuildContents);
    }

    @Override
    protected Collection<ItemStack> getDefaultCreativeTabEntries() {
        return Lists.newArrayList();
    }

    protected void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
        RegistryAccess registryAccess = WorldHelpers.getRegistryAccess();
        if (event.getTab() == this.getMod().getDefaultCreativeTab() && registryAccess != null) { // Level can be null during game loading
            Registry<IAbilityType> registry = AbilityHelpers.getRegistry(registryAccess);
            registry.forEach(abilityType -> {
                for (int level = 1; level <= abilityType.getMaxLevel(); level++) {
                    Ability ability = new Ability(abilityType, level);
                    if (AbilityHelpers.PREDICATE_ABILITY_ENABLED.test(abilityType)) {
                        event.accept(ItemAbilityTotem.getTotem(ability));
                    }
                }
            });
        }
    }
}
