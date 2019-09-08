package org.cyclops.everlastingabilities.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.item.ItemGui;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.ItemStackMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for items with abilities.
 * @author rubensworks
 */
public abstract class ItemGuiAbilityContainer extends ItemGui {

    protected ItemGuiAbilityContainer(Properties properties) {
        super(properties);
    }

    @Override
    public Class<? extends Container> getContainerClass(World world, PlayerEntity playerEntity, ItemStack itemStack) {
        return ContainerAbilityContainer.class;
    }

    @Nullable
    @Override
    public INamedContainerProvider getContainer(World world, PlayerEntity playerEntity, int itemIndex, Hand hand, ItemStack itemStack) {
        return new NamedContainerProvider(itemIndex, hand, itemStack.getDisplayName());
    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(itemStack, worldIn, tooltip, flagIn);

        itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).ifPresent(abilityStore -> {
            List<Ability> abilities = new ArrayList<Ability>(abilityStore.getAbilities());
            Collections.sort(abilities);

            // Display each ability in store, one line at a time
            // Or display "none" string if list is empty
            boolean empty = true;
            for (Ability ability : abilities) {
                empty = false;
                String name = L10NHelpers.localize(ability.getAbilityType().getTranslationKey());
                tooltip.add(new StringTextComponent(TextFormatting.YELLOW + name + ": " + TextFormatting.RESET + ability.getLevel()));
            }
            if (empty) {
                tooltip.add(new StringTextComponent(TextFormatting.GRAY.toString() + TextFormatting.ITALIC + L10NHelpers.localize("general.everlastingabilities.empty")));
            }
        });
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        // TODO: restore when Forge fixed that bug (backwards compat is already taken care of, because data is stored twice (in stacktag and capdata))
        //return new SerializableCapabilityProvider<IMutableAbilityStore>(MutableAbilityStoreConfig.CAPABILITY,
        //        new DefaultMutableAbilityStore());
        return new DefaultCapabilityProvider<>(() -> MutableAbilityStoreConfig.CAPABILITY,
                new ItemStackMutableAbilityStore(stack));
    }

    public abstract boolean canMoveFromPlayer();

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack == null || newStack == null || oldStack.getItem() != newStack.getItem();
    }

    public static class NamedContainerProvider implements INamedContainerProvider {

        private final int itemIndex;
        private final Hand hand;
        private final ITextComponent title;

        public NamedContainerProvider(int itemIndex, Hand hand, ITextComponent title) {
            this.itemIndex = itemIndex;
            this.hand = hand;
            this.title = title;
        }

        @Override
        public ITextComponent getDisplayName() {
            return this.title;
        }

        @Nullable
        @Override
        public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
            return new ContainerAbilityContainer(id, playerInventory, itemIndex, hand);
        }
    }

}
