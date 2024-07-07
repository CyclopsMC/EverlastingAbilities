package org.cyclops.everlastingabilities.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.cyclops.cyclopscore.inventory.ItemLocation;
import org.cyclops.cyclopscore.inventory.container.NamedContainerProviderItem;
import org.cyclops.cyclopscore.item.ItemGui;
import org.cyclops.everlastingabilities.Capabilities;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    public MenuProvider getContainer(Level world, Player playerEntity, ItemLocation itemLocation) {
        return new NamedContainerProviderItem(itemLocation, itemLocation.getItemStack(playerEntity).getHoverName(), ContainerAbilityContainer::new);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(itemStack, context, tooltip, flagIn);

        Optional.ofNullable(itemStack.getCapability(Capabilities.MutableAbilityStore.ITEM, null)).ifPresent(abilityStore -> {
            List<Ability> abilities = new ArrayList<Ability>(abilityStore.getAbilities());
            Collections.sort(abilities);

            // Display each ability in store, one line at a time
            // Or display "none" string if list is empty
            boolean empty = true;
            for (Ability ability : abilities) {
                empty = false;
                tooltip.add(Component.translatable(ability.getAbilityType().getTranslationKey())
                        .append(" " + ability.getLevel())
                        .setStyle(Style.EMPTY
                                .withColor(TextColor.fromLegacyFormat(ChatFormatting.YELLOW))));
            }
            if (empty) {
                tooltip.add(Component.translatable("general.everlastingabilities.empty")
                        .setStyle(Style.EMPTY
                                .withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY))
                                .withItalic(true)));
            }
        });
    }

    public abstract boolean canMoveFromPlayer();

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack == null || newStack == null || oldStack.getItem() != newStack.getItem();
    }

}
