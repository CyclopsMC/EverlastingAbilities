package org.cyclops.everlastingabilities.item;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.GatherComponentsEvent;
import org.cyclops.everlastingabilities.EverlastingAbilitiesForge;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.ItemDataMutableAbilityStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Config for the ability bottle.
 * @author rubensworks
 */
public class ItemAbilityBottleConfigForge extends ItemAbilityBottleConfig<EverlastingAbilitiesForge> {

    public ItemAbilityBottleConfigForge() {
        super(EverlastingAbilitiesForge._instance,
                (eConfig, properties) -> new ItemAbilityBottleForge(properties
                        .stacksTo(1)));
        GatherComponentsEvent.Item.BUS.addListener(this::modifyComponents);
        AttachCapabilitiesEvent.ItemStacks.BUS.addListener(this::registerCapability);
    }

    protected void modifyComponents(GatherComponentsEvent.Item event) {
        if (event.getOwner() == getInstance()) {
            event.register(RegistryEntries.DATACOMPONENT_ABILITY_STORE.value(), new DefaultMutableAbilityStore());
        }
    }

    protected void registerCapability(AttachCapabilitiesEvent.ItemStacks event) {
        ItemStack stack = event.getObject();
        if (stack.getItem() instanceof ItemGuiAbilityContainer) {
            event.addCapability(Identifier.fromNamespaceAndPath(Reference.MOD_ID, getNamedId()), new ICapabilityProvider() {
                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    return LazyOptional.of(() -> new ItemDataMutableAbilityStore(stack, () -> stack.set(DataComponents.RARITY, ItemAbilityTotem.getRarity(stack)))).cast();
                }
            });
        }
    }

}
