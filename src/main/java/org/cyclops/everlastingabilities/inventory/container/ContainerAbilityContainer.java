package org.cyclops.everlastingabilities.inventory.container;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import org.cyclops.cyclopscore.helper.InventoryHelpers;
import org.cyclops.cyclopscore.inventory.container.ItemInventoryContainer;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import org.cyclops.everlastingabilities.client.gui.ContainerScreenAbilityContainer;
import org.cyclops.everlastingabilities.item.ItemGuiAbilityContainer;

import java.util.Collections;
import java.util.List;

/**
 * Container for the labeller.
 * @author rubensworks
 */
public class ContainerAbilityContainer extends ItemInventoryContainer<ItemGuiAbilityContainer> {

    @OnlyIn(Dist.CLIENT)
    private ContainerScreenAbilityContainer gui;

    public ContainerAbilityContainer(int id, PlayerInventory inventory, PacketBuffer packetBuffer) {
        this(id, inventory, readItemIndex(packetBuffer), readHand(packetBuffer));
    }

    public ContainerAbilityContainer(int id, PlayerInventory inventory, int itemIndex, Hand hand) {
        super(RegistryEntries.CONTAINER_ABILITYCONTAINER, id, inventory, itemIndex, hand);
        addInventory(inventory, 0, 8, 195, 1, 9);

        // If level is not consistent with total experience count, fix it.
        // This can be caused by vanilla's xp command that adds levels but doesn't change the total xp count.
        // If other mods mess things up, this will resolve it as well.
        int level = AbilityHelpers.getLevelForExperience(player.experienceTotal);
        if (player.experienceLevel != level) {
            player.experienceTotal = AbilityHelpers.getExperienceForLevel(player.experienceLevel);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void setGui(ContainerScreenAbilityContainer gui) {
        this.gui = gui;
    }

    @OnlyIn(Dist.CLIENT)
    public ContainerScreenAbilityContainer getGui() {
        return this.gui;
    }

    @Override
    protected int getSizeInventory() {
        return 0;
    }

    public LazyOptional<IMutableAbilityStore> getPlayerAbilityStore() {
        return player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
    }

    public LazyOptional<IMutableAbilityStore> getItemAbilityStore() {
        ItemStack itemStack = getItemStack(player);
        if (itemStack == null) {
            return null;
        }
        return itemStack.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
    }

    public List<Ability> getPlayerAbilities() {
        return getPlayerAbilityStore()
                .map(abilityStore -> (List<Ability>) Lists.newArrayList(abilityStore.getAbilities()))
                .orElseGet(Collections::emptyList);
    }

    public List<Ability> getItemAbilities() {
        return getItemAbilityStore()
                .map(abilityStore -> (List<Ability>) Lists.newArrayList(abilityStore.getAbilities()))
                .orElseGet(Collections::emptyList);
    }

    public void moveFromPlayer(Ability playerAbility) {
        getItemAbilityStore().ifPresent(abilityStore -> {
            Ability insertedAbility = AbilityHelpers.insert(playerAbility, abilityStore);
            if (insertedAbility != null) {
                AbilityHelpers.removePlayerAbility(player, insertedAbility, true, true);
            }
        });
    }

    public void moveToPlayer(Ability itemAbility) {
        getItemAbilityStore().ifPresent(abilityStore -> {
            Ability insertedAbility = AbilityHelpers.addPlayerAbility(player, itemAbility, true, true);
            if (insertedAbility != null) {
                AbilityHelpers.extract(insertedAbility, abilityStore);

                if(getItemAbilities().isEmpty() && !getItem().canMoveFromPlayer()) {
                    player.inventory.setInventorySlotContents(itemIndex, ItemStack.EMPTY);
                }
            }
        });
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return getItemStack(player) != null && (getItem().canMoveFromPlayer() || !getItemAbilities().isEmpty());
    }

    @Override
    public String getGuiModId() {
        return Reference.MOD_ID; // TODO: needed?
    }

    @Override
    public int getGuiId() {
        return 0; // TODO: needed?
    }
}
