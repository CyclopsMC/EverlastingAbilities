package org.cyclops.everlastingabilities.item;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.cyclops.cyclopscore.inventory.container.NamedContainerProviderItem;
import org.cyclops.cyclopscore.item.ItemGui;
import org.cyclops.cyclopscore.modcompat.capabilities.DefaultCapabilityProvider;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.capability.ItemStackMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

/**
 * Base class for items with abilities.
 * @author rubensworks
 */
public abstract class ItemGuiAbilityContainer extends ItemGui {

    protected ItemGuiAbilityContainer(Properties properties) {
        super(properties);
    }

    @Override
    public Class<? extends AbstractContainerMenu> getContainerClass(Level world, Player playerEntity, ItemStack itemStack) {
        return ContainerAbilityContainer.class;
    }

    @Nullable
    @Override
    public MenuProvider getContainer(Level world, Player playerEntity, int itemIndex, InteractionHand hand, ItemStack itemStack) {
        return new NamedContainerProviderItem(itemIndex, hand, itemStack.getHoverName(), ContainerAbilityContainer::new);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(itemStack, worldIn, tooltip, flagIn);

        itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).ifPresent(abilityStore -> {
            List<Ability> abilities = new ArrayList<Ability>(abilityStore.getAbilities());
            Collections.sort(abilities);

            // Display each ability in store, one line at a time
            // Or display "none" string if list is empty
            boolean empty = true;
            for (Ability ability : abilities) {
                empty = false;
                tooltip.add(new TranslatableComponent(ability.getAbilityType().getTranslationKey())
                        .append(" " + ability.getLevel())
                        .setStyle(Style.EMPTY
                                .withColor(TextColor.fromLegacyFormat(ChatFormatting.YELLOW))));
            }
            if (empty) {
                tooltip.add(new TranslatableComponent("general.everlastingabilities.empty")
                        .setStyle(Style.EMPTY
                                .withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY))
                                .withItalic(true)));
            }
        });
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
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

}
