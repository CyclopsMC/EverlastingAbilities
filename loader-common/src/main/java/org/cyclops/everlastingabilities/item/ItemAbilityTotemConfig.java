package org.cyclops.everlastingabilities.item;

import com.google.common.collect.Lists;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.cyclops.cyclopscore.config.ConfigurablePropertyCommon;
import org.cyclops.cyclopscore.config.ModConfigLocation;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.api.Ability;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Config for the ability totem.
 * @author rubensworks
 */
public class ItemAbilityTotemConfig<M extends IModBase> extends ItemConfigCommon<M> {

    @ConfigurablePropertyCommon(category = "core", comment = "This many totems combined in a crafting grid produces a new random totem (0 to disable)")
    public static int totemCraftingCount = 3;

    @ConfigurablePropertyCommon(category = "core", comment = "When combining totems, percentage chance of getting one higher rarity than normal.", configLocation = ModConfigLocation.SERVER)
    public static int totemCraftingRarityIncreasePercent = 15;

    public ItemAbilityTotemConfig(M mod, Function<ItemConfigCommon<M>, ? extends Item> elementConstructor) {
        super(mod,
                "ability_totem",
                elementConstructor);
    }

    @Override
    protected Collection<ItemStack> getDefaultCreativeTabEntries() {
        return Lists.newArrayList();
    }

    protected void onCreativeModeTabBuildContentsCommon(CreativeModeTab tab, CreativeModeTab.ItemDisplayParameters parameters, Consumer<ItemStack> register) {
        if (tab == this.getMod().getDefaultCreativeTab()) { // Level can be null during game loading
            EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getRegistryLookup(parameters.holders()).listElements().forEach(abilityType -> {
                for (int level = 1; level <= abilityType.value().getMaxLevel(); level++) {
                    Ability ability = new Ability(abilityType, level);
                    if (EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getPredicateAbilityEnabled().test(abilityType)) {
                        register.accept(EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getTotem(ability));
                    }
                }
            });
        }
    }
}
