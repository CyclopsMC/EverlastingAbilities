package org.cyclops.everlastingabilities.inventory.container;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cyclops.cyclopscore.helper.InventoryHelpers;
import org.cyclops.cyclopscore.inventory.IGuiContainerProviderConfigurable;
import org.cyclops.cyclopscore.inventory.container.ItemInventoryContainer;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.cyclops.everlastingabilities.client.gui.GuiAbilityContainer;
import org.cyclops.everlastingabilities.item.ItemAbilityTotem;
import org.cyclops.everlastingabilities.item.ItemGuiAbilityContainer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Container for the labeller.
 * @author rubensworks
 */
public class ContainerAbilityContainer extends ItemInventoryContainer<ItemGuiAbilityContainer> {

    @SideOnly(Side.CLIENT)
    private GuiAbilityContainer gui;

    /**
     * Make a new instance.
     *
     * @param player The player.
     * @param itemIndex The index of the item in use inside the player inventory.
     * @param hand The hand the item is in.
     */
    public ContainerAbilityContainer(EntityPlayer player, int itemIndex, EnumHand hand) {
        super(player.inventory, (ItemGuiAbilityContainer) InventoryHelpers.getItemFromIndex(player, itemIndex, hand).getItem(), itemIndex, hand);
        addInventory(player.inventory, 0, 8, 195, 1, 9);

        // If level is not consistent with total experience count, fix it.
        // This can be caused by vanilla's xp command that adds levels but doesn't change the total xp count.
        // If other mods mess things up, this will resolve it as well.
        int level = AbilityHelpers.getLevelForExperience(player.experienceTotal);
        if (player.experienceLevel != level) {
            player.experienceTotal = AbilityHelpers.getExperienceForLevel(player.experienceLevel);
        }
    }

    @Override
    public IGuiContainerProviderConfigurable getGuiProvider() {
        return ItemAbilityTotem.getInstance();
    }

    @SideOnly(Side.CLIENT)
    public void setGui(GuiAbilityContainer gui) {
        this.gui = gui;
    }

    @SideOnly(Side.CLIENT)
    public GuiAbilityContainer getGui() {
        return this.gui;
    }

    @Override
    protected int getSizeInventory() {
        return 0;
    }


    public IMutableAbilityStore getPlayerAbilityStore() {
        return player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
    }

    public IMutableAbilityStore getItemAbilityStore() {
        ItemStack itemStack = getItemStack(player);
        if (itemStack == null) {
            return null;
        }
        return itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
    }

    public @Nullable List<Ability> getPlayerAbilities() {
        IMutableAbilityStore abilityStore = getPlayerAbilityStore();
        if (abilityStore == null) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(abilityStore.getAbilities());
    }

    public List<Ability> getItemAbilities() {
        IMutableAbilityStore abilityStore = getItemAbilityStore();
        if (abilityStore == null) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(abilityStore.getAbilities());
    }

    public void moveFromPlayer(Ability playerAbility) {
        Ability insertedAbility = AbilityHelpers.insert(playerAbility, getItemAbilityStore());
        if (insertedAbility != null) {
            AbilityHelpers.removePlayerAbility(player, insertedAbility, true, true);
        }
    }

    public void moveToPlayer(Ability itemAbility) {
        Ability insertedAbility = AbilityHelpers.addPlayerAbility(player, itemAbility, true, true);
        if (insertedAbility != null) {
            AbilityHelpers.extract(insertedAbility, getItemAbilityStore());

            if(getItemAbilities().isEmpty() && !getItem().canMoveFromPlayer()) {
                player.inventory.setInventorySlotContents(itemIndex, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return getItemStack(player) != null && (getItem().canMoveFromPlayer() || !getItemAbilities().isEmpty());
    }
}
